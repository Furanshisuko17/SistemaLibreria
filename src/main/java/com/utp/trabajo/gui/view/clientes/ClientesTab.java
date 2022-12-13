package com.utp.trabajo.gui.view.clientes;

import com.utp.trabajo.exception.security.NotEnoughPermissionsException;
import com.utp.trabajo.model.entities.Cliente;
import com.utp.trabajo.services.ClienteService;
import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.util.OptionPaneService;
import hu.akarnokd.rxjava3.swing.SwingObservable;
import java.awt.event.AdjustmentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;

public class ClientesTab extends org.jdesktop.swingx.JXPanel {

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

    private ListSelectionModel selectionModel;
    
    private Cliente clienteSeleccionado = null;

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
    private ClienteService clienteService;

    public ClientesTab() {
        initComponents();
        initTableClientes();
    }

    @PostConstruct
    private void init() {
        checkPermissions();
        updateTable(false); // mover hacia un listener que verifique que se ha abierto el jPanel
    }

    private void initTableClientes() {
        defaultTableModelClientes.setColumnIdentifiers(columnNames);
        tablaClientes.setModel(defaultTableModelClientes);
        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(50);
        
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
        selectionModel = tablaClientes.getSelectionModel();
        selectionModel.addListSelectionListener((ListSelectionEvent e) -> {
            
            if (!canDelete || !canEdit) {
                return;
            }
            if (!selectionModel.isSelectionEmpty()) {
                eliminarClienteButton.setEnabled(true);
            } else {
                eliminarClienteButton.setEnabled(false);
            }

            if (selectionModel.getSelectedItemsCount() == 1) {
                editarClienteButton.setEnabled(true);
            } else {
                editarClienteButton.setEnabled(false);
            }

        });
        setIdle();
        eliminarClienteButton.setEnabled(false);
        editarClienteButton.setEnabled(false);
        
        nuevoClienteDialog.pack();
        nuevoClienteDialog.setLocationRelativeTo(this);
        editarClienteDialog.pack();
        editarClienteDialog.setLocationRelativeTo(this);

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
            nuevoClienteButton.setEnabled(false);
            guardarClienteButton.setEnabled(false);
        }
        if (!permissions.contains("delete")) {
            canDelete = false;
            eliminarClienteButton.setEnabled(false);
        }
        if (!permissions.contains("edit")) {
            canEdit = false;
            editarClienteButton.setEnabled(false);
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
        if (!canRead) {
            setBusy("Sin permisos suficientes para leer datos.");
            return;
        }
        setBusy("Cargando...");
        reloadTableButton.setEnabled(false);
        loadMoreButton.setEnabled(false);
        retrievingData = true;
        int oldRowCount = defaultTableModelClientes.getRowCount();
        if (reload) {
            defaultTableModelClientes.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
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
                    Logger.getLogger(ClientesTab.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };
        new SwingWorker<List<Cliente>, List<Cliente>>() {
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
        }.execute();
    }

    private List<Long> getIdFromSelectedRows() { // refactor! DONE!
        List<Long> idClientes = new ArrayList<>();
        for (int i : selectionModel.getSelectedIndices()) { //rows 
            i = tablaClientes.convertRowIndexToModel(i);
            // ↑ IMPORTANTISIMO, en caso de que la tabla esté ordenada por alguna columna, esto devolvera siempre la fila seleccionada.
            idClientes.add((Long) defaultTableModelClientes.getValueAt(i, 0));
        }
        return idClientes;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nuevoClienteDialog = new javax.swing.JDialog();
        nuevoClienteLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        dniLabel = new javax.swing.JLabel();
        nombresLabel = new javax.swing.JLabel();
        dniField = new javax.swing.JTextField();
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
        editarClienteDialog = new javax.swing.JDialog();
        nuevoClienteLabel1 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        dniLabel1 = new javax.swing.JLabel();
        nombresLabel1 = new javax.swing.JLabel();
        dniField1 = new javax.swing.JTextField();
        nombresField1 = new javax.swing.JTextField();
        direccionLabel1 = new javax.swing.JLabel();
        direccionField1 = new javax.swing.JTextField();
        telefonoLabel1 = new javax.swing.JLabel();
        telefonoField1 = new javax.swing.JTextField();
        estadoCivilLabel1 = new javax.swing.JLabel();
        estadoCivilField1 = new javax.swing.JTextField();
        jSeparator4 = new javax.swing.JSeparator();
        cancelarCreacionClienteButton1 = new javax.swing.JButton();
        guardarClienteButton1 = new javax.swing.JButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        tableInformationLabel = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        tablaClientes = new org.jdesktop.swingx.JXTable();
        reloadTableButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();
        editarClienteButton = new javax.swing.JButton();
        nuevoClienteButton = new javax.swing.JButton();
        eliminarClienteButton = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        contadorClientesLabel = new javax.swing.JLabel();

        nuevoClienteDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        nuevoClienteDialog.setTitle("Nuevo cliente");
        nuevoClienteDialog.setAlwaysOnTop(true);
        nuevoClienteDialog.setModal(true);
        nuevoClienteDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        nuevoClienteDialog.setResizable(false);
        nuevoClienteDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                nuevoClienteDialogWindowClosing(evt);
            }
        });

        nuevoClienteLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nuevoClienteLabel.setText("Crear nuevo cliente");

        dniLabel.setText("DNI / RUC:");

        nombresLabel.setText("Nombres:");

        direccionLabel.setText("Direccion:");

        telefonoLabel.setText("Teléfono:");

        estadoCivilLabel.setText("Est. Civil:");

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
                                .addComponent(dniLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dniField))
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
                    .addGroup(nuevoClienteDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(guardarClienteButton)
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
                            .addComponent(dniLabel)
                            .addComponent(dniField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(nuevoClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarCreacionClienteButton)
                    .addComponent(guardarClienteButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        editarClienteDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        editarClienteDialog.setTitle("Editar cliente");
        editarClienteDialog.setAlwaysOnTop(true);
        editarClienteDialog.setModal(true);
        editarClienteDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        editarClienteDialog.setResizable(false);
        editarClienteDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                editarClienteDialogWindowClosing(evt);
            }
        });

        nuevoClienteLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nuevoClienteLabel1.setText("Editar cliente #00");

        dniLabel1.setText("DNI / RUC:");

        nombresLabel1.setText("Nombres:");

        direccionLabel1.setText("Direccion:");

        telefonoLabel1.setText("Teléfono:");

        estadoCivilLabel1.setText("Est. Civil:");

        cancelarCreacionClienteButton1.setText("Cancelar");
        cancelarCreacionClienteButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarCreacionClienteButton1ActionPerformed(evt);
            }
        });

        guardarClienteButton1.setText("Guardar");
        guardarClienteButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarClienteButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout editarClienteDialogLayout = new javax.swing.GroupLayout(editarClienteDialog.getContentPane());
        editarClienteDialog.getContentPane().setLayout(editarClienteDialogLayout);
        editarClienteDialogLayout.setHorizontalGroup(
            editarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator3)
            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(editarClienteDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(editarClienteDialogLayout.createSequentialGroup()
                        .addGroup(editarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(editarClienteDialogLayout.createSequentialGroup()
                                .addComponent(telefonoLabel1)
                                .addGap(14, 14, 14)
                                .addComponent(telefonoField1, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
                            .addComponent(nuevoClienteLabel1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editarClienteDialogLayout.createSequentialGroup()
                                .addComponent(dniLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dniField1))
                            .addGroup(editarClienteDialogLayout.createSequentialGroup()
                                .addComponent(nombresLabel1)
                                .addGap(11, 11, 11)
                                .addComponent(nombresField1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(editarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(direccionLabel1)
                            .addComponent(estadoCivilLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(editarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(estadoCivilField1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(direccionField1)))
                    .addGroup(editarClienteDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(guardarClienteButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelarCreacionClienteButton1)))
                .addContainerGap())
        );
        editarClienteDialogLayout.setVerticalGroup(
            editarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editarClienteDialogLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(nuevoClienteLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(editarClienteDialogLayout.createSequentialGroup()
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(editarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(dniLabel1)
                            .addComponent(dniField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(editarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nombresLabel1)
                            .addComponent(nombresField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(editarClienteDialogLayout.createSequentialGroup()
                        .addGroup(editarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(direccionLabel1)
                            .addComponent(direccionField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(editarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(estadoCivilLabel1)
                            .addComponent(estadoCivilField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(telefonoLabel1)
                    .addComponent(telefonoField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editarClienteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarCreacionClienteButton1)
                    .addComponent(guardarClienteButton1))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        tableInformationLabel.setText("Sin datos.");

        tablaClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaClientes.setColumnControlVisible(true);
        tablaClientes.setEditable(false);
        scrollPane.setViewportView(tablaClientes);

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
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
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

        editarClienteButton.setText("Editar");
        editarClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarClienteButtonActionPerformed(evt);
            }
        });

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

        contadorClientesLabel.setText("Cargando...");

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(contadorClientesLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(busyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(nuevoClienteButton)
                        .addComponent(editarClienteButton)
                        .addComponent(eliminarClienteButton))
                    .addComponent(contadorClientesLabel, javax.swing.GroupLayout.Alignment.TRAILING))
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

    private void clearEditarClienteDialog() {
        nombresField1.setText("");
        dniField1.setText("");
        telefonoField1.setText("");
        direccionField1.setText("");
        estadoCivilField1.setText("");
        clienteSeleccionado = null;
    }
    
    private void clearNuevoClienteWindow() {
        nombresField.setText("");
        dniField.setText("");
        telefonoField.setText("");
        direccionField.setText("");
        estadoCivilField.setText("");
    }
    
    private void nuevoClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoClienteButtonActionPerformed
        nuevoClienteDialog.setVisible(true);
    }//GEN-LAST:event_nuevoClienteButtonActionPerformed

    private void eliminarClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarClienteButtonActionPerformed
        List<Long> selectedClientesId = getIdFromSelectedRows();

        int selectedOption = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar " + (selectedClientesId.size() == 1 ? "1 cliente?" : (selectedClientesId.size() + " clientes?")),
                selectedClientesId.size() == 1 ? "Eliminar un cliente" : "Eliminar varios clientes",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (selectedOption == JOptionPane.YES_OPTION) {
            List<Cliente> clientesEliminados = new ArrayList<>();
            try {
                clientesEliminados = clienteService.eliminarClientes(selectedClientesId); //implementar swingworker
            }
            catch (NotEnoughPermissionsException ex) {
                // just pass?
            }

            if (!clientesEliminados.isEmpty()) {
                updateTable(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Ocurrió un error al eliminar " + (selectedClientesId.size() == 1 ? "el cliente seleccionado." : "los clientes seleccionados."),
                        "¡Error!", JOptionPane.ERROR_MESSAGE);
            }
        } else if (selectedOption == JOptionPane.NO_OPTION) {
            //do nothing
        }

    }//GEN-LAST:event_eliminarClienteButtonActionPerformed

    private void cancelarEdicionCliente() {
        boolean hasChanges = true;

        if (nombresField1.getText().equals(clienteSeleccionado.getNombre())) {
            hasChanges = false;
        }

        if (dniField1.getText().equals(clienteSeleccionado.getIdentificacion())) {
            hasChanges = false;
        }

        if (telefonoField1.getText().equals(clienteSeleccionado.getTelefono())) {
            hasChanges = false;
        }

        if (direccionField1.getText().equals(clienteSeleccionado.getDireccion())) {
            hasChanges = false;
        }

        if (estadoCivilField1.getText().equals(clienteSeleccionado.getEstadoCivil())) {
            hasChanges = false;
        }
        
        if (hasChanges) {
            editarClienteDialog.setVisible(false);
        }else {
            int ans = OptionPaneService.questionMessage(editarClienteDialog, "¿Desea salir sin guardar los cambios?", "Cambios sin guardar");
            if (ans == JOptionPane.YES_OPTION) {
                editarClienteDialog.setVisible(false);
                clearEditarClienteDialog();
            }
        }
    }
    
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
        }else {
            int ans = OptionPaneService.questionMessage(nuevoClienteDialog, "¿Desea salir sin guardar los cambios?", "Cambios sin guardar");
            if (ans == JOptionPane.YES_OPTION) {
                nuevoClienteDialog.setVisible(false);
                clearNuevoClienteWindow();
            }
        }
    }
    
    private void cancelarCreacionClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarCreacionClienteButtonActionPerformed
        cancelarCreacionCliente();
    }//GEN-LAST:event_cancelarCreacionClienteButtonActionPerformed

    private void guardarClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarClienteButtonActionPerformed
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

        if (dniField.getText().isBlank()) {
            dniField.putClientProperty("JComponent.outline", "error");
            error = true;
        } else {
            try {
                dni = Integer.parseInt(dniField.getText());
            }
            catch (Exception e) {
                dniField.putClientProperty("JComponent.outline", "error");
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
            }
            catch (Exception e) {
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
            return;
        } else {
            setBusy("Guardando cliente...");
            c.setIdentificacion(String.valueOf(dni));
            c.setNombre(nombresField.getText());
            c.setDireccion(direccionField.getText());
            c.setEstadoCivil(estadoCivilField.getText());
            c.setTelefono(String.valueOf(telefono));
            c.setNumeroCompras((long) 0);
            new SwingWorker<Cliente, Cliente>() {
                @Override
                protected Cliente doInBackground() throws Exception {
                    return clienteService.nuevoCliente(c); 
                }

                @Override
                protected void done() {
                    try {
                        get(); //maybe check if it was correctly added?
                        setIdle();
                        clearNuevoClienteWindow();  
                    }
                    catch (InterruptedException ex) {}
                    catch (ExecutionException ex) {
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
                    }
                }
            }.execute();
        }
        nuevoClienteDialog.setVisible(false);
    }//GEN-LAST:event_guardarClienteButtonActionPerformed

    private void editarClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarClienteButtonActionPerformed
        List<Long> idClientesSeleccionado = getIdFromSelectedRows();
        if (idClientesSeleccionado.size() == 1) {
            setBusy("Cargando cliente #" + idClientesSeleccionado.get(0));
            new SwingWorker<Cliente, Cliente>() {
                @Override
                protected Cliente doInBackground() throws Exception {
                    return clienteService.encontrarClientePorId(idClientesSeleccionado.get(0));
                }

                @Override
                protected void done() {
                    try {
                        Cliente cliente = get();
                        clienteSeleccionado = cliente;
                        nuevoClienteLabel1.setText("Editar cliente #" + cliente.getIdCliente());
                        dniField1.setText(cliente.getIdentificacion());
                        telefonoField1.setText(cliente.getTelefono());
                        direccionField1.setText(cliente.getDireccion());
                        estadoCivilField1.setText(cliente.getEstadoCivil());
                        setIdle();
                        editarClienteDialog.setVisible(true);
                    }
                    catch (InterruptedException ex) {}
                    catch (ExecutionException ex) {
                        try {
                            throw ex.getCause();
                        } catch (NotEnoughPermissionsException e) {
                            OptionPaneService.errorMessage(nuevoClienteDialog, "No dispone de permisos suficientes para poder crear un nuevo cliente.", "Sin permisos.");
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
    }//GEN-LAST:event_editarClienteButtonActionPerformed

    private void nuevoClienteDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_nuevoClienteDialogWindowClosing
        cancelarCreacionCliente();
    }//GEN-LAST:event_nuevoClienteDialogWindowClosing

    private void cancelarCreacionClienteButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarCreacionClienteButton1ActionPerformed
        cancelarEdicionCliente();
    }//GEN-LAST:event_cancelarCreacionClienteButton1ActionPerformed

    private void guardarClienteButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarClienteButton1ActionPerformed
        nombresField1.putClientProperty("JComponent.outline", "");
        dniField1.putClientProperty("JComponent.outline", "");
        telefonoField1.putClientProperty("JComponent.outline", "");
        direccionField1.putClientProperty("JComponent.outline", "");
        estadoCivilField1.putClientProperty("JComponent.outline", "");
        //TODO: reemplazar razon social por un combobox
        Cliente c = clienteSeleccionado;
        int dni = 0;
        int telefono = 0;
        boolean error = false;

        if (nombresField1.getText().isBlank()) {
            nombresField1.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (dniField1.getText().isBlank()) {
            dniField1.putClientProperty("JComponent.outline", "error");
            error = true;
        } else {
            try {
                dni = Integer.parseInt(dniField1.getText());
            }
            catch (Exception e) {
                dniField1.putClientProperty("JComponent.outline", "error");
                error = true;
                //e.printStackTrace();
            }
        }

        if (telefonoField1.getText().isBlank()) {
            telefonoField1.putClientProperty("JComponent.outline", "error");
            error = true;
        } else {
            try {
                telefono = Integer.parseInt(telefonoField1.getText());
            }
            catch (Exception e) {
                telefonoField1.putClientProperty("JComponent.outline", "error");
                error = true;
                //e.printStackTrace();
            }
        }

        if (direccionField1.getText().isBlank()) {
            direccionField1.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (estadoCivilField1.getText().isBlank()) {
            estadoCivilField1.putClientProperty("JComponent.outline", "error");
            error = true;
        }

        if (error) {
            return;
        } else {
            setBusy("Guardando cliente...");
            c.setIdentificacion(String.valueOf(dni));
            c.setNombre(nombresField1.getText());
            c.setDireccion(direccionField1.getText());
            c.setEstadoCivil(estadoCivilField1.getText());
            c.setTelefono(String.valueOf(telefono));
            new SwingWorker<Cliente, Cliente>() {
                @Override
                protected Cliente doInBackground() throws Exception {
                    return clienteService.actualizarCliente(c); 
                }

                @Override
                protected void done() {
                    try {
                        get(); //maybe check if it was correctly added?
                        setIdle();
                        clearEditarClienteDialog();  
                    }
                    catch (InterruptedException ex) {}
                    catch (ExecutionException ex) {
                        try {
                            throw ex.getCause();
                        } catch (NotEnoughPermissionsException e) {
                            OptionPaneService.errorMessage(editarClienteDialog, "No dispone de permisos suficientes para poder crear un nuevo cliente.", "Sin permisos.");
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
        editarClienteDialog.setVisible(false);
    }//GEN-LAST:event_guardarClienteButton1ActionPerformed

    private void editarClienteDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_editarClienteDialogWindowClosing
        cancelarEdicionCliente();
    }//GEN-LAST:event_editarClienteDialogWindowClosing
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarCreacionClienteButton;
    private javax.swing.JButton cancelarCreacionClienteButton1;
    private javax.swing.JLabel contadorClientesLabel;
    private javax.swing.JTextField direccionField;
    private javax.swing.JTextField direccionField1;
    private javax.swing.JLabel direccionLabel;
    private javax.swing.JLabel direccionLabel1;
    private javax.swing.JTextField dniField;
    private javax.swing.JTextField dniField1;
    private javax.swing.JLabel dniLabel;
    private javax.swing.JLabel dniLabel1;
    private javax.swing.JButton editarClienteButton;
    private javax.swing.JDialog editarClienteDialog;
    private javax.swing.JButton eliminarClienteButton;
    private javax.swing.JTextField estadoCivilField;
    private javax.swing.JTextField estadoCivilField1;
    private javax.swing.JLabel estadoCivilLabel;
    private javax.swing.JLabel estadoCivilLabel1;
    private javax.swing.JButton guardarClienteButton;
    private javax.swing.JButton guardarClienteButton1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JTextField nombresField;
    private javax.swing.JTextField nombresField1;
    private javax.swing.JLabel nombresLabel;
    private javax.swing.JLabel nombresLabel1;
    private javax.swing.JButton nuevoClienteButton;
    private javax.swing.JDialog nuevoClienteDialog;
    private javax.swing.JLabel nuevoClienteLabel;
    private javax.swing.JLabel nuevoClienteLabel1;
    private javax.swing.JButton reloadTableButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaClientes;
    private javax.swing.JLabel tableInformationLabel;
    private javax.swing.JTextField telefonoField;
    private javax.swing.JTextField telefonoField1;
    private javax.swing.JLabel telefonoLabel;
    private javax.swing.JLabel telefonoLabel1;
    // End of variables declaration//GEN-END:variables
}
