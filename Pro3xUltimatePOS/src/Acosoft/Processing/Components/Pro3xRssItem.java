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

import java.util.Calendar;
import java.util.Date;

public class Pro3xRssItem
{
    private Date datum = Calendar.getInstance().getTime();
    private String naslov;
    private String opis;
    private String lokacija;

    /**
     * @return the datum
     */
    public Date getDatum()
    {
        return datum;
    }

    /**
     * @param datum the datum to set
     */
    public void setDatum(Date datum)
    {
        this.datum = datum;
    }

    /**
     * @return the naslov
     */
    public String getNaslov()
    {
        return naslov;
    }

    /**
     * @param naslov the naslov to set
     */
    public void setNaslov(String naslov)
    {
        this.naslov = naslov;
    }

    /**
     * @return the opis
     */
    public String getOpis()
    {
        return opis;
    }

    /**
     * @param opis the opis to set
     */
    public void setOpis(String opis)
    {
        this.opis = opis;
    }

    /**
     * @return the lokacija
     */
    public String getLokacija()
    {
        return lokacija;
    }

    /**
     * @param lokacija the lokacija to set
     */
    public void setLokacija(String lokacija)
    {
        this.lokacija = lokacija;
    }
}
