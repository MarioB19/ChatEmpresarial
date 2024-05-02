package ChatEmpresarial.server.conection;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import ChatEmpresarial.server.pages.LogPage;
import ChatEmpresarial.shared.utilities.Enumerators.DescripcionAccion;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private LogPage logPage;

    public ClientHandler(Socket clientSocket, LogPage logPage) {
        this.clientSocket = clientSocket;
        this.logPage = logPage;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                logPage.updateLog(DescripcionAccion.ENVIAR_MENSAJE, "Mensaje del cliente: " + inputLine);
                out.println("Servidor responde: " + inputLine);
                
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
           // logPage.updateLog(DescripcionAccion.ERROR_CONEXION, "Error en conexión con cliente: " + ex.getMessage());
        } finally {
            try {
                clientSocket.close();
                logPage.updateLog(DescripcionAccion.CERRAR_SESION, "Conexión cerrada con cliente: " + clientSocket.getInetAddress().toString());
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
             //   logPage.updateLog(DescripcionAccion.ERROR_CONEXION, "Error al cerrar conexión con cliente: " + ex.getMessage());
            }
        }
    }
}
