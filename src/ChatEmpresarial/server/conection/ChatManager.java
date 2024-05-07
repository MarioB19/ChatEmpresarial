
package ChatEmpresarial.server.conection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;

public class ChatManager {
    // Mapa para almacenar las sesiones de chat. Cada clave es una combinación de dos nombres de usuario
    private static final ConcurrentHashMap<String, List<Message>> chats = new ConcurrentHashMap<>();

    // Método para crear un chat entre dos usuarios
    public static String createChat(String user1, String user2) {
        String sessionKey = getSessionKey(user1, user2);
        if (chats.putIfAbsent(sessionKey, new ArrayList<>()) == null) {
            return "1";
        } else {
            return "0";
        }
   
    }

    // Método para enviar un mensaje en un chat específico
    public static String sendMessage(String user1, String user2, String message) {
        String sessionKey = getSessionKey(user1, user2);
        List<Message> chatMessages = chats.get(sessionKey);
        if (chatMessages != null) {
            chatMessages.add(new Message(user1, message));  // Guardar mensaje con el remitente
            return "1";
        }
        return "-1";
    }

    // Método para obtener los mensajes de un chat entre dos usuarios
   public static List<String> getChatMessages(String user1, String user2) {
        String sessionKey = getSessionKey(user1, user2);
        List<Message> messages = chats.getOrDefault(sessionKey, new ArrayList<>());
        List<String> jsonFormattedMessages = new ArrayList<>();
        
        for (Message message : messages) {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("sender", message.getSender());
            jsonMessage.put("content", message.getContent());
            jsonFormattedMessages.add(jsonMessage.toString());
        }
        
        return jsonFormattedMessages;
    }
   
       public static void cleanUpChats() {
        Iterator<String> sessionKeys = chats.keySet().iterator();
        while (sessionKeys.hasNext()) {
            String sessionKey = sessionKeys.next();
            String[] users = sessionKey.split(":");
            // Comprueba si ambos usuarios aún están conectados
            if (!GlobalClients.connectedClients.containsKey(users[0]) || !GlobalClients.connectedClients.containsKey(users[1])) {
                sessionKeys.remove();  // Elimina el chat si alguno de los usuarios no está conectado
            }
        }
    }
   

    // Método privado para obtener una clave de sesión consistente, independientemente del orden de los nombres
    private static String getSessionKey(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + ":" + user2 : user2 + ":" + user1;
    }

    // Clase interna para representar un mensaje dentro de un chat
    public static class Message {
        private final String sender;
        private final String content;

        public Message(String sender, String content) {
            this.sender = sender;
            this.content = content;
        }

        public String getSender() {
            return sender;
        }

        public String getContent() {
            return content;
        }
    }
}
