package ChatEmpresarial.client.pages;

import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.models.Grupo;
import ChatEmpresarial.shared.models.Usuario;
import ChatEmpresarial.client.pages.UsuarioFixCellRenderer;
import ChatEmpresarial.client.pages.GroupFixCellRenderer;
import ChatEmpresarial.shared.utilities.Enumerators;
import ChatEmpresarial.shared.utilities.Functions;
import ChatEmpresarial.client.utilities.GetUsers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
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
        for (int i = 1; i <= 3; i++) {
            Usuario usuario = new Usuario();
            usuario.setNombre("User" + i);
            usuariosConectados.add(usuario);
            usuariosDesconectados.add(usuario);
            amigosConectados.add(usuario);
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

}
