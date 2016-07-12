package it.publisys.pagamentionline.service;

import it.govpay.servizi.PagamentiTelematiciGPApp;
import it.govpay.servizi.PagamentiTelematiciGPAppService;
import it.govpay.servizi.PagamentiTelematiciGPPrt;
import it.govpay.servizi.PagamentiTelematiciGPPrtService;
import it.govpay.servizi.commons.*;
import it.govpay.servizi.gpapp.*;
import it.govpay.servizi.gpprt.*;
import it.govpay.servizi.gpprt.GpChiediStatoVersamento;
import it.govpay.servizi.pa.*;
import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.PagamentiOnlineKey;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.xml.ws.BindingProvider;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Date;
import java.util.Properties;

/**
 * @author Francesco A. Tabino
 */
@Service
public class GovPayService {



    @Autowired
    private PagamentoService pagamentoService;

    public PagamentiTelematiciGPPrt getPagamentiTelematiciGPPrt(Pagamento pagamento) {
        return getPagamentiTelematiciGPPrt(pagamento.getEnte());
    }

    public PagamentiTelematiciGPPrt getPagamentiTelematiciGPPrt(Ente ente) {
        Properties prop = pagamentoService.loadProperties(ente);

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        prop.getProperty("username"),
                        prop.getProperty("password").toCharArray());
            }
        });

        PagamentiTelematiciGPPrt port = new PagamentiTelematiciGPPrtService().getGPPrtPort();
        ((BindingProvider) port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, prop.getProperty("username"));
        ((BindingProvider) port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, prop.getProperty("password"));
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, prop.getProperty("url"));
        return port;
    }


    public PagamentiTelematiciGPApp getPagamentiTelematiciGPApp(Ente ente) {
        Properties prop = pagamentoService.loadProperties(ente);

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        prop.getProperty("usernameApp"),
                        prop.getProperty("passwordApp").toCharArray());
            }
        });
        PagamentiTelematiciGPApp portApp = new PagamentiTelematiciGPAppService().getGPAppPort();
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, prop.getProperty("usernameApp"));
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, prop.getProperty("passwordApp"));
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, prop.getProperty("urlApp"));
        return portApp;
    }

    private Anagrafica getAnagrafica(User user) {
        Anagrafica debitore = new Anagrafica();
        debitore.setCellulare("333333333333");
        debitore.setCap("85050");
        debitore.setCivico("69");
        debitore.setCodUnivoco(user.getFiscalcode());
        debitore.setEmail(user.getEmail());
        debitore.setFax("800800800");
        debitore.setIndirizzo("Contrada Santa Loja");
        debitore.setLocalita("Tito Scalo");
        debitore.setNazione("IT");
        debitore.setProvincia("PZ");
        debitore.setRagioneSociale(user.getFirstname() + user.getLastname());
        debitore.setTelefono("900900900");
        return debitore;

    }

    public GpChiediSceltaWisp gpSceltaWISP(Pagamento pagamento) {
        Properties prop = pagamentoService.loadProperties(pagamento);
        GpChiediSceltaWisp gpChiediSceltaWisp = new GpChiediSceltaWisp();
        gpChiediSceltaWisp.setCodDominio(prop.getProperty(PagamentiOnlineKey.COD_DOMINIO));
        gpChiediSceltaWisp.setCodPortale(prop.getProperty(PagamentiOnlineKey.COD_PORTALE));
        gpChiediSceltaWisp.setCodKeyPA(pagamento.getPid());
        gpChiediSceltaWisp.setCodKeyWISP(pagamento.getKeyWisp());

        return gpChiediSceltaWisp;
    }


    public GpAvviaTransazionePagamento gpGeneraRpt(Pagamento pagamento, User user, Canale canale) {
        Properties prop = pagamentoService.loadProperties(pagamento);
        GpAvviaTransazionePagamento gpAvviaTransazionePagamento = new GpAvviaTransazionePagamento();
        gpAvviaTransazionePagamento.setAutenticazione(TipoAutenticazione.N_A);

        //canale.setTipoVersamento(TipoVersamento.CP);
        //gpAvviaTransazionePagamento.setCanale(canale);
        gpAvviaTransazionePagamento.setCodPortale(prop.getProperty(PagamentiOnlineKey.COD_PORTALE));
        gpAvviaTransazionePagamento.setIbanAddebito(null);
        gpAvviaTransazionePagamento.setUrlRitorno(prop.getProperty(ModelMappings.URLBACK) + "?pid=" + pagamento.getPid());
        gpAvviaTransazionePagamento.setVersante(null);

        GpAvviaTransazionePagamento.SceltaWisp sw = new GpAvviaTransazionePagamento.SceltaWisp();
        sw.setCodDominio(prop.getProperty(PagamentiOnlineKey.COD_DOMINIO));
        sw.setCodKeyWISP(pagamento.getKeyWisp());
        sw.setCodKeyPA(pagamento.getPid());
        gpAvviaTransazionePagamento.setSceltaWisp(sw);

        //creazione versamento
        Versamento versamento = new Versamento();
        versamento.setAggiornabile(false);
        versamento.setCausale(pagamento.getCausale());
        versamento.setCodApplicazione(prop.getProperty(PagamentiOnlineKey.COD_APPLICAZIONE));
        versamento.setCodDominio(prop.getProperty(PagamentiOnlineKey.COD_DOMINIO));
        BigDecimal importo = new BigDecimal(pagamento.getImporto(), MathContext.DECIMAL64);
        versamento.setImportoTotale(importo);
        versamento.setDataScadenza(pagamento.getRata().getDataA());
        versamento.setCodVersamentoEnte(pagamento.getPid());
        versamento.setDebitore(getAnagrafica(user));

        Versamento.SingoloVersamento singoloVersamento = new Versamento.SingoloVersamento();
        singoloVersamento.setCodSingoloVersamentoEnte(pagamento.getPid() + "_1");
        singoloVersamento.setCodTributo(pagamento.getTributo().getCodice());
        singoloVersamento.setImporto(importo);
        versamento.getSingoloVersamento().add(singoloVersamento);

        gpAvviaTransazionePagamento.getVersamentoOrVersamentoRef().add(versamento);

        return gpAvviaTransazionePagamento;
    }


    public GpChiediStatoTransazione chiediStato(Pagamento pagamento) {

        Properties prop = pagamentoService.loadProperties(pagamento);
        System.out.println( "get ccp" +  pagamento.getCcp());
        GpChiediStatoTransazione gpChiediStatoTransazione = new GpChiediStatoTransazione();
        gpChiediStatoTransazione.setCcp(pagamento.getCcp());
        gpChiediStatoTransazione.setCodDominio(prop.getProperty(PagamentiOnlineKey.COD_DOMINIO));
        gpChiediStatoTransazione.setCodPortale(prop.getProperty(PagamentiOnlineKey.COD_PORTALE));
        gpChiediStatoTransazione.setIuv(pagamento.getRefnumber());


        return gpChiediStatoTransazione;
    }


    public GpChiediListaVersamenti gpCercaVersamentiRequest(User user, Ente ente) {

        Properties prop = pagamentoService.loadProperties(ente);

        GpChiediListaVersamenti gpChiediListaVersamenti = new GpChiediListaVersamenti();
        gpChiediListaVersamenti.setCodUnivocoDebitore(user.getFiscalcode());
        gpChiediListaVersamenti.setCodPortale(prop.getProperty(PagamentiOnlineKey.COD_PORTALE));
        gpChiediListaVersamenti.getStato().add(StatoVersamento.NON_ESEGUITO);
        gpChiediListaVersamenti.setOrdinamento("DATA_SCADENZA_DES");

        return gpChiediListaVersamenti;

    }

    public it.govpay.servizi.gpapp.GpChiediStatoVersamento gpChiediStatoVersamento(String codVersamentoEnte, String codApplicazione) {

        it.govpay.servizi.gpapp.GpChiediStatoVersamento gpChiediStatoVersamento = new it.govpay.servizi.gpapp.GpChiediStatoVersamento();
         gpChiediStatoVersamento.setCodApplicazione(codApplicazione);
        gpChiediStatoVersamento.setCodVersamentoEnte(codVersamentoEnte);

        return gpChiediStatoVersamento;
    }




}
