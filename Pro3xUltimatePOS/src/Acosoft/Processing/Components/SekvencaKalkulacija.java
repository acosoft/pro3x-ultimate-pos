/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Acosoft.Processing.Components;

import Acosoft.Processing.Pro3App;

/**
 *
 * @author aco
 */
public class SekvencaKalkulacija extends InfoSekvenca {

    @Override
    public void spremi() {
        Pro3App.getApplication().SpremiSekvencuKalkulacija();
    }
    
    
    
}
