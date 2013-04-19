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


public class BarcodeInfo
{

    public BarcodeInfo(String prikazCijene, String barcode, String naziv, String sifra)
    {
        this.prikazCijene = prikazCijene;
        this.barcode = barcode;
        this.naziv = naziv;
        this.sifra = sifra;
    }

    protected String prikazCijene;

    public String getPrikazCijene()
    {
        return prikazCijene;
    }

    public void setPrikazCijene(String prikazCijene)
    {
        this.prikazCijene = prikazCijene;
    }

    protected String barcode;

    public String getBarcode()
    {
        return barcode;
    }

    public void setBarcode(String barcode)
    {
        this.barcode = barcode;
    }

    protected String naziv;

    public String getNaziv()
    {
        return naziv;
    }

    public void setNaziv(String naziv)
    {
        this.naziv = naziv;
    }

    protected String sifra;

    public String getSifra()
    {
        return sifra;
    }

    public void setSifra(String sifra)
    {
        this.sifra = sifra;
    }
}
