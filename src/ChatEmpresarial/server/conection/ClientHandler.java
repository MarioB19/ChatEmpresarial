package ChatEmpresarial.server.conection;

import ChatEmpresarial.server.controllers.CreateGroupController;
import ChatEmpresarial.server.controllers.ChatFriendController;
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
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream()); ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
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
                String response;

                switch (requestType) {
                    case REGISTER:
                        response = handleRegister(jsonObject);

                        break;
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

                    case REQUEST_CHAT_FRIEND:
                        response = handleChatFriend(jsonObject);

                        break;
                    case SEND_MESSAGE_FRIEND:
                        response = handleSendMessageFriend(jsonObject);
                        break;

                    case DELETE_CHAT_FRIEND:
                        response = handleDeleteChatFriend(jsonObject);
                        break;

                    case FIND_FRIENDS:
                        response = handleFindFriends(clientSocket, jsonObject);

                        break;

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
        System.out.println("Mandando a agregar a grupo: " + jsonObject.toString());
        System.out.println("Agregando usuario a grupo.....");
        String idGrupo = jsonObject.getString("idGrupo");
        String idReceptor = jsonObject.getString("idReceptor");
        String Remitente = jsonObject.getString("Remitente");
        GroupChatController.agregarParticipante(idGrupo, idReceptor, Remitente);
        System.out.println("Usuario agregado correctamente");
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
            String idgrupo = jsonObject.getString("idGrupo");

            // Llama al método que obtiene los mensajes
            String mensajesJson = GroupChatController.obtenerUsuariosFueraDelGrupo(idchat, idgrupo);

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

    //Método que maneja la creación de un chat de amigos
    private String handleChatFriend(JSONObject jsonObject) {
        try {
            // Extrae los nombres del remitente y receptor desde el JSON
            String remitente = jsonObject.getString("remitente");
            String receptor = jsonObject.getString("receptor");

            // Llama al método que obtiene los mensajes
            String mensajesJson = ChatFriendController.obtainAllMessages(remitente, receptor);

            // Devuelve la respuesta JSON completa que incluye todos los mensajes
            return mensajesJson;

        } catch (Exception e) {
            // Maneja cualquier excepción y retorna un error estándar
            e.printStackTrace();
            return "-1";
        }

    }

    //Método para guardar un mensaje
    private String handleSendMessageFriend(JSONObject jsonObject) {
        try {
            // Extrae el remitente, receptor y contenido del mensaje desde el JSON
            String remitente = jsonObject.getString("remitente");
            String receptor = jsonObject.getString("receptor");
            String contenido = jsonObject.getString("contenido");

            // Llama al método que envía el mensaje y obtiene el resultado como un número
            String resultado = ChatFriendController.SendMessage(remitente, receptor, contenido);

            // Devuelve el número tal como lo proporciona el controlador
            return resultado;

        } catch (Exception e) {
            // Maneja cualquier excepción y retorna un error estándar
            e.printStackTrace();
            return "-1";
        }
    }

    //Método para eliminar los mensajes así como el chat y la amistad
    private String handleDeleteChatFriend(JSONObject jsonObject) {
        try {
            // Extrae los nombres del remitente y receptor desde el JSON
            String remitente = jsonObject.getString("remitente");
            String receptor = jsonObject.getString("receptor");

            // Llama al método que elimina el chat y obtiene el resultado como un número
            String resultado = ChatFriendController.DeleteAllMessagesAndFriendship(remitente, receptor);

            // Devuelve el número tal como lo proporciona el controlador
            return resultado;

        } catch (Exception e) {
            // Maneja cualquier excepción y retorna un error estándar
            e.printStackTrace();
            return "-1";
        }

    }

    //Método para encontrar a todos los amigos 
    private String handleFindFriends(Socket clientSocket, JSONObject jsonObject) {
        JSONObject respuesta = new JSONObject();
        try {
            // Obtener el nombre de usuario del remitente usando el socket
            // Preparar la respuesta JSON

            String remitenteUsername = IdentifyUserName(clientSocket);

            if (remitenteUsername == null) {
                respuesta.put("status", "-1");
                return respuesta.toString();
            }

            // Llamar al método en `ChatFriendController` para obtener la lista de amigos
            String friendsList = ChatFriendController.findFriends(remitenteUsername);

            respuesta.put("status", "0");
            respuesta.put("message", friendsList);

            return respuesta.toString();

        } catch (Exception e) {
            e.printStackTrace();
            respuesta.put("status", "-2");
            return respuesta.toString();
        }
    }

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

//Método que maneja cosas raras
    private String handleUnknownAction() {
        System.out.println("Received unknown action.");

        return "200";
    }

}
