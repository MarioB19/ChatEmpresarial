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
//@Table(name = "participantes")
public class Participante {
  //  @Id
  //  @GeneratedValue(strategy = GenerationType.IDENTITY)
  //  @Column(name = "id_participante")
    private Integer idParticipante;

   // @ManyToOne
  //  @JoinColumn(name = "id_chat", referencedColumnName = "id_chat")
    private Chat chat;

   // @ManyToOne
   // @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

    // ... otros campos y getters y setters
}