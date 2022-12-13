package com.utp.trabajo.gui.view.ventas;

import com.formdev.flatlaf.FlatClientProperties;
import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.gui.view.clientes.ClientesTab;
import com.utp.trabajo.model.entities.Cliente;
import com.utp.trabajo.model.entities.Comprobante;
import com.utp.trabajo.model.entities.DetallesVenta;
import com.utp.trabajo.model.entities.MetodoPago;
import com.utp.trabajo.model.entities.Producto;
import com.utp.trabajo.model.entities.Venta;
import com.utp.trabajo.services.ClienteService;
import com.utp.trabajo.services.ProductoService;
import com.utp.trabajo.services.VentasService;
import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.util.IconService;
import com.utp.trabajo.services.util.OptionPaneService;
import com.utp.trabajo.services.util.Utils;
import hu.akarnokd.rxjava3.swing.SwingObservable;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import javax.swing.ButtonGroup;
import javax.swing.DefaultSingleSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXRootPane;
import org.springframework.beans.factory.annotation.Autowired;

public class NuevaVentaWindow extends org.jdesktop.swingx.JXPanel {

    private DefaultTableModel defaultTableModelClientes = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
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
    private String[] columnNames = {"ID", "Nombre", "Dirección", "DNI/RUC", "Teléfono", "Estado civil", "N° compras"};
    //TODO: set minimum and default sizes for each column

    private ListSelectionModel clienteSelectionModel;

    private boolean retrievingData = false;

    private long lastId = 1;

    private long rowsPerUpdate = 100;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private VentasService ventaService;

    @Autowired
    private SecurityService securityService;

    private DefaultTableModel nuevaVentaTableModel = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Long.class;
                case 1:
                    return String.class;
                case 2:
                    return Integer.class;
                case 3:
                    return Double.class;
                case 4:
                    return Double.class;
                default:
                    return String.class;
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 2 || column == 4;
        }
    };

    private String columnHeaders[] = {"ID", "Producto", "Cantidad", "Precio", "Total"};

    private ListSelectionModel nuevaVentaSelectionModel;

    private DefaultSingleSelectionModel searchPopupModel = new DefaultSingleSelectionModel();

    private List<ProductoSeleccionado> productos = new ArrayList<>();

    private SpinnerNumberModel precioSpinnerModel;

    private SpinnerNumberModel cantidadSpinnerModel;

    private Producto selectedProducto = null;

    private boolean isProductoSelected = false;

    private Cliente emptyCliente;

    private Cliente selectedCliente = null;

    private boolean isSearching = false;

    private BigDecimal totalCompra;

    private JButton searchProductButton;
    private ButtonGroup buscarProductoButtonGroup;
    private JToggleButton findByIdButton;
    private JToggleButton findByNameButton;

    @Autowired
    private IconService iconService;

    @Autowired
    private ProductoService productoService;

    public NuevaVentaWindow() {
        initComponents();
        initVentaTable();
        initClientesTable();
        initProductSpinners();
        setIdle();
        productoSearchField.requestFocusInWindow();
        totalField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, new JLabel(" S/. "));
        agregarProductoButton.setEnabled(false);
        totalCompra = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        nuevoClienteDialog.pack();
        nuevoClienteDialog.setLocationRelativeTo(this);
        emptyCliente = new Cliente();
        emptyCliente.setIdCliente(1L);
        
    }

    @PostConstruct
    private void init() {
//        checkPermissions();
//        updateTable(false); // mover hacia un listener que verifique que se ha abierto el jPanel
        initSearchButtons();
        new SwingWorker<Cliente, Cliente>() {
            @Override
            protected Cliente doInBackground() throws Exception {
                setBusy("Cargando...");
                return clienteService.encontrarClientePorId(1L);
            }

            @Override
            protected void done() {
                try {
                    emptyCliente = get();
                } catch (InterruptedException ex) {
                    Logger.getLogger(NuevaVentaWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(NuevaVentaWindow.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    setIdle();
                }
            }
            
        }.execute();
    }

    private void updateTotal() {
        priceLabel.setText("TOTAL : S/. " + totalCompra.toPlainString());
        int totalProductos = 0;
        for (ProductoSeleccionado producto : productos) {
            totalProductos += producto.cantidad;
        } // cant use forEach() WHYY LMFAO
        cantidadProductos.setText(productos.size() + " ITEMS - TOTAL: " + totalProductos);
    }

    private boolean isClienteSelected() {
        return selectedCliente != null;
    }

    private void initClientesTable() {
        defaultTableModelClientes.setColumnIdentifiers(columnNames);
        tablaClientes.setModel(defaultTableModelClientes);
        tablaClientes.getColumnModel().getColumn(0).setMaxWidth(50);
        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(50);

        scrollPane2.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            if (retrievingData) {
                return;
            }
            int maxValue = scrollPane2.getVerticalScrollBar().getMaximum() - scrollPane2.getVerticalScrollBar().getVisibleAmount();
            int currentValue = scrollPane2.getVerticalScrollBar().getValue();
            float fraction = (float) currentValue / (float) maxValue;
            if (fraction > 0.999f) {
                updateTable(false);
                System.out.println("Scroll bar is near the bottom");
            }
        });
        clienteSelectionModel = tablaClientes.getSelectionModel();
        clienteSelectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            if (!clienteSelectionModel.isSelectionEmpty()) {
                seleccionarClienteButtonDialog.setEnabled(true);
            } else {
                seleccionarClienteButtonDialog.setEnabled(false);
            }

            if (clienteSelectionModel.getSelectedItemsCount() == 1) {
                seleccionarClienteButtonDialog.setEnabled(true);
            } else {
                seleccionarClienteButtonDialog.setEnabled(false);
            }

        });
        setIdle();
        seleccionarClienteButtonDialog.setEnabled(false);

        jLayeredPane1.removeAll();
        jLayeredPane1.setLayer(tableInformationLabel, javax.swing.JLayeredPane.DEFAULT_LAYER, 0);
        jLayeredPane1.setLayer(scrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER, -1);

        seleccionarClienteDialog.pack();
        seleccionarClienteDialog.setLocationRelativeTo(this);
    }

    private void addDataToTable(List<Cliente> data) {
        data.forEach(cliente -> {
            Vector vec = new Vector();
            vec.add(cliente.getIdCliente());
            vec.add(cliente.getNombre());
            vec.add(cliente.getDireccion());
            vec.add(cliente.getIdentificacion());
            vec.add(cliente.getTelefono());
            vec.add(cliente.getEstadoCivil());
            vec.add(cliente.getNumeroCompras());
            defaultTableModelClientes.addRow(vec);
        });
    }

    private void updateTable(boolean reload) { //refactor!  DONE!
        tableInformationLabel.setVisible(false);

        setBusy("Cargando clientes...");
        reloadTableButton.setEnabled(false);
        loadMoreButton.setEnabled(false);
        retrievingData = true;
        int oldRowCount = defaultTableModelClientes.getRowCount();
        if (reload) {
            defaultTableModelClientes.setRowCount(0);
            lastId = 1;
            setBusy("Recargando tabla...");
        }

        SwingWorker worker2 = new SwingWorker<Long, Long>() {
            @Override
            protected Long doInBackground() throws Exception {
                return clienteService.contarClientes();
            }

            @Override
            protected void done() {
                try {
                    Long cantidadClientesDatabase = get();
                    int cantidadClientesTabla = defaultTableModelClientes.getRowCount();
                    contadorClientesLabel.setText("Mostrando: " + cantidadClientesTabla + "/" + cantidadClientesDatabase);

                } catch (InterruptedException ex) {
                    try {
                        throw ex.getCause();
                    } catch (NotEnoughPermissionsException e) {
                        //Joption pane or do nothing!
                    } catch (Throwable e) {
                        System.out.println("impossible :");
                        e.printStackTrace();
                        System.out.println("impossible end");
                        return;
                    }
                } catch (ExecutionException ex) {
                    Logger.getLogger(ClientesTab.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };
        SwingWorker obtenerClientesWorker = new SwingWorker<List<Cliente>, List<Cliente>>() {
            @Override
            protected List<Cliente> doInBackground() throws Exception {
                // set lastId and configurable rowsPerUpdate if reloading just reload all data
                if (reload) {
                    return clienteService.streamClientes(lastId, (long) oldRowCount);
                }
                return clienteService.streamClientes(lastId, rowsPerUpdate);
            }

            @Override
            protected void done() {
                try {
                    addDataToTable(get());
                    int lastRow = 0;
                    int rowCount = defaultTableModelClientes.getRowCount();
                    if (rowCount != 0) {
                        lastRow = rowCount - 1;
                    }
                    var id = defaultTableModelClientes.getValueAt(lastRow, 0);
                    lastId = Long.parseLong(id.toString());

                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(ClientesTab.class.getName()).log(Level.SEVERE, null, ex);
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
        };
        obtenerClientesWorker.execute();

    }

    private void initVentaTable() {
        nuevaVentaTableModel.setColumnIdentifiers(columnHeaders);
        detalleVentaTable.setModel(nuevaVentaTableModel);

        nuevaVentaSelectionModel = detalleVentaTable.getSelectionModel();
        nuevaVentaSelectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            if (!nuevaVentaSelectionModel.isSelectionEmpty()) {
                eliminarProducto.setEnabled(true);
            } else {
                eliminarProducto.setEnabled(false);
            }

        });
        detalleVentaTable.getColumn(0).setPreferredWidth(50);
        detalleVentaTable.getColumn(0).setMaxWidth(50);
        detalleVentaTable.getColumn(1).setPreferredWidth(300);
        detalleVentaTable.getTableHeader().setReorderingAllowed(false);
        detalleVentaTable.getTableHeader().setEnabled(false);
        eliminarProducto.setEnabled(false);
    }

    private void initProductSpinners() {

        cantidadSpinnerModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        cantidadSpinner.setModel(cantidadSpinnerModel);
        JComponent editor1 = cantidadSpinner.getEditor();
        JFormattedTextField field = (JFormattedTextField) editor1.getComponent(0);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);

        SwingObservable.change(cantidadSpinnerModel)
            .map(JSpinner -> (Number) cantidadSpinner.getValue())
            .subscribe(value -> {
                Number precio = (Number) precioSpinner.getValue();
                double result = value.doubleValue() * precio.doubleValue();
                totalField.setText(String.format("%.2f", result));
            });

        precioSpinnerModel = new SpinnerNumberModel(0.0, 0.0, Double.MAX_VALUE, 0.01);
        precioSpinner.setModel(precioSpinnerModel);
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) precioSpinner.getEditor();
        JComponent editor2 = precioSpinner.getEditor();
        JFormattedTextField field2 = (JFormattedTextField) editor2.getComponent(0);
        DefaultFormatter formatter2 = (DefaultFormatter) field2.getFormatter();
        formatter2.setCommitsOnValidEdit(true);

        DecimalFormat format = editor.getFormat();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        format.setCurrency(Currency.getInstance("PEN"));

        SwingObservable.change(precioSpinnerModel)
            .map(JSpinner -> (Number) precioSpinner.getValue())
            .subscribe(value -> {
                Number cantidad = (Number) cantidadSpinner.getValue();
                double result = value.doubleValue() * cantidad.doubleValue();
                totalField.setText(String.format("%.2f", result));
            });
    }

    private void initSearchButtons() {
        buscarProductoButtonGroup = new ButtonGroup();

        findByIdButton = new JToggleButton();
        findByIdButton.setText("ID");
        findByIdButton.setToolTipText("Buscar usando el ID del producto");
        findByIdButton.setSelected(true);
        findByIdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productoSearchField.setText("");
            }
        });
        buscarProductoButtonGroup.add(findByIdButton);

        findByNameButton = new JToggleButton();
        findByNameButton.setText("Nombre");
        findByNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productoSearchField.setText("");
            }
        });
        buscarProductoButtonGroup.add(findByNameButton);
        findByNameButton.setToolTipText("Buscar usando el nombre del producto");

        JToolBar buscarProductoToolBar = new JToolBar();
        buscarProductoToolBar.add(findByIdButton);
        buscarProductoToolBar.add(findByNameButton);

        searchProductButton = new JButton(iconService.iconoEsperandoBusqueda);

        productoSearchField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, searchProductButton);
        productoSearchField.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, buscarProductoToolBar);
        productoSearchField.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        productoSearchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar productos");

        popupMenu.setFocusable(false);

        SwingObservable.document(productoSearchField.getDocument())
            .skipWhile(documentEvent -> isSearching)
            .doOnNext(documentEvent -> isSearching = true)
            .debounce(500, TimeUnit.MILLISECONDS)
            .skipWhile(documentEvent -> isProductoSelected)
            .map(documentEvent -> productoSearchField.getText())
            .subscribe(this::searchProducts);

    }

    private void selectProducto(Producto producto) {

        popupMenu.setVisible(false);
        cantidadSpinner.requestFocusInWindow();

        isProductoSelected = true;
        long currentStock = producto.getAlmacen().getStock();

        stockRestanteField.setText("" + currentStock);

        selectedProducto = producto;

        cantidadSpinnerModel.setMaximum(new Comparable() {
            @Override
            public int compareTo(Object o) {
                return Integer.compare((int) currentStock, (Integer) o);
            }
        });

        productoSearchField.setText(producto.getNombre() + " " + producto.getMarca().getNombreMarca());

        long minimumStockPlus10percent = producto.getAlmacen().getStockMinimo() + Math.round((double) currentStock * (0.1));
        System.out.println(minimumStockPlus10percent);
        if (currentStock < producto.getAlmacen().getStockMinimo()) {
            stockRestanteField.putClientProperty("JComponent.outline", "error");
        } else if (currentStock < minimumStockPlus10percent && currentStock >= producto.getAlmacen().getStockMinimo()) {
            stockRestanteField.putClientProperty("JComponent.outline", "warning");
        } else {
            stockRestanteField.putClientProperty("JComponent.outline", Color.GREEN.darker());
        }

        agregarProductoButton.setEnabled(true);

    }

    private void deselectProducto() {
        selectedProducto = null;
        stockRestanteField.putClientProperty("JComponent.outline", "");
        cantidadSpinnerModel.setMaximum(new Comparable() {
            @Override
            public int compareTo(Object o) {
                return Integer.compare(Integer.MAX_VALUE, (Integer) o);
            }
        });
        stockRestanteField.setText("");
        cantidadSpinnerModel.setValue(1);
        precioSpinnerModel.setValue(0);
        agregarProductoButton.setEnabled(false);
        productoSearchField.setText("");
        isProductoSelected = false;
        productoSearchField.requestFocusInWindow();
    }

    private void searchProducts(String text) {

        if (isProductoSelected) {
            return;
        }

        productoSearchField.putClientProperty("JComponent.outline", "");
        popupMenu.removeAll();
        popupMenu.setEnabled(true);

        if (text.isBlank()) {
            popupMenu.setVisible(false);
            popupMenu.removeAll();
            return;
        }
        isSearching = true;
        popupMenu.setFocusable(true);
        if (findByIdButton.isSelected()) {
            setBusy("Buscando producto...");
            searchProductButton.setIcon(iconService.iconoBuscando);

            long id = 0;

            try {
                id = Long.parseLong(text);
            } catch (NumberFormatException e) {
                productoSearchField.putClientProperty("JComponent.outline", "error");
                setIdle();
                return;
            }
            final long idProducto = id;

            SwingWorker searchProductoWorker = new SwingWorker<Producto, Producto>() {
                @Override
                protected Producto doInBackground() throws Exception {
                    return productoService.encontrarProductoPorId(idProducto);
                }

                @Override
                protected void done() {
                    try {
                        Producto producto = get();
                        popupMenu.removeAll();
                        popupMenu.add(producto.getIdProducto() + " - " + producto.getNombre() + " " + producto.getMarca().getNombreMarca() + " - Stock: " + producto.getAlmacen().getStock())
                            .addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    selectProducto(producto);
                                }
                            });
                        popupMenu.pack();
                        popupMenu.show(productoSearchField, 0, productoSearchField.getHeight() + 2);
                        productoSearchField.requestFocusInWindow();
                        searchProductButton.setIcon(iconService.iconoBusquedaEncontrada);
                    } catch (InterruptedException ex) {
                    } catch (ExecutionException ex) {
                        try {
                            throw ex.getCause();
                        } catch (NoSuchElementException e) {
                            popupMenu.removeAll();
                            popupMenu.add("No se encontró el producto.");
                            popupMenu.pack();
                            popupMenu.show(productoSearchField, 0, productoSearchField.getHeight() + 2);
                            searchProductButton.setIcon(iconService.iconoBusquedaFallida);
                            productoSearchField.putClientProperty("JComponent.outline", "warning");
                            productoSearchField.requestFocusInWindow();
                            popupMenu.setEnabled(false);
                            return;
                        } catch (Throwable e) {
                            System.out.println("impossible :");
                            e.printStackTrace();
                            System.out.println("impossible end");
                            return;
                        }
                    } finally {
                        setIdle();
                        isSearching = false;
                    }
                }
            };
            searchProductoWorker.execute();

        } else if (findByNameButton.isSelected()) {
            setBusy("Buscando producto...");
            searchProductButton.setIcon(iconService.iconoBuscando);

            SwingWorker searchProductoWorker = new SwingWorker<List<Producto>, Producto>() {
                @Override
                protected List<Producto> doInBackground() throws Exception {
                    List<Producto> productos = productoService.encontrarProductosPorNombre(text);
                    if (productos.isEmpty()) {
                        throw new NoSuchElementException("no elements found");
                    }
                    return productos;
                }

                @Override
                protected void done() {
                    List<Producto> productos = null;
                    try {
                        productos = get();
                        popupMenu.removeAll();
                        productos.forEach(producto -> {
                            popupMenu.add(producto.getIdProducto() + " - " + producto.getNombre() + " " + producto.getMarca().getNombreMarca() + " - Stock: " + producto.getAlmacen().getStock())
                                .addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        selectProducto(producto);
                                    }
                                });
                        });
                        popupMenu.pack();
                        popupMenu.show(productoSearchField, 0, productoSearchField.getHeight() + 2);
                        productoSearchField.requestFocusInWindow();
                        searchProductButton.setIcon(iconService.iconoBusquedaEncontrada);
                    } catch (InterruptedException ex) {
                    } catch (ExecutionException ex) {
                        try {
                            throw ex.getCause();
                        } catch (NoSuchElementException e) {
                            popupMenu.removeAll();
                            popupMenu.add("No se encontró el producto.");
                            popupMenu.pack();
                            popupMenu.show(productoSearchField, 0, productoSearchField.getHeight() + 2);
                            searchProductButton.setIcon(iconService.iconoBusquedaFallida);
                            productoSearchField.putClientProperty("JComponent.outline", "warning");
                            productoSearchField.requestFocusInWindow();
                            popupMenu.setEnabled(false);
                            return;
                        } catch (Throwable e) {
                            System.out.println("impossible :");
                            e.printStackTrace();
                            System.out.println("impossible end");
                            return;
                        }
                    } finally {
                        setIdle();
                        isSearching = false;
                    }

                }
            };
            searchProductoWorker.execute();
        }
        isSearching = false;
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

        popupMenu = new javax.swing.JPopupMenu();
        seleccionarClienteDialog = new javax.swing.JDialog();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        tableInformationLabel = new javax.swing.JLabel();
        scrollPane2 = new javax.swing.JScrollPane();
        tablaClientes = new org.jdesktop.swingx.JXTable();
        loadMoreButton = new javax.swing.JButton();
        contadorClientesLabel = new javax.swing.JLabel();
        nuevoCliente = new javax.swing.JButton();
        seleccionarClienteButtonDialog = new javax.swing.JButton();
        reloadTableButton = new javax.swing.JButton();
        nuevoClienteDialog = new javax.swing.JDialog(seleccionarClienteDialog);
        nuevoClienteLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        dniLabel1 = new javax.swing.JLabel();
        nombresLabel = new javax.swing.JLabel();
        dniField1 = new javax.swing.JTextField();
        nombresField = new javax.swing.JTextField();
        direccionLabel = new javax.swing.JLabel();
        direccionField = new javax.swing.JTextField();
        telefonoLabel = new javax.swing.JLabel();
        telefonoField = new javax.swing.JTextField();
        estadoCivilLabel = new javax.swing.JLabel();
        estadoCivilField = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        cancelarCreacionClienteButton = new javax.swing.JButton();
        guardarClienteButton = new javax.swing.JButton();
        guardarSeleccionarButton = new javax.swing.JButton();
        guardarVentaDialog = new javax.swing.JDialog();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        priceLabel = new javax.swing.JLabel();
        productoLabel = new javax.swing.JLabel();
        cantidadLabel = new javax.swing.JLabel();
        precioLabel = new javax.swing.JLabel();
        totalField = new javax.swing.JTextField();
        totalLabel = new javax.swing.JLabel();
        agregarProductoButton = new javax.swing.JButton();
        seleccionarClienteButton = new javax.swing.JButton();
        panel = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        detalleVentaTable = new org.jdesktop.swingx.JXTable();
        eliminarProducto = new javax.swing.JButton();
        cantidadProductos = new javax.swing.JLabel();
        clienteField = new javax.swing.JTextField();
        clienteLabel = new javax.swing.JLabel();
        dniLabel = new javax.swing.JLabel();
        dniField = new javax.swing.JTextField();
        numeroDeCompraLabel = new javax.swing.JLabel();
        numeroDeComprasField = new javax.swing.JTextField();
        cancelarVenta = new javax.swing.JButton();
        guardarVenta = new javax.swing.JButton();
        stockRestanteField = new javax.swing.JTextField();
        stockRestanteLabel = new javax.swing.JLabel();
        productoSearchField = new javax.swing.JTextField();
        cantidadSpinner = new javax.swing.JSpinner();
        precioSpinner = new javax.swing.JSpinner();
        comprobanteLabel = new javax.swing.JLabel();
        comprobanteComboBox = new javax.swing.JComboBox<>();

        popupMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                popupMenuPopupMenuWillBecomeVisible(evt);
            }
        });
        popupMenu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                popupMenuKeyPressed(evt);
            }
        });

        seleccionarClienteDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        seleccionarClienteDialog.setTitle("Seleccionar cliente");
        seleccionarClienteDialog.setAlwaysOnTop(true);
        seleccionarClienteDialog.setModal(true);
        seleccionarClienteDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        seleccionarClienteDialog.setName("seleccionarCliente"); // NOI18N

        jLayeredPane1.setOpaque(true);

        tableInformationLabel.setText("Sin datos.");
        tableInformationLabel.setOpaque(true);

        tablaClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaClientes.setColumnControlVisible(true);
        tablaClientes.setEditable(false);
        scrollPane2.setViewportView(tablaClientes);

        jLayeredPane1.setLayer(tableInformationLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(scrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(tableInformationLabel)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(0, 153, Short.MAX_VALUE)
                .addComponent(tableInformationLabel)
                .addGap(0, 153, Short.MAX_VALUE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE))
        );

        loadMoreButton.setText("Cargar más entradas");
        loadMoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMoreButtonActionPerformed(evt);
            }
        });

        contadorClientesLabel.setText("Cargando...");

        nuevoCliente.setText("Nuevo");
        nuevoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoClienteActionPerformed(evt);
            }
        });

        seleccionarClienteButtonDialog.setText("Seleccionar");
        seleccionarClienteButtonDialog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarClienteButtonDialogActionPerformed(evt);
            }
        });

        reloadTableButton.setText("Recargar");
        reloadTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadTableButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout seleccionarClienteDialogLayout = new javax.swing.GroupLayout(seleccionarClienteDialog.getContentPane());
        seleccionarClienteDialog.getContentPane().setLayout(seleccionarClienteDialogLayout);
        seleccionarClienteDialogLayout.setHorizontalGroup(
            seleccionarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seleccionarClienteDialogLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(seleccionarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, seleccionarClienteDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(loadMoreButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reloadTableButton))
                    .addGroup(seleccionarClienteDialogLayout.createSequentialGroup()
                        .addComponent(seleccionarClienteButtonDialog)
                        .addGap(12, 12, 12)
                        .addComponent(nuevoCliente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 329, Short.MAX_VALUE)
                        .addComponent(contadorClientesLabel)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, seleccionarClienteDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLayeredPane1)
                .addGap(6, 6, 6))
        );
        seleccionarClienteDialogLayout.setVerticalGroup(
            seleccionarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seleccionarClienteDialogLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(seleccionarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(seleccionarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(seleccionarClienteButtonDialog)
                        .addComponent(nuevoCliente))
                    .addComponent(contadorClientesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(seleccionarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reloadTableButton)
                    .addComponent(loadMoreButton))
                .addContainerGap())
        );

        nuevoClienteDialog.setTitle("Nuevo cliente");
        nuevoClienteDialog.setAlwaysOnTop(true);
        nuevoClienteDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        nuevoClienteDialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        nuevoClienteDialog.setResizable(false);
        nuevoClienteDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                nuevoClienteDialogWindowClosing(evt);
            }
        });

        nuevoClienteLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nuevoClienteLabel.setText("Crear nuevo cliente");

        dniLabel1.setText("DNI / RUC:");

        nombresLabel.setText("Nombres:");

        direccionLabel.setText("Direccion:");

        telefonoLabel.setText("Teléfono:");

        estadoCivilLabel.setText("Est. Civil");

        cancelarCreacionClienteButton.setText("Cancelar");
        cancelarCreacionClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarCreacionClienteButtonActionPerformed(evt);
            }
        });

        guardarClienteButton.setText("Guardar");
        guardarClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarClienteButtonActionPerformed(evt);
            }
        });

        guardarSeleccionarButton.setText("Guardar y seleccionar");
        guardarSeleccionarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarSeleccionarButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout nuevoClienteDialogLayout = new javax.swing.GroupLayout(nuevoClienteDialog.getContentPane());
        nuevoClienteDialog.getContentPane().setLayout(nuevoClienteDialogLayout);
        nuevoClienteDialogLayout.setHorizontalGroup(
            nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                        .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                                .addComponent(telefonoLabel)
                                .addGap(14, 14, 14)
                                .addComponent(telefonoField, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
                            .addComponent(nuevoClienteLabel)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoClienteDialogLayout.createSequentialGroup()
                                .addComponent(dniLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dniField1))
                            .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                                .addComponent(nombresLabel)
                                .addGap(11, 11, 11)
                                .addComponent(nombresField)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(direccionLabel)
                            .addComponent(estadoCivilLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(estadoCivilField, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(direccionField)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevoClienteDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(guardarClienteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(guardarSeleccionarButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelarCreacionClienteButton)))
                .addContainerGap())
        );
        nuevoClienteDialogLayout.setVerticalGroup(
            nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(nuevoClienteLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(dniLabel1)
                            .addComponent(dniField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nombresLabel)
                            .addComponent(nombresField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                        .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(direccionLabel)
                            .addComponent(direccionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(estadoCivilLabel)
                            .addComponent(estadoCivilField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(telefonoLabel)
                    .addComponent(telefonoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarCreacionClienteButton)
                    .addComponent(guardarClienteButton)
                    .addComponent(guardarSeleccionarButton))
                .addContainerGap())
        );

        guardarVentaDialog.setTitle("Guardar venta");
        guardarVentaDialog.setAlwaysOnTop(true);
        guardarVentaDialog.setModal(true);
        guardarVentaDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        guardarVentaDialog.setResizable(false);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Boleta", "Factura" }));

        jLabel1.setText("Tipo de comprobante:");

        javax.swing.GroupLayout guardarVentaDialogLayout = new javax.swing.GroupLayout(guardarVentaDialog.getContentPane());
        guardarVentaDialog.getContentPane().setLayout(guardarVentaDialogLayout);
        guardarVentaDialogLayout.setHorizontalGroup(
            guardarVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(guardarVentaDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(190, Short.MAX_VALUE))
        );
        guardarVentaDialogLayout.setVerticalGroup(
            guardarVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(guardarVentaDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(guardarVentaDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(282, Short.MAX_VALUE))
        );

        setMinimumSize(new java.awt.Dimension(797, 526));

        busyLabel.setBusy(true);
        busyLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        busyLabel.setPreferredSize(new java.awt.Dimension(22, 22));

        priceLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        priceLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        priceLabel.setText("TOTAL : S/. 0.00");
        priceLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        productoLabel.setText("Producto");

        cantidadLabel.setText("Cantidad");

        precioLabel.setText("Precio");

        totalField.setEditable(false);
        totalField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        totalField.setText("0.00");
        totalField.setFocusable(false);

        totalLabel.setText("Total");

        agregarProductoButton.setText("Agregar");
        agregarProductoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarProductoButtonActionPerformed(evt);
            }
        });

        seleccionarClienteButton.setText("Seleccionar cliente");
        seleccionarClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarClienteButtonActionPerformed(evt);
            }
        });

        detalleVentaTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        detalleVentaTable.setEditable(false);
        scrollPane.setViewportView(detalleVentaTable);

        eliminarProducto.setText("Eliminar");
        eliminarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarProductoActionPerformed(evt);
            }
        });

        cantidadProductos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cantidadProductos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cantidadProductos.setText("0 ITEMS - TOTAL: 0");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane)
            .addGroup(panelLayout.createSequentialGroup()
                .addComponent(cantidadProductos, javax.swing.GroupLayout.DEFAULT_SIZE, 724, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eliminarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cantidadProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eliminarProducto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE))
        );

        clienteField.setEditable(false);
        clienteField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clienteFieldActionPerformed(evt);
            }
        });

        clienteLabel.setText("Cliente");

        dniLabel.setText("DNI / RUC");

        dniField.setEditable(false);

        numeroDeCompraLabel.setText("Nº de compras");

        numeroDeComprasField.setEditable(false);

        cancelarVenta.setText("Cancelar");
        cancelarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarVentaActionPerformed(evt);
            }
        });

        guardarVenta.setText("Guardar");
        guardarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarVentaActionPerformed(evt);
            }
        });

        stockRestanteField.setEditable(false);
        stockRestanteField.setFocusable(false);

        stockRestanteLabel.setText("Stock");

        productoSearchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                productoSearchFieldFocusGained(evt);
            }
        });
        productoSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                productoSearchFieldKeyReleased(evt);
            }
        });

        comprobanteLabel.setText("Tipo de comprobante:");

        comprobanteComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Boleta", "Factura" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(productoSearchField, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(productoLabel)
                                    .addComponent(clienteLabel))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(clienteField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(stockRestanteLabel)
                                    .addComponent(stockRestanteField, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cantidadLabel)
                                    .addComponent(cantidadSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(precioLabel)
                                    .addComponent(precioSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(totalLabel)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(totalField, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(agregarProductoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(dniLabel)
                                    .addComponent(dniField, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(numeroDeCompraLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(numeroDeComprasField, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(seleccionarClienteButton))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(priceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(comprobanteLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(comprobanteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(guardarVenta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelarVenta)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clienteLabel)
                    .addComponent(dniLabel)
                    .addComponent(numeroDeCompraLabel))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clienteField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(seleccionarClienteButton)
                    .addComponent(dniField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numeroDeComprasField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(productoLabel)
                    .addComponent(cantidadLabel)
                    .addComponent(precioLabel)
                    .addComponent(totalLabel)
                    .addComponent(stockRestanteLabel))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(agregarProductoButton)
                    .addComponent(stockRestanteField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(productoSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cantidadSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precioSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(busyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(priceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarVenta)
                    .addComponent(guardarVenta)
                    .addComponent(comprobanteLabel)
                    .addComponent(comprobanteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void seleccionarClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarClienteButtonActionPerformed
        if (isClienteSelected()) {
            clearClienteData();
        } else {
            updateTable(false);
            seleccionarClienteDialog.setVisible(true);
        }
    }//GEN-LAST:event_seleccionarClienteButtonActionPerformed

    private void agregarProductoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarProductoButtonActionPerformed
        int cantidad = cantidadSpinnerModel.getNumber().intValue();
        BigDecimal precio = new BigDecimal(precioSpinnerModel.getNumber().doubleValue());
        precio = precio.setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = precio.multiply(new BigDecimal(cantidad));
        total = total.setScale(2, RoundingMode.HALF_UP);
        
        selectedProducto.getAlmacen().setStock(selectedProducto.getAlmacen().getStock() - cantidad);
        selectedProducto.setFechaUltimaVenta(new Timestamp(new Date().getTime()));
        Producto printableProducto = selectedProducto;
        printableProducto.getAlmacen().setProducto(null);
        Utils.prettyPrintObject(printableProducto);
        
        productos.add(new ProductoSeleccionado(selectedProducto, cantidad, precio, total));

        Vector datos = new Vector();
        datos.add(selectedProducto.getIdProducto());
        datos.add(selectedProducto.getNombre() + " " + selectedProducto.getMarca().getNombreMarca());
        datos.add(cantidad);
        datos.add(precio.toPlainString());
        datos.add(total.toPlainString());
        nuevaVentaTableModel.addRow(datos);
        totalCompra = totalCompra.add(total);
        updateTotal();
        deselectProducto();
    }//GEN-LAST:event_agregarProductoButtonActionPerformed

    private void eliminarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarProductoActionPerformed

        IntStream.of(nuevaVentaSelectionModel.getSelectedIndices())
            .boxed()
            .sorted(Collections.reverseOrder())
            .map(detalleVentaTable::convertRowIndexToModel)
            .forEach(value -> {
                totalCompra = totalCompra.subtract(productos.get(value).getTotal());
                nuevaVentaTableModel.removeRow(value);
                productos.remove(value.intValue());
            });
        updateTotal();
    }//GEN-LAST:event_eliminarProductoActionPerformed

    private void guardarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarVentaActionPerformed
        
        if (productos.isEmpty()) {
            return;
        }

        setBusy("Guardando venta...");
        
        Venta venta = new Venta();
        Comprobante comprobante = new Comprobante();
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setIdMetodoPago(1L);
        
        List<Producto> newProducts = new ArrayList<>();
        
        if (String.valueOf(comprobanteComboBox.getSelectedItem()) == "Boleta") {
            comprobante.setIdComprobante(1L);
            venta.setComprobante(comprobante);
        } else {
            comprobante.setIdComprobante(2L);
            venta.setComprobante(comprobante);
        }

        if (isClienteSelected()) {
            venta.setCliente(selectedCliente);
            selectedCliente.setNumeroCompras(selectedCliente.getNumeroCompras() + 1L);
        } else {
            venta.setCliente(emptyCliente);
            emptyCliente.setNumeroCompras(emptyCliente.getNumeroCompras() + 1L);
        }

        venta.setEmpleado(securityService.getLoggedEmpleado());
        venta.setFechaEmision(new Timestamp(new Date().getTime()));
        venta.setIgv(BigInteger.ZERO);
        venta.setMetodoPago(metodoPago);
        venta.setPrecioTotal(totalCompra);

        List<DetallesVenta> detallesVenta = new ArrayList<>();

        productos.forEach(producto -> {
            DetallesVenta detalleVenta = new DetallesVenta();
            detalleVenta.setVenta(venta);
            detalleVenta.setCantidad(producto.getCantidad());
            detalleVenta.setPrecioUnidad(producto.getPrecio());
            detalleVenta.setTotal(producto.getTotal());
            detalleVenta.setProducto(producto.getProducto());
            detallesVenta.add(detalleVenta);
        });
        
        venta.setDetallesVenta(detallesVenta);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                ventaService.nuevaVenta(venta);
                List<Producto> collected = productos.stream()
                    .map(producto -> producto.getProducto())
                    .collect(Collectors.toList());
                productoService.actualizarProductos(collected);
                clienteService.actualizarCliente(venta.getCliente());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    closeWindow();
                } catch (InterruptedException ex) {
                    Logger.getLogger(NuevaVentaWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(NuevaVentaWindow.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    setIdle();
                }
            }

        }.execute();

    }//GEN-LAST:event_guardarVentaActionPerformed

    private void cerrarVentana() {
        if(!productos.isEmpty()) {
            int answ = OptionPaneService.questionMessage(this, "¿Desea cerrar sin guardar?", "Cerrar sin guardar");
            if(answ == JOptionPane.YES_OPTION) {
                closeWindow();
            } 
        } else {
            closeWindow();
        }
        
    }
    
    private void closeWindow() {
        JXFrame topFrame = (JXFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.setVisible(false);
        topFrame.dispose();
    }
    
    private void cancelarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarVentaActionPerformed
        cerrarVentana();
    }//GEN-LAST:event_cancelarVentaActionPerformed

    //Listeners abajo! ---------------------------------------------------------

    private void productoSearchFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_productoSearchFieldFocusGained
        searchProductButton.setIcon(iconService.iconoEsperandoBusqueda);
        //deselectProducto();
    }//GEN-LAST:event_productoSearchFieldFocusGained

    private void productoSearchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_productoSearchFieldKeyReleased
        if (evt.getKeyCode() == evt.VK_DOWN) {
            if (popupMenu.isShowing()) {
                popupMenu.requestFocusInWindow();
                System.out.println("inside!");
            }
            System.out.println("DOWN!");
        }

        if (evt.getKeyCode() == evt.VK_ENTER) {
            System.out.println("ENTER!");
            if (!isSearching) {
                if (!popupMenu.isShowing()) {
                    popupMenu.setFocusable(true);
                    popupMenu.show(productoSearchField, 0, productoSearchField.getHeight() + 2);
                    popupMenu.requestFocusInWindow();
                }
            }
        }
    }//GEN-LAST:event_productoSearchFieldKeyReleased

    private void loadMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMoreButtonActionPerformed
        updateTable(false);
    }//GEN-LAST:event_loadMoreButtonActionPerformed

    private void reloadTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadTableButtonActionPerformed
        updateTable(true);
    }//GEN-LAST:event_reloadTableButtonActionPerformed

    private void clienteFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clienteFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clienteFieldActionPerformed

    private void popupMenuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_popupMenuKeyPressed
        if (evt.getKeyCode() == evt.VK_ENTER) {
            int selectedIndex = popupMenu.getSelectionModel().getSelectedIndex();
            if (selectedIndex == -1) {
                //popupMenu.requestFocusInWindow();
                System.out.println("-1");
                return;
            }
            JMenuItem menuItem = (JMenuItem) popupMenu.getComponent(selectedIndex);
            menuItem.doClick();
        }
        if (evt.getKeyCode() == evt.VK_UP) {
            int selectedIndex = popupMenu.getSelectionModel().getSelectedIndex();
            int menuSize = popupMenu.getComponentCount();
            if (selectedIndex > 0) {
                popupMenu.getSelectionModel().setSelectedIndex(selectedIndex - 1);
            } else {
                popupMenu.getSelectionModel().setSelectedIndex(menuSize - 1);
            }
        }
        if (evt.getKeyCode() == evt.VK_DOWN) {
            int selectedIndex = popupMenu.getSelectionModel().getSelectedIndex();
            int menuSize = popupMenu.getComponentCount();
            if (selectedIndex < menuSize - 1) {
                popupMenu.getSelectionModel().setSelectedIndex(selectedIndex + 1);
            } else {
                popupMenu.getSelectionModel().setSelectedIndex(0);
            }
        }
        System.out.println(popupMenu.getSelectionModel().getSelectedIndex());
    }//GEN-LAST:event_popupMenuKeyPressed

    private void popupMenuPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_popupMenuPopupMenuWillBecomeVisible
        try {
            if (!popupMenu.getSelectionModel().isSelected()) {
                popupMenu.getSelectionModel().setSelectedIndex(0);
            }
            System.out.println(popupMenu.getSelectionModel().getSelectedIndex());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        }
    }//GEN-LAST:event_popupMenuPopupMenuWillBecomeVisible

    //Listeners arriba! ---------------------------------------------------------
    private void cancelarCreacionCliente() {
        boolean isBlank = true;

        if (!nombresField.getText().isBlank()) {
            isBlank = false;
        }

        if (!dniField.getText().isBlank()) {
            isBlank = false;
        }

        if (!telefonoField.getText().isBlank()) {
            isBlank = false;
        }

        if (!direccionField.getText().isBlank()) {
            isBlank = false;
        }

        if (!estadoCivilField.getText().isBlank()) {
            isBlank = false;
        }

        if (isBlank) {
            nuevoClienteDialog.setVisible(false);
        } else {
            int ans = OptionPaneService.questionMessage(nuevoClienteDialog, "¿Desea salir sin guardar los cambios?", "Cambios sin guardar");
            if (ans == JOptionPane.YES_OPTION) {
                nuevoClienteDialog.setVisible(false);
                clearNuevoClienteWindow();
            }
        }
    }

    private void clearNuevoClienteWindow() {
        nombresField.setText("");
        dniField.setText("");
        telefonoField.setText("");
        direccionField.setText("");
        estadoCivilField.setText("");
    }

    private void cancelarCreacionClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarCreacionClienteButtonActionPerformed
        cancelarCreacionCliente();
    }//GEN-LAST:event_cancelarCreacionClienteButtonActionPerformed

    private Cliente validateCliente() {
        nombresField.putClientProperty("JComponent.outline", "");
        dniField.putClientProperty("JComponent.outline", "");
        telefonoField.putClientProperty("JComponent.outline", "");
        direccionField.putClientProperty("JComponent.outline", "");
        estadoCivilField.putClientProperty("JComponent.outline", "");
        //TODO: reemplazar razon social por un combobox
        Cliente c = new Cliente();
        int dni = 0;
        int telefono = 0;
        boolean error = false;

        if (nombresField.getText().isBlank()) {
            nombresField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (dniField1.getText().isBlank()) {
            dniField1.putClientProperty("JComponent.outline", "error");
            error = true;
        } else {
            try {
                dni = Integer.parseInt(dniField1.getText());
            } catch (Exception e) {
                dniField1.putClientProperty("JComponent.outline", "error");
                error = true;
                //e.printStackTrace();
            }
        }

        if (telefonoField.getText().isBlank()) {
            telefonoField.putClientProperty("JComponent.outline", "error");
            error = true;
        } else {
            try {
                telefono = Integer.parseInt(telefonoField.getText());
            } catch (Exception e) {
                telefonoField.putClientProperty("JComponent.outline", "error");
                error = true;
                //e.printStackTrace();
            }
        }

        if (direccionField.getText().isBlank()) {
            direccionField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (estadoCivilField.getText().isBlank()) {
            estadoCivilField.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (error) {
            setIdle();
            return null;
        } else {
            c.setIdentificacion(String.valueOf(dni));
            c.setNombre(nombresField.getText());
            c.setDireccion(direccionField.getText());
            c.setEstadoCivil(estadoCivilField.getText());
            c.setTelefono(String.valueOf(telefono));
            c.setNumeroCompras((long) 0);
            return c;
        }
    }

    private void guardarClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarClienteButtonActionPerformed
        setBusy("Guardando cliente...");
        Cliente c = validateCliente();

        if (c == null) {
            setIdle();
            return;
        }

        new SwingWorker<Cliente, Cliente>() {
            @Override
            protected Cliente doInBackground() throws Exception {
                return clienteService.nuevoCliente(c);
            }

            @Override
            protected void done() {
                try {
                    get();
                    updateTable(true);
                } catch (InterruptedException ex) {
                } catch (ExecutionException ex) {
                    try {
                        throw ex.getCause();
                    } catch (NotEnoughPermissionsException e) {
                        OptionPaneService.errorMessage(nuevoClienteDialog, "No dispone de permisos suficientes para poder crear un nuevo cliente.", "Sin permisos.");
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
        clearNuevoClienteWindow();
        nuevoClienteDialog.setVisible(false);
    }//GEN-LAST:event_guardarClienteButtonActionPerformed

    private void nuevoClienteDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_nuevoClienteDialogWindowClosing
        cancelarCreacionCliente();
    }//GEN-LAST:event_nuevoClienteDialogWindowClosing

    private void nuevoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoClienteActionPerformed
        nuevoClienteDialog.setVisible(true);
    }//GEN-LAST:event_nuevoClienteActionPerformed

    private void guardarSeleccionarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarSeleccionarButtonActionPerformed
        setBusy("Guardando cliente...");
        Cliente c = validateCliente();

        if (c == null) {
            setIdle();
            return;
        }

        new SwingWorker<Cliente, Cliente>() {
            @Override
            protected Cliente doInBackground() throws Exception {
                return clienteService.nuevoCliente(c);
            }

            @Override
            protected void done() {
                try {
                    selectedCliente = get();
                    clearNuevoClienteWindow();
                    nuevoClienteDialog.setVisible(false);
                    seleccionarClienteDialog.setVisible(false);
                    fillClienteData(selectedCliente);
                } catch (InterruptedException ex) {
                } catch (ExecutionException ex) {
                    try {
                        throw ex.getCause();
                    } catch (NotEnoughPermissionsException e) {
                        OptionPaneService.errorMessage(nuevoClienteDialog, "No dispone de permisos suficientes para poder crear un nuevo cliente.", "Sin permisos.");
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
    }//GEN-LAST:event_guardarSeleccionarButtonActionPerformed

    private void seleccionarClienteButtonDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarClienteButtonDialogActionPerformed
        setBusy("Cargando...");
        List<Long> idClientes = new ArrayList<>();
        for (int i : clienteSelectionModel.getSelectedIndices()) { //rows 
            i = tablaClientes.convertRowIndexToModel(i);
            // ↑ IMPORTANTISIMO, en caso de que la tabla esté ordenada por alguna columna, esto devolvera siempre la fila seleccionada.
            idClientes.add((Long) defaultTableModelClientes.getValueAt(i, 0));
        }
        final long idCliente = idClientes.get(0);
        new SwingWorker<Cliente, Cliente>() {
            @Override
            protected Cliente doInBackground() throws Exception {
                return clienteService.encontrarClientePorId(idCliente);
            }

            @Override
            protected void done() {
                try {
                    selectedCliente = get();
                    nuevoClienteDialog.setVisible(false);
                    seleccionarClienteDialog.setVisible(false);
                    fillClienteData(selectedCliente);
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(ClientesTab.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    setIdle();
                }
            }
        }.execute();
    }//GEN-LAST:event_seleccionarClienteButtonDialogActionPerformed

    private void fillClienteData(Cliente cliente) {
        clienteField.setText(cliente.getNombre());
        dniField.setText(cliente.getIdentificacion());
        numeroDeComprasField.setText("" + cliente.getNumeroCompras());
        seleccionarClienteButton.setText("Quitar selección");
    }

    private void clearClienteData() {
        clienteField.setText("");
        dniField.setText("");
        numeroDeComprasField.setText("");
        selectedCliente = null;
        seleccionarClienteButton.setText("Seleccionar cliente");
    }

    public List<ProductoSeleccionado> getProductos() {
        return productos;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agregarProductoButton;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarCreacionClienteButton;
    private javax.swing.JButton cancelarVenta;
    private javax.swing.JLabel cantidadLabel;
    private javax.swing.JLabel cantidadProductos;
    private javax.swing.JSpinner cantidadSpinner;
    private javax.swing.JTextField clienteField;
    private javax.swing.JLabel clienteLabel;
    private javax.swing.JComboBox<String> comprobanteComboBox;
    private javax.swing.JLabel comprobanteLabel;
    private javax.swing.JLabel contadorClientesLabel;
    private org.jdesktop.swingx.JXTable detalleVentaTable;
    private javax.swing.JTextField direccionField;
    private javax.swing.JLabel direccionLabel;
    private javax.swing.JTextField dniField;
    private javax.swing.JTextField dniField1;
    private javax.swing.JLabel dniLabel;
    private javax.swing.JLabel dniLabel1;
    private javax.swing.JButton eliminarProducto;
    private javax.swing.JTextField estadoCivilField;
    private javax.swing.JLabel estadoCivilLabel;
    private javax.swing.JButton guardarClienteButton;
    private javax.swing.JButton guardarSeleccionarButton;
    private javax.swing.JButton guardarVenta;
    private javax.swing.JDialog guardarVentaDialog;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JTextField nombresField;
    private javax.swing.JLabel nombresLabel;
    private javax.swing.JButton nuevoCliente;
    private javax.swing.JDialog nuevoClienteDialog;
    private javax.swing.JLabel nuevoClienteLabel;
    private javax.swing.JLabel numeroDeCompraLabel;
    private javax.swing.JTextField numeroDeComprasField;
    private javax.swing.JPanel panel;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JLabel precioLabel;
    private javax.swing.JSpinner precioSpinner;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JLabel productoLabel;
    private javax.swing.JTextField productoSearchField;
    private javax.swing.JButton reloadTableButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JScrollPane scrollPane2;
    private javax.swing.JButton seleccionarClienteButton;
    private javax.swing.JButton seleccionarClienteButtonDialog;
    private javax.swing.JDialog seleccionarClienteDialog;
    private javax.swing.JTextField stockRestanteField;
    private javax.swing.JLabel stockRestanteLabel;
    private org.jdesktop.swingx.JXTable tablaClientes;
    private javax.swing.JLabel tableInformationLabel;
    private javax.swing.JTextField telefonoField;
    private javax.swing.JLabel telefonoLabel;
    private javax.swing.JTextField totalField;
    private javax.swing.JLabel totalLabel;
    // End of variables declaration//GEN-END:variables

    @Data
    @AllArgsConstructor
    private class ProductoSeleccionado {

        private Producto producto;

        private int cantidad;

        private BigDecimal precio;

        private BigDecimal total;

    }

}
