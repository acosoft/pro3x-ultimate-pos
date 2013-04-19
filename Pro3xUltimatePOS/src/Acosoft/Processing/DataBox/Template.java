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

package Acosoft.Processing.DataBox;

import java.io.Serializable;
import java.text.MessageFormat;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name="Template.Svi", query="SELECT t FROM Template t")
public class Template implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="oznaka")
    private String oznaka;
    @Column(name="naziv")
    private String oznakaRacuna;
    @Column(name="tip")
    private String tip;
    @Lob
    @Column(name="format", length=Integer.MAX_VALUE)
    private String format;
    
    @Column(name="putanja", nullable=true, length=1000)
    private String predlozak;
    
    @Column(name="rok_placanja", nullable=true)
    private int rokPlacanja;
    
    @Column(name="nacin_placanja", nullable=true)
    private String nacinPlacanja;
    
    @Column(name="opis_placanja", nullable=false)
    private String opisPlacanja;
    
    @Column(name="prioritet")
    private int prioritet;

    public String getOpisPlacanja() {
        return opisPlacanja;
    }

    public void setOpisPlacanja(String opisPlacanja) {
        this.opisPlacanja = opisPlacanja;
    }
    
    public int getPrioritet() {
        return prioritet;
    }
    
    public String getNacinPlacanja()
    {
        return nacinPlacanja;
    }

    public int getRokPlacanja() {
        return rokPlacanja;
    }

    public void setRokPlacanja(int rokPlacanja) {
        this.rokPlacanja = rokPlacanja;
    }
    
    public String getPredlozak() {
        return predlozak;
    }

    public void setPredlozak(String predlozak) {
        this.predlozak = predlozak;
    }
    
    @Column(length=1000)
    protected String opis;

    public String getOpis()
    {
        return opis;
    }

    public void setOpis(String opis)
    {
        this.opis = opis;
    }

    public String format(String oznakaRacuna, String opis)
    {
        if(opis == null) opis = "";
        return MessageFormat.format(getOpis(), oznakaRacuna, opis);
    }

    @Override
    public String toString()
    {
        return getTip();
    }

    public String getOznaka()
    {
        return oznaka;
    }

    public void setOznaka(String oznaka)
    {
        this.oznaka = oznaka;
    }

    public String getOznakaRacuna()
    {
        return oznakaRacuna;
    }

    public void setOznakaRacuna(String naziv)
    {
        this.oznakaRacuna = naziv;
    }

    public String getTip()
    {
        return tip;
    }

    public void setTip(String tip)
    {
        this.tip = tip;
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }
    
    public boolean isPlacen()
    {
        return getRokPlacanja() == 0;
    }
    
    public boolean isFiskalnaTransakcija()
    {
        return getNacinPlacanja() != null && (getNacinPlacanja().equals("G") || getNacinPlacanja().equals("K"));
    }
    
    public boolean isGotovinskaTransakcija()
    {
        return getNacinPlacanja() != null && getNacinPlacanja().equals("G");
    }
    
    public String formatirajNapomenu(String oznakaRacuna, String napomena, String ziroRacun, String dodatneInformacije, String nazivTvrtke)
    {
        return getFormat()
                .replace("{napomena}", (napomena==null)?"":napomena)
                .replace("{oznaka računa}", (oznakaRacuna==null)?"":oznakaRacuna)
                .replace("{žiro račun}", (ziroRacun==null)?"":ziroRacun)
                .replace("{dodatne informacije}", (dodatneInformacije==null)?"":dodatneInformacije)
                .replace("{naziv tvrtke}", (nazivTvrtke==null)?"":nazivTvrtke);
    }
}
