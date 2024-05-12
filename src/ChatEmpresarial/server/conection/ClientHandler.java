package ChatEmpresarial.server.conection;

import ChatEmpresarial.server.controllers.ChatFriendController;
import ChatEmpresarial.server.controllers.FriendInvitationController;
import ChatEmpresarial.server.controllers.LogController;
import ChatEmpresarial.server.controllers.LoginController;
import ChatEmpresarial.server.controllers.RecoveryPasswordController;
import ChatEmpresarial.server.controllers.RecoveryPassword2Controller;
import ChatEmpresarial.server.controllers.RegisterController;
import ChatEmpresarial.server.controllers.UsersController;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

import ChatEmpresarial.server.pages.LogPage;
import ChatEmpresarial.shared.utilities.Enumerators.DescripcionAccion;
import ChatEmpresarial.shared.utilities.Enumerators.TipoRequest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.json.JSONArray;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private LogPage logPage;
    private Semaphore semaphore;

    //Diccionario de clientes conectados
    public ClientHandler(Socket clientSocket, LogPage logPage, Semaphore semaphore) {
        this.clientSocket = clientSocket;
        this.logPage = logPage;
        this.semaphore = semaphore;

    }

    @Override
    public void run() {
        try ( ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());  ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
            out.flush(); // Asegúrate de que la cabecera de ObjectOutputStream se envíe.

            Object inputObject;
            while ((inputObject = in.readObject()) != null) {
                String json = inputObject.toString(); // Suponiendo que el mensaje llega como String o como un objeto que se puede convertir en String.
                //logPage.updateLog(DescripcionAccion.ENVIAR_MENSAJE, "Mensaje del cliente: " + message)

                JSONObject jsonObject = new JSONObject(json); // Parse the string to a JSONObject
                String action = jsonObject.getString("action"); // Extract the 'action' field

                TipoRequest requestType = TipoRequest.valueOf(action.toUpperCase()); // Convertir string a enum
                System.out.println("request type" + requestType);
                System.out.println("Accion recibida " + action);
                String response = "";

                switch (requestType) {
                    case REGISTER:
                        response = handleRegister(jsonObject);
                        break;
                        
                    case LOGOUT:
                        
                     
                     String username = jsonObject.getString("nombre");
                    response = "0"; 
                    JSONObject responseJson = new JSONObject();
                    responseJson.put("response", response);
                    out.writeObject(responseJson.toString());
                    out.flush();  
                    handleLogout(username); 
                    return; 
                   
                        

                        
                    case LOGIN:
                        response = handleLogin(jsonObject);

                        if (response != "-1") //Si no es un error 
                        {
                            //Almacenar el cliente 
                            if (GlobalClients.connectedClients.containsKey(response)) {
                                System.out.println("Error: El cliente ya existe en el diccionario con el nombre de usuario '" + response + "'.");
                            } else {
                                // Almacenar el cliente
                                GlobalClients.connectedClients.put(response, clientSocket);
                                System.out.println("Cliente agregado: " + response + ". Total de clientes conectados ahora: " + GlobalClients.connectedClients.size());
                            }

                            //Retona un 0, indicando que todo salio bien
                            response = "0";
                        }

                        break;

                    case FORGOTPSW1:
                        response = handleRecoveryPassword(jsonObject);
                        break;
                    case FORGOTPSW2:
                        response = handleRecoveryPassword2(jsonObject);
                        break;

             

        
                    case SEND_MESSAGE_FRIEND:
                        response = handleSendMessageFriend(clientSocket,jsonObject);
                        break;

                    case DELETE_CHAT_FRIEND:
                          response = handleDeleteAllMessagesAndFriendship(clientSocket, jsonObject);
                        break;

                    case FIND_FRIENDS_CONNECTED:
                        response = handleFindFriendsConnected(clientSocket);

                        break;

                    // Encontrar amigos desconectados
                    case FIND_FRIENDS_DISCONNECTED:
                        response = handleFindFriendsDisconnected(clientSocket);
                        break;

                    // Obtener todos los mensajes entre amigos
                    case GET_MESSAGE_FRIEND:
                        response = handleObtainAllMessages(clientSocket, jsonObject);
                        break;

                    // Enviar una solicitud de amistad
                    case SENT_INVITATION_FRIEND:
                        response = handleSentFriendInvitation(clientSocket, jsonObject);
                        break;

                    // Rechazar una solicitud de amistad
                    case REFUSE_INVITATION_FRIEND:
                        response = handleCancelFriendInvitation(clientSocket, jsonObject);
                        break;

                    // Obtener las solicitudes de amistad recibidas
                    case GET_INVITATION_FRIEND:
                        response = handleGetReceivedInvitations(clientSocket, jsonObject);
                        break;

                    // Aceptar una solicitud de amistad
                    case ACCEPT_INVITATION_FRIEND:
                        response = handleAcceptFriendInvitation(clientSocket, jsonObject);
                        break;

               

                    // Encontrar amigos conectados
                   

                    case FIND_USERS_CONNECTED:
                        response = handleFindUsersConnected();

                        break;

                    case FIND_USERS_DISCONNECTED:
                        response = handleFindUsersDisconnected();
                        break;

                    case CREATE_CHAT_USERS:
                        response = ChatManager.createChat(jsonObject.getString("user1"), jsonObject.getString("user2"));
                        break;

                    case SEND_MESSAGE_CHAT_USERS:
                        System.out.println("Entra aqui");
                        response = ChatManager.sendMessage(
                                jsonObject.getString("user1"),
                                jsonObject.getString("user2"),
                                jsonObject.getString("message")
                        );
                        break;
                    case GET_MESSAGES_CHAT_USERS:
                        List<String> messages = ChatManager.getChatMessages(
                                jsonObject.getString("user1"),
                                jsonObject.getString("user2")
                        );
                        response = new JSONObject().put("messages", messages).toString();
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
                removeClientBySocket(clientSocket); // Se elimina el socket del diccionario

                ChatManager.cleanUpChats();

                logPage.updateLog(DescripcionAccion.CERRAR_SESION, "Conexión cerrada con cliente: " + clientSocket.getInetAddress().toString());
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error" + ex.getMessage());
            }

            semaphore.release();
        } //FINALLY

    } //RUN

    public String handleRegister(JSONObject data) throws SQLException {
        System.out.println("Handling register with data: " + data.toString());

        // Extracción de datos del JSON
        String username = data.optString("username");
        String password = data.optString("password");
        String favoriteMovie = data.optString("favoriteMovie");
        String favoriteFood = data.optString("favoriteFood");

        return RegisterController.insertUser(username, password, favoriteMovie, favoriteFood);

    }

//Método que maneja el login
    private String handleLogin(JSONObject data) {
        System.out.println("Handling login with data: " + data.toString());

        //Extracción de datos del JSON
        String username = data.optString("username");
        String password = data.optString("password");

        return LoginController.Logging(username, password);
    }

    public String handleRecoveryPassword(JSONObject data) throws SQLException {
        System.out.println("Handling recovery password with data: " + data.toString());

        // Extracción de datos del JSON
        String username = data.optString("username");
        String favoriteMovie = data.optString("favoriteMovie");
        String favoriteFood = data.optString("favoriteFood");

        return RecoveryPasswordController.verifyUserDetails(username, favoriteMovie, favoriteFood);

    }

    public String handleRecoveryPassword2(JSONObject data) throws SQLException {
        System.out.println("Handling recovery password with data: " + data.toString());

        // Extracción de datos del JSON
        String username = data.optString("username");
        String password = data.optString("password");

        return RecoveryPassword2Controller.updatePassword(username, password);

    }

    //Método que maneja el cierre de sesión
    private void removeClientBySocket(Socket socket) {
        String keyToRemove = null;
        for (HashMap.Entry<String, Socket> entry : GlobalClients.connectedClients.entrySet()) {
            if (entry.getValue().equals(socket)) {
                keyToRemove = entry.getKey();
                break; //Se termina de buscar una vez encontrado
            }
        }
        if (keyToRemove != null) {
            GlobalClients.connectedClients.remove(keyToRemove); // Elimina la entrada del diccionario una vez encontrado
        }
    }

    //Método que identifica la llave del Cliente ( su nombre de usuario)
    private String IdentifyUserName(Socket socket) {
        String Username = null;

        for (HashMap.Entry<String, Socket> entry : GlobalClients.connectedClients.entrySet()) {
            if (entry.getValue().equals(socket)) {
                Username = entry.getKey();
                break; //Se termina de buscar una vez encontrado
            }
        }
        return Username;
    }

   
    

    //Método para encontrar a todos los amigos conectados
    private String handleFindFriendsConnected(Socket clientSocket) {
        JSONObject respuesta = new JSONObject();
        try {
            // Identifica el nombre del remitente
            String remitenteUsername = IdentifyUserName(clientSocket);

            if (remitenteUsername == null) {
                respuesta.put("status", "-1");
                return respuesta.toString();
            }

            // Obtén la lista de amigos del remitente
            String friendsListJson = ChatFriendController.findFriends(remitenteUsername);
            JSONArray friendsList = new JSONArray(friendsListJson);

            // Lista para amigos conectados
            JSONArray amigosConectados = new JSONArray();

            // Verifica cuáles amigos están conectados
            for (int i = 0; i < friendsList.length(); i++) {
                String amigoNombre = friendsList.getString(i);
                if (GlobalClients.connectedClients.containsKey(amigoNombre)) {
                    amigosConectados.put(amigoNombre);
                }
            }

            // Devuelve los amigos conectados como un JSON
            respuesta.put("status", "0");
            respuesta.put("message", amigosConectados);

            return respuesta.toString();

        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("status", "-2");
            return respuesta.toString();
        }
    }

    // Método para encontrar a todos los amigos desconectados
    private String handleFindFriendsDisconnected(Socket clientSocket) {
        JSONObject respuesta = new JSONObject();
        try {
            // Identifica el nombre del remitente
            String remitenteUsername = IdentifyUserName(clientSocket);

            if (remitenteUsername == null) {
                respuesta.put("status", "-1");
                return respuesta.toString();
            }

            // Obtén la lista de amigos del remitente
            String friendsListJson = ChatFriendController.findFriends(remitenteUsername);
            JSONArray friendsList = new JSONArray(friendsListJson);

            // Lista para amigos desconectados
            JSONArray amigosDesconectados = new JSONArray();

            // Verifica cuáles amigos no están conectados
            for (int i = 0; i < friendsList.length(); i++) {
                String amigoNombre = friendsList.getString(i);
                if (!GlobalClients.connectedClients.containsKey(amigoNombre)) {
                    amigosDesconectados.put(amigoNombre);
                }
            }

            // Devuelve los amigos desconectados como un JSON
            respuesta.put("status", "0");
            respuesta.put("message", amigosDesconectados);

            return respuesta.toString();

        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("status", "-2");
            return respuesta.toString();
        }
    }

//Método para enviar una solicitud de amistad 
    private String handleSentFriendInvitation(Socket clientSocket, JSONObject jsonObject) {
        JSONObject respuesta = new JSONObject();
        try {
            // Identificar el nombre del remitente
            String remitenteUsername = IdentifyUserName(clientSocket);

            if (remitenteUsername == null) {
                respuesta.put("status", "-4");
                respuesta.put("message", "Remitente no identificado");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Intentar enviar una solicitud de amistad: remitente no identificado");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return respuesta.toString();
            }

            // Deserializar el JSON para obtener el nombre del receptor
            String receptorUsername = jsonObject.optString("receptor");

            // Verificar que el nombre del receptor esté presente
            if (receptorUsername == null || receptorUsername.isEmpty()) {
                respuesta.put("status", "-5");
                respuesta.put("message", "Campo 'receptor' faltante en la solicitud");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Intentar enviar una solicitud de amistad: falta el campo 'receptor'");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return respuesta.toString();
            }

            // Llamar al método para agregar la solicitud de amistad
            String result = FriendInvitationController.AddInvitation(remitenteUsername, receptorUsername);

            // Incluir el resultado en la respuesta
            if (result.equals("0")) {
                respuesta.put("status", "0");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ENVIAR_SOLICITUD_AMISTAD, remitenteUsername, receptorUsername);
                } catch (SQLException ex) {

                }
            } else {
                respuesta.put("status", result);
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Error al enviar una solicitud de amistad de " + remitenteUsername + " a " + receptorUsername);
                } catch (SQLException ex) {

                }
            }

            return respuesta.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            respuesta.put("status", "-6");
            respuesta.put("message", "Error interno");
            try {
                LogController.insertLogStatic(DescripcionAccion.ERROR, "Error interno al enviar una solicitud de amistad");
            } catch (SQLException exSql) {

            }
            return respuesta.toString();
        }
    }

    private String handleCancelFriendInvitation(Socket clientSocket, JSONObject jsonObject) {
        JSONObject respuesta = new JSONObject();
        try {
            // Identificar el nombre del remitente desde el socket
            String receptorUsername = IdentifyUserName(clientSocket);

            if (receptorUsername == null) {
                respuesta.put("status", "-5");
                respuesta.put("message", "Remitente no identificado");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Intentar cancelar una solicitud de amistad: remitente no identificado");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return respuesta.toString();
            }

            // Deserializar el JSON para obtener el nombre del receptor
            String remitenteUsername = jsonObject.optString("receptor");

            // Verificar que el nombre del receptor esté presente
            if (receptorUsername == null || receptorUsername.isEmpty()) {
                respuesta.put("status", "-6");
                respuesta.put("message", "Campo 'receptor' faltante en la solicitud");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Intentar cancelar una solicitud de amistad: falta el campo 'receptor'");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return respuesta.toString();
            }

            // Llamar al método para cancelar la solicitud de amistad
            String result = FriendInvitationController.CancelInvitation(remitenteUsername, receptorUsername);

            // Incluir el resultado en la respuesta con un mapeo de códigos distinto
            switch (result) {
                case "0":
                    respuesta.put("status", "0");
                    respuesta.put("message", "Solicitud de amistad cancelada con éxito");
                    try {
                        LogController.insertLogStatic(DescripcionAccion.ELIMINAR_AMISTAD, remitenteUsername, receptorUsername);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case "-2":
                    respuesta.put("status", "-2");
                    respuesta.put("message", "Remitente o receptor no encontrado");
                    try {
                        LogController.insertLogStatic(DescripcionAccion.ERROR, "Error al cancelar la solicitud de amistad: remitente o receptor no encontrado");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case "-1":
                default:
                    respuesta.put("status", "-1");
                    respuesta.put("message", "Error al cancelar la solicitud de amistad");
                    try {
                        LogController.insertLogStatic(DescripcionAccion.ERROR, "Error al cancelar la solicitud de amistad entre " + remitenteUsername + " y " + receptorUsername);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    break;
            }

            return respuesta.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            respuesta.put("status", "-7");
            respuesta.put("message", "Error interno");
            try {
                LogController.insertLogStatic(DescripcionAccion.ERROR, "Error interno al cancelar la solicitud de amistad");
            } catch (SQLException exSql) {
                exSql.printStackTrace();
            }
            return respuesta.toString();
        }
    }

//Método para recibir las solicitudes
    private String handleGetReceivedInvitations(Socket clientSocket, JSONObject jsonObject) {
        JSONObject respuesta = new JSONObject();
        try {
            // Identificar el nombre del receptor usando el socket
            String receptorUsername = IdentifyUserName(clientSocket);

            if (receptorUsername == null) {
                respuesta.put("status", "-4");
                respuesta.put("message", "Receptor no identificado");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Intentar obtener solicitudes recibidas: receptor no identificado");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return respuesta.toString();
            }

            // Llamar al método que obtiene las solicitudes de amistad recibidas
            String receivedInvitationsJson = FriendInvitationController.GetReceivedInvitations(receptorUsername);

            // Verificar si el resultado es un código de error
            if (receivedInvitationsJson.equals("-2")) {
                respuesta.put("status", "-5");
                respuesta.put("message", "Receptor no encontrado");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Intentar obtener solicitudes recibidas: receptor " + receptorUsername + " no encontrado");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else if (receivedInvitationsJson.equals("-1")) {
                respuesta.put("status", "-6");
                respuesta.put("message", "Error al obtener solicitudes recibidas");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Error en SQL al obtener solicitudes recibidas para " + receptorUsername);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                // Si no es un error, devolver la lista de solicitudes recibidas
                JSONArray receivedInvitationsArray = new JSONArray(receivedInvitationsJson);
                respuesta.put("status", "0");
                respuesta.put("message", receivedInvitationsArray);
                try {
                    LogController.insertLogStatic(DescripcionAccion.OBTENER_SOLICITUDES, receptorUsername);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            return respuesta.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            respuesta.put("status", "-7");
            respuesta.put("message", "Error interno");
            try {
                LogController.insertLogStatic(DescripcionAccion.ERROR, "Error interno al obtener solicitudes recibidas");
            } catch (SQLException exSql) {
                exSql.printStackTrace();
            }
            return respuesta.toString();
        }
    }

//Método para recibir soliciudes
    private String handleAcceptFriendInvitation(Socket clientSocket, JSONObject jsonObject) {
        JSONObject respuesta = new JSONObject();
        try {
            // Identificar el nombre del receptor (quien acepta la solicitud)
            String receptorUsername = IdentifyUserName(clientSocket);

            if (receptorUsername == null) {
                respuesta.put("status", "-4");
                respuesta.put("message", "Receptor no identificado");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Intentar aceptar una solicitud de amistad: receptor no identificado");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return respuesta.toString();
            }

            // Deserializar el JSON para obtener el nombre del remitente (quien envió la solicitud)
            String remitenteUsername = jsonObject.optString("receptor");

            if (remitenteUsername == null || remitenteUsername.isEmpty()) {
                respuesta.put("status", "-5");
                respuesta.put("message", "Campo 'receptor' faltante en la solicitud");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Intentar aceptar una solicitud de amistad: falta el campo 'receptor'");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return respuesta.toString();
            }

            // Llamar al método para aceptar la solicitud de amistad
            String result = FriendInvitationController.acceptFriendInvitation(remitenteUsername, receptorUsername);

            // Evaluar el resultado
            switch (result) {
                case "0":
                    respuesta.put("status", "0");
                    respuesta.put("message", "Solicitud de amistad aceptada con éxito");
                    try {
                        LogController.insertLogStatic(DescripcionAccion.ACEPTAR_SOLICITUD_AMISTAD, remitenteUsername, receptorUsername);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case "-2":
                    respuesta.put("status", "-6");
                    respuesta.put("message", "Remitente o receptor no encontrado");
                    try {
                        LogController.insertLogStatic(DescripcionAccion.ERROR, "Error al aceptar la solicitud de amistad: remitente o receptor no encontrado");
                    } catch (SQLException ex) {

                    }
                    break;
                case "-1":
                default:
                    respuesta.put("status", "-7");
                    respuesta.put("message", "Error al aceptar la solicitud de amistad");
                    try {
                        LogController.insertLogStatic(DescripcionAccion.ERROR, "Error al aceptar la solicitud de amistad entre " + remitenteUsername + " y " + receptorUsername);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    break;
            }

            return respuesta.toString();

        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("status", "-8");
            respuesta.put("message", "Error interno");
            try {
                LogController.insertLogStatic(DescripcionAccion.ERROR, "Error interno al aceptar una solicitud de amistad");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return respuesta.toString();
        }
    }

//Método para cargar todos los mensajes
    private String handleObtainAllMessages(Socket clientSocket, JSONObject jsonObject) {
        JSONObject respuesta = new JSONObject();
        // Identificar el nombre del receptor (quien acepta la solicitud)
        String receptorUsername = IdentifyUserName(clientSocket);
        try {
            if (receptorUsername == null) {
                respuesta.put("status", "-4");
                respuesta.put("message", "Receptor no identificado");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Intentar aceptar una solicitud de amistad: receptor no identificado");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return respuesta.toString();
            }

            // Deserializar el JSON para obtener el nombre del remitente (quien envió la solicitud)
            String remitenteUsername = jsonObject.optString("receptor");

            if (remitenteUsername.isEmpty() || receptorUsername.isEmpty()) {
                respuesta.put("status", "-4");
                respuesta.put("message", "Campos 'remitente' o 'receptor' faltantes");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Obtener todos los mensajes: campos 'remitente' o 'receptor' faltantes");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return respuesta.toString();
            }

            // Llamar al método para obtener todos los mensajes
            String result = ChatFriendController.obtainAllMessages(remitenteUsername, receptorUsername);

            if (result.equals("-1")) {
                respuesta.put("status", "-5");
                respuesta.put("message", "Error al obtener todos los mensajes");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Error al obtener todos los mensajes entre " + remitenteUsername + " y " + receptorUsername);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                respuesta.put("status", "0");
                respuesta.put("message", result);
                try {
                    LogController.insertLogStatic(DescripcionAccion.CONSULTAR_MENSAJES, remitenteUsername, receptorUsername);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            return respuesta.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            respuesta.put("status", "-6");
            respuesta.put("message", "Error interno al obtener todos los mensajes");
            try {
                LogController.insertLogStatic(DescripcionAccion.ERROR, "Error interno al obtener todos los mensajes");
            } catch (SQLException exSql) {
                exSql.printStackTrace();
            }
            return respuesta.toString();
        }
    }

//Método para mandar mensajes a otros amigos
    private String handleSendMessageFriend(Socket clientSocket, JSONObject jsonObject) {
        JSONObject respuesta = new JSONObject();
        String receptorUsername = IdentifyUserName(clientSocket);
        try {
            if (receptorUsername == null) {
                respuesta.put("status", "-4");
                respuesta.put("message", "Receptor no identificado");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Intentar aceptar una solicitud de amistad: receptor no identificado");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return respuesta.toString();
            }

            // Deserializar el JSON para obtener el nombre del remitente (quien envió la solicitud)
            String remitenteUsername = jsonObject.optString("receptor");

            String contenido = jsonObject.optString("contenido");

            String result = ChatFriendController.SendMessage(remitenteUsername, receptorUsername, contenido);

            if (result.equals("-1")) {
                respuesta.put("status", "-5");
                respuesta.put("message", "Error al enviar el mensaje");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Error al enviar el mensaje de " + remitenteUsername + " a " + receptorUsername);
                } catch (SQLException ex) {

                }
            } else {
                respuesta.put("status", "0");
                respuesta.put("message", "Mensaje enviado con éxito");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ENVIAR_MENSAJE, remitenteUsername, receptorUsername);
                } catch (SQLException ex) {

                }
            }

            return respuesta.toString();

        } catch (Exception ex) {

            respuesta.put("status", "-6");
            respuesta.put("message", "Error interno al enviar el mensaje");
            try {
                LogController.insertLogStatic(DescripcionAccion.ERROR, "Error interno al enviar el mensaje");
            } catch (SQLException exSql) {

            }
            return respuesta.toString();
        }
    }

//Metodo para eliminar un chat y la amistad
    private String handleDeleteAllMessagesAndFriendship(Socket clientSocket, JSONObject jsonObject) {
        JSONObject respuesta = new JSONObject();
        String receptorUsername = IdentifyUserName(clientSocket);
        try {
            if (receptorUsername == null) {
                respuesta.put("status", "-4");
                respuesta.put("message", "Receptor no identificado");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Intentar aceptar una solicitud de amistad: receptor no identificado");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return respuesta.toString();
            }

            // Deserializar el JSON para obtener el nombre del remitente (quien envió la solicitud)
            String remitenteUsername = jsonObject.optString("receptor");

            if (remitenteUsername == null) {
                respuesta.put("status", "-6");
                respuesta.put("message", "Receptor no identificado");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Intentar aceptar una solicitud de amistad: receptor no identificado");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            // Llamar al método para eliminar todos los mensajes y la amistad
            String result = ChatFriendController.DeleteAllMessagesAndFriendship(remitenteUsername, receptorUsername);

            if (result.equals("-1")) {
                respuesta.put("status", "-5");
                respuesta.put("message", "Error al eliminar todos los mensajes y la amistad");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ERROR, "Error al eliminar todos los mensajes y la amistad entre " + remitenteUsername + " y " + receptorUsername);
                } catch (SQLException ex) {

                }
            } else {
                respuesta.put("status", "0");
                respuesta.put("message", "Mensajes y amistad eliminados con éxito");
                try {
                    LogController.insertLogStatic(DescripcionAccion.ELIMINAR_AMISTAD, remitenteUsername, receptorUsername);
                } catch (SQLException ex) {

                }
            }

            return respuesta.toString();

        } catch (Exception ex) {

            respuesta.put("status", "-6");
            respuesta.put("message", "Error interno al eliminar todos los mensajes y la amistad");
            try {
                LogController.insertLogStatic(DescripcionAccion.ERROR, "Error interno al eliminar todos los mensajes y la amistad");
            } catch (SQLException exSql) {

            }
            return respuesta.toString();
        }
    }

//---------------------------------------------------------------------------------------------------------------------------------
    private String handleFindUsersConnected() {
        JSONArray connectedUsers = new JSONArray();
        GlobalClients.connectedClients.keySet().forEach(connectedUsers::put);  // Añade todos los nombres de usuario al JSONArray

        JSONObject responseJson = new JSONObject();
        responseJson.put("status", "0");
        responseJson.put("message", connectedUsers);

        return responseJson.toString();
    }

    private String handleFindUsersDisconnected() {
        JSONObject responseJson = new JSONObject();
        try {
            List<String> allUsers = UsersController.getAllUsers();  // Suponiendo que tienes un método que obtiene todos los usuarios
            JSONArray disconnectedUsers = new JSONArray();

            for (String user : allUsers) {
                if (!GlobalClients.connectedClients.containsKey(user)) {
                    disconnectedUsers.put(user);
                }
            }

            responseJson.put("status", "0");
            responseJson.put("message", disconnectedUsers);
        } catch (Exception e) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
            responseJson.put("status", "-1");
            responseJson.put("error", "Error al obtener usuarios desconectados");
        }

        return responseJson.toString();
    }
    
    
    private void handleLogout(String username) {
    // Cerrar el socket y remover el usuario de los clientes conectados
    removeClientBySocketLogOut(clientSocket);

    // Borrar los chats asociados al usuario
    ChatManager.cleanUpChats();
}
    
    private void removeClientBySocketLogOut(Socket socket) {
    GlobalClients.connectedClients.values().removeIf(val -> val.equals(socket));
    try {
        socket.close();
    } catch (IOException e) {
        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
    }
}




//Método que maneja cosas raras
    private String handleUnknownAction() {
        System.out.println("Received unknown action.");

        return "200";
    }

}
