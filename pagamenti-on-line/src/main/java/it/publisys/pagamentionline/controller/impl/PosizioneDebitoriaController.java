package it.publisys.pagamentionline.controller.impl;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import it.govpay.servizi.PagamentiTelematiciGPApp;
import it.govpay.servizi.PagamentiTelematiciGPPrt;
import it.govpay.servizi.commons.StatoVersamento;
import it.govpay.servizi.gpapp.GpChiediStatoVersamentoResponse;
import it.govpay.servizi.gpprt.GpChiediListaVersamentiResponse;
import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.PagamentiOnlineKey;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.dto.DebitoDTO;
import it.publisys.pagamentionline.service.EnteService;
import it.publisys.pagamentionline.service.GovPayService;
import it.publisys.pagamentionline.service.PagamentoService;
import it.publisys.pagamentionline.transformer.VersamentoTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Francesco A. Tabino
 */
@Controller
@SessionAttributes(value = "debiti", types = List.class)
public class PosizioneDebitoriaController extends BaseController {

    @Autowired
    private EnteService enteService;

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private GovPayService govPayService;

    @Autowired
    private VersamentoTransformer versamentoTransformer;

    @ModelAttribute(ModelMappings.ENTI)
    public List<Ente> listaEnti() {
        return enteService.findAll();
    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_DEBITORIO, method = RequestMethod.GET)
    public String pagamentoDebitorio(ModelMap model, Authentication auth) {

        if (auth != null && auth.isAuthenticated()) {
            User user = (User) auth.getPrincipal();
            List<DebitoDTO> debiti = new ArrayList<>();
            //TODO: mettere nel db questo pagamento 66l
            Pagamento pagamento = pagamentoService.getPagamento(66L);
            PagamentiTelematiciGPPrt port = govPayService.getPagamentiTelematiciGPPrt(pagamento);
            GpChiediListaVersamentiResponse gpCercaVersamentiResponse = port.gpChiediListaVersamenti(govPayService.gpCercaVersamentiRequest(user, pagamento.getEnte()));
            gpCercaVersamentiResponse.getVersamento().stream()
                    .filter(v -> !v.getStato().value().equals(StatoVersamento.ESEGUITO.value())).forEach(v ->
                    debiti.add(versamentoTransformer.transofrmToPagamento(v)));
            model.addAttribute("debiti", debiti);
        }
        return ViewMappings.PAGAMENTO_DEBITORIO;
    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_DEBITORIO_SEARCH , method = RequestMethod.POST)
    public String pagamentoDebitorioSearch(@RequestParam("operazione") String operazione,ModelMap model, Authentication auth) {

        if (auth != null && auth.isAuthenticated()) {
            List<DebitoDTO> debiti = (List<DebitoDTO>) model.get("debiti");

            System.out.print("operazione" + operazione);
            User user = (User) auth.getPrincipal();
            Pagamento pagamento = pagamentoService.getPagamento(66L);
            PagamentiTelematiciGPApp app = govPayService.getPagamentiTelematiciGPApp(pagamento.getEnte());

            debiti = debiti.stream().filter(d ->  operazione.equals(d.getCodVersamentoEnte())).collect(Collectors.toList());
            model.addAttribute("debiti", debiti);
        }
            return ViewMappings.PAGAMENTO_DEBITORIO;
    }


    @RequestMapping(value = RequestMappings.PAGAMENTO_DEBITORIO_PRINT + "/{codApplicazione}/{codVersamentoEnte}",
            method = RequestMethod.GET)
    public String printRicevuta(@PathVariable String codApplicazione, @PathVariable String codVersamentoEnte,
                                ModelMap model, Authentication auth, HttpServletResponse response) throws IOException {
        if (auth != null && auth.isAuthenticated()) {
            List<DebitoDTO> debiti = (List<DebitoDTO>) model.get("debiti");
            User user = (User) auth.getPrincipal();
            Pagamento pagamento = pagamentoService.getPagamento(66L);
            PagamentiTelematiciGPApp app = govPayService.getPagamentiTelematiciGPApp(pagamento.getEnte());

            GpChiediStatoVersamentoResponse risposta = app.gpChiediStatoVersamento(govPayService.gpChiediStatoVersamento(codApplicazione, codVersamentoEnte));
            DebitoDTO debito = debiti.stream().filter(d -> codApplicazione.equals(d.getCodApplicazione()) && codVersamentoEnte.equals(d.getCodVersamentoEnte())).findFirst().get();

            byte[] out  =createPDF(user, risposta, pagamento, debito);
            if(null != out){
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=\""+ debito.getCodVersamentoEnte() + ".pdf\"");
                response.getOutputStream().write(out);
            }
        }
        return ViewMappings.PAGAMENTO_DEBITORIO;
    }


    private byte[] createPDF(User user, GpChiediStatoVersamentoResponse risposta, Pagamento pagamento, DebitoDTO debito) {

        Document doc = new Document(PageSize.A4, 0.0f, 0.0f, 42.48f, 42.48f);
        PdfWriter docWriter = null;
        Properties prop = pagamentoService.loadProperties(pagamento);
        //per i pagamenti caricati mi serve iuv come prenderlo??
        String iuv = risposta.getTransazione().get(0).getIuv();
        ByteArrayOutputStream out = null;
        try {
             out = new ByteArrayOutputStream();
            docWriter = PdfWriter.getInstance(doc, out);
            doc.addAuthor("Regione Basilivata");
            doc.addCreator("http://govpay-test.regione.basilicata.it/pagamentionline");
            doc.addTitle("AVVISO DI PAGAMENTO");

            doc.open();


            String absoluteContext = "C:/Progetti/pubpay/pagamenti-on-line/trunk/src/main/webapp/";

            Image imageRB = Image.getInstance(absoluteContext + "/resources/img/logo_comune3.jpg");
            imageRB.setIndentationLeft(42.48f);
            doc.add(imageRB);

            Image imagePP = Image.getInstance(absoluteContext + "/resources/img/logo_pagopa4.jpg");
            imagePP.setAbsolutePosition(PageSize.A4.getWidth() - 42.48f - imagePP.getWidth(), (PageSize.A4.getHeight() - 42.48f - imagePP.getHeight()));
            doc.add(imagePP);


            Font fontItalic = new Font(Font.FontFamily.TIMES_ROMAN, 11.0f, Font.ITALIC);
            Font fontBoldItalic = new Font(Font.FontFamily.TIMES_ROMAN, 11.0f, Font.BOLDITALIC);


            doc.add(Chunk.NEWLINE);
            Paragraph paragraphTitle = new Paragraph("AVVISO DI PAGAMENTO", FontFactory.getFont("Calibri", 14, Font.BOLD));
            paragraphTitle.setAlignment(Paragraph.ALIGN_CENTER);
            doc.add(paragraphTitle);
            doc.add(Chunk.NEWLINE);
            Paragraph paragraphCF = new Paragraph("Codice Fiscale Ente Creditore: " + user.getFiscalcode(), FontFactory.getFont("Calibri", 11, Font.NORMAL));
            paragraphCF.setIndentationLeft(42.48f);
            doc.add(paragraphCF);

            Paragraph paragraphCap = new Paragraph("Codice avviso pagamento: " + "0" + 1 + iuv, FontFactory.getFont("Calibri", 11, Font.NORMAL));
            paragraphCap.setIndentationLeft(42.48f);
            doc.add(paragraphCap);

            Paragraph paragraphIUV = new Paragraph("Codice IUV: " + iuv, FontFactory.getFont("Calibri", 11, Font.NORMAL));
            paragraphIUV.setIndentationLeft(42.48f);
            doc.add(paragraphIUV);

            Paragraph paragraphImporto = new Paragraph("Importo versamento in Euro: " + debito.getImportoDovuto(), FontFactory.getFont("Calibri", 11, Font.NORMAL));
            paragraphImporto.setIndentationLeft(42.48f);
            doc.add(paragraphImporto);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Paragraph paragraphData = new Paragraph("Data scadenza: " + sdf.format(debito.getDataScadenza()), FontFactory.getFont("Calibri", 11, Font.NORMAL));
            paragraphData.setIndentationLeft(42.48f);
            doc.add(paragraphData);
            doc.add(Chunk.NEWLINE);


            Chunk p01 = new Chunk("\"Attraverso il sistema", fontItalic);
            Chunk p02 = new Chunk("\"pagoPA® ", fontBoldItalic);
            Chunk p03 = new Chunk(" è possibile effettuare il pagamento con le seguenti modalità:", fontItalic);

            Chunk p21 = new Chunk(" è un  ", fontItalic);
            Chunk p22 = new Chunk("sistema pubblico ", fontBoldItalic);
            Chunk p23 = new Chunk("- fatto di regole, standard e strumenti definiti ", fontItalic);
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

            String myString = buildBarCode(prop.getProperty(PagamentiOnlineKey.COD_DOMINIO), 1, iuv, debito.getImportoDovuto()).toString();
            Barcode128 code128 = new Barcode128();
            code128.setCode(myString.trim());
            code128.setBarHeight(39.38f);
            code128.setCodeType(Barcode128.CODE128);
            PdfContentByte cb = docWriter.getDirectContent();
            Image code128Image = code128.createImageWithBarcode(cb, null, null);
            code128Image.setAbsolutePosition(42.48f, 42.48f);
            doc.add(code128Image);

            //Non uso itext per via del bordo
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>(1);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            String codice = buildQrCode002(prop.getProperty(PagamentiOnlineKey.COD_DOMINIO), 1, iuv, debito.getImportoDovuto()).toString();
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
            qrcodeImage.setAbsolutePosition(PageSize.A4.getWidth() - 42.48f - qrcodeImage.getScaledWidth(), 42.48f);
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
        return out.toByteArray();
    }


    private static byte[] buildQrCode002(String codDominio, int applicationCode, String iuv, BigDecimal importoTotale) throws JAXBException, SAXException {
        // Da "L’Avviso di pagamento analogico nel sistema pagoPA" par. 2.1
        String qrCode = "PAGOPA|002|0" + applicationCode + iuv + "|" + codDominio + "|" + (nFormatter.format(importoTotale).replace(".", ""));
        return qrCode.getBytes();
    }

    private static final DecimalFormat nFormatter = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));

    private static String buildBarCode(String gln, int applicationCode, String iuv, BigDecimal importoTotale) {
        // Da Guida Tecnica di Adesione PA 3.8 pag 25
        String payToLoc = "415";
        String refNo = "8020";
        String amount = "3902";
        String importo = nFormatter.format(importoTotale).replace(".", "");
        return payToLoc + gln + refNo + "0" + applicationCode + iuv + amount + importo;
    }


}
