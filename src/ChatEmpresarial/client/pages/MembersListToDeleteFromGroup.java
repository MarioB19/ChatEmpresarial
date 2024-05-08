/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.client.pages;

/**
 *
 * @author brunosanchezpadilla
 */

import ChatEmpresarial.shared.models.Grupo;
import ChatEmpresarial.shared.models.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class MembersListToDeleteFromGroup extends JFrame {
    private Grupo grupo;
    private JPanel userPanel;
    private JScrollPane scrollPane;
    private JButton btnVolver;
    private String nombreUserActive;

    public MembersListToDeleteFromGroup(Grupo grupo, String nombreUserActive) {
        this.nombreUserActive = nombreUserActive;
        this.grupo = grupo;
        initializeUI();
        setSize(400, 600); // Set the size of the frame
        setLocationRelativeTo(null); // Center the frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close operation
    }

    private void initializeUI() {
        setTitle("Eliminar usuarios del grupo: " + grupo.getNombre());
        getContentPane().setLayout(new BorderLayout());

        userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));

        updateUsersList();

        scrollPane = new JScrollPane(userPanel);
        scrollPane.setPreferredSize(new Dimension(200, 550));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        btnVolver = new JButton("Volver");
        getContentPane().add(btnVolver, BorderLayout.NORTH);
        btnVolver.addActionListener(this::actionVolver);
    }

    private void updateUsersList() {
        userPanel.removeAll();
        for (Usuario user : grupo.getUsuarios()) {
            JPanel panelUsuario = new JPanel();
            panelUsuario.setLayout(new FlowLayout(FlowLayout.LEFT));
            JLabel lblNombre = new JLabel(user.getNombre());
            JButton btnEliminar = new JButton("Eliminar");
            btnEliminar.setBackground(new Color(244, 67, 54)); // Rojo para el botón de eliminar
            btnEliminar.addActionListener(e -> eliminarUsuario(user));
            panelUsuario.add(lblNombre);
            panelUsuario.add(btnEliminar);
            userPanel.add(panelUsuario);
        }
        userPanel.revalidate();
        userPanel.repaint();
    }

    private void eliminarUsuario(Usuario usuario) {
        // Aquí iría la lógica para eliminar el usuario del grupo
        System.out.println("Eliminar usuario: " + usuario.getNombre());
        
        ChatList chatList = new ChatList(nombreUserActive);
        chatList.setVisible(true);
        dispose();
        updateUsersList(); // Actualiza la lista después de eliminar
    }

    private void actionVolver(ActionEvent e) {
        
        this.dispose(); // Cierra esta ventana
    }
}
