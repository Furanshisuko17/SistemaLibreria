package com.utp.trabajo.gui.view.compras;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.entities.Compra;
import com.utp.trabajo.model.entities.MetodoPago;
import com.utp.trabajo.services.ComprasService;
import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.util.DateTableCellRenderer;
import java.awt.event.AdjustmentEvent;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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

public class ListaComprasTab extends org.jdesktop.swingx.JXPanel {

    DefaultTableModel defaultTableModelCompras = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Long.class;
                case 1:
                    return Date.class;
                case 2:
                    return String.class;
                case 3:
                    return BigInteger.class;
                case 4:
                    return String.class;
                case 5:
                    return BigInteger.class;
                default:
                    return String.class;
            }
        }
    };
    String[] columnNames = {"ID", "Fecha de compra", "Transporte", "Descuento", "Metodo de pago", "Precio total"};

    ListSelectionModel selectionModel;
    private boolean retrievingData = false;
    private boolean canRead = true;
    private boolean canEdit = true;
    private boolean canDelete = true;
    private boolean canCreate = true;

    private long lastId = 0;

    private long limit = 100;
    @Autowired
    private SecurityService securityService;

    @Autowired
    private ComprasService comprasService;

    public ListaComprasTab() {
        initComponents();
        initTable();

        nuevaCompraDialog.pack();
        nuevaCompraDialog.setLocationRelativeTo(this);
        System.out.println("Compras tab - Nueva instancia!");
    }

    @PostConstruct
    public void init() {
        checkPermissions();
        retrieveData(false);
    }
    
    private void initTable() {
        defaultTableModelCompras.setColumnIdentifiers(columnNames);
        tablaCompras.setModel(defaultTableModelCompras);
        tablaCompras.getColumnModel().getColumn(1).setCellRenderer(new DateTableCellRenderer(true));
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

        selectionModel = tablaCompras.getSelectionModel();
        selectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            if (!canDelete || !canEdit) {
                return;
            }
            if (!selectionModel.isSelectionEmpty()) {
                eliminarCompraButton.setEnabled(true);
            } else {
                eliminarCompraButton.setEnabled(false);
            }

            if (selectionModel.getSelectedItemsCount() == 1) {
                editarCompraButton.setEnabled(true);
            } else {
                editarCompraButton.setEnabled(false);
            }

        });

        setIdle();
        eliminarCompraButton.setEnabled(false);
        editarCompraButton.setEnabled(false);
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
            reloadButton.setEnabled(false);
        }
        if (!permissions.contains("create")) {
            canCreate = false;
            nuevaCompraButton.setEnabled(false);
            //guardarProveedorButton.setEnabled(false);
        }
        if (!permissions.contains("delete")) {
            canDelete = false;
            eliminarCompraButton.setEnabled(false);
        }
        if (!permissions.contains("edit")) {
            canEdit = false;
            editarCompraButton.setEnabled(false);
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
        reloadButton.setEnabled(false);
        nuevaCompraButton.setEnabled(false);
        retrievingData = true;
        loadMoreButton.setEnabled(false);
        if (reload) {
            defaultTableModelCompras.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }
        SwingWorker worker = new SwingWorker<List<Compra>, List<Compra>>() {
            @Override
            protected List<Compra> doInBackground() throws Exception {
                return comprasService.streamCompras(lastId, limit);// set lastId and configurable limit
            }

            @Override
            protected void done() {
                try {
                    var compras = get();
                    for (Compra compra : compras) {
                        Object[] values = new Object[6];
                        values[0] = compra.getIdCompra();
                        values[1] = compra.getFechaCompra();
                        values[2] = compra.getTransporte();
                        values[3] = compra.getDescuento();
                        values[4] = compra.getMetodoPago().getMetodoPago();
                        values[5] = compra.getPrecioTotal();
                        defaultTableModelCompras.addRow(values);
                    }
                    int lastRow = 0;
                    int rowCount = defaultTableModelCompras.getRowCount();
                    if (rowCount != 0) {
                        lastRow = rowCount - 1;
                    }
                    var id = defaultTableModelCompras.getValueAt(lastRow, 0);
                    lastId = Long.parseLong(id.toString());
                } catch (InterruptedException | ExecutionException ex) {
                    setIdle();
                    Logger.getLogger(ListaComprasTab.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    tableInformationLabel.setVisible(true);
                }
                setIdle();
                reloadButton.setEnabled(true);
                nuevaCompraButton.setEnabled(true);
                loadMoreButton.setEnabled(true);
                retrievingData = false;
                retrievingData = false;
            }
        };
        worker.execute();
    }

    private List<Compra> getSelectedRows() {
        List<Compra> compras = new ArrayList<>();
        for (int i : selectionModel.getSelectedIndices()) { //rows 
            //System.out.println(i);
            i = tablaCompras.convertRowIndexToModel(i); //IMPORTANTISIMO, en caso de que la tabla esté ordenada por alguna columna, esto devolvera siempre la fila seleccionada.
            Compra c = new Compra();
            c.setIdCompra((Long) defaultTableModelCompras.getValueAt(i, 0));
            c.setFechaCompra((Timestamp) defaultTableModelCompras.getValueAt(i, 1));
            c.setTransporte((String) defaultTableModelCompras.getValueAt(i, 2));
            c.setDescuento((BigInteger) defaultTableModelCompras.getValueAt(i, 3));
            c.setMetodoPago((MetodoPago) defaultTableModelCompras.getValueAt(i, 4));
            c.setPrecioTotal((BigInteger) defaultTableModelCompras.getValueAt(i, 5));
            compras.add(c);
        }
        return compras;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nuevaCompraDialog = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        IdCompraField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        fechaCompraField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        precioTotalField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        metodoPagoField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        descuentoField = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        cancelarNuevaCompraButton = new javax.swing.JButton();
        guardarButton = new javax.swing.JButton();
        verDetallesDialog = new javax.swing.JDialog();
        detalleCompraLabel = new javax.swing.JLabel();
        detalleCompraScrollPane = new javax.swing.JScrollPane();
        detalleCompraTable = new org.jdesktop.swingx.JXTable();
        verEstadoDialog = new javax.swing.JDialog();
        estadoCompraLabel = new javax.swing.JLabel();
        estadoCompraScrollPane = new javax.swing.JScrollPane();
        estadoCompraTable = new org.jdesktop.swingx.JXTable();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        tableInformationLabel = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        tablaCompras = new org.jdesktop.swingx.JXTable();
        nuevaCompraButton = new javax.swing.JButton();
        editarCompraButton = new javax.swing.JButton();
        eliminarCompraButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();
        reloadButton = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        DetallesCompraButton = new javax.swing.JButton();
        VerEstadoCompraButton = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Crear nueva compra");

        jLabel2.setText("IdCompra:");

        jLabel3.setText("Fecha de compra:");

        jLabel4.setText("Precio Total:");

        jLabel5.setText("Método de pago:");

        jLabel6.setText("Descuento:");

        cancelarNuevaCompraButton.setText("Cancelar");

        guardarButton.setText("Guardar");
        guardarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout nuevaCompraDialogLayout = new javax.swing.GroupLayout(nuevaCompraDialog.getContentPane());
        nuevaCompraDialog.getContentPane().setLayout(nuevaCompraDialogLayout);
        nuevaCompraDialogLayout.setHorizontalGroup(
            nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(nuevaCompraDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevaCompraDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(guardarButton)
                        .addGap(18, 18, 18)
                        .addComponent(cancelarNuevaCompraButton))
                    .addGroup(nuevaCompraDialogLayout.createSequentialGroup()
                        .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(nuevaCompraDialogLayout.createSequentialGroup()
                                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel6)
                                    .addGroup(nuevaCompraDialogLayout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel2)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(IdCompraField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(descuentoField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fechaCompraField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addGap(18, 18, 18)
                                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(precioTotalField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(metodoPagoField, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel1))
                        .addGap(0, 9, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        nuevaCompraDialogLayout.setVerticalGroup(
            nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nuevaCompraDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IdCompraField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(precioTotalField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(fechaCompraField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(metodoPagoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(descuentoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarNuevaCompraButton)
                    .addComponent(guardarButton))
                .addContainerGap())
        );

        detalleCompraLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        detalleCompraLabel.setText("DETALLES DE LAS COMPRAS");

        detalleCompraTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        detalleCompraScrollPane.setViewportView(detalleCompraTable);

        javax.swing.GroupLayout verDetallesDialogLayout = new javax.swing.GroupLayout(verDetallesDialog.getContentPane());
        verDetallesDialog.getContentPane().setLayout(verDetallesDialogLayout);
        verDetallesDialogLayout.setHorizontalGroup(
            verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(detalleCompraScrollPane)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, verDetallesDialogLayout.createSequentialGroup()
                .addContainerGap(393, Short.MAX_VALUE)
                .addComponent(detalleCompraLabel)
                .addGap(335, 335, 335))
        );
        verDetallesDialogLayout.setVerticalGroup(
            verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(verDetallesDialogLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(detalleCompraLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(detalleCompraScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                .addContainerGap())
        );

        estadoCompraLabel.setText("ESTADO DE LA COMPRA");

        estadoCompraTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        estadoCompraScrollPane.setViewportView(estadoCompraTable);

        javax.swing.GroupLayout verEstadoDialogLayout = new javax.swing.GroupLayout(verEstadoDialog.getContentPane());
        verEstadoDialog.getContentPane().setLayout(verEstadoDialogLayout);
        verEstadoDialogLayout.setHorizontalGroup(
            verEstadoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(estadoCompraScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 998, Short.MAX_VALUE)
            .addGroup(verEstadoDialogLayout.createSequentialGroup()
                .addGap(398, 398, 398)
                .addComponent(estadoCompraLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        verEstadoDialogLayout.setVerticalGroup(
            verEstadoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(verEstadoDialogLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(estadoCompraLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(estadoCompraScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE))
        );

        tableInformationLabel.setText("Sin datos.");

        tablaCompras.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaCompras.setColumnControlVisible(true);
        scrollPane.setViewportView(tablaCompras);

        jLayeredPane1.setLayer(tableInformationLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(scrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 778, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(tableInformationLabel)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 349, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addGap(0, 213, Short.MAX_VALUE)
                    .addComponent(tableInformationLabel)
                    .addGap(0, 213, Short.MAX_VALUE)))
        );

        nuevaCompraButton.setText("Nuevo");
        nuevaCompraButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevaCompraButtonActionPerformed(evt);
            }
        });

        editarCompraButton.setText("Editar");
        editarCompraButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarCompraButtonActionPerformed(evt);
            }
        });

        eliminarCompraButton.setText("Eliminar");
        eliminarCompraButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarCompraButtonActionPerformed(evt);
            }
        });

        loadMoreButton.setText("Cargar más");
        loadMoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMoreButtonActionPerformed(evt);
            }
        });

        reloadButton.setText("Recargar");
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });

        busyLabel.setBusy(true);
        busyLabel.setPreferredSize(new java.awt.Dimension(22, 22));

        DetallesCompraButton.setText("VER DETALLES");
        DetallesCompraButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DetallesCompraButtonActionPerformed(evt);
            }
        });

        VerEstadoCompraButton.setText("VER ESTADO DE PRODUCTOS");
        VerEstadoCompraButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VerEstadoCompraButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(loadMoreButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(reloadButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nuevaCompraButton)
                        .addGap(12, 12, 12)
                        .addComponent(editarCompraButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(eliminarCompraButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 224, Short.MAX_VALUE)
                        .addComponent(DetallesCompraButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(VerEstadoCompraButton)
                        .addGap(12, 12, 12)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevaCompraButton)
                    .addComponent(editarCompraButton)
                    .addComponent(eliminarCompraButton)
                    .addComponent(DetallesCompraButton)
                    .addComponent(VerEstadoCompraButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(reloadButton)
                        .addComponent(loadMoreButton))
                    .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void nuevaCompraButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevaCompraButtonActionPerformed
        // TODO add your handling code here:
        nuevaCompraDialog.setVisible(true);
    }//GEN-LAST:event_nuevaCompraButtonActionPerformed

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        // TODO add your handling code here:
        IdCompraField.putClientProperty("JComponent.outline", "");
        fechaCompraField.putClientProperty("JComponent.outline", "");
        descuentoField.putClientProperty("JComponent.outline", "");
        precioTotalField.putClientProperty("JComponent.outline", "");
        metodoPagoField.putClientProperty("JComponent.outline", "");
        //TODO: reemplazar razon social por un combobox
        Compra c = new Compra();

        boolean error = false;

        if (IdCompraField.getText().isBlank()) {
            IdCompraField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (fechaCompraField.getText().isBlank()) {
            fechaCompraField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (descuentoField.getText().isBlank()) {
            descuentoField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (precioTotalField.getText().isBlank()) {
            precioTotalField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (metodoPagoField.getText().isBlank()) {
            metodoPagoField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (error) {
            return;
        }
        nuevaCompraDialog.setVisible(false);
    }//GEN-LAST:event_guardarButtonActionPerformed

    private void editarCompraButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarCompraButtonActionPerformed
        // TODO add your handling code here:
        List<Compra> comprasSeleccionado = getSelectedRows();
        if (comprasSeleccionado.size() == 1) {
            Compra compraSeleccionado = comprasSeleccionado.get(0);
        }
    }//GEN-LAST:event_editarCompraButtonActionPerformed

    private void eliminarCompraButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarCompraButtonActionPerformed
        // TODO add your handling code here:
        List<Long> selectedComprasId = new ArrayList();
        for (Compra compra : getSelectedRows()) {
            selectedComprasId.add(compra.getIdCompra());
        }
        try {
            List<Compra> comprasEliminados = comprasService.eliminarCompra(selectedComprasId);
        } catch (NotEnoughPermissionsException ex) {
            Logger.getLogger(ListaComprasTab.class.getName()).log(Level.SEVERE, null, ex);
        }

        retrieveData(true);
    }//GEN-LAST:event_eliminarCompraButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        // TODO add your handling code here:
        retrieveData(false);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        // TODO add your handling code here:
        retrieveData(true);
    }//GEN-LAST:event_reloadButtonActionPerformed

    private void DetallesCompraButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DetallesCompraButtonActionPerformed
        // TODO add your handling code here:
        verDetallesDialog.setVisible(true);
    }//GEN-LAST:event_DetallesCompraButtonActionPerformed

    private void VerEstadoCompraButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VerEstadoCompraButtonActionPerformed
        // TODO add your handling code here:
        verEstadoDialog.setVisible(true);
    }//GEN-LAST:event_VerEstadoCompraButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DetallesCompraButton;
    private javax.swing.JTextField IdCompraField;
    private javax.swing.JButton VerEstadoCompraButton;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarNuevaCompraButton;
    private javax.swing.JTextField descuentoField;
    private javax.swing.JLabel detalleCompraLabel;
    private javax.swing.JScrollPane detalleCompraScrollPane;
    private org.jdesktop.swingx.JXTable detalleCompraTable;
    private javax.swing.JButton editarCompraButton;
    private javax.swing.JButton eliminarCompraButton;
    private javax.swing.JLabel estadoCompraLabel;
    private javax.swing.JScrollPane estadoCompraScrollPane;
    private org.jdesktop.swingx.JXTable estadoCompraTable;
    private javax.swing.JTextField fechaCompraField;
    private javax.swing.JButton guardarButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JTextField metodoPagoField;
    private javax.swing.JButton nuevaCompraButton;
    private javax.swing.JDialog nuevaCompraDialog;
    private javax.swing.JTextField precioTotalField;
    private javax.swing.JButton reloadButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaCompras;
    private javax.swing.JLabel tableInformationLabel;
    private javax.swing.JDialog verDetallesDialog;
    private javax.swing.JDialog verEstadoDialog;
    // End of variables declaration//GEN-END:variables
}
