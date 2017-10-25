/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import APP.controlloAccesso.Permesso;
import APP.controlloIlluminazione.ControlloIlluminazione;
import APP.controlloIlluminazione.Criterio;
import APP.controlloPAI.ControlloPAI;
import APP.controlloTraffico.Circolazione;
import APP.controlloTraffico.ControlloTraffico;
import GUI.ExternalActionOnModelEvent;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


/**
 *
 * @author Lorenzo
 */
@Path("api/secured/status")
public class TunnelStatus {
        
    private final static String HEADER_ONLY_KEY__DESIRED_INFORMATIONS = "DesiredInfo";
    private final static String HEADER_ONLY_VALUE__DESIRED_INFORMATIONS_ALL = "all";
    private final static String VALUE__DESIRED_INFORMATIONS_TRAFFIC = "traffic";
    private final static String VALUE__DESIRED_INFORMATIONS_LIGHTING = "lighting";
    private final static String VALUE__DESIRED_INFORMATIONS_PAI = "pai";
      
    @GET
    @Produces("application/json")
    public String getStatus(@Context ContainerRequestContext crc) {
        JsonObjectBuilder json = Json.createObjectBuilder();
        
        String headers = crc.getHeaderString(HEADER_ONLY_KEY__DESIRED_INFORMATIONS);
        if(headers == null || headers == ""){
            return null;
        }
        
        List<String> desired_infos = Arrays.asList(headers.split(", "));
                
        if(desired_infos.contains(HEADER_ONLY_VALUE__DESIRED_INFORMATIONS_ALL)){
            retrieveEveryStatus(json);
        }
        else{
            if(desired_infos.contains(VALUE__DESIRED_INFORMATIONS_LIGHTING)){
                retrieveLightingStatus(json);
            }
            if(desired_infos.contains(VALUE__DESIRED_INFORMATIONS_TRAFFIC)){
                retrieveTrafficStatus(json);
            }
            if(desired_infos.contains(VALUE__DESIRED_INFORMATIONS_PAI)){
                retrievePaiStatus(json);
            }
        }
        JsonObject builtJson = json.build();
        if(builtJson.isEmpty()){
            return null;
        }
        String result = builtJson.toString();
        return result;
    }
    
    @POST
    @Path("update/"+ VALUE__DESIRED_INFORMATIONS_LIGHTING +"/"+ KEY__LIGHTING__CURRENT_CRITERION)
    @SetStatusFilterAnnotation
    public void setLightingCriterion(@Context ContainerRequestContext crc){
        String criterio = (String)crc.getProperty(SetStatusFilter.KEY__VALUE_TO_SET);
        ControlloIlluminazione.getInstance().provideCriterio(Criterio.valueOf(criterio));
        new ExternalActionOnModelEvent(this).fire();
    }
    
    @POST
    @Path("update/"+ VALUE__DESIRED_INFORMATIONS_LIGHTING +"/"+ KEY__LIGHTING__CONSTANT_CRITERION_VALUE)
    @SetStatusFilterAnnotation
    public void setLightingConstantCriterionValue(@Context ContainerRequestContext crc){
        int value = Integer.parseInt((String)crc.getProperty(SetStatusFilter.KEY__VALUE_TO_SET));
        ControlloIlluminazione.getInstance().setIntensitaCriterioCostante(value);
        new ExternalActionOnModelEvent(this).fire();
    }
    
    @POST
    @Path("update/"+ VALUE__DESIRED_INFORMATIONS_TRAFFIC +"/"+ KEY__TRAFFIC__CURRENT_CIRCULATION)
    @SetStatusFilterAnnotation
    public void setTrafficCirculation(@Context ContainerRequestContext crc){
        String circolazione = (String)crc.getProperty(SetStatusFilter.KEY__VALUE_TO_SET);   
        ControlloTraffico.getInstance().setCircolazione(Circolazione.valueOf(circolazione));
        new ExternalActionOnModelEvent(this).fire();
    }
    
    @POST
    @Path("update/"+ VALUE__DESIRED_INFORMATIONS_TRAFFIC +"/"+ KEY__TRAFFIC__LEFT_GREEN_DURATION)
    @SetStatusFilterAnnotation
    public void setTrafficLeftGreenDuration(@Context ContainerRequestContext crc){
        int value = Integer.parseInt((String)crc.getProperty(SetStatusFilter.KEY__VALUE_TO_SET));
        ControlloTraffico.getInstance().setDurataVerdeSXRossoDX(value);
        new ExternalActionOnModelEvent(this).fire();
    }
        
    @POST
    @Path("update/"+ VALUE__DESIRED_INFORMATIONS_TRAFFIC +"/"+ KEY__TRAFFIC__RIGHT_GREEN_DURATION)
    @SetStatusFilterAnnotation
    public void setTrafficRightGreenDuration(@Context ContainerRequestContext crc){
        int value = Integer.parseInt((String)crc.getProperty(SetStatusFilter.KEY__VALUE_TO_SET));
        ControlloTraffico.getInstance().setDurataVerdeDXRossoSX(value);
        new ExternalActionOnModelEvent(this).fire();
    }
    
    @POST
    @Path("update/"+ VALUE__DESIRED_INFORMATIONS_TRAFFIC +"/"+ KEY__TRAFFIC__ADDITIONAL_RED_DURATION)
    @SetStatusFilterAnnotation
    public void setTrafficAdditionalRedDuration(@Context ContainerRequestContext crc){
        int value = Integer.parseInt((String)crc.getProperty(SetStatusFilter.KEY__VALUE_TO_SET));
        ControlloTraffico.getInstance().setDurataRossoAggiuntiva(value);
        new ExternalActionOnModelEvent(this).fire();
    }
    
    @POST
    @Path("update/"+ VALUE__DESIRED_INFORMATIONS_PAI +"/"+ KEY__PAI__IS_ACTIVE)
    @SetStatusFilterAnnotation
    public void setPaiIsActive(@Context ContainerRequestContext crc){
        PermessoWeb permesso = (PermessoWeb)crc.getSecurityContext().getUserPrincipal();
        boolean value = Boolean.parseBoolean((String)crc.getProperty(SetStatusFilter.KEY__VALUE_TO_SET));
        if(!value)
            ControlloPAI.getInstance().disattivaPAI(permesso.username);
        new ExternalActionOnModelEvent(this).fire();
    }
    
    private final static String KEY__LIGHTING__LEDS_VALUES = "ledsValues";
    public final static String KEY__LIGHTING__CURRENT_CRITERION = "currentCriterion";
    public final static String KEY__LIGHTING__CONSTANT_CRITERION_VALUE = "constantCriterionValue";
    
    private static void retrieveLightingStatus(JsonObjectBuilder outerJson){
        JsonObjectBuilder json = Json.createObjectBuilder();
        
        ControlloIlluminazione controller = ControlloIlluminazione.getInstance();
        
        int[] leds = controller.getIntensitaLED();
        JsonArrayBuilder jsonLedsArray = Json.createArrayBuilder();
        for(int i =0; i<leds.length; i++)
            jsonLedsArray.add(leds[i]);
        
        json.add(KEY__LIGHTING__LEDS_VALUES, jsonLedsArray);
        json.add(KEY__LIGHTING__CONSTANT_CRITERION_VALUE, controller.getIntensitaCriterioCostante());
        json.add(KEY__LIGHTING__CURRENT_CRITERION, controller.obtainCriterio().toString());
        
        outerJson.add(VALUE__DESIRED_INFORMATIONS_LIGHTING, json);
    }
    
    private final static String KEY__TRAFFIC__IS_LEFT_GREEN = "isLeftTrafficLightGreen";
    private final static String KEY__TRAFFIC__IS_RIGHT_GREEN = "isRightTrafficLightGreen";
    public final static String KEY__TRAFFIC__CURRENT_CIRCULATION = "currentCirculation";
    public final static String KEY__TRAFFIC__LEFT_GREEN_DURATION = "leftGreenDuration";
    public final static String KEY__TRAFFIC__RIGHT_GREEN_DURATION = "rightGreenDuration";
    public final static String KEY__TRAFFIC__ADDITIONAL_RED_DURATION = "additionalRedDuration";
    
    private static void retrieveTrafficStatus(JsonObjectBuilder outerJson){
        JsonObjectBuilder json = Json.createObjectBuilder();
        
        ControlloTraffico controller = ControlloTraffico.getInstance();
        
        json.add(KEY__TRAFFIC__IS_LEFT_GREEN, controller.isSmf1Verde());
        json.add(KEY__TRAFFIC__IS_RIGHT_GREEN, controller.isSmf2Verde());
        json.add(KEY__TRAFFIC__CURRENT_CIRCULATION, controller.getCircolazione().toString());
        json.add(KEY__TRAFFIC__LEFT_GREEN_DURATION, controller.getDurataVerdeSXRossoDX());
        json.add(KEY__TRAFFIC__RIGHT_GREEN_DURATION, controller.getDurataVerdeDXRossoSX());
        json.add(KEY__TRAFFIC__ADDITIONAL_RED_DURATION, controller.getDurataRossoAggiuntiva());
        
        outerJson.add(VALUE__DESIRED_INFORMATIONS_TRAFFIC, json);
    }
    
    public final static String KEY__PAI__IS_ACTIVE = "isActive";
    private final static String KEY__PAI__IS_TEMPERATURE_HIGH = "isTemperatureHigh";
    
    private static void retrievePaiStatus(JsonObjectBuilder outerJson){
        JsonObjectBuilder json = Json.createObjectBuilder();
        
        ControlloPAI controller = ControlloPAI.getInstance();
        
        json.add(KEY__PAI__IS_ACTIVE, controller.isPAIAttiva());
        json.add(KEY__PAI__IS_TEMPERATURE_HIGH, controller.isTemperaturaAlta());
        
        outerJson.add(VALUE__DESIRED_INFORMATIONS_PAI, json);
    }
  
    private static void retrieveEveryStatus(JsonObjectBuilder json){
        retrieveLightingStatus(json);
        retrieveTrafficStatus(json);
        retrievePaiStatus(json);
    }
}
