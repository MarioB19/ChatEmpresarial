package ChatEmpresarial.client.pages;

import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.utilities.Enumerators.TipoRequest;
import ChatEmpresarial.shared.utilities.Functions;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.json.JSONObject;

public class ForgotPasswordPage2 extends JFrame {
    private JPasswordField newPassword;
    private JLabel newPasswordError;
    private String username;
    
    public ForgotPasswordPage2(String username) {
         this.username = username;
        setTitle("Restablecer Contraseña");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(173, 216, 230)); // Color de fondo
        backgroundPanel.setLayout(new GridBagLayout());

        JPanel resetPasswordPanel = new JPanel();
        resetPasswordPanel.setBackground(Color.WHITE);
        resetPasswordPanel.setLayout(new GridBagLayout());
        resetPasswordPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("Restablecer Contraseña");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        newPassword = new JPasswordField(20);
        newPassword.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        newPasswordError = new JLabel();
        newPasswordError.setForeground(Color.RED);
        newPasswordError.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton resetButton = new JButton("Restablecer Contraseña");
        resetButton.setBackground(new Color(135, 206, 250));
        resetButton.addActionListener(e -> handleResetPassword());

        JLabel backToLogin = new JLabel("Regresar al Login");
        backToLogin.setFont(new Font("Arial", Font.PLAIN, 16));
        backToLogin.setForeground(new Color(0, 0, 255));
        backToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        backToLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openLoginPage();
                dispose(); 
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        resetPasswordPanel.add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 4, 0);
        resetPasswordPanel.add(new JLabel("Nueva Contraseña"), gbc);

        gbc.gridy = 2;
        resetPasswordPanel.add(newPassword, gbc);

        gbc.gridy = 3;
        resetPasswordPanel.add(newPasswordError, gbc); 

        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 20, 0);
        resetPasswordPanel.add(resetButton, gbc);

        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        resetPasswordPanel.add(backToLogin, gbc);

        backgroundPanel.add(resetPasswordPanel, gbc);

        add(backgroundPanel);
    }



    private void handleResetPassword() {
        boolean isValid=true;
        String newPasswordStr = new String(newPassword.getPassword());
        
        if (newPasswordStr.length() < 4 || newPasswordStr.length() > 20) {
            newPasswordError.setText("La contraseña debe tener entre 4 y 20 caracteres.");
            isValid = false;
        } else {
            newPasswordError.setText("");
       
        }
        
        
        if (isValid) {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", Functions.toSHA256(newPasswordStr));
        json.put("action", TipoRequest.FORGOTPSW2.toString());
        
        PersistentClient client = PersistentClient.getInstance();
        String serverResponse = client.sendMessageAndWaitForResponse(json.toString());

        // Mostrar la respuesta en un diálogo según el código
        switch (serverResponse) {
            case "0":  // Éxito
                JOptionPane.showMessageDialog(null, "Cambio de contraseña exitoso", "Exitoso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                openLoginPage();
                
                break;
                
            case "-1":  // Error desconocido
                JOptionPane.showMessageDialog(null, "An unknown error occurred during recover password.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            default:  // Cualquier otra respuesta
                JOptionPane.showMessageDialog(null, "Unexpected server response: " + serverResponse, "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
        }
    }

    private void openLoginPage() {
        LoginPage loginPage = new LoginPage();
        loginPage.setVisible(true);
    }
}
