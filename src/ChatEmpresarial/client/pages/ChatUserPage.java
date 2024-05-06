package ChatEmpresarial.client.pages;

import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.utilities.Enumerators.TipoRequest;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChatUserPage extends JFrame {
    private JTextArea textArea;
    private JTextField textField;
    private String contactName;
    private String activeUser;
    private static final Color COLOR_FONDO = new Color(225, 245, 254);
    private static final Color COLOR_BOTON = new Color(2, 136, 209);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private Timer messageFetchTimer;

    public ChatUserPage(String contactName, String activeUser) {
        this.contactName = contactName;
        this.activeUser = activeUser;
        initializeUI();
        setupMessageFetchingTimer();
     
    }

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

    private void sendText(ActionEvent e) {
        String text = textField.getText();
        if (!text.isEmpty()) {
            textField.setText("");
            JSONObject json = new JSONObject();
            json.put("user1", activeUser);
            json.put("user2", contactName);
            json.put("message", text);
            json.put("action", TipoRequest.SEND_MESSAGE_CHAT_USERS);
            
            PersistentClient client = PersistentClient.getInstance();
                String serverResponse = client.sendMessageAndWaitForResponse(json.toString());
                System.out.println("Server respuesta" + serverResponse);
            textArea.append(activeUser + ": " + text + "\n");
        }
    }

    private void fetchMessages() {
        textArea.setText(""); // Clear the text area each time we fetch messages
        JSONObject json = new JSONObject();
        json.put("user1", activeUser);
        json.put("user2", contactName);
        json.put("action", TipoRequest.GET_MESSAGES_CHAT_USERS);

        String serverResponse = PersistentClient.getInstance().sendMessageAndWaitForResponse(json.toString());
        
        System.out.println("server response" + serverResponse);
        if (serverResponse == null || serverResponse.isEmpty()) {
            System.err.println("La respuesta del servidor es nula o está vacía.");
            return;
        }

        try {
            JSONObject response = new JSONObject(serverResponse);
            if (!response.has("messages")) {
                System.err.println("La respuesta del servidor no contiene la clave 'messages'.");
                return;
            }
            JSONArray messages = response.getJSONArray("messages");
            messages.forEach(msg -> {
                if (msg instanceof String) {
                    JSONObject messageDetails = new JSONObject((String) msg);
                    String sender = messageDetails.getString("sender");
                    String content = messageDetails.getString("content");
                    receiveMessage(sender + ": " + content);
                }
            });
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
