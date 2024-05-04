package ChatEmpresarial.client.pages;

import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.client.utilities.SessionManager;
import ChatEmpresarial.shared.utilities.Enumerators.TipoRequest;
import ChatEmpresarial.shared.utilities.Functions;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.json.JSONObject;

public class LoginPage extends JFrame {
    
    //Propiedades globales
    int attempts = 0;
    
    public LoginPage() {
        setTitle("Login");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(173, 216, 230));
        backgroundPanel.setLayout(new GridBagLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField userText = new JTextField(20);
        userText.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(135, 206, 250));

        JLabel registerLink = new JLabel("Regístrate aquí");
        registerLink.setFont(new Font("Arial", Font.PLAIN, 16));
        registerLink.setForeground(new Color(0, 0, 255));
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RegisterPage registerPage = new RegisterPage(); // Instanciar la nueva ventana
                registerPage.setVisible(true); 
                dispose(); // Cerrar la ventana de login
            }
        });

        JLabel forgotPasswordLink = new JLabel("Recuperar contraseña");
        forgotPasswordLink.setFont(new Font("Arial", Font.ITALIC, 10));
        forgotPasswordLink.setForeground(new Color(128, 0, 128));
        forgotPasswordLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Agregar el listener para abrir la página de recuperación de contraseña
        forgotPasswordLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage(); // Instanciar la nueva ventana
                forgotPasswordPage.setVisible(true); 
                dispose(); // Cerrar la ventana de login
            }
        });
        
         loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
         
                
                //Realizar solicitud del login
                String username = userText.getText();
                String password = new String(passwordText.getPassword());
                
                handleLogin(username, password);
           
                //-----------------------------------//
                
           
           
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 30, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        loginPanel.add(new JLabel("Nombre de Usuario"), gbc);

        gbc.gridy = 2;
        loginPanel.add(userText, gbc);

        gbc.gridy = 3;
        loginPanel.add(new JLabel("Contraseña"), gbc);

        gbc.gridy = 4;
        loginPanel.add(passwordText, gbc);

        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.CENTER; 
        gbc.anchor = GridBagConstraints.WEST; // Para alinear el texto a la izquierda
        loginPanel.add(forgotPasswordLink, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER; // Centrar el botón
        loginPanel.add(loginButton, gbc);

        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(registerLink, gbc);

        backgroundPanel.add(loginPanel, gbc);

        add(backgroundPanel);
    }
    
    
    
    //_-------------------------------------
    //Métodos privados
    
    
               private void handleLogin(String username, String password) {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", Functions.toSHA256(password));
        json.put("action", TipoRequest.LOGIN.toString());

        PersistentClient client = PersistentClient.getInstance();
        String serverResponse = client.sendMessageAndWaitForResponse(json.toString());

        switch (serverResponse) {
            case "-1":
                JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrecta. Intenta nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
                attempts++;
                if (attempts == 3) {
                    JOptionPane.showMessageDialog(null, "Has alcanzado el máximo de intentos. Por favor, registra una cuenta.", "Máximo de Intentos", JOptionPane.ERROR_MESSAGE);
                    RegisterPage registerPage = new RegisterPage();
                    registerPage.setVisible(true);
                    dispose();
                }
                break;
            case "0":
                JOptionPane.showMessageDialog(null, "Inicio de sesión exitoso.", "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
                ChatList chatList = new ChatList();
                chatList.setVisible(true);
                dispose();
                break;
            default:
                JOptionPane.showMessageDialog(null, "Error desconocido. Intenta nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }
}
