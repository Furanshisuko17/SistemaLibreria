package com.utp.trabajo.gui.view.ventas;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.gui.view.clientes.ClientesTab;
import com.utp.trabajo.model.entities.Cliente;
import com.utp.trabajo.model.entities.Venta;
import com.utp.trabajo.services.VentasService;
import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.util.DateTableCellRenderer;
import com.utp.trabajo.services.util.IconService;
import com.utp.trabajo.services.util.OptionPaneService;
import java.awt.Frame;
import java.awt.event.AdjustmentEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
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
import org.springframework.transaction.annotation.Transactional;

public class VentasTab extends org.jdesktop.swingx.JXPanel {

    private DefaultTableModel defaultTableModelVentas = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Long.class;
                case 1:
                    return Date.class;
                default:
                    return String.class;
            }
        }
    };

    private String[] columnNames = {"ID", "Fecha de emisión", "Cliente", "Comprobante", "Total"};
    
    ListSelectionModel selectionModel;
    
    private boolean canRead = true;
    private boolean canEdit = true;
    private boolean canDelete = true;
    private boolean canCreate = true;

    private boolean retrievingData = false;

    private long lastId = 0;

    private long rowsPerUpdate = 100;

    @Autowired
    private VentasService ventaService;
    
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
        checkPermissions();
        updateTable(false);
    }
    
    private void initTable() {
        defaultTableModelVentas.setColumnIdentifiers(columnNames);
        tablaVentas.setModel(defaultTableModelVentas);
        tablaVentas.getColumnModel().getColumn(0).setMinWidth(2);
        tablaVentas.getColumnModel().getColumn(0).setMaxWidth(50);
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
    
    private void addDataToTable(List<Venta> data) {
        data.forEach(venta -> {
            Vector vec = new Vector();
            vec.add(venta.getIdVenta());
            vec.add(venta.getFechaEmision());
            if(venta.getCliente().getIdCliente() == 1L) {
                vec.add(venta.getCliente().getNombre());
            } else {
                vec.add(venta.getCliente().getIdentificacion() + " - " + venta.getCliente().getNombre());
            }
            vec.add(venta.getComprobante().getNombreComprobante());
            vec.add(venta.getPrecioTotal().toPlainString());
            defaultTableModelVentas.addRow(vec);
        });
    }

    private void updateTable(boolean reload) {
        tableInformationLabel.setVisible(false);
        if (!canRead) {
            setBusy("Sin permisos suficientes para leer datos.");
            return;
        }

        setBusy("Cargando...");
        reloadTableButton.setEnabled(false);
        loadMoreButton.setEnabled(false);
        retrievingData = true;
        int oldRowCount = defaultTableModelVentas.getRowCount();
        if (reload) {
            defaultTableModelVentas.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }
        
        SwingWorker worker2 = new SwingWorker<Long, Long>() {
            @Override
            protected Long doInBackground() throws Exception {
                return ventaService.contarVentas();
            }

            @Override
            protected void done() {
                try {
                    Long cantidadClientesDatabase = get();
                    int cantidadClientesTabla = defaultTableModelVentas.getRowCount();
                    contadorVentasLabel.setText("Mostrando: " + cantidadClientesTabla + "/" + cantidadClientesDatabase);

                }
                catch (InterruptedException ex) {
                    try {
                        throw ex.getCause();
                    }
                    catch (NotEnoughPermissionsException e) {
                        //Joption pane or do nothing!
                    }
                    catch (Throwable e) {
                        System.out.println("impossible :");
                        e.printStackTrace();
                        System.out.println("impossible end");
                        return;
                    }
                }
                catch (ExecutionException ex) {
                    Logger.getLogger(VentasTab.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };
        new SwingWorker<List<Venta>, List<Venta>>() {
            @Override
            protected List<Venta> doInBackground() throws Exception {
                if (reload) {
                    return ventaService.streamVentas(lastId, (long) oldRowCount);
                }
                return ventaService.streamVentas(lastId, rowsPerUpdate);
            }

            @Override
            protected void done() {
                try {
                    addDataToTable(get());
                    int lastRow = 0;
                    int rowCount = defaultTableModelVentas.getRowCount();
                    if (rowCount != 0) {
                        lastRow = rowCount - 1;
                    }
                    var id = defaultTableModelVentas.getValueAt(lastRow, 0);
                    lastId = Long.parseLong(id.toString());
                } catch (InterruptedException ex) {
                    Logger.getLogger(VentasTab.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(VentasTab.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    tableInformationLabel.setVisible(true);
                    //Logger.getLogger(ClientesTab.class.getName()).log(Level.SEVERE, null, ex);
                    //} catch (NotEnoughPermissionsException ex) {

                } finally {
                    setIdle();
                    reloadTableButton.setEnabled(true);
                    loadMoreButton.setEnabled(true);
                    retrievingData = false;
                }
                worker2.execute();
            }
        }.execute();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        detallesVentaDialog = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        detalleVentaTable = new org.jdesktop.swingx.JXTable();
        clienteField = new javax.swing.JTextField();
        clienteLabel = new javax.swing.JLabel();
        dniLabel = new javax.swing.JLabel();
        dniField = new javax.swing.JTextField();
        numeroComprasLabel = new javax.swing.JLabel();
        numeroComprasField = new javax.swing.JTextField();
        empleadoLabel = new javax.swing.JLabel();
        empleadoField = new javax.swing.JTextField();
        fechaEmisionLabel = new javax.swing.JLabel();
        fechaEmisionField = new javax.swing.JTextField();
        metodoPagoField = new javax.swing.JTextField();
        metodoPagoLabel = new javax.swing.JLabel();
        comprobanteField = new javax.swing.JTextField();
        comprobanteLabel = new javax.swing.JLabel();
        totalVentaField = new javax.swing.JTextField();
        totalVentaLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
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

        detallesVentaDialog.setTitle("Detalles de venta");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Venta #00");

        detalleVentaTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Producto", "Cantidad", "Precio", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(detalleVentaTable);
        if (detalleVentaTable.getColumnModel().getColumnCount() > 0) {
            detalleVentaTable.getColumnModel().getColumn(0).setMinWidth(5);
            detalleVentaTable.getColumnModel().getColumn(0).setPreferredWidth(50);
            detalleVentaTable.getColumnModel().getColumn(0).setMaxWidth(60);
            detalleVentaTable.getColumnModel().getColumn(1).setPreferredWidth(500);
            detalleVentaTable.getColumnModel().getColumn(2).setPreferredWidth(100);
            detalleVentaTable.getColumnModel().getColumn(2).setMaxWidth(100);
            detalleVentaTable.getColumnModel().getColumn(3).setPreferredWidth(100);
            detalleVentaTable.getColumnModel().getColumn(3).setMaxWidth(100);
            detalleVentaTable.getColumnModel().getColumn(4).setPreferredWidth(100);
            detalleVentaTable.getColumnModel().getColumn(4).setMaxWidth(100);
        }

        clienteLabel.setText("Cliente");

        dniLabel.setText("DNI / RUC");

        dniField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dniFieldActionPerformed(evt);
            }
        });

        numeroComprasLabel.setText("Nº de compras");

        empleadoLabel.setText("Empleado");

        fechaEmisionLabel.setText("Fecha de emisión");

        metodoPagoLabel.setText("Método de pago");

        comprobanteLabel.setText("Tipo de comprobante");

        totalVentaLabel.setText("Total");

        javax.swing.GroupLayout detallesVentaDialogLayout = new javax.swing.GroupLayout(detallesVentaDialog.getContentPane());
        detallesVentaDialog.getContentPane().setLayout(detallesVentaDialogLayout);
        detallesVentaDialogLayout.setHorizontalGroup(
            detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(detallesVentaDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(detallesVentaDialogLayout.createSequentialGroup()
                        .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(detallesVentaDialogLayout.createSequentialGroup()
                                .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(fechaEmisionLabel)
                                    .addComponent(fechaEmisionField, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(metodoPagoLabel)
                                    .addGroup(detallesVentaDialogLayout.createSequentialGroup()
                                        .addComponent(metodoPagoField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(comprobanteField, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(totalVentaField, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(detallesVentaDialogLayout.createSequentialGroup()
                                .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(clienteLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(clienteField, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(dniLabel)
                                    .addComponent(dniField, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(detallesVentaDialogLayout.createSequentialGroup()
                                        .addComponent(comprobanteLabel)
                                        .addGap(18, 18, 18)
                                        .addComponent(totalVentaLabel))
                                    .addGroup(detallesVentaDialogLayout.createSequentialGroup()
                                        .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(numeroComprasLabel)
                                            .addComponent(numeroComprasField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(6, 6, 6)
                                        .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(empleadoLabel)
                                            .addComponent(empleadoField, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addGap(0, 31, Short.MAX_VALUE)))
                .addContainerGap())
        );
        detallesVentaDialogLayout.setVerticalGroup(
            detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detallesVentaDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(detallesVentaDialogLayout.createSequentialGroup()
                            .addComponent(clienteLabel)
                            .addGap(2, 2, 2)
                            .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(clienteField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(dniField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(detallesVentaDialogLayout.createSequentialGroup()
                            .addComponent(dniLabel)
                            .addGap(24, 24, 24)))
                    .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(detallesVentaDialogLayout.createSequentialGroup()
                            .addComponent(numeroComprasLabel)
                            .addGap(2, 2, 2)
                            .addComponent(numeroComprasField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(detallesVentaDialogLayout.createSequentialGroup()
                            .addComponent(empleadoLabel)
                            .addGap(2, 2, 2)
                            .addComponent(empleadoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(detallesVentaDialogLayout.createSequentialGroup()
                        .addComponent(fechaEmisionLabel)
                        .addGap(2, 2, 2)
                        .addComponent(fechaEmisionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(detallesVentaDialogLayout.createSequentialGroup()
                        .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(metodoPagoLabel)
                            .addComponent(comprobanteLabel)
                            .addComponent(totalVentaLabel))
                        .addGap(2, 2, 2)
                        .addGroup(detallesVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(metodoPagoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comprobanteField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(totalVentaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addContainerGap())
        );

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jXTable1);

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
        detallesVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detallesVentaActionPerformed(evt);
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
        updateTable(true);
    }//GEN-LAST:event_reloadTableButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        updateTable(false);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    private void nuevoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoButtonActionPerformed
        JXFrame nuevaVentaDialog = new JXFrame("Nueva venta", false);
        nuevaVentaDialog.setIconImage(iconService.iconoNuevaVenta.getImage());
        NuevaVentaWindow nuevaVenta = getNuevaVentaTabInstance();
        nuevaVentaDialog.getContentPane().add(nuevaVenta);
        nuevaVentaDialog.setMinimumSize(new java.awt.Dimension(820, 520));
        nuevaVentaDialog.pack();
        
        nuevaVentaDialog.setLocationRelativeTo(this);
        nuevaVentaDialog.setVisible(true);
        nuevaVentaDialog.setDefaultCloseOperation(JXFrame.DO_NOTHING_ON_CLOSE);
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

    private void detallesVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detallesVentaActionPerformed
        
    }//GEN-LAST:event_detallesVentaActionPerformed

    private void dniFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dniFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dniFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JTextField clienteField;
    private javax.swing.JLabel clienteLabel;
    private javax.swing.JTextField comprobanteField;
    private javax.swing.JLabel comprobanteLabel;
    private javax.swing.JLabel contadorVentasLabel;
    private org.jdesktop.swingx.JXTable detalleVentaTable;
    private javax.swing.JButton detallesVenta;
    private javax.swing.JDialog detallesVentaDialog;
    private javax.swing.JTextField dniField;
    private javax.swing.JLabel dniLabel;
    private javax.swing.JTextField empleadoField;
    private javax.swing.JLabel empleadoLabel;
    private javax.swing.JTextField fechaEmisionField;
    private javax.swing.JLabel fechaEmisionLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JTextField metodoPagoField;
    private javax.swing.JLabel metodoPagoLabel;
    private javax.swing.JButton nuevoButton;
    private javax.swing.JTextField numeroComprasField;
    private javax.swing.JLabel numeroComprasLabel;
    private javax.swing.JButton reloadTableButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaVentas;
    private javax.swing.JLabel tableInformationLabel;
    private javax.swing.JTextField totalVentaField;
    private javax.swing.JLabel totalVentaLabel;
    // End of variables declaration//GEN-END:variables

    @Autowired
    private ObjectFactory<NuevaVentaWindow> nuevaVentaWindowObjectFactory;

    public NuevaVentaWindow getNuevaVentaTabInstance() {
        return nuevaVentaWindowObjectFactory.getObject();
    }
    
}

