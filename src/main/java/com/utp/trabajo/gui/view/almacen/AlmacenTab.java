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

public class AlmacenTab extends org.jdesktop.swingx.JXPanel {

    DefaultTableModel defaultTableModelAlmacen = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Long.class;
                case 2:
                    return Long.class;
                case 3:
                    return Integer.class;
                case 4:
                    return Integer.class;
                default:
                    return String.class;
            }
        }

    };
    String[] columnNames = {"ID", "Producto" , "Stock", "Stock inicial", "Stock mínimo", "Ubicación"};
    
    ListSelectionModel selectionModel;
    private boolean canRead = true;
    private boolean canEdit = true;
    private boolean canDelete = true;
    private boolean canCreate = true;

    private boolean retrievingData = false;

    private long lastId = 0;

    private long rowsPerUpdate = 10;
   
    private Almacen almacenSeleccionado = null;
    
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

            if (selectionModel.getSelectedItemsCount() == 1) {
                editarAlmacenButton.setEnabled(true);
            } else {
                editarAlmacenButton.setEnabled(false);
            }

        });
        tablaAlmacen.getColumnModel().getColumn(0).setPreferredWidth(100);
        setIdle();
        editarAlmacenButton.setEnabled(false);
        editarAlmacenDialog.pack();
        editarAlmacenDialog.setLocationRelativeTo(this);

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
            vec.add(almacen.getProducto().getNombre() + " " + almacen.getProducto().getMarca().getNombreMarca());
            vec.add(almacen.getStock());
            vec.add(almacen.getStockInicial());
            vec.add(almacen.getStockMinimo());
            vec.add(almacen.getEstanteria() + almacen.getColumna() + almacen.getFila());
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
        int oldRowCount = defaultTableModelAlmacen.getRowCount();
        if (reload) {
            defaultTableModelAlmacen .setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }
        new SwingWorker<List<Almacen>, List<Almacen>>() {
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
                }
                finally {
                    setIdle();
                    reloadTableButton.setEnabled(true);
                    loadMoreButton.setEnabled(true);
                    retrievingData = false;
                }
            }
        }.execute();

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
     private void clearNuevoAlmacenWindow() {
        productoField.setText("");
        marcaField.setText("");
        stockField.setText("");
        stockinicialField.setText("");
        stockminimoField.setText("");
        columnaField.setText("");
        estanteriaField.setText("");
        filaField.setText("");
    }
    private void cancelarEdicionAlmacen() {
        boolean isBlank = true;

        if (!stockField.getText().isBlank()) {
            isBlank = false;
        }

        if (!stockinicialField.getText().isBlank()) {
            isBlank = false;
        }

        if (!stockminimoField.getText().isBlank()) {
            isBlank = false;
        }

        if (!columnaField.getText().isBlank()) {
            isBlank = false;
        }

        if (!estanteriaField.getText().isBlank()) {
            isBlank = false;
        }
         if (!filaField.getText().isBlank()) {
            isBlank = false;
        }
        
        if (isBlank) {
            editarAlmacenDialog.setVisible(false);
        }else {
            int ans = OptionPaneService.questionMessage(editarAlmacenDialog, "¿Desea salir sin guardar los cambios?", "Cambios sin guardar");
            if (ans == JOptionPane.YES_OPTION) {
                editarAlmacenDialog.setVisible(false);
                clearNuevoAlmacenWindow();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editarAlmacenDialog = new javax.swing.JDialog();
        guardarAlmacenButton = new javax.swing.JButton();
        cancelarEdicionButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        editarAlmacenLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        stockField = new javax.swing.JTextField();
        stockinicialField = new javax.swing.JTextField();
        stockminimoField = new javax.swing.JTextField();
        columnaField = new javax.swing.JTextField();
        estanteriaField = new javax.swing.JTextField();
        filaField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        productoField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        marcaField = new javax.swing.JTextField();
        editarAlmacenButton = new javax.swing.JButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        scrollPane = new javax.swing.JScrollPane();
        tablaAlmacen = new org.jdesktop.swingx.JXTable();
        loadMoreButton = new javax.swing.JButton();
        reloadTableButton = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        tableInformationLabel = new javax.swing.JLabel();

        editarAlmacenDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        editarAlmacenDialog.setTitle("Editar almacén del producto");
        editarAlmacenDialog.setAlwaysOnTop(true);
        editarAlmacenDialog.setModal(true);
        editarAlmacenDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        editarAlmacenDialog.setResizable(false);
        editarAlmacenDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                editarAlmacenDialogWindowClosing(evt);
            }
        });

        guardarAlmacenButton.setText("Guardar");
        guardarAlmacenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarAlmacenButtonActionPerformed(evt);
            }
        });

        cancelarEdicionButton.setText("Cancelar");
        cancelarEdicionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarEdicionButtonActionPerformed(evt);
            }
        });

        editarAlmacenLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        editarAlmacenLabel.setText("Editar almacén del producto #00");

        jLabel2.setText("Stock:");

        jLabel3.setText("Stock inicial:");

        jLabel4.setText("Stock mínimo:");

        jLabel5.setText("Columna:");

        jLabel6.setText("Estanteria:");

        jLabel7.setText("Fila:");

        jLabel1.setText("Producto:");

        productoField.setEditable(false);

        jLabel8.setText("Marca:");

        marcaField.setEditable(false);

        javax.swing.GroupLayout editarAlmacenDialogLayout = new javax.swing.GroupLayout(editarAlmacenDialog.getContentPane());
        editarAlmacenDialog.getContentPane().setLayout(editarAlmacenDialogLayout);
        editarAlmacenDialogLayout.setHorizontalGroup(
            editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editarAlmacenDialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(guardarAlmacenButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cancelarEdicionButton)
                .addContainerGap())
            .addGroup(editarAlmacenDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(editarAlmacenDialogLayout.createSequentialGroup()
                        .addComponent(editarAlmacenLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(editarAlmacenDialogLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1))
                        .addGap(6, 6, 6)
                        .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(stockinicialField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(stockField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stockminimoField)
                            .addComponent(productoField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(editarAlmacenDialogLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editarAlmacenDialogLayout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(filaField, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap())
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editarAlmacenDialogLayout.createSequentialGroup()
                                        .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel5))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(columnaField)
                                            .addComponent(marcaField, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                                        .addGap(6, 6, 6))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editarAlmacenDialogLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(estanteriaField, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                                .addContainerGap())))))
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        editarAlmacenDialogLayout.setVerticalGroup(
            editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editarAlmacenDialogLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(editarAlmacenLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(productoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(marcaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(stockinicialField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(editarAlmacenDialogLayout.createSequentialGroup()
                        .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(columnaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(stockField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(filaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6)
                .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(editarAlmacenDialogLayout.createSequentialGroup()
                        .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(stockminimoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(guardarAlmacenButton)
                            .addComponent(cancelarEdicionButton))
                        .addGap(6, 6, 6))
                    .addGroup(editarAlmacenDialogLayout.createSequentialGroup()
                        .addGroup(editarAlmacenDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(estanteriaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        editarAlmacenButton.setText("Editar");
        editarAlmacenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarAlmacenButtonActionPerformed(evt);
            }
        });

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
            .addComponent(scrollPane)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
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
                        .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 208, Short.MAX_VALUE)
                        .addComponent(loadMoreButton)
                        .addGap(18, 18, 18)
                        .addComponent(reloadTableButton)
                        .addGap(16, 16, 16))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLayeredPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(editarAlmacenButton)
                        .addGap(0, 0, Short.MAX_VALUE))))
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
                .addComponent(editarAlmacenButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(loadMoreButton)
                        .addComponent(reloadTableButton))
                    .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
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

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        updateTable(true);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    private void editarAlmacenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarAlmacenButtonActionPerformed
        List<Long> idProductosSeleccionado = getIdFromSelectedRows();
         
        if (idProductosSeleccionado.size() == 1) {
            setBusy("Cargando producto #" + idProductosSeleccionado.get(0));
            new SwingWorker<Almacen, Almacen>() {
                @Override
                protected Almacen doInBackground() throws Exception {
                    return almacenService.encontrarAlmacenPorId(idProductosSeleccionado.get(0));
                }

                @Override
                protected void done() {
                    try {
                        Almacen a = get();
                        almacenSeleccionado = a;
                        editarAlmacenLabel.setText("Editar almacén del producto #" + a.getIdProducto());
                        productoField.setText(a.getProducto().getNombre());
                        marcaField.setText(a.getProducto().getMarca().getNombreMarca());
                        stockField.setText(a.getStock().toString());
                        stockinicialField.setText("" + a.getStockInicial());
                        stockminimoField.setText("" + a.getStockMinimo());
                        columnaField.setText("" + a.getColumna());
                        estanteriaField.setText(a.getEstanteria());
                        filaField.setText("" + a.getFila());
                        setIdle();
                        editarAlmacenDialog.setVisible(true);
                    }
                    catch (InterruptedException ex) {}
                    catch (ExecutionException ex) {
                        try {
                            throw ex.getCause();
                        } catch (NotEnoughPermissionsException e) {
                            OptionPaneService.errorMessage(editarAlmacenDialog, "No dispone de permisos suficientes para poder crear un nuevo producto en almacen.", "Sin permisos.");
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
    }//GEN-LAST:event_editarAlmacenButtonActionPerformed

    private void cancelarEdicionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarEdicionButtonActionPerformed
        cancelarEdicionAlmacen();
    }//GEN-LAST:event_cancelarEdicionButtonActionPerformed

    private void guardarAlmacenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarAlmacenButtonActionPerformed
        stockField.putClientProperty("JComponent.outline", "");
        stockinicialField.putClientProperty("JComponent.outline", "");
        stockminimoField.putClientProperty("JComponent.outline", "");
        columnaField.putClientProperty("JComponent.outline", "");
        estanteriaField.putClientProperty("JComponent.outline", "");
        filaField.putClientProperty("JComponent.outline", "");

        Almacen a = almacenSeleccionado;

        boolean error = false;

        if (stockField.getText().isBlank()) {
            stockField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (stockinicialField.getText().isBlank()) {
            stockinicialField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (stockminimoField.getText().isBlank()) {
            stockminimoField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (columnaField.getText().isBlank()) {
            columnaField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (estanteriaField.getText().isBlank()) {
            estanteriaField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (filaField.getText().isBlank()) {
            filaField.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Long.parseLong(stockField.getText());
        } catch (NumberFormatException e) {
            stockField.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Integer.parseInt(stockinicialField.getText());
        } catch (NumberFormatException e) {
            stockinicialField.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Integer.parseInt(stockminimoField.getText());
        } catch (NumberFormatException e) {
            stockminimoField.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Integer.parseInt(columnaField.getText());
        } catch (NumberFormatException e) {
            columnaField.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Integer.parseInt(filaField.getText());
        } catch (NumberFormatException e) {
            filaField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (error) {
            return;
        } else {
            setBusy("Actualizando almacén...");
            a.setStock(Long.parseLong(stockField.getText()));
            a.setStockInicial(Integer.parseInt(stockinicialField.getText()));
            a.setStockMinimo(Integer.parseInt(stockminimoField.getText()));
            a.setColumna(Integer.parseInt(columnaField.getText()));
            a.setEstanteria(estanteriaField.getText());
            a.setFila(Integer.parseInt(filaField.getText()));

            SwingWorker nuevoAlmacenWorker = new SwingWorker<Almacen, Almacen>() {
                @Override
                protected Almacen doInBackground() throws Exception {
                    return almacenService.nuevoAlmacen(a);
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
                            OptionPaneService.errorMessage(editarAlmacenDialog, "No dispone de permisos suficientes para poder crear un nuevo cliente.", "Sin permisos.");
                            return;
                        } catch (Throwable imp) {
                            System.out.println("impossible!: \n");
                            imp.printStackTrace();
                            System.out.println("impossible end!: \n");
                        }
                    }
                }
            };
            nuevoAlmacenWorker.execute();
            clearNuevoAlmacenWindow();
        }

        editarAlmacenDialog.setVisible(false);
    }//GEN-LAST:event_guardarAlmacenButtonActionPerformed

    private void editarAlmacenDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_editarAlmacenDialogWindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_editarAlmacenDialogWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarEdicionButton;
    private javax.swing.JTextField columnaField;
    private javax.swing.JButton editarAlmacenButton;
    private javax.swing.JDialog editarAlmacenDialog;
    private javax.swing.JLabel editarAlmacenLabel;
    private javax.swing.JTextField estanteriaField;
    private javax.swing.JTextField filaField;
    private javax.swing.JButton guardarAlmacenButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JTextField marcaField;
    private javax.swing.JTextField productoField;
    private javax.swing.JButton reloadTableButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField stockField;
    private javax.swing.JTextField stockinicialField;
    private javax.swing.JTextField stockminimoField;
    private org.jdesktop.swingx.JXTable tablaAlmacen;
    private javax.swing.JLabel tableInformationLabel;
    // End of variables declaration//GEN-END:variables
}
