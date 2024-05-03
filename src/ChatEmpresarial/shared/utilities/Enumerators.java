/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.shared.utilities;


/**
 * 
 * Clase: Enumerators
 Tipo: Catálogo
 Función: proporcionar estructuras preestablecidas para diversas acciones dentro del sistema
 * @author aguil
 */

public class Enumerators {
    
    public enum TipoRequest {
    REGISTER("register"),
    LOGIN("login");

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
    CREAR_GRUPO("El usuario % ha creado un grupo"),
    ENVIAR_SOLICITUD_AMISTAD("El usuario %s ha enviado una solicitud de amistad al usuario %s "),
    ACEPTAR_SOLICITUD_AMISTAD("El usuario %s ha aceptado la solicitud de amistad del usuario %s  "),
    RECHAZAR_SOLICIUD_AMISTAD("El usuario %s ha rechazado la solicitud de amistad de %s"),
    ENVIAR_SOLICITUD_GRUPO("El usuario %s ha invitado al grupo %s a los usuarios: %s"),
    ELIMINAR_AMISTAD("El %s ha eliminado a %s de sus amigos "),
    SALIR_GRUPO("El %s ha abandonado el grupo %s"),
    ELIMINAR_GRUPO("El grupo %s ha sido eliminado");
    
 
    //Necesario para acceder al formato del log
    private String descripcion;

    DescripcionAccion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
    
    
    
}
