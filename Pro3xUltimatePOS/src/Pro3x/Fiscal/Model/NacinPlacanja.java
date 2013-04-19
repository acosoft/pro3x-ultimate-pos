/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.Fiscal.Model;

/**
 *
 * @author aco
 */
public enum NacinPlacanja {
    
    Gotovina("G", "Gotovina"), Kartice("K", "Kartice"), Transakcijski("T", "Transakcijski račun");
    
    private String kratica;
    private String opis;

    private NacinPlacanja(String kratica, String opis) {
        this.kratica = kratica;
        this.opis = opis;
    }
    
    public String getKratica() {
        return kratica;
    }

    public String getOpis() {
        return opis;
    }
}
