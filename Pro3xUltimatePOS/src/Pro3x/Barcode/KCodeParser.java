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

package Pro3x.Barcode;

import Acosoft.Processing.DataBox.Roba;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class KCodeParser implements BarcodeParser
{
    private BarcodeControler controler;
    private Pattern pattern = Pattern.compile("K(\\d+)X{0,1}(.*)");
    private String sifra = "";
    private Double cijena = 0D;

    public void setup(BarcodeControler controler)
    {
        this.controler = controler;
    }

    private void extract(Matcher m)
    {
        String temp = m.group(1);

        if(temp.isEmpty())
            cijena = 0D;
        else
            cijena = Double.parseDouble(temp) / 100;
        
        sifra = m.group(2);
    }

    public boolean parse(String code)
    {
        Matcher m = pattern.matcher(code);

        if(m.matches())
        {
            extract(m);
            return true;
        }
        else
            return false;
    }

    public String getSifra()
    {
        return sifra;
    }

    public Double getCijena()
    {
        return cijena;
    }

    public boolean execute(boolean modeNormativi)
    {
        for(Roba roba : controler.popisArtikala())
        {
            if(roba.getSifra().equals(sifra))
            {
                if(modeNormativi == true)
                    controler.dodajNormativ(roba, 1D, cijena);
                else
                    controler.dodajStavku(roba, 1D, cijena);
                return true;
            }
        }

        return false;
    }

    public static String format(String sifra, Double cijena)
    {
        NumberFormat nf = NumberFormat.getInstance();

        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(0);
        nf.setGroupingUsed(false);

        try
        {
            Double ipc = nf.parse(nf.format(cijena)).doubleValue();
            ipc = ipc * 100;

            return "*K" + nf.format(ipc) + "X" + sifra + "*";
        }
        catch (ParseException ex)
        {
            Logger.getLogger(KCodeParser.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
}
