package com.utp.trabajo.gui.view.productos;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.gui.view.clientes.ClientesTab;
import com.utp.trabajo.model.entities.Cliente;
import com.utp.trabajo.model.entities.Producto;
import com.utp.trabajo.services.ProductoService;
import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.util.DateTableCellRenderer;
import java.awt.event.AdjustmentEvent;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductoTab extends org.jdesktop.swingx.JXPanel {
    
    DefaultTableModel defaultTableModelProductos = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Long.class;
                case 6:
                    return Date.class;
                case 5:
                    return Long.class;
                default:
                    return String.class;
            }
        }

    };
    
    String[] columnNames = {"ID", "Nombre", "Descripción", "Marca", "Tipo de producto", "Stock", "Última compra"};
    
    ListSelectionModel selectionModel;
    
    private boolean canRead = true;
    private boolean canEdit = true;
    private boolean canDelete = true;
    private boolean canCreate = true;

    private boolean retrievingData = false;

    private long lastId = 0;

    private long rowsPerUpdate = 100;

    @Autowired 
    private ProductoService productoService;
    
    @Autowired
    private SecurityService securityService;

    public ProductoTab() {
        initComponents();
        initTable();
        
    }
    
    @PostConstruct
    private void init() {
        checkPermissions();
        updateTable(false); // mover hacia un listener que verifique que se ha abierto el jPanel
    }

    private void initTable() {
        defaultTableModelProductos.setColumnIdentifiers(columnNames);
        tableProductos.setModel(defaultTableModelProductos);
        tableProductos.getColumnModel().getColumn(0).setMinWidth(2);
        tableProductos.getColumnModel().getColumn(0).setMaxWidth(50);
        tableProductos.getColumnModel().getColumn(0).setPreferredWidth(50);
        
        tableProductos.getColumnModel().getColumn(6).setCellRenderer(new DateTableCellRenderer(true));
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
        selectionModel = tableProductos.getSelectionModel();
        selectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            if (!canDelete || !canEdit) {
                return;
            }
            if (!selectionModel.isSelectionEmpty()) {
                eliminarButton.setEnabled(true);
            } else {
                eliminarButton.setEnabled(false);
            }

            if (selectionModel.getSelectedItemsCount() == 1) {
                editarButton.setEnabled(true);
            } else {
                editarButton.setEnabled(false);
            }

        });
        
        setIdle();
        eliminarButton.setEnabled(false);
        editarButton.setEnabled(false);
//        nuevoClienteDialog.pack();
//        nuevoClienteDialog.setLocationRelativeTo(this);

        layeredPane.removeAll();
        layeredPane.setLayer(tableInformationLabel, javax.swing.JLayeredPane.DEFAULT_LAYER, 0);
        layeredPane.setLayer(scrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER, -1);
    }
    
    private void checkPermissions() {
        List<String> permissions = securityService.getPermissions();

        //read, create, edit, delete
        if (!permissions.contains("read")) {
            canRead = false;
            loadMoreButton.setEnabled(false);
            recargarButton.setEnabled(false);
        }
        if (!permissions.contains("create")) {
            canCreate = false;
            nuevoButton.setEnabled(false);
            //guardarClienteButton.setEnabled(false);
        }
        if (!permissions.contains("delete")) {
            canDelete = false;
            eliminarButton.setEnabled(false);
        }
        if (!permissions.contains("edit")) {
            canEdit = false;
            editarButton.setEnabled(false);
        }

    }
    
    private void addDataToTable(List<Producto> data) {
        data.forEach(producto -> {
            Vector vec = new Vector();
            vec.add(producto.getIdProducto());
            vec.add(producto.getNombre());
            vec.add(producto.getDescripcion());
            vec.add(producto.getMarca().getNombreMarca());
            vec.add(producto.getTipoProducto().getTipo());
            vec.add(producto.getAlmacen().getStock());
            vec.add(producto.getFechaUltimaVenta());
            defaultTableModelProductos.addRow(vec);
        });
    }
    
    private void updateTable(boolean reload) { //refactor!  DONE!
        tableInformationLabel.setVisible(false);
        if (!canRead) {
            setBusy("Sin permisos suficientes para leer datos.");
            return;
        }

        setBusy("Cargando...");
        recargarButton.setEnabled(false);
        loadMoreButton.setEnabled(false);
        retrievingData = true;
        int oldRowCount = defaultTableModelProductos.getRowCount();
        if (reload) {
            defaultTableModelProductos.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }

        SwingWorker counterWorker = new SwingWorker<Long, Long>() {
            @Override
            protected Long doInBackground() throws Exception {
                return productoService.contarProductos();
            }

            @Override
            protected void done() {
                try {
                    Long cantidadClientesDatabase = get();
                    int cantidadClientesTabla = defaultTableModelProductos.getRowCount();
                    contadorLabel.setText("Mostrando: " + cantidadClientesTabla + "/" + cantidadClientesDatabase);

                }
                catch (InterruptedException ex) {
                    try {
                        throw ex.getCause();
                    }
                    catch (NotEnoughPermissionsException e) {
                        contadorLabel.setText(e.getMessage());
                    }
                    catch (Throwable e) {
                        System.out.println("impossible :");
                        e.printStackTrace();
                        System.out.println("impossible end");
                        return;
                    }
                }
                catch (ExecutionException ex) {
                    Logger.getLogger(ClientesTab.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };
        SwingWorker obtenerProductosWorker = new SwingWorker<List<Producto>, List<Producto>>() {
            @Override
            protected List<Producto> doInBackground() throws Exception {
                // set lastId and configurable rowsPerUpdate if reloading just reload all data
                if (reload) {
                    return productoService.streamProductos(lastId, (long) oldRowCount);
                }
                return productoService.streamProductos(lastId, rowsPerUpdate);
            }

            @Override
            protected void done() {
                try {
                    addDataToTable(get());
                    int lastRow = 0;
                    int rowCount = defaultTableModelProductos.getRowCount();
                    if (rowCount != 0) {
                        lastRow = rowCount - 1;
                    }
                    var id = defaultTableModelProductos.getValueAt(lastRow, 0);
                    lastId = Long.parseLong(id.toString());

                }
                catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(ClientesTab.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    tableInformationLabel.setVisible(true);
                }
                finally {
                    setIdle();
                    recargarButton.setEnabled(true);
                    loadMoreButton.setEnabled(true);
                    retrievingData = false;
                }
                counterWorker.execute();
            }
        };
        obtenerProductosWorker.execute();

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

        nuevoButton = new javax.swing.JButton();
        editarButton = new javax.swing.JButton();
        eliminarButton = new javax.swing.JButton();
        recargarButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        layeredPane = new javax.swing.JLayeredPane();
        scrollPane = new javax.swing.JScrollPane();
        tableProductos = new org.jdesktop.swingx.JXTable();
        tableInformationLabel = new javax.swing.JLabel();
        contadorLabel = new javax.swing.JLabel();

        nuevoButton.setText("Nuevo");
        nuevoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoButtonActionPerformed(evt);
            }
        });

        editarButton.setText("Editar");
        editarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarButtonActionPerformed(evt);
            }
        });

        eliminarButton.setText("Eliminar");
        eliminarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarButtonActionPerformed(evt);
            }
        });

        recargarButton.setText("Recargar");
        recargarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recargarButtonActionPerformed(evt);
            }
        });

        loadMoreButton.setText("Cargar más entradas");
        loadMoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMoreButtonActionPerformed(evt);
            }
        });

        busyLabel.setBusy(true);
        busyLabel.setPreferredSize(new java.awt.Dimension(22, 22));

        tableProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableProductos.setColumnControlVisible(true);
        tableProductos.setEditable(false);
        scrollPane.setViewportView(tableProductos);

        tableInformationLabel.setText("jLabel1");

        layeredPane.setLayer(scrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        layeredPane.setLayer(tableInformationLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layeredPaneLayout = new javax.swing.GroupLayout(layeredPane);
        layeredPane.setLayout(layeredPaneLayout);
        layeredPaneLayout.setHorizontalGroup(
            layeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layeredPaneLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(scrollPane))
            .addGroup(layeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layeredPaneLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(tableInformationLabel)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layeredPaneLayout.setVerticalGroup(
            layeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
            .addGroup(layeredPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layeredPaneLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(tableInformationLabel)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        contadorLabel.setText("Cargando...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(layeredPane)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nuevoButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(editarButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(eliminarButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 375, Short.MAX_VALUE)
                        .addComponent(contadorLabel)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(busyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(loadMoreButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(recargarButton)
                        .addGap(6, 6, 6))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(nuevoButton)
                        .addComponent(editarButton)
                        .addComponent(eliminarButton))
                    .addComponent(contadorLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(layeredPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(recargarButton)
                        .addComponent(loadMoreButton))
                    .addComponent(busyLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void nuevoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoButtonActionPerformed
        
    }//GEN-LAST:event_nuevoButtonActionPerformed

    private void editarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarButtonActionPerformed
        
    }//GEN-LAST:event_editarButtonActionPerformed

    private void eliminarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarButtonActionPerformed
        
    }//GEN-LAST:event_eliminarButtonActionPerformed

    private void recargarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recargarButtonActionPerformed
        updateTable(true);
    }//GEN-LAST:event_recargarButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        updateTable(false);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JLabel contadorLabel;
    private javax.swing.JButton editarButton;
    private javax.swing.JButton eliminarButton;
    private javax.swing.JLayeredPane layeredPane;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JButton nuevoButton;
    private javax.swing.JButton recargarButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JLabel tableInformationLabel;
    private org.jdesktop.swingx.JXTable tableProductos;
    // End of variables declaration//GEN-END:variables
}
