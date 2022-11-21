package com.utp.trabajo.gui.view;

import com.utp.trabajo.gui.view.almacen.AlmacenView;
import com.utp.trabajo.gui.view.compras.ComprasView;
import com.utp.trabajo.gui.view.ventas.VentasView;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.utp.trabajo.exception.UsernameNotFoundException;
import com.utp.trabajo.exception.WrongPasswordException;
import com.utp.trabajo.model.entities.Empleado;
import com.utp.trabajo.services.security.AuthService;
import com.utp.trabajo.services.util.IconService;
import com.utp.trabajo.services.util.UtilService;
import java.awt.event.ItemEvent;
import java.util.concurrent.ExecutionException;
import javax.annotation.PostConstruct;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    private PlainDocument descripcionDocument;
    
    Empleado loggedEmpleado;

	public MainView() {
        defineUI();
		initComponents();
        //toolBar.add(userButton);
        busyLabel.setEnabled(false);
		busyLabel.setBusy(true);
        loginPrompt.pack();
        loginPrompt.setLocationRelativeTo(this);
        //loginPrompt.setVisible(true); 
		cuentaWindow.pack();
		cuentaWindow.setLocationRelativeTo(this);
        
        descripcionDocument = new PlainDocument();
        descripcionTextArea.setDocument(descripcionDocument);
        descripcionDocument.setDocumentFilter(new DocumentFilter() {
            int maxCharacters = 500;
            //FIX ??-??
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                int documentLength = fb.getDocument().getLength();
                int incomingTextLength = string.length();
                if (documentLength + incomingTextLength <= maxCharacters) {
                    super.insertString(fb, offset, string, attr);
                }else {
                    String truncatedString = string.substring(0, maxCharacters - documentLength);
                    //^ maybe check if incomingTextLength == 0...
                    super.insertString(fb, offset, truncatedString, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int documentLength = fb.getDocument().getLength();
                int incomingTextLength = text.length();
                if (documentLength + incomingTextLength <= maxCharacters) {
                    super.insertString(fb, offset, text, attrs);
                }else {
                    String truncatedString = text.substring(0, maxCharacters - documentLength);
                    //^ maybe check if incomingTextLength == 0...
                   super.insertString(fb, offset, truncatedString, attrs);
                }
                
            }
            
            
        });
        descripcionDocument.addDocumentListener(new DocumentListener() {
            @Override   
            public void insertUpdate(DocumentEvent e) {
                updateCharsLabel();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCharsLabel();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
    }
        
	@PostConstruct
	private void init(){
		iconInit();
        pack();
		setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        
	}
    
    private void defineUI() {
        UIManager.put("PasswordField.showRevealButton", true );
        UIManager.put("PasswordField.showCapsLock", true);
    }
	
	private void iconInit() {
		setIconImage(iconos.iconoPrincipal.getImage()); // Icono de la ventana principal
		openVentasWindowButton.setIcon(iconos.iconoVentas.derive(24, 24));
		openComprasWindowButton.setIcon(iconos.iconoCompras.derive(24, 24));
		openAlmacenWindowButton.setIcon(iconos.iconoAlmacen.derive(24, 24));
		openEstadisticasWindowButton.setIcon(iconos.iconoEstadisticas.derive(24, 24));
		openAdministracionWindowButton.setIcon(iconos.iconoAdministracion.derive(24, 24));
		
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
        cuentaWindow = new javax.swing.JDialog();
        nombreLabel = new javax.swing.JLabel();
        nombresField = new javax.swing.JTextField();
        apellidoLabel = new javax.swing.JLabel();
        apellidosField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        dniField = new javax.swing.JTextField();
        direccionLabel = new javax.swing.JLabel();
        direccionField = new javax.swing.JTextField();
        fechaNacimientoDatePicker = new org.jdesktop.swingx.JXDatePicker();
        fechaNacimientoLabel = new javax.swing.JLabel();
        fechaContratacionLabel = new javax.swing.JLabel();
        fechaContratoDatePicker = new org.jdesktop.swingx.JXDatePicker();
        fechaCeseLabel = new javax.swing.JLabel();
        fechaCeseDatePicker = new org.jdesktop.swingx.JXDatePicker();
        datosPersonalesLabel = new javax.swing.JLabel();
        telefonoLabel = new javax.swing.JLabel();
        telefonoField = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        usuarioLabel = new javax.swing.JLabel();
        contrasenaLabel = new javax.swing.JLabel();
        usuarioField = new javax.swing.JTextField();
        contrasenaField = new com.formdev.flatlaf.extras.components.FlatPasswordField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descripcionTextArea = new javax.swing.JTextArea();
        cerrarVentana = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        aceptarButon = new javax.swing.JButton();
        maxCaracteresLabel = new javax.swing.JLabel();
        panelPrincipal = new javax.swing.JDesktopPane();
        toolBar = new javax.swing.JToolBar();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        openVentasWindowButton = new javax.swing.JButton();
        openComprasWindowButton = new javax.swing.JButton();
        openAlmacenWindowButton = new javax.swing.JButton();
        openEstadisticasWindowButton = new javax.swing.JButton();
        openAdministracionWindowButton = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
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
        userDetailsButton = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
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

        cuentaWindow.setTitle("Configuración de la cuenta");
        cuentaWindow.setResizable(false);
        cuentaWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                cuentaWindowWindowClosing(evt);
            }
        });

        nombreLabel.setText("Nombres:");

        apellidoLabel.setText("Apellidos:");

        jLabel1.setText("DNI:");

        direccionLabel.setText("Dirección:");

        fechaNacimientoDatePicker.setEditable(false);
        fechaNacimientoDatePicker.setPreferredSize(new java.awt.Dimension(143, 22));

        fechaNacimientoLabel.setText("Fecha de nacimiento:");

        fechaContratacionLabel.setText("Fecha de contrato:");

        fechaContratoDatePicker.setEditable(false);
        fechaContratoDatePicker.setPreferredSize(new java.awt.Dimension(143, 22));

        fechaCeseLabel.setText("Fecha de cese:");

        fechaCeseDatePicker.setEditable(false);
        fechaCeseDatePicker.setPreferredSize(new java.awt.Dimension(143, 22));

        datosPersonalesLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        datosPersonalesLabel.setText("Datos personales");

        telefonoLabel.setText("Teléfono:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Datos de inicio de sesión");

        usuarioLabel.setText("Usuario:");
        usuarioLabel.setAutoscrolls(true);

        contrasenaLabel.setText("Contraseña:");

        jLabel3.setText("Descripción / biografía: ");

        descripcionTextArea.setColumns(20);
        descripcionTextArea.setLineWrap(true);
        descripcionTextArea.setRows(5);
        descripcionTextArea.setTabSize(4);
        descripcionTextArea.setWrapStyleWord(true);
        jScrollPane2.setViewportView(descripcionTextArea);

        cerrarVentana.setText("Cerrar");
        cerrarVentana.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarVentanaActionPerformed(evt);
            }
        });

        jButton2.setText("?");

        aceptarButon.setText("Aceptar");

        maxCaracteresLabel.setText("500/500");

        javax.swing.GroupLayout cuentaWindowLayout = new javax.swing.GroupLayout(cuentaWindow.getContentPane());
        cuentaWindow.getContentPane().setLayout(cuentaWindowLayout);
        cuentaWindowLayout.setHorizontalGroup(
            cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator3)
            .addGroup(cuentaWindowLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cuentaWindowLayout.createSequentialGroup()
                        .addComponent(datosPersonalesLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(cuentaWindowLayout.createSequentialGroup()
                        .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(cuentaWindowLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(maxCaracteresLabel))
                            .addComponent(jScrollPane2)
                            .addGroup(cuentaWindowLayout.createSequentialGroup()
                                .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(cuentaWindowLayout.createSequentialGroup()
                                        .addGap(15, 15, 15)
                                        .addComponent(usuarioLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(usuarioField, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(34, 34, 34)
                                        .addComponent(contrasenaLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(contrasenaField, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel2)
                                    .addGroup(cuentaWindowLayout.createSequentialGroup()
                                        .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(cuentaWindowLayout.createSequentialGroup()
                                                .addComponent(direccionLabel)
                                                .addGap(11, 11, 11)
                                                .addComponent(direccionField))
                                            .addGroup(cuentaWindowLayout.createSequentialGroup()
                                                .addComponent(nombreLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(nombresField, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(cuentaWindowLayout.createSequentialGroup()
                                                .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(apellidoLabel)
                                                    .addComponent(jLabel1))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(apellidosField, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                                                    .addComponent(dniField))))
                                        .addGap(18, 18, 18)
                                        .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(cuentaWindowLayout.createSequentialGroup()
                                                .addComponent(fechaContratacionLabel)
                                                .addGap(27, 27, 27)
                                                .addComponent(fechaContratoDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(cuentaWindowLayout.createSequentialGroup()
                                                .addComponent(fechaCeseLabel)
                                                .addGap(49, 49, 49)
                                                .addComponent(fechaCeseDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(cuentaWindowLayout.createSequentialGroup()
                                                .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(fechaNacimientoLabel)
                                                    .addComponent(telefonoLabel))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(fechaNacimientoDatePicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(telefonoField))))))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cuentaWindowLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(aceptarButon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cerrarVentana)
                .addContainerGap())
        );
        cuentaWindowLayout.setVerticalGroup(
            cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cuentaWindowLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(6, 6, 6)
                .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usuarioLabel)
                    .addComponent(usuarioField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(contrasenaLabel)
                    .addComponent(contrasenaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(datosPersonalesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nombreLabel)
                    .addComponent(nombresField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(telefonoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(telefonoLabel))
                .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cuentaWindowLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fechaNacimientoDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fechaNacimientoLabel)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cuentaWindowLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(apellidoLabel)
                            .addComponent(apellidosField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fechaContratacionLabel)
                    .addComponent(fechaContratoDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dniField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fechaCeseLabel)
                    .addComponent(fechaCeseDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(direccionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(direccionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(maxCaracteresLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(cuentaWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cerrarVentana)
                    .addComponent(jButton2)
                    .addComponent(aceptarButon))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        toolBar.add(filler2);

        openVentasWindowButton.setText("Ventas");
        openVentasWindowButton.setFocusable(false);
        openVentasWindowButton.setHideActionText(true);
        openVentasWindowButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openVentasWindowButton.setIconTextGap(2);
        openVentasWindowButton.setMaximumSize(new java.awt.Dimension(91, 50));
        openVentasWindowButton.setMinimumSize(new java.awt.Dimension(92, 50));
        openVentasWindowButton.setPreferredSize(new java.awt.Dimension(92, 50));
        openVentasWindowButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openVentasWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openVentasWindowButtonActionPerformed(evt);
            }
        });
        toolBar.add(openVentasWindowButton);

        openComprasWindowButton.setText("Compras");
        openComprasWindowButton.setFocusable(false);
        openComprasWindowButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openComprasWindowButton.setIconTextGap(2);
        openComprasWindowButton.setMaximumSize(new java.awt.Dimension(91, 50));
        openComprasWindowButton.setMinimumSize(new java.awt.Dimension(92, 50));
        openComprasWindowButton.setPreferredSize(new java.awt.Dimension(92, 50));
        openComprasWindowButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openComprasWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openComprasWindowButtonActionPerformed(evt);
            }
        });
        toolBar.add(openComprasWindowButton);

        openAlmacenWindowButton.setText("Almacén");
        openAlmacenWindowButton.setFocusable(false);
        openAlmacenWindowButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openAlmacenWindowButton.setIconTextGap(2);
        openAlmacenWindowButton.setMaximumSize(new java.awt.Dimension(91, 50));
        openAlmacenWindowButton.setMinimumSize(new java.awt.Dimension(92, 50));
        openAlmacenWindowButton.setPreferredSize(new java.awt.Dimension(92, 50));
        openAlmacenWindowButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openAlmacenWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAlmacenWindowButtonActionPerformed(evt);
            }
        });
        toolBar.add(openAlmacenWindowButton);

        openEstadisticasWindowButton.setText("Estadísticas");
        openEstadisticasWindowButton.setFocusable(false);
        openEstadisticasWindowButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openEstadisticasWindowButton.setIconTextGap(2);
        openEstadisticasWindowButton.setMaximumSize(new java.awt.Dimension(91, 50));
        openEstadisticasWindowButton.setMinimumSize(new java.awt.Dimension(92, 50));
        openEstadisticasWindowButton.setPreferredSize(new java.awt.Dimension(92, 50));
        openEstadisticasWindowButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openEstadisticasWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openEstadisticasWindowButtonActionPerformed(evt);
            }
        });
        toolBar.add(openEstadisticasWindowButton);

        openAdministracionWindowButton.setText("Admin");
        openAdministracionWindowButton.setFocusable(false);
        openAdministracionWindowButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openAdministracionWindowButton.setIconTextGap(2);
        openAdministracionWindowButton.setMaximumSize(new java.awt.Dimension(91, 50));
        openAdministracionWindowButton.setMinimumSize(new java.awt.Dimension(92, 50));
        openAdministracionWindowButton.setPreferredSize(new java.awt.Dimension(92, 50));
        openAdministracionWindowButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        openAdministracionWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAdministracionWindowButtonActionPerformed(evt);
            }
        });
        toolBar.add(openAdministracionWindowButton);
        toolBar.add(filler1);

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
        userButton.setText("Usuario");

        userDetailsButton.setText("Configuración de la cuenta");
        userDetailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userDetailsButtonActionPerformed(evt);
            }
        });
        userButton.add(userDetailsButton);
        userButton.add(jSeparator1);

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
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panelPrincipal))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void initCuentaWindow() {
        fechaCeseDatePicker.setEnabled(false);
        fechaContratoDatePicker.setEnabled(false);
        fechaNacimientoDatePicker.setEnabled(false);
        dniField.setEditable(false);
        nombresField.setEditable(false);
        apellidosField.setEditable(false);
        
        fillCuentaWindow();
    }
    
    private void fillCuentaWindow() {
        nombresField.setText(loggedEmpleado.getNombres());
        apellidosField.setText(loggedEmpleado.getApellidos());
        dniField.setText(loggedEmpleado.getDni());
        direccionField.setText(loggedEmpleado.getDireccion());
        telefonoField.setText(loggedEmpleado.getTelefono());
        fechaNacimientoDatePicker.setDate(loggedEmpleado.getFechaNacimiento());
        fechaContratoDatePicker.setDate(loggedEmpleado.getFechaContratacion());
        fechaCeseDatePicker.setDate(loggedEmpleado.getFechaCese());
        descripcionTextArea.setText(loggedEmpleado.getDescripcion());

        usuarioField.setText(loggedEmpleado.getUsername());
    }
    
    private void resetCuentaWindow() {
        nombresField.setText(loggedEmpleado.getNombres());
        apellidosField.setText(loggedEmpleado.getApellidos());
        dniField.setText(loggedEmpleado.getDni());
        direccionField.setText(loggedEmpleado.getDireccion());
        telefonoField.setText(loggedEmpleado.getTelefono());
        fechaNacimientoDatePicker.setDate(loggedEmpleado.getFechaNacimiento());
        fechaContratoDatePicker.setDate(loggedEmpleado.getFechaContratacion());
        fechaCeseDatePicker.setDate(loggedEmpleado.getFechaCese());
        descripcionTextArea.setText(loggedEmpleado.getDescripcion());

        usuarioField.setText(loggedEmpleado.getUsername());
    }
    
    private void updateCharsLabel() {
        int lines = descripcionDocument.getLength();
        maxCaracteresLabel.setText(lines + "/500");
    }
    
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
        panelPrincipal.removeAll();
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
                    loggedEmpleado = authService.getLoggedEmpleado();
                    userButton.setText(loggedEmpleado.getUsername());
                    initCuentaWindow();

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

    private void userDetailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userDetailsButtonActionPerformed
		cuentaWindow.setVisible(true);
		
    }//GEN-LAST:event_userDetailsButtonActionPerformed

    private void cerrarVentanaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarVentanaActionPerformed
        cuentaWindow.setVisible(false);
        resetCuentaWindow();
    }//GEN-LAST:event_cerrarVentanaActionPerformed

    private void cuentaWindowWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_cuentaWindowWindowClosing
        resetCuentaWindow();
    }//GEN-LAST:event_cuentaWindowWindowClosing
    
   
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AutenticationPanelMain;
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JLabel InfoLabel;
    private javax.swing.JLabel PasswordLabel;
    private com.formdev.flatlaf.extras.components.FlatSeparator Separator;
    private javax.swing.JLabel UsernameLabel;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton aceptarButon;
    private javax.swing.JLabel apellidoLabel;
    private javax.swing.JTextField apellidosField;
    private javax.swing.JMenu appearanceButton;
    private javax.swing.ButtonGroup appearanceButtonGroup;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private javax.swing.JButton cerrarVentana;
    private com.formdev.flatlaf.extras.components.FlatPasswordField contrasenaField;
    private javax.swing.JLabel contrasenaLabel;
    private javax.swing.JDialog cuentaWindow;
    private javax.swing.JRadioButtonMenuItem darkModeButton;
    private javax.swing.JLabel datosPersonalesLabel;
    private javax.swing.JTextArea descripcionTextArea;
    private javax.swing.JTextField direccionField;
    private javax.swing.JLabel direccionLabel;
    private javax.swing.JTextField dniField;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private org.jdesktop.swingx.JXDatePicker fechaCeseDatePicker;
    private javax.swing.JLabel fechaCeseLabel;
    private javax.swing.JLabel fechaContratacionLabel;
    private org.jdesktop.swingx.JXDatePicker fechaContratoDatePicker;
    private org.jdesktop.swingx.JXDatePicker fechaNacimientoDatePicker;
    private javax.swing.JLabel fechaNacimientoLabel;
    private javax.swing.JMenu fileMenu;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JMenu helpMenu;
    private org.jdesktop.swingx.JXLabel informationLabel;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JRadioButtonMenuItem lightModeButton;
    private javax.swing.JButton loginButton;
    private javax.swing.JDialog loginPrompt;
    private javax.swing.JMenuItem logoutButton;
    private javax.swing.JLabel maxCaracteresLabel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel nombreLabel;
    private javax.swing.JTextField nombresField;
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
    private javax.swing.JTextField telefonoField;
    private javax.swing.JLabel telefonoLabel;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JMenu userButton;
    private javax.swing.JMenuItem userDetailsButton;
    private javax.swing.JTextField usernameInput;
    private javax.swing.JTextField usuarioField;
    private javax.swing.JLabel usuarioLabel;
    // End of variables declaration//GEN-END:variables

    
}
