/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.shared.models;

import java.util.Date;

/**
 *
 * @author aguil
 */

import java.sql.Timestamp;

public class Usuario {

    
    //PK
    private int id_usuario;

    //Propiedades
    private String nombre;

   
    private String contrasena;

  
    private String pelicula_favorita;

    
    private String comida_favorita;

    
    private int estado;

   
    private Timestamp fecha_creacion;
    
    
    //-------------------
    //getters y setters

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getPelicula_favorita() {
        return pelicula_favorita;
    }

    public void setPelicula_favorita(String pelicula_favorita) {
        this.pelicula_favorita = pelicula_favorita;
    }

    public String getComida_favorita() {
        return comida_favorita;
    }

    public void setComida_favorita(String comida_favorita) {
        this.comida_favorita = comida_favorita;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Timestamp getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Timestamp fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }
    
    
}
