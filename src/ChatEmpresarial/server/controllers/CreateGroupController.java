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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author BRUNO
 */
public class CreateGroupController extends Conexion {
    
    public static String createGroupInDatabase(String adminName, String groupName, int[] participantIds) {
        
    // Database connection and SQL statement
    
    Conexion conexion = new Conexion(); // Create a new instance to use the connection
    Connection con = conexion.getCon();
        PreparedStatement sql;
        ResultSet rs;
        System.out.println("Creating group....");
        System.out.println("Data recieved to create group: " + adminName + groupName);
    
     try {
            // 1. Retrieve the admin ID using the admin's name
            sql = con.prepareStatement("SELECT id_usuario FROM usuario WHERE nombre = ?");
            sql.setString(1, adminName);
            rs = sql.executeQuery();
            if (!rs.next()) {
                System.out.println("Admin not found");
                return "-1"; // Admin not found
            }
            int adminId = rs.getInt("id_usuario");
            
            // 2. Create the group
            sql = con.prepareStatement("INSERT INTO grupo (nombre, id_administrador, fecha_creacion) VALUES (?, ?, NOW())", PreparedStatement.RETURN_GENERATED_KEYS);
            sql.setString(1, groupName);
            sql.setInt(2, adminId);
            int result = sql.executeUpdate();
            if (result == 0) {
                                System.out.println("Failed to create group");

                return "-1"; // Failed to create group
            }
            rs = sql.getGeneratedKeys();
            rs.next();
            int groupId = rs.getInt(1); // Retrieve the generated group ID

            // 3. Create the chat
            sql = con.prepareStatement("INSERT INTO chat (fecha_creacion) VALUES (NOW())", PreparedStatement.RETURN_GENERATED_KEYS);
            
            sql.executeUpdate();
            rs = sql.getGeneratedKeys();
            rs.next();
            int chatId = rs.getInt(1); // Retrieve the generated chat ID
            
            System.out.println("Chat ID generated when creating group: " + chatId);

            // 4. Add participants to the chat
            sql = con.prepareStatement("INSERT INTO participantes (id_chat, id_usuario, fecha_creacion) VALUES (?, ?, NOW())");
            for (int participantId : participantIds) {
                sql.setInt(1, chatId);
                sql.setInt(2, participantId);
                sql.executeUpdate();
            }
            sql.setInt(1, chatId);
            sql.setInt(2, adminId);
            sql.executeUpdate();

            // Update the chat ID in the group
            sql = con.prepareStatement("UPDATE grupo SET id_chat = ? WHERE id_grupo = ?");
            sql.setInt(1, chatId);
            sql.setInt(2, groupId);
            sql.executeUpdate();

            return "0"; // Success

        } catch (SQLException ex) {
            Logger.getLogger(CreateGroupController.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("sql exception");
            return "-1";
        } finally {
            try {
                if (con != null) con.close(); // Close the connection explicitly
            } catch (SQLException ex) {
                Logger.getLogger(RegisterController.class.getName()).log(Level.SEVERE, "Error closing connection", ex);
            }
        }
    }
    
    
        public static List<JSONObject> getAllGroups(String activeuser) throws SQLException {
        List<JSONObject> users = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getCon();

        // SQL query to fetch usernames and ids
        String query = "SELECT g.nombre AS NombreGrupo, g.id_administrador, g.id_chat, g.id_grupo FROM usuario u JOIN participantes p ON u.id_usuario = p.id_usuario JOIN chat c ON p.id_chat = c.id_chat JOIN grupo g ON c.id_chat = g.id_chat WHERE u.nombre = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, activeuser);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                JSONObject user = new JSONObject();
                user.put("id", rs.getInt("id_grupo"));
                user.put("nombre", rs.getString("NombreGrupo"));
                user.put("admin", rs.getString("id_administrador"));
                user.put("chat", rs.getString("id_chat"));
                users.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, "SQL Error in getAllUsersExceptSelf", ex);
            throw ex;
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException ex) {
                Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, "Error closing connection", ex);
            }
        }
        return users;
    }
}
