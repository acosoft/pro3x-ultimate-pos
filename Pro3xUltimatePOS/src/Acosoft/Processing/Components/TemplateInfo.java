/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Acosoft.Processing.Components;

/**
 *
 * @author aco
 */
public class TemplateInfo 
{
    private String path;
    private String display;

    public TemplateInfo(String path, String display) {
        this.path = path;
        this.display = display;
    }
    
    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return getDisplay();
    }
}
