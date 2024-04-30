package ChatEmpresarial.client.conection;

import ChatEmpresarial.client.pages.LoginPage;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class PersistentClient implements Runnable {
    private final String serverAddress = "192.168.100.20"; // Dirección del servidor
    private final int serverPort = 5432; // Puerto del servidor

    public static void main(String[] args) {
        // Separar la creación de la interfaz y la conexión en métodos distintos
        PersistentClient client = new PersistentClient();
  
        client.startClient(); // Iniciar el hilo del cliente
    }


    private void startClient() {
        new Thread(this).start(); // Iniciar el hilo del cliente
    }

    @Override
    public void run() {
        try (
            Socket socket = new Socket(serverAddress, serverPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Conectado al servidor: " + serverAddress + " en el puerto " + serverPort);

            String input;
            while (true) { // Bucle infinito para mantener la conexión abierta.
                System.out.println("Escribe un mensaje para enviar al servidor:");

                if ((input = userInput.readLine()) != null) {
                    out.println(input); // Enviar al servidor.
                    String serverResponse = in.readLine(); // Leer la respuesta.
                    System.out.println("Respuesta del servidor: " + serverResponse);

                    if ("Adiós, cerrando conexión".equalsIgnoreCase(serverResponse)) {
                        break; // Salir si el servidor cierra la conexión.
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
