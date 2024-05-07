/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatEmpresarial.client.utilities;
import ChatEmpresarial.client.conection.PersistentClient;
import ChatEmpresarial.shared.models.Usuario;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author brunosanchezpadilla
 */
public class GetUsers {
    




public static ArrayList<Usuario> getUsers() {
    ArrayList<Usuario> users = new ArrayList<>();

    try {
        JSONObject request = new JSONObject();
        request.put("action", "GETUSERS");  // Define the action type according to your server-side handling

        PersistentClient client = PersistentClient.getInstance();
        String response = client.sendMessageAndWaitForResponse(request.toString());

        JSONObject jsonResponse = new JSONObject(response);
        String status = jsonResponse.getString("status");

        switch (status) {
            case "0":  // Success
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


    
}
