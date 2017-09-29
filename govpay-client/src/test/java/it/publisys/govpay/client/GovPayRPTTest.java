package it.publisys.govpay.client;

import it.govpay.servizi.commons.*;
import it.govpay.servizi.v2_3.*;
import it.govpay.servizi.v2_3.PagamentiTelematiciGPApp;
import it.govpay.servizi.v2_3.PagamentiTelematiciGPAppService;
import it.govpay.servizi.v2_3.PagamentiTelematiciGPPrt;
import it.govpay.servizi.v2_3.PagamentiTelematiciGPPrtService;
import it.govpay.servizi.v2_3.gpprt.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.BindingProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Date;
import java.util.UUID;

public class GovPayRPTTest {

    private static final String urlApp = "http://demo.publisys.it/govpay/PagamentiTelematiciGPAppService";
    private static final String url = "http://demo.publisys.it/govpay/PagamentiTelematiciGPPrtService";
    private static final String username = "pubportale";
    private static final String password = "password";
    private static final String codApplicazione = "PubbApp1";
    private static final String codPortale = "PubPortale";
    private static final String codDominio = "00975860768";
    private static final String codTributo = "BOLLO";
    private static final String backurl = "";
    private static final String codPsp = "GovPAYPsp1";
    private static final String codCanale = "GovPAYPsp1_CP";


    private static PagamentiTelematiciGPPrt port;
    private static PagamentiTelematiciGPApp portApp;
    //private static PagamentiTelematiciPAService portPA;

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


}
