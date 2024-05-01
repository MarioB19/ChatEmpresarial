package ChatEmpresarial.client.conection;

import java.io.*;
import java.net.*;

public class PersistentClient implements Runnable {
    private static PersistentClient instance = null;

    private final String serverAddress = "192.168.100.20";  // Dirección IP del servidor
    private final int serverPort = 5432;                    // Puerto del servidor
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Constructor privado para implementar el patrón Singleton
    private PersistentClient() {}

    // Método estático para obtener la instancia única
    public static PersistentClient getInstance() {
        if (instance == null) {
            synchronized (PersistentClient.class) {
                if (instance == null) {
                    instance = new PersistentClient();
                    new Thread(instance).start(); // Iniciar el hilo del cliente
                }
            }
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            // Establece la conexión con el servidor
            socket = new Socket(serverAddress, serverPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Conectado al servidor: " + serverAddress + " en el puerto " + serverPort);

            // Lugar para agregar manejo de mensajes recibidos si es necesario

        } catch (IOException e) {
            System.out.println("Error conectando al servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para enviar mensajes al servidor
    public void sendMessage(Object message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error al enviar mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para recibir mensajes del servidor
    public Object receiveMessage() {
        try {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al recibir mensaje: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Método para cerrar la conexión y limpiar los recursos
    public void closeConnection() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
            System.out.println("Conexión cerrada correctamente.");
        } catch (IOException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
