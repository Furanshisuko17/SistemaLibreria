package com.utp.trabajo.gui.view.compras;

import javax.swing.table.DefaultTableModel;

public class DetalleCompra extends org.jdesktop.swingx.JXPanel {
    public static DefaultTableModel modelo2;
    public DetalleCompra() {
        initComponents();
        modelo2.addColumn("ID detalle de la compra");
        modelo2.addColumn("Cantidad");
        modelo2.addColumn("Fecha de llegada");
        modelo2.addColumn("Fecha de salida");
        modelo2.addColumn("Precio");
        detalleCompraTable1.setModel(modelo2);
        
       //ABRIMOS LA OTRA TABLA
        ListaComprasTab detalleCompraTable2=new ListaComprasTab();
       detalleCompraTable2.setVisible(true);
    }
    
    public void nuevaTabla(){
        modelo2=new DefaultTableModel();
        detalleCompraTable1.setModel(modelo2);
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        detalleCompraTable1 = new org.jdesktop.swingx.JXTable();
        jLabel1 = new javax.swing.JLabel();

        detalleCompraTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(detalleCompraTable1);

        jLabel1.setText("DETALLES DE LAS COMPRAS");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(218, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(209, 209, 209))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXTable detalleCompraTable1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
