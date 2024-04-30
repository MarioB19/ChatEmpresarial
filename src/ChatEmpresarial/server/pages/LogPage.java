package ChatEmpresarial.server.pages;

import ChatEmpresarial.server.controllers.LogController;
import ChatEmpresarial.shared.utilities.Enumerators.DescripcionAccion;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Ventana de la interfaz gráfica que muestra los logs y permite interactuar con ellos.
 */
public class LogPage extends JFrame {

    private LogController logController = new LogController(); // Controlador de los logs
    private JTextArea logTextArea = new JTextArea(30, 50); // Área de texto para mostrar los logs
    private ScheduledExecutorService executor; // Executor para tareas programadas

    /**
     * Constructor de LogPage que configura la interfaz y comienza el monitoreo de logs.
     */
    public LogPage() {
        configurarUI();
        iniciarMonitoreoDeCambios();
    }

    /**
     * Configura la interfaz de usuario, incluyendo el área de texto y botones.
     */
    private void configurarUI() {
        setTitle("Log Analysis");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(173, 216, 230)); // Color de fondo del panel

        logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logTextArea.setForeground(Color.WHITE);
        logTextArea.setBackground(Color.BLACK);
        logTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logTextArea);

        JButton saveButton = new JButton("Guardar Logs");
        saveButton.setFont(new Font("Arial", Font.BOLD, 20));
        saveButton.setBackground(new Color(135, 206, 250));
        saveButton.addActionListener(e -> saveLogs());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add(scrollPane, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(saveButton, gbc);

        add(mainPanel);
    }

    /**
     * Guarda los logs actuales en formato JSON.
     */
    private void saveLogs() {
        logController.saveLogsToJson(logTextArea);
    }
    
        
         public void updateLog(DescripcionAccion descripcion, String mensaje) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            logTextArea.append(mensaje + "\n"); // Actualizar UI
            try {
                // Insertar el mensaje en la base de datos como un log
                logController.insertLog(descripcion, mensaje); // Usando la acción específica proporcionada
            } catch (Exception e) {
                logTextArea.append("Error al insertar log en la base de datos: " + e.getMessage() + "\n");
            }
        });
    }

    /**
     * Inicia el monitoreo de cambios en los logs, actualizando la interfaz cada 5 segundos.
     */
    private void iniciarMonitoreoDeCambios() {
        executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            // Limpia los logs antiguos y carga los nuevos
            logController.setLogs(new ArrayList<>()); // Resetear los logs almacenados
            logController.loadLogs(logTextArea); // Cargar logs nuevos en el área de texto
            System.out.println("Logs actualizados.");
        };
        executor.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }


}
