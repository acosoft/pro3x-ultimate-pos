// Pro3x Community project
// Copyright (C) 2009  Aleksandar Zgonjan
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, http://www.gnu.org/licenses/gpl.html

package Acosoft.Processing.Components;

/**
 *
 * @author nonstop
 */
public class InfoRacun
{
    private String zaglavlje;
    private String ziroRacun;
    private String informacije;
    private String nazivTvrtke;
    private String mjestoIzdavanja;
    private String valuta;
    private String oib;

    public String getOib() {
        return oib;
    }

    public void setOib(String oib) {
        this.oib = oib;
    }
    
    /**
     * @return the zaglavlje
     */
    public String getZaglavlje()
    {
        return zaglavlje;
    }

    /**
     * @param zaglavlje the zaglavlje to set
     */
    public void setZaglavlje(String zaglavlje)
    {

        this.zaglavlje = zaglavlje;
    }

    /**
     * @return the ziroRacun
     */
    public String getZiroRacun()
    {
        return ziroRacun;
    }

    /**
     * @param ziroRacun the ziroRacun to set
     */
    public void setZiroRacun(String ziroRacun)
    {
        this.ziroRacun = ziroRacun;
    }

    /**
     * @return the informacije
     */
    public String getInformacije()
    {
        return informacije;
    }

    /**
     * @param informacije the informacije to set
     */
    public void setInformacije(String informacije)
    {
        this.informacije = informacije;
    }

    /**
     * @return the nazivTvrtke
     */
    public String getNazivTvrtke()
    {
        return nazivTvrtke;
    }

    /**
     * @param nazivTvrtke the nazivTvrtke to set
     */
    public void setNazivTvrtke(String nazivTvrtke)
    {
        this.nazivTvrtke = nazivTvrtke;
    }

    public String getMjestoIzdavanja()
    {
        return mjestoIzdavanja;
    }

    public void setMjestoIzdavanja(String mjestoIzdavanja)
    {
        this.mjestoIzdavanja = mjestoIzdavanja;
    }

    public String getValuta()
    {
        return valuta;
    }

    public void setValuta(String valuta)
    {
        this.valuta = valuta;
    }


}
