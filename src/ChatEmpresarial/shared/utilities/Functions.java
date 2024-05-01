/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.shared.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author brand
 */
public class Functions {
    
     public static String toSHA256(String input) {
        try {
            // Obtener una instancia de MessageDigest que realiza el hash SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Convertir la cadena de entrada en bytes, calcular el hash y almacenar el resultado
            byte[] hashBytes = digest.digest(input.getBytes());
            
            // Convertir el array de bytes en una representación hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // En caso de que el algoritmo SHA-256 no esté disponible
            throw new RuntimeException("Error: SHA-256 algorithm not found.", e);
        }
    }

    
}
