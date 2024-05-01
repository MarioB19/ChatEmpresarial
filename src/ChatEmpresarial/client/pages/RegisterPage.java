package ChatEmpresarial.client.pages;

import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.utilities.Functions;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.json.JSONObject;

public class RegisterPage extends JFrame {
    private JTextField userName;
    private JPasswordField password;
    private JTextField favMovie;
    private JTextField favFood;
    
    private JLabel userNameError;
    private JLabel passwordError;
    private JLabel favMovieError;
    private JLabel favFoodError;

    public RegisterPage() {
        setTitle("Registro");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(173, 216, 230));
        backgroundPanel.setLayout(new GridBagLayout());

        JPanel registerPanel = new JPanel();
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setLayout(new GridBagLayout());
        registerPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("Registro");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        userName = new JTextField(20);
        userName.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        userNameError = new JLabel();
        userNameError.setForeground(Color.RED);
        userNameError.setFont(new Font("Arial", Font.PLAIN, 10)); // Letra más pequeña

        password = new JPasswordField(20);
        password.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        passwordError = new JLabel();
        passwordError.setForeground(Color.RED);
        passwordError.setFont(new Font("Arial", Font.PLAIN, 10)); // Letra más pequeña

        favMovie = new JTextField(20);
        favMovie.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        favMovieError = new JLabel();
        favMovieError.setForeground(Color.RED);
        favMovieError.setFont(new Font("Arial", Font.PLAIN, 10)); // Letra más pequeña

        favFood = new JTextField(20);
        favFood.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        favFoodError = new JLabel();
        favFoodError.setForeground(Color.RED);
        favFoodError.setFont(new Font("Arial", Font.PLAIN, 10)); // Letra más pequeña

        JButton registerButton = new JButton("Registrarse");
        registerButton.setBackground(new Color(135, 206, 250));
        registerButton.addActionListener(e -> handleRegister());

        JLabel backToLogin = new JLabel("Regresar al Login");
        backToLogin.setFont(new Font("Arial", Font.PLAIN, 16));
        backToLogin.setForeground(new Color(0, 0, 255));
        backToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

          backToLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openLoginPage(); // Ir a la página de inicio de sesión
                dispose(); // Cerrar la página actual
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        registerPanel.add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 5, 0);
        registerPanel.add(new JLabel("Nombre de Usuario"), gbc);

        gbc.gridy = 2;
        registerPanel.add(userName, gbc);
        gbc.gridy = 3;
        registerPanel.add(userNameError, gbc);

        gbc.gridy = 4;
        registerPanel.add(new JLabel("Contraseña"), gbc);

        gbc.gridy = 5;
        registerPanel.add(password, gbc);
        gbc.gridy = 6;
        registerPanel.add(passwordError, gbc);

        gbc.gridy = 7;
        registerPanel.add(new JLabel("Nombre de tu comida favorita"), gbc);

        gbc.gridy = 8;
        registerPanel.add(favFood, gbc);
        gbc.gridy = 9;
        registerPanel.add(favFoodError, gbc);

        gbc.gridy = 10;
        registerPanel.add(new JLabel("Nombre de tu película favorita"), gbc);

        gbc.gridy = 11;
        registerPanel.add(favMovie, gbc);
        gbc.gridy = 12;
        registerPanel.add(favMovieError, gbc);
        

        gbc.gridy = 13;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 20, 0);
        registerPanel.add(registerButton, gbc);

        gbc.gridy = 14;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 20, 0);
        registerPanel.add(backToLogin, gbc);

        backgroundPanel.add(registerPanel, gbc);

        add(backgroundPanel);
    }

     private void handleRegister() {
    boolean isValid = true;

    String username = userName.getText();
    String passwordStr = new String(password.getPassword());
    String favoriteMovie = favMovie.getText();
    String favoriteFood = favFood.getText();

    // Validación de nombre de usuario
    if (username.length() < 2 || username.length() > 15) {
        userNameError.setText("Debe tener entre 2 y 15 caracteres.");
        isValid = false;
    } else {
        userNameError.setText("");
    }

    // Validación de contraseña
    if (passwordStr.length() < 4 || passwordStr.length() > 20) {
        passwordError.setText("Debe tener entre 4 y 20 caracteres.");
        isValid = false;
    } else {
        passwordError.setText("");
    }

    // Validación de película favorita
    if (favoriteMovie.length() < 2 || favoriteMovie.length() > 70) {
        favMovieError.setText("Debe tener entre 2 y 70 caracteres.");
        isValid = false;
    } else {
        favMovieError.setText("");
    }

    // Validación de comida favorita
    if (favoriteFood.length() < 2 || favoriteFood.length() > 70) {
        favFoodError.setText("Debe tener entre 2 y 70 caracteres.");
        isValid = false;
    } else {
        favFoodError.setText("");
    }
    
    
    
    

    if (isValid) {
      
        
 
        // Crear el objeto JSON manualmente
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", Functions.toSHA256(passwordStr));
        json.put("favoriteMovie", Functions.toSHA256(favoriteMovie));
        json.put("favoriteFood",  Functions.toSHA256(favoriteFood));
        json.put("action", "register"); // Agregar un campo de acción para indicar el tipo de solicitud

        //PersistentClient client = PersistentClient.getInstance();
        //client.sendMessage(json.toString());  // Envía el objeto JSON como un string al servidor
         
     
         System.out.println("JSON to be sent:");
          System.out.println(json.toString(4)); // El número indica el factor de indentación para una impresión más legible

          
          /*
          
        // Recibe la respuesta del servidor
        String response = (String) client.receiveMessage();
        if (response.equals("Registro exitoso")) {
            JOptionPane.showMessageDialog(this, "Registro exitoso");
            openLoginPage();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error en el registro");
        }

*/

    
    }
}
     
     private void openLoginPage() {
        LoginPage loginPage = new LoginPage();
        loginPage.setVisible(true);
    }


}

