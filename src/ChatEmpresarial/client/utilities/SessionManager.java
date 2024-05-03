/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.client.utilities;

/**
 *
 * @author aguil
 */
public class SessionManager {
    private static SessionManager instance = null;
    private String username;
    private boolean isLoggedIn = false;

    // Constructor privado para prevenir la instancia desde fuera de la clase
    private SessionManager() {}

    // Método estático para obtener la instancia
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(String username) {
        this.username = username;
        this.isLoggedIn = true;
    }

    public void logout() {
        this.username = null;
        this.isLoggedIn = false;
    }

    public String getUsername() {
        return username;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
