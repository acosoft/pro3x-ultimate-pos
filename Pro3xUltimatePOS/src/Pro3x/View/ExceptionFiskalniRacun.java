/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.View;

import FinaClient.RacunOdgovor;

/**
 *
 * @author aco
 */
public class ExceptionFiskalniRacun extends Exception 
{
    private RacunOdgovor odgovor;

    public ExceptionFiskalniRacun(RacunOdgovor odgovor, String message) {
        super(message);
        this.odgovor = odgovor;
    }

    public RacunOdgovor getOdgovor() {
        return odgovor;
    }
}
