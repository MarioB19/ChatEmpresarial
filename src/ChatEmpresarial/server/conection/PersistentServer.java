package ChatEmpresarial.server.conection;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import ChatEmpresarial.server.pages.LogPage;
import ChatEmpresarial.shared.utilities.Enumerators.DescripcionAccion;

public class PersistentServer {
    private final int port;
    private final ExecutorService executor;
    private LogPage logPage; // Referencia a la ventana de logs

    public PersistentServer(int port) {
        this.port = port;
        this.executor = Executors.newFixedThreadPool(10); // Fija el número de hilos en el pool
        initUI();
    }

    private void initUI() {
        // Inicializa y muestra la ventana de logs en el Event Dispatch Thread de Swing
        try {
            SwingUtilities.invokeAndWait(() -> {
                logPage = new LogPage();
                logPage.setVisible(true);
            });
        } catch (Exception e) {
            Logger.getLogger(PersistentServer.class.getName()).log(Level.SEVERE, "Failed to initialize UI", e);
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            if (logPage == null) {
                throw new IllegalStateException("LogPage has not been initialized properly.");
            }

            logPage.updateLog(DescripcionAccion.SERVIDOR_INICIADO, "Servidor escuchando en el puerto " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();  // Aceptar la conexión entrante
                logPage.updateLog(DescripcionAccion.CONEXION_ACEPTADA, "Cliente conectado desde: " + clientSocket.getInetAddress());

                // Crear una nueva instancia de ClientHandler y enviarla al ExecutorService para manejarla en un hilo separado
                ClientHandler handler = new ClientHandler(clientSocket, logPage);
                executor.submit(handler);
            }

        } catch (IOException e) {
            Logger.getLogger(PersistentServer.class.getName()).log(Level.SEVERE, null, e);
            if (logPage != null) {
                   System.out.println("Error al iniciar el servidor: " + e.getMessage());
               // logPage.updateLog(DescripcionAccion.ERROR_SERVIDOR, "Error al iniciar el servidor: " + e.getMessage());
            } else {
                System.out.println("Error al iniciar el servidor: " + e.getMessage());
            }
        }
    }
}
