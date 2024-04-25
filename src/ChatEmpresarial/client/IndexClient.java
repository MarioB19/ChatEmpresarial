/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.client;

import ChatEmpresarial.client.conection.PersistentClient;

/**
 *
 * @author brand
 */

public class IndexClient {
    // Constructor que se ejecuta al instanciar esta clase.
    public IndexClient() {
        System.out.println("Iniciando conexión al servidor...");
        
        // Crear un hilo para manejar la conexión persistente.
        Thread clientThread = new Thread(new PersistentClient());
        clientThread.start(); // Iniciar el hilo para la conexión.
    }

}