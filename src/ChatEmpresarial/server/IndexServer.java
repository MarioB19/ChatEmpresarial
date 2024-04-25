/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.server;

import ChatEmpresarial.server.conection.PersistentServer;

/**
 *
 * @author brand
 */
public class IndexServer {
    private PersistentServer server; // Objeto para manejar el servidor.

    // Constructor que inicia el servidor cuando se instancia esta clase.
    public IndexServer() {
        System.out.println("Iniciando el servidor...");

        int port = 12345; // Puerto en el que el servidor escuchará.
        server = new PersistentServer(port); // Crear el servidor persistente.
        server.start(); // Iniciar el servidor.
    }

  
}