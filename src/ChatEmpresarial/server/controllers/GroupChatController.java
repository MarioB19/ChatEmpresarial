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
    
}
