package ChatEmpresarial.client.pages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterPage extends JFrame {
    public RegisterPage() {
        setTitle("Registro");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(173, 216, 230)); // Mismo color que la página de login
        backgroundPanel.setLayout(new GridBagLayout());

        JPanel registerPanel = new JPanel();
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setLayout(new GridBagLayout());
        registerPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60)); 

        JLabel title = new JLabel("Registro");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField userName = new JTextField(20);
        userName.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        JPasswordField password = new JPasswordField(20);
        password.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        JTextField favMovie = new JPasswordField(20);
        favMovie.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        JTextField favFood = new JTextField(20);
        favFood.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        JButton registerButton = new JButton("Registrarse");
        registerButton.setBackground(new Color(135, 206, 250)); 

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
                dispose(); // Cerrar la ventana de registro
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 30, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        registerPanel.add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        registerPanel.add(new JLabel("Nombre de Usuario"), gbc);

        gbc.gridy = 2;
        registerPanel.add(userName, gbc);

        gbc.gridy = 3;
        registerPanel.add(new JLabel("Contraseña"), gbc);

        gbc.gridy = 4;
        registerPanel.add(password, gbc);

        gbc.gridy = 5;
        registerPanel.add(new JLabel("Nombre de tu comida fav"), gbc);

        gbc.gridy = 6;
        registerPanel.add(favFood, gbc);
        
        gbc.gridy = 7;
        registerPanel.add(new JLabel("Nombre de tu pelicula fav"), gbc);

        gbc.gridy = 8;
        registerPanel.add(favMovie, gbc);

        gbc.gridy = 9; 
        gbc.fill = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 20, 0);
        registerPanel.add(registerButton, gbc);

        gbc.gridy = 10; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.insets = new Insets(20, 0, 20, 0);
        registerPanel.add(backToLogin, gbc);

        backgroundPanel.add(registerPanel, gbc);

        add(backgroundPanel);
    }
}
