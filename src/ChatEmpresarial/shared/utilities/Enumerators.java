/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.shared.utilities;

/**
 *
 * Clase: Enumerators Tipo: Catálogo Función: proporcionar estructuras
 * preestablecidas para diversas acciones dentro del sistema
 *
 * @author aguil
 */
public class Enumerators {

    public enum TipoRequest {
    REGISTER("register"),
    LOGOUT("logout"),
    LOGIN("login"),
    FORGOTPSW1("forgotpsw1"),
    FORGOTPSW2("forgotpsw2"),
    REQUEST_CHAT_FRIEND("chatfriend"),
    SEND_MESSAGE_FRIEND("sentMessageFriend"),
    GET_MESSAGE_FRIEND("getMessageFriends"),
    FIND_FRIENDS_CONNECTED("findFriends"),
    FIND_FRIENDS_DISCONNECTED("findFriendsDisconnected"),
    DELETE_CHAT_FRIEND("deletefriend"),
    FIND_USERS_DISCONNECTED("findUsersDisconnected"),
    FIND_USERS_CONNECTED("findUsersConnected"),
    CREATE_CHAT_USERS("createChatUsers"),
    SEND_MESSAGE_CHAT_USERS("sendMessageChatUsers"),
    GET_MESSAGES_CHAT_USERS("getMessagesChatUsers"),
    SENT_INVITATION_FRIEND("sentFriendInvitation"),
    REFUSE_INVITATION_FRIEND("refFriendInvitation"),
    GET_INVITATION_FRIEND("getFriendInvitation"),
    GET_SEND_INVITATION_FRIEND("getSendFriendInvitation"),
    ACCEPT_INVITATION_FRIEND("Accepted Invitation"),
    CREATEGROUP("creategroup"),
     GET_ALL_USERS_EXCEPT_SELF("getAllUsersExceptSelf"),
     GET_ALL_GROUPS("getallgroups"),
        GET_MESSAGES_GROUP("getdmessagesgroup"),
        SEND_MESSAGE_GROUP("sendmessagegroup"),
        FIND_USERS_CONNECTED_GROUP("findusersconnectedgroup"),
        FIND_USERS_DISCONNECTED_GROUP("findusersdisconnectedgroup"),
        DELETE_GROUP("deletegroup"),
        EXIT_GROUP("exitgroup"),
        GET_USERS_GROUP("getusersgroup"),
        GET_USERS_NOT_IN_GROUP("getusersnotingroup"),
        GET_ALL_GROUPS_REQUESTS("getgrouprequests"),
        ADD_USER_TO_GROUP("addusertogroup"),
        ACCEPT_REQUEST_GROUP("acceptrequestgroup"), 
        DENY_REQUEST_GROUP("denyrequestgroup");

        private final String actionString;

        TipoRequest(String actionString) {
            this.actionString = actionString;
        }

        @Override
        public String toString() {
            return this.actionString;
        }
    }

    //Enumerador para determinar el tipo de Log a registrar
    public enum TipoLog {
    SERVIDOR_INICIADO,
    CONEXION_ACEPTADA,
    CREAR_CUENTA,
    INICIAR_SESION,
    MODIFICAR_CONTRASENA,
    CERRAR_SESION,
    ENVIAR_MENSAJE,
    CREAR_GRUPO,
    ENVIAR_SOLICITUD_AMISTAD,
    ACEPTAR_SOLICITUD_AMISTAD,
    RECHAZAR_SOLICIUD_AMISTAD,
    ENVIAR_SOLICITUD_GRUPO,
    ELIMINAR_AMISTAD,
    SALIR_GRUPO,
    ELIMINAR_GRUPO,
    OBTENER_SOLICITUDES,
    ERROR;
    
    // Agrega más acciones según sea necesario
}

public enum DescripcionAccion {
    SERVIDOR_INICIADO("El servidor ha sido iniciado en el puerto %s"),
    CONEXION_ACEPTADA("El cliente con ip %s se ha conectado al server"),
    CREAR_CUENTA("El usuario %s ha creado una cuenta"),
    INICIAR_SESION("El usuario %s ha iniciado sesión"),
    MODIFICAR_CONTRASENA("El usuario %s ha modificado su contraseña"),
    CERRAR_SESION("El usuario %s ha cerrado sesión"),
    ENVIAR_MENSAJE("El usuario %s ha enviado un mensaje a %s"),
    CREAR_GRUPO("El usuario %s ha creado un grupo con nombre %s"),
    ENVIAR_SOLICITUD_AMISTAD("El usuario %s ha enviado una solicitud de amistad al usuario %s "),
    ACEPTAR_SOLICITUD_AMISTAD("El usuario %s ha aceptado la solicitud de amistad del usuario %s  "),
    RECHAZAR_SOLICIUD_AMISTAD("El usuario %s ha rechazado la solicitud de amistad de %s"),
    ENVIAR_SOLICITUD_GRUPO("El usuario %s ha invitado al grupo %s a lo(s) usuario(s) %s"),
    ELIMINAR_AMISTAD("El %s ha eliminado a %s de sus amigos "),
    SALIR_GRUPO("El %s ha abandonado el grupo %s"),
    ELIMINAR_GRUPO("El grupo %s ha sido eliminado"),
  //  OBTENER_SOLICITUDES("El usuario &s  ha recibido sus solicitudes de amistad :V"),
    CONSULTAR_MENSAJES("El usuario %s ha consultado los mensajes de %s"),
    ERROR("Ocurrio un error al: %s");

    
 
    //Necesario para acceder al formato del log
    private String descripcion;

    DescripcionAccion(String descripcion) {
        this.descripcion = descripcion;
    }

    

       //Necesario para acceder al formato del log
       

        public String getDescripcion() {
            return descripcion;
        }

        //Enumerador para determinar el tipo de chat
        public enum TipoChat {

        }

    }

}
