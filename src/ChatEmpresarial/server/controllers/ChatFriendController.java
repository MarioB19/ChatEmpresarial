/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.server.controllers;

import ChatEmpresarial.server.db.Conexion;
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
 *
 * @author aguil
 */
public class ChatFriendController {
    
    //Metodo para obtener todos los mensajes realizados en un chat (NO RELACIONA CON USUARIO: SE NOS FUE)
    public static String obtainAllMessages(String remitente, String receptor) {
        Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
        Connection con = conexion.getCon();
        JSONArray mensajesArray = new JSONArray(); // Lista para almacenar los mensajes

        try {
            // Consulta para obtener el id del chat compartido entre remitente y receptor
            String queryAmistad = "SELECT id_chat FROM amistad WHERE (id_remitente = ? AND id_receptor = ?) OR (id_remitente = ? AND id_receptor = ?)";
            int idChat = -1;

            try (PreparedStatement sqlAmistad = con.prepareStatement(queryAmistad)) {
                sqlAmistad.setString(1, remitente);
                sqlAmistad.setString(2, receptor);
                sqlAmistad.setString(3, receptor);
                sqlAmistad.setString(4, remitente);

                ResultSet rsAmistad = sqlAmistad.executeQuery();
                if (rsAmistad.next()) {
                    idChat = rsAmistad.getInt("id_chat");
                }
            }

            
            // Consulta para obtener los mensajes del chat existente
            String queryMensajes = "SELECT contenido, nombre  FROM mensaje WHERE id_chat = ? ORDER BY fecha_creacion ASC";
            try (PreparedStatement sqlMensajes = con.prepareStatement(queryMensajes)) {
                sqlMensajes.setInt(1, idChat);
                ResultSet rsMensajes = sqlMensajes.executeQuery();

                // Recorrer los resultados de la consulta y crear objetos JSON para cada mensaje
                while (rsMensajes.next()) {
                    String contenido = rsMensajes.getString("contenido");
                   
                    String usuario = rsMensajes.getString("nombre");

                    JSONObject mensajeJson = new JSONObject();
                    mensajeJson.put("contenido", contenido);
                   
                    mensajeJson.put("usuario", usuario);

                    mensajesArray.put(mensajeJson); // Añadir al arreglo JSON
                }
            }

            // Crear un objeto JSON con el resultado
           
            
              try {
                  con.close();
              } catch (SQLException ex) {
                  java.util.logging.Logger.getLogger(FriendInvitationController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
              }
            return mensajesArray.toString(); // Retornar toda la respuesta como string

        } catch (SQLException ex) {
           
            return "-1"; // Devuelve un código de error en caso de excepción
        }
    }
    
    
    //Método para eliminar los mensajes de un chat
   public static String DeleteAllMessagesAndFriendship(String remitente, String receptor) {
    Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
    Connection con = conexion.getCon();

    try {
        // Consulta para obtener el id del chat compartido entre remitente y receptor
       String queryAmistad = "SELECT a.id_chat FROM amistad a " +
                              "INNER JOIN usuario u1 ON a.id_remitente = u1.id_usuario " +
                              "INNER JOIN usuario u2 ON a.id_receptor = u2.id_usuario " +
                              "WHERE (u1.nombre = ? AND u2.nombre = ?) OR (u1.nombre = ? AND u2.nombre = ?)";
        int idChat = -1;

        // Encontrar el id_chat
        try (PreparedStatement sqlAmistad = con.prepareStatement(queryAmistad)) {
            sqlAmistad.setString(1, remitente);
            sqlAmistad.setString(2, receptor);
            sqlAmistad.setString(3, receptor);
            sqlAmistad.setString(4, remitente);

            ResultSet rsAmistad = sqlAmistad.executeQuery();
            if (rsAmistad.next()) {
                idChat = rsAmistad.getInt("id_chat");
            } else {
                return "-1"; // Retornar -1 si no se encuentra el chat
            }
        }

        // Eliminar mensajes relacionados con el id_chat
        String deleteMensajesQuery = "DELETE FROM mensaje WHERE id_chat = ?";
        try (PreparedStatement sqlDeleteMensajes = con.prepareStatement(deleteMensajesQuery)) {
            sqlDeleteMensajes.setInt(1, idChat);
            sqlDeleteMensajes.executeUpdate();
        }

        // Eliminar el registro de amistad asociado
        String deleteAmistadQuery = "DELETE FROM amistad WHERE id_chat = ?";
        try (PreparedStatement sqlDeleteAmistad = con.prepareStatement(deleteAmistadQuery)) {
            sqlDeleteAmistad.setInt(1, idChat);
            sqlDeleteAmistad.executeUpdate();
        }
        
          try {
                  con.close();
              } catch (SQLException ex) {
                  java.util.logging.Logger.getLogger(FriendInvitationController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
              }

        return "1"; // Retornar "1" indicando éxito

    } catch (SQLException ex) {
        
        return "-1"; // Retornar "-1" en caso de excepción
    }
    
   }
   
   public static String SendMessage(String remitente, String receptor, String contenido) {
    Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
    Connection con = conexion.getCon();
    int idChat = -1;

    try {
        // Encuentra el `id_chat` para el remitente y receptor dados
        String queryAmistad = "SELECT id_chat FROM amistad WHERE ((id_remitente = ? AND id_receptor = ?) OR (id_remitente = ? AND id_receptor = ?)) AND tipo_chat=1";
        try (PreparedStatement sqlAmistad = con.prepareStatement(queryAmistad)) {
            sqlAmistad.setString(1, remitente);
            sqlAmistad.setString(2, receptor);
            sqlAmistad.setString(3, receptor);
            sqlAmistad.setString(4, remitente);

            ResultSet rsAmistad = sqlAmistad.executeQuery();
            if (rsAmistad.next()) {
                idChat = rsAmistad.getInt("id_chat");
            } else {
                // Si no existe, retornar -1 indicando que el chat no se encuentra
                return "-1";
            }
        }

        // Inserta el nuevo mensaje en la tabla `mensaje`
        String insertMensajeQuery = "INSERT INTO mensaje (id_chat, contenido, fecha_creacion, nombre) VALUES (?, ?, ?, ?)";
        try (PreparedStatement sqlInsertMensaje = con.prepareStatement(insertMensajeQuery)) {
            sqlInsertMensaje.setInt(1, idChat);
            sqlInsertMensaje.setString(2, contenido);
            sqlInsertMensaje.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            sqlInsertMensaje.setString(4, remitente);
            sqlInsertMensaje.executeUpdate();
        }
  try {
                  con.close();
              } catch (SQLException ex) {
                  java.util.logging.Logger.getLogger(FriendInvitationController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
              }
        // Retornar "1" indicando éxito
        return "1";

    } catch (SQLException ex) {
      
        return "-1"; // Retornar "-1" en caso de excepción
    }
    
    
    
   
}
   
    public static String findFriends(String remitenteUsername) {
        Conexion conexion = new Conexion(); // Instancia para obtener la conexión
        Connection con = conexion.getCon();
        List<String> friendsList = new ArrayList<>();

        try {
            // Consulta para obtener el `id_usuario` del remitente
            String queryRemitente = "SELECT id_usuario FROM usuario WHERE nombre = ?";
            int remitenteId = -1;

            // Encontrar el ID del remitente usando su nombre de usuario
            try (PreparedStatement sqlRemitente = con.prepareStatement(queryRemitente)) {
                sqlRemitente.setString(1, remitenteUsername);
                ResultSet rsRemitente = sqlRemitente.executeQuery();
                if (rsRemitente.next()) {
                    remitenteId = rsRemitente.getInt("id_usuario");
                } else {
                    // Retornar un JSON vacío si el remitente no se encuentra
                    return "-2";
                }
            }

            // Consulta para obtener los `id_receptor` relacionados con el remitente
            String queryAmistad = "SELECT id_receptor FROM amistad WHERE id_remitente = ?";
            List<Integer> friendsIds = new ArrayList<>();

            // Encontrar todos los amigos relacionados al remitente
            try (PreparedStatement sqlAmistad = con.prepareStatement(queryAmistad)) {
                sqlAmistad.setInt(1, remitenteId);
                ResultSet rsAmistad = sqlAmistad.executeQuery();

                while (rsAmistad.next()) {
                    friendsIds.add(rsAmistad.getInt("id_receptor"));
                }
            }
            
            if(friendsIds.isEmpty())
            {
                queryAmistad = "SELECT id_remitente FROM amistad WHERE id_receptor = ?";
           friendsIds = new ArrayList<>();

            // Encontrar todos los amigos relacionados al remitente
            try (PreparedStatement sqlAmistad = con.prepareStatement(queryAmistad)) {
                sqlAmistad.setInt(1, remitenteId);
                ResultSet rsAmistad = sqlAmistad.executeQuery();

                while (rsAmistad.next()) {
                    friendsIds.add(rsAmistad.getInt("id_remitente"));
                }
            }
                
            }
            

            // Consulta para obtener los nombres de los amigos
            if (!friendsIds.isEmpty()) {
                String ids = friendsIds.toString().replace("[", "").replace("]", ""); // Convertir lista a cadena
                String queryUsuarios = "SELECT nombre FROM usuario WHERE id_usuario IN (" + ids + ")";

                // Ejecutar la consulta para obtener los nombres de usuario
                try (Statement sqlUsuarios = con.createStatement()) {
                    ResultSet rsUsuarios = sqlUsuarios.executeQuery(queryUsuarios);

                    while (rsUsuarios.next()) {
                        friendsList.add(rsUsuarios.getString("nombre"));
                    }
                }
            }

        } catch (SQLException ex) {
            return "-1";
        }

        // Convertir la lista de amigos en un JSONArray
        JSONArray friendsArray = new JSONArray(friendsList);
  try {
                  con.close();
              } catch (SQLException ex) {
                  java.util.logging.Logger.getLogger(FriendInvitationController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
              }
        
        return friendsArray.toString(); // Devuelve el JSON como un String
    }
   
}
