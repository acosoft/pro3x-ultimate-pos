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

package Acosoft.Processing.Components;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

//TODO: dodaj datum posljednjeg učitavanja, datum posljednjeg spremanja, mogućnost resetiranja
public class InfoSekvenca
{
    public static enum ResetiranjeSekvence
    {
        Dnevno("dnevno"), Tjedno("tjedno"),
        Mjesecno("mjesečno"), Godisnje("godišnje");
        
        private final String opis;
        private ResetiranjeSekvence(String opis)
        {
            this.opis = opis;
        }
        
        public String getOpis()
        {
            return opis;
        }
    }

    public int getBrojZnamenki()
    {
        return brojZnamenki;
    }

    public void setBrojZnamenki(int brojZnamenki)
    {
        this.brojZnamenki = brojZnamenki;
    }

    public boolean isAutoSekvenca()
    {
        return autoSekvenca;
    }

    public void setAutoSekvenca(boolean autoSekvenca)
    {
        this.autoSekvenca = autoSekvenca;
    }
    public static enum Varijable
    {
        Dan("dan"), Mjesec("mjesec"), Godina("godina"), Sekvenca("sekvenca");

        private final String naziv;

        Varijable(String naziv)
        {
            this.naziv = naziv;
        }

        public String Kod()
        {
            return "{" + naziv + "}";
        }

        public String ZamjenskiKod()
        {
            return "\\{" + naziv + "\\}";
        }
    }

    protected ResetiranjeSekvence resetiranje;

    public ResetiranjeSekvence getResetiranje()
    {
        return resetiranje;
    }

    public void setResetiranje(ResetiranjeSekvence resetiranje)
    {
        this.resetiranje = resetiranje;
    }


    private java.beans.PropertyChangeSupport changeSupport;
    private String format;
    private int vrijednost;
    private int brojZnamenki;
    private boolean autoSekvenca;

    public InfoSekvenca()
    {
        changeSupport = new PropertyChangeSupport(this);
        setFormat(Varijable.Sekvenca.Kod());
        setVrijednost(1);
        setBrojZnamenki(3);
        setAutoSekvenca(false);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        changeSupport.removePropertyChangeListener(listener);
    }

    public int getVrijednost()
    {
        return vrijednost;
    }

    public void setVrijednost(int vrijednost)
    {
        int stara = this.vrijednost;
        this.vrijednost = vrijednost;
        changeSupport.firePropertyChange("vrijednost", stara, vrijednost);
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        String staro = this.format;
        this.format = format;
        changeSupport.firePropertyChange("format", staro, format);
    }

    public synchronized int Sljedeca()
    {
        int sekvenca = getVrijednost();
        setVrijednost(sekvenca +  1);
        
        spremi();
        
        return sekvenca;
    }
    
    public void spremi()
    {
        
    }

    protected Date datumPosljednjeSekvence;

    public Date getDatumPosljednjeSekvence()
    {
        if(datumPosljednjeSekvence == null)
            datumPosljednjeSekvence = Calendar.getInstance().getTime();

        return datumPosljednjeSekvence;
    }

    public void setDatumPosljednjeSekvence(Date datumPosljednjeSekvence)
    {
        this.datumPosljednjeSekvence = datumPosljednjeSekvence;
    }

    public String SljedecaFormatiranaSekvenca()
    {
//        Calendar datum = ResetirajSekvencu();
//        String temp = format;

        NumberFormat nf = NumberFormat.getInstance();
        //nf.setMinimumIntegerDigits(2);
        nf.setGroupingUsed(false);

//        temp = temp.replaceAll(Varijable.Dan.ZamjenskiKod(),nf.format(datum.get(Calendar.DAY_OF_MONTH)));
//        temp = temp.replaceAll(Varijable.Mjesec.ZamjenskiKod(), nf.format(datum.get(Calendar.MONTH) + 1));
//        temp = temp.replaceAll(Varijable.Godina.ZamjenskiKod(), String.valueOf(datum.get(Calendar.YEAR)));
//
//        //nf.setMinimumIntegerDigits(getBrojZnamenki());
//        temp = temp.replaceAll(Varijable.Sekvenca.ZamjenskiKod(), nf.format(Sljedeca()));

        return nf.format(Sljedeca());
    }
}
