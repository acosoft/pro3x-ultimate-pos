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

package Pro3x.Kalkulacije.Model;

import Acosoft.Processing.DataBox.RobaKartica;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(value="kalkulacija")
public class KarticaStavkeKalkulacije extends RobaKartica implements Serializable
{
    @ManyToOne
    @JoinColumn(name="stavka_kalkulacije")
    protected StavkaKalkulacije stavka;
    public static final String PROP_STAVKA = "stavka";

    public StavkaKalkulacije getStavka()
    {
        return stavka;
    }

    public void setStavka(StavkaKalkulacije stavka)
    {
        StavkaKalkulacije oldStavka = this.stavka;
        this.stavka = stavka;
        propertyChangeSupport.firePropertyChange(PROP_STAVKA, oldStavka, stavka);
    }

    @Override
    public Double getUlaznaVrijednost()
    {
        return getUlaznaCijena() * getKolicinaUlaz();
    }

    @Override
    public Double getMaloprodajnaCijena()
    {
        return getStavka().getCijenaSaPorezom();
    }

    @Transient
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




