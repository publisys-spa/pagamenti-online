package it.publisys.govpay.client;

import it.govpay.servizi.commons.*;
import it.govpay.servizi.v2_3.*;
import it.govpay.servizi.v2_3.gpprt.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.ws.BindingProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author Francesco A. Tabino
 */
public class GovPayTestSuite {

    private static final String urlApp = "";
    private static final String url = "";
    private static final String username = "";
    private static final String password = "";
    private static final String codApplicazione = "";
    private static final String codPortale = "";
    private static final String codDominio = "";
    private static final String codTributo = "";
    private static final String backurl = "";
    private static final String codPsp = "GovPAYPsp1";
    private static final String codCanale = "GovPAYPsp1_CP";


    private static PagamentiTelematiciGPPrt port;
    private static PagamentiTelematiciGPApp portApp;

    private static Anagrafica anagraficaDebitore;

    @BeforeClass
    public static void setup() throws Exception {

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        username,
                        password.toCharArray());
            }
        });


        port = new PagamentiTelematiciGPPrtService().getGPPrtPort();
        ((BindingProvider) port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
        ((BindingProvider) port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);


        portApp = new PagamentiTelematiciGPAppService().getGPAppPort();
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlApp);


        anagraficaDebitore = new Anagrafica();
        anagraficaDebitore.setRagioneSociale("Mario Rossi");
        anagraficaDebitore.setEmail("margoneto@publisys.it");
        anagraficaDebitore.setCodUnivoco("DDDDDD0D00D000D");
        anagraficaDebitore.setIndirizzo("Via Roma");
        anagraficaDebitore.setCivico("1");
        anagraficaDebitore.setLocalita("Roma");
        anagraficaDebitore.setProvincia("Roma");
        anagraficaDebitore.setNazione("IT");
        anagraficaDebitore.setCap("00000");
    }


    @Test
    public void pagaSpontaneamente() throws Exception {

        System.out.println(">>>>>>>>>> Invio richiesta di pagamento senza IUV");
        GpAvviaTransazionePagamento request = gpAvviaTransazionePagamento();
        GpAvviaTransazionePagamentoResponse response = port.gpAvviaTransazionePagamento(request, null);

        System.out.println(">>>>>>>>>> Ritornato esito con codice " + response.getCodOperazione());
        Assert.assertEquals(response.getCodOperazione(), EsitoOperazione.OK);

        if (null != response.getRifTransazione() && !response.getRifTransazione().isEmpty()) {
            System.out.println(">>>>>>>>>> IUV associato al pagamento: " + response.getRifTransazione().get(0).getIuv());

            if (response.getUrlRedirect() != null) {
                System.out.println();
                System.out.println(">>>>>>>>>> Procedere al pagamento utilizzando un browser alla seguente URL:");
                System.out.println(response.getUrlRedirect());
            }
            System.out.println(">>>>>>>>>> Sistema in attesa della notifica da GovPay....");
        }
    }

    @Test
    public void gpChiediListaVersamenti() {

        GpChiediListaVersamenti gpChiediListaVersamenti = new GpChiediListaVersamenti();
        gpChiediListaVersamenti.setCodUnivocoDebitore("RGNMRZ81S11M109P");
        gpChiediListaVersamenti.setCodPortale(codPortale);
       /* List<StatoVersamento> versamentos = new ArrayList<StatoVersamento>();
        versamentos.add(StatoVersamento.NON_ESEGUITO);
        versamentos.add(StatoVersamento.ANNULLATO);
        gpChiediListaVersamenti.getStato().addAll(versamentos);
        gpChiediListaVersamenti.setOrdinamento("DATA_SCADENZA_DES");*/
        GpChiediListaVersamentiResponse response = port.gpChiediListaVersamenti(gpChiediListaVersamenti);
        //   System.out.println(response.getCodEsitoOperazione());
        System.out.println(response.getCodEsito());
    }


    private GpAvviaTransazionePagamento gpAvviaTransazionePagamento() throws IOException, InterruptedException {

        Versamento v = buildVersamento();

        GpAvviaTransazionePagamento gpAvviaTransazionePagamento = new GpAvviaTransazionePagamento();
        gpAvviaTransazionePagamento.setAutenticazione(TipoAutenticazione.N_A);
        Canale canale = new Canale();
        canale.setCodCanale(codCanale);
        canale.setCodPsp(codPsp);
        canale.setTipoVersamento(TipoVersamento.CP);
        gpAvviaTransazionePagamento.setCanale(canale);
        gpAvviaTransazionePagamento.setCodPortale(codPortale);
        gpAvviaTransazionePagamento.setIbanAddebito(null);
        gpAvviaTransazionePagamento.setUrlRitorno(backurl);
        gpAvviaTransazionePagamento.setVersante(null);

        gpAvviaTransazionePagamento.getVersamentoOrVersamentoRef().add(v);

        return gpAvviaTransazionePagamento;

    }

    public static Versamento buildVersamento() throws InterruptedException {
        Versamento versamento = new Versamento();
        versamento.setAggiornabile(false);
        versamento.setCausale("Causale");
        versamento.setCodApplicazione(codApplicazione);
        versamento.setCodDominio(codDominio);
        versamento.setCodVersamentoEnte(new Date().getTime() + "");

        versamento.setDebitore(anagraficaDebitore);
        versamento.setImportoTotale(BigDecimal.valueOf(Double.parseDouble("1")));

        Versamento.SingoloVersamento singoloVersamento = new Versamento.SingoloVersamento();
        singoloVersamento.setCodSingoloVersamentoEnte(versamento.getCodVersamentoEnte() + "_1");
        singoloVersamento.setCodTributo(codTributo);
        singoloVersamento.setImporto(BigDecimal.valueOf(Double.parseDouble("1")));
        versamento.getSingoloVersamento().add(singoloVersamento);

        return versamento;
    }

    @Test
    public void chiediStato() throws Exception {

        GpChiediStatoTransazione gpChiediStatoTransazione = new GpChiediStatoTransazione();
        gpChiediStatoTransazione.setCodDominio(codDominio);
        gpChiediStatoTransazione.setCodPortale(codPortale);
        gpChiediStatoTransazione.setIuv("RF73000000000000019");
        gpChiediStatoTransazione.setCcp("n/a");
        GpChiediStatoTransazioneResponse gpChiediStatoTransazioneResponse = port.gpChiediStatoTransazione(gpChiediStatoTransazione);
        System.out.println(">>>>>>>>>> Stato versamento iuv RF73000000000000019 ccp n/a:");
        System.out.println(gpChiediStatoTransazioneResponse.getTransazione().getStato().toString());
        System.out.println(gpChiediStatoTransazioneResponse.getTransazione().getEsito().toString());

    }

    @Test
    public void gpChiediSceltaWisp() {


        GpChiediSceltaWisp gpChiediSceltaWisp = new GpChiediSceltaWisp();
        gpChiediSceltaWisp.setCodDominio(codDominio);
        gpChiediSceltaWisp.setCodPortale(codPortale);
        //gpChiediSceltaWisp.setCodKeyPA("123456789");
        //gpChiediSceltaWisp.setCodKeyWISP("3d2ed262106a4e3e9ac42b79c3d4f6d716060154");

        GpChiediSceltaWispResponse gpChiediSceltaWispResponse = port.gpChiediSceltaWisp(gpChiediSceltaWisp);
    }


}
