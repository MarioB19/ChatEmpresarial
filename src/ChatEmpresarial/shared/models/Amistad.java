/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.shared.models;

import java.sql.Timestamp;

/**
 *
 * @author aguil
 */
public class Amistad {
    
    //Propiedades
    
    private int id_amistad;
    
    private int id_receptor;
    
    private int id_chat;
    
    private int estado_amistad; //Puede ser byte
    
    private Timestamp  fecha_cracion;

    public int getId_amistad() {
        return id_amistad;
    }

    public void setId_amistad(int id_amistad) {
        this.id_amistad = id_amistad;
    }

    public int getId_receptor() {
        return id_receptor;
    }

    public void setId_receptor(int id_receptor) {
        this.id_receptor = id_receptor;
    }

    public int getId_chat() {
        return id_chat;
    }

    public void setId_chat(int id_chat) {
        this.id_chat = id_chat;
    }

    public int getEstado_amistad() {
        return estado_amistad;
    }

    public void setEstado_amistad(int estado_amistad) {
        this.estado_amistad = estado_amistad;
    }

    public Timestamp getFecha_cracion() {
        return fecha_cracion;
    }

    public void setFecha_cracion(Timestamp fecha_cracion) {
        this.fecha_cracion = fecha_cracion;
    }
    
    
    
}
