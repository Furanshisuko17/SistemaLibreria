package com.utp.trabajo.gui.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.utp.trabajo.model.entities.services.UtilService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ComprasView extends javax.swing.JInternalFrame {

	private FlatSVGIcon iconoVentana;
	
	@Autowired
	private UtilService utilidades;
        
	@PostConstruct
	private void init(){
		this.iconoVentana = utilidades.get16x16Icon("/icons/comprasIcono.svg");
		setFrameIcon(iconoVentana);
	}
		
	public ComprasView() {
		
		initComponents();
	}


	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Compras");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 394, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
