package ChatEmpresarial.server.controllers;

import ChatEmpresarial.server.db.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterController {

    /**
     * Attempts to insert a new user into the database.
     *
     * @param username User's username
     * @param password User's password (assumed to be hashed already)
     * @param favoriteMovie User's favorite movie (hashed)
     * @param favoriteFood User's favorite food (hashed)
     * @return int Status code (0 = success, 1 = user exists, -1 = SQL error)
     * @throws SQLException If a database access error occurs
     */
    public static String insertUser(String username, String password, String favoriteMovie, String favoriteFood) throws SQLException {
        Conexion conexion = new Conexion(); // Create a new instance to use the connection
        Connection con = conexion.getCon();

        // First, check if the username already exists
        String checkUserSql = "SELECT COUNT(*) FROM usuario WHERE nombre = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkUserSql)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return "1"; // The user already exists
            }
        }

        // If the user does not exist, proceed with the insertion
        String sql = "INSERT INTO usuario (nombre, contrasena, pelicula_favorita, comida_favorita, estado, fecha_creacion) VALUES (?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, favoriteMovie);
            
            pstmt.setString(4, favoriteFood);
            pstmt.setInt(5,0);
            
            
            int result = pstmt.executeUpdate();
            return result > 0 ? "0" : "-1"; // Returns 0 if the insertion is successful, -1 if not
        } catch (SQLException ex) {
            Logger.getLogger(RegisterController.class.getName()).log(Level.SEVERE, "SQL Error in insertUser", ex);
            throw ex; // Propagate the exception for external handling
        } finally {
            try {
                if (con != null) con.close(); // Close the connection explicitly
            } catch (SQLException ex) {
                Logger.getLogger(RegisterController.class.getName()).log(Level.SEVERE, "Error closing connection", ex);
            }
        }
    }
}
