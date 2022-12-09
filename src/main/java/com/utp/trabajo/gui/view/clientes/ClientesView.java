package com.utp.trabajo.gui.view.clientes;

import com.utp.trabajo.services.util.IconService;
import com.utp.trabajo.services.util.UtilService;
import java.beans.PropertyVetoException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientesView extends javax.swing.JInternalFrame {

    @Autowired
    private UtilService utilidades;

    @Autowired
    private IconService iconos;

    public ClientesView() {
        initComponents();
        try {
            setMaximum(true);
        } catch (PropertyVetoException ex) {
        }
    }
    
    @PostConstruct
    private void init() {
        setFrameIcon(iconos.iconoCliente);
    }

    public void abrirVentana() {
        setVisible(false);
        com.formdev.flatlaf.FlatLaf.updateUI();
        tabbedPane.add("Clientes", getClientesTabInstance());
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
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Clientes");

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
    
    
    @Autowired
    private ObjectFactory<ClientesTab> clientesTabObjectFactory;

    public ClientesTab getClientesTabInstance() {
        return clientesTabObjectFactory.getObject();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
