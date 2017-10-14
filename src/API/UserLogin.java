/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author Lorenzo
 */
@Path("api/secured/login")
public class UserLogin {
    
    private final static String AUTHENTICATION_LEVEL_JSON_KEY = "authLvl";
    
    @GET
    @Produces("application/json")
    public String loginCheck(@Context SecurityContext securityContext) {
        PermessoWeb permesso = (PermessoWeb)securityContext.getUserPrincipal();
        JsonObject json = Json.createObjectBuilder()
        .add(AUTHENTICATION_LEVEL_JSON_KEY, permesso.getAuthenticationLevel()).build();
        String result = json.toString();
        return result;
    }
}
