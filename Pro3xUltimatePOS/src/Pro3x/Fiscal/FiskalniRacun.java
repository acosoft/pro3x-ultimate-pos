/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Pro3x.Fiscal;

import Acosoft.Processing.DataBox.PoreznaStavkaRacuna;
import Acosoft.Processing.DataBox.PotStavkaRacuna;
import Acosoft.Processing.DataBox.Racun;
import FinaClient.BrojRacunaType;
import FinaClient.FiskalizacijaPortType;
import FinaClient.FiskalizacijaService;
import FinaClient.NacinPlacanjaType;
import FinaClient.OznakaSlijednostiType;
import FinaClient.PdvType;
import FinaClient.PorezNaPotrosnjuType;
import FinaClient.PorezType;
import FinaClient.RacunOdgovor;
import FinaClient.RacunType;
import FinaClient.RacunZahtjev;
import FinaClient.ZaglavljeType;
import FiscalClient.SecureSoapHandler;
import Pro3x.Configuration.General;
import Pro3x.View.ExceptionFiskalniRacun;
import Pro3x.View.SimpleDecimal;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author aco
 */
public class FiskalniRacun {
    
    private KeyInfo keyInfo;
    private LocationInfo info;
    private Racun racun;

    public FiskalniRacun(Racun racun) {
        this.racun = racun;
        this.keyInfo = IzborCertifikata.loadKeyInfo();
        this.info = IzmjenaPoslovnogProstora.loadLocationInfo();
    }
    
    public String fiskaliziraj() throws Exception {
        FiskalizacijaService service = new FiskalizacijaService();
        service.setHandlerResolver(new HandlerResolver() {

            @Override
            public List<Handler> getHandlerChain(PortInfo portInfo) {
                List<Handler> handlers = new ArrayList<Handler>();
                try {
                    handlers.add(new SecureSoapHandler(keyInfo.getPath(), String.valueOf(keyInfo.getPassword()), String.valueOf(keyInfo.getPassword())));
                } catch (Exception ex) {
                    Logger.getLogger(FiskalniRacun.class.getName()).log(Level.SEVERE, null, ex);
                }
                return handlers;
            }
        });

        FiskalizacijaPortType port = service.getFiskalizacijaPortType();
        
        if(General.isFiskalizacijaProdukcija())
        {
            //prebacivanje na produkcijske servise
            BindingProvider bp = (BindingProvider) port;
            Map<String, Object> context = bp.getRequestContext();
            context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "https://cis.porezna-uprava.hr:8449/FiskalizacijaService");
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss");
        RacunZahtjev zahtjev = new RacunZahtjev();

        ZaglavljeType zaglavlje = new ZaglavljeType();
        zaglavlje.setIdPoruke(racun.getKljuc());
        zaglavlje.setDatumVrijeme(sdf.format(racun.getIzdan()));

        zahtjev.setZaglavlje(zaglavlje);

        RacunType rt = new RacunType();

        BrojRacunaType broj = new BrojRacunaType();
        broj.setBrOznRac(racun.getOznaka());
        broj.setOznNapUr(info.getOznakaUredaja());
        broj.setOznPosPr(info.getNaziv());

        rt.setBrRac(broj);

        rt.setDatVrijeme(sdf.format(racun.getIzdan()));

        rt.setIznosUkupno(new SimpleDecimal(racun.getUkupno()));
        rt.setNacinPlac(NacinPlacanjaType.valueOf(racun.getTemplate().getNacinPlacanja()));
        rt.setOib(info.getOibTvrtke());
        rt.setOibOper(racun.getOperater().getOib());
        
        rt.setOznSlijed(OznakaSlijednostiType.N);
        
        PdvType pdv = new PdvType();
        PorezNaPotrosnjuType pot = new PorezNaPotrosnjuType();
        
        for(PoreznaStavkaRacuna poreznaStavka : racun.getPorezneStavke())
        {
            PorezType porez = new PorezType();
            
            porez.setStopa(new SimpleDecimal(poreznaStavka.getStopa() * 100));
            porez.setOsnovica(new SimpleDecimal(poreznaStavka.getOsnovica()));
            porez.setIznos(new SimpleDecimal(poreznaStavka.getIznos()));
            
            if(poreznaStavka instanceof PotStavkaRacuna)
            {
                pot.getPorez().add(porez);
            }
            else
            {
                pdv.getPorez().add(porez);
            }
        }
        
        if(pdv.getPorez().size() > 0) rt.setPdv(pdv);
        if(pot.getPorez().size() > 0) rt.setPnp(pot);
        
        rt.setUSustPdv(info.isPdvObveznik());
        
        if(racun.getZki() == null || racun.getZki().isEmpty())
        {
            String zki = IzracunajZastitniKod();
            racun.setZki(zki);
            rt.setNakDost(false);
        }
        else
            rt.setNakDost(true);
        
        rt.setZastKod(racun.getZki());
        
        zahtjev.setRacun(rt);

        RacunOdgovor odgovor = port.racuni(zahtjev);
        
        if(odgovor.getGreske() != null)
        {
            throw new ExceptionFiskalniRacun(odgovor, "IZNIMKA: Račun trenutno nije moguće izdati sa JIR oznakom.");
        }
        
        racun.setJir(odgovor.getJir());
        return odgovor.getJir();
    }

    private double IzracunIznosaKojiSeNeOporezuje() {
        return 0D;
    }

    private double IzracunIznosOslobodjenPdva() {
        return 0D;
    }
    
    private String IzracunajZastitniKod() throws Exception
    {
        String kod = "";
        
        kod += info.getOibTvrtke();
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        
        kod += sdf.format(racun.getIzdan());
        kod += racun.getOznaka();
        kod += info.getNaziv();
        kod += info.getOznakaUredaja();
        kod += racun.getUkupno().toString();
        
        KeyStore store = KeyStore.getInstance("PKCS12");
        store.load(new FileInputStream(keyInfo.getPath()), keyInfo.getPassword());
        
        String alias = store.aliases().nextElement();
        Key key = store.getKey(alias, keyInfo.getPassword());
        
        Signature algoritam = Signature.getInstance("SHA1withRSA");
        
        algoritam.initSign((PrivateKey) key);
        algoritam.update(kod.getBytes());
        
        byte[] potpis = algoritam.sign();

        return DigestUtils.md5Hex(potpis);
    }
    
}
