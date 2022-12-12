package com.utp.trabajo.gui.view.compras;

/*import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.entities.Compra;
import com.utp.trabajo.services.ComprasService;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;*/
import javax.swing.table.DefaultTableModel;
//import org.springframework.beans.factory.annotation.Autowired;

public class EstadoCompra extends org.jdesktop.swingx.JXPanel {
public static DefaultTableModel modelo3;
    public EstadoCompra() {
        initComponents();
         modelo3 = new DefaultTableModel();
         modelo3.addColumn("ID estado de la compra");
         modelo3.addColumn("Estado");
        estadoCompraTable1.setModel(modelo3);
    }
    public void nuevaTabla(){
        modelo3=new DefaultTableModel();
        estadoCompraTable1.setModel(modelo3);
    }
    

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        estadoCompraTable1 = new org.jdesktop.swingx.JXTable();
        tableInformationLabel = new javax.swing.JLabel();
        loadMoreButton = new javax.swing.JButton();
        reloadButton = new javax.swing.JButton();
        contadorEstadoCompraLabel = new javax.swing.JLabel();

        estadoCompraTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(estadoCompraTable1);

        tableInformationLabel.setText("Sin Datos.");

        jLayeredPane1.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(tableInformationLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap(329, Short.MAX_VALUE)
                .addComponent(tableInformationLabel)
                .addGap(337, 337, 337))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap(163, Short.MAX_VALUE)
                .addComponent(tableInformationLabel)
                .addGap(154, 154, 154))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        loadMoreButton.setText("CARGAR MÁS");
        loadMoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMoreButtonActionPerformed(evt);
            }
        });

        reloadButton.setText("RECARGAR");
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });

        contadorEstadoCompraLabel.setText("...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(reloadButton)
                        .addGap(18, 18, 18)
                        .addComponent(loadMoreButton)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(contadorEstadoCompraLabel)
                .addGap(34, 34, 34))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contadorEstadoCompraLabel)
                .addGap(2, 2, 2)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reloadButton)
                    .addComponent(loadMoreButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        // TODO add your handling code here:
        //updateTable(true);
    }//GEN-LAST:event_reloadButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        // TODO add your handling code here:
        //updateTable(false);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel contadorEstadoCompraLabel;
    private org.jdesktop.swingx.JXTable estadoCompraTable1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JButton reloadButton;
    private javax.swing.JLabel tableInformationLabel;
    // End of variables declaration//GEN-END:variables
}
