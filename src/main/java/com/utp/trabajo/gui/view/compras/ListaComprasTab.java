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
        //TABLA DE DETALLE COMPRA
        DetalleCompra detalleCompraTable1=new DetalleCompra();
        detalleCompraTable1.setVisible(true);
        
        //TABLA DE ESTADO COMPRA
        EstadoCompra estadoCompraTable1=new EstadoCompra();
        estadoCompraTable1.setVisible(true);
        initTable();

        nuevaCompraDialog.pack();
        nuevaCompraDialog.setLocationRelativeTo(this);
        System.out.println("Compras tab - Nueva instancia!");
        verDetallesDialog.pack();
        verDetallesDialog.setLocationRelativeTo(this);
        System.out.println("Detalles de compra - Nueva instancia!");
        verEstadoDialog.pack();
        verEstadoDialog.setLocationRelativeTo(this);
        System.out.println("Estado de compra - Nueva instancia!");
        
        
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
        detalleCompraTable2 = new org.jdesktop.swingx.JXTable();
        jButton1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cantidadField = new javax.swing.JTextField();
        idDetalleCompraField = new javax.swing.JTextField();
        fechaLlegadaField = new javax.swing.JTextField();
        fechaSalidaField = new javax.swing.JTextField();
        precioField = new javax.swing.JTextField();
        guardarDetallesButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        verEstadoDialog = new javax.swing.JDialog();
        estadoCompraLabel = new javax.swing.JLabel();
        estadoCompraScrollPane = new javax.swing.JScrollPane();
        estadoCompraTable2 = new org.jdesktop.swingx.JXTable();
        jLabel12 = new javax.swing.JLabel();
        idEstadoCompraField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        estadoField = new javax.swing.JTextField();
        agregarButton = new javax.swing.JButton();
        pasarDatosButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
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
        detalleCompraLabel.setText("EDITAR DETALLES");

        detalleCompraTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "IDdetalleCompra", "Cantidad", "Fecha llegada", "Fecha salida", "Precio"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        detalleCompraScrollPane.setViewportView(detalleCompraTable2);

        jButton1.setText("Transferir fila seleccionada a la otra tabla");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel7.setText("IDdetalleCompra");

        jLabel8.setText("Cantidad");

        jLabel9.setText("Fecha de Llegada");

        jLabel10.setText("Fecha de Salida");

        jLabel11.setText("Precio");

        guardarDetallesButton.setText("Guardar");
        guardarDetallesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarDetallesButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout verDetallesDialogLayout = new javax.swing.GroupLayout(verDetallesDialog.getContentPane());
        verDetallesDialog.getContentPane().setLayout(verDetallesDialogLayout);
        verDetallesDialogLayout.setHorizontalGroup(
            verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, verDetallesDialogLayout.createSequentialGroup()
                .addGroup(verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, verDetallesDialogLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(guardarDetallesButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 229, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, verDetallesDialogLayout.createSequentialGroup()
                        .addGroup(verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGap(79, 79, 79)
                        .addGroup(verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fechaLlegadaField)
                            .addComponent(idDetalleCompraField)
                            .addComponent(cantidadField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel10))))
                .addGap(18, 18, 18)
                .addGroup(verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(precioField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fechaSalidaField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(137, 137, 137))
            .addComponent(jSeparator3)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, verDetallesDialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(detalleCompraLabel)
                .addGap(338, 338, 338))
            .addGroup(verDetallesDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(detalleCompraScrollPane)
                .addContainerGap())
        );
        verDetallesDialogLayout.setVerticalGroup(
            verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(verDetallesDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(detalleCompraLabel)
                .addGap(30, 30, 30)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(verDetallesDialogLayout.createSequentialGroup()
                        .addGroup(verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(fechaSalidaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(precioField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(verDetallesDialogLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(idDetalleCompraField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(cantidadField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(17, 17, 17)
                .addGroup(verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(fechaLlegadaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(verDetallesDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(guardarDetallesButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(detalleCompraScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        verEstadoDialog.setResizable(false);

        estadoCompraLabel.setText("ESTADO DE LA COMPRA");

        estadoCompraTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID estadoCompra", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        estadoCompraScrollPane.setViewportView(estadoCompraTable2);

        jLabel12.setText("ID estadoCompra");

        jLabel13.setText("Estado");

        estadoField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                estadoFieldActionPerformed(evt);
            }
        });

        agregarButton.setText("Agregar");
        agregarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarButtonActionPerformed(evt);
            }
        });

        pasarDatosButton.setText("Pasar a la tabla general");
        pasarDatosButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasarDatosButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout verEstadoDialogLayout = new javax.swing.GroupLayout(verEstadoDialog.getContentPane());
        verEstadoDialog.getContentPane().setLayout(verEstadoDialogLayout);
        verEstadoDialogLayout.setHorizontalGroup(
            verEstadoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator4)
            .addGroup(verEstadoDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(verEstadoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(estadoCompraScrollPane)
                    .addGroup(verEstadoDialogLayout.createSequentialGroup()
                        .addGroup(verEstadoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13))
                        .addGap(18, 18, 18)
                        .addGroup(verEstadoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(estadoField, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(idEstadoCompraField))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(verEstadoDialogLayout.createSequentialGroup()
                        .addComponent(agregarButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pasarDatosButton)))
                .addContainerGap())
            .addGroup(verEstadoDialogLayout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addComponent(estadoCompraLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        verEstadoDialogLayout.setVerticalGroup(
            verEstadoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(verEstadoDialogLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(estadoCompraLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(verEstadoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(idEstadoCompraField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(verEstadoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(estadoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(verEstadoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(agregarButton)
                    .addComponent(pasarDatosButton))
                .addGap(18, 18, 18)
                .addComponent(estadoCompraScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

        DetallesCompraButton.setText("EDITAR DETALLES");
        DetallesCompraButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DetallesCompraButtonActionPerformed(evt);
            }
        });

        VerEstadoCompraButton.setText("EDITAR ESTADO DE PRODUCTO");
        VerEstadoCompraButton.setActionCommand("EDITAR ESTADO DE PRODUCTOS");
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 196, Short.MAX_VALUE)
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
        int FilaSeleccionada=detalleCompraTable2.getSelectedRow();
        
    }//GEN-LAST:event_DetallesCompraButtonActionPerformed

    private void VerEstadoCompraButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VerEstadoCompraButtonActionPerformed
        // TODO add your handling code here:
        verEstadoDialog.setVisible(true);
        
    }//GEN-LAST:event_VerEstadoCompraButtonActionPerformed
    //PARA EL BOTON DE PASAR DATOS A OTRA TABLA(detalle compra)
    DefaultTableModel modelo1;
    public void nuevaTablaDC(){
        //AL PASAR LOS DATOS, LIMPIAMOS LA PRIMERA FILA
        modelo1=new DefaultTableModel();
        detalleCompraTable2.setModel(modelo1);
    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        int FilaSeleccionada=detalleCompraTable2.getSelectedRow();
        if (FilaSeleccionada>=0) {
            String Datos[]=new String[5];
            Datos[0]=detalleCompraTable2.getValueAt(FilaSeleccionada,0).toString();
            Datos[1]=detalleCompraTable2.getValueAt(FilaSeleccionada,1).toString();
            Datos[2]=detalleCompraTable2.getValueAt(FilaSeleccionada,2).toString();
            Datos[3]=detalleCompraTable2.getValueAt(FilaSeleccionada,3).toString();
            Datos[4]=detalleCompraTable2.getValueAt(FilaSeleccionada,4).toString();
            
            DetalleCompra.modelo2.addRow(Datos);
            modelo1.removeRow(FilaSeleccionada);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void guardarDetallesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarDetallesButtonActionPerformed
        // TODO add your handling code here:
        String IDdetalleCompra=idDetalleCompraField.getText();
        String Cantidad=cantidadField.getText();
        String FechaLlegada=fechaLlegadaField.getText();
        String FechaSalida=fechaSalidaField.getText();
        String Precio=precioField.getText();
        String Datos[]={IDdetalleCompra,Cantidad,FechaLlegada,FechaSalida,Precio};
        modelo1.addRow(Datos);
    }//GEN-LAST:event_guardarDetallesButtonActionPerformed

    private void estadoFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_estadoFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_estadoFieldActionPerformed
    //PARA EL BOTON DE PASAR DATOS A OTRA TABLA(detalle compra)
    DefaultTableModel modelo4;
    public void nuevaTablaEC(){
        //AL PASAR LOS DATOS, LIMPIAMOS LA PRIMERA FILA
        modelo1=new DefaultTableModel();
        estadoCompraTable2.setModel(modelo4);
    }
    private void pasarDatosButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasarDatosButtonActionPerformed
        // TODO add your handling code here:
        int FilaSeleccionada=estadoCompraTable2.getSelectedRow();
        if (FilaSeleccionada>=0) {
            String Datos[]=new String[2];
            Datos[0]=detalleCompraTable2.getValueAt(FilaSeleccionada,0).toString();
            Datos[1]=detalleCompraTable2.getValueAt(FilaSeleccionada,1).toString();
            
            EstadoCompra.modelo3.addRow(Datos);
            modelo4.removeRow(FilaSeleccionada);
        }
    }//GEN-LAST:event_pasarDatosButtonActionPerformed

    private void agregarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarButtonActionPerformed
        // TODO add your handling code here:
        String IDestadoCompra=idEstadoCompraField.getText();
        String Estado=estadoField.getText();
        String Datos[]={IDestadoCompra,Estado};
        modelo4.addRow(Datos);
    }//GEN-LAST:event_agregarButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DetallesCompraButton;
    private javax.swing.JTextField IdCompraField;
    private javax.swing.JButton VerEstadoCompraButton;
    private javax.swing.JButton agregarButton;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarNuevaCompraButton;
    private javax.swing.JTextField cantidadField;
    private javax.swing.JTextField descuentoField;
    private javax.swing.JLabel detalleCompraLabel;
    private javax.swing.JScrollPane detalleCompraScrollPane;
    private org.jdesktop.swingx.JXTable detalleCompraTable2;
    private javax.swing.JButton editarCompraButton;
    private javax.swing.JButton eliminarCompraButton;
    private javax.swing.JLabel estadoCompraLabel;
    private javax.swing.JScrollPane estadoCompraScrollPane;
    private org.jdesktop.swingx.JXTable estadoCompraTable2;
    private javax.swing.JTextField estadoField;
    private javax.swing.JTextField fechaCompraField;
    private javax.swing.JTextField fechaLlegadaField;
    private javax.swing.JTextField fechaSalidaField;
    private javax.swing.JButton guardarButton;
    private javax.swing.JButton guardarDetallesButton;
    private javax.swing.JTextField idDetalleCompraField;
    private javax.swing.JTextField idEstadoCompraField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JTextField metodoPagoField;
    private javax.swing.JButton nuevaCompraButton;
    private javax.swing.JDialog nuevaCompraDialog;
    private javax.swing.JButton pasarDatosButton;
    private javax.swing.JTextField precioField;
    private javax.swing.JTextField precioTotalField;
    private javax.swing.JButton reloadButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaCompras;
    private javax.swing.JLabel tableInformationLabel;
    private javax.swing.JDialog verDetallesDialog;
    private javax.swing.JDialog verEstadoDialog;
    // End of variables declaration//GEN-END:variables
}
