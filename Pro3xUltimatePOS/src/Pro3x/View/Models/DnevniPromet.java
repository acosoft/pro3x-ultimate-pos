/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View.Models;

import Acosoft.Processing.DataBox.Racun;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author aco
 */
public class DnevniPromet 
{
    private List<SegmentPrometa> segmenti = new ArrayList<SegmentPrometa>();
    private SegmentPrometa trenutni;
    
    private boolean isGotovinski(Racun r)
    {
        return r.getTemplate().getTip().equals("Gotovinska uplata");
    }
    
    public void dodaj(Racun r)
    {
        if(r.getStorniran() == null)
        {
            if(trenutni == null)
            {
                trenutni = new SegmentPrometa(isGotovinski(r));
                segmenti.add(trenutni);
            }
            else if(isGotovinski(r) != trenutni.isGotovinski())
            {
                trenutni = new SegmentPrometa(isGotovinski(r));
                segmenti.add(trenutni);
            }

            trenutni.dodaj(r);
        }
    }

    public List<SegmentPrometa> getSegmenti() {
        return segmenti;
    }
}
