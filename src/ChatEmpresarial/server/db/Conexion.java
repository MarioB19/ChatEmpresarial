/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author brand
 */

public class Conexion {
    
    private Connection con;
    
    public Conexion()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
        } catch (ClassNotFoundException ex) {
            
            
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            
            
        }
        
          try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_empresarial", "root", "");


        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
      public Connection getCon() {
        return con;
    }
                
                         
    
}


