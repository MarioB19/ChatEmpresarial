package ChatEmpresarial.server.conection;

import ChatEmpresarial.server.controllers.CreateGroupController;
import ChatEmpresarial.server.controllers.ChatFriendController;

import ChatEmpresarial.server.controllers.FriendInvitationController;
import ChatEmpresarial.server.controllers.LogController;

import ChatEmpresarial.server.controllers.GroupChatController;

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
import static ChatEmpresarial.shared.utilities.Enumerators.TipoRequest.ADD_USER_TO_GROUP;
import static ChatEmpresarial.shared.utilities.Enumerators.TipoRequest.DELETE_GROUP;
import static ChatEmpresarial.shared.utilities.Enumerators.TipoRequest.EXIT_GROUP;
import static ChatEmpresarial.shared.utilities.Enumerators.TipoRequest.FIND_USERS_CONNECTED;
import static ChatEmpresarial.shared.utilities.Enumerators.TipoRequest.FIND_USERS_DISCONNECTED;
import static ChatEmpresarial.shared.utilities.Enumerators.TipoRequest.GET_ALL_GROUPS;
import static ChatEmpresarial.shared.utilities.Enumerators.TipoRequest.GET_ALL_GROUPS_REQUESTS;
import static ChatEmpresarial.shared.utilities.Enumerators.TipoRequest.GET_ALL_USERS_EXCEPT_SELF;
import static ChatEmpresarial.shared.utilities.Enumerators.TipoRequest.GET_MESSAGES_GROUP;
import static ChatEmpresarial.shared.utilities.Enumerators.TipoRequest.GET_USERS_GROUP;
import static ChatEmpresarial.shared.utilities.Enumerators.TipoRequest.GET_USERS_NOT_IN_GROUP;
import java.sql.SQLException;
import java.util.Arrays;
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

                        
                        //Obtener las invitaciones enviadas
                        
                    case GET_SEND_INVITATION_FRIEND:
                        
                        response = handleGetSentInvitations(clientSocket, jsonObject);
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


                    case CREATEGROUP:
                        response = handleCreateGroup(jsonObject);
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

                    case GET_ALL_USERS_EXCEPT_SELF:
                        response = handleGetAllUsersExceptSelf(jsonObject);
                        break;

                    case GET_ALL_GROUPS:
                        response = handleGetAllGroups(jsonObject);
                        break;
                    case GET_MESSAGES_GROUP:
                        response = handleFetchMessagesGroup(jsonObject);
                        break;
                    case SEND_MESSAGE_GROUP:
                        response = handleSendMessageGroup(jsonObject);
                        break;

                    case FIND_USERS_CONNECTED_GROUP:
                        response = handleFindUsersConnectedGroup(jsonObject);
                        break;

                    case FIND_USERS_DISCONNECTED_GROUP:
                        response = handleFindUsersDisconnectedGroup(jsonObject);
                        break;
                    case DELETE_GROUP:
                        response = handleDeleteGroup(jsonObject);
                        break;
                    case EXIT_GROUP:
                        response = handleExitGroup(jsonObject);
                        break;
                    case GET_USERS_GROUP:
                        response = handlegetMembersGroup(jsonObject);
                        break;
                    case GET_USERS_NOT_IN_GROUP:
                        response = handlegetMembersNotInGroup(jsonObject);
                        break;
                    case GET_ALL_GROUPS_REQUESTS:
                        response = handleGetAllGroupsRequests(jsonObject);
                        break;
                    case ADD_USER_TO_GROUP:
                        response = handleAddUserToGroup(jsonObject);
                        break;
                    case ACCEPT_REQUEST_GROUP:
                        response = handleAcceptRequestGroup(jsonObject);
                        break;
                    case DENY_REQUEST_GROUP:
                        response = handleDenyRequestGroup(jsonObject);
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

    public String handleCreateGroup(JSONObject data) throws SQLException {
        System.out.println("Handling create group with data: " + data.toString());

        // Extracción de datos del JSON
        String groupname = data.optString("groupname");
        String adminName = data.optString("adminId");
        JSONArray participantIdsJson = data.optJSONArray("participantIds");

        if (participantIdsJson == null || participantIdsJson.length() == 0) {
            System.out.println("Error: No participants provided.");
            return "-1";  // Retorna error si no hay participantes.
        }

        // Convertir JSONArray a array de int
        int[] participantIds = new int[participantIdsJson.length()];
        for (int i = 0; i < participantIdsJson.length(); i++) {
            participantIds[i] = participantIdsJson.optInt(i);
        }

        // Log de los IDs de los participantes
        System.out.println("Participant IDs: " + Arrays.toString(participantIds));

        // Llamada al controlador con el nombre del admin y los IDs de los participantes
        String response = CreateGroupController.createGroupInDatabase(adminName, groupname, participantIds);
        System.out.println("Response from createGroupInDatabase: " + response);
        return response;
    }

    public String handleGetAllUsersExceptSelf(JSONObject data) throws SQLException {
        System.out.println("Handling get all users except self with data: " + data.toString());

        String activeuser = data.optString("activeuser");

        JSONObject responseJson = new JSONObject();
        try {
            List<JSONObject> allUsers = UsersController.getAllUsersExceptSelf(activeuser);
            JSONArray result = new JSONArray(allUsers);

            responseJson.put("status", "0");
            responseJson.put("users", result);
        } catch (Exception e) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
            responseJson.put("status", "-1");
            responseJson.put("error", "Error al obtener usuarios");
        }

        System.out.println("Response sent while fetch users except self" + responseJson.toString());
        return responseJson.toString();
    }

    public String handleGetAllGroups(JSONObject data) throws SQLException {
        System.out.println("Handling get all groups with data: " + data.toString());

        String activeuser = data.optString("activeuser");

        JSONObject responseJson = new JSONObject();
        try {
            List<JSONObject> allUsers = CreateGroupController.getAllGroups(activeuser);
            JSONArray result = new JSONArray(allUsers);

            responseJson.put("status", "0");
            responseJson.put("groups", result);
        } catch (Exception e) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
            responseJson.put("status", "-1");
            responseJson.put("error", "Error al obtener grupos");
        }

        System.out.println("Response sent while fetch groups " + responseJson.toString());
        return responseJson.toString();
    }

    //Método que maneja la obtencion de un chat de grupos
    private String handleFetchMessagesGroup(JSONObject jsonObject) {
        System.out.println("Request recibida para obtener mensajes de grupo: " + jsonObject.toString());
        try {
            // Extrae los nombres del remitente y receptor desde el JSON
            String idchat = jsonObject.getString("idChat");

            // Llama al método que obtiene los mensajes
            String mensajesJson = GroupChatController.obtainAllMessages(Integer.parseInt(idchat));

            // Devuelve la respuesta JSON completa que incluye todos los mensajes
            System.out.println("Mensajes del grupo: " + mensajesJson);
            return mensajesJson;

        } catch (Exception e) {
            // Maneja cualquier excepción y retorna un error estándar
            e.printStackTrace();
            return "-1";
        }
    }

    //Método para guardar un mensaje de grupo
    private String handleSendMessageGroup(JSONObject jsonObject) {
        try {
            // Extrae el remitente, receptor y contenido del mensaje desde el JSON
            String idChat = jsonObject.getString("idChat");
            String contenido = jsonObject.getString("contenido");

            // Llama al método que envía el mensaje y obtiene el resultado como un número
            String resultado = GroupChatController.SendMessage(Integer.parseInt(idChat), contenido);

            // Devuelve el número tal como lo proporciona el controlador
            return resultado;

        } catch (Exception e) {
            // Maneja cualquier excepción y retorna un error estándar
            e.printStackTrace();
            return "-1";
        }
    }

    private String handleFindUsersConnectedGroup(JSONObject jsonObject) {
        JSONArray connectedUsers = new JSONArray();
        GlobalClients.connectedClients.keySet().forEach(connectedUsers::put);
        String idchat = jsonObject.getString("idChat");
        System.out.println("Mandando a usuarios de grupo conectados: " + connectedUsers.toString());
        String resultado = GroupChatController.getConnected(idchat, connectedUsers);

        JSONArray connectedUserNames = new JSONArray(resultado);

        JSONObject responseJson = new JSONObject();
        responseJson.put("status", "0");
        responseJson.put("message", connectedUserNames);

        System.out.println("Enviando del server al cliente usuarios conectados: " + responseJson.toString());
        return responseJson.toString();
    }

    private String handleFindUsersDisconnectedGroup(JSONObject jsonObject) {
        JSONObject responseJson = new JSONObject();
        String idchat = jsonObject.getString("idChat");
        try {
            List<String> allUsers = UsersController.getAllUsers();
            JSONArray disconnectedUsers = new JSONArray();

            for (String user : allUsers) {
                if (!GlobalClients.connectedClients.containsKey(user)) {
                    disconnectedUsers.put(user);
                }
            }

            String resultado = GroupChatController.getConnected(idchat, disconnectedUsers);

            JSONArray connectedUserNames = new JSONArray(resultado);
            responseJson.put("status", "0");
            responseJson.put("message", connectedUserNames);

            System.out.println("Enviando del server al cliente usuarios conectados: " + responseJson.toString());
            return responseJson.toString();

        } catch (Exception e) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
            responseJson.put("status", "-1");
            responseJson.put("error", "Error al obtener usuarios desconectados");
        }
        return responseJson.toString();

    }

    private String handleDeleteGroup(JSONObject jsonObject) {
        JSONArray connectedUsers = new JSONArray();
        GlobalClients.connectedClients.keySet().forEach(connectedUsers::put);
        String idChat = jsonObject.getString("idChat");
        String idGrupo = jsonObject.getString("idGrupo");
        System.out.println("Mandando a eliminar grupo conectados: " + jsonObject.toString());
        GroupChatController.deleteGroup(idChat, idGrupo);
        return "0";
    }

    private String handleExitGroup(JSONObject jsonObject) {
        JSONArray connectedUsers = new JSONArray();
        GlobalClients.connectedClients.keySet().forEach(connectedUsers::put);
        String idChat = jsonObject.getString("idChat");
        String idGrupo = jsonObject.getString("idGrupo");
        String nombre = jsonObject.getString("nombre");
        System.out.println("Mandando a eliminar grupo conectados: " + jsonObject.toString());
        GroupChatController.eliminarParticipante(idChat, nombre, idGrupo);
        return "0";
    }

    private String handleAddUserToGroup(JSONObject jsonObject) {
        JSONArray connectedUsers = new JSONArray();
        GlobalClients.connectedClients.keySet().forEach(connectedUsers::put);
        String idChat = jsonObject.getString("idChat");
        String idGrupo = jsonObject.getString("idGrupo");
        String nombre = jsonObject.getString("nombre");
        System.out.println("Mandando a eliminar grupo conectados: " + jsonObject.toString());
        GroupChatController.eliminarParticipante(idChat, nombre, idGrupo);
        return "0";
    }

    private String handleAcceptRequestGroup(JSONObject jsonObject) {
        JSONArray connectedUsers = new JSONArray();
        GlobalClients.connectedClients.keySet().forEach(connectedUsers::put);
        String idSolicitud = jsonObject.getString("idSolicitud");
        String idGrupo = jsonObject.getString("idGrupo");
        System.out.println("Mandando aceptar solicitud de grupo: " + jsonObject.toString());
        GroupChatController.acceptRequest(idSolicitud, idGrupo);
        return "0";
    }

    private String handleDenyRequestGroup(JSONObject jsonObject) {
        JSONArray connectedUsers = new JSONArray();
        GlobalClients.connectedClients.keySet().forEach(connectedUsers::put);
        String idSolicitud = jsonObject.getString("idSolicitud");
        String idGrupo = jsonObject.getString("idGrupo");
        System.out.println("Mandando rechazar solicitud de grupo: " + jsonObject.toString());
        GroupChatController.deleteRequestById(idSolicitud, idGrupo);
        return "0";
    }

    //Método que maneja la obtencion de un chat de grupos
    private String handlegetMembersGroup(JSONObject jsonObject) {
        System.out.println("Request recibida para obtener miembros de grupo: " + jsonObject.toString());
        try {
            // Extrae los nombres del remitente y receptor desde el JSON
            String idchat = jsonObject.getString("idChat");
            String nombre = jsonObject.getString("nombre");

            // Llama al método que obtiene los mensajes
            String mensajesJson = GroupChatController.obtenerUsuariosGrupoExceptoUsuario(idchat, nombre);

            // Devuelve la respuesta JSON completa que incluye todos los mensajes
            System.out.println("Miembros del grupo: " + mensajesJson);
            return mensajesJson;

        } catch (Exception e) {
            // Maneja cualquier excepción y retorna un error estándar
            e.printStackTrace();
            return "-1";
        }
    }

    //Método que maneja la obtencion de un chat de grupos
    private String handlegetMembersNotInGroup(JSONObject jsonObject) {
        System.out.println("Request recibida para obtener miembros de grupo: " + jsonObject.toString());
        try {
            // Extrae los nombres del remitente y receptor desde el JSON
            String idchat = jsonObject.getString("idChat");

            // Llama al método que obtiene los mensajes
            String mensajesJson = GroupChatController.obtenerUsuariosFueraDelGrupo(idchat);

            // Devuelve la respuesta JSON completa que incluye todos los mensajes
            System.out.println("Miembros del grupo: " + mensajesJson);
            return mensajesJson;

        } catch (Exception e) {
            // Maneja cualquier excepción y retorna un error estándar
            e.printStackTrace();
            return "-1";
        }
    }

    public String handleGetAllGroupsRequests(JSONObject data) throws SQLException {
        System.out.println("Handling get all groups REQUESTS with data: " + data.toString());

        String activeuser = data.optString("activeuser");

        JSONObject responseJson = new JSONObject();
        try {
            List<JSONObject> allUsers = CreateGroupController.getAllRequests(activeuser);
            JSONArray result = new JSONArray(allUsers);

            responseJson.put("status", "0");
            responseJson.put("request", result);
        } catch (Exception e) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
            responseJson.put("status", "-1");
            responseJson.put("error", "Error al obtener las solicitudes de los grupos");
        }

        System.out.println("Response sent while fetch groups requests " + responseJson.toString());
        return responseJson.toString();
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
                        LogController.insertLogStatic(DescripcionAccion.RECHAZAR_SOLICIUD_AMISTAD, remitenteUsername, receptorUsername);
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
               
            } else {
                // Si no es un error, devolver la lista de solicitudes recibidas
                JSONArray receivedInvitationsArray = new JSONArray(receivedInvitationsJson);
                respuesta.put("status", "0");
                respuesta.put("message", receivedInvitationsArray);
           
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

//Método para aceptar soliciudes
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

  
    //Método para recibir las solicitudes que se mandaron
    
    private String handleGetSentInvitations(Socket clientSocket, JSONObject jsonObject) {
    JSONObject respuesta = new JSONObject();
    try {
        // Identificar el nombre del remitente usando el socket
        String remitenteUsername = IdentifyUserName(clientSocket);

        if (remitenteUsername == null) {
            respuesta.put("status", "-4");
            respuesta.put("message", "Remitente no identificado");
            try {
                LogController.insertLogStatic(DescripcionAccion.ERROR, "Intentar obtener solicitudes enviadas: remitente no identificado");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return respuesta.toString();
        }

        // Llamar al método que obtiene las solicitudes de amistad enviadas
        String sentInvitationsJson = FriendInvitationController.GetSentInvitations(remitenteUsername);

        // Verificar si el resultado es un código de error
        if (sentInvitationsJson.equals("-2")) {
            respuesta.put("status", "-5");
            respuesta.put("message", "Remitente no encontrado");
            try {
                LogController.insertLogStatic(DescripcionAccion.ERROR, "Intentar obtener solicitudes enviadas: remitente " + remitenteUsername + " no encontrado");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (sentInvitationsJson.equals("-1")) {
            respuesta.put("status", "-6");
            respuesta.put("message", "Error al obtener solicitudes enviadas");
        } else {
            // Si no es un error, devolver la lista de solicitudes enviadas
            JSONArray sentInvitationsArray = new JSONArray(sentInvitationsJson);
            respuesta.put("status", "0");
            respuesta.put("message", sentInvitationsArray);
        }

        return respuesta.toString();

    } catch (Exception ex) {
        ex.printStackTrace();
        respuesta.put("status", "-7");
        respuesta.put("message", "Error interno");
        try {
            LogController.insertLogStatic(DescripcionAccion.ERROR, "Error interno al obtener solicitudes enviadas");
        } catch (SQLException exSql) {
            exSql.printStackTrace();
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
