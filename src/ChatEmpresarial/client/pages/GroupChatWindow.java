/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.client.pages;

import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.models.Grupo;
import ChatEmpresarial.shared.models.Usuario;
import ChatEmpresarial.shared.utilities.Enumerators;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author brunosanchezpadilla
 */
public class GroupChatWindow extends JFrame {

    private DefaultListModel<Usuario> modeloUsuariosConectados = new DefaultListModel<>();
    private DefaultListModel<Usuario> modeloUsuariosDesconectados = new DefaultListModel<>();
    private JList<Usuario> listaUsuariosConectados = new JList<>(modeloUsuariosConectados);
    private JList<Usuario> listaUsuariosDesconectados = new JList<>(modeloUsuariosDesconectados);

    private Grupo grupo;
    private JTextArea chatArea;
    private JTextArea connectedUsers;
    private JTextArea disconnectedUsers;
    private JTextField messageInput;
    private JButton sendButton;
    private JButton backButton;
    private JButton btnEditarMiembros;
    private JButton btnAgregarMiembros;
    private JButton btnEliminarGrupo;
    private JButton btnSalirDelGrupo;

    private Timer messageFetchTimer;
    private Timer connectedUsersFetchTimer;
    private Timer disconnectedUsersFetchTimer;

    private String nombreUserActive;

    public GroupChatWindow(Grupo grupo, String nombreUserActive) {
        this.nombreUserActive = nombreUserActive;
        this.grupo = grupo;
        initializeUI();
        setupMessageFetchingTimer();
    }

    private void initializeUI() {
        setTitle("Chat - " + grupo.getNombre());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());

        // Crear un panel para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Usamos FlowLayout para alinear los botones

        // Inicializar botones
        backButton = new JButton("Volver");
        btnEditarMiembros = new JButton("Eliminar Miembros");
        btnAgregarMiembros = new JButton("Agregar Miembros");
        btnEliminarGrupo = new JButton("Eliminar Grupo");
        btnSalirDelGrupo = new JButton("Abandonar Grupo");

        btnEditarMiembros.setBackground(new Color(255, 193, 7)); // Amarillo
        btnEliminarGrupo.setBackground(new Color(244, 67, 54)); // Rojo
        btnSalirDelGrupo.setBackground(new Color(158, 158, 158)); // Gris
        btnEditarMiembros.setForeground(Color.BLACK);
        btnEliminarGrupo.setForeground(Color.BLACK);
        btnSalirDelGrupo.setForeground(Color.BLACK);

        buttonPanel.add(backButton);

        if (nombreUserActive.equals(grupo.getNombreAdmin())) {
            // El usuario activo es el administrador del grupo
            buttonPanel.add(btnEditarMiembros);
            buttonPanel.add(btnEliminarGrupo);
            buttonPanel.add(btnAgregarMiembros);
        } else {
            // El usuario activo no es el administrador del grupo
            buttonPanel.add(btnSalirDelGrupo);
        }

        // Agregar el panel de botones al norte del BorderLayout
        getContentPane().add(buttonPanel, BorderLayout.NORTH);

        btnSalirDelGrupo.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que quieres salir del grupo?",
                    "Confirmar salida del grupo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                // Código para manejar la salida del grupo
                handleExitGroup();
                System.out.println("El usuario ha salido del grupo.");

                ChatList chatList = new ChatList(nombreUserActive);
                chatList.setVisible(true);
                dispose();
            }
        });

        btnEliminarGrupo.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que quieres eliminar el grupo?",
                    "Confirmar eliminación del grupo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                // Código para manejar la eliminación del grupo
                handleDeleteGroup();
                System.out.println("El grupo ha sido eliminado.");

                ChatList chatList = new ChatList(nombreUserActive);
                chatList.setVisible(true);
                dispose();
            }
        });

        btnAgregarMiembros.addActionListener(e -> {
            AddMemberToGroup membersList = new AddMemberToGroup(grupo, nombreUserActive);
            membersList.setVisible(true);
            dispose();
        });

        btnEditarMiembros.addActionListener(e -> {
            MembersListToDeleteFromGroup membersList = new MembersListToDeleteFromGroup(grupo, nombreUserActive);
            membersList.setVisible(true);
            dispose();
        });
        backButton.addActionListener(e -> {
            ChatList chatList = new ChatList(nombreUserActive);
            chatList.setVisible(true);
            dispose();
        });

        // Configuración del panel principal dividido
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(500);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Configuración de los paneles de mensajes y usuarios
        configureMessagePanel(splitPane);
        configureUserPanel(splitPane);
    }

    private void configureMessagePanel(JSplitPane splitPane) {
        JPanel messagePanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane messageScroll = new JScrollPane(chatArea);
        messagePanel.add(messageScroll, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageInput = new JTextField();
        messageInput.addActionListener(this::sendText);

        sendButton = new JButton("Enviar");
        sendButton.addActionListener(this::sendText);
        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        messagePanel.add(inputPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(messagePanel);
    }

    private void configureUserPanel(JSplitPane splitPane) {
        JPanel userPanel = new JPanel(new GridLayout(2, 1));
        connectedUsers = new JTextArea("Miembros del grupo Conectados\n");
        connectedUsers.setEditable(false);
        JScrollPane connectedScroll = new JScrollPane(connectedUsers);
        userPanel.add(connectedScroll);

        disconnectedUsers = new JTextArea("Miembros del grupo Desconectados\n");
        disconnectedUsers.setEditable(false);
        JScrollPane disconnectedScroll = new JScrollPane(disconnectedUsers);
        userPanel.add(disconnectedScroll);

        splitPane.setRightComponent(userPanel);
    }

    private void setupMessageFetchingTimer() {
        int delay = 1000; // Tiempo en milisegundos entre cada ejecución
        messageFetchTimer = new Timer(delay, e -> fetchMessages());
        connectedUsersFetchTimer = new Timer(delay, e -> handleListUsersConectados());
        disconnectedUsersFetchTimer = new Timer(delay, e -> handleListUsersDesconectados());
        
        messageFetchTimer.start();
        connectedUsersFetchTimer.start();
        disconnectedUsersFetchTimer.start();
        
        

   
    }

    private void sendText(ActionEvent e) {
        String text = messageInput.getText();
        if (!text.isEmpty()) {
            messageInput.setText("");
            JSONObject json = new JSONObject();
            json.put("idChat", Integer.toString(grupo.getId_chat()));
            json.put("contenido", text);
            json.put("nameGroup", grupo.getNombre());
            json.put("username", nombreUserActive);
            json.put("action", "SEND_MESSAGE_GROUP");

            PersistentClient client = PersistentClient.getInstance();
            String serverResponse = client.sendMessageAndWaitForResponse(json.toString());
            System.out.println("Server respuesta" + serverResponse);
            chatArea.append(text + "\n");
        }
    }

    private void fetchMessages() {
        JSONObject json = new JSONObject();
        json.put("idChat", Integer.toString(grupo.getId_chat()));
        json.put("action", "GET_MESSAGES_GROUP");

        String serverResponse = PersistentClient.getInstance().sendMessageAndWaitForResponse(json.toString());

        System.out.println("Respuesta de mensajes del servidor: " + serverResponse);

        if (serverResponse == null || serverResponse.isEmpty()) {
            System.err.println("La respuesta del servidor es nula o está vacía.");
            return;
        }

        try {
            JSONObject response = new JSONObject(serverResponse);
            if (!response.has("mensajes")) {
                System.err.println("La respuesta del servidor no contiene la clave 'mensajes'.");
                return;
            }
            JSONArray messages = response.getJSONArray("mensajes");
            SwingUtilities.invokeLater(() -> {
                chatArea.setText(""); // Limpia el área de chat antes de añadir nuevos mensajes
                for (int i = 0; i < messages.length(); i++) {
                    JSONObject messageDetails = messages.getJSONObject(i);
                    String content = messageDetails.getString("contenido");
                    String fecha = messageDetails.getString("fecha_creacion");
                    chatArea.append(fecha + ": " + content + "\n");
                }
            });
        } catch (Exception e) {
            System.err.println("Error al parsear la respuesta del servidor: " + e.getMessage());
        }
    }

    public void receiveMessage(String formattedMessage) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(formattedMessage + "\n");
        });
    }

    private void handleListUsersConectados() {

        JSONObject json = new JSONObject();
        json.put("idChat", Integer.toString(grupo.getId_chat()));
        json.put("action", "FIND_USERS_CONNECTED_GROUP");

        PersistentClient client = PersistentClient.getInstance();
        String serverResponse = client.sendMessageAndWaitForResponse(json.toString());

        try {
            JSONObject responseJson = new JSONObject(serverResponse); // Parsea la respuesta directamente como un JSONObject
            int status = responseJson.getInt("status");

            if (status == 0) {
                JSONArray usuariosArray = responseJson.getJSONArray("message"); // Accede directamente al array JSON
                SwingUtilities.invokeLater(() -> {
                    connectedUsers.setText(""); // Limpia el área de chat antes de añadir nuevos usuarios
                    connectedUsers.append("Miembros del grupo Conectados:" + "\n" + "\n");
                    for (int i = 0; i < usuariosArray.length(); i++) {
                        String usuario = usuariosArray.getString(i);
                        connectedUsers.append("- " + usuario + "\n");
                    }
                });
            } else {
                System.err.println("Error al obtener usuarios Desconectados: estado " + status);
            }
        } catch (JSONException e) {
            System.err.println("Error al parsear la respuesta del servidor: " + e.getMessage());
        }
    }

    private void handleListUsersDesconectados() {

        JSONObject json = new JSONObject();
        json.put("idChat", Integer.toString(grupo.getId_chat()));
        json.put("action", "FIND_USERS_DISCONNECTED_GROUP");

        PersistentClient client = PersistentClient.getInstance();
        String serverResponse = client.sendMessageAndWaitForResponse(json.toString());

        try {
            JSONObject responseJson = new JSONObject(serverResponse); // Parsea la respuesta directamente como un JSONObject
            int status = responseJson.getInt("status");

            if (status == 0) {
                JSONArray usuariosArray = responseJson.getJSONArray("message"); // Accede directamente al array JSON
                SwingUtilities.invokeLater(() -> {
                    disconnectedUsers.setText(""); // Limpia el área de chat antes de añadir nuevos usuarios
                    disconnectedUsers.append("Miembros del grupo Desconectados:" + "\n" + "\n");
                    for (int i = 0; i < usuariosArray.length(); i++) {
                        String usuario = usuariosArray.getString(i);
                        disconnectedUsers.append("- " + usuario + "\n");
                    }
                });

            } else {
                System.err.println("Error al obtener usuarios desconectados: estado " + status);
            }
        } catch (JSONException e) {
            System.err.println("Error al parsear la respuesta del servidor: " + e.getMessage());
        }
    }

    private void handleDeleteGroup() {

        JSONObject json = new JSONObject();
        json.put("idChat", Integer.toString(grupo.getId_chat()));
        json.put("idGrupo", Integer.toString(grupo.getId_grupo()));
        json.put("action", "DELETE_GROUP");
           json.put("nameGroup", grupo.getNombre());
            json.put("username", nombreUserActive);
        

        PersistentClient client = PersistentClient.getInstance();
        String serverResponse = client.sendMessageAndWaitForResponse(json.toString());
    }

    private void handleExitGroup() {

        JSONObject json = new JSONObject();
        json.put("idChat", Integer.toString(grupo.getId_chat()));
        json.put("idGrupo", Integer.toString(grupo.getId_grupo()));
        json.put("nombre", nombreUserActive);
        json.put("action", "EXIT_GROUP");
        json.put("nameGroup", grupo.getNombre());
      
        
        
        
        
        PersistentClient client = PersistentClient.getInstance();
        String serverResponse = client.sendMessageAndWaitForResponse(json.toString());
    }
}
