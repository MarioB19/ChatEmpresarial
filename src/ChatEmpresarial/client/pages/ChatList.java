package ChatEmpresarial.client.pages;

import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.models.Grupo;
import ChatEmpresarial.shared.models.Usuario;
import ChatEmpresarial.client.pages.UsuarioFixCellRenderer;
import ChatEmpresarial.client.pages.GroupFixCellRenderer;
import ChatEmpresarial.shared.utilities.Enumerators;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChatList extends JFrame {

    
    private ArrayList<Usuario> usuariosConectados = new ArrayList<>();
    private ArrayList<Usuario> usuariosDesconectados = new ArrayList<>();
    private ArrayList<Usuario> amigosConectados = new ArrayList<>();
    private ArrayList<Usuario> amigosDesconectados = new ArrayList<>();
    private ArrayList<Usuario> solicitudesAmigos = new ArrayList<>();
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

    public ChatList() {
        inicializarDatosDePrueba();
        configurarVentana();
        configurarNavegacion();
        setVisible(true);
    }

    private void inicializarDatosDePrueba() {
        
        
        //Inicializar amigos
        
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
            usuariosConectados.add(usuario);
            usuariosDesconectados.add(usuario);
          //  amigosConectados.add(usuario);
            amigosDesconectados.add(usuario);
            solicitudesAmigos.add(usuario);

            Grupo grupo = new Grupo();
            grupo.setId_grupo(i);
            grupos.add(grupo);
            solicitudesGrupos.add(grupo);
        }
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

        // Panel principal con CardLayout
        panelPrincipal = new JPanel();
        cardLayout = new CardLayout();
        panelPrincipal.setLayout(cardLayout);
        panelPrincipal.add(crearPanelUsuarios(), "Usuarios");
        panelPrincipal.add(crearPanelAmigos(), "Amigos");
        panelPrincipal.add(crearPanelGrupos(), "Grupos");

        btnUsuarios.addActionListener(e -> cardLayout.show(panelPrincipal, "Usuarios"));
        btnAmigos.addActionListener(e -> cardLayout.show(panelPrincipal, "Amigos"));
        btnGrupos.addActionListener(e -> cardLayout.show(panelPrincipal, "Grupos"));

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
        panel.add(crearListaUsuarios("Usuarios Conectados", usuariosConectados, true));
        panel.add(crearListaUsuarios("Usuarios Desconectados", usuariosDesconectados, false));
        return panel;
    }

    private JScrollPane crearListaUsuarios(String titulo, ArrayList<Usuario> usuarios, boolean estaConectado) {
        DefaultListModel<Usuario> modelo = new DefaultListModel<>();
        usuarios.forEach(modelo::addElement);

        JList<Usuario> lista = new JList<>(modelo);
        lista.setCellRenderer(new UsuarioCellRenderer());
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setLayoutOrientation(JList.VERTICAL);

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        return scrollPane;
    }

    private JPanel crearPanelAmigos() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(crearListaAmigos("Amigos Conectados", amigosConectados, true));
        panel.add(crearListaAmigos("Amigos Desconectados", amigosDesconectados, false));
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

    private JPanel crearPanelGrupos() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(crearListaGrupos("Grupos", grupos, false));
        panel.add(crearListaSolicitudes("Solicitudes de Grupos", solicitudesGrupos, false));
        return panel;
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

    private JScrollPane crearLista(String titulo, ArrayList<?> elementos, boolean esUsuario) {
        DefaultListModel<Object> modelo = new DefaultListModel<>();
        elementos.forEach(modelo::addElement);

        JList<Object> lista = new JList<>(modelo);
        lista.setCellRenderer(esUsuario ? new UsuarioFixCellRenderer() : new GroupFixCellRenderer());
        lista.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        lista.setLayoutOrientation(JList.VERTICAL_WRAP);
        lista.setFixedCellWidth(120);

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        return scrollPane;
    }

    private JScrollPane crearListaSolicitudes(String titulo, ArrayList<?> elementos, boolean esUsuario) {
        DefaultListModel<Object> modelo = new DefaultListModel<>();
        elementos.forEach(modelo::addElement);

        JList<Object> lista = new JList<>(modelo);
        lista.setCellRenderer(new SolicitudesCellRenderer(esUsuario));
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

    class SolicitudesCellRenderer extends JPanel implements ListCellRenderer<Object> {

        private JLabel lblNombre = new JLabel();
        private JButton btnAceptar = new JButton("Aceptar");
        private JButton btnRechazar = new JButton("Rechazar");

        public SolicitudesCellRenderer(boolean esUsuario) {
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
    
  
}
        
