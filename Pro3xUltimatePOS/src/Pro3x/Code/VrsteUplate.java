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

public enum VrsteUplate
{
    GotovinskaUplata("Gotovinska uplata", "Gotovinska uplata", "Gotovinski promet", true, true, "G"),
    BezGotovinskaUplata("Bez gotovinska uplata", "Bez gotovinska uplata", "Bez gotovinski promet", false, true, "T"),
    NijePlacen("Nije naplaćen", "Odgoda plaćanja", "Odgoda plaćanja", false, false, "T"),
    Visa("Visa kartica", "Visa kartice", "Visa kartice", true, false, "K"),
    Maestro("Maestro kartica", "Maestro kartice", "Maestro kartice", true, false, "K"),
    Mastercard("Mastercard", "Mastercard kartice", "Mastercad kartice", true, false, "K"),
    Amex("American Express", "American Express", "American Express", true, false, "K"),
    Diners("Diners kartica", "Diners kartice", "Diners kartice", true, false, "K"),
    Ponuda("Samo otvori novu ponudu", "Nova ponuda", "Ponude", false, false, null),
    Storno("Storno promet", "Storno račun", "Storno promet", true, true, "O");

    private final String opis;
    private final String nacinPlacanja;
    private final String print;
    private final boolean fiskalnaTransakcija;
    private final boolean placen;
    private final String tip; // naziv spremljen u bazu podataka koji se koristi za usporedbu sa odabranim predloškom
    
    private VrsteUplate(String tip, String opis, String print, boolean fiskalnaTransakcija, boolean placen, String nacinPlacanja)
    {
        this.opis = opis;
        this.print = print;
        this.fiskalnaTransakcija = fiskalnaTransakcija;
        this.placen = placen;
        this.tip = tip;
        this.nacinPlacanja = nacinPlacanja;
    }
    
    public boolean isFiskalnaTransakcija()
    {
        return fiskalnaTransakcija;
    }

    public String getPrint()
    {
        return print;
    }

    public String getNacinPlacanja() {
        return nacinPlacanja;
    }
    
    @Override
    public String toString()
    {
        return tip;
    }
    
    public boolean isPlacen()
    {
        return this.placen;
    }
    
    public String getTip()
    {
        return tip;
    }
    
    public String getOpis()
    {
        return opis;
    }

    /**
     * @deprecated 
     */
    public boolean isKoristiR1Sekvencu()
    {
        return false;
    }
}
