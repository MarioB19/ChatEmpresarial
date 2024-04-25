/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.shared.models;

/**
 *
 * @author aguil
 */

//@Entity
//@Table(name = "mensaje")
public class Mensaje {
  //  @Id
  //  @GeneratedValue(strategy = GenerationType.IDENTITY)
  //  @Column(name = "id_mensaje")
    private Integer idMensaje;

   // @ManyToOne
  //  @JoinColumn(name = "id_chat", referencedColumnName = "id_chat")
    private Chat chat;

    // ... otros campos y getters y setters
}
