/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.server.controllers;
import ChatEmpresarial.server.db.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BRUNO
 */
public class CreateGroupController extends Conexion {
    
    public static String createGroupInDatabase(String name, int adminId) {
        
    // Database connection and SQL statement
    
    Conexion conexion = new Conexion(); // Create a new instance to use the connection
    Connection con = conexion.getCon();
    PreparedStatement sql;
    
    try{
        
        sql = con.prepareStatement("INSERT INTO grupo (nombre, id_administrador, id_chat, fecha_creacion) VALUES (?, ?, ?, NOW())");
        sql.setString(1, name);
        sql.setInt(2, adminId);
        sql.setInt(3, 1);
        
        int result = sql.executeUpdate();
        return result > 0 ? "0" : "-1";

    }
    catch (SQLException ex) {
              
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
             return "-1";
        } finally {
            try {
                if (con != null) con.close(); // Close the connection explicitly
            } catch (SQLException ex) {
                Logger.getLogger(RegisterController.class.getName()).log(Level.SEVERE, "Error closing connection", ex);
            }
        }
    }

    
    /*public static int createChat() throws SQLException {
        Conexion conexion = new Conexion(); // Create a new instance to use the connection
        Connection con = conexion.getCon();
    }*/

    
    
}
