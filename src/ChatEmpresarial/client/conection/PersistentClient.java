/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.client.conection;

/**
 *
 * @author brand
 */

import java.io.*;
import java.net.*;


public class PersistentClient implements Runnable {
    private final String serverAddress = "localhost"; // Dirección del servidor
    private final int serverPort = 12345; // Puerto del servidor

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
            
        }
    }
}