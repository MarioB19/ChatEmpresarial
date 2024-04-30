package ChatEmpresarial.client;

import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.client.pages.LoginPage;
import javax.swing.SwingUtilities;

public class IndexClient {
    // Constructor que se ejecuta al instanciar esta clase.
    public IndexClient() {
        System.out.println("Iniciando conexión al servidor...");
        
        // Crear un hilo para manejar la conexión persistente.
        Thread clientThread = new Thread(new PersistentClient());
        clientThread.start(); // Iniciar el hilo para la conexión.

        // Lanzar la interfaz gráfica
        launchGUI();
    }

    private void launchGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginPage loginPage = new LoginPage();
                loginPage.setVisible(true); // Mostrar la ventana de login
            }
        });
    }

    public static void main(String[] args) {
        new IndexClient(); // Crear una instancia de IndexClient que inicia todo.
    }
}
