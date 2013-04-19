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

package Pro3x.Configuration;

public class BazaPodataka
{
    private String korisnik;
    private String zaporka;
    private String driver;
    private String connection;

    public String getKorisnik()
    {
        return korisnik;
    }

    public void setKorisnik(String korisnik)
    {
        this.korisnik = korisnik;
    }

    public String getZaporka()
    {
        return zaporka;
    }

    public void setZaporka(String zaporka)
    {
        this.zaporka = zaporka;
    }

    public String getDriver()
    {
        return driver;
    }

    public void setDriver(String driver)
    {
        this.driver = driver;
    }

    public String getConnection()
    {
        return connection;
    }

    public void setConnection(String server)
    {
        this.connection = server;
    }
}
