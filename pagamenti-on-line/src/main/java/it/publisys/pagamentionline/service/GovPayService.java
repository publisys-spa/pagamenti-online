package it.publisys.pagamentionline.service;

import it.govpay.servizi.commons.*;
import it.govpay.servizi.v2_3.*;
import it.govpay.servizi.v2_3.gpapp.GpAnnullaVersamento;
import it.govpay.servizi.gpapp.GpChiediFlussoRendicontazione;
import it.govpay.servizi.v2_3.gpapp.GpChiediListaFlussiRendicontazione;
import it.govpay.servizi.v2_3.gpapp.GpChiediStatoVersamento;
import it.govpay.servizi.v2_3.gpprt.GpAvviaTransazionePagamento;
import it.govpay.servizi.v2_3.gpprt.GpChiediListaVersamenti;
import it.govpay.servizi.v2_3.gpprt.GpChiediSceltaWisp;
import it.govpay.servizi.v2_3.gpprt.GpChiediStatoTransazione;
import it.govpay.servizi.v2_3.gprnd.*;
import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.PagamentiOnlineKey;
import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.BindingProvider;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Francesco A. Tabino, acoviello
 */
@Service
public class GovPayService {


    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private ProviderService providerService;

    public PagamentiTelematiciGPPrt getPagamentiTelematiciGPPrt() {
        Properties prop = providerService.loadPropertiesGovPay();

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        prop.getProperty(PagamentiOnlineKey.USERNAME),
                        prop.getProperty(PagamentiOnlineKey.PASSWORD).toCharArray());
            }
        });

        PagamentiTelematiciGPPrt port = new PagamentiTelematiciGPPrtService().getGPPrtPort();
        ((BindingProvider) port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, prop.getProperty(PagamentiOnlineKey.USERNAME));
        ((BindingProvider) port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, prop.getProperty(PagamentiOnlineKey.PASSWORD));
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, prop.getProperty(PagamentiOnlineKey.URL));
        return port;
    }


    public PagamentiTelematiciGPApp getPagamentiTelematiciGPApp() {
        Properties prop = providerService.loadPropertiesGovPay();

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        prop.getProperty(PagamentiOnlineKey.USERNAME_APP),
                        prop.getProperty(PagamentiOnlineKey.PASSWORD_APP).toCharArray());
            }
        });
        PagamentiTelematiciGPApp portApp = new PagamentiTelematiciGPAppService().getGPAppPort();
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, prop.getProperty(PagamentiOnlineKey.USERNAME_APP));
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, prop.getProperty(PagamentiOnlineKey.PASSWORD_APP));
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, prop.getProperty(PagamentiOnlineKey.URL_APP));
        return portApp;
    }

    public PagamentiTelematiciGPRnd getPagamentiTelematiciGPRnd() {
        Properties prop = providerService.loadPropertiesGovPay();

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        prop.getProperty(PagamentiOnlineKey.USERNAME_GPRND),
                        prop.getProperty(PagamentiOnlineKey.PASSWORD_GPRND).toCharArray());
            }
        });
        PagamentiTelematiciGPRnd portGPRnd = new PagamentiTelematiciGPRndService().getGPRndPort();
        ((BindingProvider) portGPRnd).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, prop.getProperty(PagamentiOnlineKey.USERNAME_GPRND));
        ((BindingProvider) portGPRnd).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, prop.getProperty(PagamentiOnlineKey.PASSWORD_GPRND));
        ((BindingProvider) portGPRnd).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, prop.getProperty(PagamentiOnlineKey.URL_GPRND));
        return portGPRnd;
    }

    private Anagrafica getAnagrafica(User user) {
        Anagrafica debitore = new Anagrafica();
        debitore.setRagioneSociale(user.getFirstname() + " " +  user.getLastname());
        debitore.setCodUnivoco(user.getFiscalcode());
        debitore.setEmail(user.getEmail());
        debitore.setCellulare(user.getCellulare());
        debitore.setTelefono(user.getTelefono());
        debitore.setFax(user.getFax());
        debitore.setIndirizzo(user.getIndirizzo());
        debitore.setCivico(user.getCivico());
        debitore.setLocalita(user.getLocalita());
        debitore.setCap(user.getCap());
        debitore.setProvincia(user.getProvincia());
        debitore.setNazione(user.getNazione());
        return debitore;
    }

    public GpChiediSceltaWisp gpSceltaWISP(Pagamento pagamento) {
        GpChiediSceltaWisp gpChiediSceltaWisp = new GpChiediSceltaWisp();
        gpChiediSceltaWisp.setCodDominio(pagamento.getEnte().getCodDominio());
        gpChiediSceltaWisp.setCodPortale(providerService.loadPropertiesGovPay().getProperty(PagamentiOnlineKey.COD_PORTALE));
        gpChiediSceltaWisp.setCodKeyPA(pagamento.getPid());
        gpChiediSceltaWisp.setCodKeyWISP(pagamento.getKeyWisp());

        return gpChiediSceltaWisp;
    }


    public GpAvviaTransazionePagamento gpGeneraRpt(Pagamento pagamento, User user) {
        Properties prop = pagamentoService.loadProperties(pagamento);
        GpAvviaTransazionePagamento gpAvviaTransazionePagamento = new GpAvviaTransazionePagamento();
        gpAvviaTransazionePagamento.setAutenticazione(TipoAutenticazione.N_A);

        //todo:codPortale da provider govPay
        gpAvviaTransazionePagamento.setCodPortale(providerService.loadPropertiesGovPay().getProperty(PagamentiOnlineKey.COD_PORTALE));
        gpAvviaTransazionePagamento.setAggiornaSeEsiste(true);
        gpAvviaTransazionePagamento.setIbanAddebito(null);
        gpAvviaTransazionePagamento.setUrlRitorno(prop.getProperty(ModelMappings.URL_BACK)); //+ "?pid=" + pagamento.getPid());
        gpAvviaTransazionePagamento.setVersante(null);

        GpAvviaTransazionePagamento.SceltaWisp sw = new GpAvviaTransazionePagamento.SceltaWisp();
        sw.setCodDominio(pagamento.getEnte().getCodDominio());
        sw.setCodKeyWISP(pagamento.getKeyWisp());
        sw.setCodKeyPA(pagamento.getPid());
        gpAvviaTransazionePagamento.setSceltaWisp(sw);

        //creazione versamento
        Versamento versamento = new Versamento();
        versamento.setAggiornabile(false);
        versamento.setCausale(pagamento.getCausale());
        versamento.setCodApplicazione(pagamento.getTributo().getApplicazione().getCodice());
        versamento.setCodDominio(pagamento.getEnte().getCodDominio());
        BigDecimal importo = new BigDecimal(pagamento.getImporto(), MathContext.DECIMAL64);
        versamento.setImportoTotale(importo);
        versamento.setDataScadenza(pagamento.getRata().getDataA());
        versamento.setCodVersamentoEnte(pagamento.getCodVersamentoEnte());
        versamento.setDebitore(getAnagrafica(user));

        Versamento.SingoloVersamento singoloVersamento = new Versamento.SingoloVersamento();
        singoloVersamento.setCodSingoloVersamentoEnte(pagamento.getCodVersamentoEnte() + "_1");
        singoloVersamento.setCodTributo(pagamento.getTributo().getCodice());
        singoloVersamento.setImporto(importo);
        versamento.getSingoloVersamento().add(singoloVersamento);

        gpAvviaTransazionePagamento.getVersamentoOrVersamentoRef().add(versamento);

        return gpAvviaTransazionePagamento;
    }


    public GpChiediStatoTransazione chiediStato(Pagamento pagamento) {
        GpChiediStatoTransazione gpChiediStatoTransazione = new GpChiediStatoTransazione();
        gpChiediStatoTransazione.setCcp(pagamento.getCcp());
        gpChiediStatoTransazione.setCodDominio(pagamento.getEnte().getCodDominio());
        gpChiediStatoTransazione.setCodPortale(pagamento.getTributo().getApplicazione().getCodice());
        gpChiediStatoTransazione.setIuv(pagamento.getIuv());
        return gpChiediStatoTransazione;
    }

    public GpAnnullaVersamento gpAnnullaVersamento(String codApplicazione, String codVersamentoEnte) {

        GpAnnullaVersamento gpAnnullaVersamento = new GpAnnullaVersamento();
        gpAnnullaVersamento.setCodApplicazione(codApplicazione);
        gpAnnullaVersamento.setCodVersamentoEnte(codVersamentoEnte);
        return gpAnnullaVersamento;
    }


    public GpChiediListaVersamenti gpCercaVersamentiRequest(User user, StatoVersamento... statoVersamento) {
        GpChiediListaVersamenti gpChiediListaVersamenti = new GpChiediListaVersamenti();
        gpChiediListaVersamenti.setCodUnivocoDebitore(user.getFiscalcode());
        gpChiediListaVersamenti.setCodPortale(providerService.loadPropertiesGovPay().getProperty(PagamentiOnlineKey.COD_PORTALE));
        List<StatoVersamento> statiVersamenti = Arrays.stream(statoVersamento).collect(Collectors.toList());
        gpChiediListaVersamenti.getStato().addAll(statiVersamenti);
        gpChiediListaVersamenti.setOrdinamento("DATA_SCADENZA_DES");
        return gpChiediListaVersamenti;
    }

    public GpChiediStatoVersamento gpChiediStatoVersamento(String codApplicazione, String codVersamentoEnte) {
        GpChiediStatoVersamento gpChiediStatoVersamento = new GpChiediStatoVersamento();
        gpChiediStatoVersamento.setCodApplicazione(codApplicazione);
        gpChiediStatoVersamento.setCodVersamentoEnte(codVersamentoEnte);
        return gpChiediStatoVersamento;
    }

    public it.govpay.servizi.v2_3.gprnd.GpChiediListaFlussiRendicontazione gpChiediListaFlussiRendicontazione(String codApplicazione, String codDominio) {
        it.govpay.servizi.v2_3.gprnd.GpChiediListaFlussiRendicontazione gpChiediListaFlussiRendicontazione = new it.govpay.servizi.v2_3.gprnd.GpChiediListaFlussiRendicontazione();
        gpChiediListaFlussiRendicontazione.setCodApplicazione(codApplicazione);
        gpChiediListaFlussiRendicontazione.setCodDominio(codDominio);
        return gpChiediListaFlussiRendicontazione;
    }

    public it.govpay.servizi.v2_3.gprnd.GpChiediFlussoRendicontazione gpChiediFlussoRendicontazione(String codApplicazione, String codFlusso) {
        it.govpay.servizi.v2_3.gprnd.GpChiediFlussoRendicontazione gpChiediFlussoRendicontazione = new it.govpay.servizi.v2_3.gprnd.GpChiediFlussoRendicontazione();
        gpChiediFlussoRendicontazione.setCodApplicazione(codApplicazione);
        gpChiediFlussoRendicontazione.setCodFlusso(codFlusso);
        return gpChiediFlussoRendicontazione;
    }


}
