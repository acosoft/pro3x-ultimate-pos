/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View.Models;

import Acosoft.Processing.DataBox.Roba;
import java.util.List;

/**
 *
 * @author aco
 */
public class StanjeArtikla {
    
    private Roba roba;
    private Double stanje;

    public StanjeArtikla(Object item) {
        
        Object[] data = (Object[]) item;
        roba = (Roba) data[0];
        
        Double ulaz = (Double) data[1];
        Double izlaz = (Double) data[2];
        
        stanje = ulaz - izlaz;
    }
    
    public String getMjera()
    {
        return roba.getMjera();
    }
    
    public Double getStanje()
    {
        return stanje;
    }
    
    public String getNaziv()
    {
        return roba.getNaziv();
    }
    
    public Double getCijena()
    {
        return roba.getCijena();
    }
    
    public Double getUkupno()
    {
        return getCijena() * getStanje();
    }
}
