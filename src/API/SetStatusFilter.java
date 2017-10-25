/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import APP.controlloAccesso.Permesso;
import APP.controlloIlluminazione.Criterio;
import APP.controlloPAI.ControlloPAI;
import APP.controlloTraffico.Circolazione;
import java.io.IOException;
import java.util.List;
import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Priority(2)
@Provider
@SetStatusFilterAnnotation
public class SetStatusFilter implements ContainerRequestFilter {

    public static final String KEY__VALUE_TO_SET = "Value";
    
    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
        PermessoWeb permesso = (PermessoWeb)crc.getSecurityContext().getUserPrincipal();
        if(permesso.permit!=Permesso.OPERATORE){
            crc.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        
        List<String> valueKeyValue = crc.getHeaders().get(KEY__VALUE_TO_SET);
        if(valueKeyValue==null || valueKeyValue.size()!=1){
            crc.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
            return;
        }
        String value = valueKeyValue.get(0);
          
        
        try{
            if(!crc.getUriInfo().getPath().contains("update/pai") && ControlloPAI.getInstance().isPAIAttiva()){
                crc.abortWith(Response.status(Response.Status.FORBIDDEN).build());
                return;
            }
            if(crc.getUriInfo().getPath().contains("update/lighting")){
                // Controlli constraints Illuminazione
                if(crc.getUriInfo().getPath().contains(TunnelStatus.KEY__LIGHTING__CURRENT_CRITERION)){
                    if(!value.equals(Criterio.COSTANTE_MANUALE.toString()) && !value.equals(Criterio.DINAMICO.toString())){
                        crc.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
                    }
                } else if(crc.getUriInfo().getPath().contains(TunnelStatus.KEY__LIGHTING__CONSTANT_CRITERION_VALUE)){
                    int parsedValue = Integer.parseInt(value);
                    if(parsedValue <0 || parsedValue > 255)
                        crc.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
                }
            }
            else if(crc.getUriInfo().getPath().contains("update/traffic")){
                // Controlli constraints Traffico
                if(crc.getUriInfo().getPath().contains(TunnelStatus.KEY__TRAFFIC__CURRENT_CIRCULATION)){
                    if( !value.equals(Circolazione.DOPPIO_SENSO.toString()) && 
                        !value.equals(Circolazione.INTERDETTA.toString()) &&
                        !value.equals(Circolazione.SENSO_UNICO_DX.toString()) &&
                        !value.equals(Circolazione.SENSO_UNICO_SX.toString()) &&
                        !value.equals(Circolazione.SENSO_UNICO_ALTER.toString())){

                        crc.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
                    }
                } else if(crc.getUriInfo().getPath().contains(TunnelStatus.KEY__TRAFFIC__LEFT_GREEN_DURATION)){
                    int parsedValue = Integer.parseInt(value);
                    if(parsedValue < 1 || parsedValue > 90)
                        crc.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
                } else if(crc.getUriInfo().getPath().contains(TunnelStatus.KEY__TRAFFIC__RIGHT_GREEN_DURATION)){
                    int parsedValue = Integer.parseInt(value);
                    if(parsedValue < 1 || parsedValue > 90)
                        crc.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
                } else if(crc.getUriInfo().getPath().contains(TunnelStatus.KEY__TRAFFIC__ADDITIONAL_RED_DURATION)){
                    int parsedValue = Integer.parseInt(value);
                    if(parsedValue < 1 || parsedValue > 90)
                        crc.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
                }
            }
            else if(crc.getUriInfo().getPath().contains("update/pai")){
                // Controlli constraints PAI
                if(crc.getUriInfo().getPath().contains(TunnelStatus.KEY__PAI__IS_ACTIVE)){
                    if( !value.equals("true") && !value.equals("false")){
                        crc.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
                    }
                }
            }
            else{
                crc.abortWith(Response.status(Response.Status.NOT_FOUND).build());
                return;
            }
        }catch(RuntimeException e){
            crc.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
        }
        //****  La richiesta risulta valida e passa alla risorsa  ****\\
        
        crc.setProperty(KEY__VALUE_TO_SET, value);
    }
       
}
