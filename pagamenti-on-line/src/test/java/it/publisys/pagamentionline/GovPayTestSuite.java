package it.publisys.pagamentionline;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import it.govpay.servizi.*;
import it.govpay.servizi.commons.Anagrafica;
import it.govpay.servizi.commons.TipoAutenticazione;
import it.govpay.servizi.commons.Versamento;
import it.govpay.servizi.gpapp.GpCaricaVersamento;
import it.govpay.servizi.gpapp.GpCaricaVersamentoResponse;
import it.govpay.servizi.gpprt.*;
import it.govpay.servizi.pa.*;
import it.publisys.pagamentionline.config.AppConfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.BindingProvider;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Francesco A. Tabino
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes = {AppConfig.class})
@WebAppConfiguration
public class GovPayTestSuite {

    private static final String urlApp = "http://demo.publisys.it/govpay/PagamentiTelematiciGPAppService";
    private static final String url = "http://demo.publisys.it/govpay/PagamentiTelematiciGPPrtService";
    private static final String username = "pubportale";
    private static final String usernameApp = "pubapp1";
    private static final String password = "password";
    private static final String codPsp = "GovPAYPsp1";
    private static final String codApplicazione = "PubApp1";
    private static final String codPortale = "PubApp1";
    private static final String codEnte = "PubUff1";
    private static final String codDominio = "00975860768";
    private static final String codTributo = "BOLLO";
    private static final String backurl = "";


    //configurazione per serviziclienti
    /*private static final String urlApp = "http://serviziclienti.link.it/govpay/PagamentiTelematiciGPAppService";
    private static final String url = "http://serviziclienti.link.it/govpay/PagamentiTelematiciGPPrtService";
    private static final String username = "PSYS";
    private static final String usernameApp = "PSYS";
    private static final String password = "e987%df";
    private static final String codPortale = "PSYS";
    private static final String codDominio = "80002950766";
    private static final String codTributo = "TSCUOLA";*/


    private static PagamentiTelematiciGPPrt port;
    private static PagamentiTelematiciGPApp portApp;
    private static PagamentiTelematiciPAService portPA;

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
//
//
        portApp = new PagamentiTelematiciGPAppService().getGPAppPort();
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, usernameApp);
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
        ((BindingProvider) portApp).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlApp);
//
//
        anagraficaDebitore = new Anagrafica();
        anagraficaDebitore.setCodUnivoco("RSSMRA85T10A562S");
        anagraficaDebitore.setRagioneSociale("Mario Rossi 2");
        anagraficaDebitore.setEmail("acoviello@publisys.it");
        anagraficaDebitore.setIndirizzo("Via Roma");
        anagraficaDebitore.setCivico("1");
        anagraficaDebitore.setLocalita("Roma");
        anagraficaDebitore.setProvincia("Roma");
        anagraficaDebitore.setNazione("IT");
        anagraficaDebitore.setCap("00000");

    }


    @Test
    public void chiediStato() throws Exception {
        //con parametri http://serviziclienti.link.it/govpay/PagamentiTelematiciGPAppService
        GpChiediStatoTransazione gpChiediStatoTransazione = new GpChiediStatoTransazione();
        gpChiediStatoTransazione.setCodDominio(codDominio);
        gpChiediStatoTransazione.setCodPortale(codPortale);
        gpChiediStatoTransazione.setIuv("RF90000000000000004");
        gpChiediStatoTransazione.setCcp("n/a");
        GpChiediStatoTransazioneResponse gpChiediStatoTransazioneResponse = port.gpChiediStatoTransazione(gpChiediStatoTransazione);

        System.out.printf(gpChiediStatoTransazioneResponse.getTransazione().getStato().toString());

    }

    @Test
    public void createPDF() {

        Document doc = new Document(PageSize.A4, 0.0f, 0.0f, 42.48f, 42.48f);
        PdfWriter docWriter = null;

        try {
            String path = "C:/Users/micaputo.REG-BAS/Desktop/sample.pdf";
            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.addAuthor("Regione Basilivata");
            doc.addCreator("http://govpay-test.regione.basilicata.it/pagamentionline");
            doc.addTitle("AVVISO DI PAGAMENTO");

            doc.open();


            String absoluteContext = "C:/Progetti/pubpay/pagamenti-on-line/trunk/src/main/webapp/";

            Image imageRB = Image.getInstance(absoluteContext + "/resources/img/logo_comune3.jpg");
            //imageRB.setAlignment(Image.LEFT);
            imageRB.setIndentationLeft(42.48f);

            // imageRB.setAbsolutePosition(42.48f, (PageSize.A4.getHeight() - 42.48f - imageRB.getHeight()));
            doc.add(imageRB);

            Image imagePP = Image.getInstance(absoluteContext + "/resources/img/logo_pagopa4.jpg");

            imagePP.setAbsolutePosition(PageSize.A4.getWidth() - 42.48f - imagePP.getWidth(), (PageSize.A4.getHeight() - 42.48f - imagePP.getHeight()));

            doc.add(imagePP);


            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 11.0f, Font.NORMAL);
            Font fontBold = new Font(Font.FontFamily.TIMES_ROMAN, 11.0f, Font.BOLD);
            Font fontItalic = new Font(Font.FontFamily.TIMES_ROMAN, 11.0f, Font.ITALIC);
            Font fontBoldItalic = new Font(Font.FontFamily.TIMES_ROMAN, 11.0f, Font.BOLDITALIC);


            doc.add(Chunk.NEWLINE);
            Paragraph paragraphTitle = new Paragraph("AVVISO DI PAGAMENTO", FontFactory.getFont("Calibri", 14, Font.BOLD));
            paragraphTitle.setAlignment(Paragraph.ALIGN_CENTER);
            doc.add(paragraphTitle);
            doc.add(Chunk.NEWLINE);

            Paragraph paragraphCF = new Paragraph("Codice Fiscale Ente Creditore: ", FontFactory.getFont("Calibri", 11, Font.NORMAL));
            paragraphCF.setIndentationLeft(42.48f);
            doc.add(paragraphCF);

            Paragraph paragraphCap = new Paragraph("Codice avviso pagamento: ", FontFactory.getFont("Calibri", 11, Font.NORMAL));
            paragraphCap.setIndentationLeft(42.48f);
            doc.add(paragraphCap);

            Paragraph paragraphIUV = new Paragraph("Codice IUV: ", FontFactory.getFont("Calibri", 11, Font.NORMAL));
            paragraphIUV.setIndentationLeft(42.48f);
            doc.add(paragraphIUV);

            Paragraph paragraphImporto = new Paragraph("Importo versamento in Euro: ", FontFactory.getFont("Calibri", 11, Font.NORMAL));
            paragraphImporto.setIndentationLeft(42.48f);
            doc.add(paragraphImporto);

            Paragraph paragraphData = new Paragraph("Data scadenza: ", FontFactory.getFont("Calibri", 11, Font.NORMAL));
            paragraphData.setIndentationLeft(42.48f);
            doc.add(paragraphData);
            doc.add(Chunk.NEWLINE);



            Chunk p01 = new Chunk("\"Attraverso il sistema",fontItalic);
            Chunk p02 = new Chunk("\"pagoPA® ", fontBoldItalic);
            Chunk p03 = new Chunk(" è possibile effettuare il pagamento con le seguenti modalità:",fontItalic);

            Chunk p21 = new Chunk(" è un  ", fontItalic);
            Chunk p22 = new Chunk("sistema pubblico ",fontBoldItalic);
            Chunk p23 = new Chunk("- fatto di regole, standard e strumenti definiti ",fontItalic);
            Chunk p24 = new Chunk("dall'Agenzia per l'Italia Digitale ", fontBoldItalic);
            Chunk p25 = new Chunk("e accettati dalla Pubblica Amministrazione e dai PSP aderenti all'iniziativa - che garantisce a  ", fontItalic);
            Chunk p26 = new Chunk("a privati e aziende di effettuare pagamenti elettronici alla PA ", fontBoldItalic);
            Chunk p27 = new Chunk(" in modo sicuro e affidabile, semplice e in totale trasparenza nei costi di commissione. Si tratta di un'iniziativa promossa dalla ", fontItalic);
            Chunk p28 = new Chunk(" Presidenza del Consiglio dei Ministri ", fontBoldItalic);
            Chunk p29 = new Chunk("  alla quale tutte le PA sono obbligate ad aderire.\"", fontItalic);


            Paragraph paragraph0 = new Paragraph();
            paragraph0.add(p01);
            paragraph0.add(p02);
            paragraph0.add(p03);
            paragraph0.setIndentationLeft(42.48f);
            doc.add(paragraph0);

            Paragraph paragraph1 = new Paragraph("•  \tsul sito web di Regione Basilicata (www.regione.basilicata.it), accedendo all'apposita sezione e scegliendo tra gli strumenti disponibili: carta di credito o debito o prepagata, oppure il bonifico bancario o il bollettino postale nel caso si disponga di un conto corrente presso banche, Poste e altri prestatori di servizio di pagamento aderenti all’iniziativa. Per poter effettuare il pagamento occorre indicare il codice IUV presente sull'avviso \n" +
                    "\n" + "•  \tpresso le banche, Poste e altri prestatori di servizio di pagamento aderenti all'iniziativa tramite i canali da questi messi a disposizione (come ad esempio: home banking, ATM, APP da smartphone, sportello, ecc). L’elenco dei punti abilitati a ricevere pagamenti tramite pagoPA® è disponibile alla pagina https://wisp.pagopa.gov.it/elencopsp.\"\n" +
                    "Per poter effettuare il pagamento occorre utilizzare il Codice Avviso di Pagamento oppure il QR Code o i Codici a Barre, presenti sulla stampa dell'avviso.\"\n", fontItalic);
            paragraph1.setIndentationLeft(84.96f);
            paragraph1.setIndentationRight(42.48f);
            paragraph1.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            doc.add(paragraph1);
            doc.add(Chunk.NEWLINE);



            //Paragraph paragraph2 = new Paragraph("\"pagoPA® è un sistema pubblico - fatto di regole, standard e strumenti definiti dall'Agenzia per l'Italia Digitale e accettati dalla Pubblica Amministrazione e dai PSP aderenti all'iniziativa - che garantisce a privati e aziende di effettuare pagamenti elettronici alla PA in modo sicuro e affidabile, semplice e in totale trasparenza nei costi di commissione. Si tratta di un'iniziativa promossa dalla Presidenza del Consiglio dei Ministri alla quale tutte le PA sono obbligate ad aderire.\"", fontItalic);
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
            paragraph2.setIndentationLeft(42.48f);
            paragraph2.setIndentationRight(42.48f);
            paragraph2.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            doc.add(paragraph2);
            doc.add(Chunk.NEWLINE);



            Paragraph paragraph3 = new Paragraph("\"L'importo del presente documento potrebbe subire variazioni rispetto a quanto sopra riportato in quanto aggiornato automaticamente dal sistema (in funzione di eventuali sgravi, note di credito, indennità di mora, sanzioni o interessi, ecc.). Il prestatore di servizi di pagamento presso il quale è presentato potrebbe pertanto richiedere un importo diverso da quello indicato sul documento stesso\".", fontItalic);
            paragraph3.setIndentationLeft(42.48f);
            paragraph3.setIndentationRight(42.48f);
            paragraph3.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            doc.add(paragraph3);
            doc.add(Chunk.NEWLINE);


            String myString = "4158000295076680200100000000000206039025035";
            Barcode128 code128 = new Barcode128();
            code128.setCode(myString.trim());
            code128.setBarHeight(39.38f);
            code128.setCodeType(Barcode128.CODE128);
            PdfContentByte cb = docWriter.getDirectContent();
            Image code128Image = code128.createImageWithBarcode(cb, null, null);
            code128Image.setAbsolutePosition(42.48f, 42.48f);
            //code128Image.scaleAbsolute(223.92f, 39.38f);
            doc.add(code128Image);


            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>(1);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            QRCode qrCode = Encoder.encode("PAGOPA|002|01000000000002161|80002950766|110000", ErrorCorrectionLevel.L, hints);

// Step 2 - create a BufferedImage out of this array
            int width = qrCode.getMatrix().getWidth();
            int height = qrCode.getMatrix().getHeight();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            int[] rgbArray = new int[width * height];
            int i = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                   // rgbArray[i] = qrCode.getMatrix().get(x, y) > 0 ? 0xFFFFFF : 0x000000;

                    int grey =qrCode.getMatrix().get(x, y) > 0 ? 0x00 : 0xff;
                    rgbArray[y*width+x] = 0xff000000 | (0x00010101*grey);
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
            qrcodeImage.setAbsolutePosition(PageSize.A4.getWidth() - 42.48f - qrcodeImage.getScaledWidth(), 42.48f);
            doc.add(qrcodeImage);

            //148 troppo grande (corrisponde a 4.16 e 147 troppo piccolo 3.10)
            //BarcodeQRCode qrcode = new BarcodeQRCode("PAGOPA|002|01000000000002161|80002950766|110000", 1, 1, null);
            //Image qrcodeImage2 = qrcode.getImage();
            //qrcodeImage.setAbsolutePosition(PageSize.A4.getWidth() - 42.48f - qrcodeImage.getWidth(), 42.48f);
            //doc.add(qrcodeImage2);

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
    public void gpAvviaTransazionePagamento() throws IOException {


        GpAvviaTransazionePagamento req = new GpAvviaTransazionePagamento();
       /* req.setAutenticazione(auth);

        req.setCallbackUrl(backurl);

        Canale canale = new Canale();
        canale.setCodPsp(codPsp);
        canale.setTipoVersamento(tipoVersamento);
        req.setCanale(canale);*/

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
        //String uuid = UUID.randomUUID().toString();
        //versamento.setCodVersamentoEnte(uuid);//"1460980677037");
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

        GpAvviaTransazionePagamentoResponse response = port.gpAvviaTransazionePagamento(req);

        System.out.println(response.getCodEsitoOperazione());
        System.out.println(response.getPspSessionId());
        System.out.println(response.getCodOperazione());
        System.out.println(response.getDescrizioneEsitoOperazione());
        System.out.println(response.getRifTransazione().size());
        if (!response.getRifTransazione().isEmpty()) {
            System.out.println(response.getRifTransazione().get(0).getIuv());
            System.out.println(response.getRifTransazione().get(0).getCcp());
        }
    }

    @Test
    public void gpCaricaVersamento() {


        GpCaricaVersamento gpCaricaVersamento = new GpCaricaVersamento();
        gpCaricaVersamento.setAggiornaSeEsiste(true);
        /*IdPagamento idPagamento = esitoOperazione.getIdPagamento().get(0);
        String iuv = idPagamento.getCodApplicazione();*/

        Versamento p = new Versamento();
        p.setCodApplicazione(codPortale);
        p.setCodVersamentoEnte("1460980677037");
        p.setCodDominio(codDominio);
        //p.setCodUnitaOperativa(codUnitaOperativa);
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
        GpCaricaVersamentoResponse response = portApp.gpCaricaVersamento(gpCaricaVersamento);
        System.out.println(response.getDescrizioneEsitoOperazione());
    }

}
