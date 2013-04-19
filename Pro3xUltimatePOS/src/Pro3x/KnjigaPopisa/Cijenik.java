/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Pro3x.KnjigaPopisa;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

/**
 *
 * @author Aco
 */
public class Cijenik extends LinkedList<CijenaArtikla>
{
    public void SortirajCijenik()
    {
        Collections.sort(this, new Comparator<CijenaArtikla>()
        {
            public int compare(CijenaArtikla o1, CijenaArtikla o2)
            {
                //obrnuto sortiranje po datumu
                return -o1.getDatum().compareTo(o2.getDatum());
            }
        });
    }

    public Double IzracunajCijenu(Date datum)
    {

        for(CijenaArtikla cijena : this)
        {
            if(cijena.getDatum().compareTo(datum) >= 0)
                return cijena.getCijena();
        }

        return 0D;
    }
}
