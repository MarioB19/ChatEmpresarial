/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.client.pages;

import ChatEmpresarial.shared.models.Grupo;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author brunosanchezpadilla
 */



public class GroupChatWindow extends JFrame {
    private Grupo grupo;
    private JTextArea chatArea;
    private JTextArea connectedUsers;
    private JTextArea disconnectedUsers;
    private JTextField messageInput;
    private JButton sendButton;
    private JButton backButton;
    private String nombreUserActive;

    public GroupChatWindow(Grupo grupo, String nombreUserActive) {
        this.nombreUserActive = nombreUserActive;
        this.grupo = grupo;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Chat - " + grupo.getNombre());
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());

        // Botón de volver atrás
        backButton = new JButton("Volver");
        getContentPane().add(backButton, BorderLayout.NORTH);
        backButton.addActionListener(e -> {
        ChatList chatList = new ChatList(nombreUserActive);
        chatList.setVisible(true);
        dispose();
        }
        );

        // Panel principal que divide la ventana en dos columnas
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(300);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Columna izquierda: Área de mensajes y entrada de texto
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane messageScroll = new JScrollPane(chatArea);
        messagePanel.add(messageScroll, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        messageInput = new JTextField();
        sendButton = new JButton("Enviar");
        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        messagePanel.add(inputPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(messagePanel);

        // Columna derecha: Usuarios conectados y desconectados
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new GridLayout(2, 1));
        
        connectedUsers = new JTextArea("Usuarios Conectados\n");
        connectedUsers.setEditable(false);
        JScrollPane connectedScroll = new JScrollPane(connectedUsers);
        userPanel.add(connectedScroll);

        disconnectedUsers = new JTextArea("Usuarios Desconectados\n");
        disconnectedUsers.setEditable(false);
        JScrollPane disconnectedScroll = new JScrollPane(disconnectedUsers);
        userPanel.add(disconnectedScroll);

        splitPane.setRightComponent(userPanel);
    }
}

