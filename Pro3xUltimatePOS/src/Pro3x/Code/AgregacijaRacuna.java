/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Pro3x.Code;

import Acosoft.Processing.DataBox.Racun;

/**
 *
 * @author Aco
 */
public class AgregacijaRacuna
{
    public AgregacijaRacuna(VrsteUplate nacinPlacanja)
    {
        this.nacinPlacanja = nacinPlacanja;
        this.osnovica = 0D;
        this.porez = 0D;
        this.ukupno = 0D;
    }

    protected VrsteUplate nacinPlacanja;

    public VrsteUplate getNacinPlacanja()
    {
        return nacinPlacanja;
    }

    protected Double osnovica;

    public Double getOsnovica()
    {
        return osnovica;
    }

    protected Double porez;

    public Double getPorez()
    {
        return porez;
    }

    protected Double ukupno;

    public Double getUkupno()
    {
        return ukupno;
    }

    protected double ZaokruziBroj(double broj)
    {
        return Math.round(broj * 100) / 100D;
    }

    public boolean DodajRacun(Racun racun)
    {
        if(racun.getTemplate().getTip().equals(nacinPlacanja.toString()))
        {
            double ukupnoRacun = ZaokruziBroj(racun.getUkupno());
            osnovica += ukupnoRacun - racun.getPorez();
            porez += racun.getPorez();
            ukupno += ukupnoRacun;

            return true;
        }
        else
            return false;
    }
}
