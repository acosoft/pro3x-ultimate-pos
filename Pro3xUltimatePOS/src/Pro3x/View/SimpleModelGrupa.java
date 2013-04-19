/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

import Acosoft.Processing.DataBox.GrupaArtikala;

/**
 *
 * @author aco
 */
public class SimpleModelGrupa extends SimpleModel<GrupaArtikala>
{

    public SimpleModelGrupa() {
        super("GrupaArtikala.SveGrupe");
    }
    
    @Override
    protected GrupaArtikala createInstance() {
        return new GrupaArtikala();
    }
    
    @Override
    public String getColumnName(int column) {
        return "Naziv grupe";
    }

    @Override
    protected void write(GrupaArtikala item, int column, Object newValue) {
        item.setNaziv((String)newValue);
    }

    @Override
    protected Object read(GrupaArtikala item, int column) {
        return item.getNaziv();
    }
}
