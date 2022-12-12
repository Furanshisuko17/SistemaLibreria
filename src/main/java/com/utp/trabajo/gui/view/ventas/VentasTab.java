package com.utp.trabajo.gui.view.ventas;

import com.utp.trabajo.model.entities.Venta;
import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.util.DateTableCellRenderer;
import com.utp.trabajo.services.util.IconService;
import com.utp.trabajo.services.util.OptionPaneService;
import java.awt.Frame;
import java.awt.event.AdjustmentEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXFrame;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class VentasTab extends org.jdesktop.swingx.JXPanel {

    private DefaultTableModel defaultTableModelVentas = new DefaultTableModel() {
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

    private String[] columnNames = {"ID", "Fecha de emisión", "Cliente", "Comprobante", "Cantidad de ítems", "Total"};
    
    ListSelectionModel selectionModel;
    
    private boolean canRead = true;
    private boolean canEdit = true;
    private boolean canDelete = true;
    private boolean canCreate = true;

    private boolean retrievingData = false;

    private long lastId = 0;

    private long rowsPerUpdate = 100;

    @Autowired
    private SecurityService securityService;
    
    @Autowired
    private IconService iconService;

    public VentasTab() {
        initComponents();
        initTable();
    }

    @PostConstruct
    public void init() {
        updateTable(false);
    }
    
    private void initTable() {
        defaultTableModelVentas.setColumnIdentifiers(columnNames);
        tablaVentas.setModel(defaultTableModelVentas);
        tablaVentas.getColumnModel().getColumn(0).setPreferredWidth(50);
        
        tablaVentas.getColumnModel().getColumn(1).setCellRenderer(new DateTableCellRenderer(true));
        
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
        selectionModel = tablaVentas.getSelectionModel();
        selectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            if (selectionModel.getSelectedItemsCount() == 1) {
                detallesVenta.setEnabled(true);
            } else {
                detallesVenta.setEnabled(false);
            }

        });
        setIdle();
        detallesVenta.setEnabled(false);

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
        if (!permissions.contains("create")) {
            canCreate = false;
            nuevoButton.setEnabled(false);
//            guardarButton.setEnabled(false);
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

    private void updateTable(boolean reload) {
        if (!canRead) {
            setBusy("Sin permisos suficientes para leer datos.");
            return;
        }

        setBusy("Cargando...");
        if (reload) {
            defaultTableModelVentas.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }
        SwingWorker worker = new SwingWorker<List<Venta>, List<Venta>>() {
            @Override
            protected List<Venta> doInBackground() throws Exception {
                return new ArrayList<Venta>();// set lastId and configurable limit
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException ex) {
                    Logger.getLogger(VentasTab.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(VentasTab.class.getName()).log(Level.SEVERE, null, ex);
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
        tableInformationLabel = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        tablaVentas = new org.jdesktop.swingx.JXTable();
        reloadTableButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();
        nuevoButton = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        contadorVentasLabel = new javax.swing.JLabel();
        detallesVenta = new javax.swing.JButton();

        tableInformationLabel.setText("Sin datos.");

        tablaVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaVentas.setColumnControlVisible(true);
        tablaVentas.setEditable(false);
        scrollPane.setViewportView(tablaVentas);

        jLayeredPane1.setLayer(tableInformationLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(scrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(tableInformationLabel)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(tableInformationLabel)
                    .addGap(0, 0, Short.MAX_VALUE)))
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

        nuevoButton.setText("Nuevo");
        nuevoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoButtonActionPerformed(evt);
            }
        });

        busyLabel.setBusy(true);
        busyLabel.setPreferredSize(new java.awt.Dimension(22, 22));

        contadorVentasLabel.setText("Cargando...");

        detallesVenta.setText("Detalles");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nuevoButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(detallesVenta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(contadorVentasLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(busyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(loadMoreButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(reloadTableButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(contadorVentasLabel))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nuevoButton)
                            .addComponent(detallesVenta))))
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
        
    }//GEN-LAST:event_reloadTableButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    private void nuevoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoButtonActionPerformed
        JXFrame nuevaVentaDialog = new JXFrame("Nueva venta", false);
        nuevaVentaDialog.setIconImage(iconService.iconoNuevaVenta.getImage());
        NuevaVentaWindow nuevaVenta = getNuevaVentaTabInstance();
        nuevaVentaDialog.getContentPane().add(nuevaVenta);
        nuevaVentaDialog.pack();
        
        nuevaVentaDialog.setLocationRelativeTo(this);
        nuevaVentaDialog.setVisible(true);
        nuevaVentaDialog.setDefaultCloseOperation(JXFrame.DO_NOTHING_ON_CLOSE);
        nuevaVentaDialog.setMinimumSize(new java.awt.Dimension(800, 520));
        nuevaVentaDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                if(!nuevaVenta.getProductos().isEmpty()) {
                    int answ = OptionPaneService.questionMessage(nuevaVentaDialog, "¿Desea cerrar sin guardar?", "Cerrar sin guardar");
                    if(answ == JOptionPane.YES_OPTION) {
                        nuevaVentaDialog.setVisible(false);
                        nuevaVentaDialog.dispose();
                    } 
                } else {
                    nuevaVentaDialog.setVisible(false);
                    nuevaVentaDialog.dispose();
                }
            } 
        });
    }//GEN-LAST:event_nuevoButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JLabel contadorVentasLabel;
    private javax.swing.JButton detallesVenta;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JButton nuevoButton;
    private javax.swing.JButton reloadTableButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaVentas;
    private javax.swing.JLabel tableInformationLabel;
    // End of variables declaration//GEN-END:variables

    @Autowired
    private ObjectFactory<NuevaVentaWindow> nuevaVentaWindowObjectFactory;

    public NuevaVentaWindow getNuevaVentaTabInstance() {
        return nuevaVentaWindowObjectFactory.getObject();
    }
    
}

