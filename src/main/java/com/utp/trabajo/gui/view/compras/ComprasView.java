package com.utp.trabajo.gui.view.compras;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.utp.trabajo.services.ComprasService;
import com.utp.trabajo.services.security.SecurityService;
import com.utp.trabajo.services.util.IconService;
import com.utp.trabajo.services.util.UtilService;
import java.beans.PropertyVetoException;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ComprasView extends javax.swing.JInternalFrame {

    @Autowired
    private ComprasService comprasService;

    @Autowired
    private UtilService utilidades;

    @Autowired
    private IconService iconos;

    public ComprasView() {
        initComponents();
        try {
            setMaximum(true);
        } catch (PropertyVetoException ex) {
        }
    }

    @PostConstruct
    private void init() {
        setFrameIcon(iconos.iconoCompras);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();

        setClosable(true);
        setIconifiable(true);
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
    
    public void abrirVentana() {
        setVisible(false);
        com.formdev.flatlaf.FlatLaf.updateUI();

        //Colocar tabs aquí
        tabbedPane.add("Compras", getListaComprasTabInstance());
        tabbedPane.add("Proveedores", getProveedoresTabInstance());

        setVisible(true);
    }

    public void cerrarVentana() {
        tabbedPane.removeAll();
    }

    @Autowired
    private ObjectFactory<ProveedoresTab> proveedoresTabObjectFactory;

    public ProveedoresTab getProveedoresTabInstance() {
        return proveedoresTabObjectFactory.getObject();
    }

    @Autowired
    private ObjectFactory<ListaComprasTab> listaComprasTabObjectFactory;

    public ListaComprasTab getListaComprasTabInstance() {
        return listaComprasTabObjectFactory.getObject();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
