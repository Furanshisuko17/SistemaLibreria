package com.utp.trabajo.gui.view.compras;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.ProveedorService;
import com.utp.trabajo.model.entities.Proveedor;
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

public class ProveedoresTab extends org.jdesktop.swingx.JXPanel {

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

    private long rowsPerUpdate = 10;
    @Autowired
    private SecurityService securityService;

    @Autowired
    private ProveedorService proveedoresService;

    public ProveedoresTab() {
        initComponents();
        initTableProveedores();
        System.out.println("Proveedores tab - Nueva instancia!");
    }

    @PostConstruct
    private void init() {
        checkPermissions();
        updateTable(false); // mover hacia un listener que verifique que se ha abierto el jPanel
    }
    private void initTableProveedores() {
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
                updateTable(false);
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
        tablaProveedores.getColumnModel().getColumn(0).setPreferredWidth(100);
        setIdle();
        eliminarProveedorButton.setEnabled(false);
        editarProveedorButton.setEnabled(false);
        nuevoProveedorDialog.pack();
        nuevoProveedorDialog.setLocationRelativeTo(this);

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
            nuevoProveedorButton.setEnabled(false);
            guardarProveedorButton.setEnabled(false);
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
    private void addDataToTable(List<Proveedor> data) {
        data.forEach(cliente -> {
            Vector vec = new Vector();
            vec.add(cliente.getIdProveedor());
            vec.add(cliente.getNombre());
            vec.add(cliente.getRuc());
            vec.add(cliente.getDireccion());
            vec.add(cliente.getTelefono());
            vec.add(cliente.getTipoComercio());
            defaultTableModelProveedores.addRow(vec);
        });
    }

    private void updateTable(boolean reload) {
        tableInformationLabel.setVisible(false);
        if (!canRead) {
            setBusy("Sin permisos suficientes para leer datos.");
            return;
        }

        setBusy("Cargando...");
        reloadTableButton.setEnabled(false);
        loadMoreButton.setEnabled(false);
        retrievingData = true;
        int oldRowCount = defaultTableModelProveedores.getRowCount();
        if (reload) {
            defaultTableModelProveedores.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }

        SwingWorker worker2 = new SwingWorker<Long, Long>() {
            @Override
            protected Long doInBackground() throws Exception {
                return proveedoresService.contarProveedores();
            }

            @Override
            protected void done() {
                try {
                    Long cantidadProveedoresDatabase = get();
                    int cantidadProveedoresTabla = defaultTableModelProveedores.getRowCount();
                    contadorProveedoresLabel.setText("Mostrando: " + cantidadProveedoresTabla + "/" + cantidadProveedoresDatabase);

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
                    Logger.getLogger(ProveedoresTab.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };
        SwingWorker obtenerProveedoresWorker = new SwingWorker<List<Proveedor>, List<Proveedor>>() {
            @Override
            protected List<Proveedor> doInBackground() throws Exception {
                // set lastId and configurable rowsPerUpdate if reloading just reload all data
                if (reload) {
                    return proveedoresService.streamProveedores(lastId, (long) oldRowCount);
                }
                return proveedoresService.streamProveedores(lastId, rowsPerUpdate);
            }

            @Override
            protected void done() {
                try {
                    addDataToTable(get());
                    int lastRow = 0;
                    int rowCount = defaultTableModelProveedores.getRowCount();
                    if (rowCount != 0) {
                        lastRow = rowCount - 1;
                    }
                    var id = defaultTableModelProveedores.getValueAt(lastRow, 0);
                    lastId = Long.parseLong(id.toString());

                }
                catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(ProveedoresTab.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    tableInformationLabel.setVisible(true);
                    //Logger.getLogger(ProveedoresTab.class.getName()).log(Level.SEVERE, null, ex);
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
        obtenerProveedoresWorker.execute();

    }

    private List<Long> getIdFromSelectedRows() { // refactor! DONE!
        List<Long> idProveedores = new ArrayList<>();
        for (int i : selectionModel.getSelectedIndices()) { //rows 
            i = tablaProveedores.convertRowIndexToModel(i);
            // ↑ IMPORTANTISIMO, en caso de que la tabla esté ordenada por alguna columna, esto devolvera siempre la fila seleccionada.
            idProveedores.add((Long) defaultTableModelProveedores.getValueAt(i, 0));
        }
        return idProveedores;
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
        nuevoProveedorButton = new javax.swing.JButton();
        editarProveedorButton = new javax.swing.JButton();
        eliminarProveedorButton = new javax.swing.JButton();
        reloadTableButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        contadorProveedoresLabel = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        tablaProveedores = new org.jdesktop.swingx.JXTable();

        nuevoProveedorDialog.setResizable(false);
        nuevoProveedorDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                nuevoProveedorDialogWindowClosing(evt);
            }
        });

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
            .addComponent(jSeparator1)
            .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nuevoProveedorLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rucLabel)
                            .addComponent(tipoComercioLabel)
                            .addComponent(nombresLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(nombresField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(rucField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tipoComercioField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(direccionLabel)
                            .addComponent(telefonoLabel))
                        .addGap(18, 18, 18)
                        .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(telefonoField, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                            .addComponent(direccionField)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoProveedorDialogLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(guardarProveedorButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelarCreacionProveedorButton)))
                .addContainerGap())
        );
        nuevoProveedorDialogLayout.setVerticalGroup(
            nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(nuevoProveedorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                        .addComponent(direccionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nombresLabel)
                            .addComponent(telefonoLabel)
                            .addComponent(telefonoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tipoComercioLabel))
                    .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                        .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rucField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rucLabel)
                            .addComponent(direccionLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nombresField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tipoComercioField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(nuevoProveedorDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoProveedorDialogLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
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

        jLayeredPane1.setLayer(tableInformationLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 604, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(tableInformationLabel)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 287, Short.MAX_VALUE)
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

        contadorProveedoresLabel.setText("Cargando...");

        tablaProveedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaProveedores.setColumnControlVisible(true);
        scrollPane.setViewportView(tablaProveedores);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 336, Short.MAX_VALUE)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(contadorProveedoresLabel)
                        .addGap(27, 27, 27))
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevoProveedorButton)
                    .addComponent(editarProveedorButton)
                    .addComponent(eliminarProveedorButton)
                    .addComponent(contadorProveedoresLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE)
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
        List<Long> idProveedoresSeleccionado = getIdFromSelectedRows();
        if (idProveedoresSeleccionado.size() == 1) {
            Proveedor proveedorSeleccionado = null;
            SwingWorker editarProveedorWorker = new SwingWorker<Proveedor, Proveedor>() {
                @Override
                protected Proveedor doInBackground() throws Exception {
                    return proveedoresService.encontrarProveedorPorId(idProveedoresSeleccionado.get(0));
                }

                @Override
                protected void done() {
                    try {
                        get();
                    }
                    catch (InterruptedException ex) {}
                    catch (ExecutionException ex) {
                        try {
                            throw ex.getCause();
                        } catch (NotEnoughPermissionsException e) {
                            OptionPaneService.errorMessage(nuevoProveedorDialog, "No dispone de permisos suficientes para poder crear un nuevo proveedor.", "Sin permisos.");
                        } catch (Throwable imp) {
                            System.out.println("impossible!: \n");
                            imp.printStackTrace();
                            System.out.println("impossible end!: \n");
                        }
                    }
                }
                
            };
        }
    }//GEN-LAST:event_editarProveedorButtonActionPerformed

    private void eliminarProveedorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarProveedorButtonActionPerformed
        // TODO add your handling code here:
        List<Long> selectedProveedoresId = getIdFromSelectedRows();

        int selectedOption = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar " + (selectedProveedoresId.size() == 1 ? "1 cliente?" : (selectedProveedoresId.size() + " proveedores?")),
                selectedProveedoresId.size() == 1 ? "Eliminar un proveedor" : "Eliminar varios porveedores",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (selectedOption == JOptionPane.YES_OPTION) {
            List<Proveedor> proveedoresEliminados = new ArrayList<>();
            try {
                proveedoresEliminados = proveedoresService.eliminarProveedor(selectedProveedoresId); //implementar swingworker
            }
            catch (NotEnoughPermissionsException ex) {
                // just pass?
            }

            if (!proveedoresEliminados.isEmpty()) {
                updateTable(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Ocurrió un error al eliminar " + (selectedProveedoresId.size() == 1 ? "el proveedor seleccionado." : "los proveedores seleccionados."),
                        "¡Error!", JOptionPane.ERROR_MESSAGE);
            }
        } else if (selectedOption == JOptionPane.NO_OPTION) {
            //do nothing
        }
    }//GEN-LAST:event_eliminarProveedorButtonActionPerformed

    private void reloadTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadTableButtonActionPerformed
        // TODO add your handling code here:
        updateTable(true);
    }//GEN-LAST:event_reloadTableButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        // TODO add your handling code here:
        updateTable(false);
    }//GEN-LAST:event_loadMoreButtonActionPerformed
    private void clearNuevoProveedorWindow() {
        nombresField.setText("");
        rucField.setText("");
        telefonoField.setText("");
        direccionField.setText("");
        tipoComercioField.setText("");
    }
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

        if (tipoComercioField.getText().isBlank()) {
            tipoComercioField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (error) {
            return;
        } else {
            setBusy("Guardando proveedor...");
            p.setRuc(String.valueOf(ruc));
            p.setNombre(nombresField.getText());
            p.setDireccion(direccionField.getText());
            p.setTipoComercio(tipoComercioField.getText());
            p.setTelefono(String.valueOf(telefono));
            SwingWorker nuevoProveedorWorker = new SwingWorker<Proveedor, Proveedor>() {
                @Override
                protected Proveedor doInBackground() throws Exception {
                    return proveedoresService.nuevoProveedor(p); 
                }

                @Override
                protected void done() {
                    try {
                        get(); //maybe check if it was correctly added?
                        setIdle();
                    }
                    catch (InterruptedException ex) {}
                    catch (ExecutionException ex) {
                        try {
                            throw ex.getCause();
                        } catch (NotEnoughPermissionsException e) {
                            OptionPaneService.errorMessage(nuevoProveedorDialog, "No dispone de permisos suficientes para poder crear un nuevo cliente.", "Sin permisos.");
                            return;
                        } catch (Throwable imp) {
                            System.out.println("impossible!: \n");
                            imp.printStackTrace();
                            System.out.println("impossible end!: \n");
                        }
                    }
                }
            };
            nuevoProveedorWorker.execute();
            clearNuevoProveedorWindow();
        }
        nuevoProveedorDialog.setVisible(false);
    }//GEN-LAST:event_guardarProveedorButtonActionPerformed

    private void rucFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rucFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rucFieldActionPerformed
    private void cancelarCreacionProveedor() {
        boolean isBlank = true;

        if (!nombresField.getText().isBlank()) {
            isBlank = false;
        }

        if (!rucField.getText().isBlank()) {
            isBlank = false;
        }

        if (!telefonoField.getText().isBlank()) {
            isBlank = false;
        }

        if (!direccionField.getText().isBlank()) {
            isBlank = false;
        }

        if (!tipoComercioField.getText().isBlank()) {
            isBlank = false;
        }
        
        if (isBlank) {
            nuevoProveedorDialog.setVisible(false);
        }else {
            int ans = OptionPaneService.questionMessage(nuevoProveedorDialog, "¿Desea salir sin guardar los cambios?", "Cambios sin guardar");
            if (ans == JOptionPane.YES_OPTION) {
                nuevoProveedorDialog.setVisible(false);
                clearNuevoProveedorWindow();
            }
        }
    }
    private void cancelarCreacionProveedorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarCreacionProveedorButtonActionPerformed
        // TODO add your handling code here:
        nuevoProveedorDialog.setVisible(false);
    }//GEN-LAST:event_cancelarCreacionProveedorButtonActionPerformed

    private void nuevoProveedorDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_nuevoProveedorDialogWindowClosing
        // TODO add your handling code here:
        cancelarCreacionProveedor();
    }//GEN-LAST:event_nuevoProveedorDialogWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarCreacionProveedorButton;
    private javax.swing.JLabel contadorProveedoresLabel;
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
