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
//@Table(name = "solicitud_amistad")
public class SolicitudAmistad {
  //  @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name = "solicitud_id")
    private Integer solicitudId;

    //@ManyToOne
   // @JoinColumn(name = "id_receptor", referencedColumnName = "id_usuario")
    private Usuario receptor;

    //@ManyToOne
    //@JoinColumn(name = "id_remitente", referencedColumnName = "id_usuario")
    private Usuario remitente;

    // ... otros campos y getters y setters
}
