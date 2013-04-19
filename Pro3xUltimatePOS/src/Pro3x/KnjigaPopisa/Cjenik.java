/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Pro3x.KnjigaPopisa;

import java.util.Date;
import java.util.LinkedList;

/**
 *
 * @author Aco
 */
public class Cjenik extends LinkedList<CijenaArtikla>
{
    private void SortirajCijenik()
    {

    }

    public Double IzracunajCijenu(Date datum)
    {
        for(CijenaArtikla cijena : this)
        {
            if(cijena.getDatum().compareTo(datum) <= 0)
                return cijena.getCijena();
        }

        if(this.size() > 0)
            return this.getFirst().getCijena();
        else
            return 0D;
    }
}
