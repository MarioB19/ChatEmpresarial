package ChatEmpresarial.server.conection;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import ChatEmpresarial.server.pages.LogPage;
import ChatEmpresarial.shared.utilities.Enumerators.DescripcionAccion;
import java.lang.reflect.InvocationTargetException;

public class PersistentServer {
    private final int port;
    private final ExecutorService executor;
    private final Semaphore semaphore; // Semáforo para limitar conexiones simultáneas
    private LogPage logPage; // Referencia a la ventana de logs

    public PersistentServer(int port, int maxConnections) {
        this.port = port;
        this.executor = Executors.newFixedThreadPool(10); // Fija el número de hilos en el pool
        this.semaphore = new Semaphore(maxConnections, true); // true para fairness
        initUI();
    }

    private void initUI() {
        try {
            // Inicializa y muestra la ventana de logs en el Event Dispatch Thread de Swing
            SwingUtilities.invokeAndWait(() -> {
                logPage = new LogPage();
                logPage.setVisible(true);
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(PersistentServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(PersistentServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logPage.updateLog(DescripcionAccion.SERVIDOR_INICIADO, "Servidor escuchando en el puerto " + port);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();  // Aceptar la conexión entrante
                    semaphore.acquire();  // Adquiere un permiso antes de proceder

                    logPage.updateLog(DescripcionAccion.CONEXION_ACEPTADA, "Cliente conectado desde: " + clientSocket.getInetAddress());

                    // Crear una nueva instancia de ClientHandler y enviarla al ExecutorService para manejarla en un hilo separado
                    ClientHandler handler = new ClientHandler(clientSocket, logPage, semaphore);
                    
                    executor.submit(() -> {
                        try {
                            handler.run();
                        } finally {
                            semaphore.release(); // Asegura que el semáforo se libere incluso si hay una excepción
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restablece el estado interrumpido
                    Logger.getLogger(PersistentServer.class.getName()).log(Level.SEVERE, "Thread was interrupted", e);
                    break; // Salir del bucle si el hilo fue interrumpido
                } catch (IOException e) {
                    Logger.getLogger(PersistentServer.class.getName()).log(Level.SEVERE, "Error accepting client connection", e);
                    // Considerar si continuar aceptando conexiones después de un error
                }
            }
        } catch (IOException e) {
            Logger.getLogger(PersistentServer.class.getName()).log(Level.SEVERE, "Could not open server socket on port " + port, e);
        } finally {
            executor.shutdownNow();
            //logPage.updateLog(DescripcionAccion.SERVIDOR_DETENIDO, "Servidor detenido.");
        }
    }

    
}
