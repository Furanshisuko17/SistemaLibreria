package com.utp.trabajo.gui.view.compras;

import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.transaction.ProveedorService;
import com.utp.trabajo.model.entities.Proveedor;
import java.awt.event.AdjustmentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;

public class ProovedoresTab extends org.jdesktop.swingx.JXPanel {

    DefaultTableModel defaultTableModelProveedores = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Long.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                case 3:
                    return String.class; //maybe just String?
                case 4:
                    return Integer.class;
                case 5:
                    return String.class;
                default:
                    return String.class;
            }
        }
    };

    String[] columnNames = {"IdProveedor", "Nombre", "Direccion", "RUC", "Teléfono", "Tipo de comercio"};

    ListSelectionModel selectionModel;

    private boolean canRead = true;
    private boolean canEdit = true;
    private boolean canDelete = true;
    private boolean canCreate = true;

    private boolean retrievingData = false;

    private long lastId = 0;

    private long limit = 100;
    @Autowired
    private SecurityService securityService;

    @Autowired
    private ProveedorService proveedoresService;

    public ProovedoresTab() {
        initComponents();
        defaultTableModelProveedores.setColumnIdentifiers(columnNames);
        tablaProveedores.setModel(defaultTableModelProveedores);
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

        selectionModel = tablaProveedores.getSelectionModel();
        selectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            if (!canDelete || !canEdit) {
                return;
            }
            if (!selectionModel.isSelectionEmpty()) {
                eliminarProveedorButton.setEnabled(true);
            } else {
                eliminarProveedorButton.setEnabled(false);
            }

            if (selectionModel.getSelectedItemsCount() == 1) {
                editarProveedorButton.setEnabled(true);
            } else {
                editarProveedorButton.setEnabled(false);
            }

        });

        setIdle();
        eliminarProveedorButton.setEnabled(false);
        editarProveedorButton.setEnabled(false);
        jLayeredPane1.removeAll();
        jLayeredPane1.setLayer(tableInformationLabel, javax.swing.JLayeredPane.DEFAULT_LAYER, 0);
        jLayeredPane1.setLayer(scrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER, -1);

        nuevoProveedorDialog.pack();
        nuevoProveedorDialog.setLocationRelativeTo(this);
        System.out.println("Proveedores tab - Nueva instancia!");
    }

    @PostConstruct
    private void init() {
        checkPermissions();
        retrieveData(false); // mover hacia un listener que verifique que se ha abierto el jPanel
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
            nuevoProveedorButton.setEnabled(false);
            //guardarProveedorButton.setEnabled(false);
        }
        if (!permissions.contains("delete")) {
            canDelete = false;
            eliminarProveedorButton.setEnabled(false);
        }
        if (!permissions.contains("edit")) {
            canEdit = false;
            editarProveedorButton.setEnabled(false);
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
        tableInformationLabel.setVisible(false);
        if (!canRead) {
            setBusy("Sin permisos suficientes para leer datos.");
            return;
        }

        setBusy("Cargando...");
        reloadTableButton.setEnabled(false);
        nuevoProveedorButton.setEnabled(false);
        retrievingData = true;
        loadMoreButton.setEnabled(false);

        if (reload) {
            defaultTableModelProveedores.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }

        SwingWorker worker = new SwingWorker<List<Proveedor>, List<Proveedor>>() {
            @Override
            protected List<Proveedor> doInBackground() throws Exception {
                return proveedoresService.streamProveedores(lastId, limit); // set lastId and configurable limit
            }

            @Override
            protected void done() {
                try {
                    var proveedores = get();
                    for (Proveedor proveedor : proveedores) {
                        Object[] values = new Object[6];
                        values[0] = proveedor.getIdProveedor();
                        values[1] = proveedor.getNombre();
                        values[2] = proveedor.getDireccion();
                        values[3] = proveedor.getRuc();
                        values[4] = proveedor.getTelefono();
                        values[5] = proveedor.getTipoComercio();
                        defaultTableModelProveedores.addRow(values);
                    }
                    int lastRow = 0;
                    int rowCount = defaultTableModelProveedores.getRowCount();
                    if (rowCount != 0) {
                        lastRow = rowCount - 1;
                    }
                    var id = defaultTableModelProveedores.getValueAt(lastRow, 0);
                    lastId = Long.parseLong(id.toString());
                } catch (InterruptedException | ExecutionException ex) {
                    setIdle();
                    Logger.getLogger(ProovedoresTab.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    tableInformationLabel.setVisible(true);
                    //busyLabel.setBusy(false);
                    //Logger.getLogger(ProovedoresTab.class.getName()).log(Level.SEVERE, null, ex);
                }
                setIdle();
                reloadTableButton.setEnabled(true);
                nuevoProveedorButton.setEnabled(true);
                loadMoreButton.setEnabled(true);
                retrievingData = false;
                retrievingData = false;
            }
        };
        worker.execute();
    }

    private List<Proveedor> getSelectedRows() {
        List<Proveedor> proveedores = new ArrayList<>();
        for (int i : selectionModel.getSelectedIndices()) { //rows 
            //System.out.println(i);
            i = tablaProveedores.convertRowIndexToModel(i); //IMPORTANTISIMO, en caso de que la tabla esté ordenada por alguna columna, esto devolvera siempre la fila seleccionada.
            Proveedor p = new Proveedor();
            p.setIdProveedor((Long) defaultTableModelProveedores.getValueAt(i, 0));
            p.setNombre((String) defaultTableModelProveedores.getValueAt(i, 1));
            p.setDireccion((String) defaultTableModelProveedores.getValueAt(i, 2));
            p.setRuc((String) defaultTableModelProveedores.getValueAt(i, 3));
            p.setTelefono((String) defaultTableModelProveedores.getValueAt(i, 4));
            p.setTipoComercio((String) defaultTableModelProveedores.getValueAt(i, 5));
            proveedores.add(p);
        }
        return proveedores;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nuevoProveedorDialog = new javax.swing.JDialog();
        nuevoProveedorLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        rucLabel = new javax.swing.JLabel();
        nombresLabel = new javax.swing.JLabel();
        rucField = new javax.swing.JTextField();
        nombresField = new javax.swing.JTextField();
        direccionLabel = new javax.swing.JLabel();
        direccionField = new javax.swing.JTextField();
        telefonoLabel = new javax.swing.JLabel();
        telefonoField = new javax.swing.JTextField();
        tipoComercioLabel = new javax.swing.JLabel();
        tipoComercioField = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        cancelarCreacionProveedorButton = new javax.swing.JButton();
        guardarProveedorButton = new javax.swing.JButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        tableInformationLabel = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        tablaProveedores = new org.jdesktop.swingx.JXTable();
        nuevoProveedorButton = new javax.swing.JButton();
        editarProveedorButton = new javax.swing.JButton();
        eliminarProveedorButton = new javax.swing.JButton();
        reloadTableButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));

        nuevoProveedorDialog.setResizable(false);

        nuevoProveedorLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nuevoProveedorLabel.setText("Crear nuevo proveedor");

        rucLabel.setText("RUC:");

        nombresLabel.setText("Nombres:");

        rucField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rucFieldActionPerformed(evt);
            }
        });

        direccionLabel.setText("Dirección:");

        telefonoLabel.setText("Teléfono:");

        tipoComercioLabel.setText("Tipo de comercio:");

        cancelarCreacionProveedorButton.setText("Cancelar");
        cancelarCreacionProveedorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarCreacionProveedorButtonActionPerformed(evt);
            }
        });

        guardarProveedorButton.setText("Guardar");
        guardarProveedorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarProveedorButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout nuevoProveedorDialogLayout = new javax.swing.GroupLayout(nuevoProveedorDialog.getContentPane());
        nuevoProveedorDialog.getContentPane().setLayout(nuevoProveedorDialogLayout);
        nuevoProveedorDialogLayout.setHorizontalGroup(
            nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                        .addComponent(rucLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                        .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tipoComercioLabel)
                            .addComponent(nombresLabel))
                        .addGap(18, 18, 18)
                        .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                                .addComponent(tipoComercioField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                                .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nombresField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rucField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                                        .addComponent(telefonoLabel)
                                        .addGap(23, 23, 23)
                                        .addComponent(telefonoField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                                        .addComponent(direccionLabel)
                                        .addGap(18, 18, 18)
                                        .addComponent(direccionField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addGap(66, 66, 66))
            .addComponent(jSeparator1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoProveedorDialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoProveedorDialogLayout.createSequentialGroup()
                        .addComponent(nuevoProveedorLabel)
                        .addGap(167, 167, 167))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoProveedorDialogLayout.createSequentialGroup()
                        .addComponent(guardarProveedorButton)
                        .addGap(18, 18, 18)
                        .addComponent(cancelarCreacionProveedorButton)
                        .addContainerGap())))
        );
        nuevoProveedorDialogLayout.setVerticalGroup(
            nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(nuevoProveedorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(direccionField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(direccionLabel)
                            .addComponent(rucField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(rucLabel)))
                .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(nombresLabel))
                    .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(telefonoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(telefonoLabel)
                                .addComponent(nombresField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tipoComercioField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tipoComercioLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoProveedorDialogLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelarCreacionProveedorButton)
                            .addComponent(guardarProveedorButton))
                        .addContainerGap())))
        );

        tableInformationLabel.setText("Sin datos.");

        tablaProveedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaProveedores.setColumnControlVisible(true);
        scrollPane.setViewportView(tablaProveedores);

        jLayeredPane1.setLayer(tableInformationLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(scrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 598, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(tableInformationLabel)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 267, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(tableInformationLabel)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        nuevoProveedorButton.setText("Nuevo");
        nuevoProveedorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoProveedorButtonActionPerformed(evt);
            }
        });

        editarProveedorButton.setText("Editar");
        editarProveedorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarProveedorButtonActionPerformed(evt);
            }
        });

        eliminarProveedorButton.setText("Eliminar");
        eliminarProveedorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarProveedorButtonActionPerformed(evt);
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

        busyLabel.setBusy(true);
        busyLabel.setPreferredSize(new java.awt.Dimension(22, 22));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 350, Short.MAX_VALUE)
                        .addComponent(loadMoreButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(reloadTableButton))
                    .addComponent(jLayeredPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nuevoProveedorButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editarProveedorButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(eliminarProveedorButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevoProveedorButton)
                    .addComponent(editarProveedorButton)
                    .addComponent(eliminarProveedorButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reloadTableButton)
                    .addComponent(loadMoreButton)
                    .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void nuevoProveedorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoProveedorButtonActionPerformed
        // TODO add your handling code here:
        nuevoProveedorDialog.setVisible(true);
    }//GEN-LAST:event_nuevoProveedorButtonActionPerformed

    private void editarProveedorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarProveedorButtonActionPerformed
        // TODO add your handling code here:
        List<Proveedor> proveedoresSeleccionado = getSelectedRows();
        if (proveedoresSeleccionado.size() == 1) {
            Proveedor proveedorSeleccionado = proveedoresSeleccionado.get(0);
        }
    }//GEN-LAST:event_editarProveedorButtonActionPerformed

    private void eliminarProveedorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarProveedorButtonActionPerformed
        // TODO add your handling code here:
        List<Long> selectedProveedoresId = new ArrayList();
        for (Proveedor proveedor : getSelectedRows()) {
            selectedProveedoresId.add(proveedor.getIdProveedor());
        }
        List<Proveedor> proveedoresEliminados = proveedoresService.eliminarProveedor(selectedProveedoresId);

        retrieveData(true);
    }//GEN-LAST:event_eliminarProveedorButtonActionPerformed

    private void reloadTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadTableButtonActionPerformed
        // TODO add your handling code here:
        retrieveData(true);
    }//GEN-LAST:event_reloadTableButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        // TODO add your handling code here:
        retrieveData(false);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    private void guardarProveedorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarProveedorButtonActionPerformed
        // TODO add your handling code here:
        nombresField.putClientProperty("JComponent.outline", "");
        rucField.putClientProperty("JComponent.outline", "");
        telefonoField.putClientProperty("JComponent.outline", "");
        direccionField.putClientProperty("JComponent.outline", "");
        tipoComercioField.putClientProperty("JComponent.outline", "");
        //TODO: reemplazar razon social por un combobox
        Proveedor p = new Proveedor();
        int ruc = 0;
        int telefono = 0;
        boolean error = false;

        if (nombresField.getText().isBlank()) {
            nombresField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (rucField.getText().isBlank()) {
            rucField.putClientProperty("JComponent.outline", "error");
            error = true;
        } else {
            try {
                ruc = Integer.parseInt(rucField.getText());
            } catch (Exception e) {
                rucField.putClientProperty("JComponent.outline", "error");
                error = true;
                e.printStackTrace();
            }
        }

        if (telefonoField.getText().isBlank()) {
            telefonoField.putClientProperty("JComponent.outline", "error");
            error = true;
        } else {
            try {
                telefono = Integer.parseInt(telefonoField.getText());
            } catch (Exception e) {
                telefonoField.putClientProperty("JComponent.outline", "error");
                error = true;
                e.printStackTrace();
            }
        }

        if (direccionField.getText().isBlank()) {
            direccionField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (tipoComercioField.getText().isBlank()) {
            tipoComercioField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (error) {
            return;
        } else {
            p.setRuc(String.valueOf(ruc));
            p.setNombre(nombresField.getText());
            p.setDireccion(direccionField.getText());
            p.setTipoComercio(tipoComercioField.getText());
            p.setTelefono(String.valueOf(telefono));
            proveedoresService.nuevoProveedor(p); // implementar swingworker
        }
        nuevoProveedorDialog.setVisible(false);
    }//GEN-LAST:event_guardarProveedorButtonActionPerformed

    private void rucFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rucFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rucFieldActionPerformed

    private void cancelarCreacionProveedorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarCreacionProveedorButtonActionPerformed
        // TODO add your handling code here:
        nuevoProveedorDialog.setVisible(false);
    }//GEN-LAST:event_cancelarCreacionProveedorButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarCreacionProveedorButton;
    private javax.swing.JTextField direccionField;
    private javax.swing.JLabel direccionLabel;
    private javax.swing.JButton editarProveedorButton;
    private javax.swing.JButton eliminarProveedorButton;
    private javax.swing.JButton guardarProveedorButton;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JTextField nombresField;
    private javax.swing.JLabel nombresLabel;
    private javax.swing.JButton nuevoProveedorButton;
    private javax.swing.JDialog nuevoProveedorDialog;
    private javax.swing.JLabel nuevoProveedorLabel;
    private javax.swing.JButton reloadTableButton;
    private javax.swing.JTextField rucField;
    private javax.swing.JLabel rucLabel;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaProveedores;
    private javax.swing.JLabel tableInformationLabel;
    private javax.swing.JTextField telefonoField;
    private javax.swing.JLabel telefonoLabel;
    private javax.swing.JTextField tipoComercioField;
    private javax.swing.JLabel tipoComercioLabel;
    // End of variables declaration//GEN-END:variables
}
