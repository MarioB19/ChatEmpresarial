/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.client.pages;



import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import java.sql.Timestamp;

// Mensaje representa el contenido de cada mensaje enviado en el chat
class Mensaje {
    String contenido;
    Timestamp fechaCreacion;
    int idChat;
    String usuario;

    public Mensaje(String contenido, Timestamp fechaCreacion, int idChat, String usuario) {
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
        this.idChat = idChat;
        this.usuario = usuario;
    }
}

public class ChatAmigosPage extends JFrame {
    private String currentUser = "UsuarioActual"; // Identificador del usuario actual
    private int idChat = 1; // Asume que este chat tiene un ID único
    private List<Mensaje> chatHistory = new ArrayList<>(); // Lista para almacenar mensajes
    private JTextArea chatArea = new JTextArea(); // Área de texto para el chat

    public ChatAmigosPage() {
        setTitle("Chat Simple");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        chatArea.setEditable(false); // Evitar que se edite directamente el chat
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel para enviar mensajes
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("Enviar");

        // Botón para regresar
        JButton backButton = new JButton("Regresar");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateBack();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageContent = inputField.getText();
                if (!messageContent.trim().isEmpty()) {
                    // Crear el mensaje con marca de tiempo actual y añadirlo al chat
                    Mensaje mensaje = new Mensaje(messageContent, new Timestamp(System.currentTimeMillis()), idChat, currentUser);
                    addMessageToChat(mensaje);
                    inputField.setText(""); // Limpiar campo de entrada
                }
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        JPanel navigationPanel = new JPanel();
        navigationPanel.add(backButton);
        mainPanel.add(navigationPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Método para agregar mensajes al chat
    private void addMessageToChat(Mensaje mensaje) {
        chatHistory.add(mensaje); // Añadir a la lista de historial
        updateChatArea(mensaje); // Actualizar la visualización
    }

    // Actualiza el área de texto para reflejar un mensaje nuevo
    private void updateChatArea(Mensaje mensaje) {
        String bubbleColor = mensaje.usuario.equals(currentUser) ? "#ADD8E6" : "#FFFFFF";
        String bubble = String.format(
            "<div style='background-color:%s; padding:10px; margin:5px; border-radius:10px;'>%s<br><small>%s</small></div>",
            bubbleColor,
            mensaje.contenido,
            mensaje.fechaCreacion.toString()
        );

        chatArea.append(String.format("%s\n", bubble)); // Añade la burbuja al área de chat
    }

    // Método para volver a la pantalla anterior
    private void navigateBack() {
        saveChatHistoryToFile();
        // Aquí iría el código para regresar a la pantalla de la lista de chats
        dispose(); // Cierra la ventana actual
    }

    // Método para guardar el historial del chat en un archivo usando GSON
    private void saveChatHistoryToFile() {
        Gson gson = new Gson();
        String chatJson = gson.toJson(chatHistory);
        // Escribir en un archivo JSON usando la librería de manejo de archivos
        // Aquí podrías usar un escritor de archivos para almacenar `chatJson`
    }
    
}
