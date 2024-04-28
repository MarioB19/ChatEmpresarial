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

public class Log {
    
    
    //PK
    private int id_log;
    
    //Propiedades
    private String descripcion;
    
    private Timestamp fecha_creacion;
    
    //Constructores

    public Log(String descripcion, Timestamp fecha_creacion) {
        this.descripcion = descripcion;
        this.fecha_creacion = fecha_creacion;
    }

    public Log(String descripcion) {
        this.descripcion = descripcion;
      
    }
    
    
    public Log()
    {
        this.descripcion = " ";
        
    }
    
    
    //getters y setters

    public int getId_log() {
        return id_log;
    }

    public void setId_log(int id_log) {
        this.id_log = id_log;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Timestamp getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Timestamp fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }
    
    
    
    
    
}
