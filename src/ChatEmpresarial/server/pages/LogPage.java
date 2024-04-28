/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.server.pages;

import ChatEmpresarial.server.controllers.LogController;
import ChatEmpresarial.shared.models.Log;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.JTextArea;

/**
 *
 * @author aguil
 */
public class LogPage extends JFrame {

    private LogController logController = new LogController(); //Controlador de los logs
      JTextArea logTextArea = new JTextArea(30, 50); // Área grande para los logs
        private ScheduledExecutorService executor; // Declarar el ScheduledExecutorService
      
      //-------------------
      //Constructor
      //-------------------
      
      
            public LogPage()
            {
                Configuracion();
              iniciarMonitoreoDeCambios();
            }
            
            
      //--------------------
     //Métodos
     //---------------------
            
            
     //Métodos de configuración inicial de la página
    private void Configuracion() {
       
         setTitle("Log Analysis");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(); //Generar un panel para asignar los logs
        mainPanel.setBackground(new Color(173, 216, 230)); // Color de fondo del panel principal
        mainPanel.setLayout(new GridBagLayout());

      
        logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logTextArea.setForeground(Color.WHITE); // Color del texto
        logTextArea.setBackground(Color.BLACK); // Color del fondo
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        
        logController.loadLogs(logTextArea); //Cargar datos

        JScrollPane scrollPane = new JScrollPane(logTextArea); // Añadir scroll al área de texto

        JButton saveButton = new JButton("Guardar");
        saveButton.setFont(new Font("Arial", Font.BOLD, 20));
        saveButton.setBackground(new Color(135, 206, 250)); // Color del botón
        saveButton.addActionListener(e -> saveLogs()); //Listener para escuchar el momento en que se haga clic en el botón

        GridBagConstraints gbc = new GridBagConstraints(); //configuración del layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add(scrollPane, gbc); // Añadir el área de texto con scroll

        gbc.gridy = 1;
        gbc.weighty = 0; // Reducir el peso vertical para el botón
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(saveButton, gbc); // Añadir el botón de guardar

        add(mainPanel); // Añadir el panel principal al JFrame
        
        
    }
    
    
    
    //Método para llamar el método de guardar en json los datos desde el controlador
    private void saveLogs()
    {
      System.out.println("Guardando logs...");
    logController.saveLogsToJson(logTextArea);
    System.out.println("Logs guardados.");
    }
    
    
    
    //Método que estará constantemente llamando a la base de datos. Así actualiza la consola cada 5 segundos
  
    private void iniciarMonitoreoDeCambios() {
        executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            logController.setLogs(new ArrayList<Log>()); //Reiniicar el 
            logController.loadLogs(logTextArea);
            
            System.out.println("Verificando cambios...");
           
        };
        executor.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS); // Ejecutar cada 5 segundos
    }


    // Método para detener el monitoreo cuando la ventana se cierra
    @Override
    public void dispose() {
        super.dispose();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown(); // Apagar el executor
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
    
    
    





