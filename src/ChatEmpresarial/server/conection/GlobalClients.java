/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.server.conection;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author mario
 */
public class GlobalClients {
    public static final ConcurrentHashMap<String, Socket> connectedClients = new ConcurrentHashMap<>();
}

