/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

/**
 *
 * @author aco
 */
public interface iEntityColumn<T, V> 
{
    public V read(T item);
    public String getHeader();
    public Class getColumnClass();
}
