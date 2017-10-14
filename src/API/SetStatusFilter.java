/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import APP.controlloAccesso.Permesso;
import APP.controlloIlluminazione.Criterio;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.annotation.Priority;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Priority(2)
@Provider
@SetStatusFilterAnnotation
public class SetStatusFilter implements ContainerRequestFilter {

    public static final String VALUE_HEADER_KEY = "Value";
    
    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
        PermessoWeb permesso = (PermessoWeb)crc.getSecurityContext().getUserPrincipal();
        if(permesso.permit!=Permesso.OPERATORE){
            crc.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        
        List<String> valueKeyValue = crc.getHeaders().get(VALUE_HEADER_KEY);
        if(valueKeyValue==null || valueKeyValue.size()!=1){
            crc.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
            return;
        }
        String value = valueKeyValue.get(0);
                
        if(crc.getUriInfo().getPath().contains("update/lighting")){
            // Controlli constraints Illuminazione
            if(crc.getUriInfo().getPath().contains(TunnelStatus.CURRENT_CRITERION_JSON_KEY)){
                if(!value.equals(Criterio.COSTANTE_MANUALE.toString()) && !value.equals(Criterio.DINAMICO.toString())){
                    crc.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
                }
            } else if(crc.getUriInfo().getPath().contains(TunnelStatus.CONSTANT_CRITERION_VALUE_JSON_KEY)){
                
            }
        }
        else if(crc.getUriInfo().getPath().contains("update/traffic")){
            // Controlli constraints Traffico
            if(crc.getUriInfo().getPath().contains(TunnelStatus.CURRENT_CIRCULATION_JSON_KEY)){
                
            } else if(crc.getUriInfo().getPath().contains(TunnelStatus.LEFT_TRAFFIC_LIGHT_GREEN_DURATION_JSON_KEY)){
                
            } else if(crc.getUriInfo().getPath().contains(TunnelStatus.RIGHT_TRAFFIC_LIGHT_GREEN_DURATION_JSON_KEY)){
                
            } else if(crc.getUriInfo().getPath().contains(TunnelStatus.ADDITIONAL_RED_DURATION_JSON_KEY)){
                
            }
        }
        else if(crc.getUriInfo().getPath().contains("update/pai")){
            // Controlli constraints PAI
            if(crc.getUriInfo().getPath().contains(TunnelStatus.IS_PAI_ACTIVE)){
                
            }
        }
        else{
            crc.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        
        crc.setProperty(VALUE_HEADER_KEY, value);
    }
    
    
    
}
