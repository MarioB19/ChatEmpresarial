/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.server.controllers;

import ChatEmpresarial.server.db.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aguil
 */
public class LoginController extends Conexion {
    
    
    
    
    
    //Método para iniciar sesión
    
    public static String Logging(String username, String password)
    {   
        
          Conexion conexion = new Conexion(); // Create a new instance to use the connection
        Connection con = conexion.getCon();
        
        if(username.length() == 0 || username == null){
            return "-1";
            
        }
        
         PreparedStatement sql;
        //Realizar query para obtener el nombre de usuairo
        try {
            sql = con.prepareStatement("SELECT nombre FROM usuario WHERE(nombre=? && contrasena=?)");
        
        
        sql.setString(1, username);
        sql.setString(2, password);
        sql.executeQuery();
        
          ResultSet r;
        r = sql.executeQuery();
        
        
        r.next();
             String foundUsername = r.getString("nombre");
          
             return foundUsername;
        
        //Retornar el nombre de usuario
        
      
                
        }
        catch (SQLException ex) {
              
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
             return "-1";
        }
   
          
     
      
    }    
        
    }
    
    
    
