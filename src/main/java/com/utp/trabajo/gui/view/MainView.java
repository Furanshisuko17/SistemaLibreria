package com.utp.trabajo.gui.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.utp.trabajo.model.entities.services.UtilService;
import javax.annotation.PostConstruct;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MainView extends javax.swing.JFrame {
	
	//TODO: Implementar un menu principal

	private FlatSVGIcon iconoVentana;
	
	@Autowired
	private ApplicationContext context;

	@Autowired 
	private UtilService utilidades;

	public MainView() {
		initComponents();
	}
        
	@PostConstruct
	private void init(){
		this.iconoVentana = utilidades.get16x16Icon("/icons/iconoPrincipal.svg");
		setIconImage(iconoVentana.getImage());
		setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        alphaPainter1 = new org.jdesktop.swingx.painter.AlphaPainter();
        alphaPainter2 = new org.jdesktop.swingx.painter.AlphaPainter();
        panelPrincipal = new javax.swing.JDesktopPane();
        jToolBar1 = new javax.swing.JToolBar();
        openVentasWindowButton = new javax.swing.JButton();
        openComprasWindowButton = new javax.swing.JButton();
        openAlmacenWindowButton = new javax.swing.JButton();
        openEstadisticasWindowButton = new javax.swing.JButton();
        openAdministracionWindowButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistema de ventas - Librería El Estudiante");

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 639, Short.MAX_VALUE)
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 341, Short.MAX_VALUE)
        );

        jToolBar1.setRollover(true);

        openVentasWindowButton.setText("Ventas");
        openVentasWindowButton.setFocusable(false);
        openVentasWindowButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openVentasWindowButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openVentasWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openVentasWindowButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(openVentasWindowButton);

        openComprasWindowButton.setText("Compras");
        openComprasWindowButton.setFocusable(false);
        openComprasWindowButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openComprasWindowButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openComprasWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openComprasWindowButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(openComprasWindowButton);

        openAlmacenWindowButton.setText("Almacén");
        openAlmacenWindowButton.setFocusable(false);
        openAlmacenWindowButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openAlmacenWindowButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openAlmacenWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAlmacenWindowButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(openAlmacenWindowButton);

        openEstadisticasWindowButton.setText("Estadísticas");
        openEstadisticasWindowButton.setFocusable(false);
        openEstadisticasWindowButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openEstadisticasWindowButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openEstadisticasWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openEstadisticasWindowButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(openEstadisticasWindowButton);

        openAdministracionWindowButton.setText("Administración");
        openAdministracionWindowButton.setFocusable(false);
        openAdministracionWindowButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openAdministracionWindowButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openAdministracionWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAdministracionWindowButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(openAdministracionWindowButton);

        fileMenu.setMnemonic('f');
        fileMenu.setText("Archivo");

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Abrir");
        fileMenu.add(openMenuItem);

        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Guardar");
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Guardar como...");
        fileMenu.add(saveAsMenuItem);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_DOWN_MASK));
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Salir");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Editar");
        menuBar.add(editMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Ayuda");

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("Acerca de");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panelPrincipal))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
		System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void openVentasWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openVentasWindowButtonActionPerformed
        
		VentasView ventanaVentas = context.getBean(VentasView.class);
       
		
		ventanaVentas.addInternalFrameListener(new InternalFrameListener() {
			@Override
			public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
				openVentasWindowButton.setEnabled(true);
			}
			@Override
			public void internalFrameOpened(InternalFrameEvent e) {}
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {}
			@Override
			public void internalFrameIconified(InternalFrameEvent e) {}
			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {}                        
			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) {}
		});
		panelPrincipal.add(ventanaVentas);
		ventanaVentas.setVisible(true);
		openVentasWindowButton.setEnabled(false);
    }//GEN-LAST:event_openVentasWindowButtonActionPerformed

    private void openComprasWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openComprasWindowButtonActionPerformed
        ComprasView comprasView = context.getBean(ComprasView.class);
		
		comprasView.addInternalFrameListener(new InternalFrameListener() {
			@Override
			public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
				openComprasWindowButton.setEnabled(true);
			}
			@Override
			public void internalFrameOpened(InternalFrameEvent e) {}
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {}
			@Override
			public void internalFrameIconified(InternalFrameEvent e) {}
			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {}                        
			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) {}
		});
		panelPrincipal.add(comprasView);
		comprasView.setVisible(true);
		openComprasWindowButton.setEnabled(false);
    }//GEN-LAST:event_openComprasWindowButtonActionPerformed

    private void openAlmacenWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openAlmacenWindowButtonActionPerformed
        AlmacenView almacenView = context.getBean(AlmacenView.class);
		
		almacenView.addInternalFrameListener(new InternalFrameListener() {
			@Override
			public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
				openAlmacenWindowButton.setEnabled(true);
			}
			@Override
			public void internalFrameOpened(InternalFrameEvent e) {}
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {}
			@Override
			public void internalFrameIconified(InternalFrameEvent e) {}
			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {}                        
			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) {}
		});
		panelPrincipal.add(almacenView);
		almacenView.setVisible(true);
		openAlmacenWindowButton.setEnabled(false);
    }//GEN-LAST:event_openAlmacenWindowButtonActionPerformed

    private void openEstadisticasWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openEstadisticasWindowButtonActionPerformed
        EstadisticasView estadisticasView = context.getBean(EstadisticasView.class);
		
		estadisticasView.addInternalFrameListener(new InternalFrameListener() {
			@Override
			public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
				openEstadisticasWindowButton.setEnabled(true);
			}
			@Override
			public void internalFrameOpened(InternalFrameEvent e) {}
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {}
			@Override
			public void internalFrameIconified(InternalFrameEvent e) {}
			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {}                        
			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) {}
		});
		panelPrincipal.add(estadisticasView);
		estadisticasView.setVisible(true);
		openEstadisticasWindowButton.setEnabled(false);
    }//GEN-LAST:event_openEstadisticasWindowButtonActionPerformed

    private void openAdministracionWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openAdministracionWindowButtonActionPerformed
        AdministracionView administracionView = context.getBean(AdministracionView.class);
		
		administracionView.addInternalFrameListener(new InternalFrameListener() {
			@Override
			public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
				openAdministracionWindowButton.setEnabled(true);
			}
			@Override
			public void internalFrameOpened(InternalFrameEvent e) {}
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {}
			@Override
			public void internalFrameIconified(InternalFrameEvent e) {}
			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {}                        
			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) {}
		});
		panelPrincipal.add(administracionView);
		administracionView.setVisible(true);
		openAdministracionWindowButton.setEnabled(false);
    }//GEN-LAST:event_openAdministracionWindowButtonActionPerformed
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private org.jdesktop.swingx.painter.AlphaPainter alphaPainter1;
    private org.jdesktop.swingx.painter.AlphaPainter alphaPainter2;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton openAdministracionWindowButton;
    private javax.swing.JButton openAlmacenWindowButton;
    private javax.swing.JButton openComprasWindowButton;
    private javax.swing.JButton openEstadisticasWindowButton;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JButton openVentasWindowButton;
    private javax.swing.JDesktopPane panelPrincipal;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    // End of variables declaration//GEN-END:variables

}
