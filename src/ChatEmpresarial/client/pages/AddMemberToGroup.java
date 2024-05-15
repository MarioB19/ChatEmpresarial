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
import ChatEmpresarial.shared.models.Grupo;
import ChatEmpresarial.shared.models.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class AddMemberToGroup extends JFrame {

    private Grupo grupo;
    private JPanel userPanel;
    private JScrollPane scrollPane;
    private JButton btnVolver;
    private String nombreUserActive;
    private Timer timer;

    public AddMemberToGroup(Grupo grupo, String nombreUserActive) {
        this.nombreUserActive = nombreUserActive;
        this.grupo = grupo;
        initializeUI();
        setSize(400, 600); // Set the size of the frame
        setLocationRelativeTo(null); // Center the frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close operation

        int delay = 15000; // 5000 milisegundos = 5 segundos
        timer = new Timer(delay, e -> updateUsersList());
        timer.start();
    }

    private void initializeUI() {
        setTitle("Añadir usuarios del grupo: " + grupo.getNombre());
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
        fetchUsers();
        userPanel.revalidate();
        userPanel.repaint();
    }

    private void agregarUsuario(Usuario usuario) {
        // Aquí iría la lógica para eliminar el usuario del grupo
        System.out.println("Añadir usuario: " + usuario.getNombre());
        handleAddUser(usuario);
        updateUsersList(); // Actualiza la lista después de eliminar
    }

    private void actionVolver(ActionEvent e) {
        GroupChatWindow chatWindow = new GroupChatWindow(grupo, nombreUserActive);
        chatWindow.setVisible(true);
        dispose();
        this.dispose(); // Cierra esta ventana
    }

    private void handleAddUser(Usuario user) {
        JSONObject json = new JSONObject();
        json.put("idGrupo", Integer.toString(grupo.getId_grupo()));
        json.put("idReceptor", user.getNombre());
        json.put("Remitente", nombreUserActive);
        json.put("action", "ADD_USER_TO_GROUP");

        PersistentClient client = PersistentClient.getInstance();
        client.sendMessageAndWaitForResponse(json.toString());
    }

    private void fetchUsers() {
        userPanel.removeAll(); // Limpiar el panel antes de agregar nuevos usuarios
        try {
            JSONObject json = new JSONObject();
            json.put("idChat", Integer.toString(grupo.getId_chat()));
            json.put("idGrupo", Integer.toString(grupo.getId_grupo()));
            json.put("nombre", nombreUserActive);
            json.put("action", "GET_USERS_NOT_IN_GROUP");

            String serverResponse = PersistentClient.getInstance().sendMessageAndWaitForResponse(json.toString());

            System.out.println("Respuesta de usuarios del servidor: " + serverResponse);

            if (serverResponse == null || serverResponse.isEmpty()) {
                System.err.println("La respuesta del servidor es nula o está vacía.");
                return;
            }

            JSONObject response = new JSONObject(serverResponse);
            if (!response.has("usuarios")) {
                System.err.println("La respuesta del servidor no contiene la clave 'usuarios'.");
                return;
            }
            JSONArray users = response.getJSONArray("usuarios");

            for (int i = 0; i < users.length(); i++) {
                String nombreUsuario = users.getString(i);
                Usuario usuario = new Usuario(); // Crear objeto Usuario y setear sus propiedades
                usuario.setNombre(nombreUsuario);

                // Crear panel para el usuario y agregarlo al panel de usuarios
                JPanel panelUsuario = new JPanel();
                panelUsuario.setLayout(new FlowLayout(FlowLayout.LEFT));
                JLabel lblNombre = new JLabel(nombreUsuario);
                JButton btnEliminar = new JButton("Añadir");
                btnEliminar.addActionListener(e -> agregarUsuario(usuario));
                panelUsuario.add(lblNombre);
                panelUsuario.add(btnEliminar);
                userPanel.add(panelUsuario);
            }
        } catch (Exception e) {
            System.err.println("Error al parsear la respuesta del servidor: " + e.getMessage());
        }
    }

}
