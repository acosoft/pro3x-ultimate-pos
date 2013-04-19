/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.Fiscal;

/**
 *
 * @author aco
 */
public class KeyInfo {
    
    private String path;
    private char[] password;
    private boolean fiskalizacijaAktivirana;

    public KeyInfo() {
        this.path = "";
        this.password = new char[0];
        this.fiskalizacijaAktivirana = false;
    }

    public boolean isFiskalizacijaAktivirana() {
        return fiskalizacijaAktivirana;
    }

    public void setFiskalizacijaAktivirana(boolean fiskalizacijaAktivirana) {
        this.fiskalizacijaAktivirana = fiskalizacijaAktivirana;
    }
    
    public char[] getPassword() {
        
        if(password == null)
        {
            return new char[0];
        }
        else
            return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
}
