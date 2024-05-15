/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.server.controllers;

import ChatEmpresarial.server.db.Conexion;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author BRUNO
 */
public class GroupChatController {

    public static String obtainAllMessages(int chatId) {
        Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
        Connection con = conexion.getCon();
        JSONArray mensajesArray = new JSONArray(); // Lista para almacenar los mensajes

        try {
            // Consulta para obtener los mensajes del chat de grupo
            String queryMensajes = "SELECT contenido, fecha_creacion  FROM mensaje WHERE id_chat = ? ORDER BY fecha_creacion ASC";
            try (PreparedStatement sqlMensajes = con.prepareStatement(queryMensajes)) {
                sqlMensajes.setInt(1, chatId);
                ResultSet rsMensajes = sqlMensajes.executeQuery();

                // Recorrer los resultados de la consulta y crear objetos JSON para cada mensaje
                while (rsMensajes.next()) {
                    String contenido = rsMensajes.getString("contenido");
                    Timestamp fechaCreacion = rsMensajes.getTimestamp("fecha_creacion");

                    JSONObject mensajeJson = new JSONObject();
                    mensajeJson.put("contenido", contenido);
                    mensajeJson.put("fecha_creacion", fechaCreacion.toString());

                    mensajesArray.put(mensajeJson); // Añadir al arreglo JSON
                }
            }

            // Crear un objeto JSON con el resultado
            JSONObject response = new JSONObject();
            response.put("idChat", chatId);
            response.put("mensajes", mensajesArray);

            return response.toString(); // Retornar toda la respuesta como string

        } catch (SQLException ex) {

            return "-1"; // Devuelve un código de error en caso de excepción
        }
    }

    public static String SendMessage(int idChat, String contenido) {
        Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
        Connection con = conexion.getCon();

        try {
            // Inserta el nuevo mensaje en la tabla `mensaje`
            String insertMensajeQuery = "INSERT INTO mensaje (id_chat, contenido, fecha_creacion) VALUES (?, ?, ?)";
            try (PreparedStatement sqlInsertMensaje = con.prepareStatement(insertMensajeQuery)) {
                sqlInsertMensaje.setInt(1, idChat);
                sqlInsertMensaje.setString(2, contenido);
                sqlInsertMensaje.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                sqlInsertMensaje.executeUpdate();
            }

            // Retornar "1" indicando éxito
            return "1";

        } catch (SQLException ex) {

            return "-1"; // Retornar "-1" en caso de excepción
        }
    }

    public static String getConnected(String chatId, JSONArray connectedUsers) {
        Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
        Connection con = conexion.getCon();
        JSONArray users = new JSONArray(); // Lista para almacenar los nombres de usuario

        try {
            // Construir la parte de la cláusula IN dinámicamente
            StringBuilder inClause = new StringBuilder();
            inClause.append("(");
            for (int i = 0; i < connectedUsers.length(); i++) {
                inClause.append("?");
                if (i < connectedUsers.length() - 1) {
                    inClause.append(", ");
                }
            }
            inClause.append(")");

            // Consulta para obtener los nombres de usuario del chat de grupo
            String queryUsers = "SELECT u.nombre FROM usuario u JOIN participantes p ON u.id_usuario = p.id_usuario WHERE p.id_chat = ? AND u.nombre IN " + inClause.toString();
            try (PreparedStatement sqlUsers = con.prepareStatement(queryUsers)) {
                sqlUsers.setInt(1, Integer.parseInt(chatId));

                // Establecer los parámetros IN con los nombres de los usuarios conectados
                for (int i = 0; i < connectedUsers.length(); i++) {
                    sqlUsers.setString(i + 2, connectedUsers.getString(i));
                }

                ResultSet rs = sqlUsers.executeQuery();

                // Recorrer los resultados de la consulta y añadir los nombres de usuario al JSONArray
                while (rs.next()) {
                    String nombreUsuario = rs.getString("nombre");
                    users.put(nombreUsuario); // Añadir al arreglo JSON
                }
            }

            // Crear un objeto JSON con el resultado
            return users.toString();

        } catch (SQLException ex) {
            ex.printStackTrace();
            return "-1"; // Devuelve un código de error en caso de excepción
        }
    }

    private static void checkGroupSize(String idGrupo, String idChat) {
        Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
        Connection con = conexion.getCon();

        try {
            // Consulta para obtener la suma total de personas en el grupo
            String query = "SELECT "
                    + "SUM(total_personas) AS total_personas_en_grupo "
                    + "FROM ( "
                    + "SELECT "
                    + "COUNT(DISTINCT p.id_usuario) AS total_personas "
                    + "FROM grupo g "
                    + "LEFT JOIN participantes p ON g.id_chat = p.id_chat "
                    + "WHERE g.id_grupo = ? "
                    + "UNION ALL "
                    + "SELECT "
                    + "COUNT(DISTINCT s.id_receptor) AS total_personas "
                    + "FROM solicitudes_grupo s "
                    + "WHERE s.id_grupo = ? "
                    + ") AS counts;";
            try (PreparedStatement sql = con.prepareStatement(query)) {
                sql.setInt(1, parseInt(idGrupo));
                sql.setInt(2, parseInt(idGrupo));
                ResultSet rs = sql.executeQuery();

                // Verificar si el resultado es menor a 3
                if (rs.next()) {
                    int totalPersonas = rs.getInt("total_personas_en_grupo");
                    if (totalPersonas < 3) {
                        // Eliminar el grupo si total_personas_en_grupo es menor a 3
                        String deleteGroupQuery = "DELETE FROM grupo WHERE id_grupo = ?";
                        try (PreparedStatement deleteGroupSql = con.prepareStatement(deleteGroupQuery)) {
                            deleteGroupSql.setInt(1, parseInt(idGrupo));
                            int rowsAffectedGroup = deleteGroupSql.executeUpdate();
                            if (rowsAffectedGroup > 0) {
                                System.out.println("Grupo eliminado porque tiene menos de 3 personas");

                                // Eliminar el chat asociado al grupo
                                String deleteChatQuery = "DELETE FROM chat WHERE id_chat = ?";
                                try (PreparedStatement deleteChatSql = con.prepareStatement(deleteChatQuery)) {
                                    deleteChatSql.setInt(1, parseInt(idChat));
                                    int rowsAffectedChat = deleteChatSql.executeUpdate();
                                    if (rowsAffectedChat > 0) {
                                        System.out.println("Chat eliminado asociado al grupo");
                                    } else {
                                        System.out.println("No se pudo eliminar el chat asociado al grupo");
                                    }
                                }
                            } else {
                                System.out.println("No se pudo eliminar el grupo");
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error interno: " + ex.toString());
        }
    }

    public static void deleteGroup(String idChat, String idGrupo) {
        Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
        Connection con = conexion.getCon();
        // Eliminar el grupo si total_personas_en_grupo es menor a 3
        String deleteGroupQuery = "DELETE FROM grupo WHERE id_grupo = ?";
        try (PreparedStatement deleteGroupSql = con.prepareStatement(deleteGroupQuery)) {
            deleteGroupSql.setInt(1, parseInt(idGrupo));
            int rowsAffectedGroup = deleteGroupSql.executeUpdate();
            if (rowsAffectedGroup > 0) {
                System.out.println("Grupo eliminado");

                // Eliminar el chat asociado al grupo
                String deleteChatQuery = "DELETE FROM chat WHERE id_chat = ?";
                try (PreparedStatement deleteChatSql = con.prepareStatement(deleteChatQuery)) {
                    deleteChatSql.setInt(1, parseInt(idChat));
                    int rowsAffectedChat = deleteChatSql.executeUpdate();
                    if (rowsAffectedChat > 0) {
                        System.out.println("Chat eliminado asociado al grupo");
                    } else {
                        System.out.println("No se pudo eliminar el chat asociado al grupo");
                    }
                }
            } else {
                System.out.println("No se pudo eliminar el grupo");
            }
        } catch (SQLException ex) {
            System.out.println("Error interno al intentar eliminar grupo: " + ex.toString());
        }
    }

    public static void eliminarParticipante(String idChat, String nombreUsuario, String idGrupo) {
        Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
        Connection con = conexion.getCon();

        try {
            // Obtener el id_usuario correspondiente al nombre del usuario
            String queryUsuario = "SELECT id_usuario FROM usuario WHERE nombre = ?";
            try (PreparedStatement sqlUsuario = con.prepareStatement(queryUsuario)) {
                sqlUsuario.setString(1, nombreUsuario);
                ResultSet rsUsuario = sqlUsuario.executeQuery();
                if (rsUsuario.next()) {
                    int idUsuario = rsUsuario.getInt("id_usuario");

                    // Eliminar el participante
                    String queryEliminarParticipante = "DELETE FROM participantes WHERE id_chat = ? AND id_usuario = ?";
                    try (PreparedStatement sqlEliminarParticipante = con.prepareStatement(queryEliminarParticipante)) {
                        sqlEliminarParticipante.setInt(1, parseInt(idChat));
                        sqlEliminarParticipante.setInt(2, idUsuario);
                        int filasAfectadas = sqlEliminarParticipante.executeUpdate();
                        if (filasAfectadas > 0) {
                            checkGroupSize(idGrupo, idChat);
                            System.out.println("Participante eliminado correctamente");

                        } else {
                            System.out.println("No se encontró el participante para eliminar");
                        }
                    }
                } else {
                    System.out.println("No se encontró el usuario");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al eliminar participante: " + ex.toString());
        }
    }

    public static String obtenerUsuariosGrupoExceptoUsuario(String idChat, String nombreUsuarioExcluir) {
        JSONArray usuariosArray = new JSONArray();
        Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
        Connection con = conexion.getCon();

        try {
            // Obtener el id_usuario del usuario a excluir
            String queryIdUsuarioExcluir = "SELECT id_usuario FROM usuario WHERE nombre = ?";
            try (PreparedStatement sqlIdUsuarioExcluir = con.prepareStatement(queryIdUsuarioExcluir)) {
                sqlIdUsuarioExcluir.setString(1, nombreUsuarioExcluir);
                ResultSet rsIdUsuarioExcluir = sqlIdUsuarioExcluir.executeQuery();
                if (rsIdUsuarioExcluir.next()) {
                    int idUsuarioExcluir = rsIdUsuarioExcluir.getInt("id_usuario");

                    // Obtener los nombres de usuario del grupo excepto el usuario a excluir
                    String queryUsuariosGrupo = "SELECT u.nombre FROM participantes p JOIN usuario u ON p.id_usuario = u.id_usuario WHERE p.id_chat = ? AND u.id_usuario != ?";
                    try (PreparedStatement sqlUsuariosGrupo = con.prepareStatement(queryUsuariosGrupo)) {
                        sqlUsuariosGrupo.setInt(1, parseInt(idChat));
                        sqlUsuariosGrupo.setInt(2, idUsuarioExcluir);
                        ResultSet rsUsuariosGrupo = sqlUsuariosGrupo.executeQuery();
                        while (rsUsuariosGrupo.next()) {
                            String nombreUsuario = rsUsuariosGrupo.getString("nombre");
                            usuariosArray.put(nombreUsuario);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al obtener usuarios del grupo: " + ex.toString());
            return "-1"; // Devuelve un código de error en caso de excepción
        }

        // Crear un objeto JSON con la lista de usuarios
        JSONObject responseJson = new JSONObject();
        responseJson.put("usuarios", usuariosArray);

        return responseJson.toString(); // Retornar el objeto JSON como una cadena de texto
    }

    public static String obtenerUsuariosFueraDelGrupo(String idChat, String idGrupo) {
        JSONArray usuariosFueraDelGrupo = new JSONArray();
        Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
        Connection con = conexion.getCon();

        try {
            // Consulta para obtener los usuarios que no están en el grupo
            String queryUsuariosFueraDelGrupo = "SELECT u.nombre, u.id_usuario FROM usuario u "
                    + "WHERE u.id_usuario NOT IN ("
                    + "    SELECT id_receptor FROM solicitudes_grupo WHERE id_grupo = ?"
                    + ") AND u.id_usuario NOT IN ("
                    + "    SELECT id_usuario FROM participantes WHERE id_chat = ?"
                    + ")";
            try (PreparedStatement sqlUsuariosFueraDelGrupo = con.prepareStatement(queryUsuariosFueraDelGrupo)) {
                sqlUsuariosFueraDelGrupo.setInt(1, parseInt(idGrupo));
                sqlUsuariosFueraDelGrupo.setInt(2, parseInt(idChat));
                ResultSet rsUsuariosFueraDelGrupo = sqlUsuariosFueraDelGrupo.executeQuery();
                while (rsUsuariosFueraDelGrupo.next()) {
                    String nombreUsuario = rsUsuariosFueraDelGrupo.getString("nombre");
                    usuariosFueraDelGrupo.put(nombreUsuario);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al obtener usuarios fuera del grupo: " + ex.toString());
            return "-1"; // Devuelve un código de error en caso de excepción
        }

        // Crear un objeto JSON con la lista de usuarios
        JSONObject responseJson = new JSONObject();
        responseJson.put("usuarios", usuariosFueraDelGrupo);

        return responseJson.toString(); // Retornar el objeto JSON como una cadena de texto
    }

    public static int getChatIdForGroup(String groupId) {
        Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
        Connection con = conexion.getCon();
        int chatId = -1; // Valor predeterminado en caso de que no se encuentre ningún ID de chat

        try {
            String query = "SELECT id_chat FROM grupo WHERE id_grupo = ?";
            try (PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setInt(1, Integer.parseInt(groupId));
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    chatId = rs.getInt("id_chat");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al obtener ID del chat del grupo: " + ex.getMessage());
        }

        return chatId;
    }

    public static void deleteRequestById(String requestId, String idGrupo) {
        Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
        Connection con = conexion.getCon();

        try {
            String query = "DELETE FROM solicitudes_grupo WHERE solicitud_id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setInt(1, Integer.parseInt(requestId));
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Solicitud eliminada correctamente");
                    int idchat = getChatIdForGroup(idGrupo);
                    checkGroupSize(idGrupo, Integer.toString(idchat));
                } else {
                    System.out.println("No se encontró ninguna solicitud con ese ID");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al eliminar la solicitud: " + ex.getMessage());
        }
    }

    public static void acceptRequest(String requestId, String idGrupo) {
        Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
        Connection con = conexion.getCon();
        int chatId = getChatIdForGroup(idGrupo);
        int userId = getUserIdFromRequest(requestId);

        try {
            // Insertar al usuario en la tabla participantes
            String insertQuery = "INSERT INTO participantes (id_chat, id_usuario, fecha_creacion) VALUES (?, ?, NOW())";
            try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, chatId);
                insertStmt.setInt(2, userId);
                insertStmt.executeUpdate();
            }

            // Eliminar la solicitud
            String deleteQuery = "DELETE FROM solicitudes_grupo WHERE solicitud_id = ?";
            try (PreparedStatement deleteStmt = con.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, Integer.parseInt(requestId));
                int rowsAffected = deleteStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Solicitud eliminada correctamente");
                } else {
                    System.out.println("No se encontró ninguna solicitud con ese ID");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al procesar la solicitud: " + ex.getMessage());
        }
    }

    public static int getUserIdFromRequest(String requestId) {
        Conexion conexion = new Conexion(); // Instanciar para obtener la conexión
        Connection con = conexion.getCon();

        int userId = -1; // Valor por defecto en caso de que no se encuentre el usuario

        try {
            // Consulta para obtener el ID del usuario a partir del ID de la solicitud
            String query = "SELECT id_receptor FROM solicitudes_grupo WHERE solicitud_id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(query)) {
                pstmt.setInt(1, Integer.parseInt(requestId));
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt("id_receptor");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al obtener el ID del usuario: " + ex.getMessage());
        }

        return userId;
    }

    public static String agregarParticipante(String idGrupo, String idReceptor, String Remitente) {
        // Database connection and SQL statement
        Conexion conexion = new Conexion(); // Create a new instance to use the connection
        Connection con = conexion.getCon();
        PreparedStatement sql;
        ResultSet rs;
        System.out.println("Adding to group....");

        try {
            // 1. Retrieve the admin ID using the admin's name
            sql = con.prepareStatement("SELECT id_usuario FROM usuario WHERE nombre = ?");
            sql.setString(1, Remitente);
            rs = sql.executeQuery();
            if (!rs.next()) {
                System.out.println("Admin not found");
                return "-1";
            }
            int adminId = rs.getInt("id_usuario");

            // 1. Retrieve the users ID using the name
            sql = con.prepareStatement("SELECT id_usuario FROM usuario WHERE nombre = ?");
            sql.setString(1, idReceptor);
            rs = sql.executeQuery();
            if (!rs.next()) {
                System.out.println("Admin not found");
                return "-1";
            }
            int idreceptor = rs.getInt("id_usuario");

            //Añadir solicitudes
            sql = con.prepareStatement("INSERT INTO solicitudes_grupo (id_grupo, id_receptor, id_remitente, fecha_creacion) VALUES (?, ?, ?, NOW())");
            sql.setInt(1, Integer.valueOf(idGrupo));
            sql.setInt(2, idreceptor);
            sql.setInt(3, adminId);
            sql.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Error al obtener el ID del usuario: " + ex.getMessage());
        }

        return "0";
    }

}
