/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package API;

import APP.controlloAccesso.Permesso;
import java.security.Principal;

/**
 *
 * @author Lorenzo
 */
public class PermessoWeb implements Principal {

    public Permesso permit;
    public String username;
    public String password;
    
    @Override
    public String getName() {
        if(permit!=null)
            return permit.toString();
        else
            return null;
    }   
    
    public int getAuthenticationLevel(){
        if(permit == null)
            return 0;
        if(permit == Permesso.CONTROLLORE)
            return 1;
        if(permit == Permesso.OPERATORE)
            return 2;
        return -1;
    }
}
