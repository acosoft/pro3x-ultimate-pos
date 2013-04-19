/*
 * Pro3x Community project
 * Copyright (C) 2009  Aleksandar Zgonjan
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, http://www.gnu.org/licenses/gpl.html
 * and open the template in the editor.
 */

package Pro3x.Code;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class AgregacijaPrometa
{
    protected String naziv;
    public static final String PROP_NAZIV = "naziv";
    protected Double kolicina;
    public static final String PROP_KOLICINA = "kolicina";
    protected Double vrijednost;
    public static final String PROP_VRIJEDNOST = "vrijednost";
    protected Double osnovica;
    public static final String PROP_OSNOVICA = "osnovica";
    protected String vrsta;
    public static final String PROP_VRSTA = "vrsta";

    public String getVrsta()
    {
        return vrsta;
    }

    public void setVrsta(String vrsta)
    {
        String oldVrsta = this.vrsta;
        this.vrsta = vrsta;
        propertyChangeSupport.firePropertyChange(PROP_VRSTA, oldVrsta, vrsta);
    }


    public Double getOsnovica()
    {
        return osnovica;
    }

    public void setOsnovica(Double osnovica)
    {
        Double oldOsnovica = this.osnovica;
        this.osnovica = osnovica;
        propertyChangeSupport.firePropertyChange(PROP_OSNOVICA, oldOsnovica, osnovica);
    }


    public AgregacijaPrometa()
    {
        this.naziv = "";
        this.kolicina = 0D;
    }

    public AgregacijaPrometa(String naziv, Double kolicina, Double vrijednost, Double osnovica)
    {
        this.naziv = naziv;
        this.kolicina = kolicina;
        this.vrijednost = vrijednost;
        this.osnovica = osnovica;
    }

    public Double getVrijednost()
    {
        return vrijednost;
    }

    public void setVrijednost(Double vrijednost)
    {
        Double oldVrijednost = this.vrijednost;
        this.vrijednost = vrijednost;
        propertyChangeSupport.firePropertyChange(PROP_VRIJEDNOST, oldVrijednost, vrijednost);
    }

    public Double getKolicina()
    {
        return kolicina;
    }

    public void setKolicina(Double kolicina)
    {
        Double oldKolicina = this.kolicina;
        this.kolicina = kolicina;
        propertyChangeSupport.firePropertyChange(PROP_KOLICINA, oldKolicina, kolicina);
    }

    public String getNaziv()
    {
        return naziv;
    }

    public void setNaziv(String naziv)
    {
        String oldNaziv = this.naziv;
        this.naziv = naziv;
        propertyChangeSupport.firePropertyChange(PROP_NAZIV, oldNaziv, naziv);
    }
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
