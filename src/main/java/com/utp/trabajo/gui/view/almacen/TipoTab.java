/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.utp.trabajo.gui.view.almacen;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.entities.TipoProducto;
import com.utp.trabajo.services.TipoProductoService;
import com.utp.trabajo.services.security.SecurityService;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.springframework.beans.factory.annotation.Autowired;

public class TipoTab extends org.jdesktop.swingx.JXPanel {

    DefaultTableModel defaultTableModelTipoProducto = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Long.class;
                case 1:
                    return String.class;
                default:
                    return String.class;
            }
        }
    };
    String[] columnNames = {"ID", "Tipo de Producto"};

    ListSelectionModel selectionModel;

    private boolean canEdit = true;
    private boolean canDelete = true;
    private boolean canCreate = true;
    private boolean retrievingData = false;
    private boolean canRead = true;

    private long lastId = 0;

    private long limit = 100;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private TipoProductoService tipoproductoService;

    public TipoTab() {
        initComponents();
        defaultTableModelTipoProducto.setColumnIdentifiers(columnNames);
        tablaTipoTab.setModel(defaultTableModelTipoProducto);
        scrollPane.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            if (retrievingData) {
                return;
            }
            int maxValue = scrollPane.getVerticalScrollBar().getMaximum() - scrollPane.getVerticalScrollBar().getVisibleAmount();
            int currentValue = scrollPane.getVerticalScrollBar().getValue();
            float fraction = (float) currentValue / (float) maxValue;
            if (fraction > 0.999f) {
                retrieveData(false);
                System.out.println("Scroll bar is near the bottom");
            }
        });
        selectionModel = tablaTipoTab.getSelectionModel();
        selectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            if (!canDelete || !canEdit) {
                return;
            }
            if (!selectionModel.isSelectionEmpty()) {
                eliminarTipoButton.setEnabled(true);
            } else {
                eliminarTipoButton.setEnabled(false);
            }

            if (selectionModel.getSelectedItemsCount() == 1) {
                editarTipoButton.setEnabled(true);
            } else {
                editarTipoButton.setEnabled(false);
            }

        });
        setIdle();
        eliminarTipoButton.setEnabled(false);
        editarTipoButton.setEnabled(false);
        nuevoTipoDialog.pack();
        nuevoTipoDialog.setLocationRelativeTo(this);
        System.out.println("Clientes tab - Nueva instancia!");
    }

    @PostConstruct
    private void init() {
        checkPermissions();
        retrieveData(false); // mover hacia un listener que verifique que se ha abierto el jPanel
    }

    private void checkPermissions() {
        List<String> permissions = securityService.getPermissions();
        if (!permissions.contains("read")) {
            canRead = false;
            loadMoreButton.setEnabled(false);
            reloadTableButton.setEnabled(false);
        }
        if (!permissions.contains("create")) {
            canCreate = false;
            nuevoTipoButton.setEnabled(false);
            guardarTipoButton.setEnabled(false);
        }
        if (!permissions.contains("delete")) {
            canDelete = false;
            eliminarTipoButton.setEnabled(false);
        }
        if (!permissions.contains("edit")) {
            canEdit = false;
            editarTipoButton.setEnabled(false);
        }

    }

    private void setBusy() {
        busyLabel.setEnabled(true);
    }

    private void setBusy(String message) {
        busyLabel.setEnabled(true);
        busyLabel.setText(message);
    }

    private void setIdle() {
        busyLabel.setEnabled(false);
        busyLabel.setText("");
    }

    private void retrieveData(boolean reload) {
        if (!canRead) {
            setBusy("Sin permisos suficientes para leer datos.");
            return;
        }
        setBusy("Cargando...");
        reloadTableButton.setEnabled(false);
        loadMoreButton.setEnabled(false);
        retrievingData = true;

        if (reload) {
            defaultTableModelTipoProducto.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }

        SwingWorker worker = new SwingWorker<List<TipoProducto>, List<TipoProducto>>() {
            @Override
            protected List<TipoProducto> doInBackground() throws Exception {
                return tipoproductoService.streamTipoProducto(lastId, limit); // set lastId and configurable limit
            }

            @Override
            protected void done() {
                try {
                    var tipoproductos = get();
                    for (TipoProducto tipoproducto : tipoproductos) {
                        Object[] values = new Object[2];
                        values[0] = tipoproducto.getIdTipoProducto();
                        values[1] = tipoproducto.getTipo();

                        defaultTableModelTipoProducto.addRow(values);
                    }
                    int lastRow = 0;
                    int rowCount = defaultTableModelTipoProducto.getRowCount();
                    if (rowCount != 0) {
                        lastRow = rowCount - 1;
                    }
                    var id = defaultTableModelTipoProducto.getValueAt(lastRow, 0);
                    lastId = Long.parseLong(id.toString());
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(MarcaTab.class.getName()).log(Level.SEVERE, null, ex);
                }
                setIdle();
                reloadTableButton.setEnabled(true);
                loadMoreButton.setEnabled(true);
                retrievingData = false;
            }
        };
        worker.execute();
    }

    private List<TipoProducto> getSelectedRows() {
        List<TipoProducto> TipoProducto = new ArrayList<>();
        for (int i : selectionModel.getSelectedIndices()) { //rows 
            //System.out.println(i);
            i = tablaTipoTab.convertRowIndexToModel(i); //IMPORTANTISIMO, en caso de que la tabla esté ordenada por alguna columna, esto devolvera siempre la fila seleccionada.
            TipoProducto c = new TipoProducto();
            c.setIdTipoProducto((Long) defaultTableModelTipoProducto.getValueAt(i, 0));
            c.setTipo((String) defaultTableModelTipoProducto.getValueAt(i, 1));

            TipoProducto.add(c);
        }
        return TipoProducto;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nuevoTipoDialog = new javax.swing.JDialog();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        nuevaMarcaLabel = new javax.swing.JLabel();
        guardarTipoButton = new javax.swing.JButton();
        cancelarTipoButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        idField = new javax.swing.JTextField();
        tipoField = new javax.swing.JTextField();
        scrollPane = new javax.swing.JScrollPane();
        tablaTipoTab = new org.jdesktop.swingx.JXTable();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        nuevoTipoButton = new javax.swing.JButton();
        editarTipoButton = new javax.swing.JButton();
        eliminarTipoButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();
        reloadTableButton = new javax.swing.JButton();

        nuevaMarcaLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nuevaMarcaLabel.setText("Nuevo Tipo");

        guardarTipoButton.setText("Guardar");
        guardarTipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarTipoButtonActionPerformed(evt);
            }
        });

        cancelarTipoButton.setText("Cancelar");
        cancelarTipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarTipoButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("ID:");

        jLabel2.setText("Tipo de Producto:");

        tipoField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipoFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout nuevoTipoDialogLayout = new javax.swing.GroupLayout(nuevoTipoDialog.getContentPane());
        nuevoTipoDialog.getContentPane().setLayout(nuevoTipoDialogLayout);
        nuevoTipoDialogLayout.setHorizontalGroup(
            nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(nuevoTipoDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoTipoDialogLayout.createSequentialGroup()
                        .addComponent(nuevaMarcaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 158, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoTipoDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(guardarTipoButton)
                        .addGap(29, 29, 29)
                        .addComponent(cancelarTipoButton)))
                .addContainerGap())
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(nuevoTipoDialogLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(27, 27, 27)
                .addGroup(nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(idField)
                    .addComponent(tipoField, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        nuevoTipoDialogLayout.setVerticalGroup(
            nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nuevoTipoDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nuevaMarcaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(idField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tipoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cancelarTipoButton)
                    .addComponent(guardarTipoButton))
                .addGap(32, 32, 32))
        );

        setPreferredSize(new java.awt.Dimension(626, 419));

        tablaTipoTab.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        scrollPane.setViewportView(tablaTipoTab);

        busyLabel.setBusy(true);
        busyLabel.setPreferredSize(new java.awt.Dimension(22, 22));

        nuevoTipoButton.setText("Nuevo");
        nuevoTipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoTipoButtonActionPerformed(evt);
            }
        });

        editarTipoButton.setText("Editar");

        eliminarTipoButton.setText("Eliminar");
        eliminarTipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarTipoButtonActionPerformed(evt);
            }
        });

        loadMoreButton.setText("Cargar más entradas");
        loadMoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMoreButtonActionPerformed(evt);
            }
        });

        reloadTableButton.setText("Recargar");
        reloadTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadTableButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 214, Short.MAX_VALUE)
                .addComponent(loadMoreButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(reloadTableButton)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nuevoTipoButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editarTipoButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(eliminarTipoButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevoTipoButton)
                    .addComponent(editarTipoButton)
                    .addComponent(eliminarTipoButton))
                .addGap(6, 6, 6)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(busyLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(loadMoreButton)
                        .addComponent(reloadTableButton)))
                .addGap(6, 6, 6))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void eliminarTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarTipoButtonActionPerformed
        List<Long> selectedMarcasId = new ArrayList();
        for (TipoProducto cliente : getSelectedRows()) {
            selectedMarcasId.add(cliente.getIdTipoProducto());
        }
        try {
            List<TipoProducto> marcaEliminados = tipoproductoService.eliminarTipoProducto(selectedMarcasId);
        } catch (NotEnoughPermissionsException ex) {
            Logger.getLogger(TipoTab.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_eliminarTipoButtonActionPerformed

    private void nuevoTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoTipoButtonActionPerformed
        nuevoTipoDialog.setVisible(true);
    }//GEN-LAST:event_nuevoTipoButtonActionPerformed

    private void tipoFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tipoFieldActionPerformed

    private void cancelarTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarTipoButtonActionPerformed
        nuevoTipoDialog.setVisible(false); // TODO add your handling code here:
    }//GEN-LAST:event_cancelarTipoButtonActionPerformed

    private void guardarTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarTipoButtonActionPerformed
        idField.putClientProperty("JComponent.outline", "");
        tipoField.putClientProperty("JComponent.outline", "");

        //TODO: reemplazar razon social por un combobox
        TipoProducto c = new TipoProducto();
        int id = 0;
        int telefono = 0;
        boolean error = false;

        if (tipoField.getText().isBlank()) {
            tipoField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (idField.getText().isBlank()) {
            idField.putClientProperty("JComponent.outline", "error");
            error = true;
        } else {
            try {
                id = Integer.parseInt(idField.getText());
            } catch (Exception e) {
                idField.putClientProperty("JComponent.outline", "error");
                error = true;
                e.printStackTrace();
            }
        }

        if (error) {
            return;
        } else {
            c.setIdTipoProducto((long) (id));
            c.setTipo(tipoField.getText());

            try {
                tipoproductoService.nuevoTipoProducto(c); // implementar swingworker
            } catch (NotEnoughPermissionsException ex) {
                Logger.getLogger(TipoTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        nuevoTipoDialog.setVisible(false);
    }//GEN-LAST:event_guardarTipoButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        retrieveData(false);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    private void reloadTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadTableButtonActionPerformed
        retrieveData(true);
    }//GEN-LAST:event_reloadTableButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarTipoButton;
    private javax.swing.JButton editarTipoButton;
    private javax.swing.JButton eliminarTipoButton;
    private javax.swing.JButton guardarTipoButton;
    private javax.swing.JTextField idField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JLabel nuevaMarcaLabel;
    private javax.swing.JButton nuevoTipoButton;
    private javax.swing.JDialog nuevoTipoDialog;
    private javax.swing.JButton reloadTableButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaTipoTab;
    private javax.swing.JTextField tipoField;
    // End of variables declaration//GEN-END:variables
}
