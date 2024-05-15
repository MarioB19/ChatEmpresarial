/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.client.pages;



import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.utilities.Enumerators;
import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import org.json.JSONArray;
import org.json.JSONObject;

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

public class ChatAmigosPage extends JFrame
{
    private JTextArea textArea;
    private JTextField textField;
    private String contactName;
    private String activeUser;
    private static final Color COLOR_FONDO = new Color(225, 245, 254);
    private static final Color COLOR_BOTON = new Color(2, 136, 209);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private Timer messageFetchTimer;

    //------------------
    //Constructor
    //-------------------
    
    public ChatAmigosPage(String contactName, String activeUser) {
        this.contactName = contactName;
        this.activeUser = activeUser;
        initializeUI();
        setupMessageFetchingTimer();
        
         this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (messageFetchTimer != null) {
                    messageFetchTimer.stop();
                }
            }
        });
    }
     
    
    
    
    //Métodos privados
    

    private void initializeUI() {
        setTitle("Chat con " + contactName);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(COLOR_FONDO);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(COLOR_FONDO);

        textField = new JTextField();
        textField.addActionListener(this::sendText);
        inputPanel.add(textField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Enviar");
        sendButton.setBackground(COLOR_BOTON);
        sendButton.setForeground(COLOR_TEXTO);
        sendButton.addActionListener(this::sendText);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        JLabel contactLabel = new JLabel("Chateando con: " + contactName, SwingConstants.CENTER);
        contactLabel.setOpaque(true);
        contactLabel.setBackground(COLOR_BOTON);
        contactLabel.setForeground(COLOR_TEXTO);
        add(contactLabel, BorderLayout.NORTH);

        setVisible(true);
    }

    private void setupMessageFetchingTimer() {
        int delay = 300; // Tiempo en milisegundos entre cada ejecución
        messageFetchTimer = new Timer(delay, e -> fetchMessages());
        messageFetchTimer.start();
    }
//Enviar mensaje de texto
    private void sendText(ActionEvent e) {
    String text = textField.getText();
    if (!text.isEmpty()) {
        
        JSONObject json = new JSONObject();
        json.put("receptor", contactName);
        json.put("contenido", text);
        json.put("action", Enumerators.TipoRequest.SEND_MESSAGE_FRIEND);

        PersistentClient client = PersistentClient.getInstance();
        String serverResponse = client.sendMessageAndWaitForResponse(json.toString());
        System.out.println("Server respuesta: " + serverResponse);
        
        if (!serverResponse.equals("-1")) {
            textArea.append(activeUser + ": " + text + "\n");
        } else {
            System.err.println("Error al enviar el mensaje.");
        }
    }
}

  private void fetchMessages() {
    textArea.setText(""); // Clear the text area each time we fetch messages
    JSONObject json = new JSONObject();
    json.put("receptor", contactName);
    json.put("action", Enumerators.TipoRequest.GET_MESSAGE_FRIEND);

    String serverResponse = PersistentClient.getInstance().sendMessageAndWaitForResponse(json.toString());

    System.out.println("server response: " + serverResponse);
    if (serverResponse == null || serverResponse.isEmpty()) {
        System.err.println("La respuesta del servidor es nula o está vacía.");
        return;
    }

    try {
        JSONObject response = new JSONObject(serverResponse);
        if (!response.has("message")) {
            System.err.println("La respuesta del servidor no contiene la clave 'message'.");
            return;
        }
        JSONArray messages = new JSONArray(response.getString("message"));
        for (int i = 0; i < messages.length(); i++) {
            JSONObject msg = messages.getJSONObject(i);
            String sender = msg.getString("usuario");
            String content = msg.getString("contenido");
            receiveMessage(sender + ": " + content);
        }
    } catch (Exception e) {
        System.err.println("Error al parsear la respuesta del servidor: " + e.getMessage());
    }
}

public void receiveMessage(String formattedMessage) {
    SwingUtilities.invokeLater(() -> {
        textArea.append(formattedMessage + "\n");
    });
}

}

/*{
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
    
}*/
