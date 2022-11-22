package com.utp.trabajo.gui.view.almacen;

import com.utp.trabajo.model.entities.Marca;
import com.utp.trabajo.services.transaction.MarcaService;
import com.utp.trabajo.services.security.SecurityService;
import java.awt.event.AdjustmentEvent;
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
import org.springframework.beans.factory.annotation.Autowired;

public class MarcaTab extends org.jdesktop.swingx.JXPanel {

    DefaultTableModel defaultTableModelMarca = new DefaultTableModel() {
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
    String[] columnNames = {"ID", "Marca"};

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
    private MarcaService marcaService;

    public MarcaTab() {
        initComponents();

        defaultTableModelMarca.setColumnIdentifiers(columnNames);
        tablaMarca.setModel(defaultTableModelMarca);
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
        setIdle();
        System.out.println("Clientes tab - Nueva instancia!");
        selectionModel = tablaMarca.getSelectionModel();
        selectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            if (!canDelete || !canEdit) {
                return;
            }
            if (!selectionModel.isSelectionEmpty()) {
                eliminarMarcaButton.setEnabled(true);
            } else {
                eliminarMarcaButton.setEnabled(false);
            }

            if (selectionModel.getSelectedItemsCount() == 1) {
                editarMarcaButton.setEnabled(true);
            } else {
                editarMarcaButton.setEnabled(false);
            }

        });
        setIdle();
        eliminarMarcaButton.setEnabled(false);
        editarMarcaButton.setEnabled(false);
        nuevaMarcaDialog.pack();
        nuevaMarcaDialog.setLocationRelativeTo(this);
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
            nuevaMarcaButton.setEnabled(false);
            guardarMarcaButton.setEnabled(false);
        }
        if (!permissions.contains("delete")) {
            canDelete = false;
            eliminarMarcaButton.setEnabled(false);
        }
        if (!permissions.contains("edit")) {
            canEdit = false;
            editarMarcaButton.setEnabled(false);
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
            defaultTableModelMarca.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }

        SwingWorker worker = new SwingWorker<List<Marca>, List<Marca>>() {
            @Override
            protected List<Marca> doInBackground() throws Exception {
                return marcaService.streamMarca(lastId, limit); // set lastId and configurable limit
            }

            @Override
            protected void done() {
                try {
                    var marcas = get();
                    for (Marca marca : marcas) {
                        Object[] values = new Object[2];
                        values[0] = marca.getIdMarca();
                        values[1] = marca.getNombreMarca();

                        defaultTableModelMarca.addRow(values);
                    }
                    int lastRow = 0;
                    int rowCount = defaultTableModelMarca.getRowCount();
                    if (rowCount != 0) {
                        lastRow = rowCount - 1;
                    }
                    var id = defaultTableModelMarca.getValueAt(lastRow, 0);
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

    private List<Marca> getSelectedRows() {
        List<Marca> Marca = new ArrayList<>();
        for (int i : selectionModel.getSelectedIndices()) { //rows 
            //System.out.println(i);
            i = tablaMarca.convertRowIndexToModel(i); //IMPORTANTISIMO, en caso de que la tabla esté ordenada por alguna columna, esto devolvera siempre la fila seleccionada.
            Marca c = new Marca();
            c.setIdMarca((Long) defaultTableModelMarca.getValueAt(i, 0));
            c.setNombreMarca((String) defaultTableModelMarca.getValueAt(i, 1));

            Marca.add(c);
        }
        return Marca;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nuevaMarcaDialog = new javax.swing.JDialog();
        nuevaMarcaLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        cancelarCreacionClienteButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        idField = new javax.swing.JTextField();
        nombreMarcaField = new javax.swing.JTextField();
        guardarMarcaButton = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        tablaMarca = new org.jdesktop.swingx.JXTable();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        nuevaMarcaButton = new javax.swing.JButton();
        editarMarcaButton = new javax.swing.JButton();
        eliminarMarcaButton = new javax.swing.JButton();
        reloadTableButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();

        nuevaMarcaLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nuevaMarcaLabel.setText("Nueva Marca");

        cancelarCreacionClienteButton.setText("Cancelar");
        cancelarCreacionClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarCreacionClienteButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("ID Marca:");

        jLabel2.setText("Nombre de la Marca:");

        guardarMarcaButton.setText("Guardar");
        guardarMarcaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarMarcaButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout nuevaMarcaDialogLayout = new javax.swing.GroupLayout(nuevaMarcaDialog.getContentPane());
        nuevaMarcaDialog.getContentPane().setLayout(nuevaMarcaDialogLayout);
        nuevaMarcaDialogLayout.setHorizontalGroup(
            nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevaMarcaDialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(guardarMarcaButton)
                .addGap(18, 18, 18)
                .addComponent(cancelarCreacionClienteButton)
                .addContainerGap())
            .addGroup(nuevaMarcaDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nuevaMarcaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(nuevaMarcaDialogLayout.createSequentialGroup()
                        .addGroup(nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(24, 24, 24)
                        .addGroup(nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(idField)
                            .addComponent(nombreMarcaField, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE))))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        nuevaMarcaDialogLayout.setVerticalGroup(
            nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nuevaMarcaDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nuevaMarcaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(idField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(nombreMarcaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarCreacionClienteButton)
                    .addComponent(guardarMarcaButton))
                .addGap(19, 19, 19))
        );

        tablaMarca.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        scrollPane.setViewportView(tablaMarca);

        busyLabel.setBusy(true);
        busyLabel.setPreferredSize(new java.awt.Dimension(22, 22));

        nuevaMarcaButton.setText("Nuevo");
        nuevaMarcaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevaMarcaButtonActionPerformed(evt);
            }
        });

        editarMarcaButton.setText("Editar");

        eliminarMarcaButton.setText("Eliminar");
        eliminarMarcaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarMarcaButtonActionPerformed(evt);
            }
        });

        reloadTableButton.setText("Recargar");
        reloadTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadTableButtonActionPerformed(evt);
            }
        });

        loadMoreButton.setText("Cargar más entradas");
        loadMoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMoreButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(loadMoreButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(reloadTableButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nuevaMarcaButton)
                        .addGap(18, 18, 18)
                        .addComponent(editarMarcaButton)
                        .addGap(18, 18, 18)
                        .addComponent(eliminarMarcaButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevaMarcaButton)
                    .addComponent(editarMarcaButton)
                    .addComponent(eliminarMarcaButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(loadMoreButton)
                        .addComponent(reloadTableButton))
                    .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void nuevaMarcaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevaMarcaButtonActionPerformed
        // TODO add your handling code here:
        nuevaMarcaDialog.setVisible(true);
    }//GEN-LAST:event_nuevaMarcaButtonActionPerformed

    private void cancelarCreacionClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarCreacionClienteButtonActionPerformed
        nuevaMarcaDialog.setVisible(false);//TODO: put a confirmation dialog
    }//GEN-LAST:event_cancelarCreacionClienteButtonActionPerformed

    private void guardarMarcaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarMarcaButtonActionPerformed
        idField.putClientProperty("JComponent.outline", "");
        nombreMarcaField.putClientProperty("JComponent.outline", "");

        Marca c = new Marca();
        int id = 0;
        boolean error = false;

        if (nombreMarcaField.getText().isBlank()) {
            nombreMarcaField.putClientProperty("JComponent.outline", "error");
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
            c.setIdMarca((long) (id));
            c.setNombreMarca(nombreMarcaField.getText());

            marcaService.nuevaMarca(c); // implementar swingworker
        }
        nuevaMarcaDialog.setVisible(false);
    }//GEN-LAST:event_guardarMarcaButtonActionPerformed

    private void reloadTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadTableButtonActionPerformed
        retrieveData(true);
    }//GEN-LAST:event_reloadTableButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        retrieveData(false);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    private void eliminarMarcaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarMarcaButtonActionPerformed
        List<Long> selectedMarcasId = new ArrayList();
        for (Marca cliente : getSelectedRows()) {
            selectedMarcasId.add(cliente.getIdMarca());
        }
        List<Marca> marcaEliminados = marcaService.eliminarMarca(selectedMarcasId);
    }//GEN-LAST:event_eliminarMarcaButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarCreacionClienteButton;
    private javax.swing.JButton editarMarcaButton;
    private javax.swing.JButton eliminarMarcaButton;
    private javax.swing.JButton guardarMarcaButton;
    private javax.swing.JTextField idField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JTextField nombreMarcaField;
    private javax.swing.JButton nuevaMarcaButton;
    private javax.swing.JDialog nuevaMarcaDialog;
    private javax.swing.JLabel nuevaMarcaLabel;
    private javax.swing.JButton reloadTableButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaMarca;
    // End of variables declaration//GEN-END:variables
}
