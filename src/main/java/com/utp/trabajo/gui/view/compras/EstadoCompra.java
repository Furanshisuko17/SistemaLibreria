package com.utp.trabajo.gui.view.compras;

import javax.swing.table.DefaultTableModel;

public class EstadoCompra extends org.jdesktop.swingx.JXPanel {
public static DefaultTableModel modelo3;
    public EstadoCompra() {
        initComponents();
         modelo3.addColumn("ID estado de la compra");
        modelo3.addColumn("Estado");
        estadoCompraTable1.setModel(modelo3);
        
       //ABRIMOS LA OTRA TABLA
        ListaComprasTab estadoCompraTable2=new ListaComprasTab();
       estadoCompraTable2.setVisible(true);
        
    }
    public void nuevaTabla(){
        modelo3=new DefaultTableModel();
        estadoCompraTable1.setModel(modelo3);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        estadoCompraTable1 = new org.jdesktop.swingx.JXTable();
        jLabel1 = new javax.swing.JLabel();

        estadoCompraTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(estadoCompraTable1);

        jLabel1.setText("Estado de la compra");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(144, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(140, 140, 140))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXTable estadoCompraTable1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
