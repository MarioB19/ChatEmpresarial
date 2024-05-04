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

public class RecoveryPasswordController {

    /**
     * Verifies user's favorite movie and food to recover password.
     *
     * @param username User's username
     * @param favoriteMovie User's favorite movie (hashed)
     * @param favoriteFood User's favorite food (hashed)
     * @return String Status code ("0" = match, "1" = no match or user not found, "-1" = SQL error)
     * @throws SQLException If a database access error occurs
     */
    public static String verifyUserDetails(String username, String favoriteMovie, String favoriteFood) throws SQLException {
        Conexion conexion = new Conexion();
        Connection con = conexion.getCon();

        String sql = "SELECT pelicula_favorita, comida_favorita FROM usuario WHERE nombre = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbFavoriteMovie = rs.getString("pelicula_favorita");
                String dbFavoriteFood = rs.getString("comida_favorita");

                if (dbFavoriteMovie.equals(favoriteMovie) && dbFavoriteFood.equals(favoriteFood)) {
                    return "0"; // Match found
                } else {
                    return "1"; // No match for movie or food
                }
            } else {
                return "1"; // Username not found
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecoveryPasswordController.class.getName()).log(Level.SEVERE, "SQL Error in verifyUserDetails", ex);
            throw ex; // Propagate the exception for external handling
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException ex) {
                Logger.getLogger(RecoveryPasswordController.class.getName()).log(Level.SEVERE, "Error closing connection", ex);
            }
        }
    }
}