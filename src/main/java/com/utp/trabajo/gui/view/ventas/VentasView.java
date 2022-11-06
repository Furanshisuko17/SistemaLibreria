package com.utp.trabajo.gui.view.ventas;

import com.utp.trabajo.services.util.IconService;
import com.utp.trabajo.services.util.UtilService;
import com.utp.trabajo.services.VentasService;
import com.utp.trabajo.services.security.SecurityService;
import javax.annotation.PostConstruct;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class VentasView extends javax.swing.JInternalFrame {

	@Autowired
	private VentasService ventasService;

	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private IconService iconos;

	@Autowired
	private UtilService utilidades;
	
	@Autowired
	private SecurityService securityService;
		
	public VentasView() {	
		initComponents();
	}
        
	@PostConstruct
	private void init(){
		setFrameIcon(iconos.iconoVentas);
		tabbedPane.add("Clientes", context.getBean(ClientesTab.class));
		
	}
	
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Ventas");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
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
