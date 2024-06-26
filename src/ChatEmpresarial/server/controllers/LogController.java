package ChatEmpresarial.server.controllers;

import ChatEmpresarial.server.db.Conexion;
import ChatEmpresarial.shared.models.Log;
import ChatEmpresarial.shared.utilities.Enumerators.DescripcionAccion;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

public class LogController {

    private ArrayList<Log> logs = new ArrayList<>();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ArrayList<Log> getLogs() {
        return logs;
    }

    public void setLogs(ArrayList<Log> logs) {
        this.logs = logs;
    }

    public void loadLogs(JTextArea textArea) {
        try {
            selectAllLogs();
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

    public void saveLogsToJson(JTextArea textArea) {
        Gson gson = new Gson();
        String json = gson.toJson(logs);
        try {
            FileWriter writer = new FileWriter("logs.json");
            writer.write(json);
            writer.close();
            deleteAllLogs();
            System.out.println("Logs guardados en formato JSON: " + json);
        } catch (IOException | SQLException ex) {
            Logger.getLogger(LogController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void insertLog(DescripcionAccion accion, Object... args) throws SQLException {
           Conexion conexion = new Conexion(); // Create a new instance to use the connection
      
        String descripcion = String.format(accion.getDescripcion(), args);
        PreparedStatement sql = conexion.getCon().prepareStatement("INSERT INTO log (descripcion, fecha_creacion) VALUES (?, NOW())");
        sql.setString(1, descripcion);
        sql.executeUpdate();
    }
    
    
       public static void insertLogStatic(DescripcionAccion accion, Object... args) throws SQLException {
    Conexion conexion = new Conexion(); // Crear una instancia para obtener la conexión
    String descripcion = String.format(accion.getDescripcion(), args);
    PreparedStatement sql = conexion.getCon().prepareStatement("INSERT INTO log (descripcion, fecha_creacion) VALUES (?, NOW())");
    sql.setString(1, descripcion);
    sql.executeUpdate();
}

    public void detectLog(String mensaje) {
        String[] mensajeDividido = mensaje.split(",");
        if (mensajeDividido.length > 1) {
            DescripcionAccion accion = DescripcionAccion.valueOf(mensajeDividido[1]);
            Object[] args = Arrays.copyOfRange(mensajeDividido, 2, mensajeDividido.length);
            try {
                insertLog(accion, args);
            } catch (SQLException ex) {
                Logger.getLogger(LogController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Mensaje mal formado, no se puede procesar.");
        }
    }

    private ArrayList<Log> selectAllLogs() throws SQLException {
           Conexion conexion = new Conexion(); // Create a new instance to use the connection
 
        PreparedStatement sql = conexion.getCon().prepareStatement("SELECT descripcion, fecha_creacion FROM log");
        ResultSet resultSet = sql.executeQuery();
        logs.clear();
        while (resultSet.next()) {
            String descripcion = resultSet.getString("descripcion");
            Timestamp fechaCreacion = resultSet.getTimestamp("fecha_creacion");
            logs.add(new Log(descripcion, fechaCreacion));
        }
        return logs;
    }

    private void deleteAllLogs() throws SQLException {
           Conexion conexion = new Conexion(); // Create a new instance to use the connection
    
        PreparedStatement sql = conexion.getCon().prepareStatement("DELETE FROM log");
        sql.executeUpdate();
    }
}
