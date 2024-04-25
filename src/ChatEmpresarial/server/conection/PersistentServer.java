/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.server.conection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author brand
 */


 
public class PersistentServer {
    private final int port; // Puerto en el que el servidor escucha.
    private final ExecutorService executor; // Para gestionar múltiples hilos de clientes.

    // Constructor para inicializar el servidor.
    public PersistentServer(int port) {
        this.port = port;
        this.executor = Executors.newFixedThreadPool(10); // Crear un pool de 10 hilos.
    }

    // Método para iniciar el servidor y escuchar conexiones entrantes.
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor escuchando en el puerto " + port);

            // Bucle para aceptar conexiones entrantes.
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Esperar a que un cliente se conecte.
                System.out.println("Cliente conectado desde: " + clientSocket.getInetAddress());

                // Asignar la conexión del cliente a un hilo.
                executor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
          
        }
    }

    // Clase para gestionar la comunicación con los clientes.
    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String inputLine;

                // Bucle para leer y responder mensajes del cliente.
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Mensaje del cliente: " + inputLine);

                    // Enviar una respuesta al cliente.
                    out.println("Servidor dice: " + inputLine);

                    // Salir si el cliente envía "salir".
                    if ("salir".equalsIgnoreCase(inputLine)) {
                        out.println("Adiós, cerrando conexión");
                        break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(PersistentServer.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    // Asegurarse de cerrar el socket del cliente al finalizar.
                    clientSocket.close();
                } catch (IOException ex) {
                    Logger.getLogger(PersistentServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

  
}