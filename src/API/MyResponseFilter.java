/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;


/**
 *
 * @author Lorenzo
 */
@Provider
public class MyResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext creq, ContainerResponseContext cresp){
        cresp.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
        cresp.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
        cresp.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS, HEAD");        
        if(!creq.getMethod().equals("OPTIONS"))
            return;
        List customHeaders = new ArrayList<String>();
        customHeaders.add("Authorization");
        customHeaders.add("DesiredInfo");
        customHeaders.add("Value");
        customHeaders.add("NotificationToken");
        cresp.getHeaders().put("Access-Control-Allow-Headers", customHeaders);
    }

    
}