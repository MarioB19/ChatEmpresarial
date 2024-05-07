package ChatEmpresarial.client.pages;

import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.models.Grupo;
import ChatEmpresarial.shared.models.Usuario;
import ChatEmpresarial.client.pages.UsuarioFixCellRenderer;
import ChatEmpresarial.client.pages.GroupFixCellRenderer;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class ChatList extends JFrame {
    
       private DefaultListModel<Usuario> modeloUsuariosConectados = new DefaultListModel<>();
    private DefaultListModel<Usuario> modeloUsuariosDesconectados = new DefaultListModel<>();
    
    private DefaultListModel<Usuario> modeloAmigosConectados = new DefaultListModel<>();
    private DefaultListModel<Usuario> modeloAmigosDesconectados = new DefaultListModel<>();
    
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
        
        
        modeloUsuariosConectados.clear();
        
        usuariosConectados.forEach(modeloUsuariosConectados::addElement);
        
 
        
        modeloUsuariosDesconectados.clear();
        usuariosDesconectados.forEach(modeloUsuariosDesconectados::addElement);
        
        //Amigos
        modeloAmigosConectados.clear();
       amigosConectados.forEach(modeloAmigosConectados::addElement);
       
       
       modeloAmigosDesconectados.clear();
      amigosDesconectados.forEach(modeloAmigosDesconectados::addElement);
       
        
        
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


    private JScrollPane crearListaUsuarios(String titulo, ArrayList<Usuario> usuarios, boolean estaConectado ) {
        
        
        DefaultTableModel modelo = new DefaultTableModel()
        {
            @Override
            public boolean isCellEditable(int row, int column) {
            // Esto hará que ninguna celda sea editable
            return false;
            }
        };
        
        modelo.addColumn("nombre");
        modelo.addColumn("borrar");
        for(Usuario us : usuarios)
        {
            modelo.addRow(new Object[]{us.getNombre(), "+"});
        }
        
        JTable lista = new JTable(modelo);
        
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
                            Usuario usuario = (Usuario) lista.getModel().getValueAt(row, 0);

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
                    
                        if (column == 1) 
                        {
                            Usuario usuario = (Usuario) lista.getModel().getValueAt(row, 0);
                            String nombre = usuario.getNombre();
                            
                            System.out.println(nombre);
                            
                                    
                        }
                }
            }

        });
        

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        
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
    */
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
        
        DefaultTableModel modelo = new DefaultTableModel()
        {
            @Override
            public boolean isCellEditable(int row, int column) {
            // Esto hará que ninguna celda sea editable
            return false;
            }
        };
        
        modelo.addColumn("nombre");
        modelo.addColumn("borrar");

        for( Usuario us : amigos)
        {
            modelo.addRow(new Object[]{us.getNombre(), "-"});
        }
        
        JTable lista = new JTable(modelo);
        
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
                        yupi();
                    }
                }
            }

        });
        

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        return scrollPane;
    }
    
    void yupi()
    {
        System.out.println("lolazo");
    }
    
    public class ButtonRenderer extends JButton implements TableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
        
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
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(crearListaGrupos("Grupos", grupos, false));
        panel.add(crearListaSolicitudesGrupo("Solicitudes de Grupos", solicitudesGrupos, false));
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
            JSONArray amigosArray = responseJson.getJSONArray("connectedFriends");
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
 
 
 


 
 

    
  
}
        
