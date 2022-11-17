package com.utp.trabajo.gui.view.compras;

import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.ProveedorService;
import com.utp.trabajo.model.entities.Proveedor;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;

public class ProovedoresTab extends org.jdesktop.swingx.JXPanel {
    DefaultTableModel defaultTableModelProveedores = new DefaultTableModel() {
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
                default:
                    return String.class;
            }
        }
    };

    String[] columnNames = {"IdProveedor", "Nombre", "Direccion", "RUC", "Teléfono", "Tipo de comercio"};
    private boolean canRead = true;

    private long lastId = 0;
    
    private long limit = 100;
    @Autowired
    private SecurityService securityService;

    @Autowired
    private ProveedorService proveedoresService;
    
    public ProovedoresTab() {
        initComponents();
        defaultTableModelProveedores.setColumnIdentifiers(columnNames);
        tablaProovedores.setModel(defaultTableModelProveedores);
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
        System.out.println("Proovedores tab - Nueva instancia!");
    }
    @PostConstruct
    private void init() {
        checkPermissions();
        retrieveData(false); // mover hacia un listener que verifique que se ha abierto el jPanel
    }

    private void checkPermissions() {
        List<String> permissions = securityService.getPermissions();
        //read, create, edit, delete
        if (!permissions.contains("read")) {
            canRead = false;
            nuevoProveedorButton.setEnabled(false);
            reloadButton.setEnabled(false);
        }
        if (!permissions.contains("write")) {
            nuevoProveedorButton.setEnabled(false);
            editarProveedorButton.setEnabled(false);
            eliminarProveedorButton.setEnabled(false);
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
		nuevoProveedorButton.setEnabled(false);
        
        if (reload) {
            defaultTableModelProveedores.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }

        SwingWorker worker = new SwingWorker<List<Proveedor>, List<Proveedor>>() {
            @Override
            protected List<Proveedor> doInBackground() throws Exception {
                return proveedoresService.streamProveedores(lastId, limit); // set lastId and configurable limit
            }

            @Override
            protected void done() {
                try {
                    var proveedores = get();
                    for (Proveedor proveedor : proveedores) {
                        Object[] values = new Object[6];
                        values[0] = proveedor.getIdProveedor();
                        values[1] = proveedor.getNombre();
                        values[2] = proveedor.getDireccion();
                        values[3] = proveedor.getRuc();
                        values[4] = proveedor.getTelefono();
                        values[5] = proveedor.getTipoComercio();
                        defaultTableModelProveedores.addRow(values);
                    }
                    int lastRow = 0;
                    int rowCount = defaultTableModelProveedores.getRowCount();
                    if (rowCount != 0) {
                        lastRow = rowCount - 1;
                    }
                    var id = defaultTableModelProveedores.getValueAt(lastRow, 0);
                    lastId = Long.parseLong(id.toString());
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(ProovedoresTab.class.getName()).log(Level.SEVERE, null, ex);
                }
                setIdle();
				reloadButton.setEnabled(true);
				nuevoProveedorButton.setEnabled(true);
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

        jLayeredPane1 = new javax.swing.JLayeredPane();
        scrollPane = new javax.swing.JScrollPane();
        tablaProovedores = new org.jdesktop.swingx.JXTable();
        nuevoProveedorButton = new javax.swing.JButton();
        editarProveedorButton = new javax.swing.JButton();
        eliminarProveedorButton = new javax.swing.JButton();
        reloadButton = new javax.swing.JButton();
        loadMoreButton = new javax.swing.JButton();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));

        tablaProovedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        scrollPane.setViewportView(tablaProovedores);

        jLayeredPane1.setLayer(scrollPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
        );

        nuevoProveedorButton.setText("Nuevo");

        editarProveedorButton.setText("Editar");

        eliminarProveedorButton.setText("Eliminar");

        reloadButton.setText("Recargar");

        loadMoreButton.setText("Cargar más");

        busyLabel.setBusy(true);
        busyLabel.setPreferredSize(new java.awt.Dimension(22, 22));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nuevoProveedorButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editarProveedorButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(eliminarProveedorButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(reloadButton)
                        .addGap(1, 1, 1)
                        .addComponent(loadMoreButton))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nuevoProveedorButton)
                    .addComponent(editarProveedorButton)
                    .addComponent(eliminarProveedorButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(reloadButton)
                        .addComponent(loadMoreButton))
                    .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton editarProveedorButton;
    private javax.swing.JButton eliminarProveedorButton;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JButton loadMoreButton;
    private javax.swing.JButton nuevoProveedorButton;
    private javax.swing.JButton reloadButton;
    private javax.swing.JScrollPane scrollPane;
    private org.jdesktop.swingx.JXTable tablaProovedores;
    // End of variables declaration//GEN-END:variables
}
