package ChatEmpresarial.server.conection;

import ChatEmpresarial.server.controllers.RegisterController;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

import ChatEmpresarial.server.pages.LogPage;
import ChatEmpresarial.shared.utilities.Enumerators.DescripcionAccion;
import ChatEmpresarial.shared.utilities.Enumerators.TipoRequest;
import java.sql.SQLException;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private LogPage logPage;

    public ClientHandler(Socket clientSocket, LogPage logPage) {
        this.clientSocket = clientSocket;
        this.logPage = logPage;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
            out.flush(); // Asegúrate de que la cabecera de ObjectOutputStream se envíe.
            
            Object inputObject;
            while ((inputObject = in.readObject()) != null) {
                String json = inputObject.toString(); // Suponiendo que el mensaje llega como String o como un objeto que se puede convertir en String.
                //logPage.updateLog(DescripcionAccion.ENVIAR_MENSAJE, "Mensaje del cliente: " + message)
                
           
            JSONObject jsonObject = new JSONObject(json); // Parse the string to a JSONObject
            String action = jsonObject.getString("action"); // Extract the 'action' field
            
            TipoRequest requestType = TipoRequest.valueOf(action.toUpperCase()); // Convertir string a enum
            
            String response;

            switch (requestType) {
                case REGISTER:
                    response = handleRegister(jsonObject);
                    
                    break;
                case LOGIN:
                    response = handleLogin(jsonObject);
                    break;
                    
                default:
                    response = handleUnknownAction();
                    
            }
            
            JSONObject responseJson = new JSONObject();
                 responseJson.put("response", response);
                    
                  
                out.writeObject(responseJson.toString());
                out.flush(); // Asegura que la respuesta sea enviada inmediatamente.
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error" + ex.getMessage());
            //logPage.updateLog(DescripcionAccion.ERROR_CONEXION, "Error en conexión con cliente: " + ex.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                clientSocket.close();
                logPage.updateLog(DescripcionAccion.CERRAR_SESION, "Conexión cerrada con cliente: " + clientSocket.getInetAddress().toString());
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                  System.out.println("Error" + ex.getMessage());
               // logPage.updateLog(DescripcionAccion.ERROR_CONEXION, "Error al cerrar conexión con cliente: " + ex.getMessage());
            }
        }
    }
    
      public String handleRegister(JSONObject data) throws SQLException {
        System.out.println("Handling register with data: " + data.toString());
        
        // Extracción de datos del JSON
        String username = data.optString("username");
        String password = data.optString("password");
        String favoriteMovie = data.optString("favoriteMovie");
        String favoriteFood = data.optString("favoriteFood");
        
       
    
        
          return  RegisterController.insertUser(username, password, favoriteMovie, favoriteFood);
         
         
          
    }


    private String handleLogin(JSONObject data) {
        System.out.println("Handling login with data: " + data.toString());
        
        return "200";
    }



    private String handleUnknownAction() {
        System.out.println("Received unknown action.");
        
        return "200";
    }
        
        
}
