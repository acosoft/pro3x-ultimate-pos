/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

import Acosoft.Processing.DataBox.PoreznaStopa;
import java.util.UUID;

/**
 *
 * @author aco
 */
public class SimpleModelPoreznihStopa extends SimpleModel<PoreznaStopa>
{

    public SimpleModelPoreznihStopa() {
        super("Porez.SveStope");
    }
    
    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        if(column == 0)
            return "Naziv";
        else
            return "Stopa";
    }

    @Override
    protected PoreznaStopa createInstance() {
        PoreznaStopa stopa = new PoreznaStopa();
        stopa.setKljuc(UUID.randomUUID().toString());
        return stopa;
    }

    @Override
    protected void write(PoreznaStopa item, int column, Object newValue) {
        if(column == 0)
            item.setOpis((String)newValue);
        else
            item.setPostotak((Double)newValue);
    }

    @Override
    protected Object read(PoreznaStopa item, int column) {
        if(column == 0)
            return item.getOpis();
        else
            return item.getPostotak();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex == 0)
            return String.class;
        else
            return Double.class;
    }
}
