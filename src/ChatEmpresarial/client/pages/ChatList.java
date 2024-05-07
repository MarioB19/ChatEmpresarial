package ChatEmpresarial.client.pages;

import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.models.Grupo;
import ChatEmpresarial.shared.models.Usuario;
import ChatEmpresarial.client.pages.UsuarioFixCellRenderer;
import ChatEmpresarial.client.pages.GroupFixCellRenderer;
import ChatEmpresarial.shared.utilities.Functions;
import ChatEmpresarial.client.utilities.GetUsers;
import ChatEmpresarial.client.utilities.SessionManager;
import ChatEmpresarial.shared.utilities.Enumerators;
import ChatEmpresarial.shared.utilities.Enumerators.TipoRequest;
import ChatEmpresarial.shared.utilities.Functions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatList extends JFrame {
    
       private DefaultListModel<Usuario> modeloUsuariosConectados = new DefaultListModel<>();
    private DefaultListModel<Usuario> modeloUsuariosDesconectados = new DefaultListModel<>();
    private JList<Usuario> listaUsuariosConectados = new JList<>(modeloUsuariosConectados);
    private JList<Usuario> listaUsuariosDesconectados = new JList<>(modeloUsuariosDesconectados);
    
    
    private String nombreUserActive;

    
    private ArrayList<Usuario> usuariosConectados = new ArrayList<>();
    private ArrayList<Usuario> usuariosDesconectados = new ArrayList<>();
    private ArrayList<Usuario> amigosConectados = new ArrayList<>();
    private ArrayList<Usuario> amigosDesconectados = new ArrayList<>();
    private ArrayList<Usuario> solicitudesAmigosEnviadas = new ArrayList<>();
    private ArrayList<Usuario> solicitudesAmigosRecibidas = new ArrayList<>();
    private ArrayList<Grupo> grupos = new ArrayList<>();
    private ArrayList<Grupo> solicitudesGrupos = new ArrayList<>();

    private final Color colorFondoPrincipal = new Color(225, 245, 254);
    private final Color colorFondoSecundario = new Color(144, 202, 249);
    private final Color colorBarraNavegacion = new Color(25, 118, 210);
    private final Color colorBoton = new Color(2, 136, 209);
    private final Color colorTexto = Color.WHITE;
    private final Color colorBotonSolicitud = new Color(76, 175, 80);  // Verde para el botón de solicitud
    private final Color colorBotonEliminar = new Color(244, 67, 54);   // Rojo para el botón de eliminar

    // Componentes de navegación
    private JPanel panelPrincipal;
    private CardLayout cardLayout;

    public ChatList(String username) {
     nombreUserActive = username;
    inicializarDatosDePrueba();  // Llamada inicial para cargar datos antes de que el Timer comience
    configurarVentana();
    configurarNavegacion();
     configurarTimer();

    setVisible(true);
}

private void configurarTimer() {
    int delay = 1000; // Retraso en milisegundos (1000 ms = 1 segundo)
    ActionListener taskPerformer = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            inicializarDatosDePrueba();  // Llama a tu método que actualiza los datos
            actualizarListasUsuarios();

        }
    };
    new Timer(delay, taskPerformer).start();
}



    private void inicializarDatosDePrueba() {
        
          
    String username = nombreUserActive;

    usuariosConectados.clear();
    usuariosDesconectados.clear();

    // Simular obtener nombres de amigos del servidor (esto deberías adaptarlo)

    ArrayList<String> usuariosConectadosNombres = handleListUsersConectados();
    ArrayList<String> usuariosDesconectadosNombres = handleListUsersDesconectados();

    for (String nombre : usuariosConectadosNombres) {
        if (!nombre.equals(username)) { // Evitar añadir al usuario logueado
            usuariosConectados.add(new Usuario(nombre));
        }
    }

    for (String nombre : usuariosDesconectadosNombres) {
        usuariosDesconectados.add(new Usuario(nombre));
    }

    // Actualizar la UI después de obtener los datos
    actualizarListasUsuarios();
    
        
          // Obtener amigos de la respuesta del servidor
    ArrayList<String> amigosNombres = handleListFriends();
        


        // Convertir nombres a objetos `Usuario`
        for (String nombre : amigosNombres) {
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            amigosConectados.add(usuario);
        }
        
   
        for (int i = 1; i <= 3; i++) {
            Usuario usuario = new Usuario();
            usuario.setNombre("User" + i);
   
      
            
            
          //  amigosConectados.add(usuario);
            amigosDesconectados.add(usuario);
            solicitudesAmigosEnviadas.add(usuario);
            solicitudesAmigosRecibidas.add(usuario);

            Grupo grupo = new Grupo();
            grupo.setId_grupo(i);
            grupos.add(grupo);
            solicitudesGrupos.add(grupo);
        }
    }
    
    private void actualizarListasUsuarios() {
    SwingUtilities.invokeLater(() -> {
        modeloUsuariosConectados.clear();
        usuariosConectados.forEach(modeloUsuariosConectados::addElement);
        
        modeloUsuariosDesconectados.clear();
        usuariosDesconectados.forEach(modeloUsuariosDesconectados::addElement);
    });
}


    private void configurarVentana() {
        setTitle("Chats Empresariales");
        setSize(360, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(colorFondoPrincipal);
    }

    private void configurarNavegacion() {
        JPanel panelNavegacion = new JPanel(new GridLayout(1, 3));
        panelNavegacion.setBackground(colorBarraNavegacion);

        JButton btnUsuarios = new JButton("Usuarios");
        JButton btnAmigos = new JButton("Amigos");
        JButton btnGrupos = new JButton("Grupos");
        JButton btnCrearGrupo = new JButton("PanelCrearGrupo");

        // Panel principal con CardLayout
        panelPrincipal = new JPanel();
        cardLayout = new CardLayout();
        panelPrincipal.setLayout(cardLayout);
        panelPrincipal.add(crearPanelUsuarios(), "Usuarios");
        panelPrincipal.add(crearPanelAmigos(), "Amigos");
        panelPrincipal.add(crearPanelGrupos(), "Grupos");
        panelPrincipal.add(crearPanelCrearGrupo(), "PanelCrearGrupo");

        btnUsuarios.addActionListener(e -> cardLayout.show(panelPrincipal, "Usuarios"));
        btnAmigos.addActionListener(e -> cardLayout.show(panelPrincipal, "Amigos"));
        btnGrupos.addActionListener(e -> cardLayout.show(panelPrincipal, "Grupos"));
        btnCrearGrupo.addActionListener(e -> cardLayout.show(panelPrincipal, "PanelCrearGrupo"));

        estiloBoton(btnUsuarios);
        estiloBoton(btnAmigos);
        estiloBoton(btnGrupos);

        panelNavegacion.add(btnUsuarios);
        panelNavegacion.add(btnAmigos);
        panelNavegacion.add(btnGrupos);

        getContentPane().add(panelNavegacion, BorderLayout.SOUTH);
        getContentPane().add(panelPrincipal, BorderLayout.CENTER);
    }

    private void estiloBoton(JButton boton) {
        boton.setBackground(colorBoton);
        boton.setForeground(colorTexto);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
    }

   
    private JPanel crearPanelUsuarios() {
    JPanel panel = new JPanel(new GridLayout(2, 1));
    panel.add(crearListaUsuarios("Usuarios Conectados", listaUsuariosConectados, modeloUsuariosConectados));
    panel.add(crearListaUsuarios("Usuarios Desconectados", listaUsuariosDesconectados, modeloUsuariosDesconectados));
    return panel;
}


    private JScrollPane crearListaUsuarios(String titulo, JList<Usuario> lista, DefaultListModel<Usuario> modelo) {
    lista.setModel(modelo);
    lista.setCellRenderer(new UsuarioCellRenderer());
    lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    lista.setLayoutOrientation(JList.VERTICAL);

    if (titulo.equals("Usuarios Conectados")) {  // Aplica solo a la lista de usuarios conectados
        lista.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList) evt.getSource();
                if (evt.getClickCount() == 2) {  // Doble clic
                    int index = list.locationToIndex(evt.getPoint());
                    if (index >= 0) {
                        Usuario usuario = (Usuario) list.getModel().getElementAt(index);
                        
                              JSONObject json = new JSONObject();
                        json.put("user1", usuario.getNombre());
                        json.put("user2", nombreUserActive);
                        json.put("action", TipoRequest.CREATE_CHAT_USERS);

                    PersistentClient client = PersistentClient.getInstance();
                    String serverResponse = client.sendMessageAndWaitForResponse(json.toString());
                       System.out.println("Server response" + serverResponse);

                        
                        new ChatUserPage(usuario.getNombre(), nombreUserActive);  // Abrir ventana de chat
                    }
                }
            }
        });
    }

    JScrollPane scrollPane = new JScrollPane(lista);
    scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
    return scrollPane;
}

    



    private JPanel crearPanelAmigos() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(crearListaAmigos("Amigos Conectados", amigosConectados, true));
        panel.add(crearListaAmigos("Amigos Desconectados", amigosDesconectados, false));
        panel.add(crearListaSolicitudesAmigos("Solicitudes Enviadas", solicitudesAmigosEnviadas ,true));
        panel.add(crearListaSolicitudesAmigos("Solicitudes Recibidas", solicitudesAmigosRecibidas ,false));
        return panel;
    }

    private JScrollPane crearListaAmigos(String titulo, ArrayList<Usuario> amigos, boolean estaConectado) {
        DefaultListModel<Usuario> modelo = new DefaultListModel<>();
        amigos.forEach(modelo::addElement);

        JList<Usuario> lista = new JList<>(modelo);
        lista.setCellRenderer(new AmigoCellRenderer());
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setLayoutOrientation(JList.VERTICAL);

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        return scrollPane;
    }
    
    private JScrollPane crearListaSolicitudesAmigos(String titulo, ArrayList<Usuario> elementos, boolean Envia) {
        DefaultListModel<Object> modelo = new DefaultListModel<>();
        elementos.forEach(modelo::addElement);

        JList<Object> lista = new JList<>(modelo);
        lista.setCellRenderer(new SolicitudesAmistadCellRenderer(Envia));
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setLayoutOrientation(JList.VERTICAL);

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        return scrollPane;
    }

    private JPanel crearPanelGrupos() {
        JPanel panel = new JPanel(new BorderLayout());

        // Botón para crear un nuevo grupo
        JButton btnCrearGrupo = new JButton("Crear Grupo");
        estiloBoton(btnCrearGrupo);
        btnCrearGrupo.addActionListener(e -> cardLayout.show(panelPrincipal, "PanelCrearGrupo"));

        // Panel para la lista de grupos y solicitudes
        JPanel panelListas = new JPanel(new GridLayout(2, 1));
        panelListas.add(crearListaGrupos("Grupos", grupos, false));
        panelListas.add(crearListaSolicitudes("Solicitudes de Grupos", solicitudesGrupos, false));

        panel.add(btnCrearGrupo, BorderLayout.NORTH);
        panel.add(panelListas, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelCrearGrupo() {
        JPanel panelCrearGrupo = new JPanel(new BorderLayout());

        // Panel for inputs and buttons
        JPanel inputPanel = new JPanel(new GridLayout(0, 1));
        JLabel lblNombreGrupo = new JLabel("Nombre:");
        JTextField txtNombreGrupo = new JTextField(10);
        JButton btnCrear = new JButton("Crear");
        JButton btnCancelar = new JButton("Cancelar");

        inputPanel.add(lblNombreGrupo);
        inputPanel.add(txtNombreGrupo);
        inputPanel.add(btnCrear);
        inputPanel.add(btnCancelar);

        
        // Scroll panel for users
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(userPanel);
        scrollPane.setPreferredSize(new Dimension(200, 120));

        // Suppose userList is your list of user objects
        ArrayList<Usuario> userList = GetUsers.getUsers();
        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();

        for (Usuario user : userList) {
            JCheckBox checkBox = new JCheckBox(user.getNombre());
            checkBox.setActionCommand(String.valueOf(user.getId_usuario())); // Store user ID in action command
            userPanel.add(checkBox);
            checkBoxes.add(checkBox);
        }

        panelCrearGrupo.add(inputPanel, BorderLayout.NORTH);
        panelCrearGrupo.add(scrollPane, BorderLayout.CENTER);


        // Acción para el botón crear grupo
        btnCrear.addActionListener(e -> {
        String groupname = txtNombreGrupo.getText();
        
        JSONObject json = new JSONObject();
        json.put("groupname", groupname);
        json.put("adminId", 1);
        json.put("action", Enumerators.TipoRequest.CREATEGROUP.toString());
        
        PersistentClient client = PersistentClient.getInstance();
        String serverResponse = client.sendMessageAndWaitForResponse(json.toString());
        
                // Mostrar la respuesta en un diálogo según el código
        switch (serverResponse) {
            case "0":  // Éxito
                JOptionPane.showMessageDialog(null, "Creation successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(panelPrincipal, "Grupos");
                break;
            case "1":  
                JOptionPane.showMessageDialog(null, "Could not create group.", "Creation Failed", JOptionPane.ERROR_MESSAGE);
                break;
            case "-1":  // Error desconocido
                JOptionPane.showMessageDialog(null, "An unknown error occurred during creation.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            default:  // Cualquier otra respuesta
                JOptionPane.showMessageDialog(null, "Unexpected server response: " + serverResponse, "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }

            System.out.println("Crear grupo: " + txtNombreGrupo.getText());
        });

        // Acción para el botón cancelar
        btnCancelar.addActionListener(e -> cardLayout.show(panelPrincipal, "Grupos"));

        return panelCrearGrupo;
    }

    private JScrollPane crearListaGrupos(String titulo, ArrayList<Grupo> grupos, boolean estaConectado) {
        DefaultListModel<Grupo> modelo = new DefaultListModel<>();
        grupos.forEach(modelo::addElement);

        JList<Grupo> lista = new JList<>(modelo);
        lista.setCellRenderer(new GrupoCellRenderer());
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setLayoutOrientation(JList.VERTICAL);

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        return scrollPane;
    }



    private JScrollPane crearListaSolicitudesGrupo(String titulo, ArrayList<?> elementos, boolean esUsuario) {
        DefaultListModel<Object> modelo = new DefaultListModel<>();
        elementos.forEach(modelo::addElement);

        JList<Object> lista = new JList<>(modelo);
        lista.setCellRenderer(new SolicitudesGrupoCellRenderer(esUsuario));
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setLayoutOrientation(JList.VERTICAL);

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        return scrollPane;
    }



    class UsuarioCellRenderer extends JPanel implements ListCellRenderer<Usuario> {

        private JLabel lblNombre = new JLabel();
        private JButton btnEnviarSolicitud = new JButton("+");

        public UsuarioCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(lblNombre);
            add(btnEnviarSolicitud);
            btnEnviarSolicitud.setBackground(colorBotonSolicitud);
            btnEnviarSolicitud.setForeground(colorTexto);

            btnEnviarSolicitud.addActionListener(e -> {
                // Lógica para enviar solicitud
                System.out.println("Solicitud enviada a: " + lblNombre.getText());
            });
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Usuario> list, Usuario value, int index, boolean isSelected, boolean cellHasFocus) {
            lblNombre.setText(value.getNombre());
            setBackground(isSelected ? colorFondoSecundario : colorFondoPrincipal);
            return this;
        }
    }

    class AmigoCellRenderer extends JPanel implements ListCellRenderer<Usuario> {

        private JLabel lblNombre = new JLabel();
        private JButton btnEliminarAmigo = new JButton("-");

        public AmigoCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(lblNombre);
            add(btnEliminarAmigo);
            btnEliminarAmigo.setBackground(colorBotonEliminar);
            btnEliminarAmigo.setForeground(colorTexto);

            btnEliminarAmigo.addActionListener(e -> {
                // Lógica para eliminar amigo
                System.out.println("Amigo eliminado: " + lblNombre.getText());
            });
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Usuario> list, Usuario value, int index, boolean isSelected, boolean cellHasFocus) {
            lblNombre.setText(value.getNombre());
            setBackground(isSelected ? colorFondoSecundario : colorFondoPrincipal);
            return this;
        }
    }
    
    
    class SolicitudesAmistadCellRenderer extends JPanel implements ListCellRenderer<Object> {

        private JLabel lblNombre = new JLabel();
        private JButton btnAceptar = new JButton("Aceptar");
        private JButton btnRechazar = new JButton("Rechazar");
        private JButton btnCancelar = new JButton("Cancelar");

        public SolicitudesAmistadCellRenderer(boolean Envia) {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(lblNombre);
            if(!Envia)
            {
                add(btnAceptar);
                add(btnRechazar);
                estiloBoton(btnAceptar);
                estiloBoton(btnRechazar);
                
                btnAceptar.addActionListener(e -> {
                // Lógica para aceptar la solicitud
                System.out.println("Solicitud aceptada para: " + lblNombre.getText());
                });

                btnRechazar.addActionListener(e -> {
                // Lógica para rechazar la solicitud
                System.out.println("Solicitud rechazada para: " + lblNombre.getText());
                });
            }
            else
            {
                add(btnCancelar);
                estiloBoton(btnCancelar);
                
                btnCancelar.addActionListener(e -> {
                // Lógica para rechazar la solicitud
                System.out.println("Solicitud cancelada para: " + lblNombre.getText());
                
            });
            }




            

        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Usuario) {
                lblNombre.setText(((Usuario) value).getNombre());
            } else if (value instanceof Grupo) {
                lblNombre.setText("Grupo ID: " + ((Grupo) value).getId_grupo());
            }
            setBackground(isSelected ? colorFondoSecundario : colorFondoPrincipal);
            return this;
        }
    }

    class GrupoCellRenderer extends JPanel implements ListCellRenderer<Grupo> {

        private JLabel lblNombre = new JLabel();
        private JButton btnEliminarGrupo = new JButton("-");

        public GrupoCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(lblNombre);
            add(btnEliminarGrupo);
            btnEliminarGrupo.setBackground(colorBotonEliminar);
            btnEliminarGrupo.setForeground(colorTexto);

            btnEliminarGrupo.addActionListener(e -> {
                // Lógica para eliminar grupo
                System.out.println("Grupo eliminado: " + lblNombre.getText());
            });
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Grupo> list, Grupo value, int index, boolean isSelected, boolean cellHasFocus) {
            lblNombre.setText("Grupo ID: " + value.getId_grupo());
            setBackground(isSelected ? colorFondoSecundario : colorFondoPrincipal);
            return this;
        }
    }

    class SolicitudesGrupoCellRenderer extends JPanel implements ListCellRenderer<Object> {

        private JLabel lblNombre = new JLabel();
        private JButton btnAceptar = new JButton("Aceptar");
        private JButton btnRechazar = new JButton("Rechazar");

        public SolicitudesGrupoCellRenderer(boolean esUsuario) {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(lblNombre);
            add(btnAceptar);
            add(btnRechazar);
            estiloBoton(btnAceptar);
            estiloBoton(btnRechazar);

            btnAceptar.addActionListener(e -> {
                // Lógica para aceptar la solicitud
                System.out.println("Solicitud aceptada para: " + lblNombre.getText());
            });

            btnRechazar.addActionListener(e -> {
                // Lógica para rechazar la solicitud
                System.out.println("Solicitud rechazada para: " + lblNombre.getText());
            });
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Usuario) {
                lblNombre.setText(((Usuario) value).getNombre());
            } else if (value instanceof Grupo) {
                lblNombre.setText("Grupo ID: " + ((Grupo) value).getId_grupo());
            }
            setBackground(isSelected ? colorFondoSecundario : colorFondoPrincipal);
            return this;
        }
    }

    //---------------------
    //Métodos privados
    //-------------------
    //Método para acceder al chat de un amigo
    private void handleChatFriendRequest(String friendUsername) {
        JSONObject json = new JSONObject();
        json.put("friend_username", friendUsername);
        json.put("action", "REQUEST_CHAT_FRIEND"); // Ajusta este campo con el tipo de acción correcto

        PersistentClient client = PersistentClient.getInstance();
        String serverResponse = client.sendMessageAndWaitForResponse(json.toString());

        switch (serverResponse) {
            case "-1":
                JOptionPane.showMessageDialog(null, "No se pudo encontrar al amigo o iniciar el chat.", "Error", JOptionPane.ERROR_MESSAGE);
                break;

            case "-2":
                JOptionPane.showMessageDialog(null, "Error desconocido. Intenta nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);

                break;
            default:
                JOptionPane.showMessageDialog(null, "Chat iniciado exitosamente.", "Información", JOptionPane.INFORMATION_MESSAGE);
                ChatAmigosPage amigos = new ChatAmigosPage();
                amigos.setVisible(true);
                break;

        }
    }

    
    
    
    
    
    
 private ArrayList<String> handleListFriends() {
    ArrayList<String> resultado = new ArrayList<>();

    JSONObject json = new JSONObject();
    json.put("action", "FIND_FRIENDS");

    PersistentClient client = PersistentClient.getInstance();
    String serverResponse = client.sendMessageAndWaitForResponse(json.toString());

    // Expresión regular para capturar status y friends
    Pattern pattern = Pattern.compile("\"status\":\\s*\"(-?\\d+)\",\\s*\"message\":\\s*(\\[.*\\])");
    Matcher matcher = pattern.matcher(serverResponse);

    if (matcher.find()) {
        // Extraer el valor de `status`
        String statusStr = matcher.group(1).trim();
        int status = Integer.parseInt(statusStr);

        // Solo procesar si el estado es exitoso (status 0)
        if (status == 0) {
            // Extraer el valor de `friends`
            String jsonFriends = matcher.group(2).trim();

            // Procesar el JSON en `friends`
            JSONArray amigosArray = new JSONArray(jsonFriends);
            for (int i = 0; i < amigosArray.length(); i++) {
                String amigo = amigosArray.getString(i);
                resultado.add(amigo); // Añadir cada amigo a la lista de resultado
            }
        } else {
            // Gestionar estados de error
            if (status == -1) {
                System.err.println("Error: no se pudo identificar al usuario remitente.");
            } else if (status == -2) {
                System.err.println("Error: ocurrió un error interno del servidor.");
            } else {
                System.err.println("Error desconocido con el estado: " + status);
            }
        }
    } else {
        System.err.println("Formato incorrecto o datos no encontrados.");
    }

    return resultado; // Devuelve la lista con los nombres de amigos
}
 
 
 private ArrayList<String> handleListUsersConectados() {
    ArrayList<String> resultado = new ArrayList<>();

    JSONObject json = new JSONObject();
    json.put("action", TipoRequest.FIND_USERS_CONNECTED);

    PersistentClient client = PersistentClient.getInstance();
    String serverResponse = client.sendMessageAndWaitForResponse(json.toString());

    try {
        JSONObject responseJson = new JSONObject(serverResponse); // Parsea la respuesta directamente como un JSONObject
        int status = responseJson.getInt("status");

        if (status == 0) {
            JSONArray usuariosArray = responseJson.getJSONArray("message"); // Accede directamente al array JSON
            for (int i = 0; i < usuariosArray.length(); i++) {
                String usuario = usuariosArray.getString(i);
                resultado.add(usuario);
            }
        } else {
            System.err.println("Error al obtener usuarios desconectados: estado " + status);
        }
    } catch (JSONException e) {
        System.err.println("Error al parsear la respuesta del servidor: " + e.getMessage());
    }

    return resultado;
    

}

 
 private ArrayList<String> handleListUsersDesconectados() {
    ArrayList<String> resultado = new ArrayList<>();

    JSONObject json = new JSONObject();
    json.put("action", TipoRequest.FIND_USERS_DISCONNECTED);

    PersistentClient client = PersistentClient.getInstance();
    String serverResponse = client.sendMessageAndWaitForResponse(json.toString());

    try {
        JSONObject responseJson = new JSONObject(serverResponse); // Parsea la respuesta directamente como un JSONObject
        int status = responseJson.getInt("status");

        if (status == 0) {
            JSONArray usuariosArray = responseJson.getJSONArray("message"); // Accede directamente al array JSON
            for (int i = 0; i < usuariosArray.length(); i++) {
                String usuario = usuariosArray.getString(i);
                resultado.add(usuario);
            }
        } else {
            System.err.println("Error al obtener usuarios desconectados: estado " + status);
        }
    } catch (JSONException e) {
        System.err.println("Error al parsear la respuesta del servidor: " + e.getMessage());
    }

    return resultado;
}
 
 
 


 
 

    
  
}
        
