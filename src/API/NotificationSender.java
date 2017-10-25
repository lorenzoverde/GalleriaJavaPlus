/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import DDI.DDI;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import java.math.BigDecimal;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Lorenzo
 */
public class NotificationSender extends Thread{
    
    private final static String HEADER_ONLY_KEY___FIREBASE_API_AUTHORIZATION = "Authorization";
    private final static String ADDRESS__FIREBASE_API_NOTIFICATION = "https://fcm.googleapis.com/fcm/send";
    private final static String SERVER_KEY__FIREBASE_API_NOTIFICATION = "AAAAQQvwsII:APA91bGHwmoBVRBExd1BqH8MFilcgxyrMtKGhC2tWuQYDn5VDFCkPT6DECiP93FijfEsUpih5WFvjxtnkPllHxJ1zINkFXmQlR9rkf-Ujgh6aOOZS3wmXA03KOK-dP2uZ4WCdvMAOIP7";

    @Override
    public void run() {
        sendFireNotifications();
    }
                  
    private  void sendFireNotifications(){
        
        List<String> tokens = DDI.getInstance().readEveryNotificationToken();        

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(ADDRESS__FIREBASE_API_NOTIFICATION);
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        invocationBuilder.header(HEADER_ONLY_KEY___FIREBASE_API_AUTHORIZATION, "key="+SERVER_KEY__FIREBASE_API_NOTIFICATION);
        for(String token : tokens){
            System.out.println("Invio notifica");
            Response response = invocationBuilder.post(Entity.json(createNotificationFor(token).toString()));
            System.out.println("Status invio notifica: "+response.getStatus());
        }

    }
    
    private JsonObject createNotificationFor(String token){
        JsonObjectBuilder builder = Json.createObjectBuilder().add("data", Json.createObjectBuilder().add("title", "Emergenza SmartTunnel").add("body", "E' scoppiato un incendio in galleria!")).add("to", token);
        return builder.build();
    }
}
