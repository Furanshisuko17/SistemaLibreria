package com.utp.trabajo.gui.view.almacen;

import com.utp.trabajo.gui.view.productos.ProductoTab;
import com.utp.trabajo.services.util.IconService;
import com.utp.trabajo.services.util.UtilService;
import com.utp.trabajo.services.security.SecurityService;
import java.beans.PropertyVetoException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.ObjectFactory;

@Component
public class AlmacenView extends javax.swing.JInternalFrame {

    @Autowired
    private UtilService utilidades;

    @Autowired
    private IconService iconos;

    public AlmacenView() {
        initComponents();
        try {
            setMaximum(true);
        } catch (PropertyVetoException ex) {
        }
    }

    @PostConstruct
    private void init() {
        setFrameIcon(iconos.iconoAlmacen);
        
    }

    public void abrirVentana() {
        setVisible(false);
        com.formdev.flatlaf.FlatLaf.updateUI();

        tabbedPane.add("Almacén", getAlmacenTabInstance());
        tabbedPane.add("Marcas", getMarcaTabInstance());
        tabbedPane.add("Tipos de productos", getTipoTabInstance());
        
        //Colocar tabs aquí

        setVisible(true);
    }

    public void cerrarVentana() {
        tabbedPane.removeAll();
    }

    @Autowired
    private ObjectFactory<MarcaTab> MarcaTabObjectFactory;

    public MarcaTab getMarcaTabInstance() {
        return MarcaTabObjectFactory.getObject();
    }

    @Autowired
    private ObjectFactory<TipoTab> TipoTabObjectFactory;

    public TipoTab getTipoTabInstance() {
        return TipoTabObjectFactory.getObject();
    }


    @Autowired
    private ObjectFactory<AlmacenTab> AlmacenTabObjectFactory;

    public AlmacenTab getAlmacenTabInstance() {
        return AlmacenTabObjectFactory.getObject();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Almacén");
        setToolTipText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
