/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import APP.controlloIlluminazione.ControlloIlluminazione;
import APP.controlloPAI.ControlloPAI;
import APP.controlloTraffico.ControlloTraffico;
import java.math.BigDecimal;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;


/**
 *
 * @author Lorenzo
 */
@Path("api/secured")
public class TunnelStatus {
    
    private final static String AUTHENTICATION_LEVEL_JSON_KEY = "authLvl";
    
    private final static String DESIRED_INFORMATIONS_HEADER_KEY = "DesiredInfo";
    private final static String DESIRED_INFORMATIONS_HEADER_VALUE_ALL = "all";
    private final static String DESIRED_INFORMATIONS_HEADER_VALUE_TRAFFIC = "traffic";
    private final static String DESIRED_INFORMATIONS_HEADER_VALUE_LIGHTING = "lighting";
    private final static String DESIRED_INFORMATIONS_HEADER_VALUE_PAI = "pai";
    
    @GET
    @Path("login")
    @Produces("application/json")
    public String loginCheck(@Context SecurityContext securityContext) {
        PermessoWeb permesso = (PermessoWeb)securityContext.getUserPrincipal();
        JsonObject json = Json.createObjectBuilder()
        .add(AUTHENTICATION_LEVEL_JSON_KEY, permesso.getAuthenticationLevel()).build();
        String result = json.toString();
        return result;
    }
    
    @GET
    @Path("status")
    @Produces("application/json")
    public String getStatus(@Context ContainerRequestContext crc) {
        JsonObjectBuilder json = Json.createObjectBuilder();
        
        List<String> desired_infos = crc.getHeaders().get(DESIRED_INFORMATIONS_HEADER_KEY);
        
        //System.out.println("Richieste remote: "+desired_infos.toString());
                
        if(desired_infos.contains(DESIRED_INFORMATIONS_HEADER_VALUE_ALL)){
            retrieveEveryStatus(json);
        }
        else{
            if(desired_infos.contains(DESIRED_INFORMATIONS_HEADER_VALUE_LIGHTING)){
                retrieveLightingStatus(json);
            }
            if(desired_infos.contains(DESIRED_INFORMATIONS_HEADER_VALUE_TRAFFIC)){
                retrieveTrafficStatus(json);
            }
            if(desired_infos.contains(DESIRED_INFORMATIONS_HEADER_VALUE_PAI)){
                retrievePaiStatus(json);
            }
        }
        String result = json.build().toString();
        return result;
    }
    
    private final static String LEDS_VALUES_JSON_KEY = "ledsValues";
    private final static String CURRENT_CRITERION_JSON_KEY = "currentCriterion";
    private final static String CONSTANT_CRITERION_VALUE_JSON_KEY = "constantCriterionValue";
    
    private static void retrieveLightingStatus(JsonObjectBuilder outerJson){
        JsonObjectBuilder json = Json.createObjectBuilder();
        
        ControlloIlluminazione controller = ControlloIlluminazione.getInstance();
        
        int[] leds = controller.getIntensitaLED();
        JsonArrayBuilder jsonLedsArray = Json.createArrayBuilder();
        for(int i =0; i<leds.length; i++)
            jsonLedsArray.add(leds[i]);
        
        json.add(LEDS_VALUES_JSON_KEY, jsonLedsArray);
        json.add(CONSTANT_CRITERION_VALUE_JSON_KEY, controller.getIntensitaCriterioCostante());
        json.add(CURRENT_CRITERION_JSON_KEY, controller.obtainCriterio().toString());
        
        outerJson.add(DESIRED_INFORMATIONS_HEADER_VALUE_LIGHTING, json);
    }
    
    private final static String IS_LEFT_TRAFFIC_LIGHT_GREEN_JSON_KEY = "isLeftTrafficLightGreen";
    private final static String IS_RIGHT_TRAFFIC_LIGHT_GREEN_JSON_KEY = "isRightTrafficLightGreen";
    private final static String CURRENT_CIRCULATION_JSON_KEY = "currentCirculation";
    private final static String LEFT_TRAFFIC_LIGHT_GREEN_DURATION_JSON_KEY = "leftTrafficLightGreenDuration";
    private final static String RIGHT_TRAFFIC_LIGHT_GREEN_DURATION_JSON_KEY = "rightTrafficLightGreenDuration";
    private final static String ADDITIONAL_RED_DURATION_JSON_KEY = "additionalRedDuration";
    
    private static void retrieveTrafficStatus(JsonObjectBuilder outerJson){
        JsonObjectBuilder json = Json.createObjectBuilder();
        
        ControlloTraffico controller = ControlloTraffico.getInstance();
        
        json.add(IS_LEFT_TRAFFIC_LIGHT_GREEN_JSON_KEY, controller.isSmf1Verde());
        json.add(IS_RIGHT_TRAFFIC_LIGHT_GREEN_JSON_KEY, controller.isSmf2Verde());
        json.add(CURRENT_CIRCULATION_JSON_KEY, controller.getCircolazione().toString());
        json.add(LEFT_TRAFFIC_LIGHT_GREEN_DURATION_JSON_KEY, controller.getDurataVerdeSXRossoDX());
        json.add(RIGHT_TRAFFIC_LIGHT_GREEN_DURATION_JSON_KEY, controller.getDurataVerdeDXRossoSX());
        json.add(ADDITIONAL_RED_DURATION_JSON_KEY, controller.getDurataRossoAggiuntiva());
        
        outerJson.add(DESIRED_INFORMATIONS_HEADER_VALUE_TRAFFIC, json);
    }
    
    private final static String IS_PAI_ACTIVE = "isPaiActive";
    private final static String IS_TEMPERATURE_HIGH = "isTemperatureHigh";
    
    private static void retrievePaiStatus(JsonObjectBuilder outerJson){
        JsonObjectBuilder json = Json.createObjectBuilder();
        
        ControlloPAI controller = ControlloPAI.getInstance();
        
        json.add(IS_PAI_ACTIVE, controller.isPAIAttiva());
        json.add(IS_TEMPERATURE_HIGH, controller.isTemperaturaAlta());
        
        outerJson.add(DESIRED_INFORMATIONS_HEADER_VALUE_PAI, json);
    }
    
    private static void retrieveEveryStatus(JsonObjectBuilder json){
        retrieveLightingStatus(json);
        retrieveTrafficStatus(json);
        retrievePaiStatus(json);
    }
}
