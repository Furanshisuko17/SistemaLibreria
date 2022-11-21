package com.utp.trabajo.gui.view;

import com.utp.trabajo.exception.auth.UsernameNotFoundException;
import com.utp.trabajo.exception.auth.WrongPasswordException;
import com.utp.trabajo.services.security.AuthService;
import java.awt.EventQueue;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Deprecated
public class LoginView extends javax.swing.JFrame {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private AuthService authService;
	
	public LoginView() {
		initComponents();
		busyLabel.setEnabled(false);
		busyLabel.setBusy(true);
	}
	//object.putClientProperty("JComponent.outline", "error");

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Iniciar sesión");
        setAlwaysOnTop(true);
        setResizable(false);

        InfoLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        InfoLabel.setText("Iniciar sesión");

        UsernameLabel.setText("Usuario:");
        UsernameLabel.setToolTipText("Especifique el usuario de la base de datos");

        usernameInput.setText("Fran");
        usernameInput.setToolTipText("");

        PasswordLabel.setText("Contraseña:");
        PasswordLabel.setToolTipText("Especifique la contraseña de la base de datos");

        passwordInput.setText("Fran1234");
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BottomPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(loginButton)
                .addGap(26, 26, 26))
        );
        BottomPanelLayout.setVerticalGroup(
            BottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BottomPanelLayout.createSequentialGroup()
                .addComponent(Separator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(BottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(loginButton)
                    .addComponent(busyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(AutenticationPanelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(AutenticationPanelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(BottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
	
	private void setBusy() {
		busyLabel.setEnabled(true);
	}
	
	private void setIdle() {
		busyLabel.setEnabled(false);
	}

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
					}
				}
				if (logeoExitoso) {
					passwordInput.putClientProperty("JComponent.outline", "");
					usernameInput.putClientProperty("JComponent.outline", "");
					setIdle();
					setVisible(false);
					EventQueue.invokeLater(() -> {
						MainView mainView = context.getBean(MainView.class);
						mainView.setVisible(true);
					});
				} else {

				}
			}
		};
		
		swingWorker.execute();
    }//GEN-LAST:event_loginButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AutenticationPanelMain;
    private javax.swing.JPanel BottomPanel;
    private javax.swing.JLabel InfoLabel;
    private javax.swing.JLabel PasswordLabel;
    private com.formdev.flatlaf.extras.components.FlatSeparator Separator;
    private javax.swing.JLabel UsernameLabel;
    private org.jdesktop.swingx.JXBusyLabel busyLabel;
    private org.jdesktop.swingx.JXLabel informationLabel;
    private javax.swing.JButton loginButton;
    private com.formdev.flatlaf.extras.components.FlatPasswordField passwordInput;
    private javax.swing.JTextField usernameInput;
    // End of variables declaration//GEN-END:variables
}
