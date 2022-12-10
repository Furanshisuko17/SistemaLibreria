package com.utp.trabajo.gui.view.ventas;

import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.swing.table.DefaultTableModel;

public class NuevaVentaWindow extends org.jdesktop.swingx.JXPanel {
    
    private DefaultTableModel nuevaVentaTableModel = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Long.class;
                case 1:
                    return String.class;
                case 2:
                    return Integer.class;
                case 3: 
                    return String.class;
                case 4:
                    return BigDecimal.class;
                case 5:
                    return BigDecimal.class;
                default:
                    return String.class;
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 2;
        }
    };

    private String columnHeaders[] = {"ID", "Producto", "Cantidad", " ", "Precio", "Total"};
    
    public NuevaVentaWindow() {
        initComponents();
        initTable();
        setIdle();
    }
    
    @PostConstruct
    private void init() {
//        checkPermissions();
//        updateTable(false); // mover hacia un listener que verifique que se ha abierto el jPanel
    }
    
    private void initTable(){
        nuevaVentaTableModel.setColumnIdentifiers(columnHeaders);
        detalleVentaTable.setModel(nuevaVentaTableModel);
        
        detalleVentaTable.getColumn(0).setPreferredWidth(50);
        detalleVentaTable.getColumn(0).setMaxWidth(50);
        detalleVentaTable.getColumn(1).setPreferredWidth(300);
        detalleVentaTable.getColumn(3).setMaxWidth(10);
        detalleVentaTable.getColumn(3).setMinWidth(10);
        
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
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        priceLabel = new javax.swing.JLabel();
        productoLabel = new javax.swing.JLabel();
        cantidadLabel = new javax.swing.JLabel();
        cantidadField = new javax.swing.JTextField();
        precioField = new javax.swing.JTextField();
        precioLabel = new javax.swing.JLabel();
        totalField = new javax.swing.JTextField();
        totalLabel = new javax.swing.JLabel();
        productoSearchField = new org.jdesktop.swingx.JXSearchField();
        agregarProductoButton = new javax.swing.JButton();
        seleccionarClienteButton = new javax.swing.JButton();
        panel = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        detalleVentaTable = new org.jdesktop.swingx.JXTable();
        eliminarProducto = new javax.swing.JButton();
        cantidadProductos = new javax.swing.JLabel();
        cantidadField1 = new javax.swing.JTextField();
        cantidadLabel1 = new javax.swing.JLabel();
        dniLabel = new javax.swing.JLabel();
        dniField = new javax.swing.JTextField();
        numeroDeCompraLabel = new javax.swing.JLabel();
        numeroDeComprasField = new javax.swing.JTextField();
        cancelarVenta = new javax.swing.JButton();
        guardarVenta = new javax.swing.JButton();
        stockRestanteField = new javax.swing.JTextField();
        stockRestanteLabel = new javax.swing.JLabel();

        busyLabel.setBusy(true);
        busyLabel.setPreferredSize(new java.awt.Dimension(22, 22));

        priceLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        priceLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        priceLabel.setText("TOTAL :");

        productoLabel.setText("Producto");

        cantidadLabel.setText("Cantidad");

        precioLabel.setText("Precio");

        totalLabel.setText("Total");

        productoSearchField.setToolTipText("Buscar producto");
        productoSearchField.setLayoutStyle(org.jdesktop.swingx.JXSearchField.LayoutStyle.MAC);
        productoSearchField.setPrompt("Buscar producto");

        agregarProductoButton.setText("Agregar");
        agregarProductoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarProductoButtonActionPerformed(evt);
            }
        });

        seleccionarClienteButton.setText("Seleccionar cliente");
        seleccionarClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarClienteButtonActionPerformed(evt);
            }
        });

        detalleVentaTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        detalleVentaTable.setEditable(false);
        scrollPane.setViewportView(detalleVentaTable);

        eliminarProducto.setText("Eliminar");
        eliminarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarProductoActionPerformed(evt);
            }
        });

        cantidadProductos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cantidadProductos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cantidadProductos.setText("2 ITEMS - TOTAL: 20");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane)
            .addGroup(panelLayout.createSequentialGroup()
                .addComponent(eliminarProducto)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(cantidadProductos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                .addComponent(cantidadProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(eliminarProducto))
        );

        cantidadField1.setEditable(false);

        cantidadLabel1.setText("Cliente");

        dniLabel.setText("DNI");

        dniField.setEditable(false);

        numeroDeCompraLabel.setText("Nº de compras");

        numeroDeComprasField.setEditable(false);

        cancelarVenta.setText("Cancelar");
        cancelarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarVentaActionPerformed(evt);
            }
        });

        guardarVenta.setText("Guardar ");
        guardarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarVentaActionPerformed(evt);
            }
        });

        stockRestanteField.setEditable(false);

        stockRestanteLabel.setText("Stock");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(productoSearchField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(productoLabel)
                                .addGap(248, 248, 248)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stockRestanteLabel)
                            .addComponent(stockRestanteField, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cantidadField, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cantidadLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(precioLabel)
                            .addComponent(precioField, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(totalLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(totalField, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(agregarProductoButton))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cantidadField1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cantidadLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dniField, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dniLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(numeroDeCompraLabel)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(numeroDeComprasField, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(seleccionarClienteButton))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(priceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(guardarVenta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelarVenta)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cantidadLabel1)
                    .addComponent(dniLabel)
                    .addComponent(numeroDeCompraLabel))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cantidadField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(seleccionarClienteButton)
                    .addComponent(dniField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numeroDeComprasField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(productoLabel)
                    .addComponent(cantidadLabel)
                    .addComponent(precioLabel)
                    .addComponent(totalLabel)
                    .addComponent(stockRestanteLabel))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cantidadField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precioField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(productoSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(agregarProductoButton)
                    .addComponent(stockRestanteField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(busyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(priceLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarVenta)
                    .addComponent(guardarVenta))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void seleccionarClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarClienteButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_seleccionarClienteButtonActionPerformed

    private void agregarProductoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarProductoButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_agregarProductoButtonActionPerformed

    private void eliminarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarProductoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eliminarProductoActionPerformed

    private void guardarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarVentaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_guardarVentaActionPerformed

    private void cancelarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarVentaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cancelarVentaActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agregarProductoButton;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarVenta;
    private javax.swing.JTextField cantidadField;
    private javax.swing.JTextField cantidadField1;
    private javax.swing.JLabel cantidadLabel;
    private javax.swing.JLabel cantidadLabel1;
    private javax.swing.JLabel cantidadProductos;
    private org.jdesktop.swingx.JXTable detalleVentaTable;
    private javax.swing.JTextField dniField;
    private javax.swing.JLabel dniLabel;
    private javax.swing.JButton eliminarProducto;
    private javax.swing.JButton guardarVenta;
    private javax.swing.JLabel numeroDeCompraLabel;
    private javax.swing.JTextField numeroDeComprasField;
    private javax.swing.JPanel panel;
    private javax.swing.JTextField precioField;
    private javax.swing.JLabel precioLabel;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JLabel productoLabel;
    private org.jdesktop.swingx.JXSearchField productoSearchField;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JButton seleccionarClienteButton;
    private javax.swing.JTextField stockRestanteField;
    private javax.swing.JLabel stockRestanteLabel;
    private javax.swing.JTextField totalField;
    private javax.swing.JLabel totalLabel;
    // End of variables declaration//GEN-END:variables
}
