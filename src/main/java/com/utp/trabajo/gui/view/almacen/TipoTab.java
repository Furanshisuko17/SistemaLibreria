package com.utp.trabajo.gui.view.almacen;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.entities.Marca;
import com.utp.trabajo.model.entities.TipoProducto;
import com.utp.trabajo.services.TipoProductoService;
import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.util.OptionPaneService;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
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
import javax.swing.event.ListSelectionListener;
import org.springframework.beans.factory.annotation.Autowired;

public class TipoTab extends org.jdesktop.swingx.JXPanel {

    DefaultTableModel defaultTableModelTipoProducto = new DefaultTableModel() {
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
    String[] columnNames = {"ID", "Tipo de Producto"};

    ListSelectionModel selectionModel;
    
    private TipoProducto selectedTipo = null;

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
            if (selectionModel.getSelectedItemsCount() == 1) {
                editarTipoButton.setEnabled(true);
            } else {
                editarTipoButton.setEnabled(false);
            }

        });
        setIdle();
        editarTipoButton.setEnabled(false);
        nuevoTipoDialog.pack();
        nuevoTipoDialog.setLocationRelativeTo(this);
        editarTipoDialog.pack();
        editarTipoDialog.setLocationRelativeTo(this);
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
        int oldRowCount = defaultTableModelTipoProducto.getRowCount();
        if (reload) {
            defaultTableModelTipoProducto.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }
        new SwingWorker<List<TipoProducto>, List<TipoProducto>>() {
            @Override
            protected List<TipoProducto> doInBackground() throws Exception {
                if (reload) {
                    return tipoproductoService.streamTipoProducto(lastId, (long) oldRowCount);
                }
                return tipoproductoService.streamTipoProducto(lastId, limit); 
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
        }.execute();
    }

    private long getSelectedId() {
        List<Long> ids = new ArrayList<>();
        for (int i : selectionModel.getSelectedIndices()) { //rows 
            //System.out.println(i);
            i = tablaTipoTab.convertRowIndexToModel(i); //IMPORTANTISIMO, en caso de que la tabla esté ordenada por alguna columna, esto devolvera siempre la fila seleccionada.
            ids.add((Long) defaultTableModelTipoProducto.getValueAt(i, 0));
        }
        return ids.get(0);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nuevoTipoDialog = new javax.swing.JDialog();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        nuevoTipoLabel = new javax.swing.JLabel();
        guardarTipoButton = new javax.swing.JButton();
        cancelarTipoButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        tipoField = new javax.swing.JTextField();
        editarTipoDialog = new javax.swing.JDialog();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        editarTipoLabel = new javax.swing.JLabel();
        guardarEdicionTipoButton = new javax.swing.JButton();
        cancelarEdicionTipoButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        tipoFieldEditar = new javax.swing.JTextField();
        scrollPane = new javax.swing.JScrollPane();
        tablaTipoTab = new org.jdesktop.swingx.JXTable();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        nuevoTipoButton = new javax.swing.JButton();
        editarTipoButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();
        reloadTableButton = new javax.swing.JButton();

        nuevoTipoDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        nuevoTipoDialog.setTitle("Nuevo tipo de producto");
        nuevoTipoDialog.setAlwaysOnTop(true);
        nuevoTipoDialog.setModal(true);
        nuevoTipoDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        nuevoTipoDialog.setResizable(false);
        nuevoTipoDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                nuevoTipoDialogWindowClosing(evt);
            }
        });

        nuevoTipoLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nuevoTipoLabel.setText("Nuevo tipo de producto");

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
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(nuevoTipoDialogLayout.createSequentialGroup()
                .addGroup(nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoTipoDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(nuevoTipoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(nuevoTipoDialogLayout.createSequentialGroup()
                        .addGap(106, 106, 106)
                        .addComponent(guardarTipoButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelarTipoButton))
                    .addGroup(nuevoTipoDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tipoField, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        nuevoTipoDialogLayout.setVerticalGroup(
            nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nuevoTipoDialogLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(nuevoTipoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tipoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarTipoButton)
                    .addComponent(guardarTipoButton))
                .addGap(6, 6, 6))
        );

        editarTipoDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        editarTipoDialog.setTitle("Nuevo tipo de producto");
        editarTipoDialog.setAlwaysOnTop(true);
        editarTipoDialog.setModal(true);
        editarTipoDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        editarTipoDialog.setResizable(false);
        editarTipoDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                editarTipoDialogWindowClosing(evt);
            }
        });

        editarTipoLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        editarTipoLabel.setText("Editar tipo de producto #00");

        guardarEdicionTipoButton.setText("Guardar");
        guardarEdicionTipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarEdicionTipoButtonActionPerformed(evt);
            }
        });

        cancelarEdicionTipoButton.setText("Cancelar");
        cancelarEdicionTipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarEdicionTipoButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("Tipo de producto:");

        javax.swing.GroupLayout editarTipoDialogLayout = new javax.swing.GroupLayout(editarTipoDialog.getContentPane());
        editarTipoDialog.getContentPane().setLayout(editarTipoDialogLayout);
        editarTipoDialogLayout.setHorizontalGroup(
            editarTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(editarTipoDialogLayout.createSequentialGroup()
                .addGroup(editarTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editarTipoDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(guardarEdicionTipoButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelarEdicionTipoButton))
                    .addGroup(editarTipoDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(editarTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(editarTipoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                            .addGroup(editarTipoDialogLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tipoFieldEditar)))))
                .addContainerGap())
        );
        editarTipoDialogLayout.setVerticalGroup(
            editarTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editarTipoDialogLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(editarTipoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(editarTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tipoFieldEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(editarTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarEdicionTipoButton)
                    .addComponent(guardarEdicionTipoButton))
                .addGap(6, 6, 6))
        );

        setPreferredSize(new java.awt.Dimension(626, 419));

        tablaTipoTab.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaTipoTab.setEditable(false);
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
        editarTipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarTipoButtonActionPerformed(evt);
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
                .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
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
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevoTipoButton)
                    .addComponent(editarTipoButton))
                .addGap(6, 6, 6)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(busyLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(loadMoreButton)
                        .addComponent(reloadTableButton)))
                .addGap(6, 6, 6))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void nuevoTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoTipoButtonActionPerformed
        nuevoTipoDialog.setVisible(true);
    }//GEN-LAST:event_nuevoTipoButtonActionPerformed

    private void tipoFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tipoFieldActionPerformed

    private void cancelarTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarTipoButtonActionPerformed
        verifyEmptyNuevoTipo();
    }//GEN-LAST:event_cancelarTipoButtonActionPerformed

    private void guardarTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarTipoButtonActionPerformed
        tipoField.putClientProperty("JComponent.outline", "");

        TipoProducto tp = new TipoProducto();
        boolean error = false;

        if (tipoField.getText().isBlank()) {
            tipoField.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        if (error) {
            return;
        } else {
            setBusy("Guardando nuevo tipo de producto...");
            tp.setTipo(tipoField.getText());
            
            new SwingWorker<TipoProducto, TipoProducto>(){
                @Override
                protected TipoProducto doInBackground() throws Exception {
                    return tipoproductoService.nuevoTipoProducto(tp);
                }

                @Override
                protected void done() {
                    try {
                        get();
                        nuevoTipoDialog.setVisible(false);
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
    }//GEN-LAST:event_guardarTipoButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        retrieveData(false);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    private void reloadTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadTableButtonActionPerformed
        retrieveData(true);
    }//GEN-LAST:event_reloadTableButtonActionPerformed

    private void guardarEdicionTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarEdicionTipoButtonActionPerformed
        tipoFieldEditar.putClientProperty("JComponent.outline", "");
        
        TipoProducto tp = selectedTipo;
        boolean error = false;
        
        if (tipoFieldEditar.getText().isBlank()) {
            tipoFieldEditar.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        if (tipoFieldEditar.getText().equals(selectedTipo.getTipo())) {
            editarTipoDialog.setVisible(false);
            return;
        }

        if (error) {
            return;
        } else {
            setBusy("Actualizando tipo de producto...");
            tp.setTipo(tipoFieldEditar.getText());
            
            new SwingWorker<TipoProducto, TipoProducto>(){
                @Override
                protected TipoProducto doInBackground() throws Exception {
                    return tipoproductoService.actualizarTipoProducto(tp);
                }

                @Override
                protected void done() {
                    try {
                        get();
                        editarTipoDialog.setVisible(false);
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
    }//GEN-LAST:event_guardarEdicionTipoButtonActionPerformed

    private void cancelarEdicionTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarEdicionTipoButtonActionPerformed
        verifyUnchangedEditarTipo();
    }//GEN-LAST:event_cancelarEdicionTipoButtonActionPerformed

    private void editarTipoDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_editarTipoDialogWindowClosing
        verifyUnchangedEditarTipo();
    }//GEN-LAST:event_editarTipoDialogWindowClosing

    private void nuevoTipoDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_nuevoTipoDialogWindowClosing
        verifyEmptyNuevoTipo();
    }//GEN-LAST:event_nuevoTipoDialogWindowClosing

    private void editarTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarTipoButtonActionPerformed
        final long selectedId = getSelectedId();
        setBusy("Cargando tipo de producto #" + selectedId);
        
        new SwingWorker<TipoProducto, TipoProducto>() {
            @Override
            protected TipoProducto doInBackground() throws Exception {
                return tipoproductoService.encontrarTipoProductoPorId(selectedId);
            }

            @Override
            protected void done() {
                try {
                    TipoProducto tp = get();
                    editarTipoLabel.setText("Editar tipo de producto #" + tp.getIdTipoProducto());
                    tipoFieldEditar.setText(tp.getTipo());
                    selectedTipo = tp;
                    setIdle();
                    editarTipoDialog.setVisible(true);
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
    }//GEN-LAST:event_editarTipoButtonActionPerformed

    private void verifyEmptyNuevoTipo() {
        if(tipoField.getText().isEmpty()){
            nuevoTipoDialog.setVisible(false);
        } else {
            int answ = OptionPaneService.questionMessage(nuevoTipoDialog, "¿Desea cerrar sin guardar?", "Cerrar sin guardar");
            if (answ == JOptionPane.YES_OPTION) {
                nuevoTipoDialog.setVisible(false);
            }
        }
    }
    
    private void verifyUnchangedEditarTipo() {
        if(tipoFieldEditar.getText().equals(selectedTipo.getTipo())){
            editarTipoDialog.setVisible(false);
        } else {
            int answ = OptionPaneService.questionMessage(editarTipoDialog, "¿Desea cerrar sin guardar?", "Cerrar sin guardar");
            if (answ == JOptionPane.YES_OPTION) {
                editarTipoDialog.setVisible(false);
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarEdicionTipoButton;
    private javax.swing.JButton cancelarTipoButton;
    private javax.swing.JButton editarTipoButton;
    private javax.swing.JDialog editarTipoDialog;
    private javax.swing.JLabel editarTipoLabel;
    private javax.swing.JButton guardarEdicionTipoButton;
    private javax.swing.JButton guardarTipoButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JButton nuevoTipoButton;
    private javax.swing.JDialog nuevoTipoDialog;
    private javax.swing.JLabel nuevoTipoLabel;
    private javax.swing.JButton reloadTableButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaTipoTab;
    private javax.swing.JTextField tipoField;
    private javax.swing.JTextField tipoFieldEditar;
    // End of variables declaration//GEN-END:variables
}
