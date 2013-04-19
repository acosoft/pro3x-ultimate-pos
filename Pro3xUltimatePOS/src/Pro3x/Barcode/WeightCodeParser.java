/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.Barcode;

import Acosoft.Processing.DataBox.Roba;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author aco
 */
public class WeightCodeParser implements BarcodeParser
{
    private BarcodeControler controler;
    private String sifra = null;
    private double kolicina = 0;
    
    public WeightCodeParser()
    {
        
    }
    
    public void setup(BarcodeControler controler) 
    {
        this.controler = controler;
    }

    public boolean parse(String code) 
    {
        if(code.length() == 12 || code.length() == 13)
        {
            int wcode = Integer.parseInt(code.substring(0, 2));
            if(wcode >= 20 && wcode <= 28)
            {
                sifra = code.substring(2, 7);
                kolicina = Double.parseDouble(code.substring(7, 12));
                
                return true;
            }
        }
        
        return false;
    }

    public String getSifra() {
        return sifra;
    }
    
    public Double getKolicina()
    {
        return kolicina / 1000;
    }

    public boolean execute(boolean modeNormativi) 
    {
        for(Roba roba : controler.popisArtikala())
        {
            if(roba.getSifra().equals(sifra))
            {
                controler.dodajStavku(roba, kolicina / 1000, roba.getMaloprodajnaCijenaZaokruzena());
                return true;
            }
        }
        
        return false;
    }
    
}
