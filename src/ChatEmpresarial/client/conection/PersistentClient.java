package ChatEmpresarial.client.conection;

import java.io.*;
import java.net.*;

public class PersistentClient {
    private static volatile PersistentClient instance = null;
    private final String serverAddress = "192.168.77.157";
    private final int serverPort = 12345;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private PersistentClient() {
      
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
        socket = new Socket(serverAddress, serverPort);
        System.out.println("Socket connected.");
        out = null; // Reset out to reinitialize it later
        in = null;  // Reset in to reinitialize it later
    }
}

private synchronized void initializeOutputStream() throws IOException {
    if (out == null) {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush(); // Send serialization header immediately to avoid deadlocks
        System.out.println("Output stream initialized.");
    }
}

private synchronized void initializeInputStream() throws IOException {
    if (in == null) {
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Input stream initialized.");
    }
}

public void sendMessage(String message) {
    new Thread(() -> {
        try {
            initializeConnection();
            initializeOutputStream(); // Initialize or reuse output stream

            System.out.println("Entro aqui");
            out.writeObject(message);
            out.flush();
            System.out.println("Message sent: " + message);


        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
            closeConnection(); // Properly close and reset all resources on error
            e.printStackTrace();  
        }
    }).start();
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

