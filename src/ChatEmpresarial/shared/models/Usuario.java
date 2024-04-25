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
//@Entity
//@Table(name = "usuario")
public class Usuario {
  //  @Id
 //   @GeneratedValue(strategy = GenerationType.IDENTITY)
 //   @Column(name = "id_usuario")
    private Integer idUsuario;

  //  @Column(name = "nombre")
    private String nombre;

   // @Column(name = "contrasena")
    private String contrasena;

   // @Column(name = "pelicula_favorita")
    private String peliculaFavorita;

   // @Column(name = "comida_favorita")
    private String comidaFavorita;

   // @Column(name = "estado")
    private Byte estado;

   // @Column(name = "fecha_creacion")
    private Date fechaCreacion;
}
