/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.shared.models;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 *
 * @author aguil
 */

public class Chat{
   
    
    //Llave
    private int id_chat;
    
    
    //Propiedades

    private Timestamp fecha_creacion;

   
    private int tipo_chat;

  //getters y setters

    public int getId_chat() {
        return id_chat;
    }

    public void setId_chat(int id_chat) {
        this.id_chat = id_chat;
    }

    public Timestamp getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Timestamp fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public int getTipo_chat() {
        return tipo_chat;
    }

    public void setTipo_chat(int tipo_chat) {
        this.tipo_chat = tipo_chat;
    }
    
    
    
    
}
