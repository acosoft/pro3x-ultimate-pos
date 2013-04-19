/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Pro3x.KnjigaPopisa;

/**
 *
 * @author Aco
 */
public class DokumentGeneric implements ZapisDokument
{
    private String opis;

    public DokumentGeneric(String opis)
    {
        this.opis = opis;
    }

    public String getOpis()
    {
        return opis;
    }

    public boolean setVisible()
    {
        return false;
    }

}
