/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.shared.models;

/**
 *
 * @author aguil
 */
import java.sql.Timestamp;

public class SolicitudGrupo {

    //PK
    private int solicitud_id; //NOTA: en la bae de datos es solicitud_id;

    //FK --> grupo
    private int id_grupo;

    //FK --> usuario
    private int id_receptor;

    //FK --> usuario
    private String remitente;
    
    private String nombre;

    //Propiedades
    private int estado_solicitud;

    private Timestamp fecha_creacion;

    //------------------------
    //Getters y setters
    public int getId_solicitud() {
        return solicitud_id;
    }

    public void setId_solicitud(int id_solicitud) {
        this.solicitud_id = id_solicitud;
    }

    public int getId_grupo() {
        return id_grupo;
    }

    public void setId_grupo(int id_grupo) {
        this.id_grupo = id_grupo;
    }

    public int getId_receptor() {
        return id_receptor;
    }

    public void setId_receptor(int id_receptor) {
        this.id_receptor = id_receptor;
    }

    public String getId_remitente() {
        return remitente;
    }

    public String getNombreGrupo() {
        return nombre;
    }

    public void setId_remitente(String id_remitente) {
        this.remitente = id_remitente;
    }
    
        public void setNombreGrupo(String nombre) {
        this.nombre = nombre;
    }

    public int getEstado_solicitud() {
        return estado_solicitud;
    }

    public void setEstado_solicitud(int estado_solicitud) {
        this.estado_solicitud = estado_solicitud;
    }

    public Timestamp getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Timestamp fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

}
