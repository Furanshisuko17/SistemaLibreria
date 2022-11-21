
package com.utp.trabajo.gui.view.compras;

import com.utp.trabajo.model.entities.Compra;
import com.utp.trabajo.model.entities.MetodoPago;
import com.utp.trabajo.services.transaction.ComprasService;
import com.utp.trabajo.services.security.SecurityService;
import java.awt.event.AdjustmentEvent;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
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
                    return Timestamp.class;
                case 2:
                    return String.class;
                case 3:
                    return BigInteger.class;
                case 4:
                    return MetodoPago.class;
                case 5:
                    return BigInteger.class;
                default:
                    return String.class;
            }
        }
    };
    String[] columnNames = {"IdCompra", "Fecha Compra", "Transporte", "Descuento","Metodo Pago", "Precio Total"};

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
        defaultTableModelCompras.setColumnIdentifiers(columnNames);
        tablaCompras.setModel(defaultTableModelCompras);
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

        nuevaCompraDialog.pack();
        nuevaCompraDialog.setLocationRelativeTo(this);
        System.out.println("Compras tab - Nueva instancia!");
    }
    @PostConstruct
    public void init() {
        checkPermissions();
        retrieveData(false);
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
        
        if(!canRead) {
            setBusy("Sin permisos suficientes para leer datos.");
            return;
        }
        
        setBusy("Cargando...");
        reloadButton.setEnabled(false);
        nuevaCompraButton.setEnabled(false);
        retrievingData = true;
        loadMoreButton.setEnabled(false);
        if(reload) {
            defaultTableModelCompras.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }
        SwingWorker worker = new SwingWorker<List<Compra>, List<Compra>>()  {
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
                        values[4] = compra.getMetodoPago();
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
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(nuevaCompraDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel6))
                .addGap(19, 19, 19)
                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(IdCompraField, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(fechaCompraField)
                    .addComponent(descuentoField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4))
                .addGap(40, 40, 40)
                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(metodoPagoField, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precioTotalField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
            .addGroup(nuevaCompraDialogLayout.createSequentialGroup()
                .addGap(166, 166, 166)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nuevaCompraDialogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(guardarButton)
                .addGap(18, 18, 18)
                .addComponent(cancelarNuevaCompraButton)
                .addContainerGap())
            .addComponent(jSeparator2)
        );
        nuevaCompraDialogLayout.setVerticalGroup(
            nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nuevaCompraDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(IdCompraField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(precioTotalField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fechaCompraField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(metodoPagoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(descuentoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(nuevaCompraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarNuevaCompraButton)
                    .addComponent(guardarButton))
                .addContainerGap())
        );

        tableInformationLabel.setText("No Data");

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
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(tableInformationLabel)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 409, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(tableInformationLabel)
                    .addGap(0, 0, Short.MAX_VALUE)))
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLayeredPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 460, Short.MAX_VALUE)
                        .addComponent(loadMoreButton)
                        .addGap(30, 30, 30)
                        .addComponent(reloadButton)
                        .addGap(21, 21, 21))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nuevaCompraButton)
                        .addGap(18, 18, 18)
                        .addComponent(editarCompraButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(eliminarCompraButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevaCompraButton)
                    .addComponent(editarCompraButton)
                    .addComponent(eliminarCompraButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(loadMoreButton)
                        .addComponent(reloadButton))))
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
        List<Compra> comprasEliminados = comprasService.eliminarCompra(selectedComprasId);

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField IdCompraField;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cancelarNuevaCompraButton;
    private javax.swing.JTextField descuentoField;
    private javax.swing.JButton editarCompraButton;
    private javax.swing.JButton eliminarCompraButton;
    private javax.swing.JTextField fechaCompraField;
    private javax.swing.JButton guardarButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JTextField metodoPagoField;
    private javax.swing.JButton nuevaCompraButton;
    private javax.swing.JDialog nuevaCompraDialog;
    private javax.swing.JTextField precioTotalField;
    private javax.swing.JButton reloadButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaCompras;
    private javax.swing.JLabel tableInformationLabel;
    // End of variables declaration//GEN-END:variables
}
