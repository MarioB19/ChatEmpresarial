/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.server.controllers;

import ChatEmpresarial.server.db.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecoveryPassword2Controller {

    /**
     * Updates the password for a given username.
     *
     * @param username User's username
     * @param newPassword New password (assumed to be hashed already)
     * @return String Status code ("0" = success, "-1" = SQL error)
     * @throws SQLException If a database access error occurs
     */
    public static String updatePassword(String username, String newPassword) throws SQLException {
        Conexion conexion = new Conexion();
        Connection con = conexion.getCon();

        String sql = "UPDATE usuario SET contrasena = ? WHERE nombre = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);

            int result = pstmt.executeUpdate();
            return result > 0 ? "0" : "-1"; // Return "0" if the update was successful, "-1" if not
        } catch (SQLException ex) {
            Logger.getLogger(RecoveryPassword2Controller.class.getName()).log(Level.SEVERE, "SQL Error in updatePassword", ex);
            return "-1"; // Return "-1" in case of SQL error
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException ex) {
                Logger.getLogger(RecoveryPassword2Controller.class.getName()).log(Level.SEVERE, "Error closing connection", ex);
            }
        }
    }
}
