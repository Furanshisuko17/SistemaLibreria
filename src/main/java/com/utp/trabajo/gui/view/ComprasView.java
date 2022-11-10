package com.utp.trabajo.gui.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.utp.trabajo.services.util.IconService;
import com.utp.trabajo.services.util.UtilService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ComprasView extends javax.swing.JInternalFrame {

	@Autowired
	private UtilService utilidades;
	
	@Autowired
	private IconService iconos;
	
	public ComprasView() {
		initComponents();
	}
    
	@PostConstruct
	private void init(){
		setFrameIcon(iconos.iconoCompras);
	}
		

    public void abrirVentana() {
        setVisible(false);
        com.formdev.flatlaf.FlatLaf.updateUI();
        
        //Colocar tabs aquí
        
        setVisible(true);
    }
    
    public void cerrarVentana() {
        tabbedPane.removeAll();
    }
    
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Compras");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
