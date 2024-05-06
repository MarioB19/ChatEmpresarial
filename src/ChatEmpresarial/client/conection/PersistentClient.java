package ChatEmpresarial.client.conection;

import java.io.*;
import java.net.*;
import org.json.JSONObject;

public class PersistentClient {
    private static volatile PersistentClient instance = null;
    private final String serverAddress = "192.168.100.20";
    private final int serverPort = 6789;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    

    private PersistentClient() {
        // Constructor privado para el patrón Singleton
    }

    public static PersistentClient getInstance() {
        if (instance == null) {
            synchronized (PersistentClient.class) {
                if (instance == null) {
                    instance = new PersistentClient();
                }
            }
        }
        return instance;
    }

    private synchronized void initializeConnection() throws IOException {
        if (socket == null || socket.isClosed()) {
            System.out.println("Attempting to connect to the server...");
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverAddress, serverPort), 5000); // Timeout de 5000 ms
            socket.setSoTimeout(10000); // Set a read timeout for the socket
            System.out.println("Socket connected.");
            
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            System.out.println("Output stream initialized.");
            
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Input stream initialized.");
        }
    }

 public String sendMessageAndWaitForResponse(String message) {
        try {
            initializeConnection();
            System.out.println("Sending message: " + message);
            out.writeObject(message);
            out.flush();
            System.out.println("Message sent, waiting for response...");

            try {
                // Espera sincrónica por la respuesta del servidor
                Object response = in.readObject();
                if (response instanceof String) {
                    JSONObject jsonResponse = new JSONObject((String) response);
                    String responseText = jsonResponse.getString("response");  // Asume que la clave es "response"
                    System.out.println("Received from server: " + responseText);
                    return responseText;  // Retorna el texto de la respuesta
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout while waiting for the server response.");
                throw e;  // Rethrow if you want to handle it elsewhere
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error sending message or receiving response: " + e.getMessage());
            closeConnection();
        }
        return null;  // Retorna null si hubo un error o la respuesta no es como se esperaba
    }
 

    public synchronized void closeConnection() {
        try {
            if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                in.close();
                in = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
            System.out.println("Connection closed successfully.");
        } catch (IOException e) {
            System.out.println("Error closing the connection: " + e.getMessage());
        }
    }
}
