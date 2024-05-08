/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.shared.models;

import java.util.Date;
import java.sql.Timestamp;



/**
 *
 * @author aguil
 */

public class Grupo {
  
    //Llave
    private int id_grupo;
    
    private String nombre;
    
    private String nombreAdmin;

  //FK --> Usuario
    private int id_aministrador;

  //FK -> Chat
    private int id_chat;

  //Propiedades
    
    private Timestamp fechaCreacion;

    // Getters y setters...

    public int getId_grupo() {
        return id_grupo;
    }
    
    public void setId_grupo(int id_grupo) {
        this.id_grupo = id_grupo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
        
        
        
    }    public String getNombreAdmin() {
        return nombre;
    }
    
    public void setNombreAdmin(String nombre) {
        this.nombre = nombre;
    }


    public int getId_aministrador() {
        return id_aministrador;
    }

    public void setId_aministrador(int id_aministrador) {
        this.id_aministrador = id_aministrador;
    }

    public int getId_chat() {
        return id_chat;
    }

    public void setId_chat(int id_chat) {
        this.id_chat = id_chat;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    
    
}