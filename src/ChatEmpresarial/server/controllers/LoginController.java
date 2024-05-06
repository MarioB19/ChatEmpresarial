package ChatEmpresarial.server.controllers;

import ChatEmpresarial.server.db.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController extends Conexion {

    // Método para iniciar sesión
    public static String Logging(String username, String password) {
        if (username == null || username.isEmpty()) {
            return "-1";  // Retorna -1 si el nombre de usuario está vacío o es nulo
        }

        Conexion conexion = new Conexion(); // Crea una nueva instancia para usar la conexión
        try (Connection con = conexion.getCon()) {
            // Prepara la consulta SQL con BINARY para una comparación sensible a mayúsculas y minúsculas
            String sqlQuery = "SELECT nombre FROM usuario WHERE nombre = BINARY ? AND contrasena = BINARY ?";
            try (PreparedStatement stmt = con.prepareStatement(sqlQuery)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {  // Verifica si hay resultados
                        String foundUsername = rs.getString("nombre");
                        return foundUsername;  // Retorna el nombre de usuario encontrado
                    } else {
                        return "-1";  // Retorna -1 si no se encontró el usuario
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            return "-1";  // Retorna -1 si ocurre una excepción SQL
        }
    }
}
