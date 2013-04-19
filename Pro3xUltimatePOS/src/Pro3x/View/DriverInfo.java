/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

/**
 *
 * @author aco
 */
public class DriverInfo
{
    private String driver;
    private String connectionString;
    
    public DriverInfo()
    {
        this("", "");
    }

    public DriverInfo(String driver, String connectionString) {
        this.driver = driver;
        this.connectionString = connectionString;
    }
    
    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    @Override
    public String toString() {
        return driver;
    }
}
