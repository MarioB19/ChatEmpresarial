/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.server.controllers;

/**
 *
 * @author aguil
 */


import ChatEmpresarial.server.db.Conexion;
import ChatEmpresarial.shared.utilities.Enumerators.DescripcionAccion;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import org.json.JSONArray;
        import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 * Clase: FriendInvitationController
 * Tipo: Controlador
 * Función: Realizar las solicitudes de las invitaciones de amistad
 */

public class FriendInvitationController {
   
    
          //Método para añadir la solicitud de amistad a la base de datos tomando en cuenta los nombres de usuario
          public static String AddInvitation(String remitenteUsername, String receptorUsername) {
    Conexion conexion = new Conexion(); // Instancia para obtener la conexión
    Connection con = conexion.getCon();

    try {
        // Obtener el ID del remitente
        String queryRemitente = "SELECT id_usuario FROM usuario WHERE nombre = ?";
        int remitenteId = -1;
        try (PreparedStatement sqlRemitente = con.prepareStatement(queryRemitente)) {
            sqlRemitente.setString(1, remitenteUsername);
            ResultSet rsRemitente = sqlRemitente.executeQuery();
            if (rsRemitente.next()) {
                remitenteId = rsRemitente.getInt("id_usuario");
            } else {
                return "-2"; // Retorna si el remitente no se encuentra
            }
        }

        // Obtener el ID del receptor
        String queryReceptor = "SELECT id_usuario FROM usuario WHERE nombre = ?";
        int receptorId = -1;
        try (PreparedStatement sqlReceptor = con.prepareStatement(queryReceptor)) {
            sqlReceptor.setString(1, receptorUsername);
            ResultSet rsReceptor = sqlReceptor.executeQuery();
            if (rsReceptor.next()) {
                receptorId = rsReceptor.getInt("id_usuario");
            } else {
                return "-3"; // Retorna si el receptor no se encuentra
            }
        }

        // Añadir la solicitud de amistad
        String insertSolicitud = "INSERT INTO solicitudes_amistad (id_remitente, id_receptor, estado_solicitud, fecha_creacion) VALUES (?, ?, 0, CURRENT_TIMESTAMP)";
        try (PreparedStatement sqlSolicitud = con.prepareStatement(insertSolicitud)) {
            sqlSolicitud.setInt(1, remitenteId);
            sqlSolicitud.setInt(2, receptorId);
            sqlSolicitud.executeUpdate();
        }

        return "0"; // Éxito

    } catch (SQLException ex) {
        ex.printStackTrace();
        return "-1"; // Error en SQL
    }
}
        
          //Método para eliminar una solicitud de amistad con base en el nombre del emisor 
          public static String CancelInvitation(String remitenteUsername, String receptorUsername) {
    Conexion conexion = new Conexion(); // Instancia para obtener la conexión
    Connection con = conexion.getCon();

    try {
        // Obtener el ID del remitente
        String queryRemitente = "SELECT id_usuario FROM usuario WHERE nombre = ?";
        int remitenteId = -1;
        try (PreparedStatement sqlRemitente = con.prepareStatement(queryRemitente)) {
            sqlRemitente.setString(1, remitenteUsername);
            ResultSet rsRemitente = sqlRemitente.executeQuery();
            if (rsRemitente.next()) {
                remitenteId = rsRemitente.getInt("id_usuario");
            } else {
                return "-2"; // Retorna si el remitente no se encuentra
            }
        }

        // Obtener el ID del receptor
        String queryReceptor = "SELECT id_usuario FROM usuario WHERE nombre = ?";
        int receptorId = -1;
        try (PreparedStatement sqlReceptor = con.prepareStatement(queryReceptor)) {
            sqlReceptor.setString(1, receptorUsername);
            ResultSet rsReceptor = sqlReceptor.executeQuery();
            if (rsReceptor.next()) {
                receptorId = rsReceptor.getInt("id_usuario");
            } else {
                return "-3"; // Retorna si el receptor no se encuentra
            }
        }

        // Eliminar la solicitud de amistad
        String deleteSolicitud = "DELETE FROM solicitudes_amistad WHERE id_remitente = ? AND id_receptor = ?";
        try (PreparedStatement sqlDelete = con.prepareStatement(deleteSolicitud)) {
            sqlDelete.setInt(1, remitenteId);
            sqlDelete.setInt(2, receptorId);
            sqlDelete.executeUpdate();
        }

        return "0"; // Éxito

    } catch (SQLException ex) {
        ex.printStackTrace();
        return "-1"; // Error en SQL
    }
}
          
          
          //Método para recibir todos las invitaciones que tiene la persona
          public static String GetReceivedInvitations(String receptorUsername) {
    Conexion conexion = new Conexion(); // Instancia para obtener la conexión
    Connection con = conexion.getCon();
    List<String> remitentesList = new ArrayList<>();

    try {
        // Obtener el ID del receptor
        String queryReceptor = "SELECT id_usuario FROM usuario WHERE nombre = ?";
        int receptorId = -1;
        try (PreparedStatement sqlReceptor = con.prepareStatement(queryReceptor)) {
            sqlReceptor.setString(1, receptorUsername);
            ResultSet rsReceptor = sqlReceptor.executeQuery();
            if (rsReceptor.next()) {
                receptorId = rsReceptor.getInt("id_usuario");
            } else {
                return "-2"; // Retorna si el receptor no se encuentra
            }
        }

        // Obtener los remitentes que han enviado solicitudes al receptor
        String querySolicitudes = "SELECT id_remitente FROM solicitudes_amistad WHERE id_receptor = ?";
        List<Integer> remitentesIds = new ArrayList<>();

        try (PreparedStatement sqlSolicitudes = con.prepareStatement(querySolicitudes)) {
            sqlSolicitudes.setInt(1, receptorId);
            ResultSet rsSolicitudes = sqlSolicitudes.executeQuery();

            while (rsSolicitudes.next()) {
                remitentesIds.add(rsSolicitudes.getInt("id_remitente"));
            }
        }

        // Obtener los nombres de los remitentes
        if (!remitentesIds.isEmpty()) {
            String ids = remitentesIds.toString().replace("[", "").replace("]", ""); // Convertir lista a cadena
            String queryUsuarios = "SELECT nombre FROM usuario WHERE id_usuario IN (" + ids + ")";

            // Ejecutar la consulta para obtener los nombres de los remitentes
            try (Statement sqlUsuarios = con.createStatement()) {
                ResultSet rsUsuarios = sqlUsuarios.executeQuery(queryUsuarios);

                while (rsUsuarios.next()) {
                    remitentesList.add(rsUsuarios.getString("nombre"));
                }
            }
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        return "-1"; // Error en SQL
    }

    // Convertir la lista de remitentes en un JSONArray
    JSONArray remitentesArray = new JSONArray(remitentesList);

    return remitentesArray.toString(); // Devuelve el JSON como un String
}
          
          
          //Método para aceptar una solicitud de un remitente con base en el receptor
          public static String acceptFriendInvitation(String remitenteUsername, String receptorUsername) {
    Conexion conexion = new Conexion(); // Crear una instancia para obtener la conexión
    Connection con = conexion.getCon();

    try {
        // Obtener los IDs del remitente y receptor
        String queryRemitente = "SELECT id_usuario FROM usuario WHERE nombre = ?";
        int remitenteId = -1;
        try (PreparedStatement sqlRemitente = con.prepareStatement(queryRemitente)) {
            sqlRemitente.setString(1, remitenteUsername);
            ResultSet rsRemitente = sqlRemitente.executeQuery();
            if (rsRemitente.next()) {
                remitenteId = rsRemitente.getInt("id_usuario");
            } else {
                LogController.insertLogStatic(DescripcionAccion.ERROR, "Aceptar solicitud de amistad: remitente no encontrado");
                return "-2"; // Remitente no encontrado
            }
        }

        String queryReceptor = "SELECT id_usuario FROM usuario WHERE nombre = ?";
        int receptorId = -1;
        try (PreparedStatement sqlReceptor = con.prepareStatement(queryReceptor)) {
            sqlReceptor.setString(1, receptorUsername);
            ResultSet rsReceptor = sqlReceptor.executeQuery();
            if (rsReceptor.next()) {
                receptorId = rsReceptor.getInt("id_usuario");
            } else {
                LogController.insertLogStatic(DescripcionAccion.ERROR, "Aceptar solicitud de amistad: receptor no encontrado");
                return "-3"; // Receptor no encontrado
            }
        }

        // Actualizar el estado de la solicitud de amistad
        String updateSolicitud = "UPDATE solicitud_amistad SET estado_solicitud = 1 WHERE id_remitente = ? AND id_receptor = ?";
        try (PreparedStatement sqlUpdate = con.prepareStatement(updateSolicitud)) {
            sqlUpdate.setInt(1, remitenteId);
            sqlUpdate.setInt(2, receptorId);
            sqlUpdate.executeUpdate();
        }

        // Crear un nuevo chat entre los dos amigos
        String insertChat = "INSERT INTO chat (fecha_creacion, tipo_chat) VALUES (NOW(), 1)";
        int chatId = -1;
        try (PreparedStatement sqlInsertChat = con.prepareStatement(insertChat, Statement.RETURN_GENERATED_KEYS)) {
            sqlInsertChat.executeUpdate();
            ResultSet rsKeys = sqlInsertChat.getGeneratedKeys();
            if (rsKeys.next()) {
                chatId = rsKeys.getInt(1);
            }
        }

        // Crear una nueva relación de amistad
        String insertAmistad = "INSERT INTO amistad (id_receptor, id_chat, estado_amistad, fecha_cracion) VALUES (?, ?, 1, NOW())";
        try (PreparedStatement sqlInsertAmistad = con.prepareStatement(insertAmistad)) {
            sqlInsertAmistad.setInt(1, receptorId);
            sqlInsertAmistad.setInt(2, chatId);
            sqlInsertAmistad.executeUpdate();
        }

        // Registrar la acción exitosa en los logs
        LogController.insertLogStatic(DescripcionAccion.ACEPTAR_SOLICITUD_AMISTAD, receptorUsername, remitenteUsername);

        return "0"; // Operación exitosa

    } catch (SQLException ex) {
        ex.printStackTrace();
        try {
            LogController.insertLogStatic(DescripcionAccion.ERROR, "Aceptar solicitud de amistad: error interno");
        } catch (SQLException logEx) {
            logEx.printStackTrace();
        }
        return "-1"; // Error SQL
    }
}

          
          
}
