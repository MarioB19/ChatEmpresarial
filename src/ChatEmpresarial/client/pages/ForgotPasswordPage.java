package ChatEmpresarial.client.pages;

import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.utilities.Enumerators;
import ChatEmpresarial.shared.utilities.Functions;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.json.JSONObject;

public class ForgotPasswordPage extends JFrame {
    private JTextField userName;
    private JTextField favMovie;
    private JTextField favFood;
    
    public ForgotPasswordPage() {
        setTitle("Recuperar Contraseña");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(173, 216, 230)); // Color de fondo
        backgroundPanel.setLayout(new GridBagLayout());

        JPanel forgotPasswordPanel = new JPanel();
        forgotPasswordPanel.setBackground(Color.WHITE);
        forgotPasswordPanel.setLayout(new GridBagLayout());
        forgotPasswordPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel instructions = new JLabel("Por favor, llene los campos necesarios para recuperar su contraseña.");
        instructions.setFont(new Font("Arial", Font.ITALIC, 14));
        instructions.setHorizontalAlignment(SwingConstants.CENTER);

        userName = new JTextField(20);
        userName.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        favFood = new JTextField(20);
        favFood.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        favMovie = new JTextField(20);
        favMovie.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        JButton recoverButton = new JButton("Recuperar Contraseña");
        recoverButton.setBackground(new Color(135, 206, 250)); 
        recoverButton.addActionListener(e -> handleRecoveryPassword());

        JLabel backToLogin = new JLabel("Regresar al Login");
        backToLogin.setFont(new Font("Arial", Font.PLAIN, 16));
        backToLogin.setForeground(new Color(0, 0, 255));
        backToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ActionListener para regresar a la ventana de login
        backToLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LoginPage loginPage = new LoginPage();
                loginPage.setVisible(true); // Regresar a la ventana de login
                dispose(); // Cerrar la ventana de recuperación
            }
        });     
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 30, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        forgotPasswordPanel.add(instructions, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        forgotPasswordPanel.add(new JLabel("Nombre de Usuario"), gbc);

        gbc.gridy = 2;
        forgotPasswordPanel.add(userName, gbc);

        gbc.gridy = 3;
        forgotPasswordPanel.add(new JLabel("Comida Favorita"), gbc);

        gbc.gridy = 4;
        forgotPasswordPanel.add(favFood, gbc);

        gbc.gridy = 5;
        forgotPasswordPanel.add(new JLabel("Película Favorita"), gbc);

        gbc.gridy = 6;
        forgotPasswordPanel.add(favMovie, gbc);

        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 20, 0);
        forgotPasswordPanel.add(recoverButton, gbc);

        gbc.gridy = 8; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.insets = new Insets(20, 0, 20, 0);
        forgotPasswordPanel.add(backToLogin, gbc);

        backgroundPanel.add(forgotPasswordPanel, gbc);

        add(backgroundPanel);
        
    }
    
    private void handleRecoveryPassword(){
      String username = userName.getText();
         String favoriteMovie = favMovie.getText();
         String favoriteFood = favFood.getText();
    
         JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("favoriteMovie", Functions.toSHA256(favoriteMovie));
        json.put("favoriteFood", Functions.toSHA256(favoriteFood));
        json.put("action", Enumerators.TipoRequest.FORGOTPSW1.toString());

        PersistentClient client = PersistentClient.getInstance();
        String serverResponse = client.sendMessageAndWaitForResponse(json.toString());
        
            switch (serverResponse) {
            case "0":  // Todos los campos coinciden
                JOptionPane.showMessageDialog(null, "Datos correctos, avanza a la siguiente pagina", "Exitoso", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                OpenForgotPsw2Page();
                
                break;
            case "1":  // Alguno de los campos no coincide
                JOptionPane.showMessageDialog(null, "Alguno de los datos no coincide con el usuario", "Fallido", JOptionPane.ERROR_MESSAGE);
                break;
            case "-1":  // Error desconocido
                JOptionPane.showMessageDialog(null, "An unknown error occurred during RecoveryPassword.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            default:  // Cualquier otra respuesta
                JOptionPane.showMessageDialog(null, "Unexpected server response: " + serverResponse, "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }
    
     private void OpenForgotPsw2Page() {
        String username = userName.getText();
        ForgotPasswordPage2 forgotpasswordPage2 = new ForgotPasswordPage2(username);
        forgotpasswordPage2.setVisible(true);
      }
}