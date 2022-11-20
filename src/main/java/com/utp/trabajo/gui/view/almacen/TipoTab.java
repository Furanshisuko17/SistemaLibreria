/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.utp.trabajo.gui.view.almacen;

import com.utp.trabajo.model.entities.TipoProducto;
import com.utp.trabajo.services.TipoProductoService;
import com.utp.trabajo.services.security.SecurityService;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.springframework.beans.factory.annotation.Autowired;
public class TipoTab extends javax.swing.JPanel {

        DefaultTableModel defaultTableModelTipoProducto = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Long.class;
                case 1:
                    return String.class;
                default:
                    return String.class;
            }
        }
    };
    String[] columnNames = {"ID", "Tipo de Producto"}; 
    
     ListSelectionModel selectionModel;
    
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
            
        }
        if (!permissions.contains("delete")) {
            canDelete = false;
            eliminarTipoButton.setEnabled(false);
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

        
        if (reload) {
            defaultTableModelTipoProducto.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }

        SwingWorker worker = new SwingWorker<List<TipoProducto>, List<TipoProducto>>() {
            @Override
            protected List<TipoProducto> doInBackground() throws Exception {
                return tipoproductoService.streamTipoProducto(lastId, limit); // set lastId and configurable limit
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
		
            }
        };
        worker.execute();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        tablaTipoTab = new org.jdesktop.swingx.JXTable();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        nuevoTipoButton = new javax.swing.JButton();
        editarTipoButton = new javax.swing.JButton();
        eliminarTipoButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();
        reloadTableButton = new javax.swing.JButton();

        tablaTipoTab.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        scrollPane.setViewportView(tablaTipoTab);

        busyLabel.setBusy(true);
        busyLabel.setPreferredSize(new java.awt.Dimension(22, 22));

        nuevoTipoButton.setText("Nuevo");

        editarTipoButton.setText("Editar");

        eliminarTipoButton.setText("Eliminar");
        eliminarTipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarTipoButtonActionPerformed(evt);
            }
        });

        loadMoreButton.setText("Cargar más entradas");

        reloadTableButton.setText("Recargar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scrollPane))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(nuevoTipoButton)
                        .addGap(28, 28, 28)
                        .addComponent(editarTipoButton)
                        .addGap(18, 18, 18)
                        .addComponent(eliminarTipoButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 154, Short.MAX_VALUE)
                        .addComponent(loadMoreButton)
                        .addGap(18, 18, 18)
                        .addComponent(reloadTableButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevoTipoButton)
                    .addComponent(editarTipoButton)
                    .addComponent(eliminarTipoButton))
                .addGap(18, 18, 18)
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(loadMoreButton)
                        .addComponent(reloadTableButton)))
                .addGap(41, 41, 41))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void eliminarTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarTipoButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eliminarTipoButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton editarTipoButton;
    private javax.swing.JButton eliminarTipoButton;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JButton nuevoTipoButton;
    private javax.swing.JButton reloadTableButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaTipoTab;
    // End of variables declaration//GEN-END:variables
}
