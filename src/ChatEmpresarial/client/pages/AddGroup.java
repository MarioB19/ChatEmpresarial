/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.client.pages;

/**
 *
 * @author brunosanchezpadilla
 */

import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.models.Usuario;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

public class AddGroup extends JFrame {
    private JTextField txtNombreGrupo;
    private JPanel userPanel;
    private JButton btnCrear, btnCancelar;
    private String nombreUserActive;
    private ArrayList<Usuario> userList;
    private Set<Integer> selectedUserIds = new HashSet<>(); 

    

    public AddGroup(String nombreUserActive, ArrayList<Usuario> userList) {
        super("Crear Grupo");
        this.nombreUserActive = nombreUserActive;
        this.userList = userList;

        setupUI();
        setSize(400, 300); // Set the size of the frame
        setLocationRelativeTo(null); // Center the frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close operation
    }

    private void setupUI() {
        getContentPane().setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0, 1));
        JLabel lblNombreGrupo = new JLabel("Nombre del grupo:");
        txtNombreGrupo = new JTextField(10);
        btnCrear = new JButton("Crear");
        btnCancelar = new JButton("Cancelar");

        inputPanel.add(lblNombreGrupo);
        inputPanel.add(txtNombreGrupo);
        inputPanel.add(btnCrear);
        inputPanel.add(btnCancelar);
        
        userPanel = new JPanel();
        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();

        for (Usuario user : userList) {
            JCheckBox checkBox = new JCheckBox(user.getNombre());
            checkBox.setActionCommand(String.valueOf(user.getId_usuario())); // Store user ID in action command
            userPanel.add(checkBox);
            checkBoxes.add(checkBox);
        }

        btnCancelar.addActionListener(e -> {
        ChatList chatList = new ChatList(nombreUserActive);
        chatList.setVisible(true);
        dispose();
        }
        );
        
        // Acción para el botón crear grupo
        btnCrear.addActionListener(e -> {
        String groupname = txtNombreGrupo.getText();
        
        if (groupname.isEmpty() || groupname.length() <=1 || groupname.length() >=50) {
            JOptionPane.showMessageDialog(null, "Group name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

            // Collect selected user IDs
            System.out.println("Total checkboxes: " + checkBoxes.size());
            for (JCheckBox checkBox : checkBoxes) {
                System.out.println("Checking checkbox for user: " + checkBox.getText());
                if (checkBox.isSelected()) {
                    System.out.println("Checkbox selected: " + checkBox.getText());
                    try {
                        int userId = Integer.parseInt(checkBox.getActionCommand());
                        selectedUserIds.add(userId);
                        System.out.println("User ID added: " + userId); // Debugging output
                    } catch (NumberFormatException ex) {
                        System.err.println("Invalid user ID format: " + checkBox.getActionCommand());
                    }
                }
            }

        
        if (!(selectedUserIds.size() >= 2)) {
            JOptionPane.showMessageDialog(null, "Selecciona 2 o más usuarios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        
        
                // Create JSON object for request
        JSONObject json = new JSONObject();
        json.put("groupname", groupname);
        json.put("adminId", nombreUserActive);  
        json.put("participantIds", new JSONArray(selectedUserIds));
        json.put("action", "CREATEGROUP");
        
        PersistentClient client = PersistentClient.getInstance();
        String serverResponse = client.sendMessageAndWaitForResponse(json.toString());
        
                // Mostrar la respuesta en un diálogo según el código
        switch (serverResponse) {
            case "0":  // Éxito
                JOptionPane.showMessageDialog(null, "Creation successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                ChatList chatList = new ChatList(nombreUserActive);
                chatList.setVisible(true);
                dispose();
                break;
            case "1":  
                JOptionPane.showMessageDialog(null, "Could not create group.", "Creation Failed", JOptionPane.ERROR_MESSAGE);
                break;
            case "-1":  // Error desconocido
                JOptionPane.showMessageDialog(null, "An unknown error occurred during creation.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            default:  // Cualquier otra respuesta
                JOptionPane.showMessageDialog(null, "Unexpected server response: " + serverResponse, "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
        });
        
        userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(userPanel);
        scrollPane.setPreferredSize(new Dimension(200, 120));

        updateUsersList();

        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    public void updateUsersList() {
        userPanel.removeAll();
        for (Usuario user : userList) {
            JCheckBox checkBox = new JCheckBox(user.getNombre());
            int userId = (user.getId_usuario());
            checkBox.setActionCommand(String.valueOf(userId));
            checkBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedUserIds.add(userId);
                } else {
                    selectedUserIds.remove(userId);
                }
            });
            userPanel.add(checkBox);
        }
        userPanel.revalidate();
        userPanel.repaint();
    }

}
