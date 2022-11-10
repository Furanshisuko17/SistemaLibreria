package com.utp.trabajo.gui.view.ventas;

import com.utp.trabajo.model.entities.Venta;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class VentasTab extends javax.swing.JPanel {
    
    DefaultTableModel defaultTableModelVentas = new DefaultTableModel() {
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
    
    private boolean canRead = true;

    private long lastId = 0;
    
    private long limit = 100;

    public VentasTab() {
        initComponents();
    }
    
    @PostConstruct
    public void init() {
        
    }
    
    private void checkPermissions() {
        
    }
    
    private void setBusy() {
		//busyLabel.setEnabled(true);
	}
    
    private void setBusy(String message) {
		//busyLabel.setEnabled(true);
        //busyLabel.setText(message);
	}
	
	private void setIdle() {
		//busyLabel.setEnabled(false);
        //busyLabel.setText("");
	}
       
    
    private void retrieveData(boolean reload) {
        if(!canRead) {
            setBusy("Sin permisos suficientes para leer datos.");
            return;
        }
        
        setBusy("Cargando...");
        if(reload) {
            defaultTableModelVentas.setRowCount(0);
            lastId = 0;
            setBusy("Recargando...");
        }
        SwingWorker worker = new SwingWorker<List<Venta>, List<Venta>>()  {
            @Override
            protected List<Venta> doInBackground() throws Exception {
                 return new ArrayList<Venta>();// set lastId and configurable limit
            }

            @Override
            protected void done() {
                
                
                setIdle();
            }
        };
        worker.execute();
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
