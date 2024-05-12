package ChatEmpresarial.client.pages;

import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.models.Grupo;
import ChatEmpresarial.shared.models.Usuario;
import ChatEmpresarial.client.pages.UsuarioFixCellRenderer;
import ChatEmpresarial.client.pages.GroupFixCellRenderer;
import ChatEmpresarial.client.utilities.SessionManager;
import ChatEmpresarial.shared.models.SolicitudAmistad;
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
    
    private DefaultTableModel modeloUsuariosConectados = new DefaultTableModel();
    private DefaultTableModel modeloUsuariosDesconectados = new DefaultTableModel();
    
    private DefaultTableModel modeloAmigosConectados = new DefaultTableModel();
    private DefaultTableModel modeloAmigosDesconectados = new DefaultTableModel();
    

    
    
    
    private Timer updateTimer;

    private String nombreUserActive;

    private ArrayList<Usuario> SolicitudAmistadRecibida = new ArrayList<>();
    private ArrayList<Usuario> SolicitudAmistadEnviada = new ArrayList<>();
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
    inicializarDatos();  // Llamada inicial para cargar datos antes de que el Timer comience
    configurarVentana();
    configurarNavegacion();
     configurarTimer();

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



    private void inicializarDatos() {
        
          
    String username = nombreUserActive;

    usuariosConectados.clear();
    usuariosDesconectados.clear();

    // Obtener los nombres de los usuarios desde el server

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
        
        obtenerSolicitudesRecibidas();
        modeloUsuariosConectados.setRowCount(0);
        

        for( Usuario us : usuariosConectados)
        {
            modeloUsuariosConectados.addRow(new Object[]{us.getNombre(), "+"});
        }
        
        
        modeloUsuariosDesconectados.setRowCount(0);
        
        for( Usuario us : usuariosDesconectados)
        {
            modeloUsuariosDesconectados.addRow(new Object[]{us.getNombre(), "+"});
        }
                
        


        for( Usuario us : usuariosConectados)
        {
            modeloUsuariosConectados.addRow(new Object[]{us.getNombre(), "+"});
        }
        
        
        modeloUsuariosDesconectados.setRowCount(0);
        
        for( Usuario us : usuariosDesconectados)
        {
            modeloUsuariosDesconectados.addRow(new Object[]{us.getNombre(), "+"});
        }
                
        
        //Amigos
        modeloAmigosConectados.setRowCount(0);
        
        for( Usuario us : amigosConectados)
        {
            modeloAmigosConectados.addRow(new Object[]{us.getNombre(), "-"});
        }
        
        

       
       modeloAmigosDesconectados.setRowCount(0);
       
        for( Usuario us : amigosDesconectados)
        {
            modeloAmigosDesconectados.addRow(new Object[]{us.getNombre(), "-"});
        }
       

        
        
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
    panel.add(crearListaUsuarios("Usuarios Conectados", usuariosConectados, true));
    panel.add(crearListaUsuarios("Usuarios Desconectados", usuariosDesconectados, false));
    return panel;
}



   
        
        

    private JScrollPane crearListaUsuarios(String titulo, ArrayList<Usuario> usuarios, boolean estaConectado) {
        
         

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
        panel.add(crearListaAmigos("Amigos Conectados", amigosConectados, true));
        panel.add(crearListaAmigos("Amigos Desconectados", amigosDesconectados, false));
        panel.add(crearListaSolicitudesAmigosRecibidas("Solicitudes Enviadas", solicitudesAmigosEnviadas ,true));
        panel.add(crearListaSolicitudesAmigosEnviadas("Solicitudes Recibidas", solicitudesAmigosRecibidas ,false));
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
    
     private JScrollPane crearListaSolicitudesAmigosRecibidas(String titulo, ArrayList<Usuario> elementos, boolean Envia) {
DefaultTableModel modelo = new DefaultTableModel()
        {
            @Override
            public boolean isCellEditable(int row, int column) {
            // Esto hará que ninguna celda sea editable
            return false;
            }
        };
        
        modelo.addColumn("nombre");
        modelo.addColumn("Agregar");
        modelo.addColumn("borrar");

        for( Usuario us : elementos)
        {
            modelo.addRow(new Object[]{us.getNombre(), "+","-"});
        }
        
        JTable lista = new JTable(modelo);
        
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
                    
                    
                    
                        if (column == 1) 
                        {
                            Usuario usuario = solicitudesAmigosEnviadas.get(row);
                            //AceptarSolicitudAmistad(usuario.getNombre());
                            //aqui falta la funcion >:)
                        }

                    
                        if (column == 2) 
                        {
                            Usuario usuario = solicitudesAmigosEnviadas.get(row);
                            //RechazarSolicitudAmistad(usuario.getNombre());
                            //aqui falta la funcion >:)
                        }
                    
                }
            }

        });
        

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setBorder(BorderFactory.createTitledBorder(titulo));
        return scrollPane;
    }
    
    
    
     private JScrollPane crearListaSolicitudesAmigosEnviadas(String titulo, ArrayList<Usuario> elementos, boolean Envia) {
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

        for( Usuario us : elementos)
        {
            modelo.addRow(new Object[]{us.getNombre(),"-"});
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
 
 
 


 
 

 
 
 //---------------------Metodos de control de peticiones -------------//
 
 private void cancelarSolicitudAmistad(String receptor) {
    JSONObject json = new JSONObject();
    json.put("receptor", receptor);
    json.put("action", "REFUSE_INVITATION_FRIEND");

    PersistentClient client = PersistentClient.getInstance();
    String serverResponse = client.sendMessageAndWaitForResponse(json.toString());

    switch (serverResponse) {
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

    switch (serverResponse) {
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

 
 
 
 
 
 
 

}