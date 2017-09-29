package it.publisys.pagamentionline;

import com.google.zxing.*;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import com.itextpdf.text.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import it.gov.digitpa.schemas._2011.pagamenti.CtRicevutaTelematica;
import it.gov.digitpa.schemas._2011.pagamenti.CtRichiestaPagamentoTelematico;
import it.govpay.bd.GovpayConfig;
import it.govpay.servizi.PagamentiTelematiciPAService;
import it.govpay.servizi.commons.*;
import it.govpay.servizi.commons.Pagamento;

import it.govpay.servizi.pa.PaNotificaTransazione;
import it.govpay.servizi.v2_3.PagamentiTelematiciGPApp;
import it.govpay.servizi.v2_3.PagamentiTelematiciGPAppService;
import it.govpay.servizi.v2_3.PagamentiTelematiciGPPrt;
import it.govpay.servizi.v2_3.PagamentiTelematiciGPPrtService;
import it.govpay.servizi.v2_3.gpapp.*;
import it.govpay.servizi.v2_3.gpprt.*;
import it.publisys.pagamentionline.config.AppConfig;
import it.publisys.pagamentionline.controller.common.HomeController;
import it.publisys.pagamentionline.domain.impl.*;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.dto.DebitoDTO;
import it.publisys.pagamentionline.service.EnteService;
import it.publisys.pagamentionline.service.RataService;
import it.publisys.pagamentionline.service.TipologiaTributoService;
import it.publisys.pagamentionline.service.TributoService;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.imageio.ImageIO;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.BindingProvider;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Francesco A. Tabino
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes = {AppConfig.class})
@WebAppConfiguration
public class GovPayTestSuite {

    private static final Logger _log = LoggerFactory.getLogger(HomeController.class);

    //configurazione per serviziclienti TEST
    private static final String urlApp = "http://serviziclienti.link.it/govpay/PagamentiTelematiciGPAppService";
    private static final String url = "http://serviziclienti.link.it/govpay/PagamentiTelematiciGPPrtService";
    private static final String username = "PSYS";
    private static final String usernameApp = "PSYS";
    private static final String password = "e987%df";
    private static final String codPortale = "PSYS";
    private static final String codDominio = "80002950766";
    private static final String codTributo = "TSCUOLA";
    private static final String iuv = "00000000000000001";


    private static PagamentiTelematiciGPPrt port;
    private static PagamentiTelematiciGPApp portApp;
    private static PagamentiTelematiciPAService portPA;

    private static Anagrafica anagraficaDebitore;


    @BeforeClass
    public static void setup() throws Exception {
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");
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
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, usernameApp);
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlApp);

        anagraficaDebitore = new Anagrafica();
        anagraficaDebitore.setCodUnivoco("RSSMRA85T10A562S");
        anagraficaDebitore.setRagioneSociale("Mario Rossi 2");
        anagraficaDebitore.setEmail("a@it");
        anagraficaDebitore.setIndirizzo("Via Roma");
        anagraficaDebitore.setCivico("1");
        anagraficaDebitore.setLocalita("Roma");
        anagraficaDebitore.setProvincia("Roma");
        anagraficaDebitore.setNazione("IT");
        anagraficaDebitore.setCap("00000");

    }


    @Test
    public void chiediStato() throws Exception {
        GpChiediStatoTransazione gpChiediStatoTransazione = new GpChiediStatoTransazione();
        gpChiediStatoTransazione.setCodDominio(codDominio);
        gpChiediStatoTransazione.setCodPortale(codPortale);
        gpChiediStatoTransazione.setIuv(iuv);
        gpChiediStatoTransazione.setCcp("n/a");
        GpChiediStatoTransazioneResponse gpChiediStatoTransazioneResponse = port.gpChiediStatoTransazione(gpChiediStatoTransazione);
    }

    @Test
    public void getDebito() {
        GpChiediListaVersamenti gpChiediListaVersamenti = new GpChiediListaVersamenti();
        gpChiediListaVersamenti.setCodUnivocoDebitore("RSSMRA85T10A562S");
        gpChiediListaVersamenti.setCodPortale(codPortale);
        List<StatoVersamento> versamentos = new ArrayList<StatoVersamento>();
        versamentos.add(StatoVersamento.NON_ESEGUITO);
        versamentos.add(StatoVersamento.ANNULLATO);
        gpChiediListaVersamenti.getStato().addAll(versamentos);
        gpChiediListaVersamenti.setOrdinamento("DATA_SCADENZA_DES");
        GpChiediListaVersamentiResponse response = port.gpChiediListaVersamenti(gpChiediListaVersamenti);
        GpChiediListaVersamentiResponse.Versamento versamento = response.getVersamento().get(0);
        //PER FARE PARSE DEL CODICE VERSAMENTO ENTE E RISALIRE AL TRIBUTO
        String codVersamentoEnte = versamento.getCodVersamentoEnte();
        String[] split = codVersamentoEnte.split("_");
        if (split.length > 1) {
            String tributo = split[1];
            String sottotipologia = split[2];
            String anno = split[3];
            String canone_accertamento = split[5];
            if (split.length > 6) {
                String rata = split[6];
                if (rata.isEmpty()) {
                    _log.info("canone_accertamento: sono maggiore di 6");
                     // pagamento.setRata(rataService.getAllRata(tributo1).get(0));
                }
            } else {
                _log.info("canone_accertamento: sono minore di 6");
                //pagamento.setRata(new Rata());
            }
        } else {
            _log.info("faccio scegliere all'utente");
        }
    }

    @Test
    public void createPDF() {
        Document doc = new Document(PageSize.A4, 0.0f, 0.0f, 42.50f, 42.50f);
        PdfWriter docWriter = null;
        String codice = "PAGOPA|002|001000000000000141|80002950766|100";
        String numeroAvviso = codice.split("\\|")[2];

        try {
            String path = "C:/Users/micaputo.REG-BAS/Desktop/sample.pdf";
            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.addAuthor("Comune di test");
            doc.addCreator("https://pagopa.comune di test/pagamentionline");
            doc.addTitle("AVVISO DI PAGAMENTO");

            doc.open();

            String absoluteContext = "C:/Progetti/pubpay/pagamenti-on-line/trunk/src/main/webapp/";

            Image imageRB = Image.getInstance(absoluteContext + "/resources/img/logo_80002950766.jpg");
            imageRB.setIndentationLeft(42.50f);
            doc.add(imageRB);

            Image imagePP = Image.getInstance(absoluteContext + "/resources/img/logo_pagopa4.jpg");

            imagePP.setAbsolutePosition(PageSize.A4.getWidth() - 42.50f - imagePP.getWidth(), (PageSize.A4.getHeight() - 42.50f - imagePP.getHeight()));

            doc.add(imagePP);

            Font fontItalic = new Font(Font.FontFamily.TIMES_ROMAN, 10.0f, Font.ITALIC);
            Font fontBoldItalic = new Font(Font.FontFamily.TIMES_ROMAN, 10.0f, Font.BOLDITALIC);


            doc.add(Chunk.NEWLINE);
            Paragraph paragraphTitle = new Paragraph("AVVISO DI PAGAMENTO", FontFactory.getFont("Calibri", 13, Font.BOLD));
            paragraphTitle.setAlignment(Paragraph.ALIGN_CENTER);
            doc.add(paragraphTitle);
            doc.add(Chunk.NEWLINE);


            Paragraph paragraphCFD = new Paragraph("Codice Fiscale Debitore: " + "CODICEFISCALE", FontFactory.getFont("Calibri", 10, Font.NORMAL));
            paragraphCFD.setIndentationLeft(42.50f);
            doc.add(paragraphCFD);

            Paragraph paragraphCF = new Paragraph("Codice Fiscale Ente Creditore: " + "80002950766", FontFactory.getFont("Calibri", 10, Font.NORMAL));
            paragraphCF.setIndentationLeft(42.50f);
            doc.add(paragraphCF);

            Paragraph paragraphCap = new Paragraph("Codice avviso pagamento: " + numeroAvviso, FontFactory.getFont("Calibri", 10, Font.NORMAL));
            paragraphCap.setIndentationLeft(42.50f);
            doc.add(paragraphCap);

            Paragraph paragraphIUV = new Paragraph("Codice IUV: " + "000000000000141", FontFactory.getFont("Calibri", 10, Font.NORMAL));
            paragraphIUV.setIndentationLeft(42.50f);
            doc.add(paragraphIUV);

            Paragraph paragraphImporto = new Paragraph("Importo versamento in Euro: " + "1.00", FontFactory.getFont("Calibri", 10, Font.NORMAL));
            paragraphImporto.setIndentationLeft(42.50f);
            doc.add(paragraphImporto);

            Paragraph paragraphData = new Paragraph("Data scadenza: " + "31/12/2017", FontFactory.getFont("Calibri", 10, Font.NORMAL));
            paragraphData.setIndentationLeft(42.50f);
            doc.add(paragraphData);

            Paragraph paragraphCausale = new Paragraph("Causale: " + "Pagamento Tassa abilitazione esercizio professionale - Rata unica pari ad euro 104", FontFactory.getFont("Calibri", 10, Font.NORMAL));
            paragraphCausale.setIndentationLeft(42.50f);
            doc.add(paragraphCausale);
            doc.add(Chunk.NEWLINE);

            Paragraph paragraph3 = new Paragraph("\"L'importo del presente documento potrebbe subire variazioni rispetto a quanto sopra riportato in quanto aggiornato automaticamente dal sistema (in funzione di eventuali sgravi, note di credito, indennità di mora, sanzioni o interessi, ecc.). Il prestatore di servizi di pagamento presso il quale è presentato potrebbe pertanto richiedere un importo diverso da quello indicato sul documento stesso\".", fontItalic);
            paragraph3.setIndentationLeft(42.50f);
            paragraph3.setIndentationRight(42.50f);
            paragraph3.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            doc.add(paragraph3);
            doc.add(Chunk.NEWLINE);


            Chunk p00 = new Chunk("Il servizio offerto dalla Regione Basilicata è disponibile dal lunedì al venerdì dalle 8.00 alle 20.00 ed il sabato dalle 8.00 alle 14.00", fontBoldItalic);
            Chunk p01 = new Chunk("\"Attraverso il sistema", fontItalic);
            Chunk p02 = new Chunk(" pagoPA® ", fontBoldItalic);
            Chunk p03 = new Chunk(" è possibile effettuare il pagamento con le seguenti modalità:", fontItalic);

            Chunk p21 = new Chunk(" è un  ", fontItalic);
            Chunk p22 = new Chunk("sistema pubblico ", fontBoldItalic);
            Chunk p23 = new Chunk("- fatto di regole, standard e strumenti definiti ", fontItalic);
            Chunk p24 = new Chunk("dall'Agenzia per l'Italia Digitale ", fontBoldItalic);
            Chunk p25 = new Chunk("e accettati dalla Pubblica Amministrazione e dai PSP aderenti all'iniziativa - che garantisce ", fontItalic);
            Chunk p26 = new Chunk("a privati e aziende di effettuare pagamenti elettronici alla PA ", fontBoldItalic);
            Chunk p27 = new Chunk(" in modo sicuro e affidabile, semplice e in totale trasparenza nei costi di commissione. Si tratta di un'iniziativa promossa dalla ", fontItalic);
            Chunk p28 = new Chunk(" Presidenza del Consiglio dei Ministri ", fontBoldItalic);
            Chunk p29 = new Chunk("  alla quale tutte le PA sono obbligate ad aderire.\"", fontItalic);

            Paragraph paragraph00 = new Paragraph();
            paragraph00.add(p00);
            paragraph00.setIndentationLeft(42.50f);
            paragraph00.setIndentationRight(42.50f);
            doc.add(paragraph00);


            Paragraph paragraph0 = new Paragraph();
            paragraph0.add(p01);
            paragraph0.add(p02);
            paragraph0.add(p03);
            paragraph0.setIndentationLeft(42.50f);
            doc.add(paragraph0);

            Paragraph paragraph1 = new Paragraph("•  \tsul sito web di Regione Basilicata (https://pagopa.regione.basilicata.it/pagamentionline), accedendo all'apposita sezione e scegliendo tra gli strumenti disponibili: carta di credito o debito o prepagata. Per poter effettuare il pagamento occorre indicare il codice IUV presente sull'avviso \n" +
                    "\n" + "•  \tpresso le banche e altri prestatori di servizio di pagamento aderenti all'iniziativa tramite i canali da questi messi a disposizione (come ad esempio: home banking, ATM, APP da smartphone, tabaccherie convenzionate con Banca ITB, sportello, ecc). L'elenco dei punti abilitati a ricevere pagamenti tramite pagoPA® è disponibile alla pagina http://www.agid.gov.it/agenda-digitale/pubblica-amministrazione/pagamenti-elettronici/psp-aderenti-elenco. " +
                    "Per poter effettuare il pagamento occorre utilizzare il Codice Avviso di Pagamento oppure il QR Code o i Codici a Barre, presenti sulla stampa dell'avviso.\"\n", fontItalic);
            paragraph1.setIndentationLeft(84.96f);
            paragraph1.setIndentationRight(42.50f);
            paragraph1.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            doc.add(paragraph1);
            doc.add(Chunk.NEWLINE);

            Paragraph paragraph2 = new Paragraph();
            paragraph2.add(p02);
            paragraph2.add(p21);
            paragraph2.add(p22);
            paragraph2.add(p23);
            paragraph2.add(p24);
            paragraph2.add(p25);
            paragraph2.add(p26);
            paragraph2.add(p27);
            paragraph2.add(p28);
            paragraph2.add(p29);
            paragraph2.setIndentationLeft(42.50f);
            paragraph2.setIndentationRight(42.50f);
            paragraph2.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            doc.add(paragraph2);
            doc.add(Chunk.NEWLINE);


            String myString = "415808888897274180200010000000000001413902100";
            Barcode128 code128 = new Barcode128();
            code128.setCode(myString.trim());
            code128.setBarHeight(39.68f);
            code128.setCodeType(Barcode128.CODE128);
            PdfContentByte cb = docWriter.getDirectContent();
            Image code128Image = code128.createImageWithBarcode(cb, null, null);
            code128Image.scaleAbsolute(221.10f, 48.28f);
            code128Image.setAbsolutePosition(42.50f, 42.50f);
            doc.add(code128Image);

            //Non uso itext per via del bordo
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>(1);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");


            QRCode qrCode = Encoder.encode(codice, ErrorCorrectionLevel.L, hints);
            int width = qrCode.getMatrix().getWidth();
            int height = qrCode.getMatrix().getHeight();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            int[] rgbArray = new int[width * height];
            int i = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int grey = qrCode.getMatrix().get(x, y) > 0 ? 0x00 : 0xff;
                    rgbArray[y * width + x] = 0xff000000 | (0x00010101 * grey);
                    i++;
                }
            }
            image.setRGB(0, 0, width, height, rgbArray, 0, width);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            Image qrcodeImage = Image.getInstance(imageInByte);
            qrcodeImage.scaleAbsolute(99, 99);
            qrcodeImage.setAbsolutePosition(PageSize.A4.getWidth() - 42.50f - qrcodeImage.getScaledWidth(), 42.50f);
            doc.add(qrcodeImage);


        } catch (DocumentException dex) {
            dex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (doc != null) {
                doc.close();
            }
            if (docWriter != null) {
                docWriter.close();
            }
        }
    }


    @Test
    public void gpChiediListaVersamenti() {
        GpChiediListaVersamenti gpChiediListaVersamenti = gpCercaVersamentiRequest(StatoVersamento.NON_ESEGUITO);
        GpChiediListaVersamentiResponse response = port.gpChiediListaVersamenti(gpChiediListaVersamenti);
        for (GpChiediListaVersamentiResponse.Versamento versamento : response.getVersamento()) {
            _log.info(versamento.getStato().value());
        }
        _log.info("size: " + response.getVersamento().size());
    }

    public GpChiediListaVersamenti gpCercaVersamentiRequest(StatoVersamento... statoVersamento) {
        GpChiediListaVersamenti gpChiediListaVersamenti = new GpChiediListaVersamenti();
        gpChiediListaVersamenti.setCodUnivocoDebitore("CODICEFISCALE");
        gpChiediListaVersamenti.setCodPortale(codPortale);
        List<StatoVersamento> versamentos = new ArrayList<>();
        versamentos.add(StatoVersamento.NON_ESEGUITO);
        versamentos.add(StatoVersamento.ANNULLATO);

        gpChiediListaVersamenti.getStato().addAll(versamentos);
        gpChiediListaVersamenti.setOrdinamento("DATA_SCADENZA_DES");
        return gpChiediListaVersamenti;
    }


    @Test
    public void gpAvviaTransazionePagamento() throws IOException {

        GpAvviaTransazionePagamento req = new GpAvviaTransazionePagamento();

        req.setCodPortale(codPortale);
        //wisp
        GpAvviaTransazionePagamento.SceltaWisp wisp = new GpAvviaTransazionePagamento.SceltaWisp();
        wisp.setCodKeyWISP("c9561e5ce50d42799c78e12e0f2a3bae");
        wisp.setCodKeyPA("4087689793b94a10b89ee4c9c0f6b925");
        wisp.setCodDominio(codDominio);
        req.setUrlRitorno("http://www.host.it/url/ritorno");
        req.setAutenticazione(TipoAutenticazione.N_A);
        req.setSceltaWisp(wisp);


        Versamento versamento = new Versamento();
        versamento.setCausale("Causale prova");
        versamento.setCodApplicazione(codPortale);
        versamento.setCodVersamentoEnte("1460980677037");
        versamento.setCodDominio(codDominio);
        versamento.setDebitore(anagraficaDebitore);
        versamento.setAggiornabile(false);
        versamento.setImportoTotale(new BigDecimal(200.00));

        Versamento.SingoloVersamento singoloVersamento = new Versamento.SingoloVersamento();
        singoloVersamento.setCodSingoloVersamentoEnte("1460980677037_1");
        singoloVersamento.setImporto(new BigDecimal(200.00));
        singoloVersamento.setCodTributo(codTributo);

        versamento.getSingoloVersamento().add(singoloVersamento);
        req.getVersamentoOrVersamentoRef().add(versamento);

        GpAvviaTransazionePagamentoResponse response = port.gpAvviaTransazionePagamento(req, null);


        _log.info("SESSION ID" + response.getPspSessionId());
        _log.info("CODICE OPERAZIONE" + response.getCodOperazione());
        _log.info("SIZE: " + response.getRifTransazione().size());
        if (!response.getRifTransazione().isEmpty()) {
            _log.info("IUV" +response.getRifTransazione().get(0).getIuv());
            _log.info("CCP" +response.getRifTransazione().get(0).getCcp());
        }
    }

    @Test
    public void gpCaricaVersamento() {
        GpCaricaVersamento gpCaricaVersamento = new GpCaricaVersamento();
        gpCaricaVersamento.setAggiornaSeEsiste(true);

        Versamento p = new Versamento();
        p.setCodApplicazione(codPortale);
        p.setCodVersamentoEnte("1460980677037");
        p.setCodDominio(codDominio);
        p.setDebitore(anagraficaDebitore);
        p.setImportoTotale(new BigDecimal(100));
        p.setDataScadenza(new Date());
        p.setAggiornabile(true);
        p.setCausale("Causale prova");

        Versamento.SingoloVersamento singoloVersamento = new Versamento.SingoloVersamento();
        singoloVersamento.setCodSingoloVersamentoEnte("1460980677037_1");
        singoloVersamento.setImporto(new BigDecimal(100.00));
        singoloVersamento.setCodTributo(codTributo);


        p.getSingoloVersamento().add(singoloVersamento);
        gpCaricaVersamento.setVersamento(p);
        GpCaricaVersamentoResponse response = portApp.gpCaricaVersamento(gpCaricaVersamento, null);

    }

}
