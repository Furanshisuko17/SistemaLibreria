package com.utp.trabajo.gui.view.ventas;

import com.utp.trabajo.model.entities.Cliente;
import com.utp.trabajo.services.ClienteService;
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
import org.springframework.beans.factory.annotation.Autowired;

public class ClientesTab extends org.jdesktop.swingx.JXPanel {
	
	DefaultTableModel defaultTableModelClientes = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch(columnIndex) {
                case 0:
                    return Long.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                case 3:
                    return Integer.class;
                case 4:
                    return Integer.class;
                case 5:
                    return String.class;
                case 6:
                    return Integer.class;
                default:
                    return String.class;
            }
        }        
    };
	String[] columnNames = {"ID", "Nombre", "Dirección", "DNI", "Teléfono", "Razón social", "N° compras"}; //TODO: set minimum and default sizes for each column
       
    private boolean canRead = true;
    
    private long lastId = 0;
    
    private long limit = 100;
    
	@Autowired
	private SecurityService securityService;
    
    @Autowired
    private ClienteService clienteService;
	
	public ClientesTab() {
		initComponents();
        defaultTableModelClientes.setColumnIdentifiers(columnNames);
        tablaClientes.setModel(defaultTableModelClientes);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                int maxValue = scrollPane.getVerticalScrollBar().getMaximum() - scrollPane.getVerticalScrollBar().getVisibleAmount();
                int currentValue = scrollPane.getVerticalScrollBar().getValue();
                float fraction = (float) currentValue / (float) maxValue;
                if (fraction > 0.999f) {
                    retrieveData(false);
                    System.out.println("Scroll bar is near the bottom");
                }
            }
        });
        setIdle();
        System.out.println("Clientes tab - Nueva instancia!");
	}
	
	@PostConstruct
	private void init() {
        checkPermissions();
        retrieveData(false); // mover hacia un listener que verifique que se ha abierto el jPanel
	}
    
    private void checkPermissions() {
        List<String> permissions = securityService.getPermissions();
        //read, create, edit, delete
        if(!permissions.contains("read")) {
            canRead = false;
            loadMoreButton.setEnabled(false);
            reloadTableButton.setEnabled(false);
        }
        if(!permissions.contains("write")) {
            nuevoClienteButton.setEnabled(false);
            editarClienteButton.setEnabled(false);
            eliminarClienteButton.setEnabled(false);
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
        if(!canRead) {
            setBusy("Sin permisos suficientes para leer datos.");
            return;
        }
        
        setBusy("Cargando...");
        if(reload) {
            defaultTableModelClientes.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }
        
        SwingWorker worker = new SwingWorker<List<Cliente>, List<Cliente>>()  {
            @Override
            protected List<Cliente> doInBackground() throws Exception {
                return clienteService.streamClientes(lastId, limit); // set lastId and configurable limit
            }

            @Override
            protected void done() {
                try {
                    var clientes = get();
                    for (Cliente cliente : clientes) {
                        Object[] values = new Object[7];
                        values[0] = cliente.getIdCliente();
                        values[1] = cliente.getNombre();
                        values[2] = cliente.getDireccion();
                        values[3] = cliente.getIdentificacion();
                        values[4] = cliente.getTelefono();
                        values[5] = cliente.getRazonSocial();
                        values[6] = cliente.getNumeroCompras();
                        defaultTableModelClientes.addRow(values);
                    }
                    int lastRow = 0 ;
                    int rowCount = defaultTableModelClientes.getRowCount();
                    if(rowCount != 0) {
                        lastRow = rowCount - 1;
                    }
                    var id = defaultTableModelClientes.getValueAt(lastRow, 0);
                    lastId = Long.parseLong(id.toString());
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(ClientesTab.class.getName()).log(Level.SEVERE, null, ex);
                }
                setIdle();
            }
        };
        worker.execute();
    }
    
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        scrollPane = new javax.swing.JScrollPane();
        tablaClientes = new org.jdesktop.swingx.JXTable();
        reloadTableButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();
        editarClienteButton = new javax.swing.JButton();
        nuevoClienteButton = new javax.swing.JButton();
        eliminarClienteButton = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));

        tablaClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        scrollPane.setViewportView(tablaClientes);

        jLayeredPane1.setLayer(scrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
        );

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

        editarClienteButton.setText("Editar");

        nuevoClienteButton.setText("Nuevo");
        nuevoClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoClienteButtonActionPerformed(evt);
            }
        });

        eliminarClienteButton.setText("Eliminar");
        eliminarClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarClienteButtonActionPerformed(evt);
            }
        });

        busyLabel.setBusy(true);
        busyLabel.setPreferredSize(new java.awt.Dimension(22, 22));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nuevoClienteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editarClienteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(eliminarClienteButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(busyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(loadMoreButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(reloadTableButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevoClienteButton)
                    .addComponent(editarClienteButton)
                    .addComponent(eliminarClienteButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadMoreButton)
                    .addComponent(reloadTableButton)
                    .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void reloadTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadTableButtonActionPerformed
        retrieveData(true);
    }//GEN-LAST:event_reloadTableButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        retrieveData(false);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    private void nuevoClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoClienteButtonActionPerformed
        
    }//GEN-LAST:event_nuevoClienteButtonActionPerformed

    private void eliminarClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarClienteButtonActionPerformed
        
    }//GEN-LAST:event_eliminarClienteButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton editarClienteButton;
    private javax.swing.JButton eliminarClienteButton;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JButton nuevoClienteButton;
    private javax.swing.JButton reloadTableButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaClientes;
    // End of variables declaration//GEN-END:variables
}
