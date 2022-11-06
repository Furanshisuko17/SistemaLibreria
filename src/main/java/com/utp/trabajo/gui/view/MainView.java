package com.utp.trabajo.gui.view;

import com.utp.trabajo.gui.view.ventas.VentasView;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.utp.trabajo.services.util.IconService;
import com.utp.trabajo.services.util.UtilService;
import java.awt.event.ItemEvent;
import javax.annotation.PostConstruct;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MainView extends javax.swing.JFrame {
	
	//TODO: Implementar un menu principal

	@Autowired
	private ApplicationContext context;

	@Autowired 
	private UtilService utilidades;
	
	@Autowired 
	private IconService iconos;

	public MainView() {
		initComponents();
	}
        
	@PostConstruct
	private void init(){
		iconInit();
		setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
	}
	
	private void iconInit() {
		setIconImage(iconos.iconoPrincipal.getImage()); // Icono de la ventana principal
		openVentasWindowButton.setIcon(iconos.iconoVentas);
		openComprasWindowButton.setIcon(iconos.iconoCompras);
		openAlmacenWindowButton.setIcon(iconos.iconoAlmacen);
		openEstadisticasWindowButton.setIcon(iconos.iconoEstadisticas);
		openAdministracionWindowButton.setIcon(iconos.iconoAdministracion);
		
		exitMenuItem.setIcon(iconos.iconoExit);
		appearanceButton.setIcon(iconos.iconoDarkMode);
		lightModeButton.setIcon(iconos.iconoLightMode);
		darkModeButton.setIcon(iconos.iconoDarkMode);
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        appearanceButtonGroup = new javax.swing.ButtonGroup();
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
        appearanceButton = new javax.swing.JMenu();
        darkModeButton = new javax.swing.JRadioButtonMenuItem();
        lightModeButton = new javax.swing.JRadioButtonMenuItem();
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
        openVentasWindowButton.setHideActionText(true);
        openVentasWindowButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        openVentasWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openVentasWindowButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(openVentasWindowButton);

        openComprasWindowButton.setText("Compras");
        openComprasWindowButton.setFocusable(false);
        openComprasWindowButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        openComprasWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openComprasWindowButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(openComprasWindowButton);

        openAlmacenWindowButton.setText("Almacén");
        openAlmacenWindowButton.setFocusable(false);
        openAlmacenWindowButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        openAlmacenWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAlmacenWindowButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(openAlmacenWindowButton);

        openEstadisticasWindowButton.setText("Estadísticas");
        openEstadisticasWindowButton.setFocusable(false);
        openEstadisticasWindowButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        openEstadisticasWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openEstadisticasWindowButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(openEstadisticasWindowButton);

        openAdministracionWindowButton.setText("Administración");
        openAdministracionWindowButton.setFocusable(false);
        openAdministracionWindowButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
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

        appearanceButton.setText("Apariencia");

        appearanceButtonGroup.add(darkModeButton);
        darkModeButton.setSelected(true);
        darkModeButton.setText("Oscuro");
        darkModeButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                darkModeButtonItemStateChanged(evt);
            }
        });
        appearanceButton.add(darkModeButton);

        appearanceButtonGroup.add(lightModeButton);
        lightModeButton.setText("Claro");
        appearanceButton.add(lightModeButton);

        editMenu.add(appearanceButton);

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

    private void darkModeButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_darkModeButtonItemStateChanged
		if(evt.getStateChange() == ItemEvent.SELECTED){
            FlatAnimatedLafChange.showSnapshot();
			appearanceButton.setIcon(iconos.iconoDarkMode);
            try {
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            } catch (Exception ex) {
                // MessageHandler.exceptionMessage(ex);
				System.out.println("Fail changing feel color to dark");
            }
			
            com.formdev.flatlaf.FlatLaf.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        }else if(evt.getStateChange() == ItemEvent.DESELECTED){
            FlatAnimatedLafChange.showSnapshot();
			appearanceButton.setIcon(iconos.iconoLightMode);
            try {
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            } catch (Exception ex) {
                // MessageHandler.exceptionMessage(ex);
				System.out.println("Fail changing feel color to light");
            }
            com.formdev.flatlaf.FlatLaf.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        }
		
    }//GEN-LAST:event_darkModeButtonItemStateChanged
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu appearanceButton;
    private javax.swing.ButtonGroup appearanceButtonGroup;
    private javax.swing.JRadioButtonMenuItem darkModeButton;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JRadioButtonMenuItem lightModeButton;
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
