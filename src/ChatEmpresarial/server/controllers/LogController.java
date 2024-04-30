/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.server.controllers;

import ChatEmpresarial.server.db.Conexion;
import com.google.gson.Gson;
import java.util.ArrayList;
import javax.swing.JTextArea;
import java.sql.Timestamp;
import java.lang.String;
import ChatEmpresarial.shared.models.Log;
import ChatEmpresarial.shared.utilities.Enumerators.DescripcionAccion;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.PreparedStatement;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ConnectionBuilder;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aguil
 */
/**
 * Clase: LogController Tipo: Controlador Función: Permite administrar la lógica
 * detras de la página de los logs
 *
 * @author aguil
 */
public class LogController extends Conexion {

    private ArrayList<Log> logs = new ArrayList<>();
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private Log logFormato = new Log();
    
    //-------------
    //Getters y setters
    //----------------

    public ArrayList<Log> getLogs() {
        return logs;
    }

    public void setLogs(ArrayList<Log> logs) {
        this.logs = logs;
    }
    
    

    //------------------------------
    //Métodos de la página
    //------------------------------
    //Método para cargar los logs en la ventana de registros
    public void loadLogs(JTextArea textArea) {

        try {
            this.selectAllLogs();
            
            StringBuilder sb = new StringBuilder();
            for (Log log : logs) {
                String formattedDate = dateFormatter.format(log.getFecha_creacion());
                sb.append(log.getDescripcion()).append(" - ").append(formattedDate).append("\n");
            }
            textArea.setText(sb.toString());
        } catch (SQLException ex) {
            Logger.getLogger(LogController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Método para almacenar los logs en formato JSON usando GSON
    public void saveLogsToJson(JTextArea textArea) {
        Gson gson = new Gson();
       
        String json = gson.toJson(logs);
        
        try {
            //llamar al método para eliminar logs de la BD
            DeleteAllLogs();
        } catch (SQLException ex) {
            Logger.getLogger(LogController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(json);
    }

    //---------------------------------
    //Métodos de la BD
    //--------------------------------
    //Método para insertar logs 
    public void insertLog(DescripcionAccion accion, Object... args) throws SQLException { //Se colocan los parametros necesarios
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fecha = dateFormatter.format(new Date());
        String descripcion = String.format(accion.getDescripcion(), args);

        PreparedStatement sql;

        sql = super.getCon().prepareStatement("INSERT INTO log(descripcion, fecha_creacion) VALUES (?, NOW())");
        sql.setString(1, descripcion);
          
        sql.executeUpdate();
    }

   
    //Método para recibir logs por parte del cliente: se manda a llamar en el servidor el insertlog()
    
      public void detectLog(String mensaje)
      {
          String[] mensajeDividido = mensaje.split(","); //MEnsaje donde la primera parte es: 
                                            /* Mensaje, Tipo de log, parametros*/
           
          if (mensajeDividido.length > 1) {
        String tipoAccion = mensajeDividido[1];
        DescripcionAccion accion = DescripcionAccion.valueOf(tipoAccion); //TIpo de Acción. Realizar casteo

        // Recolectar argumentos adicionales (puede depender del tipo de Log)
        Object[] args = Arrays.copyOfRange(mensajeDividido, 2, mensajeDividido.length);

              try {
                  // Llamar al método insertLog con la acción y los argumentos
                  insertLog(accion, args);
              } catch (SQLException ex) {
                  Logger.getLogger(LogController.class.getName()).log(Level.SEVERE, null, ex);
              }
    } else {
        System.out.println("Mensaje mal formado, no se puede procesar.");
    }
                                            

         
      }

    
    
  

    
    //--------------------------
    //Métodos privados
    //--------------------------
    private void parseLogsFromTextArea(JTextArea textArea) {
        String[] lines = textArea.getText().split("\\n");
        logs.clear();
        for (String line : lines) {
            int timeIndex = line.lastIndexOf(" a las ");
            String descripcion = line.substring(0, timeIndex);
            String timeStr = line.substring(timeIndex + 7);
            try {
                Timestamp timestamp = new Timestamp(dateFormatter.parse(timeStr).getTime());
                logs.add(new Log(descripcion, timestamp));
            } catch (Exception e) {
                e.printStackTrace(); // Manejar la excepción apropiadamente
            }
        }

    }
    
    
      //Método para tomar los logs
    private ArrayList<Log> selectAllLogs() throws SQLException {
        
        PreparedStatement sql;
        sql = super.getCon().prepareStatement("SELECT descripcion, fecha_creacion FROM log");
        ResultSet resultSet = sql.executeQuery();

        while (resultSet.next()) {
            String descripcion = resultSet.getString("descripcion");
            Timestamp fechaCreacion = resultSet.getTimestamp("fecha_creacion");
            logs.add(new Log(descripcion, fechaCreacion));
        }
        return logs;
    }
    
    
    
     //Método para borrar los logs
    
    private void DeleteAllLogs() throws SQLException
    {
         PreparedStatement sql;
        sql = super.getCon().prepareStatement("DELETE FROM log");
        
        sql.executeUpdate();
        
        
    }
    
}
