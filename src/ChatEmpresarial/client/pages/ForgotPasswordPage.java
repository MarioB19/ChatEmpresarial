package ChatEmpresarial.client.pages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ForgotPasswordPage extends JFrame {
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

        JTextField userName = new JTextField(20);
        userName.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        JTextField favFood = new JTextField(20);
        favFood.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        JTextField favMovie = new JTextField(20);
        favMovie.setBorder(BorderFactory.createLineBorder(new Color(0, 191, 255)));

        JButton recoverButton = new JButton("Recuperar Contraseña");
        recoverButton.setBackground(new Color(135, 206, 250)); 

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

          // Acción para abrir ForgotPasswordPage2
        recoverButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ForgotPasswordPage2 forgotPasswordPage2 = new ForgotPasswordPage2(); 
                forgotPasswordPage2.setVisible(true);
                dispose(); // Cerrar esta ventana
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
}
