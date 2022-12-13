package com.utp.trabajo.gui.view.almacen;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.entities.Marca;
import com.utp.trabajo.services.MarcaService;
import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.util.OptionPaneService;
import java.awt.event.AdjustmentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;
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
    
    private Marca selectedMarca = null;

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

            if (selectionModel.getSelectedItemsCount() == 1) {
                editarMarcaButton.setEnabled(true);
            } else {
                editarMarcaButton.setEnabled(false);
            }

        });
        setIdle();
        editarMarcaButton.setEnabled(false);
        nuevaMarcaDialog.pack();
        nuevaMarcaDialog.setLocationRelativeTo(this);
        editarMarcaDialog.pack();
        editarMarcaDialog.setLocationRelativeTo(this);
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nuevaMarcaDialog = new javax.swing.JDialog();
        nuevaMarcaLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        cancelarCreacionClienteButton = new javax.swing.JButton();
        nombreMarcaLabel = new javax.swing.JLabel();
        nombreMarcaField = new javax.swing.JTextField();
        guardarMarcaButton = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        editarMarcaDialog = new javax.swing.JDialog();
        editarMarcaLabel = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        cancelarEdicionMarca = new javax.swing.JButton();
        nombreMarcaLabelEdit = new javax.swing.JLabel();
        nombreMarcaFieldEdit = new javax.swing.JTextField();
        guardarMarcaButtonEdit = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        scrollPane = new javax.swing.JScrollPane();
        tablaMarca = new org.jdesktop.swingx.JXTable();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        nuevaMarcaButton = new javax.swing.JButton();
        editarMarcaButton = new javax.swing.JButton();
        reloadTableButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();

        nuevaMarcaDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        nuevaMarcaDialog.setTitle("Nueva marca");
        nuevaMarcaDialog.setAlwaysOnTop(true);
        nuevaMarcaDialog.setModal(true);
        nuevaMarcaDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        nuevaMarcaDialog.setResizable(false);
        nuevaMarcaDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                nuevaMarcaDialogWindowClosing(evt);
            }
        });

        nuevaMarcaLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nuevaMarcaLabel.setText("Nueva marca");

        cancelarCreacionClienteButton.setText("Cancelar");
        cancelarCreacionClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarCreacionClienteButtonActionPerformed(evt);
            }
        });

        nombreMarcaLabel.setText("Nombre:");

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevaMarcaDialogLayout.createSequentialGroup()
                .addGroup(nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(nuevaMarcaDialogLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(guardarMarcaButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelarCreacionClienteButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, nuevaMarcaDialogLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(nuevaMarcaDialogLayout.createSequentialGroup()
                                .addComponent(nuevaMarcaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 41, Short.MAX_VALUE))
                            .addGroup(nuevaMarcaDialogLayout.createSequentialGroup()
                                .addComponent(nombreMarcaLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(nombreMarcaField)))))
                .addContainerGap())
            .addComponent(jSeparator5)
        );
        nuevaMarcaDialogLayout.setVerticalGroup(
            nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nuevaMarcaDialogLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(nuevaMarcaLabel)
                .addGap(6, 6, 6)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nombreMarcaLabel)
                    .addComponent(nombreMarcaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarCreacionClienteButton)
                    .addComponent(guardarMarcaButton))
                .addGap(6, 6, 6))
        );

        editarMarcaDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        editarMarcaDialog.setTitle("Editar marca");
        editarMarcaDialog.setAlwaysOnTop(true);
        editarMarcaDialog.setModal(true);
        editarMarcaDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        editarMarcaDialog.setResizable(false);
        editarMarcaDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                editarMarcaDialogWindowClosing(evt);
            }
        });

        editarMarcaLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        editarMarcaLabel.setText("Editar marca #00");

        cancelarEdicionMarca.setText("Cancelar");
        cancelarEdicionMarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarEdicionMarcaActionPerformed(evt);
            }
        });

        nombreMarcaLabelEdit.setText("Nombre:");

        guardarMarcaButtonEdit.setText("Guardar");
        guardarMarcaButtonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarMarcaButtonEditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout editarMarcaDialogLayout = new javax.swing.GroupLayout(editarMarcaDialog.getContentPane());
        editarMarcaDialog.getContentPane().setLayout(editarMarcaDialogLayout);
        editarMarcaDialogLayout.setHorizontalGroup(
            editarMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator3)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editarMarcaDialogLayout.createSequentialGroup()
                .addGroup(editarMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(editarMarcaDialogLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(guardarMarcaButtonEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelarEdicionMarca))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, editarMarcaDialogLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(editarMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(editarMarcaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(editarMarcaDialogLayout.createSequentialGroup()
                                .addComponent(nombreMarcaLabelEdit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(nombreMarcaFieldEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
            .addComponent(jSeparator4)
        );
        editarMarcaDialogLayout.setVerticalGroup(
            editarMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editarMarcaDialogLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(editarMarcaLabel)
                .addGap(6, 6, 6)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(editarMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nombreMarcaLabelEdit)
                    .addComponent(nombreMarcaFieldEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(editarMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarEdicionMarca)
                    .addComponent(guardarMarcaButtonEdit))
                .addGap(6, 6, 6))
        );

        setPreferredSize(new java.awt.Dimension(626, 419));

        tablaMarca.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaMarca.setEditable(false);
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
        editarMarcaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarMarcaButtonActionPerformed(evt);
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
                        .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(loadMoreButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(reloadTableButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nuevaMarcaButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editarMarcaButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevaMarcaButton)
                    .addComponent(editarMarcaButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
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
        nuevaMarcaDialog.setVisible(true);
    }//GEN-LAST:event_nuevaMarcaButtonActionPerformed

    private void reloadTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadTableButtonActionPerformed
        retrieveData(true);
    }//GEN-LAST:event_reloadTableButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        retrieveData(false);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    private void verifyEmptyNuevaMarca() {
        if(nombreMarcaField.getText().isEmpty()){
            nuevaMarcaDialog.setVisible(false);
        } else {
            int answ = OptionPaneService.questionMessage(nuevaMarcaDialog, "¿Desea cerrar sin guardar?", "Cerrar sin guardar");
            if (answ == JOptionPane.YES_OPTION) {
                nuevaMarcaDialog.setVisible(false);
            }
        }
    }
    
    private void verifyUnchangedEditarMarca() {
        if(nombreMarcaFieldEdit.getText().equals(selectedMarca.getNombreMarca())){
            editarMarcaDialog.setVisible(false);
        } else {
            int answ = OptionPaneService.questionMessage(editarMarcaDialog, "¿Desea cerrar sin guardar?", "Cerrar sin guardar");
            if (answ == JOptionPane.YES_OPTION) {
                editarMarcaDialog.setVisible(false);
            }
        }
    }
    
    private void editarMarcaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarMarcaButtonActionPerformed
        List<Long> idMarcas = new ArrayList<>();
        for (int i : selectionModel.getSelectedIndices()) { //rows 
            i = tablaMarca.convertRowIndexToModel(i);
            idMarcas.add((Long) defaultTableModelMarca.getValueAt(i, 0));
        }
        final long selectedId = idMarcas.get(0);
        setBusy("Cargando marca #" + selectedId);
        
        new SwingWorker<Marca, Marca>() {
            @Override
            protected Marca doInBackground() throws Exception {
                return marcaService.encontrarMarcaPorId(selectedId);
            }

            @Override
            protected void done() {
                try {
                    Marca marca = get();
                    editarMarcaLabel.setText("Editar marca #" + marca.getIdMarca());
                    nombreMarcaFieldEdit.setText(marca.getNombreMarca());
                    selectedMarca = marca;
                    setIdle();
                    editarMarcaDialog.setVisible(true);
                } catch (InterruptedException ex) {
                } catch (ExecutionException ex) {
                    try {
                        throw ex.getCause();
                    } catch (NotEnoughPermissionsException e) {
                        Logger.getLogger(MarcaTab.class.getName()).log(Level.SEVERE, null, e);
                        return;
                    } catch (Throwable imp) {
                        System.out.println("impossible!: \n");
                        imp.printStackTrace();
                        System.out.println("impossible end!: \n");
                    }
                } finally {
                    setIdle();
                }
                
            }
            
        }.execute();
    }//GEN-LAST:event_editarMarcaButtonActionPerformed

    private void cancelarEdicionMarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarEdicionMarcaActionPerformed
        verifyUnchangedEditarMarca();
    }//GEN-LAST:event_cancelarEdicionMarcaActionPerformed

    private void guardarMarcaButtonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarMarcaButtonEditActionPerformed
        nombreMarcaFieldEdit.putClientProperty("JComponent.outline", "");
        
        Marca marca = selectedMarca;
        boolean error = false;
        
        if (nombreMarcaFieldEdit.getText().isBlank()) {
            nombreMarcaFieldEdit.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        if (nombreMarcaFieldEdit.getText().equals(selectedMarca.getNombreMarca())) {
            editarMarcaDialog.setVisible(false);
            return;
        }

        if (error) {
            return;
        } else {
            setBusy("Actualizando marca...");
            marca.setNombreMarca(nombreMarcaFieldEdit.getText());
            
            new SwingWorker<Marca, Marca>(){
                @Override
                protected Marca doInBackground() throws Exception {
                    return marcaService.actualizarMarca(marca);
                }

                @Override
                protected void done() {
                    try {
                        get();
                        editarMarcaDialog.setVisible(false);
                    } catch (InterruptedException ex) {
                    } catch (ExecutionException ex) {
                        try {
                            throw ex.getCause();
                        } catch (NotEnoughPermissionsException e) {
                            Logger.getLogger(MarcaTab.class.getName()).log(Level.SEVERE, null, e);
                            return;
                        } catch (Throwable imp) {
                            System.out.println("impossible!: \n");
                            imp.printStackTrace();
                            System.out.println("impossible end!: \n");
                        }
                    } finally {
                        setIdle();
                    }
                }
                
            }.execute();
        }
    }//GEN-LAST:event_guardarMarcaButtonEditActionPerformed

    private void editarMarcaDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_editarMarcaDialogWindowClosing
        verifyUnchangedEditarMarca();
    }//GEN-LAST:event_editarMarcaDialogWindowClosing

    private void nuevaMarcaDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_nuevaMarcaDialogWindowClosing
        verifyEmptyNuevaMarca();
    }//GEN-LAST:event_nuevaMarcaDialogWindowClosing

    private void guardarMarcaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarMarcaButtonActionPerformed
        nombreMarcaField.putClientProperty("JComponent.outline", "");

        Marca marca = new Marca();
        boolean error = false;

        if (nombreMarcaField.getText().isBlank()) {
            nombreMarcaField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (error) {
            return;
        } else {
            setBusy("Guardando nueva marca...");
            marca.setNombreMarca(nombreMarcaField.getText());

            new SwingWorker<Marca, Marca>(){
                @Override
                protected Marca doInBackground() throws Exception {
                    return marcaService.nuevaMarca(marca);
                }

                @Override
                protected void done() {
                    try {
                        get();
                        nuevaMarcaDialog.setVisible(false);
                    } catch (InterruptedException ex) {
                    } catch (ExecutionException ex) {
                        try {
                            throw ex.getCause();
                        } catch (NotEnoughPermissionsException e) {
                            Logger.getLogger(MarcaTab.class.getName()).log(Level.SEVERE, null, e);
                            return;
                        } catch (Throwable imp) {
                            System.out.println("impossible!: \n");
                            imp.printStackTrace();
                            System.out.println("impossible end!: \n");
                        }
                    } finally {
                        setIdle();
                    }
                }

            }.execute();
        }
    }//GEN-LAST:event_guardarMarcaButtonActionPerformed

    private void cancelarCreacionClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarCreacionClienteButtonActionPerformed
        verifyEmptyNuevaMarca();
    }//GEN-LAST:event_cancelarCreacionClienteButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarCreacionClienteButton;
    private javax.swing.JButton cancelarEdicionMarca;
    private javax.swing.JButton editarMarcaButton;
    private javax.swing.JDialog editarMarcaDialog;
    private javax.swing.JLabel editarMarcaLabel;
    private javax.swing.JButton guardarMarcaButton;
    private javax.swing.JButton guardarMarcaButtonEdit;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JTextField nombreMarcaField;
    private javax.swing.JTextField nombreMarcaFieldEdit;
    private javax.swing.JLabel nombreMarcaLabel;
    private javax.swing.JLabel nombreMarcaLabelEdit;
    private javax.swing.JButton nuevaMarcaButton;
    private javax.swing.JDialog nuevaMarcaDialog;
    private javax.swing.JLabel nuevaMarcaLabel;
    private javax.swing.JButton reloadTableButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaMarca;
    // End of variables declaration//GEN-END:variables
}
