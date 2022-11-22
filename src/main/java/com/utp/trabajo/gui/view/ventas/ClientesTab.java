package com.utp.trabajo.gui.view.ventas;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.entities.Cliente;
import com.utp.trabajo.services.ClienteService;
import com.utp.trabajo.services.security.SecurityService;
import java.awt.event.AdjustmentEvent;
import java.util.ArrayList;
import java.util.List;
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

public class ClientesTab extends org.jdesktop.swingx.JXPanel {

    DefaultTableModel defaultTableModelClientes = new DefaultTableModel() {
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
    String[] columnNames = {"ID", "Nombre", "Dirección", "DNI/RUC", "Teléfono", "Razón social", "N° compras"};
    //TODO: set minimum and default sizes for each column

    ListSelectionModel selectionModel;

    private boolean canRead = true;
    private boolean canEdit = true;
    private boolean canDelete = true;
    private boolean canCreate = true;

    private boolean retrievingData = false;

    private long lastId = 0;

    private long limit = 10;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ClienteService clienteService; //TODO:  refactor!

    public ClientesTab() {
        initComponents();
        initTableClientes();

        System.out.println("Clientes tab - Nueva instancia!");
    }

    @PostConstruct
    private void init() {
        checkPermissions();
        updateTable(false, false); // mover hacia un listener que verifique que se ha abierto el jPanel
    }

    private void initTableClientes() {
        defaultTableModelClientes.setColumnIdentifiers(columnNames);
        tablaClientes.setModel(defaultTableModelClientes);
        scrollPane.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            if (retrievingData) {
                return;
            }
            int maxValue = scrollPane.getVerticalScrollBar().getMaximum() - scrollPane.getVerticalScrollBar().getVisibleAmount();
            int currentValue = scrollPane.getVerticalScrollBar().getValue();
            float fraction = (float) currentValue / (float) maxValue;
            if (fraction > 0.999f) {
                updateTable(false, false);
                System.out.println("Scroll bar is near the bottom");
            }
        });
        selectionModel = tablaClientes.getSelectionModel();
        selectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            if (!canDelete || !canEdit) {
                return;
            }
            if (!selectionModel.isSelectionEmpty()) {
                eliminarClienteButton.setEnabled(true);
            } else {
                eliminarClienteButton.setEnabled(false);
            }

            if (selectionModel.getSelectedItemsCount() == 1) {
                editarClienteButton.setEnabled(true);
            } else {
                editarClienteButton.setEnabled(false);
            }

        });
        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(100);
        setIdle();
        eliminarClienteButton.setEnabled(false);
        editarClienteButton.setEnabled(false);
        nuevoClienteDialog.pack();
        nuevoClienteDialog.setLocationRelativeTo(this);

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
            nuevoClienteButton.setEnabled(false);
            guardarClienteButton.setEnabled(false);
        }
        if (!permissions.contains("delete")) {
            canDelete = false;
            eliminarClienteButton.setEnabled(false);
        }
        if (!permissions.contains("edit")) {
            canEdit = false;
            editarClienteButton.setEnabled(false);
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

    private void updateTable(boolean reload, boolean checkChanges) { //refactor!  DONE!
        tableInformationLabel.setVisible(false);
        if (!canRead) {
            setBusy("Sin permisos suficientes para leer datos.");
            return;
        }

        setBusy("Cargando...");
        reloadTableButton.setEnabled(false);
        loadMoreButton.setEnabled(false);
        retrievingData = true;
        if (reload) {
            defaultTableModelClientes.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }

        SwingWorker worker2 = new SwingWorker<Long, Long>() {
            @Override
            protected Long doInBackground() throws Exception {
                return clienteService.contarClientes();
            }

            @Override
            protected void done() {
                try {
                    Long cantidadClientesDatabase = get();
                    int cantidadClientesTabla = defaultTableModelClientes.getRowCount();
                    contadorClientesLabel.setText("Mostrando: " + cantidadClientesTabla + "/" + cantidadClientesDatabase);

                } catch (InterruptedException ex) {
                    try {
                        throw ex.getCause();
                    } catch (NotEnoughPermissionsException e) {
                        //Joption pane or do nothing!
                    } catch (Throwable e) {
                        System.out.println("impossible :");
                        e.printStackTrace();
                        System.out.println("impossible end");
                        return;
                    }
                } catch (ExecutionException ex) {
                    Logger.getLogger(ClientesTab.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };
        SwingWorker obtenerClientesWorker = new SwingWorker<Boolean, Boolean>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return clienteService.obtenerClientes(lastId, limit); // set lastId and configurable limit
            }

            @Override
            protected void done() {
                try {
                    boolean ready = get();
                    if (ready) {
                        clienteService.updateTable(defaultTableModelClientes, checkChanges, limit);
                    }
                    int lastRow = 0;
                    int rowCount = defaultTableModelClientes.getRowCount();
                    if (rowCount != 0) {
                        lastRow = rowCount - 1;
                    }
                    var id = defaultTableModelClientes.getValueAt(lastRow, 0);
                    lastId = Long.parseLong(id.toString());

                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(ClientesTab.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    tableInformationLabel.setVisible(true);
                    //Logger.getLogger(ClientesTab.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NotEnoughPermissionsException ex) {

                } finally {
                    setIdle();
                    reloadTableButton.setEnabled(true);
                    loadMoreButton.setEnabled(true);
                    retrievingData = false;
                }
                worker2.execute();
            }
        };
        obtenerClientesWorker.execute();

    }

    private List<Long> getIdFromSelectedRows() { // refactor! DONE!
        List<Long> idClientes = new ArrayList<>();
        for (int i : selectionModel.getSelectedIndices()) { //rows 
            i = tablaClientes.convertRowIndexToModel(i);
            // ↑ IMPORTANTISIMO, en caso de que la tabla esté ordenada por alguna columna, esto devolvera siempre la fila seleccionada.
            idClientes.add((Long) defaultTableModelClientes.getValueAt(i, 0));
        }
        return idClientes;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nuevoClienteDialog = new javax.swing.JDialog();
        nuevoClienteLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        dniLabel = new javax.swing.JLabel();
        nombresLabel = new javax.swing.JLabel();
        dniField = new javax.swing.JTextField();
        nombresField = new javax.swing.JTextField();
        direccionLabel = new javax.swing.JLabel();
        direccionField = new javax.swing.JTextField();
        telefonoLabel = new javax.swing.JLabel();
        telefonoField = new javax.swing.JTextField();
        razonSocialLabel = new javax.swing.JLabel();
        razonSocialField = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        cancelarCreacionClienteButton = new javax.swing.JButton();
        guardarClienteButton = new javax.swing.JButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        tableInformationLabel = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        tablaClientes = new org.jdesktop.swingx.JXTable();
        reloadTableButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();
        editarClienteButton = new javax.swing.JButton();
        nuevoClienteButton = new javax.swing.JButton();
        eliminarClienteButton = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        contadorClientesLabel = new javax.swing.JLabel();

        nuevoClienteDialog.setTitle("Nuevo cliente");
        nuevoClienteDialog.setAlwaysOnTop(true);
        nuevoClienteDialog.setResizable(false);

        nuevoClienteLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nuevoClienteLabel.setText("Crear nuevo cliente");

        dniLabel.setText("DNI / RUC:");

        nombresLabel.setText("Nombres:");

        direccionLabel.setText("Direccion:");

        telefonoLabel.setText("Teléfono:");

        razonSocialLabel.setText("Raz. social:");

        cancelarCreacionClienteButton.setText("Cancelar");
        cancelarCreacionClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarCreacionClienteButtonActionPerformed(evt);
            }
        });

        guardarClienteButton.setText("Guardar");
        guardarClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarClienteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout nuevoClienteDialogLayout = new javax.swing.GroupLayout(nuevoClienteDialog.getContentPane());
        nuevoClienteDialog.getContentPane().setLayout(nuevoClienteDialogLayout);
        nuevoClienteDialogLayout.setHorizontalGroup(
            nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                        .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                                .addComponent(telefonoLabel)
                                .addGap(14, 14, 14)
                                .addComponent(telefonoField, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
                            .addComponent(nuevoClienteLabel)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoClienteDialogLayout.createSequentialGroup()
                                .addComponent(dniLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dniField))
                            .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                                .addComponent(nombresLabel)
                                .addGap(11, 11, 11)
                                .addComponent(nombresField)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                                .addComponent(direccionLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(direccionField, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                            .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                                .addComponent(razonSocialLabel)
                                .addGap(8, 8, 8)
                                .addComponent(razonSocialField))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoClienteDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(guardarClienteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelarCreacionClienteButton)))
                .addContainerGap())
        );
        nuevoClienteDialogLayout.setVerticalGroup(
            nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nuevoClienteLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(dniLabel)
                            .addComponent(dniField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nombresLabel)
                            .addComponent(nombresField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                        .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(direccionLabel)
                            .addComponent(direccionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(razonSocialLabel)
                            .addComponent(razonSocialField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(telefonoLabel)
                    .addComponent(telefonoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarCreacionClienteButton)
                    .addComponent(guardarClienteButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tableInformationLabel.setText("Sin datos.");

        tablaClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaClientes.setColumnControlVisible(true);
        tablaClientes.setEditable(false);
        scrollPane.setViewportView(tablaClientes);

        jLayeredPane1.setLayer(tableInformationLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(scrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(tableInformationLabel)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(tableInformationLabel)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

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

        editarClienteButton.setText("Editar");
        editarClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarClienteButtonActionPerformed(evt);
            }
        });

        nuevoClienteButton.setText("Nuevo");
        nuevoClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoClienteButtonActionPerformed(evt);
            }
        });

        eliminarClienteButton.setText("Eliminar");
        eliminarClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarClienteButtonActionPerformed(evt);
            }
        });

        busyLabel.setBusy(true);
        busyLabel.setPreferredSize(new java.awt.Dimension(22, 22));

        contadorClientesLabel.setText("Cargando...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nuevoClienteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editarClienteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(eliminarClienteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(contadorClientesLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(busyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(loadMoreButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(reloadTableButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(nuevoClienteButton)
                        .addComponent(editarClienteButton)
                        .addComponent(eliminarClienteButton))
                    .addComponent(contadorClientesLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadMoreButton)
                    .addComponent(reloadTableButton)
                    .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void reloadTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadTableButtonActionPerformed
        updateTable(true, false);
    }//GEN-LAST:event_reloadTableButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        updateTable(false, false);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    private void nuevoClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoClienteButtonActionPerformed
        nuevoClienteDialog.setVisible(true);
    }//GEN-LAST:event_nuevoClienteButtonActionPerformed

    //TODO: ↓ Refactor!
    private void eliminarClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarClienteButtonActionPerformed
        List<Long> selectedClientesId = getIdFromSelectedRows();

        int selectedOption = JOptionPane.showConfirmDialog(this,
            "¿Desea eliminar " + (selectedClientesId.size() == 1 ? "1 cliente?" : (selectedClientesId.size() + " clientes?")),
            selectedClientesId.size() == 1 ? "Eliminar un cliente" : "Eliminar varios clientes",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (selectedOption == JOptionPane.YES_OPTION) {
            List<Cliente> clientesEliminados = new ArrayList<>();
            try {
                clientesEliminados = clienteService.eliminarClientes(selectedClientesId); //implementar swingworker
            } catch (NotEnoughPermissionsException ex) {
                // just pass?
            }

            if (!clientesEliminados.isEmpty()) {
                try {
                    clienteService.updateTable(defaultTableModelClientes, true, limit);
                } catch (NotEnoughPermissionsException ex) {
                    //implement j option pane? 
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Ocurrió un error al eliminar " + (selectedClientesId.size() == 1 ? "el cliente seleccionado." : "los clientes seleccionados."),
                    "¡Error!", JOptionPane.ERROR_MESSAGE);
            }
        } else if (selectedOption == JOptionPane.NO_OPTION) {
            //do nothing
        }

    }//GEN-LAST:event_eliminarClienteButtonActionPerformed

    private void cancelarCreacionClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarCreacionClienteButtonActionPerformed
        nuevoClienteDialog.setVisible(false);//TODO: put a confirmation dialog
    }//GEN-LAST:event_cancelarCreacionClienteButtonActionPerformed

    private void guardarClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarClienteButtonActionPerformed
        nombresField.putClientProperty("JComponent.outline", "");
        dniField.putClientProperty("JComponent.outline", "");
        telefonoField.putClientProperty("JComponent.outline", "");
        direccionField.putClientProperty("JComponent.outline", "");
        razonSocialField.putClientProperty("JComponent.outline", "");
        //TODO: reemplazar razon social por un combobox
        Cliente c = new Cliente();
        int dni = 0;
        int telefono = 0;
        boolean error = false;

        if (nombresField.getText().isBlank()) {
            nombresField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (dniField.getText().isBlank()) {
            dniField.putClientProperty("JComponent.outline", "error");
            error = true;
        } else {
            try {
                dni = Integer.parseInt(dniField.getText());
            } catch (Exception e) {
                dniField.putClientProperty("JComponent.outline", "error");
                error = true;
                //e.printStackTrace();
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
                //e.printStackTrace();
            }
        }

        if (direccionField.getText().isBlank()) {
            direccionField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (razonSocialField.getText().isBlank()) {
            razonSocialField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (error) {
            return;
        } else {
            c.setIdentificacion(String.valueOf(dni));
            c.setNombre(nombresField.getText());
            c.setDireccion(direccionField.getText());
            c.setRazonSocial(razonSocialField.getText());
            c.setTelefono(String.valueOf(telefono));
            c.setNumeroCompras((long) 0);
            try {
                clienteService.nuevoCliente(c); // implementar swingworker //TODO: refactor!
            } catch (NotEnoughPermissionsException ex) {
                //Implementar un Jdialog o dejarlo así...
            }
        }
        nuevoClienteDialog.setVisible(false);
    }//GEN-LAST:event_guardarClienteButtonActionPerformed

    private void editarClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarClienteButtonActionPerformed
        List<Long> idClientesSeleccionado = getIdFromSelectedRows();
        if (idClientesSeleccionado.size() == 1) {
            Cliente clienteSeleccionado = null;
            try {
                clienteSeleccionado = clienteService.obtenerClientePorId(idClientesSeleccionado.get(0));
            } catch (NotEnoughPermissionsException ex) {
                return;
            }
        } //TODO: Refactor and complete
    }//GEN-LAST:event_editarClienteButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarCreacionClienteButton;
    private javax.swing.JLabel contadorClientesLabel;
    private javax.swing.JTextField direccionField;
    private javax.swing.JLabel direccionLabel;
    private javax.swing.JTextField dniField;
    private javax.swing.JLabel dniLabel;
    private javax.swing.JButton editarClienteButton;
    private javax.swing.JButton eliminarClienteButton;
    private javax.swing.JButton guardarClienteButton;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JTextField nombresField;
    private javax.swing.JLabel nombresLabel;
    private javax.swing.JButton nuevoClienteButton;
    private javax.swing.JDialog nuevoClienteDialog;
    private javax.swing.JLabel nuevoClienteLabel;
    private javax.swing.JTextField razonSocialField;
    private javax.swing.JLabel razonSocialLabel;
    private javax.swing.JButton reloadTableButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaClientes;
    private javax.swing.JLabel tableInformationLabel;
    private javax.swing.JTextField telefonoField;
    private javax.swing.JLabel telefonoLabel;
    // End of variables declaration//GEN-END:variables
}
