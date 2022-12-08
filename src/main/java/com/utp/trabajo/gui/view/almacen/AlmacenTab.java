package com.utp.trabajo.gui.view.almacen;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.entities.Almacen;
import com.utp.trabajo.services.AlmacenService;
import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.util.OptionPaneService;
import java.awt.event.AdjustmentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;

public class AlmacenTab extends javax.swing.JPanel {

    DefaultTableModel defaultTableModelAlmacen = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Long.class;
                case 1:
                    return Integer.class;
                case 2:
                    return Integer.class;
                case 3:
                    return Integer.class;
                case 4:
                    return Integer.class;
                case 5:
                    return String.class;
                case 6:
                    return Integer.class;
                default:
                    return String.class;
            }
        }

    };
    String[] columnNames = {"ID", "Stock", "Stock Inicial", "Stock Minimo", "Columna", "Estanteria", "Fila"};
    
    ListSelectionModel selectionModel;
    private boolean canRead = true;
    private boolean canEdit = true;
    private boolean canDelete = true;
    private boolean canCreate = true;

    private boolean retrievingData = false;

    private long lastId = 0;

    private long rowsPerUpdate = 10;
    
    @Autowired
    private SecurityService securityService;

    @Autowired
    private AlmacenService almacenService;
    public AlmacenTab() {
        initComponents();
        initTableAlmacen();

        System.out.println("Clientes tab - Nueva instancia!");
        
    }
     @PostConstruct
    private void init() {
        checkPermissions();
        updateTable(false); // mover hacia un listener que verifique que se ha abierto el jPanel
    }
    
     private void initTableAlmacen() {
        defaultTableModelAlmacen.setColumnIdentifiers(columnNames);
        tablaAlmacen.setModel(defaultTableModelAlmacen);
        scrollPane.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            if (retrievingData) {
                return;
            }
            int maxValue = scrollPane.getVerticalScrollBar().getMaximum() - scrollPane.getVerticalScrollBar().getVisibleAmount();
            int currentValue = scrollPane.getVerticalScrollBar().getValue();
            float fraction = (float) currentValue / (float) maxValue;
            if (fraction > 0.999f) {
                updateTable(false);
                System.out.println("Scroll bar is near the bottom");
            }
        });
        selectionModel = tablaAlmacen.getSelectionModel();
        selectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            if (!canDelete || !canEdit) {
                return;
            }
            if (!selectionModel.isSelectionEmpty()) {
                eliminarAlmacenButton.setEnabled(true);
            } else {
                eliminarAlmacenButton.setEnabled(false);
            }

            if (selectionModel.getSelectedItemsCount() == 1) {
                editarAlmacenButton.setEnabled(true);
            } else {
                editarAlmacenButton.setEnabled(false);
            }

        });
        tablaAlmacen.getColumnModel().getColumn(0).setPreferredWidth(100);
        setIdle();
        eliminarAlmacenButton.setEnabled(false);
        editarAlmacenButton.setEnabled(false);
        nuevoAlmacenDialog.pack();
        nuevoAlmacenDialog.setLocationRelativeTo(this);

        jLayeredPane1.removeAll();
        jLayeredPane1.setLayer(tableInformationLabel, javax.swing.JLayeredPane.DEFAULT_LAYER, 0);
        jLayeredPane1.setLayer(scrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER, -1);
    }
     
      private void checkPermissions() {
        List<String> permissions = securityService.getPermissions();

        //read, create, edit, delete
        if (!permissions.contains("read")) {
            canRead = false;
            loadMoreButton.setEnabled(false);
            reloadTableButton.setEnabled(false);
        }
        if (!permissions.contains("create")) {
            canCreate = false;
            nuevoAlmacenButton.setEnabled(false);
            guardarAlmacenButton.setEnabled(false);
        }
        if (!permissions.contains("delete")) {
            canDelete = false;
            eliminarAlmacenButton.setEnabled(false);
        }
        if (!permissions.contains("edit")) {
            canEdit = false;
            editarAlmacenButton.setEnabled(false);
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
    
    private void addDataToTable(List<Almacen> data) {
        data.forEach(almacen -> {
            Vector vec = new Vector();
            vec.add(almacen.getIdProducto());
            vec.add(almacen.getStock());
            vec.add(almacen.getStockInicial());
            vec.add(almacen.getStockMinimo());
            vec.add(almacen.getColumna());
            vec.add(almacen.getEstanteria());
            vec.add(almacen.getFila());
            defaultTableModelAlmacen.addRow(vec);
        });
    }
    
        private void updateTable(boolean reload) { //refactor!  DONE!
        tableInformationLabel.setVisible(false);
        if (!canRead) {
            setBusy("Sin permisos suficientes para leer datos.");
            return;
        }

        setBusy("Cargando...");
        reloadTableButton.setEnabled(false);
        loadMoreButton.setEnabled(false);
        retrievingData = true;
        int oldRowCount = defaultTableModelAlmacen .getRowCount();
        if (reload) {
            defaultTableModelAlmacen .setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }

        SwingWorker worker2 = new SwingWorker<Long, Long>() {
            @Override
            protected Long doInBackground() throws Exception {
                return almacenService.contarAlmacen();
            }

            @Override
            protected void done() {
                try {
                    Long cantidadClientesDatabase = get();
                    int cantidadClientesTabla = defaultTableModelAlmacen .getRowCount();
                   // contadorClientesLabel.setText("Mostrando: " + cantidadClientesTabla + "/" + cantidadClientesDatabase);

                }
                catch (InterruptedException ex) {
                    try {
                        throw ex.getCause();
                    }
                    catch (NotEnoughPermissionsException e) {
                        //Joption pane or do nothing!
                    }
                    catch (Throwable e) {
                        System.out.println("impossible :");
                        e.printStackTrace();
                        System.out.println("impossible end");
                        return;
                    }
                }
                catch (ExecutionException ex) {
                    Logger.getLogger(AlmacenTab.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };
        SwingWorker obtenerAlmacensWorker = new SwingWorker<List<Almacen>, List<Almacen>>() {
            @Override
            protected List<Almacen> doInBackground() throws Exception {
                // set lastId and configurable rowsPerUpdate if reloading just reload all data
                if (reload) {
                    return almacenService.streamAlmacen(lastId, (long) oldRowCount);
                }
                return almacenService.streamAlmacen(lastId, rowsPerUpdate);
            }

            @Override
            protected void done() {
                try {
                    addDataToTable(get());
                    int lastRow = 0;
                    int rowCount = defaultTableModelAlmacen .getRowCount();
                    if (rowCount != 0) {
                        lastRow = rowCount - 1;
                    }
                    var id = defaultTableModelAlmacen .getValueAt(lastRow, 0);
                    lastId = Long.parseLong(id.toString());

                }
                catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(AlmacenTab.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    tableInformationLabel.setVisible(true);
                    //Logger.getLogger(ClientesTab.class.getName()).log(Level.SEVERE, null, ex);
                    //} catch (NotEnoughPermissionsException ex) {

                }
                finally {
                    setIdle();
                    reloadTableButton.setEnabled(true);
                    loadMoreButton.setEnabled(true);
                    retrievingData = false;
                }
                worker2.execute();
            }
        };
         obtenerAlmacensWorker.execute();

    }
        private List<Long> getIdFromSelectedRows() { // refactor! DONE!
        List<Long> idClientes = new ArrayList<>();
        for (int i : selectionModel.getSelectedIndices()) { //rows 
            i = tablaAlmacen.convertRowIndexToModel(i);
            // ↑ IMPORTANTISIMO, en caso de que la tabla esté ordenada por alguna columna, esto devolvera siempre la fila seleccionada.
            idClientes.add((Long) defaultTableModelAlmacen.getValueAt(i, 0));
        }
        return idClientes;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nuevoAlmacenDialog = new javax.swing.JDialog();
        guardarAlmacenButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        nuevoAlmacenButton = new javax.swing.JButton();
        editarAlmacenButton = new javax.swing.JButton();
        eliminarAlmacenButton = new javax.swing.JButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        scrollPane = new javax.swing.JScrollPane();
        tablaAlmacen = new org.jdesktop.swingx.JXTable();
        loadMoreButton = new javax.swing.JButton();
        reloadTableButton = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        tableInformationLabel = new javax.swing.JLabel();

        guardarAlmacenButton.setText("Guardar");

        jButton1.setText("jButton1");

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout nuevoAlmacenDialogLayout = new javax.swing.GroupLayout(nuevoAlmacenDialog.getContentPane());
        nuevoAlmacenDialog.getContentPane().setLayout(nuevoAlmacenDialogLayout);
        nuevoAlmacenDialogLayout.setHorizontalGroup(
            nuevoAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nuevoAlmacenDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nuevoAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoAlmacenDialogLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(319, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoAlmacenDialogLayout.createSequentialGroup()
                        .addGroup(nuevoAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(nuevoAlmacenDialogLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(guardarAlmacenButton)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1)))
                        .addGap(1, 1, 1))))
            .addComponent(jSeparator5)
        );
        nuevoAlmacenDialogLayout.setVerticalGroup(
            nuevoAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoAlmacenDialogLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 143, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addGroup(nuevoAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guardarAlmacenButton)
                    .addComponent(jButton1))
                .addGap(18, 18, 18))
        );

        nuevoAlmacenButton.setText("Nuevo");
        nuevoAlmacenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoAlmacenButtonActionPerformed(evt);
            }
        });

        editarAlmacenButton.setText("Editar");

        eliminarAlmacenButton.setText("Eliminar");

        tablaAlmacen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaAlmacen.setColumnControlVisible(true);
        tablaAlmacen.setEditable(false);
        scrollPane.setViewportView(tablaAlmacen);

        jLayeredPane1.setLayer(scrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane)
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
        );

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

        busyLabel.setBusy(true);
        busyLabel.setPreferredSize(new java.awt.Dimension(22, 22));

        tableInformationLabel.setText("Sin datos.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(loadMoreButton)
                        .addGap(18, 18, 18)
                        .addComponent(reloadTableButton)
                        .addGap(16, 16, 16))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLayeredPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(nuevoAlmacenButton)
                                .addGap(18, 18, 18)
                                .addComponent(editarAlmacenButton)
                                .addGap(18, 18, 18)
                                .addComponent(eliminarAlmacenButton)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(293, 293, 293)
                    .addComponent(tableInformationLabel)
                    .addContainerGap(293, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevoAlmacenButton)
                    .addComponent(editarAlmacenButton)
                    .addComponent(eliminarAlmacenButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(loadMoreButton)
                        .addComponent(reloadTableButton))
                    .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(201, 201, 201)
                    .addComponent(tableInformationLabel)
                    .addContainerGap(202, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void reloadTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadTableButtonActionPerformed
    updateTable(true);        
    }//GEN-LAST:event_reloadTableButtonActionPerformed

    private void nuevoAlmacenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoAlmacenButtonActionPerformed
        nuevoAlmacenDialog.setVisible(true);
    }//GEN-LAST:event_nuevoAlmacenButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        updateTable(true);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton editarAlmacenButton;
    private javax.swing.JButton eliminarAlmacenButton;
    private javax.swing.JButton guardarAlmacenButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JButton nuevoAlmacenButton;
    private javax.swing.JDialog nuevoAlmacenDialog;
    private javax.swing.JButton reloadTableButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaAlmacen;
    private javax.swing.JLabel tableInformationLabel;
    // End of variables declaration//GEN-END:variables
}
