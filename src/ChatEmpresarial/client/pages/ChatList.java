package ChatEmpresarial.client.pages;

import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.models.Grupo;
import ChatEmpresarial.shared.models.Usuario;
import ChatEmpresarial.client.pages.UsuarioFixCellRenderer;
import ChatEmpresarial.client.pages.GroupFixCellRenderer;
import ChatEmpresarial.shared.utilities.Functions;
import ChatEmpresarial.shared.models.SolicitudAmistad;

import ChatEmpresarial.shared.models.SolicitudGrupo;

import ChatEmpresarial.shared.utilities.Enumerators;
import ChatEmpresarial.shared.utilities.Enumerators.TipoRequest;
import ChatEmpresarial.shared.utilities.Functions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class ChatList extends JFrame {

    
    //Bandera de la página en que se encuentra
    private String CurrentVisibleCard = "Usuarios";
    
    private DefaultTableModel modeloUsuariosConectados = new DefaultTableModel();
    private DefaultTableModel modeloUsuariosDesconectados = new DefaultTableModel();
    
    private DefaultTableModel modeloAmigosConectados = new DefaultTableModel();
    private DefaultTableModel modeloAmigosDesconectados = new DefaultTableModel();
    

    private DefaultTableModel modeloSolicitudesRecibidas = new DefaultTableModel();
    private DefaultTableModel modeloSolicitudesEnviadas = new DefaultTableModel();
    
    
    private Timer updateTimer;

    private String nombreUserActive;

    private ArrayList<Usuario> SolicitudAmistadRecibida = new ArrayList<>();

    private ArrayList<Usuario> usuariosConectados = new ArrayList<>();
    private ArrayList<Usuario> usuariosDesconectados = new ArrayList<>();
    private ArrayList<Usuario> amigosConectados = new ArrayList<>();
    private ArrayList<Usuario> amigosDesconectados = new ArrayList<>();
    private ArrayList<Usuario> solicitudesAmigosEnviadas = new ArrayList<>();
   
    private ArrayList<Grupo> grupos = handleGetGroups();
    private ArrayList<SolicitudGrupo> solicitudesGrupos = handleGetRequestGroups();

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
       nombreUserActive = username;
       
  
       iniciarModelos();
        inicializarDatos();  // Llamada inicial para cargar datos antes de que el Timer comience
        configurarVentana();
        configurarNavegacion();
        configurarTimer();
        grupos = handleGetGroups();
        setVisible(true);
    
}
    
private void configurarTimer() {
    int delay = 1000; // Retraso en milisegundos (1000 ms = 1 segundo)
    ActionListener taskPerformer = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            inicializarDatos();  // Llama a tu método que actualiza los datos
            actualizarListasUsuarios();
        }
    };
    updateTimer = new Timer(delay, taskPerformer);
    updateTimer.start();
}


private void iniciarModelos()
{
       modeloUsuariosConectados.addColumn("nombre");
        modeloUsuariosConectados.addColumn("borrar");

        modeloUsuariosDesconectados.addColumn("nombre");
        modeloUsuariosDesconectados.addColumn("borrar");

        modeloAmigosConectados.addColumn("nombre");
        modeloAmigosConectados.addColumn("borrar");

        modeloAmigosDesconectados.addColumn("nombre");
        modeloAmigosDesconectados.addColumn("borrar");

        modeloSolicitudesRecibidas.addColumn("nombre");
        modeloSolicitudesRecibidas.addColumn("Agregar");
        modeloSolicitudesRecibidas.addColumn("borrar");

        modeloSolicitudesEnviadas.addColumn("nombre");
        modeloSolicitudesEnviadas.addColumn("borrar");
    
    
}



    
    private void inicializarDatos() {
                 
    String username = nombreUserActive;

    // Obtener los nombres de los usuarios desde el server

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
   
    amigosConectados.clear();
    amigosDesconectados.clear();
    
        
          // Obtener amigos de la respuesta del servidor
    ArrayList<String> amigosConectadosNombre = handleListFriendsConnected();
    ArrayList<String> amigosDesconectadosNombre = handleListFriendsDisconnected();

       
     
            
        for (String nombre : amigosConectadosNombre) {
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            amigosConectados.add(usuario);
        }
        
   
        for (String nombre: amigosDesconectadosNombre) {
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            amigosDesconectados.add(usuario);
            
     }
        

        
      
            
             actualizarListasUsuarios();
          //  amigosConectados.add(usuario);
           

           // Grupo grupo = new Grupo();
           // grupo.setId_grupo(i);
           // grupos.add(grupo);
           // solicitudesGrupos.add(grupo);
    }
    
    private void actualizarListasUsuarios() {
    SwingUtilities.invokeLater(() -> {
        
    
            // Limpiar los modelos de las tablas
            modeloUsuariosConectados.setRowCount(0);
            modeloUsuariosDesconectados.setRowCount(0);
            modeloAmigosConectados.setRowCount(0);
            modeloAmigosDesconectados.setRowCount(0);
            modeloSolicitudesRecibidas.setRowCount(0);
            modeloSolicitudesEnviadas.setRowCount(0);

            // Añadir filas a los modelos
            for (Usuario us : usuariosConectados) {
                modeloUsuariosConectados.addRow(new Object[]{us.getNombre(), "+"});
            }

            for (Usuario us : usuariosDesconectados) {
                modeloUsuariosDesconectados.addRow(new Object[]{us.getNombre(), "+"});
            }

            for (Usuario us : amigosConectados) {
                modeloAmigosConectados.addRow(new Object[]{us.getNombre(), "-"});
            }

            for (Usuario us : amigosDesconectados) {
                modeloAmigosDesconectados.addRow(new Object[]{us.getNombre(), "-"});
            }

            obtenerSolicitudesRecibidas();
            for (Usuario us : SolicitudAmistadRecibida) {
                modeloSolicitudesRecibidas.addRow(new Object[]{us.getNombre(), "+", "-"});
            }

            obtenerSolicitudesEnviadas();
            for (Usuario us : solicitudesAmigosEnviadas) {
                modeloSolicitudesEnviadas.addRow(new Object[]{us.getNombre(), "-"});
            }

            // Notificar cambios en los modelos
            modeloUsuariosConectados.fireTableDataChanged();
            modeloUsuariosDesconectados.fireTableDataChanged();
            modeloAmigosConectados.fireTableDataChanged();
            modeloAmigosDesconectados.fireTableDataChanged();
            modeloSolicitudesRecibidas.fireTableDataChanged();
            modeloSolicitudesEnviadas.fireTableDataChanged();
        });

        



       
        
    }
    

    

    private void configurarVentana() {
        setTitle("Chats Empresariales");
        setSize(360, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(colorFondoPrincipal);
        
          // Panel para cerrar sesión
    JPanel panelCerrarSesion = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton btnCerrarSesion = new JButton("Cerrar Sesión");
    btnCerrarSesion.addActionListener(e -> cerrarSesion());
    estiloBoton(btnCerrarSesion);  // Aplicar el estilo predefinido de los botones
    panelCerrarSesion.add(btnCerrarSesion);
    panelCerrarSesion.setBackground(colorBarraNavegacion);  // Usar el mismo color que la barra de navegación
     getContentPane().add(panelCerrarSesion, BorderLayout.NORTH);
    
    
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
    

    private void cerrarSesion() {
    if (updateTimer != null) {
        updateTimer.stop();  // Detener el Timer primero para evitar llamadas de actualización
        updateTimer = null;
    }

    JSONObject json = new JSONObject();
    json.put("action", TipoRequest.LOGOUT);
    json.put("nombre", nombreUserActive);

    // Asegúrate de que la instancia de cliente pueda manejar adecuadamente la reconexión
    PersistentClient client = PersistentClient.getInstance();
    String serverResponse = client.sendMessageAndWaitForResponse(json.toString());
    System.out.println("Server Response: " + serverResponse);

    if (serverResponse.equals("0")) {
        dispose();
        
      client = PersistentClient.getInstance();
     serverResponse = client.sendMessageAndWaitForResponse("init"); //inicializando comunicacion
   

        // Crear e iniciar la ventana de login
        EventQueue.invokeLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
        });
    } else {
        JOptionPane.showMessageDialog(this, "Ha ocurrido un error al cerrar sesión", "Error", JOptionPane.ERROR_MESSAGE);
    }
}





    private JPanel crearPanelUsuarios() {
    JPanel panel = new JPanel(new GridLayout(2, 1));
    panel.add(crearListaUsuarios("Usuarios Conectados", modeloUsuariosConectados, true));
    panel.add(crearListaUsuarios("Usuarios Desconectados", modeloUsuariosDesconectados, false));
    return panel;
}



   
        
        

    private JScrollPane crearListaUsuarios(String titulo, DefaultTableModel model,  boolean estaConectado) {
        
     
        
        JTable lista = new JTable(model);
        
        lista.setTableHeader(null);
        lista.getColumn("borrar").setCellRenderer(new ButtonRenderer());
        
        
        
        
        
        
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        lista.addMouseListener(new MouseAdapter() {
           
            public void mouseClicked(MouseEvent e) {
                
                int column = lista.getColumnModel().getColumnIndexAtX(e.getX()); // obtiene la columna
                int row = e.getY() / lista.getRowHeight(); // obtiene la fila

                // asegurando que la fila y columna seleccionadas están dentro de la tabla
                if (row < lista.getRowCount() && row >= 0 && column < lista.getColumnCount() && column >= 0) {
                    Object value = lista.getValueAt(row, column);
                    
                    // si es un botón, realiza la acción correspondiente
                    if(column == 0)
                    {
                        
                        if (row >= 0) {

                            
                            if(estaConectado)
                            {
                                Usuario usuario = usuariosConectados.get(row);


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
                    
                        if (column == 1) 
                        {

                            if(estaConectado)
                            {
                                Usuario usuario = usuariosConectados.get(row);
                                enviarSolicitudAmistad(usuario.getNombre());
                            }
                            else
                            {
                                Usuario usuario = usuariosDesconectados.get(row);
                                enviarSolicitudAmistad(usuario.getNombre());
                            }
                            

                            Usuario usuario = (Usuario) lista.getModel().getValueAt(row, 0);
                            String nombre = usuario.getNombre();
                            
                            System.out.println(nombre);
                            
                     
                           
                           
                        }
                }
            }

        });
        

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));

        return scrollPane;

        
        /*
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
<<<<<<< HEAD
    */
    

}

    
    
 


    private JPanel crearPanelAmigos() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(crearListaAmigos("Amigos Conectados", modeloAmigosConectados, true));
        panel.add(crearListaAmigos("Amigos Desconectados", modeloAmigosDesconectados, false));

        panel.add(crearListaSolicitudesAmigosRecibidas("Solicitudes Recibida", modeloSolicitudesRecibidas ,true));
       panel.add(crearListaSolicitudesAmigosEnviadas("Solicitudes enviadas", modeloSolicitudesEnviadas  ,false));  //NO EXISTE EL Método

        return panel;
    }

    private JScrollPane crearListaAmigos(String titulo, DefaultTableModel amigos, boolean estaConectado) {
        
       
        
       

        
        JTable lista = new JTable(amigos);
        
        lista.setTableHeader(null);
        lista.getColumn("borrar").setCellRenderer(new ButtonRenderer());
        
        
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        lista.addMouseListener(new MouseAdapter() {
           
            public void mouseClicked(MouseEvent e) {
                
                int column = lista.getColumnModel().getColumnIndexAtX(e.getX()); // obtiene la columna
                int row = e.getY() / lista.getRowHeight(); // obtiene la fila

                // asegurando que la fila y columna seleccionadas están dentro de la tabla
                if (row < lista.getRowCount() && row >= 0 && column < lista.getColumnCount() && column >= 0) {
                    Object value = lista.getValueAt(row, column);
                    
                    // si es un botón, realiza la acción correspondiente
                    if (column == 1) 
                    {
                        Usuario usuario = amigosDesconectados.get(row);
                        eliminarMensajesYAmistad(usuario.getNombre());
                            
                    }
                }
            }

        });
        

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        return scrollPane;
    }
  
     private JScrollPane crearListaSolicitudesAmigosRecibidas(String titulo,DefaultTableModel  elementos, boolean Envia) {

        JTable lista = new JTable(elementos);
        
        lista.setTableHeader(null);
        lista.getColumn("Agregar").setCellRenderer(new ButtonRenderer());
        lista.getColumn("borrar").setCellRenderer(new ButtonRenderer());
        
        
        
        
        
        
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        lista.addMouseListener(new MouseAdapter() {
           
            public void mouseClicked(MouseEvent e) {
                
                int column = lista.getColumnModel().getColumnIndexAtX(e.getX()); // obtiene la columna
                int row = e.getY() / lista.getRowHeight(); // obtiene la fila

                // asegurando que la fila y columna seleccionadas están dentro de la tabla
                if (row < lista.getRowCount() && row >= 0 && column < lista.getColumnCount() && column >= 0) {
                    Object value = lista.getValueAt(row, column);
                    
                    // si es un botón, realiza la acción correspondiente
                    
                    
                      Usuario usuario = SolicitudAmistadRecibida.get(row);
                        if (column == 1) 
                        {
                          
                            aceptarSolicitudAmistad(usuario.getNombre());
                           
                        }

                    
                        if (column == 2) 
                        {
                
                            cancelarSolicitudAmistad(usuario.getNombre());
                           
                        }
                    
                }
            }

        });
        

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        return scrollPane;
    }
    
    
    
     private JScrollPane crearListaSolicitudesAmigosEnviadas(String titulo, DefaultTableModel elementos, boolean Envia) {
             
     
        JTable lista = new JTable(elementos);
        
        lista.setTableHeader(null);
       
        lista.getColumn("borrar").setCellRenderer(new ButtonRenderer());
        
        
        
        
      
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        lista.addMouseListener(new MouseAdapter() {
           
            public void mouseClicked(MouseEvent e) {
                
                int column = lista.getColumnModel().getColumnIndexAtX(e.getX()); // obtiene la columna
                int row = e.getY() / lista.getRowHeight(); // obtiene la fila

                // asegurando que la fila y columna seleccionadas están dentro de la tabla
                if (row < lista.getRowCount() && row >= 0 && column < lista.getColumnCount() && column >= 0) {
                    Object value = lista.getValueAt(row, column);
                    
                    // si es un botón, realiza la acción correspondiente
                    
                    
                    
                        if (column == 1) 
                        {
                            Usuario usuario = solicitudesAmigosEnviadas.get(row);
                            cancelarSolicitudAmistad(usuario.getNombre());
                            
                        }

                    

                    
                }
            }

        });
        

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        return scrollPane;
    }

    

    
    public class ButtonRenderer extends JButton implements TableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
        
    }

  
   
    


    

    private JPanel crearPanelGrupos() {
        JPanel panel = new JPanel(new BorderLayout());

        // Botón para crear un nuevo grupo
        JButton btnCrearGrupo = new JButton("Crear Grupo");
        estiloBoton(btnCrearGrupo);
        btnCrearGrupo.addActionListener(e -> {
            try {
                ArrayList<Usuario> users = handleGetUsersExceptSelf();  // Fetch the users
                AddGroup createGroupFrame = new AddGroup(nombreUserActive, users);
                createGroupFrame.setVisible(true); // Show the frame
                dispose();  // Close the current frame
            } catch (Exception ex) {
                ex.printStackTrace(); // Log the exception to standard error
                JOptionPane.showMessageDialog(null, "Error opening the group creation frame: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        grupos = handleGetGroups();
        solicitudesGrupos = handleGetRequestGroups();

        // Panel para la lista de grupos y solicitudes
        JPanel panelListas = new JPanel(new GridLayout(2, 1));
        panelListas.add(crearListaGrupos("Grupos", grupos, false));
        panelListas.add(crearListaSolicitudesGrupo("Solicitudes de Grupos", solicitudesGrupos, false));

        panel.add(btnCrearGrupo, BorderLayout.NORTH);
        panel.add(panelListas, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane crearListaGrupos(String titulo, ArrayList<Grupo> grupos, boolean estaConectado) {
        DefaultListModel<Grupo> modelo = new DefaultListModel<>();
        grupos.forEach(modelo::addElement);

        JList<Grupo> lista = new JList<>(modelo);

        lista.setCellRenderer(new GrupoCellRenderer());
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setLayoutOrientation(JList.VERTICAL);

        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Grupo grupoSeleccionado = lista.getSelectedValue();
                if (grupoSeleccionado != null) {
                    GroupChatWindow chatWindow = new GroupChatWindow(grupoSeleccionado, nombreUserActive);
                    chatWindow.setVisible(true);
                    dispose();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        return scrollPane;
    }

    private JScrollPane crearListaSolicitudesGrupo(String titulo, ArrayList<SolicitudGrupo> elementos, boolean esUsuario) {
        DefaultListModel<SolicitudGrupo> modelo = new DefaultListModel<>();
        elementos.forEach(modelo::addElement);

        JList<SolicitudGrupo> lista = new JList<>(modelo);
        lista.setCellRenderer(new SolicitudesGrupoCellRenderer(esUsuario));
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setLayoutOrientation(JList.VERTICAL);

        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SolicitudGrupo solicitudGrupo = lista.getSelectedValue();
                if (solicitudGrupo != null) {
                    dispose();
                    GroupRequest gr = new GroupRequest(solicitudGrupo, nombreUserActive);
                    gr.setVisible(true);
                    dispose();
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        return scrollPane;
    }

   

    

    
    
    
    
    class GrupoCellRenderer extends JPanel implements ListCellRenderer<Grupo> {

        private JLabel lblNombre = new JLabel();
        private JButton btnIrAlGrupo = new JButton("Entrar");
        private JButton btnEditarMiembros = new JButton("Editar Miembros");
        private JButton btnEliminarGrupo = new JButton("Eliminar Grupo");
        private JButton btnSalirDelGrupo = new JButton("Salir del Grupo");

        public GrupoCellRenderer() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            topPanel.add(lblNombre);
            add(topPanel);
            add(bottomPanel);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Grupo> list, Grupo value, int index, boolean isSelected, boolean cellHasFocus) {
            lblNombre.setText("Grupo " + value.getNombre() + " con ID: " + value.getId_grupo());
            setBackground(isSelected ? colorFondoSecundario : colorFondoPrincipal);
            validate();
            return this;
        }

    }

    class SolicitudesGrupoCellRenderer extends JPanel implements ListCellRenderer<SolicitudGrupo> {

        private JLabel lblNombre = new JLabel();
        private JButton btnAceptar = new JButton("Aceptar");
        private JButton btnRechazar = new JButton("Rechazar");

        public SolicitudesGrupoCellRenderer(boolean esUsuario) {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(lblNombre);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends SolicitudGrupo> list, SolicitudGrupo value, int index, boolean isSelected, boolean cellHasFocus) {
            lblNombre.setText(value.getId_remitente() + " te invita al Grupo " + value.getNombreGrupo() + " con ID: " + value.getId_grupo());
            setBackground(isSelected ? colorFondoSecundario : colorFondoPrincipal);
            validate();
            return this;
        }
    }
    
    /*
    class ControlPanelEliminarAmigos extends JPanel 
    {
            
        private JButton btnEliminarAmigo = new JButton("-");
        private JLabel algo = new JLabel("buenas tardes");

        public ControlPanelEliminarAmigos(JList<Usuario> listaUsuarios) {

            setLayout(new FlowLayout());
            btnEliminarAmigo.setBackground(colorBotonEliminar);
            btnEliminarAmigo.setForeground(colorTexto);

            // Acción del botón

            btnEliminarAmigo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Usuario usuarioSeleccionado = listaUsuarios.getSelectedValue();
                    if (usuarioSeleccionado != null) {
                        // Realiza la acción con el usuario seleccionado
                        System.out.println("Eliminaste a: " + usuarioSeleccionado.getNombre());

                    } else {
                        System.out.println("No hay usuario seleccionado");
                    }
                }
            });
            add(btnEliminarAmigo);
            add(algo);
        }
    }

*/
    
    
    
    

    //---------------------
    //Métodos privados
    //-------------------
    //Método para acceder al chat de un amigo
    private void handleChatFriendRequest(String friendUsername) {
        JSONObject json = new JSONObject();
        json.put("receptor", friendUsername);
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

    
    //Método para solicitar el listado de amigos conectados
    private ArrayList<String> handleListFriendsConnected() {
    ArrayList<String> resultado = new ArrayList<>();

    // Crear la solicitud JSON con la acción para encontrar amigos conectados
    JSONObject json = new JSONObject();
    json.put("action", "FIND_FRIENDS_CONNECTED");

    // Usar el cliente persistente para enviar el mensaje
    PersistentClient client = PersistentClient.getInstance();
    String serverResponse = client.sendMessageAndWaitForResponse(json.toString());

    try {
        // Parsea la respuesta directamente como un objeto JSON
        JSONObject responseJson = new JSONObject(serverResponse);
        int status = responseJson.getInt("status");

        // Si el estado es 0, el resultado fue exitoso
        if (status == 0) {
            // Acceder directamente al array JSON
            JSONArray amigosArray = responseJson.getJSONArray("message");
            for (int i = 0; i < amigosArray.length(); i++) {
                String amigo = amigosArray.getString(i);
                resultado.add(amigo); // Añadir a la lista de resultado
            }
        } else {
            System.err.println("Error al obtener amigos conectados: estado " + status);
        }
    } catch (JSONException e) {
        System.err.println("Error al parsear la respuesta del servidor: " + e.getMessage());
    }

    return resultado; // Devuelve la lista de amigos conectados
}
    
    
    
    //Método para obtener los métodos de amigos disconectados
    private ArrayList<String> handleListFriendsDisconnected() {
    ArrayList<String> resultado = new ArrayList<>();

    // Crear la solicitud JSON con la acción para encontrar amigos desconectados
    JSONObject json = new JSONObject();
    json.put("action", "FIND_FRIENDS_DISCONNECTED");

    // Usar el cliente persistente para enviar el mensaje
    PersistentClient client = PersistentClient.getInstance();
    String serverResponse = client.sendMessageAndWaitForResponse(json.toString());

    try {
        // Parsea la respuesta directamente como un objeto JSON
        JSONObject responseJson = new JSONObject(serverResponse);
        int status = responseJson.getInt("status");

        // Si el estado es 0, el resultado fue exitoso
        if (status == 0) {
            // Acceder directamente al array JSON
            JSONArray amigosArray = responseJson.getJSONArray("message");
            for (int i = 0; i < amigosArray.length(); i++) {
                String amigo = amigosArray.getString(i);
                resultado.add(amigo); // Añadir a la lista de resultado
            }
        } else {
            System.err.println("Error al obtener amigos desconectados: estado " + status);
        }
    } catch (JSONException e) {
        System.err.println("Error al parsear la respuesta del servidor: " + e.getMessage());
    }

    return resultado; // Devuelve la lista de amigos desconectados
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

    private ArrayList<Usuario> handleGetUsersExceptSelf() {
        ArrayList<Usuario> users = new ArrayList<>();

        try {
            System.out.println("Usuario activo mandando a get usuarios: " + nombreUserActive);
            JSONObject request = new JSONObject();
            request.put("activeuser", nombreUserActive);
            request.put("action", "GET_ALL_USERS_EXCEPT_SELF");

            PersistentClient client = PersistentClient.getInstance();
            String response = client.sendMessageAndWaitForResponse(request.toString());

            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");

            switch (status) {
                case "0":  // Success
                    System.out.println("Data recieved from server: " + jsonResponse.toString());
                    JSONArray usersArray = jsonResponse.getJSONArray("users");
                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject userJson = usersArray.getJSONObject(i);
                        Usuario user = new Usuario();
                        user.setId_usuario(userJson.getInt("id"));
                        user.setNombre(userJson.getString("nombre"));
                        users.add(user);
                    }
                    break;
                case "1":  // Failure
                    JOptionPane.showMessageDialog(null, "Failed to fetch users.", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                default:  // Unknown error or other statuses
                    JOptionPane.showMessageDialog(null, "Unexpected response while fetching users: " + status, "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error processing user data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return users;
    }

    private ArrayList<Grupo> handleGetGroups() {
        ArrayList<Grupo> groups = new ArrayList<>();

        try {
            System.out.println("Usuario activo mandando a grupos: " + nombreUserActive);

            JSONObject request = new JSONObject();
            request.put("activeuser", nombreUserActive);
            request.put("action", "GET_ALL_GROUPS");

            PersistentClient client = PersistentClient.getInstance();
            String response = client.sendMessageAndWaitForResponse(request.toString());

            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");

            System.out.println("Making request to fetch groups with: " + request.toString());

            switch (status) {
                case "0":  // Success
                    System.out.println("Groups recieved from server: " + jsonResponse.toString());
                    JSONArray usersArray = jsonResponse.getJSONArray("groups");
                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject userJson = usersArray.getJSONObject(i);
                        Grupo grupo = new Grupo();
                        grupo.setId_grupo(userJson.getInt("id"));
                        grupo.setNombre(userJson.getString("nombre"));
                        grupo.setNombreAdmin(userJson.getString("admin"));
                        grupo.setId_chat(userJson.getInt("chat"));
                        groups.add(grupo);
                    }
                    break;
                case "1":  // Failure
                    JOptionPane.showMessageDialog(null, "Failed to fetch groups.", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                default:  // Unknown error or other statuses
                    JOptionPane.showMessageDialog(null, "Unexpected response while fetching groups: " + status, "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error processing group data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return groups;
    }

    private ArrayList<SolicitudGrupo> handleGetRequestGroups() {
        ArrayList<SolicitudGrupo> solicitudes = new ArrayList<>();

        try {
            System.out.println("Usuario activo mandando a solicitudes: " + nombreUserActive);

            JSONObject request = new JSONObject();
            request.put("activeuser", nombreUserActive);
            request.put("action", "GET_ALL_GROUPS_REQUESTS");

            PersistentClient client = PersistentClient.getInstance();
            String response = client.sendMessageAndWaitForResponse(request.toString());

            JSONObject jsonResponse = new JSONObject(response);
            String status = jsonResponse.getString("status");

            System.out.println("Making request to fetch groups requests with: " + request.toString());

            switch (status) {
                case "0":  // Success
                    System.out.println("Requests recieved from server: " + jsonResponse.toString());
                    JSONArray usersArray = jsonResponse.getJSONArray("request");
                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject userJson = usersArray.getJSONObject(i);
                        SolicitudGrupo solicitudGrupo = new SolicitudGrupo();
                        solicitudGrupo.setId_solicitud(userJson.getInt("idSolicitud"));
                        solicitudGrupo.setId_grupo(userJson.getInt("idGrupo"));
                        solicitudGrupo.setId_remitente(userJson.getString("admin"));
                        solicitudGrupo.setNombreGrupo(userJson.getString("nombreGrupo"));
                        solicitudes.add(solicitudGrupo);
                    }
                    break;
                case "1":  // Failure
                    JOptionPane.showMessageDialog(null, "Failed to fetch requests.", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                default:  // Unknown error or other statuses
                    JOptionPane.showMessageDialog(null, "Unexpected response while fetching requests: " + status, "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error processing request data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return solicitudes;
    }




 
 
 //---------------------Metodos de control de peticiones de amistad -------------//
 
 private void cancelarSolicitudAmistad(String receptor) {
    JSONObject json = new JSONObject();
    json.put("receptor", receptor);
    json.put("action", "REFUSE_INVITATION_FRIEND");

    PersistentClient client = PersistentClient.getInstance();
    String serverResponse = client.sendMessageAndWaitForResponse(json.toString());
        JSONObject jsonResponse = new JSONObject(serverResponse);
            String status = jsonResponse.getString("status");

    switch (status){
        case "-5":
            JOptionPane.showMessageDialog(null, "Remitente no identificado.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
        case "-6":
            JOptionPane.showMessageDialog(null, "Campo 'receptor' faltante en la solicitud.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
        case "-2":
            JOptionPane.showMessageDialog(null, "Remitente o receptor no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
        case "0":
            JOptionPane.showMessageDialog(null, "Solicitud de amistad cancelada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            break;
        default:
            JOptionPane.showMessageDialog(null, "Error al cancelar la solicitud.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
    }
}
 
 
 private void enviarSolicitudAmistad(String receptor) {
    JSONObject json = new JSONObject();
    json.put("receptor", receptor);
    json.put("action", "SENT_INVITATION_FRIEND");

    PersistentClient client = PersistentClient.getInstance();
    String serverResponse = client.sendMessageAndWaitForResponse(json.toString());
  JSONObject jsonResponse = new JSONObject(serverResponse);
            String status = jsonResponse.getString("status");

    switch (status) {
        case "-4":
            JOptionPane.showMessageDialog(null, "Remitente no identificado.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
        case "-5":
            JOptionPane.showMessageDialog(null, "Campo 'receptor' faltante en la solicitud.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
        case "-6":
            JOptionPane.showMessageDialog(null, "Error interno.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
        case "0":
            JOptionPane.showMessageDialog(null, "Solicitud de amistad enviada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            break;
        default:
            JOptionPane.showMessageDialog(null, "Error desconocido al enviar la solicitud.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
    }
    
}
 
 
 private void obtenerSolicitudesRecibidas() {
    JSONObject json = new JSONObject();
    json.put("action", "GET_INVITATION_FRIEND");

    PersistentClient client = PersistentClient.getInstance();
    String serverResponse = client.sendMessageAndWaitForResponse(json.toString());
      JSONObject responseObject = new JSONObject(serverResponse);
           String status  = responseObject.getString("status");

    switch (status) {
        case "-4":
            JOptionPane.showMessageDialog(null, "Receptor no identificado.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
        case "-5":
            JOptionPane.showMessageDialog(null, "Receptor no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
        case "-6":
            JOptionPane.showMessageDialog(null, "Error al obtener las solicitudes recibidas.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
        case "0":
            responseObject = new JSONObject(serverResponse);
         JSONArray  receivedInvitations = responseObject.optJSONArray("message");
            // Procesar las invitaciones recibidas (JSON array)
         SolicitudAmistadRecibida.clear();
            for (int i = 0; i < receivedInvitations.length(); i++) {

                
                String usuario = receivedInvitations.getString(i);
                SolicitudAmistadRecibida.add(new Usuario(usuario));
                
                /*
=======
>>>>>>> 9a940de4585c2e9305bdb1e8f83166f0c3fa592a
                JSONArray invitation = receivedInvitations.getJSONArray(i);
                SolicitudAmistadRecibida.clear();
                for(Object inv : invitation)
                {
                    SolicitudAmistadRecibida.add(new Usuario(inv.toString()));
                }
<<<<<<< HEAD
                */

                
            }
            break;
        default:
            JOptionPane.showMessageDialog(null, "Error desconocido al obtener las solicitudes.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
    }
    
    
    //-------------Métodos de control de Amistad --------------------- /
    
    
}

 
 
private void eliminarMensajesYAmistad(String receptor) {
    // Crear un objeto JSON con la información necesaria para el servidor
    JSONObject json = new JSONObject();
    json.put("receptor", receptor);
    json.put("action", "DELETE_CHAT_FRIEND");

    // Enviar la solicitud al servidor y esperar una respuesta
    PersistentClient client = PersistentClient.getInstance();
    String serverResponse = client.sendMessageAndWaitForResponse(json.toString());

    // Procesar la respuesta del servidor
    try {
        JSONObject response = new JSONObject(serverResponse);
        String status = response.getString("status");
        String message = response.getString("message");

        // Interpretar el resultado basado en el estado
        switch (status) {
            case "-4":
                JOptionPane.showMessageDialog(null, "Receptor no identificado.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            case "-5":
                JOptionPane.showMessageDialog(null, "Error al eliminar todos los mensajes y la amistad.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            case "-6":
                JOptionPane.showMessageDialog(null, "Error interno al eliminar todos los mensajes y la amistad.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            case "0":
                JOptionPane.showMessageDialog(null, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Error desconocido.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    } catch (Exception e) {
        System.err.println("Error al analizar la respuesta del servidor: " + e.getMessage());
        JOptionPane.showMessageDialog(null, "Error al procesar la respuesta del servidor.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

 
 private void aceptarSolicitudAmistad(String receptor)
 {JSONObject json = new JSONObject();
    json.put("receptor", receptor);
    json.put("action", "ACCEPT_INVITATION_FRIEND");

      // Enviar la solicitud al servidor y esperar una respuesta
    PersistentClient client = PersistentClient.getInstance();
    String serverResponse = client.sendMessageAndWaitForResponse(json.toString());

    // Procesar la respuesta del servidor
    try {
        JSONObject response = new JSONObject(serverResponse);
        String status = response.getString("status");
        String message = response.getString("message");
        
       switch(status) {
    case "-2":
        JOptionPane.showMessageDialog(null, "Remitente no identificado.", "Error", JOptionPane.ERROR_MESSAGE);
        break;
    case "-3":
        JOptionPane.showMessageDialog(null, "Receptor no identificado.", "Error", JOptionPane.ERROR_MESSAGE);
        break;
    case "-4":
        JOptionPane.showMessageDialog(null, "Receptor no identificado.", "Error", JOptionPane.ERROR_MESSAGE);
        break;
    case "-5":
        JOptionPane.showMessageDialog(null, "Error al eliminar todos los mensajes y la amistad.", "Error", JOptionPane.ERROR_MESSAGE);
        break;
    case "-6":
        JOptionPane.showMessageDialog(null, "Error interno al eliminar todos los mensajes y la amistad.", "Error", JOptionPane.ERROR_MESSAGE);
        break;
    case "0":
        JOptionPane.showMessageDialog(null, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        break;
    default:
        JOptionPane.showMessageDialog(null, "Error desconocido.", "Error", JOptionPane.ERROR_MESSAGE);
        break;
}

        
    }catch(Exception ex)
    
    {
     
     
 }
      
     
 }
 
 private void obtenerSolicitudesEnviadas() {
    JSONObject json = new JSONObject();
    json.put("action", "GET_SEND_INVITATION_FRIEND");

    PersistentClient client = PersistentClient.getInstance();
    String serverResponse = client.sendMessageAndWaitForResponse(json.toString());
    JSONObject responseObject = new JSONObject(serverResponse);
    String status = responseObject.getString("status");

    switch (status) {
        case "-4":
            JOptionPane.showMessageDialog(null, "Remitente no identificado.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
        case "-5":
            JOptionPane.showMessageDialog(null, "Remitente no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
        case "-6":
            JOptionPane.showMessageDialog(null, "Error al obtener las solicitudes enviadas.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
        case "0":
            JSONArray sentInvitations = responseObject.optJSONArray("message");
            // Procesar las invitaciones enviadas (JSON array)
            solicitudesAmigosEnviadas.clear();
            for (int i = 0; i < sentInvitations.length(); i++) {
                String usuario = sentInvitations.getString(i);
                solicitudesAmigosEnviadas.add(new Usuario(usuario));
            }
            break;
        default:
            JOptionPane.showMessageDialog(null, "Error desconocido al obtener las solicitudes enviadas.", "Error", JOptionPane.ERROR_MESSAGE);
            break;
    }
}

 
 //--------------------------------------------------------------------------------------//
 
 // Método para cambiar la pestaña y actualizar la variable
private void showCard(String cardName) {
    cardLayout.show(panelPrincipal, cardName);
    CurrentVisibleCard = cardName; // Guardar la pestaña actual
}

// Método para restaurar la pestaña después de una actualización
private void restoreVisibleCard() {
    cardLayout.show(panelPrincipal, CurrentVisibleCard);
}
 
 
 

}

