/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Pro3x.Code;

import Acosoft.Processing.DataBox.Roba;
import Acosoft.Processing.DataBox.RobaKartica;
import java.util.Date;

/**
 *
 * @author Aco
 */
public class ZapisnikPromjeneCijene
{

    protected Roba artikal;

    public Roba getArtikal()
    {
        return artikal;
    }

    public void setArtikal(Roba artikal)
    {
        this.artikal = artikal;
    }

    protected Double staraCijena;

    public Double getStaraCijena()
    {
        return staraCijena;
    }

    public void setStaraCijena(Double staraCijena)
    {
        this.staraCijena = staraCijena;
    }

    protected Double novaCijena;

    public Double getNovaCijena()
    {
        return novaCijena;
    }

    public void setNovaCijena(Double novaCijena)
    {
        this.novaCijena = novaCijena;
    }

    protected Date datum;

    public Date getDatum()
    {
        return datum;
    }

    public void setDatum(Date datum)
    {
        this.datum = datum;
    }

    protected Double kolicina;

    public Double getKolicina()
    {
        return kolicina;
    }

    public void setKolicina(Double kolicina)
    {
        this.kolicina = kolicina;
    }

    protected RobaKartica kartica;

    public RobaKartica getKartica()
    {
        return kartica;
    }

    public void setKartica(RobaKartica kartica)
    {
        this.kartica = kartica;
    }
}
