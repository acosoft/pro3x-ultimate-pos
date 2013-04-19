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

import Acosoft.Processing.Pro3App;
import Pro3x.Licences.LicenceInfo;

public class General
{
//    private static Pro3App app = Pro3App.getApplication();
//    private static LicenceInfo reg = app.getRegistracijskiKljuc();
    
    private static final boolean maloprodaja = isActive("maloprodaja"); //true
    private static final boolean userMode = isActive("userMode");  //false
    
    //TODO: Važno! Prebaci na produkcijske fiskalne servise
    private static final boolean fiskalizacijaProdukcija = isActive("fiskalizacijaProdukcija");

    //TODO: Važno! Prebaci na direktni ispis računa
    private static final boolean ispisRacuna = isActive("ispisRacuna"); //true
    private static final boolean skraceniKodovi = isActive("skraceniKodovi"); //false
    private static final boolean editSequence = isActive("izmjenaSekvenci"); //true
    private static final boolean stanjeSkladista = isActive("stanjeSkladista"); //true;
    private static final boolean tempTasks = isActive("tempTasks"); //false;
    private static final boolean brisanjeKartice = isActive("brisanjeKartice"); //false;

    //Skriva tablicu sa normativima pri unosu računa
    private static final boolean skriveniNormativi = isActive("skriveniNormativi"); //true;

    //Skriva mogućnost definicije normativa na popisu artikala
    private static final boolean koristiNormativeArtikala = isActive("koristiNormativeArtikala"); //true;
    private static final boolean koristiMigracijuArtikala = isActive("koristiMigracijuArtikala"); //false;
    private static final boolean koristiZapisnikePromjeneCijena = isActive("koristiZapisnikePromjeneCijena"); //true;
    private static final boolean koristiTehnickuPodrsku = isActive("koristiTehnickuPodrsku"); //false;
    private static final boolean korisniKnjiguPopisa = false;
    private static final boolean koristiGotovinskeTransakcije = isActive("koristiGotovinskeTransakcije"); //true;
    private static final boolean koristiUsporedivanjeSkladista = isActive("koristiUsporedivanjeSkladista"); //false;

    //barcode template sa opisom ili bez opisa artikla
    //private static final String barcodeTemplateSaOpisom = "resources/GraziaIspisBarkod.jasper";
    private static final String barcodeTemplateBezOpisa = "resources/IspisBarkod.jasper";
    private static final String barcodeSaDeklaracijom = "resources/BarkodSaDeklaracijom.jasper";
    
    private static final boolean koristiDeklaracije = isActive("koristiDeklaracije"); //false;
    private static final boolean koristiPopupEditorCijena = isActive("koristiPopupEditorCijena"); //true;
    private static final boolean koristiIzracunOstatka = isActive("koristiIzracunOstatka"); //true;
    private static final boolean koristiDisplayZaKupca = isActive("koristiDisplayZaKupca"); //true;
    private static final String poslinkDisplayZaKupca = value("poslinkDisplayZaKupca", null);
    
    private static final boolean koristiPorezNaPotrosnju = isActive("porezNaPotrosnju");
    
    public static boolean isKoristiPorezNaPotrosnju()
    {
        return koristiPorezNaPotrosnju;
    }

    private static String value(String option, String fallback)
    {
        LicenceInfo reg = Pro3App.getApplication().getRegistracijskiKljuc();
        
        if(reg != null)
        {
            return reg.readOption(option, fallback);
        }
        else
        {
            return fallback;
        }
    }
    
    public static boolean isFiskalizacijaProdukcija()
    {
        return fiskalizacijaProdukcija;
    }
    
    private static boolean isActive(String option)
    {
        LicenceInfo reg = Pro3App.getApplication().getRegistracijskiKljuc();
        
        if(reg != null)
        {
            return reg.verifyOption(option, "true");
        }
        else
        {
            return false;
        }
    }
    
    public static String getPoslinkDisplayZaKupca()
    {
        return poslinkDisplayZaKupca;
    }
    
    public static boolean isKoristiDisplayZaKupca()
    {
        return koristiDisplayZaKupca;
    }
    
    public static boolean isKoristiIzracunOstatka()
    {
        return koristiIzracunOstatka;
    }
    
    public static boolean isKoristiPopupEditorCijena()
    {
        return koristiPopupEditorCijena;
    }

    public static boolean isKoristiDeklaracije()
    {
        return koristiDeklaracije;
    }

    public static String getBarcodeTemplate()
    {
        if(isKoristiDeklaracije())
            //return barcodeTemplateSaOpisom;
            return barcodeSaDeklaracijom;
        else
            return barcodeTemplateBezOpisa;
    }

    public static boolean isKoristiUsporedivanjeSkladista()
    {
        return koristiUsporedivanjeSkladista;
    }

    public static boolean isKoristiGotovinskeTransakcije()
    {
        return koristiGotovinskeTransakcije;
    }

    public static boolean isKoristiKnjigeuPopisa()
    {
        return korisniKnjiguPopisa;
    }

    private static final String oibPrefix = "OIB";

    public static boolean isKoristiTehnickuPodrsku()
    {
        return koristiTehnickuPodrsku;
    }

    public static boolean isDirektniIspisRacuna()
    {
        return ispisRacuna;
    }

    public static boolean isKoristiNormativeArtikala()
    {
        return koristiNormativeArtikala;
    }

    public static boolean isKoristiZapisnikePromjeneCijena()
    {
        return koristiZapisnikePromjeneCijena;
    }

    public static boolean isKoristiMigracijuArtikala()
    {
        return koristiMigracijuArtikala;
    }

    public static Boolean isMaloprodaja()
    {
        return maloprodaja;
    }

    public static boolean isUserMode()
    {
        return userMode;
    }

    public static boolean isSkraceniKodovi() {
        return skraceniKodovi;
    }

    public static boolean isEditSequence() {
        return editSequence;
    }

    public static boolean isStanjeSkladista() {
        return stanjeSkladista;
    }

    public static boolean isTempTaks()
    {
        return tempTasks;
    }

    public static boolean isBrisanjeKartice() {
        return brisanjeKartice;
    }

    public static String getOibPrefix()
    {
        return oibPrefix;
    }

    public static boolean isSkriveniNormativi()
    {
        return skriveniNormativi;
    }
}
