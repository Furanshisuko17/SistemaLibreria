package com.utp.trabajo.gui.view.ventas;

import com.utp.trabajo.services.util.IconService;
import com.utp.trabajo.services.util.UtilService;
import com.utp.trabajo.services.VentasService;
import com.utp.trabajo.services.security.SecurityService;
import javax.annotation.PostConstruct;
import javax.swing.UIManager;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VentasView extends javax.swing.JInternalFrame {

	@Autowired
	private VentasService ventasService;

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
	}
	
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();

        setClosable(true);
        setIconifiable(true);
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
    
    public void abrirVentana() {
        setVisible(false);
        com.formdev.flatlaf.FlatLaf.updateUI();
        tabbedPane.add("Nueva venta" , getNuevaVentaTabInstance());
        tabbedPane.add("Ventas", getVentasTabInstance());
        tabbedPane.add("Clientes", getClientesTabInstance());
        setVisible(true);
    }
    
    public void cerrarVentana() {
        tabbedPane.removeAll();
    }
           
    @Autowired
    private ObjectFactory<ClientesTab> clientesTabObjectFactory;
    
    public ClientesTab getClientesTabInstance() {
        return clientesTabObjectFactory.getObject();
    }
    
    @Autowired
    private ObjectFactory<VentasTab> ventasTabObjectFactory;
    
    public VentasTab getVentasTabInstance() {
        return ventasTabObjectFactory.getObject();
    }
    
    @Autowired
    private ObjectFactory<NuevaVentaTab> nuevaVentaTabObjectFactory;
    
    public NuevaVentaTab getNuevaVentaTabInstance() {
        return nuevaVentaTabObjectFactory.getObject();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
