package com.utp.trabajo.gui.view.productos;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.gui.view.almacen.MarcaTab;
import com.utp.trabajo.gui.view.clientes.ClientesTab;
import com.utp.trabajo.model.entities.Almacen;
import com.utp.trabajo.model.entities.Cliente;
import com.utp.trabajo.model.entities.Marca;
import com.utp.trabajo.model.entities.Producto;
import com.utp.trabajo.model.entities.TipoProducto;
import com.utp.trabajo.services.MarcaService;
import com.utp.trabajo.services.ProductoService;
import com.utp.trabajo.services.TipoProductoService;
import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.util.DateTableCellRenderer;
import com.utp.trabajo.services.util.OptionPaneService;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
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
    
    private PlainDocument descripcionDocument;
    
    private Marca marcaSeleccionada = null;
    
    private TipoProducto tipoSeleccionado = null;
    
    private Producto productoSeleccionado = null;

    @Autowired 
    private ProductoService productoService;
    
    @Autowired
    private SecurityService securityService;

    public ProductoTab() {
        initComponents();
        initTable();
        initMarcaTable();
        initTipoTable();
        descripcionDocument = new PlainDocument();
        descripcionDocument.setDocumentFilter(new DocumentFilter() {
            int maxCharacters = 400;

            //FIX ??-??
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                int documentLength = fb.getDocument().getLength();
                int incomingTextLength = string.length();
                if (documentLength + incomingTextLength <= maxCharacters) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    String truncatedString = string.substring(0, maxCharacters - documentLength);
                    //^ maybe check if incomingTextLength == 0...
                    super.insertString(fb, offset, truncatedString, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int documentLength = fb.getDocument().getLength();
                int incomingTextLength = text.length();
                if (documentLength + incomingTextLength <= maxCharacters) {
                    super.insertString(fb, offset, text, attrs);
                } else {
                    String truncatedString = text.substring(0, maxCharacters - documentLength);
                    //^ maybe check if incomingTextLength == 0...
                    super.insertString(fb, offset, truncatedString, attrs);
                }

            }

        });
        descripcionDocument.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCharsLabel();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCharsLabel();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateCharsLabel();
            }
        });
        descripcionProducto.setDocument(descripcionDocument);
        editarDescripcionProducto.setDocument(descripcionDocument);
        seleccionarMarcaDialog.pack();
        seleccionarMarcaDialog.setLocationRelativeTo(this);
        seleccionarTipoProductoDialog.pack();
        seleccionarTipoProductoDialog.setLocationRelativeTo(this);
        nuevoProductoDialog.pack();
        nuevoProductoDialog.setLocationRelativeTo(this);
        editarProductoDialog.pack();
        editarProductoDialog.setLocationRelativeTo(this);
    }
    
    @PostConstruct
    private void init() {
        checkPermissions();
        updateTable(false); 
        retrieveTipoData(false);
        retrieveMarcaData(false);
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

            if (selectionModel.getSelectedItemsCount() == 1) {
                editarButton.setEnabled(true);
            } else {
                editarButton.setEnabled(false);
            }

        });
        
        setIdle();
        editarButton.setEnabled(false);

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
        if (!permissions.contains("edit")) {
            canEdit = false;
            editarButton.setEnabled(false);
        }

    }
    
    private void updateCharsLabel() {
        int lines = descripcionDocument.getLength();
        maxCaracteresLabel.setText(lines + "/400");
        maxCaracteresLabel1.setText(lines + "/400");
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
    
    private long getSelectedId() {
        List<Long> ids = new ArrayList<>();
        for (int i : selectionModel.getSelectedIndices()) { //rows 
            //System.out.println(i);
            i = tableProductos.convertRowIndexToModel(i); //IMPORTANTISIMO, en caso de que la tabla esté ordenada por alguna columna, esto devolvera siempre la fila seleccionada.
            ids.add((Long) defaultTableModelProductos.getValueAt(i, 0));
        }
        return ids.get(0);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nuevoProductoDialog = new javax.swing.JDialog();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        nuevoProductoLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        maxCaracteresLabel = new javax.swing.JLabel();
        nuevoDescripcionScrollPane = new javax.swing.JScrollPane();
        descripcionProducto = new javax.swing.JTextArea();
        guardarAlmacenButton = new javax.swing.JButton();
        cancelarCreacionButton = new javax.swing.JButton();
        seleccionarMarca = new javax.swing.JButton();
        seleccionarTipo = new javax.swing.JButton();
        stockField = new javax.swing.JTextField();
        stockinicialField = new javax.swing.JTextField();
        stockminimoField = new javax.swing.JTextField();
        columnaField = new javax.swing.JTextField();
        estanteriaField = new javax.swing.JTextField();
        filaField = new javax.swing.JTextField();
        productoField = new javax.swing.JTextField();
        marcaField = new javax.swing.JTextField();
        tipoProductoField = new javax.swing.JTextField();
        editarProductoDialog = new javax.swing.JDialog();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel14 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        editarProductoLabel = new javax.swing.JLabel();
        maxCaracteresLabel1 = new javax.swing.JLabel();
        editarDescripcionScrollPane = new javax.swing.JScrollPane();
        editarDescripcionProducto = new javax.swing.JTextArea();
        actualizarProductoButton = new javax.swing.JButton();
        cancelarEdicionButton = new javax.swing.JButton();
        seleccionarMarcaEditar = new javax.swing.JButton();
        seleccionarTipoEditar = new javax.swing.JButton();
        stockFieldEditar = new javax.swing.JTextField();
        stockinicialFieldEditar = new javax.swing.JTextField();
        stockminimoFieldEditar = new javax.swing.JTextField();
        columnaFieldEditar = new javax.swing.JTextField();
        estanteriaFieldEditar = new javax.swing.JTextField();
        filaFieldEditar = new javax.swing.JTextField();
        productoFieldEditar = new javax.swing.JTextField();
        marcaFieldEditar = new javax.swing.JTextField();
        tipoProductoFieldEditar = new javax.swing.JTextField();
        seleccionarMarcaDialog = new javax.swing.JDialog();
        scrollPaneMarca = new javax.swing.JScrollPane();
        tablaMarca = new org.jdesktop.swingx.JXTable();
        nuevaMarcaButton = new javax.swing.JButton();
        reloadTableMarcaButton = new javax.swing.JButton();
        loadMoreMarcaButton = new javax.swing.JButton();
        seleccionarMarcaButton = new javax.swing.JButton();
        seleccionarTipoProductoDialog = new javax.swing.JDialog();
        nuevoTipoButton = new javax.swing.JButton();
        scrollPaneTipo = new javax.swing.JScrollPane();
        tablaTipoTab = new org.jdesktop.swingx.JXTable();
        loadMoreTipoButton = new javax.swing.JButton();
        reloadTableTipoButton = new javax.swing.JButton();
        seleccionarTipoButton = new javax.swing.JButton();
        nuevaMarcaDialog = new javax.swing.JDialog();
        nuevaMarcaLabel = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        cancelarCreacionClienteButton = new javax.swing.JButton();
        nombreMarcaLabel = new javax.swing.JLabel();
        nombreMarcaField = new javax.swing.JTextField();
        guardarMarcaButton = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JSeparator();
        nuevoTipoDialog = new javax.swing.JDialog();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator8 = new javax.swing.JSeparator();
        nuevoTipoLabel = new javax.swing.JLabel();
        guardarTipoButton = new javax.swing.JButton();
        cancelarTipoButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        tipoField = new javax.swing.JTextField();
        nuevoButton = new javax.swing.JButton();
        editarButton = new javax.swing.JButton();
        recargarButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        layeredPane = new javax.swing.JLayeredPane();
        scrollPane = new javax.swing.JScrollPane();
        tableProductos = new org.jdesktop.swingx.JXTable();
        tableInformationLabel = new javax.swing.JLabel();
        contadorLabel = new javax.swing.JLabel();

        nuevoProductoDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        nuevoProductoDialog.setTitle("Nuevo producto");
        nuevoProductoDialog.setModal(true);
        nuevoProductoDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        nuevoProductoDialog.setResizable(false);
        nuevoProductoDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                nuevoProductoDialogWindowClosing(evt);
            }
        });

        nuevoProductoLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nuevoProductoLabel.setText("Nuevo producto");

        jLabel2.setText("Stock:");

        jLabel3.setText("Stock inicial:");

        jLabel4.setText("Stock mínimo:");

        jLabel5.setText("Columna:");

        jLabel6.setText("Estanteria:");

        jLabel7.setText("Fila:");

        jLabel1.setText("Producto");

        jLabel15.setText("Descripción (opcional)");

        jLabel16.setText("Marca");

        jLabel17.setText("Tipo de producto:");

        maxCaracteresLabel.setText("400/400");

        descripcionProducto.setColumns(20);
        descripcionProducto.setLineWrap(true);
        descripcionProducto.setRows(5);
        descripcionProducto.setWrapStyleWord(true);
        nuevoDescripcionScrollPane.setViewportView(descripcionProducto);

        guardarAlmacenButton.setText("Guardar");
        guardarAlmacenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarAlmacenButtonActionPerformed(evt);
            }
        });

        cancelarCreacionButton.setText("Cancelar");
        cancelarCreacionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarCreacionButtonActionPerformed(evt);
            }
        });

        seleccionarMarca.setText("Seleccionar");
        seleccionarMarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarMarcaActionPerformed(evt);
            }
        });

        seleccionarTipo.setText("Seleccionar");
        seleccionarTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarTipoActionPerformed(evt);
            }
        });

        marcaField.setEditable(false);

        tipoProductoField.setEditable(false);

        javax.swing.GroupLayout nuevoProductoDialogLayout = new javax.swing.GroupLayout(nuevoProductoDialog.getContentPane());
        nuevoProductoDialog.getContentPane().setLayout(nuevoProductoDialogLayout);
        nuevoProductoDialogLayout.setHorizontalGroup(
            nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(nuevoProductoDialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(guardarAlmacenButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cancelarCreacionButton)
                .addContainerGap())
            .addGroup(nuevoProductoDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoProductoDialogLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addGap(6, 6, 6)
                        .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(stockinicialField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(stockField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stockminimoField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(nuevoProductoDialogLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoProductoDialogLayout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(columnaField, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoProductoDialogLayout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(filaField, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap())))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoProductoDialogLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(estanteriaField, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                                .addContainerGap())))
                    .addGroup(nuevoProductoDialogLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(302, 353, Short.MAX_VALUE))
                    .addGroup(nuevoProductoDialogLayout.createSequentialGroup()
                        .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(productoField)
                            .addComponent(nuevoDescripcionScrollPane)
                            .addComponent(nuevoProductoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(nuevoProductoDialogLayout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(nuevoProductoDialogLayout.createSequentialGroup()
                                .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(nuevoProductoDialogLayout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(marcaField)
                                    .addComponent(tipoProductoField))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(seleccionarMarca)
                                    .addComponent(seleccionarTipo)))
                            .addGroup(nuevoProductoDialogLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(maxCaracteresLabel)))
                        .addContainerGap())))
        );
        nuevoProductoDialogLayout.setVerticalGroup(
            nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoProductoDialogLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(nuevoProductoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(productoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel16)
                .addGap(2, 2, 2)
                .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(marcaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(seleccionarMarca))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addGap(2, 2, 2)
                .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tipoProductoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(seleccionarTipo))
                .addGap(6, 6, 6)
                .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(stockinicialField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(nuevoProductoDialogLayout.createSequentialGroup()
                        .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(columnaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(stockField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(filaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6)
                .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(stockminimoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(estanteriaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)))
                .addGap(6, 6, 6)
                .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(maxCaracteresLabel))
                .addGap(2, 2, 2)
                .addComponent(nuevoDescripcionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(nuevoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guardarAlmacenButton)
                    .addComponent(cancelarCreacionButton))
                .addGap(6, 6, 6))
        );

        editarProductoDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        editarProductoDialog.setTitle("Editar producto");
        editarProductoDialog.setAlwaysOnTop(true);
        editarProductoDialog.setModal(true);
        editarProductoDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        editarProductoDialog.setResizable(false);
        editarProductoDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                editarProductoDialogWindowClosing(evt);
            }
        });

        jLabel14.setText("Stock:");

        jLabel18.setText("Stock inicial:");

        jLabel19.setText("Stock mínimo:");

        jLabel20.setText("Columna:");

        jLabel21.setText("Estanteria:");

        jLabel22.setText("Fila:");

        jLabel23.setText("Producto:");

        jLabel24.setText("Descripción (opcional)");

        jLabel25.setText("Marca:");

        jLabel26.setText("Tipo de producto:");

        editarProductoLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        editarProductoLabel.setText("Editar producto #00");

        maxCaracteresLabel1.setText("0/400");

        editarDescripcionProducto.setColumns(20);
        editarDescripcionProducto.setLineWrap(true);
        editarDescripcionProducto.setRows(5);
        editarDescripcionProducto.setWrapStyleWord(true);
        editarDescripcionScrollPane.setViewportView(editarDescripcionProducto);

        actualizarProductoButton.setText("Guardar");
        actualizarProductoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actualizarProductoButtonActionPerformed(evt);
            }
        });

        cancelarEdicionButton.setText("Cancelar");
        cancelarEdicionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarEdicionButtonActionPerformed(evt);
            }
        });

        seleccionarMarcaEditar.setText("Seleccionar");
        seleccionarMarcaEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarMarcaEditarActionPerformed(evt);
            }
        });

        seleccionarTipoEditar.setText("Seleccionar");
        seleccionarTipoEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarTipoEditarActionPerformed(evt);
            }
        });

        marcaFieldEditar.setEditable(false);

        tipoProductoFieldEditar.setEditable(false);

        javax.swing.GroupLayout editarProductoDialogLayout = new javax.swing.GroupLayout(editarProductoDialog.getContentPane());
        editarProductoDialog.getContentPane().setLayout(editarProductoDialogLayout);
        editarProductoDialogLayout.setHorizontalGroup(
            editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator7, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(editarProductoDialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(actualizarProductoButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cancelarEdicionButton)
                .addContainerGap())
            .addGroup(editarProductoDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(editarProductoDialogLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel14)
                            .addComponent(jLabel19))
                        .addGap(6, 6, 6)
                        .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(stockinicialFieldEditar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(stockFieldEditar, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stockminimoFieldEditar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(editarProductoDialogLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editarProductoDialogLayout.createSequentialGroup()
                                        .addComponent(jLabel20)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(columnaFieldEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editarProductoDialogLayout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(filaFieldEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap())))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editarProductoDialogLayout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(estanteriaFieldEditar, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                                .addContainerGap())))
                    .addGroup(editarProductoDialogLayout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(302, 350, Short.MAX_VALUE))
                    .addGroup(editarProductoDialogLayout.createSequentialGroup()
                        .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(productoFieldEditar)
                            .addComponent(editarDescripcionScrollPane)
                            .addComponent(editarProductoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(editarProductoDialogLayout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(editarProductoDialogLayout.createSequentialGroup()
                                .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(editarProductoDialogLayout.createSequentialGroup()
                                        .addComponent(jLabel26)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(marcaFieldEditar)
                                    .addComponent(tipoProductoFieldEditar))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(seleccionarMarcaEditar)
                                    .addComponent(seleccionarTipoEditar)))
                            .addGroup(editarProductoDialogLayout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(maxCaracteresLabel1)))
                        .addContainerGap())))
        );
        editarProductoDialogLayout.setVerticalGroup(
            editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editarProductoDialogLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(editarProductoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(productoFieldEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel25)
                .addGap(2, 2, 2)
                .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(marcaFieldEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(seleccionarMarcaEditar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26)
                .addGap(2, 2, 2)
                .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tipoProductoFieldEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(seleccionarTipoEditar))
                .addGap(6, 6, 6)
                .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel18)
                        .addComponent(stockinicialFieldEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(editarProductoDialogLayout.createSequentialGroup()
                        .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(columnaFieldEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)
                            .addComponent(stockFieldEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(filaFieldEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6)
                .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(stockminimoFieldEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19))
                    .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(estanteriaFieldEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21)))
                .addGap(6, 6, 6)
                .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(maxCaracteresLabel1))
                .addGap(2, 2, 2)
                .addComponent(editarDescripcionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(editarProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(actualizarProductoButton)
                    .addComponent(cancelarEdicionButton))
                .addGap(6, 6, 6))
        );

        seleccionarMarcaDialog.setTitle("Seleccionar marca");
        seleccionarMarcaDialog.setAlwaysOnTop(true);
        seleccionarMarcaDialog.setModal(true);
        seleccionarMarcaDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);

        tablaMarca.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaMarca.setEditable(false);
        scrollPaneMarca.setViewportView(tablaMarca);

        nuevaMarcaButton.setText("Nuevo");
        nuevaMarcaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevaMarcaButtonActionPerformed(evt);
            }
        });

        reloadTableMarcaButton.setText("Recargar");
        reloadTableMarcaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadTableMarcaButtonActionPerformed(evt);
            }
        });

        loadMoreMarcaButton.setText("Cargar más entradas");
        loadMoreMarcaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMoreMarcaButtonActionPerformed(evt);
            }
        });

        seleccionarMarcaButton.setText("Seleccionar");
        seleccionarMarcaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarMarcaButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout seleccionarMarcaDialogLayout = new javax.swing.GroupLayout(seleccionarMarcaDialog.getContentPane());
        seleccionarMarcaDialog.getContentPane().setLayout(seleccionarMarcaDialogLayout);
        seleccionarMarcaDialogLayout.setHorizontalGroup(
            seleccionarMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seleccionarMarcaDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(seleccionarMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(seleccionarMarcaDialogLayout.createSequentialGroup()
                        .addGap(0, 354, Short.MAX_VALUE)
                        .addComponent(loadMoreMarcaButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(reloadTableMarcaButton))
                    .addComponent(scrollPaneMarca)
                    .addGroup(seleccionarMarcaDialogLayout.createSequentialGroup()
                        .addComponent(seleccionarMarcaButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nuevaMarcaButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        seleccionarMarcaDialogLayout.setVerticalGroup(
            seleccionarMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, seleccionarMarcaDialogLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(seleccionarMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevaMarcaButton)
                    .addComponent(seleccionarMarcaButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneMarca, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addGroup(seleccionarMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadMoreMarcaButton)
                    .addComponent(reloadTableMarcaButton))
                .addGap(6, 6, 6))
        );

        seleccionarTipoProductoDialog.setTitle("Seleccionar tipo de producto");
        seleccionarTipoProductoDialog.setAlwaysOnTop(true);
        seleccionarTipoProductoDialog.setModal(true);
        seleccionarTipoProductoDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);

        nuevoTipoButton.setText("Nuevo");
        nuevoTipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoTipoButtonActionPerformed(evt);
            }
        });

        tablaTipoTab.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaTipoTab.setEditable(false);
        scrollPaneTipo.setViewportView(tablaTipoTab);

        loadMoreTipoButton.setText("Cargar más entradas");
        loadMoreTipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMoreTipoButtonActionPerformed(evt);
            }
        });

        reloadTableTipoButton.setText("Recargar");
        reloadTableTipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadTableTipoButtonActionPerformed(evt);
            }
        });

        seleccionarTipoButton.setText("Seleccionar");
        seleccionarTipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarTipoButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout seleccionarTipoProductoDialogLayout = new javax.swing.GroupLayout(seleccionarTipoProductoDialog.getContentPane());
        seleccionarTipoProductoDialog.getContentPane().setLayout(seleccionarTipoProductoDialogLayout);
        seleccionarTipoProductoDialogLayout.setHorizontalGroup(
            seleccionarTipoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, seleccionarTipoProductoDialogLayout.createSequentialGroup()
                .addContainerGap(354, Short.MAX_VALUE)
                .addComponent(loadMoreTipoButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(reloadTableTipoButton)
                .addContainerGap())
            .addGroup(seleccionarTipoProductoDialogLayout.createSequentialGroup()
                .addGroup(seleccionarTipoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(seleccionarTipoProductoDialogLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(scrollPaneTipo))
                    .addGroup(seleccionarTipoProductoDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(seleccionarTipoButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nuevoTipoButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(6, 6, 6))
        );
        seleccionarTipoProductoDialogLayout.setVerticalGroup(
            seleccionarTipoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, seleccionarTipoProductoDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(seleccionarTipoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(seleccionarTipoButton)
                    .addComponent(nuevoTipoButton))
                .addGap(6, 6, 6)
                .addComponent(scrollPaneTipo, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(seleccionarTipoProductoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadMoreTipoButton)
                    .addComponent(reloadTableTipoButton))
                .addGap(6, 6, 6))
        );

        nuevaMarcaDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        nuevaMarcaDialog.setTitle("Nueva marca");
        nuevaMarcaDialog.setAlwaysOnTop(true);
        nuevaMarcaDialog.setModal(true);
        nuevaMarcaDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        nuevaMarcaDialog.setResizable(false);
        nuevaMarcaDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                nuevaMarcaDialogWindowClosing(evt);
            }
        });

        nuevaMarcaLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nuevaMarcaLabel.setText("Nueva marca");

        cancelarCreacionClienteButton.setText("Cancelar");
        cancelarCreacionClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarCreacionClienteButtonActionPerformed(evt);
            }
        });

        nombreMarcaLabel.setText("Nombre:");

        guardarMarcaButton.setText("Guardar");
        guardarMarcaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarMarcaButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout nuevaMarcaDialogLayout = new javax.swing.GroupLayout(nuevaMarcaDialog.getContentPane());
        nuevaMarcaDialog.getContentPane().setLayout(nuevaMarcaDialogLayout);
        nuevaMarcaDialogLayout.setHorizontalGroup(
            nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevaMarcaDialogLayout.createSequentialGroup()
                .addGroup(nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(nuevaMarcaDialogLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(guardarMarcaButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelarCreacionClienteButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, nuevaMarcaDialogLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(nuevaMarcaDialogLayout.createSequentialGroup()
                                .addComponent(nuevaMarcaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 41, Short.MAX_VALUE))
                            .addGroup(nuevaMarcaDialogLayout.createSequentialGroup()
                                .addComponent(nombreMarcaLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(nombreMarcaField)))))
                .addContainerGap())
            .addComponent(jSeparator6)
        );
        nuevaMarcaDialogLayout.setVerticalGroup(
            nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nuevaMarcaDialogLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(nuevaMarcaLabel)
                .addGap(6, 6, 6)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nombreMarcaLabel)
                    .addComponent(nombreMarcaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(nuevaMarcaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarCreacionClienteButton)
                    .addComponent(guardarMarcaButton))
                .addGap(6, 6, 6))
        );

        nuevoTipoDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        nuevoTipoDialog.setTitle("Nuevo tipo de producto");
        nuevoTipoDialog.setAlwaysOnTop(true);
        nuevoTipoDialog.setModal(true);
        nuevoTipoDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        nuevoTipoDialog.setResizable(false);
        nuevoTipoDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                nuevoTipoDialogWindowClosing(evt);
            }
        });

        nuevoTipoLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nuevoTipoLabel.setText("Nuevo tipo de producto");

        guardarTipoButton.setText("Guardar");
        guardarTipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarTipoButtonActionPerformed(evt);
            }
        });

        cancelarTipoButton.setText("Cancelar");
        cancelarTipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarTipoButtonActionPerformed(evt);
            }
        });

        jLabel8.setText("Tipo de Producto:");

        javax.swing.GroupLayout nuevoTipoDialogLayout = new javax.swing.GroupLayout(nuevoTipoDialog.getContentPane());
        nuevoTipoDialog.getContentPane().setLayout(nuevoTipoDialogLayout);
        nuevoTipoDialogLayout.setHorizontalGroup(
            nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator8, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(nuevoTipoDialogLayout.createSequentialGroup()
                .addGroup(nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoTipoDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(nuevoTipoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(nuevoTipoDialogLayout.createSequentialGroup()
                        .addGap(106, 106, 106)
                        .addComponent(guardarTipoButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelarTipoButton))
                    .addGroup(nuevoTipoDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tipoField, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        nuevoTipoDialogLayout.setVerticalGroup(
            nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nuevoTipoDialogLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(nuevoTipoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(tipoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(nuevoTipoDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarTipoButton)
                    .addComponent(guardarTipoButton))
                .addGap(6, 6, 6))
        );

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

        tableInformationLabel.setText("Sin datos.");

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(contadorLabel)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(busyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
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
                        .addComponent(editarButton))
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
        nuevoProductoDialog.setVisible(true);
    }//GEN-LAST:event_nuevoButtonActionPerformed

    private void editarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarButtonActionPerformed
        final long selectedId = getSelectedId();
        setBusy("Cargando producto #" + selectedId);
        
        new SwingWorker<Producto, Producto>() {
            @Override
            protected Producto doInBackground() throws Exception {
                return productoService.encontrarProductoPorId(selectedId);
            }

            @Override
            protected void done() {
                try {
                    Producto producto = get();
                    productoSeleccionado = producto;
                    editarProductoLabel.setText("Editar producto #" + producto.getIdProducto());
                    productoFieldEditar.setText(producto.getNombre());
                    marcaFieldEditar.setText(producto.getMarca().getNombreMarca());
                    tipoProductoFieldEditar.setText(producto.getTipoProducto().getTipo());
                    stockFieldEditar.setText(producto.getAlmacen().getStock().toString());
                    stockinicialFieldEditar.setText("" + producto.getAlmacen().getStockInicial());
                    stockminimoFieldEditar.setText("" + producto.getAlmacen().getStockMinimo());
                    columnaFieldEditar.setText("" + producto.getAlmacen().getColumna());
                    estanteriaFieldEditar.setText(producto.getAlmacen().getEstanteria());
                    filaFieldEditar.setText("" + producto.getAlmacen().getFila());
                    editarDescripcionProducto.setText(producto.getDescripcion());
                    marcaSeleccionada = producto.getMarca();
                    tipoSeleccionado = producto.getTipoProducto();
                    seleccionarMarcaEditar.setText("Deseleccionar");
                    seleccionarTipoEditar.setText("Deseleccionar");
                    setIdle();
                    editarProductoDialog.setVisible(true);
                } catch (InterruptedException ex) {
                } catch (ExecutionException ex) {
                    try {
                        throw ex.getCause();
                    } catch (NotEnoughPermissionsException e) {
                    } catch (Throwable imp) {
                        System.out.println("impossible!: \n");
                        imp.printStackTrace();
                        System.out.println("impossible end!: \n");
                    }
                } finally {
                    setIdle();
                }
            }
        }.execute();
        
    }//GEN-LAST:event_editarButtonActionPerformed

    private void recargarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recargarButtonActionPerformed
        updateTable(true);
    }//GEN-LAST:event_recargarButtonActionPerformed

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        updateTable(false);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    private void guardarAlmacenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarAlmacenButtonActionPerformed
        productoField.putClientProperty("JComponent.outline", "");
        marcaField.putClientProperty("JComponent.outline", "");
        tipoProductoField.putClientProperty("JComponent.outline", "");
        stockField.putClientProperty("JComponent.outline", "");
        stockinicialField.putClientProperty("JComponent.outline", "");
        stockminimoField.putClientProperty("JComponent.outline", "");
        columnaField.putClientProperty("JComponent.outline", "");
        estanteriaField.putClientProperty("JComponent.outline", "");
        filaField.putClientProperty("JComponent.outline", "");

        Producto producto = new Producto();
        Almacen almacen = new Almacen();
        
        boolean error = false;

        if (marcaSeleccionada == null) {
            marcaField.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        if (tipoSeleccionado == null) {
            tipoProductoField.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        if (stockField.getText().isBlank()) {
            stockField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (stockinicialField.getText().isBlank()) {
            stockinicialField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (stockminimoField.getText().isBlank()) {
            stockminimoField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (columnaField.getText().isBlank()) {
            columnaField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (estanteriaField.getText().isBlank()) {
            estanteriaField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (filaField.getText().isBlank()) {
            filaField.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Long.parseLong(stockField.getText());
        } catch (NumberFormatException e) {
            stockField.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Integer.parseInt(stockinicialField.getText());
        } catch (NumberFormatException e) {
            stockinicialField.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Integer.parseInt(stockminimoField.getText());
        } catch (NumberFormatException e) {
            stockminimoField.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Integer.parseInt(columnaField.getText());
        } catch (NumberFormatException e) {
            columnaField.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Integer.parseInt(filaField.getText());
        } catch (NumberFormatException e) {
            filaField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (error) {
            return;
        } else {
            setBusy("Guardando producto...");
            producto.setDescripcion(descripcionProducto.getText());
            producto.setNombre(productoField.getText());
            producto.setMarca(marcaSeleccionada); 
            producto.setTipoProducto(tipoSeleccionado);
            almacen.setStock(Long.parseLong(stockField.getText()));
            almacen.setStockInicial(Integer.parseInt(stockinicialField.getText()));
            almacen.setStockMinimo(Integer.parseInt(stockminimoField.getText()));
            almacen.setColumna(Integer.parseInt(columnaField.getText()));
            almacen.setEstanteria(estanteriaField.getText());
            almacen.setFila(Integer.parseInt(filaField.getText()));
            almacen.setProducto(producto);
            producto.setAlmacen(almacen);
            
            new SwingWorker<Producto, Producto>() {
                @Override
                protected Producto doInBackground() throws Exception {
                    return productoService.nuevoProducto(producto);
                }

                @Override
                protected void done() {
                    try {
                        get(); //maybe check if it was correctly added?
                        setIdle();
                        clearNuevoProductoWindow();
                        nuevoProductoDialog.setVisible(false);
                    } catch (InterruptedException ex) {}
                    catch (ExecutionException ex) {
                        try {
                            throw ex.getCause();
                        } catch (NotEnoughPermissionsException e) {
                            OptionPaneService.errorMessage(nuevoProductoDialog, "No dispone de permisos suficientes para poder crear un nuevo producto.", "Sin permisos.");
                            return;
                        } catch (Throwable imp) {
                            System.out.println("impossible!: \n");
                            imp.printStackTrace();
                            System.out.println("impossible end!: \n");
                        }
                    }
                }
            }.execute();
        }
    }//GEN-LAST:event_guardarAlmacenButtonActionPerformed

    private void seleccionarMarca() {
        seleccionarMarcaDialog.setVisible(true);
    }
    
    private void seleccionarTipo() {
        seleccionarTipoProductoDialog.setVisible(true);
    }
    
    private void clearNuevoProductoWindow() {
        productoField.putClientProperty("JComponent.outline", "");
        marcaField.putClientProperty("JComponent.outline", "");
        tipoProductoField.putClientProperty("JComponent.outline", "");
        stockField.putClientProperty("JComponent.outline", "");
        stockinicialField.putClientProperty("JComponent.outline", "");
        stockminimoField.putClientProperty("JComponent.outline", "");
        columnaField.putClientProperty("JComponent.outline", "");
        estanteriaField.putClientProperty("JComponent.outline", "");
        filaField.putClientProperty("JComponent.outline", "");
        productoField.setText("");
        marcaField.setText("");
        tipoProductoField.setText("");
        stockField.setText("");
        stockinicialField.setText("");
        stockminimoField.setText("");
        columnaField.setText("");
        estanteriaField.setText("");
        filaField.setText("");
        descripcionProducto.setText("");
        editarDescripcionProducto.setText("");
        marcaSeleccionada = null;
        tipoSeleccionado = null;
        seleccionarMarca.setText("Seleccionar");
        seleccionarTipo.setText("Seleccionar");
    }
    
    private void cancelarCreacionProducto() {
        boolean isBlank = true;

        if (!productoField.getText().isBlank()) {
            isBlank = false;
        }

        if (marcaSeleccionada != null) {
            isBlank = false;
        }

        if (tipoSeleccionado != null) {
            isBlank = false;
        }

        if (!stockField.getText().isBlank()) {
            isBlank = false;
        }

        if (!stockinicialField.getText().isBlank()) {
            isBlank = false;
        }
        
        if (!stockminimoField.getText().isBlank()) {
            isBlank = false;
        }
        
        if (!columnaField.getText().isBlank()) {
            isBlank = false;
        }
        
        if (!descripcionProducto.getText().isBlank()) {
            isBlank = false;
        }
        
        if (isBlank) {
            nuevoProductoDialog.setVisible(false);
        }else {
            int ans = OptionPaneService.questionMessage(nuevoProductoDialog, "¿Desea salir sin guardar los cambios?", "Cambios sin guardar");
            if (ans == JOptionPane.YES_OPTION) {
                nuevoProductoDialog.setVisible(false);
                clearNuevoProductoWindow();
            }
        }
    }
    
    private void clearEdicionProductoWindow() {
        productoFieldEditar.putClientProperty("JComponent.outline", "");
        marcaFieldEditar.putClientProperty("JComponent.outline", "");
        tipoProductoFieldEditar.putClientProperty("JComponent.outline", "");
        stockFieldEditar.putClientProperty("JComponent.outline", "");
        stockinicialFieldEditar.putClientProperty("JComponent.outline", "");
        stockminimoFieldEditar.putClientProperty("JComponent.outline", "");
        columnaFieldEditar.putClientProperty("JComponent.outline", "");
        estanteriaFieldEditar.putClientProperty("JComponent.outline", "");
        filaFieldEditar.putClientProperty("JComponent.outline", "");
        productoFieldEditar.setText("");
        marcaFieldEditar.setText("");
        tipoProductoFieldEditar.setText("");
        stockFieldEditar.setText("");
        stockinicialFieldEditar.setText("");
        stockminimoFieldEditar.setText("");
        columnaFieldEditar.setText("");
        estanteriaFieldEditar.setText("");
        filaFieldEditar.setText("");
        editarDescripcionProducto.setText("");
        descripcionProducto.setText("");
        marcaSeleccionada = null;
        tipoSeleccionado = null;
        seleccionarMarcaEditar.setText("Seleccionar");
        seleccionarTipoEditar.setText("Seleccionar");
    }
    
    private void cancelarEdicionProducto() {
        boolean hasChanges = true;

        if (productoFieldEditar.getText().equals(productoSeleccionado.getNombre())) {
            hasChanges = false;
        }

        if (productoSeleccionado.getMarca().getIdMarca() == marcaSeleccionada.getIdMarca()) {
            hasChanges = false;
        }

        if (productoSeleccionado.getTipoProducto().getIdTipoProducto() == tipoSeleccionado.getIdTipoProducto()) {
            hasChanges = false;
        }

        if (stockFieldEditar.getText().equals(productoSeleccionado.getAlmacen().getStock().toString())) {
            hasChanges = false;
        }

        if (stockinicialFieldEditar.getText().equals(productoSeleccionado.getAlmacen().getStockInicial())) {
            hasChanges = false;
        }
        
        if (stockminimoFieldEditar.getText().equals(productoSeleccionado.getAlmacen().getStockMinimo())) {
            hasChanges = false;
        }
        
        if (columnaFieldEditar.getText().equals(productoSeleccionado.getAlmacen().getColumna())) {
            hasChanges = false;
        }
        
        if (estanteriaFieldEditar.getText().equals(productoSeleccionado.getAlmacen().getEstanteria())) {
            hasChanges = false;
        }
        
        if (filaFieldEditar.getText().equals(productoSeleccionado.getAlmacen().getFila())) {
            hasChanges = false;
        }
        
        if (editarDescripcionProducto.getText().equals(productoSeleccionado.getDescripcion())) {
            hasChanges = false;
        }
        
        if (hasChanges) {
            editarProductoDialog.setVisible(false);
        }else {
            int ans = OptionPaneService.questionMessage(editarProductoDialog, "¿Desea salir sin guardar los cambios?", "Cambios sin guardar");
            if (ans == JOptionPane.YES_OPTION) {
                editarProductoDialog.setVisible(false);
                clearEdicionProductoWindow();
            }
        }
    }   
    
    private void cancelarCreacionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarCreacionButtonActionPerformed
        cancelarEdicionProducto();
    }//GEN-LAST:event_cancelarCreacionButtonActionPerformed

    private void actualizarProductoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actualizarProductoButtonActionPerformed
        productoFieldEditar.putClientProperty("JComponent.outline", "");
        marcaFieldEditar.putClientProperty("JComponent.outline", "");
        tipoProductoFieldEditar.putClientProperty("JComponent.outline", "");
        stockFieldEditar.putClientProperty("JComponent.outline", "");
        stockinicialFieldEditar.putClientProperty("JComponent.outline", "");
        stockminimoFieldEditar.putClientProperty("JComponent.outline", "");
        columnaFieldEditar.putClientProperty("JComponent.outline", "");
        estanteriaFieldEditar.putClientProperty("JComponent.outline", "");
        filaFieldEditar.putClientProperty("JComponent.outline", "");

        Producto producto = productoSeleccionado;
        Almacen almacen = producto.getAlmacen();
        boolean error = false;

        if (marcaSeleccionada == null) {
            marcaFieldEditar.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        if (tipoSeleccionado == null) {
            tipoProductoFieldEditar.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        if (stockFieldEditar.getText().isBlank()) {
            stockFieldEditar.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (stockinicialFieldEditar.getText().isBlank()) {
            stockinicialFieldEditar.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (stockminimoFieldEditar.getText().isBlank()) {
            stockminimoFieldEditar.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (columnaFieldEditar.getText().isBlank()) {
            columnaFieldEditar.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (estanteriaFieldEditar.getText().isBlank()) {
            estanteriaFieldEditar.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (filaFieldEditar.getText().isBlank()) {
            filaFieldEditar.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Long.parseLong(stockFieldEditar.getText());
        } catch (NumberFormatException e) {
            stockFieldEditar.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Integer.parseInt(stockinicialFieldEditar.getText());
        } catch (NumberFormatException e) {
            stockinicialFieldEditar.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Integer.parseInt(stockminimoFieldEditar.getText());
        } catch (NumberFormatException e) {
            stockminimoFieldEditar.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Integer.parseInt(columnaFieldEditar.getText());
        } catch (NumberFormatException e) {
            columnaFieldEditar.putClientProperty("JComponent.outline", "error");
            error = true;
        }
        
        try {
            Integer.parseInt(filaFieldEditar.getText());
        } catch (NumberFormatException e) {
            filaFieldEditar.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (error) {
            return;
        } else {
            setBusy("Actualizando producto...");
            producto.setDescripcion(editarDescripcionProducto.getText());
            producto.setNombre(productoFieldEditar.getText());
            producto.setMarca(marcaSeleccionada); 
            producto.setTipoProducto(tipoSeleccionado);
            almacen.setStock(Long.parseLong(stockFieldEditar.getText()));
            almacen.setStockInicial(Integer.parseInt(stockinicialFieldEditar.getText()));
            almacen.setStockMinimo(Integer.parseInt(stockminimoFieldEditar.getText()));
            almacen.setColumna(Integer.parseInt(columnaFieldEditar.getText()));
            almacen.setEstanteria(estanteriaFieldEditar.getText());
            almacen.setFila(Integer.parseInt(filaFieldEditar.getText()));
            almacen.setProducto(producto);
            producto.setAlmacen(almacen);
            
            new SwingWorker<Producto, Producto>() {
                @Override
                protected Producto doInBackground() throws Exception {
                    return productoService.actualizarProducto(producto);
                }

                @Override
                protected void done() {
                    try {
                        get();
                        setIdle();
                        clearEdicionProductoWindow();
                        editarProductoDialog.setVisible(false);
                    } catch (InterruptedException ex) {}
                    catch (ExecutionException ex) {
                        try {
                            throw ex.getCause();
                        } catch (NotEnoughPermissionsException e) {
                            OptionPaneService.errorMessage(editarProductoDialog, "No dispone de permisos suficientes para poder crear un nuevo producto.", "Sin permisos.");
                        } catch (Throwable imp) {
                            System.out.println("impossible!: \n");
                            imp.printStackTrace();
                            System.out.println("impossible end!: \n");
                        }
                    }
                }
            }.execute();
        }
    }//GEN-LAST:event_actualizarProductoButtonActionPerformed

    private void cancelarEdicionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarEdicionButtonActionPerformed
        cancelarEdicionProducto();
    }//GEN-LAST:event_cancelarEdicionButtonActionPerformed

    private void seleccionarMarcaEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarMarcaEditarActionPerformed
        seleccionarMarca();
    }//GEN-LAST:event_seleccionarMarcaEditarActionPerformed

    private void seleccionarMarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarMarcaActionPerformed
        seleccionarMarca();
    }//GEN-LAST:event_seleccionarMarcaActionPerformed

    private void seleccionarTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarTipoActionPerformed
        seleccionarTipo();
    }//GEN-LAST:event_seleccionarTipoActionPerformed

    private void nuevoProductoDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_nuevoProductoDialogWindowClosing
        cancelarCreacionProducto();
    }//GEN-LAST:event_nuevoProductoDialogWindowClosing

    private void editarProductoDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_editarProductoDialogWindowClosing
        cancelarEdicionProducto();
    }//GEN-LAST:event_editarProductoDialogWindowClosing

    private void nuevaMarcaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevaMarcaButtonActionPerformed
        nuevaMarcaDialog.setVisible(true);
    }//GEN-LAST:event_nuevaMarcaButtonActionPerformed

    private void reloadTableMarcaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadTableMarcaButtonActionPerformed
        retrieveMarcaData(true);
    }//GEN-LAST:event_reloadTableMarcaButtonActionPerformed

    private void loadMoreMarcaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreMarcaButtonActionPerformed
        retrieveMarcaData(false);
    }//GEN-LAST:event_loadMoreMarcaButtonActionPerformed

    private void loadMoreTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreTipoButtonActionPerformed
        retrieveTipoData(false);
    }//GEN-LAST:event_loadMoreTipoButtonActionPerformed

    private void reloadTableTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadTableTipoButtonActionPerformed
        retrieveTipoData(true);
    }//GEN-LAST:event_reloadTableTipoButtonActionPerformed

    private void seleccionarTipoEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarTipoEditarActionPerformed
        seleccionarTipo();
    }//GEN-LAST:event_seleccionarTipoEditarActionPerformed

    private void cancelarCreacionClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarCreacionClienteButtonActionPerformed
        verifyEmptyNuevaMarca();
    }//GEN-LAST:event_cancelarCreacionClienteButtonActionPerformed

    private void guardarMarcaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarMarcaButtonActionPerformed
        nombreMarcaField.putClientProperty("JComponent.outline", "");

        Marca marca = new Marca();
        boolean error = false;

        if (nombreMarcaField.getText().isBlank()) {
            nombreMarcaField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (error) {
            return;
        } else {
            setBusy("Guardando nueva marca...");
            marca.setNombreMarca(nombreMarcaField.getText());

            new SwingWorker<Marca, Marca>(){
                @Override
                protected Marca doInBackground() throws Exception {
                    return marcaService.nuevaMarca(marca);
                }

                @Override
                protected void done() {
                    try {
                        get();
                        nuevaMarcaDialog.setVisible(false);
                    } catch (InterruptedException ex) {
                    } catch (ExecutionException ex) {
                        try {
                            throw ex.getCause();
                        } catch (NotEnoughPermissionsException e) {
                            return;
                        } catch (Throwable imp) {
                            System.out.println("impossible!: \n");
                            imp.printStackTrace();
                            System.out.println("impossible end!: \n");
                        }
                    } finally {
                        setIdle();
                    }
                }

            }.execute();
        }
    }//GEN-LAST:event_guardarMarcaButtonActionPerformed

    private void nuevaMarcaDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_nuevaMarcaDialogWindowClosing
        verifyEmptyNuevaMarca();
    }//GEN-LAST:event_nuevaMarcaDialogWindowClosing

    private void guardarTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarTipoButtonActionPerformed
        tipoField.putClientProperty("JComponent.outline", "");

        TipoProducto tp = new TipoProducto();
        boolean error = false;

        if (tipoField.getText().isBlank()) {
            tipoField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (error) {
            return;
        } else {
            setBusy("Guardando nuevo tipo de producto...");
            tp.setTipo(tipoField.getText());

            new SwingWorker<TipoProducto, TipoProducto>(){
                @Override
                protected TipoProducto doInBackground() throws Exception {
                    return tipoproductoService.nuevoTipoProducto(tp);
                }

                @Override
                protected void done() {
                    try {
                        get();
                        nuevoTipoDialog.setVisible(false);
                    } catch (InterruptedException ex) {
                    } catch (ExecutionException ex) {
                        try {
                            throw ex.getCause();
                        } catch (NotEnoughPermissionsException e) {
                            Logger.getLogger(MarcaTab.class.getName()).log(Level.SEVERE, null, e);
                            return;
                        } catch (Throwable imp) {
                            System.out.println("impossible!: \n");
                            imp.printStackTrace();
                            System.out.println("impossible end!: \n");
                        }
                    } finally {
                        setIdle();
                    }
                }

            }.execute();
        }
    }//GEN-LAST:event_guardarTipoButtonActionPerformed

    private void cancelarTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarTipoButtonActionPerformed
        verifyEmptyNuevoTipo();
    }//GEN-LAST:event_cancelarTipoButtonActionPerformed

    private void nuevoTipoDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_nuevoTipoDialogWindowClosing
        verifyEmptyNuevoTipo();
    }//GEN-LAST:event_nuevoTipoDialogWindowClosing

    private void seleccionarMarcaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarMarcaButtonActionPerformed
        setBusy("Seleccionando marca...");
        final long selectedId = getMarcaSelectedId();
        new SwingWorker<Marca, Marca>() {
            @Override
            protected Marca doInBackground() throws Exception {
                return marcaService.encontrarMarcaPorId(selectedId);
            }

            @Override
            protected void done() {
                try {
                    Marca marca = get();
                    marcaSeleccionada = marca;
                    marcaField.setText(marca.getNombreMarca());
                    marcaFieldEditar.setText(marca.getNombreMarca());
                    seleccionarMarca.setText("Deseleccionar");
                    seleccionarMarcaEditar.setText("Deseleccionar");
                    setIdle();
                    seleccionarMarcaDialog.setVisible(false);
                } catch (InterruptedException ex) {
                } catch (ExecutionException ex) {
                    try {
                        throw ex.getCause();
                    } catch (NotEnoughPermissionsException e) {
                    } catch (Throwable imp) {
                        System.out.println("impossible!: \n");
                        imp.printStackTrace();
                        System.out.println("impossible end!: \n");
                    }
                } finally {
                    setIdle();
                }
                
            }
        }.execute();
    }//GEN-LAST:event_seleccionarMarcaButtonActionPerformed

    private void seleccionarTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarTipoButtonActionPerformed
        final long selectedId = getTipoSelectedId();
        setBusy("Seleccionando tipo de producto...");
        new SwingWorker<TipoProducto, TipoProducto>() {
            @Override
            protected TipoProducto doInBackground() throws Exception {
                return tipoproductoService.encontrarTipoProductoPorId(selectedId);
            }

            @Override
            protected void done() {
                try {
                    TipoProducto tp = get();
                    tipoSeleccionado = tp;
                    tipoProductoField.setText(tp.getTipo());
                    tipoProductoFieldEditar.setText(tp.getTipo());
                    seleccionarTipo.setText("Deseleccionar");
                    seleccionarTipoEditar.setText("Deseleccionar");
                    setIdle();
                    seleccionarTipoProductoDialog.setVisible(false);
                } catch (InterruptedException ex) {
                } catch (ExecutionException ex) {
                    try {
                        throw ex.getCause();
                    } catch (NotEnoughPermissionsException e) {
                    } catch (Throwable imp) {
                        System.out.println("impossible!: \n");
                        imp.printStackTrace();
                        System.out.println("impossible end!: \n");
                    }
                } finally {
                    setIdle();
                }
            }
        }.execute();
        
    }//GEN-LAST:event_seleccionarTipoButtonActionPerformed

    private void nuevoTipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoTipoButtonActionPerformed
        nuevoTipoDialog.setVisible(true);
    }//GEN-LAST:event_nuevoTipoButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton actualizarProductoButton;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarCreacionButton;
    private javax.swing.JButton cancelarCreacionClienteButton;
    private javax.swing.JButton cancelarEdicionButton;
    private javax.swing.JButton cancelarTipoButton;
    private javax.swing.JTextField columnaField;
    private javax.swing.JTextField columnaFieldEditar;
    private javax.swing.JLabel contadorLabel;
    private javax.swing.JTextArea descripcionProducto;
    private javax.swing.JButton editarButton;
    private javax.swing.JTextArea editarDescripcionProducto;
    private javax.swing.JScrollPane editarDescripcionScrollPane;
    private javax.swing.JDialog editarProductoDialog;
    private javax.swing.JLabel editarProductoLabel;
    private javax.swing.JTextField estanteriaField;
    private javax.swing.JTextField estanteriaFieldEditar;
    private javax.swing.JTextField filaField;
    private javax.swing.JTextField filaFieldEditar;
    private javax.swing.JButton guardarAlmacenButton;
    private javax.swing.JButton guardarMarcaButton;
    private javax.swing.JButton guardarTipoButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JLayeredPane layeredPane;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JButton loadMoreMarcaButton;
    private javax.swing.JButton loadMoreTipoButton;
    private javax.swing.JTextField marcaField;
    private javax.swing.JTextField marcaFieldEditar;
    private javax.swing.JLabel maxCaracteresLabel;
    private javax.swing.JLabel maxCaracteresLabel1;
    private javax.swing.JTextField nombreMarcaField;
    private javax.swing.JLabel nombreMarcaLabel;
    private javax.swing.JButton nuevaMarcaButton;
    private javax.swing.JDialog nuevaMarcaDialog;
    private javax.swing.JLabel nuevaMarcaLabel;
    private javax.swing.JButton nuevoButton;
    private javax.swing.JScrollPane nuevoDescripcionScrollPane;
    private javax.swing.JDialog nuevoProductoDialog;
    private javax.swing.JLabel nuevoProductoLabel;
    private javax.swing.JButton nuevoTipoButton;
    private javax.swing.JDialog nuevoTipoDialog;
    private javax.swing.JLabel nuevoTipoLabel;
    private javax.swing.JTextField productoField;
    private javax.swing.JTextField productoFieldEditar;
    private javax.swing.JButton recargarButton;
    private javax.swing.JButton reloadTableMarcaButton;
    private javax.swing.JButton reloadTableTipoButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JScrollPane scrollPaneMarca;
    private javax.swing.JScrollPane scrollPaneTipo;
    private javax.swing.JButton seleccionarMarca;
    private javax.swing.JButton seleccionarMarcaButton;
    private javax.swing.JDialog seleccionarMarcaDialog;
    private javax.swing.JButton seleccionarMarcaEditar;
    private javax.swing.JButton seleccionarTipo;
    private javax.swing.JButton seleccionarTipoButton;
    private javax.swing.JButton seleccionarTipoEditar;
    private javax.swing.JDialog seleccionarTipoProductoDialog;
    private javax.swing.JTextField stockField;
    private javax.swing.JTextField stockFieldEditar;
    private javax.swing.JTextField stockinicialField;
    private javax.swing.JTextField stockinicialFieldEditar;
    private javax.swing.JTextField stockminimoField;
    private javax.swing.JTextField stockminimoFieldEditar;
    private org.jdesktop.swingx.JXTable tablaMarca;
    private org.jdesktop.swingx.JXTable tablaTipoTab;
    private javax.swing.JLabel tableInformationLabel;
    private org.jdesktop.swingx.JXTable tableProductos;
    private javax.swing.JTextField tipoField;
    private javax.swing.JTextField tipoProductoField;
    private javax.swing.JTextField tipoProductoFieldEditar;
    // End of variables declaration//GEN-END:variables
    
    DefaultTableModel defaultTableModelMarca = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Long.class;
                default:
                    return String.class;
            }
        }
    };
    String[] columnNamesMarca = {"ID", "Marca"};
    ListSelectionModel selectionMarcaModel;
    private boolean retrievingMarcaData = false;
    private long lastIdMarca = 0;

    private long limitMarca = 100;
    
    @Autowired
    private MarcaService marcaService;
    
    private void verifyEmptyNuevaMarca() {
        if(nombreMarcaField.getText().isEmpty()){
            nuevaMarcaDialog.setVisible(false);
        } else {
            int answ = OptionPaneService.questionMessage(nuevaMarcaDialog, "¿Desea cerrar sin guardar?", "Cerrar sin guardar");
            if (answ == JOptionPane.YES_OPTION) {
                nuevaMarcaDialog.setVisible(false);
            }
        }
    }
    
    private void initMarcaTable() {
        defaultTableModelMarca.setColumnIdentifiers(columnNamesMarca);
        tablaMarca.setModel(defaultTableModelMarca);
        scrollPaneMarca.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            if (retrievingMarcaData) {
                return;
            }
            int maxValue = scrollPaneMarca.getVerticalScrollBar().getMaximum() - scrollPaneMarca.getVerticalScrollBar().getVisibleAmount();
            int currentValue = scrollPaneMarca.getVerticalScrollBar().getValue();
            float fraction = (float) currentValue / (float) maxValue;
            if (fraction > 0.999f) {
                retrieveMarcaData(false);
                System.out.println("Scroll bar is near the bottom");
            }
        });
        selectionMarcaModel = tablaMarca.getSelectionModel();
        selectionMarcaModel.addListSelectionListener((ListSelectionEvent e) -> {
            if (selectionMarcaModel.getSelectedItemsCount() == 1) {
                seleccionarMarcaButton.setEnabled(true);
            } else {
                seleccionarMarcaButton.setEnabled(false);
            }
        });
        nuevaMarcaDialog.pack();
        nuevaMarcaDialog.setLocationRelativeTo(this);
    }
    
    private void retrieveMarcaData(boolean reload) {
        if (!canRead) {
            setBusy("Sin permisos suficientes para leer datos.");
            return;
        }
        setBusy("Cargando...");
        reloadTableMarcaButton.setEnabled(false);
        loadMoreMarcaButton.setEnabled(false);
        retrievingMarcaData = true;

        if (reload) {
            defaultTableModelMarca.setRowCount(0);
            lastIdMarca = 0;
            setBusy("Recargando...");
        }

        new SwingWorker<List<Marca>, List<Marca>>() {
            @Override
            protected List<Marca> doInBackground() throws Exception {
                return marcaService.streamMarca(lastIdMarca, limitMarca); // set lastId and configurable limit
            }

            @Override
            protected void done() {
                try {
                    var marcas = get();
                    for (Marca marca : marcas) {
                        Object[] values = new Object[2];
                        values[0] = marca.getIdMarca();
                        values[1] = marca.getNombreMarca();
                        defaultTableModelMarca.addRow(values);
                    }
                    int lastRow = 0;
                    int rowCount = defaultTableModelMarca.getRowCount();
                    if (rowCount != 0) {
                        lastRow = rowCount - 1;
                    }
                    var id = defaultTableModelMarca.getValueAt(lastRow, 0);
                    lastIdMarca = Long.parseLong(id.toString());
                } catch (InterruptedException | ExecutionException ex) {
                }
                setIdle();
                reloadTableMarcaButton.setEnabled(true);
                loadMoreMarcaButton.setEnabled(true);
                retrievingMarcaData = false;
            }
        }.execute();
    }
    
    private long getMarcaSelectedId() {
        List<Long> idMarcas = new ArrayList<>();
        for (int i : selectionMarcaModel.getSelectedIndices()) { //rows 
            i = tablaMarca.convertRowIndexToModel(i);
            idMarcas.add((Long) defaultTableModelMarca.getValueAt(i, 0));
        }
        return idMarcas.get(0);
    }
    //-----------------------------------------------------------------------------------------
    
    DefaultTableModel defaultTableModelTipoProducto = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Long.class;
                default:
                    return String.class;
            }
        }
    };
    String[] columnNamesTipo = {"ID", "Tipo de Producto"};
    
    private boolean retrievingTipoData = false;
    private long lastTipoId = 0;
    ListSelectionModel selectionTipoModel;
    private long tipoLimit = 100;
    
    @Autowired
    private TipoProductoService tipoproductoService;
    
    private void initTipoTable() {
        defaultTableModelTipoProducto.setColumnIdentifiers(columnNamesTipo);
        tablaTipoTab.setModel(defaultTableModelTipoProducto);
        scrollPaneTipo.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            if (retrievingTipoData) {
                return;
            }
            int maxValue = scrollPaneTipo.getVerticalScrollBar().getMaximum() - scrollPaneTipo.getVerticalScrollBar().getVisibleAmount();
            int currentValue = scrollPaneTipo.getVerticalScrollBar().getValue();
            float fraction = (float) currentValue / (float) maxValue;
            if (fraction > 0.999f) {
                retrieveTipoData(false);
            }
        });
        selectionTipoModel = tablaTipoTab.getSelectionModel();
        selectionTipoModel.addListSelectionListener((ListSelectionEvent e) -> {
             if (selectionTipoModel.getSelectedItemsCount() == 1) {
                seleccionarTipoButton.setEnabled(true);
            } else {
                seleccionarTipoButton.setEnabled(false);
            }
        });
        setIdle();
        nuevoTipoDialog.pack();
        nuevoTipoDialog.setLocationRelativeTo(this);
    }
    
    private void retrieveTipoData(boolean reload) {
        if (!canRead) {
            setBusy("Sin permisos suficientes para leer datos.");
            return;
        }
        setBusy("Cargando...");
        reloadTableTipoButton.setEnabled(false);
        loadMoreTipoButton.setEnabled(false);
        retrievingTipoData = true;
        int oldRowCount = defaultTableModelTipoProducto.getRowCount();
        if (reload) {
            defaultTableModelTipoProducto.setRowCount(0);
            lastTipoId = 0;
            setBusy("Recargando...");
        }
        new SwingWorker<List<TipoProducto>, List<TipoProducto>>() {
            @Override
            protected List<TipoProducto> doInBackground() throws Exception {
                if (reload) {
                    return tipoproductoService.streamTipoProducto(lastTipoId, (long) oldRowCount);
                }
                return tipoproductoService.streamTipoProducto(lastTipoId, tipoLimit); 
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
                    lastTipoId = Long.parseLong(id.toString());
                } catch (InterruptedException | ExecutionException ex) {
                }
                setIdle();
                reloadTableTipoButton.setEnabled(true);
                loadMoreTipoButton.setEnabled(true);
                retrievingTipoData = false;
            }
        }.execute();
    }
    
    private void verifyEmptyNuevoTipo() {
        if(tipoField.getText().isEmpty()){
            nuevoTipoDialog.setVisible(false);
        } else {
            int answ = OptionPaneService.questionMessage(nuevoTipoDialog, "¿Desea cerrar sin guardar?", "Cerrar sin guardar");
            if (answ == JOptionPane.YES_OPTION) {
                nuevoTipoDialog.setVisible(false);
            }
        }
    }
    
    private long getTipoSelectedId() {
        List<Long> ids = new ArrayList<>();
        for (int i : selectionTipoModel.getSelectedIndices()) { //rows 
            //System.out.println(i);
            i = tablaTipoTab.convertRowIndexToModel(i); //IMPORTANTISIMO, en caso de que la tabla esté ordenada por alguna columna, esto devolvera siempre la fila seleccionada.
            ids.add((Long) defaultTableModelTipoProducto.getValueAt(i, 0));
        }
        return ids.get(0);
    }
}
