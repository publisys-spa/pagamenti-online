package it.publisys.govpay.client;

import it.govpay.servizi.commons.*;
import it.govpay.servizi.v2_3.*;
import it.govpay.servizi.v2_3.commons.GpResponse;
import it.govpay.servizi.v2_3.gpapp.*;
import it.govpay.servizi.v2_3.gpprt.*;
import it.govpay.servizi.v2_3.gpprt.GpChiediStatoVersamentoResponse;
import org.springframework.util.Assert;

import javax.xml.ws.BindingProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Francesco A. Tabino
 */
public class GovPayClient {

    private final String url;
    private final String urlApp;
    private final String username;
    private final String usernameApp;
    private final String password;
    private final String passwordApp;

    private final String codPortale;
    private final String codApplicazione;
    private final String codEnte;
    private final String codDominioEnte;

    private final PagamentiTelematiciGPPrt port;
    private final PagamentiTelematiciGPApp portApp;

    private final String message = "[Assertion failed] - %s is required; it must not be null";
    private final String maxMessage = "[Assertion failed] - %s must not be empty: it must contain at least 1 element and max 5";

    /**
     * @param url
     * @param codPortale
     * @param username
     * @param password
     * @param urlApp
     * @param codApplicazione
     * @param usernameApp
     * @param passwordApp
     * @param codEnte
     */
    public GovPayClient(String url, String codPortale, String username, String password, String urlApp, String codApplicazione, String usernameApp, String passwordApp, String codEnte, String codDominioEnte) {
        this.url = url;
        this.codPortale = codPortale;
        this.username = username;
        this.password = password;

        this.urlApp = urlApp;
        this.codApplicazione = codApplicazione;
        this.usernameApp = usernameApp;
        this.passwordApp = passwordApp;

        this.codEnte = codEnte;
        this.codDominioEnte = codDominioEnte;

        Assert.notNull(url, String.format(message, "url PagamentiTelematiciGPPrtservice"));
        Assert.notNull(codPortale, String.format(message, "codPortale"));
        Assert.notNull(username, String.format(message, "username"));
        Assert.notNull(password, String.format(message, "password"));

        Assert.notNull(urlApp, String.format(message, "urlApp PagamentiTelematiciGPAppservice"));
        Assert.notNull(codApplicazione, String.format(message, "codApplicazione"));
        Assert.notNull(usernameApp, String.format(message, "usernameApp"));
        Assert.notNull(passwordApp, String.format(message, "passwordApp"));

        Assert.notNull(codEnte, String.format(message, "codEnte"));
        Assert.notNull(codDominioEnte, String.format(message, "codDominioEnte"));


        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        username,
                        password.toCharArray());
            }
        });

        port = this.createGPPrtPort();
        portApp = this.createGPAppPort();

    }

    private PagamentiTelematiciGPApp createGPAppPort() {
        PagamentiTelematiciGPApp _port = new PagamentiTelematiciGPAppService().getGPAppPort();
        ((BindingProvider) _port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, usernameApp);
        ((BindingProvider) _port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, passwordApp);
        ((BindingProvider) _port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlApp);
        return _port;

    }

    private PagamentiTelematiciGPPrt createGPPrtPort() {
        PagamentiTelematiciGPPrt _port = new PagamentiTelematiciGPPrtService().getGPPrtPort();
        ((BindingProvider) _port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
        ((BindingProvider) _port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
        ((BindingProvider) _port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        return _port;
    }
/************************************PagamentiTelematiciGPApp********************************************************
 Servizio erogato da GovPay a beneficio dei Sistemi Gestionali delle Posizioni Debitorie dell'Ente.Operazioni esposte:
 - gpGeneraIUV
 - gpCaricaIUV
 - gpCaricaVersamento
 - gpAnnullaVersamento
 - gpNotificaPagamento
 - gpChiediStatoVersamento
 - gpChiediListaFlussiRendicondazione [DA IMPLEMENTARE SE SERVE]
 - gpChiediFlussoRendicontazione [DA IMPLEMENTARE SE SERVE]
 **********************************************************************************************************************/



    /**
     * Consente al sistema gestionale dell'ente di caricare versamenti su GovPay, tali pagamenti saranno posti in uno stato di ATTESA
     *
     * @param causale           causale del pagamento
     * @param dataScadenza      data scadenza del pagamento
     * @param importoTotale     importo totale del pagamento
     * @param debitore          dati anagrafici del debitore
     * @param singoloVersamento dati singolo pagamento
     * @return {@link EsitoOperazione}
     */
    public GpCaricaVersamentoResponse gpCaricaVersamento(boolean aggiornaSeEsiste, String codUnitaOperativa, boolean aggiornabile, Anagrafica debitore, BigDecimal importoTotale, Date dataScadenza,
                                                         String causale, Versamento.SingoloVersamento... singoloVersamento) {

        Assert.notNull(causale, String.format(message, "causale"));
        Assert.notNull(dataScadenza, String.format(message, "dataScadenza"));
        Assert.notNull(importoTotale, String.format(message, "importoTotale"));
        Assert.notNull(debitore, String.format(message, "debitore"));
        Assert.notNull(singoloVersamento, String.format(message, "singoloVersamento"));
        Assert.isTrue(singoloVersamento.length > 0 && singoloVersamento.length < 6, String.format(maxMessage, "lista versamenti"));

        GpCaricaVersamento gpCaricaVersamento = new GpCaricaVersamento();
        gpCaricaVersamento.setAggiornaSeEsiste(aggiornaSeEsiste);

        Versamento p = new Versamento();
        p.setCodApplicazione(codApplicazione);
        p.setCodVersamentoEnte(codEnte);
        p.setCodDominio(codDominioEnte);
        p.setCodUnitaOperativa(codUnitaOperativa);
        p.setDebitore(debitore);
        p.setImportoTotale(importoTotale);
        p.setDataScadenza(dataScadenza);
        p.setAggiornabile(aggiornabile);
        p.setCausale(causale);

        List<Versamento.SingoloVersamento> singoloVersamentos = Arrays.stream(singoloVersamento).collect(Collectors.toList());
        p.getSingoloVersamento().addAll(singoloVersamentos);
        gpCaricaVersamento.setVersamento(p);
        gpCaricaVersamento.setGeneraIuv(true);
        return portApp.gpCaricaVersamento(gpCaricaVersamento, null);
    }

    /**
     * Consente al sistema gestionale dell'ente di cancellare un versamento in ATTESA su GovPay     *
     *
     * @param codPagamentoEnte identificativo del debito nel dominio dell'applicazione che lo gestisce
     * @return {@link GpResponse}
     */
    public GpResponse gpAnnullaVersamento(String codPagamentoEnte) {

        Assert.notNull(codPagamentoEnte, String.format(message, "codPagamentoEnte"));

        GpAnnullaVersamento gpAnnullaVersamento = new GpAnnullaVersamento();
        gpAnnullaVersamento.setCodApplicazione(codApplicazione);
        gpAnnullaVersamento.setCodVersamentoEnte(codPagamentoEnte);
        return portApp.gpAnnullaVersamento(gpAnnullaVersamento);
    }

    /**
     * Notifica l'avvenuto pagamento di un versamento con canali alternativi a PagoPA
     *
     * @param codVersamentoEnte identificativo del debito nel dominio dell'applicazione che lo gestisce
     * @return {@link GpResponse}
     */
    public GpResponse gpNotificaPagamento(String codVersamentoEnte) {

        Assert.notNull(codVersamentoEnte, String.format(message, "codVersamentoEnte"));

        GpNotificaPagamento gpNotificaPagamento = new GpNotificaPagamento();
        gpNotificaPagamento.setCodApplicazione(codApplicazione);
        gpNotificaPagamento.setCodVersamentoEnte(codVersamentoEnte);
        return portApp.gpNotificaPagamento(gpNotificaPagamento);
    }

    /**
     * Consente di recuperare lo stato di un versamento a fronte di anomalie
     *
     * @param codVersamentoEnte identificativo del debito nel dominio dell'applicazione che lo gestisce
     * @return {@link GpChiediStatoVersamentoResponse}
     */
    public it.govpay.servizi.v2_3.gpprt.GpChiediStatoVersamentoResponse gpChiediStatoVersamento(String codVersamentoEnte) {

        Assert.notNull(codVersamentoEnte, String.format(message, "codPagamentoEnte"));

        it.govpay.servizi.v2_3.gpprt.GpChiediStatoVersamento gpChiediStatoVersamento = new it.govpay.servizi.v2_3.gpprt.GpChiediStatoVersamento();
        gpChiediStatoVersamento.setCodApplicazione(codApplicazione);
        gpChiediStatoVersamento.setCodVersamentoEnte(codVersamentoEnte);

        return port.gpChiediStatoVersamento(gpChiediStatoVersamento);
    }

/************************************PagamentiTelematiciGPPrt**********************************************************
 Servizio erogato da GovPay a beneficio dei Portali al cittadino degli Enti creditori.Operazioni esposte:
 - gpChiediListaPsp
 - gpChiediSceltaWISP
 - gpAvviaTransazionePagamento
 - gpChiediStatoPagamento
 - gpChiediStatoTransazione
 - gpAvviaRichiestaStorno
 - gpChiediStatoRichiestaStorno
 - gpChiediListaVersamenti
 **********************************************************************************************************************/

    /**
     * Richiede a GovPay la lista dei PSP disponibili per il pagamento (x portali con una versione propria del WISP)
     *
     * @return {@link GpChiediListaPspResponse}
     */
    public GpChiediListaPspResponse gpChiediListaPsp() {
        GpChiediListaPsp gpChiediListaPsp = new GpChiediListaPsp();
        return port.gpChiediListaPsp(gpChiediListaPsp);
    }

    /**
     * Consente di recuperare la scelta del PSP operata da un utente sul WISP centralizzato di AgID
     *
     * @param keyPA   identificativo sessione assegnato dal Portale
     * @param keyWISP identificativo sessione inviato dal WISP
     * @return {@link GpChiediSceltaWispResponse}
     */
    public GpChiediSceltaWispResponse gpChiediSceltaWisp(String keyPA, String keyWISP) {

        Assert.notNull(keyPA, String.format(message, "keyPA"));
        Assert.notNull(keyWISP, String.format(message, "keyWISP"));

        GpChiediSceltaWisp gpChiediSceltaWisp = new GpChiediSceltaWisp();
        gpChiediSceltaWisp.setCodDominio(codDominioEnte);
        gpChiediSceltaWisp.setCodPortale(codPortale);
        gpChiediSceltaWisp.setCodKeyPA(keyPA);
        gpChiediSceltaWisp.setCodKeyWISP(keyWISP);

        return port.gpChiediSceltaWisp(gpChiediSceltaWisp);
    }

    /**
     * Consente al portale di avviare una transazione di pagamento per uno o più versamenti (della stessa stazione)
     *
     * @param autenticazione modalità di autenticazione del cittadino sul portale
     * @param versante       dati anagrafici del versante
     * @return {@link GpAvviaTransazionePagamentoResponse }
     * @throws IOException
     */

    public GpAvviaTransazionePagamentoResponse gpAvviaTransazionePagamento(boolean aggiornaSeEsiste, List<VersamentoKey> versamentiRef, List<Versamento> versamenti, GpAvviaTransazionePagamento.SceltaWisp sceltaWisp,
                                                                           Canale canale, TipoAutenticazione autenticazione, Anagrafica versante, String ibanAddebito, String callbackUrl ) throws IOException {

        Assert.notNull(autenticazione, String.format(message, "autenticazione"));
        Assert.notNull(versante, String.format(message, "versante"));

        GpAvviaTransazionePagamento req = new GpAvviaTransazionePagamento();

        req.setCodPortale(codPortale);
        req.setAggiornaSeEsiste(aggiornaSeEsiste);
        req.getVersamentoOrVersamentoRef().addAll(versamentiRef);
        req.getVersamentoOrVersamentoRef().addAll(versamenti);

        req.setSceltaWisp(sceltaWisp);
        req.setCanale(canale);
        req.setAutenticazione(autenticazione);
        req.setVersante(versante);
        req.setIbanAddebito(ibanAddebito);
        req.setUrlRitorno(callbackUrl);

        GpAvviaTransazionePagamentoResponse response = port.gpAvviaTransazionePagamento(req, null);
        return response;
    }


    /**
     * Consente al portale di verificare lo stato di un pagamento precedentemente avviato
     *
     * @param iuv identificativo univoco del versamento
     * @param ccp codice contesto pagamento
     * @return {@link GpChiediStatoTransazioneResponse}
     */
    public GpChiediStatoTransazioneResponse gpChiediStatoTransazione(String iuv, String ccp) {

        Assert.notNull(iuv, String.format(message, "iuv"));
        Assert.notNull(ccp, String.format(message, "ccp"));

        GpChiediStatoTransazione gpChiediStatoTransazione = new GpChiediStatoTransazione();
        gpChiediStatoTransazione.setCodDominio(codDominioEnte);
        gpChiediStatoTransazione.setCodPortale(codPortale);
        gpChiediStatoTransazione.setIuv(iuv);
        gpChiediStatoTransazione.setCcp(ccp);

        return port.gpChiediStatoTransazione(gpChiediStatoTransazione);

    }

    /**
     * Consente al portale di inviare una richiesta di storno per uno o più pagamenti
     *
     * @param iuv identificativo univoco del versamento
     * @param ccp codice contesto pagamento
     * @return {@link GpAvviaRichiestaStornoResponse}
     */
    public GpAvviaRichiestaStornoResponse gpAvviaRichiestaStorno(String iuv, String ccp, String causaleRevoca, String datiAggiuntivi, GpAvviaRichiestaStorno.Pagamento... pagamenti) {

        Assert.notNull(iuv, String.format(message, "iuv"));
        Assert.notNull(ccp, String.format(message, "ccp"));

        GpAvviaRichiestaStorno gpAvviaRichiestaStorno = new GpAvviaRichiestaStorno();
        gpAvviaRichiestaStorno.setCodDominio(codDominioEnte);
        gpAvviaRichiestaStorno.setCodPortale(codPortale);
        gpAvviaRichiestaStorno.setIuv(iuv);
        gpAvviaRichiestaStorno.setCcp(ccp);
        gpAvviaRichiestaStorno.setCausaleRevoca(causaleRevoca);
        gpAvviaRichiestaStorno.setDatiAggiuntivi(datiAggiuntivi);
        List<GpAvviaRichiestaStorno.Pagamento> pagamentos = Arrays.stream(pagamenti).collect(Collectors.toList());
        gpAvviaRichiestaStorno.getPagamento().addAll(pagamentos);

        return port.gpAvviaRichiestaStorno(gpAvviaRichiestaStorno);

    }

    /**
     * Consente al portale di verificare lo stato di una richiesta di storno precedentemente avviata
     *
     * @param codRichiestaStorno identificativo della richiesta di storno
     * @return {@link GpChiediStatoRichiestaStornoResponse}
     */
    public GpChiediStatoRichiestaStornoResponse gpChiediStatoRichiestaStorno(String codRichiestaStorno) {

        Assert.notNull(codRichiestaStorno, String.format(message, "codRichiestaStorno"));

        GpChiediStatoRichiestaStorno gpChiediStatoRichiestaStorno = new GpChiediStatoRichiestaStorno();
        gpChiediStatoRichiestaStorno.setCodPortale(codPortale);
        gpChiediStatoRichiestaStorno.setCodRichiestaStorno(codRichiestaStorno);

        return port.gpChiediStatoRichiestaStorno(gpChiediStatoRichiestaStorno);

    }

    /**
     * Consente al portale di acquisire la lista dei versamenti caricati su GovPay afferenti ad un debitore
     *
     * @param codUnivocoDebitore codice fiscale del debitore
     * @return {@link GpChiediListaVersamentiResponse}
     */
    public GpChiediListaVersamentiResponse gpChiediListaVersamenti(String codUnivocoDebitore, String ordinamento, StatoVersamento... statoVersamentos) {

        Assert.notNull(codUnivocoDebitore, String.format(message, "codUnivocoDebitore"));

        GpChiediListaVersamenti gpChiediListaVersamenti = new GpChiediListaVersamenti();
        gpChiediListaVersamenti.setCodPortale(codPortale);
        gpChiediListaVersamenti.setCodUnivocoDebitore(codUnivocoDebitore);
        gpChiediListaVersamenti.setOrdinamento(ordinamento);

        List<StatoVersamento> versamentos = Arrays.stream(statoVersamentos).collect(Collectors.toList());
        gpChiediListaVersamenti.getStato().addAll(versamentos);

        return port.gpChiediListaVersamenti(gpChiediListaVersamenti);
    }


}



