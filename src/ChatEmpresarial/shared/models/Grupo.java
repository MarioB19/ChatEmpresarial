/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.shared.models;

import java.security.Timestamp;

/**
 *
 * @author aguil
 */
//@Entity
//@Table(name = "grupo")
public class Grupo {
  //  @Id
  //  @GeneratedValue(strategy = GenerationType.IDENTITY)
  //  @Column(name = "id_grupo")
    private Integer idGrupo;

   // @Column(name = "id_administrador")
    private Integer idAdministrador;

  //  @Column(name = "id_chat")
    private Integer idChat;

  //  @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;

    // Getters y setters...
}