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
import org.json.JSONArray;
import org.json.JSONObject;

public class UsersController {

    /**
     * Retrieves all user names from the database.
     *
     * @return List<String> List of user names
     * @throws SQLException If a database access error occurs
     */
    public static List<String> getAllUsers() throws SQLException {
        List<String> usernames = new ArrayList<>();
        Conexion conexion = new Conexion();  // Supone que tienes una clase que maneja la conexión
        Connection con = conexion.getCon();

        // SQL query to fetch usernames
        String sql = "SELECT nombre FROM usuario";  // Asegúrate de que 'usuario' sea el nombre correcto de tu tabla y 'nombre' el campo correcto
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                usernames.add(rs.getString("nombre"));  // Agrega el nombre de usuario a la lista
            }
        } catch (SQLException ex) {
            Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, "SQL Error in getAllUsers", ex);
            throw ex;  // Propagate the exception
        } finally {
            try {
                if (con != null) con.close();  // Cierra la conexión explícitamente
            } catch (SQLException ex) {
                Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, "Error closing connection", ex);
            }
        }

        return usernames;
    }
    
    public static List<JSONObject> getAllUsersExceptSelf(String activeuser) throws SQLException {
        List<JSONObject> users = new ArrayList<>();
        Conexion conexion = new Conexion();
        Connection con = conexion.getCon();

        // SQL query to fetch usernames and ids
        String query = "SELECT id_usuario, nombre FROM usuario WHERE nombre != ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, activeuser);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                JSONObject user = new JSONObject();
                user.put("id", rs.getInt("id_usuario"));
                user.put("nombre", rs.getString("nombre"));
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
