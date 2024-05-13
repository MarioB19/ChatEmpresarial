/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.client.pages;

/**
 *
 * @author brunosanchezpadilla
 */
import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.models.SolicitudGrupo;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import org.json.JSONObject;

public class GroupRequest extends JDialog {

    private SolicitudGrupo solicitud;
    private String nombreUserActive;

    private JPanel userPanel;
    private JScrollPane scrollPane;
    private JButton btnAceptar;
    private JButton btnCancelar;
    private JButton btnDenegar;

    public GroupRequest(SolicitudGrupo solicitud, String nombreUserActive) {
        super((JFrame) null, "Aceptar solicitud del grupo: " + solicitud.getNombreGrupo(), true);
        this.solicitud = solicitud;
        this.nombreUserActive = nombreUserActive;
        initializeUI();
        setSize(400, 80); // Set the size of the dialog
        setLocationRelativeTo(null); // Center the dialog
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Close operation
    }

    private void initializeUI() {
        getContentPane().setLayout(new BorderLayout());

        userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(userPanel);
        scrollPane.setPreferredSize(new Dimension(200, 300));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Panel para los botones

        btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(this::actionAceptar);
        panelBotones.add(btnAceptar);

        btnDenegar = new JButton("Denegar");
        btnDenegar.addActionListener(this::actionDenegar);
        panelBotones.add(btnDenegar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(this::actionCancelar);
        panelBotones.add(btnCancelar);

        btnDenegar = new JButton("Denegar");
        btnDenegar.addActionListener(this::actionDenegar);
        panelBotones.add(btnDenegar);

        getContentPane().add(panelBotones, BorderLayout.SOUTH); // Agregar el panel de botones al sur
    }

    private void actionAceptar(ActionEvent e) {
        JSONObject json = new JSONObject();
        json.put("idSolicitud", Integer.toString(solicitud.getId_solicitud()));
        json.put("idGrupo", Integer.toString(solicitud.getId_grupo()));
        json.put("userId", Integer.toString(solicitud.getId_receptor()));
        json.put("action", "ACCEPT_REQUEST_GROUP");

        PersistentClient client = PersistentClient.getInstance();
        client.sendMessageAndWaitForResponse(json.toString());

        ChatList chatList = new ChatList(nombreUserActive);
        chatList.setVisible(true);
        dispose();
    }

    private void actionDenegar(ActionEvent e) {

        JSONObject json = new JSONObject();
        json.put("idSolicitud", Integer.toString(solicitud.getId_solicitud()));
        json.put("idGrupo", Integer.toString(solicitud.getId_grupo()));
        json.put("userId", Integer.toString(solicitud.getId_receptor()));
        json.put("action", "DENY_REQUEST_GROUP");

        PersistentClient client = PersistentClient.getInstance();
        client.sendMessageAndWaitForResponse(json.toString());
        ChatList chatList = new ChatList(nombreUserActive);
        chatList.setVisible(true);
        dispose();
    }

    private void actionCancelar(ActionEvent e) {
        ChatList chatList = new ChatList(nombreUserActive);
        chatList.setVisible(true);
        dispose();
    }
}
