/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import APP.controlloAccesso.ControlloAccesso;
import APP.controlloAccesso.Permesso;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.StringTokenizer;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Lorenzo
 */
@Provider
public class SecurityFilter implements ContainerRequestFilter {

    private static String SECURED_URL_PREFIX = "secured";
    private static String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static String AUTHORIZATION_LEVEL_PROPERTY_KEY = "AuthorizationLevel";
    
    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
        if(!crc.getUriInfo().getPath().contains(SECURED_URL_PREFIX))
            return;
        Response resp = null;
        List<String> auth = crc.getHeaders().get(AUTHORIZATION_HEADER_KEY);
        if(auth==null || auth.size()<=0){
            resp = Response.status(Response.Status.UNAUTHORIZED).build();
        } else {
            StringTokenizer st = new StringTokenizer(auth.get(0), ":");
            String username = st.nextToken(), password = st.nextToken();        
            Permesso perm = ControlloAccesso.getInstance().controllaLogin(username, password);

            if(perm == null)
                resp = Response.status(Response.Status.UNAUTHORIZED).build();
            else{
                final PermessoWeb permessoWeb = new PermessoWeb();
                permessoWeb.permit = perm;
                crc.setSecurityContext( new SecurityContext() {
                    @Override
                    public boolean isUserInRole(String role) {
                        return role.equals(permessoWeb.getName());
                    }
                    @Override
                    public boolean isSecure() {
                        return false; // check HTTPS
                    }
                    @Override
                    public String getAuthenticationScheme() {
                        return null; // ...
                    }
                    @Override
                    public Principal getUserPrincipal() {
                        return permessoWeb;
                    }
                });
                
            }
        }
        if(resp != null)
            crc.abortWith(resp);
    }
}
