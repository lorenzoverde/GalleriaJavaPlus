/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Lorenzo
 */
public class ExternalActionOnModelEvent extends EventObject{
    
    private static List<ExternalActionOnModelListener> listeners = new ArrayList();

    
    public ExternalActionOnModelEvent(Object source) {
        super(source);
    }
    
    public synchronized static void subscribe(ExternalActionOnModelListener listener){
        listeners.add(listener);
    }
    
    public synchronized static void unsubscribe(ExternalActionOnModelListener listener){
        listeners.remove(listener);
    }
    
    public void fire(){
        Iterator listener = listeners.iterator();
        while( listener.hasNext() ) {
            ( (ExternalActionOnModelListener) listener.next() ).externalActionRegistered();
        }
    }
    
}
