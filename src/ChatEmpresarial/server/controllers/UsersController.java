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
}
