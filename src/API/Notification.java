/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import DDI.DDI;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;

/**
 * Notifiche.
 * Ad oggi, in realtà, non sono effettuati rigidi controlli sul fatto che la chiamata alla risorsa
 * utizzi in modo appropriato gli header di notifica. Nel peggiore dei casi ad una richiesta malformata o
 * di subscribe non viene registrato il token (se è ben formata ma il token è finto, verrà registrato un finto token),
 * viceversa ad una di subscribe non viene eliminato.
 * @author Lorenzo
 */
@Path("api/secured/notification")
public class Notification {
    private final static String HEADER_ONLY_KEY___NOTIFICATION_TOKEN = "NotificationToken";   
    
    @POST
    @Path("subscribe")
    public void subscribe(@Context ContainerRequestContext crc) {
        // Registra il token di notifica nel DB. Leggi nota risora.
        String token = getTokenFromHeaders(crc);
        if(!(token == null || token.equals("")))
            DDI.getInstance().writeNewNotificationToken(token);            
    }
    
    
    @POST
    @Path("unsubscribe")
    public void unSubscribe(@Context ContainerRequestContext crc) {
        // Elimina il token di notifica del DB. Leggi nota risora.
        String token = getTokenFromHeaders(crc);
        if(token == null || token.equals(""))
            return;
        DDI.getInstance().deleteNotificationToken(token);     
    }
    
    private String getTokenFromHeaders(ContainerRequestContext crc){
        String headers = crc.getHeaderString(HEADER_ONLY_KEY___NOTIFICATION_TOKEN);
        if(headers==null)
            return null;
        List<String> tokens = Arrays.asList(headers.split(", "));
        if(tokens == null || tokens.size()!=1)
            return null;
        return tokens.get(0);
    }
    
}
