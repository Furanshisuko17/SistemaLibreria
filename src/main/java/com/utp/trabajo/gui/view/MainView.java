package com.utp.trabajo.gui.view;

import com.utp.trabajo.gui.view.ventas.VentasView;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.utp.trabajo.exception.UsernameNotFoundException;
import com.utp.trabajo.exception.WrongPasswordException;
import com.utp.trabajo.services.security.AuthService;
import com.utp.trabajo.services.util.IconService;
import com.utp.trabajo.services.util.UtilService;
import java.awt.event.ItemEvent;
import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import javax.swing.JInternalFrame;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.ApplicationContext;

@org.springframework.stereotype.Component
public class MainView extends javax.swing.JFrame {
	
	//TODO: Implementar un menu principal

	@Autowired
	private ApplicationContext context;
    
    @Autowired
	private AuthService authService;

	@Autowired 
	private UtilService utilidades;
	
	@Autowired 
	private IconService iconos;

	public MainView() {
        defineUI();
		initComponents();
        //toolBar.add(userButton);
        busyLabel.setEnabled(false);
		busyLabel.setBusy(true);
        loginPrompt.pack();
        loginPrompt.setLocationRelativeTo(this);
        //loginPrompt.setVisible(true); 
	}
        
	@PostConstruct
	private void init(){
		iconInit();
		setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        
	}
    
    private void defineUI() {
        UIManager.put("PasswordField.showRevealButton", true );
        UIManager.put("PasswordField.showCapsLock", true);
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
        
        userButton.setIcon(iconos.iconoCuenta);
        logoutButton.setIcon(iconos.iconoLogout);
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        appearanceButtonGroup = new javax.swing.ButtonGroup();
        loginPrompt = new javax.swing.JDialog(this, false);
        AutenticationPanelMain = new javax.swing.JPanel();
        InfoLabel = new javax.swing.JLabel();
        UsernameLabel = new javax.swing.JLabel();
        usernameInput = new javax.swing.JTextField();
        PasswordLabel = new javax.swing.JLabel();
        passwordInput = new com.formdev.flatlaf.extras.components.FlatPasswordField();
        informationLabel = new org.jdesktop.swingx.JXLabel();
        BottomPanel = new javax.swing.JPanel();
        loginButton = new javax.swing.JButton();
        Separator = new com.formdev.flatlaf.extras.components.FlatSeparator();
        busyLabel = new org.jdesktop.swingx.JXBusyLabel(new java.awt.Dimension(22, 22));
        panelPrincipal = new javax.swing.JDesktopPane();
        toolBar = new javax.swing.JToolBar();
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
        userButton = new javax.swing.JMenu();
        logoutButton = new javax.swing.JMenuItem();

        loginPrompt.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        loginPrompt.setTitle("Librería El Estudiante");
        loginPrompt.setAlwaysOnTop(true);
        loginPrompt.setName("loginDialog"); // NOI18N
        loginPrompt.setResizable(false);
        loginPrompt.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                loginPromptWindowClosing(evt);
            }
        });

        InfoLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        InfoLabel.setText("Iniciar sesión");

        UsernameLabel.setText("Usuario:");
        UsernameLabel.setToolTipText("Especifique el usuario de la base de datos");

        usernameInput.setText("fran");
        usernameInput.setToolTipText("");

        PasswordLabel.setText("Contraseña:");
        PasswordLabel.setToolTipText("Especifique la contraseña de la base de datos");

        passwordInput.setText("123");
        passwordInput.setToolTipText("");
        passwordInput.setName(""); // NOI18N
        passwordInput.setPlaceholderText("");

        javax.swing.GroupLayout AutenticationPanelMainLayout = new javax.swing.GroupLayout(AutenticationPanelMain);
        AutenticationPanelMain.setLayout(AutenticationPanelMainLayout);
        AutenticationPanelMainLayout.setHorizontalGroup(
            AutenticationPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AutenticationPanelMainLayout.createSequentialGroup()
                .addGroup(AutenticationPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AutenticationPanelMainLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(InfoLabel))
                    .addGroup(AutenticationPanelMainLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(AutenticationPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(informationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(AutenticationPanelMainLayout.createSequentialGroup()
                                .addGroup(AutenticationPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(PasswordLabel)
                                    .addComponent(UsernameLabel))
                                .addGap(7, 7, 7)
                                .addGroup(AutenticationPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(passwordInput, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(usernameInput, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(0, 25, Short.MAX_VALUE))
        );
        AutenticationPanelMainLayout.setVerticalGroup(
            AutenticationPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AutenticationPanelMainLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(InfoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AutenticationPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AutenticationPanelMainLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(UsernameLabel))
                    .addComponent(usernameInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(AutenticationPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PasswordLabel)
                    .addComponent(passwordInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(informationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        loginButton.setText("Iniciar sesión");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout BottomPanelLayout = new javax.swing.GroupLayout(BottomPanel);
        BottomPanel.setLayout(BottomPanelLayout);
        BottomPanelLayout.setHorizontalGroup(
            BottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Separator, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(BottomPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(loginButton)
                .addGap(25, 25, 25))
        );
        BottomPanelLayout.setVerticalGroup(
            BottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BottomPanelLayout.createSequentialGroup()
                .addComponent(Separator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(BottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(loginButton)
                    .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout loginPromptLayout = new javax.swing.GroupLayout(loginPrompt.getContentPane());
        loginPrompt.getContentPane().setLayout(loginPromptLayout);
        loginPromptLayout.setHorizontalGroup(
            loginPromptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(loginPromptLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(AutenticationPanelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        loginPromptLayout.setVerticalGroup(
            loginPromptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginPromptLayout.createSequentialGroup()
                .addComponent(AutenticationPanelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(BottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

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

        toolBar.setRollover(true);

        openVentasWindowButton.setText("Ventas");
        openVentasWindowButton.setFocusable(false);
        openVentasWindowButton.setHideActionText(true);
        openVentasWindowButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        openVentasWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openVentasWindowButtonActionPerformed(evt);
            }
        });
        toolBar.add(openVentasWindowButton);

        openComprasWindowButton.setText("Compras");
        openComprasWindowButton.setFocusable(false);
        openComprasWindowButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        openComprasWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openComprasWindowButtonActionPerformed(evt);
            }
        });
        toolBar.add(openComprasWindowButton);

        openAlmacenWindowButton.setText("Almacén");
        openAlmacenWindowButton.setFocusable(false);
        openAlmacenWindowButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        openAlmacenWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAlmacenWindowButtonActionPerformed(evt);
            }
        });
        toolBar.add(openAlmacenWindowButton);

        openEstadisticasWindowButton.setText("Estadísticas");
        openEstadisticasWindowButton.setFocusable(false);
        openEstadisticasWindowButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        openEstadisticasWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openEstadisticasWindowButtonActionPerformed(evt);
            }
        });
        toolBar.add(openEstadisticasWindowButton);

        openAdministracionWindowButton.setText("Administración");
        openAdministracionWindowButton.setFocusable(false);
        openAdministracionWindowButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        openAdministracionWindowButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openAdministracionWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAdministracionWindowButtonActionPerformed(evt);
            }
        });
        toolBar.add(openAdministracionWindowButton);

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

        menuBar.add(javax.swing.Box.createHorizontalGlue());

        logoutButton.setText("Cerrar sesión");
        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });
        userButton.add(logoutButton);

        menuBar.add(userButton);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panelPrincipal))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void setBusy() {
		busyLabel.setEnabled(true);
	}
	
	private void setIdle() {
		busyLabel.setEnabled(false);
	}
    
    private void resetUI(){
        //TODO: Gets back the main interface to the default values
    }
    
    private void closeAllInternalWindows() {
        for (JInternalFrame frame : panelPrincipal.getAllFrames()) {
            frame.dispose();
        }       
    }
    
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
		System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed
	
    private void openVentasWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openVentasWindowButtonActionPerformed
		VentasView ventanaVentas =  context.getBean(VentasView.class);
		
		ventanaVentas.addInternalFrameListener(new InternalFrameListener() {
			@Override
			public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
				openVentasWindowButton.setEnabled(true);
                ventanaVentas.cerrarVentana();
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
		ventanaVentas.abrirVentana();
		openVentasWindowButton.setEnabled(false);
    }//GEN-LAST:event_openVentasWindowButtonActionPerformed

    private void openComprasWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openComprasWindowButtonActionPerformed
        ComprasView comprasView = context.getBean(ComprasView.class);
		
		comprasView.addInternalFrameListener(new InternalFrameListener() {
			@Override
			public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
				openComprasWindowButton.setEnabled(true);
                comprasView.cerrarVentana();
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
		comprasView.abrirVentana();
		openComprasWindowButton.setEnabled(false);
    }//GEN-LAST:event_openComprasWindowButtonActionPerformed

    private void openAlmacenWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openAlmacenWindowButtonActionPerformed
        AlmacenView almacenView = context.getBean(AlmacenView.class);
		
		almacenView.addInternalFrameListener(new InternalFrameListener() {
			@Override
			public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
				openAlmacenWindowButton.setEnabled(true);
                almacenView.cerrarVentana();
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
		almacenView.abrirVentana();
		openAlmacenWindowButton.setEnabled(false);
    }//GEN-LAST:event_openAlmacenWindowButtonActionPerformed

    private void openEstadisticasWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openEstadisticasWindowButtonActionPerformed
        EstadisticasView estadisticasView = context.getBean(EstadisticasView.class);
		
		estadisticasView.addInternalFrameListener(new InternalFrameListener() {
			@Override
			public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
				openEstadisticasWindowButton.setEnabled(true);
                estadisticasView.cerrarVentana();
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
		estadisticasView.abrirVentana();
		openEstadisticasWindowButton.setEnabled(false);
    }//GEN-LAST:event_openEstadisticasWindowButtonActionPerformed

    private void openAdministracionWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openAdministracionWindowButtonActionPerformed
        AdministracionView administracionView = context.getBean(AdministracionView.class);
		
		administracionView.addInternalFrameListener(new InternalFrameListener() {
			@Override
			public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
				openAdministracionWindowButton.setEnabled(true);
                administracionView.cerrarVentana();
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
		administracionView.abrirVentana();
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

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        usernameInput.putClientProperty("JComponent.outline", "");
        passwordInput.putClientProperty("JComponent.outline", "");
        setBusy();

        String username = usernameInput.getText();
        if (username.isBlank()) {
            usernameInput.putClientProperty("JComponent.outline", "error");
            informationLabel.setText("El nombre de usuario está vacío.");
            setIdle();
            return;
        }

        char[] rawPassword = passwordInput.getPassword();
        if (String.valueOf(rawPassword).isBlank()) {
            passwordInput.putClientProperty("JComponent.outline", "error");
            informationLabel.setText("La contraseña está vacío.");
            setIdle();
            return;
        }

        SwingWorker swingWorker = new SwingWorker<Boolean, Boolean>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                boolean logeo = authService.login(username, rawPassword);
                return logeo;
            }

            @Override
            protected void done() {
                boolean logeoExitoso = false;
                try {
                    logeoExitoso = get();
                } catch (InterruptedException ex) {
                } catch (ExecutionException ex) {
                    try {
                        throw ex.getCause();

                    } catch (UsernameNotFoundException e) {
                        usernameInput.putClientProperty("JComponent.outline", "error");
                        informationLabel.setText(e.getMessage());
                        setIdle();
                    } catch (WrongPasswordException e) {
                        passwordInput.putClientProperty("JComponent.outline", "error");
                        informationLabel.setText(e.getMessage());
                        setIdle();
                    } catch (Throwable imp) {
                        System.out.println("impossible!: \n");
                        imp.printStackTrace();
                        System.out.println("impossible end!: \n");
                    }
                }
                if (logeoExitoso) {
                    passwordInput.putClientProperty("JComponent.outline", "");
                    usernameInput.putClientProperty("JComponent.outline", "");
                    setIdle();
                    loginPrompt.setVisible(false);
                    setVisible(true);
                    userButton.setText(authService.getLoggedEmpleado().getUsername());
                } else {

                }
            }
        };

        swingWorker.execute();
    }//GEN-LAST:event_loginButtonActionPerformed

    private void loginPromptWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_loginPromptWindowClosing
        System.exit(0);
    }//GEN-LAST:event_loginPromptWindowClosing
    
    private void logoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutButtonActionPerformed
        authService.logout();
        closeAllInternalWindows();
        //Thread.currentThread().interrupt();
        this.dispose();
        //DONE: redo this by restarting the context application
        //TODO: low priority, place a loading window while logging out
    }//GEN-LAST:event_logoutButtonActionPerformed
    
   
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AutenticationPanelMain;
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JLabel InfoLabel;
    private javax.swing.JLabel PasswordLabel;
    private com.formdev.flatlaf.extras.components.FlatSeparator Separator;
    private javax.swing.JLabel UsernameLabel;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu appearanceButton;
    private javax.swing.ButtonGroup appearanceButtonGroup;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JRadioButtonMenuItem darkModeButton;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private org.jdesktop.swingx.JXLabel informationLabel;
    private javax.swing.JRadioButtonMenuItem lightModeButton;
    private javax.swing.JButton loginButton;
    private javax.swing.JDialog loginPrompt;
    private javax.swing.JMenuItem logoutButton;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton openAdministracionWindowButton;
    private javax.swing.JButton openAlmacenWindowButton;
    private javax.swing.JButton openComprasWindowButton;
    private javax.swing.JButton openEstadisticasWindowButton;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JButton openVentasWindowButton;
    private javax.swing.JDesktopPane panelPrincipal;
    private com.formdev.flatlaf.extras.components.FlatPasswordField passwordInput;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JMenu userButton;
    private javax.swing.JTextField usernameInput;
    // End of variables declaration//GEN-END:variables

}
