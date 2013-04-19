/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View.Models;

import Acosoft.Processing.DataBox.Racun;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author aco
 */
public class SegmentPrometa {
    
    private Racun min = null;
    private Racun max = null;
    private double ukupno = 0D;
    
    private List<Racun> racuni = new ArrayList<Racun>();
    
    private boolean gotovinski;

    public SegmentPrometa() {
        this(true);
    }

    public SegmentPrometa(boolean gotovinski) {
        this.gotovinski = gotovinski;
    }
    
    public Date getDatum()
    {
        return getMin().getIzdan();
    }

    public boolean isGotovinski() {
        return gotovinski;
    }

    public Racun getMax() {
        return max;
    }

    public Racun getMin() {
        return min;
    }

    public void dodaj(Racun r)
    {
        if(min == null)
        {
            min = r;
        }
        else if(min.getIzdan().after(r.getIzdan()))
        {
            min = r;
        }
        
        if(max == null)
        {
            max = r;
        }
        else if(max.getIzdan().before(r.getIzdan()))
        {
            max = r;
        }
        
        racuni.add(r);
        ukupno += r.getUkupno();
    }
    
    public String getVrsta()
    {
        if(isGotovinski())
        {
            return "Gotovinski";
        }
        else
        {
            return "Transakcijski";
        }
    }
    
    public double getUkupno()
    {
        return ukupno;
    }
}
