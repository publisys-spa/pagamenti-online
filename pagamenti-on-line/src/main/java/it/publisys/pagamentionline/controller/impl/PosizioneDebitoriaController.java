package it.publisys.pagamentionline.controller.impl;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import it.govpay.servizi.PagamentiTelematiciGPApp;
import it.govpay.servizi.PagamentiTelematiciGPPrt;
import it.govpay.servizi.commons.GpResponse;
import it.govpay.servizi.commons.StatoVersamento;
import it.govpay.servizi.commons.Versamento;
import it.govpay.servizi.v2_3.gpprt.GpChiediListaVersamentiResponse;
import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.PagamentiOnlineKey;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.*;
import it.publisys.pagamentionline.domain.search.Filter;
import it.publisys.pagamentionline.domain.search.SearchResults;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.dto.DebitoDTO;
import it.publisys.pagamentionline.service.*;
import it.publisys.pagamentionline.transformer.VersamentoTransformer;
import it.publisys.pagamentionline.util.sec.AuthorityUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
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
@SessionAttributes(types = {Pagamento.class, List.class}, value = {ModelMappings.PAGAMENTO, ModelMappings.DEBITI})
public class PosizioneDebitoriaController extends BaseController {

    @Autowired
    private EnteService enteService;
    @Autowired
    private UserService userService;

    @Autowired
    private GovPayService govPayService;

    @Autowired
    private VersamentoTransformer versamentoTransformer;

    @Autowired
    private TipologiaTributoService tipologiaTributoService;

    @Autowired
    private TributoService tributoService;
    @Autowired
    private RataService rataService;

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    ServletContext servletContext;

    @ModelAttribute(ModelMappings.ENTI)
    public List<Ente> listaEnti() {
        return enteService.findAll();
    }

    @ModelAttribute(ModelMappings.TIPOLOGIE)
    public List<TipologiaTributo> listaTipologie() {
        return tipologiaTributoService.getAllTipologie();
    }


    @RequestMapping(value = RequestMappings.PAGAMENTO_DEBITORIO_SEARCH, method = RequestMethod.POST)
    public String pagamentoDebitorioSearch(@ModelAttribute(value = ModelMappings.FILTER) Filter filter, ModelMap model, Authentication auth) {

        if (auth != null && auth.isAuthenticated() && !AuthorityUtil.isAdminLogged()) {
            List<DebitoDTO> debiti = (List<DebitoDTO>) model.get(ModelMappings.DEBITI);
            debiti = debiti.stream().filter(d -> filter.getIuv().trim().equals(d.getIuv())).collect(Collectors.toList());
            model.addAttribute(ModelMappings.DEBITI, debiti);
        }
        if (AuthorityUtil.isAdminLogged()) {
            List<DebitoDTO> debiti = new ArrayList<>();
            User user = userService.getUserByFiscalcode(filter.getCodiceFiscale());
            if (null == user) {
                String message = "Nessun utente trovato con il codice fiscale: " + filter.getCodiceFiscale();
                model.addAttribute(ModelMappings.MESSAGE, message);
                model.addAttribute(ModelMappings.DEBITI, debiti);
                return ViewMappings.SEARCH_DEBITORIO;
            }
            it.govpay.servizi.v2_3.PagamentiTelematiciGPPrt port = govPayService.getPagamentiTelematiciGPPrt();
            GpChiediListaVersamentiResponse gpCercaVersamentiResponse = port.gpChiediListaVersamenti(govPayService.gpCercaVersamentiRequest(user, StatoVersamento.NON_ESEGUITO, StatoVersamento.ANNULLATO));
            gpCercaVersamentiResponse.getVersamento().stream().forEach(v -> debiti.add(versamentoTransformer.transofrmToPagamento(v, filter.getCodiceFiscale())));
            model.addAttribute(ModelMappings.DEBITI, debiti);
            return ViewMappings.SEARCH_DEBITORIO;
        }
        return ViewMappings.PAGAMENTO_DEBITORIO;
    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_DEBITORIO, method = RequestMethod.GET)
    public String pagamentoDebitorio(ModelMap model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            User user = (User) auth.getPrincipal();
            Filter _filter = new Filter();
            model.addAttribute(ModelMappings.FILTER, _filter);
            List<DebitoDTO> debiti = new ArrayList<>();
            it.govpay.servizi.v2_3.PagamentiTelematiciGPPrt port = govPayService.getPagamentiTelematiciGPPrt();
            GpChiediListaVersamentiResponse gpCercaVersamentiResponse = port.gpChiediListaVersamenti(govPayService.gpCercaVersamentiRequest(user, StatoVersamento.NON_ESEGUITO, StatoVersamento.ANNULLATO));
            gpCercaVersamentiResponse.getVersamento().stream().forEach(v -> debiti.add(versamentoTransformer.transofrmToPagamento(v, user.getFiscalcode())));
            model.addAttribute(ModelMappings.DEBITI, debiti);
        }
        return ViewMappings.PAGAMENTO_DEBITORIO;
    }


    @RequestMapping(value = RequestMappings.PAGAMENTO_DEBITORIO_DETAIL + "/{codApplicazione}/{iuv}/{codVersamentoEnte}", method = RequestMethod.GET)
    public String toView(@PathVariable String codApplicazione, @PathVariable String iuv, @PathVariable String codVersamentoEnte, Authentication auth, ModelMap model) {
        if (auth != null && auth.isAuthenticated()) {
            model.addAttribute(ModelMappings.COD_APPLICAZIONE, codApplicazione);
            List<DebitoDTO> debiti = (List<DebitoDTO>) model.get("debiti");
            DebitoDTO debito;
            if (!iuv.equals("null")) {
                debito = debiti.stream().filter(d -> codApplicazione.equals(d.getCodApplicazione()) && iuv.trim().equals(d.getIuv())).findFirst().get();
            } else {
                debito = debiti.stream().filter(d -> codApplicazione.equals(d.getCodApplicazione()) && codVersamentoEnte.equals(d.getCodVersamentoEnte())).findFirst().get();
            }
            User user = (User) auth.getPrincipal();
            Optional<Pagamento> p = pagamentoService.findByCodVersamentoEnte(debito.getCodVersamentoEnte());
            if (p.isPresent()) {

                model.addAttribute(ModelMappings.PAGAMENTO, p.get());
                return ViewMappings.PAGAMENTO_DEBITORIO_DETAIL;
            }
            Pagamento pagamento = new Pagamento();
            pagamento.setEsecutore(user);
            pagamento.setPid(debito.getCodVersamentoEnte());
            Ente ente = enteService.findByCodDominio(debito.getCodDominio());
            String[] split = codVersamentoEnte.split("_");
            if (split.length > 1) {
                // FACCIO IL PARSE DEL CODICE VERSAMENTO ENTE
                String tipologiaTributo = split[1];
                TipologiaTributo tipT = tipologiaTributoService.getTipologiaByCodiceRadice(tipologiaTributo.trim());
                String sottotipologia = split[2];
                Tributo tributo = tributoService.getByEnteTipologiaTributoCodIntegrazione(ente, tipT, sottotipologia);
                String anno = split[3];
                String canone_accertamento = split[5];
                pagamento.setTributo(tributo);
                if (split.length > 6) {
                    String rata = split[6];
                    if (rata.isEmpty()) {
                        //deve cercare la rata posso usare il CUSTOM ID SPLITTATO da
                        pagamento.setRata(rataService.getAllRata(tributo).get(0));
                    }
                } else {
                    pagamento.setRata(rataService.getAllRata(tributo).get(0));
                }
            } else {
                pagamento.setTributo(new Tributo());
                pagamento.setRata(new Rata());

            }

            pagamento.setCausale(debito.getCausale());
            pagamento.setImporto(Double.parseDouble(debito.getImportoDovuto().toString()));
            pagamento.setStatoPagamento(debito.getStato());
            if (null != debito.getIuv()) {
                pagamento.setIuv(debito.getIuv());
            }
            pagamento.setEnte(ente);
            pagamento.setCodVersamentoEnte(debito.getCodVersamentoEnte());
            model.addAttribute(ModelMappings.PAGAMENTO, pagamento);

        }
        return ViewMappings.PAGAMENTO_DEBITORIO_DETAIL;
    }


    @RequestMapping(value = RequestMappings.PAGAMENTO_DEBITORIO_PRINT + "/{codApplicazione}/{iuv}",
            method = RequestMethod.GET)
    public String printRicevuta(@PathVariable String codApplicazione, @PathVariable String iuv,
                                ModelMap model, Authentication auth, HttpServletResponse response, HttpServletRequest request) throws IOException {
        if (auth != null && auth.isAuthenticated()) {
            List<DebitoDTO> debiti = (List<DebitoDTO>) model.get("debiti");
            DebitoDTO debito = debiti.stream().filter(d -> codApplicazione.equals(d.getCodApplicazione()) && iuv.equals(d.getIuv())).findFirst().get();
            byte[] out = createPDF(debito);
            if (null != out) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + debito.getIuv() + ".pdf\"");
                response.getOutputStream().write(out);
                response.getOutputStream().flush();
                response.getOutputStream().close();
            }
        }
        return ViewMappings.PAGAMENTO_DEBITORIO;
    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_DEBITORIO_ANNULLA + "/{codApplicazione}/{codVersamentoEnte}",
            method = RequestMethod.GET)
    public String annullaPagamento(@PathVariable String codApplicazione, @PathVariable String codVersamentoEnte,
                                   Authentication auth, ModelMap model) throws IOException {
        if (auth != null && auth.isAuthenticated()) {
            it.govpay.servizi.v2_3.PagamentiTelematiciGPApp portApp = govPayService.getPagamentiTelematiciGPApp();
            it.govpay.servizi.v2_3.commons.GpResponse response = portApp.gpAnnullaVersamento(govPayService.gpAnnullaVersamento(codApplicazione, codVersamentoEnte));
            if (response.getCodOperazione().equals(PagamentiOnlineKey.ESITO_OK)) {
                String message = "Pagamento con Numero Operazione " + codVersamentoEnte + " anullato correttamente";
                model.addAttribute(ModelMappings.MESSAGE, message);
            } else {
                String error = "Si è verificato un errore durante il pagamento con Numero Operazione " + codVersamentoEnte + ": " + response.getCodOperazione();
                model.addAttribute(ModelMappings.ERROR, error);
            }
            List<DebitoDTO> debiti = new ArrayList<>();
            model.addAttribute(ModelMappings.DEBITI, debiti);
            Filter _filter = new Filter();
            model.addAttribute(ModelMappings.FILTER, _filter);
        }
        return ViewMappings.SEARCH_DEBITORIO;
    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_DEBITORIO_ELIMINA + "/{codVersamentoEnte}",
            method = RequestMethod.GET)
    public String elimina( @PathVariable String codVersamentoEnte,
                                   Authentication auth) throws IOException {
        //non posso utilizzarlo perchè non posso segnarlo come eliminato dato che risulta dalla lista di govpay
        if (auth != null && auth.isAuthenticated()) {
            User _user = (User) auth.getPrincipal();
            Optional<Pagamento> pagamento = pagamentoService.findByCodVersamentoEnte(codVersamentoEnte);
            Pagamento p = null;

            if (pagamento.isPresent()) {
                p = pagamentoService.getPagamento(pagamento.get().getId());
                p.setStatoPagamento("ELIMINATO");
            }
            pagamentoService.save(p, _user.getUsername());
        }
        return ViewMappings.PAGAMENTO_DEBITORIO;
    }


    private byte[] createPDF(DebitoDTO debito) {
        Document doc = new Document(PageSize.A4, 0.0f, 0.0f, 42.50f, 42.50f);
        PdfWriter docWriter = null;
        ByteArrayOutputStream out = null;
        String numeroAvviso = debito.getQrCode().split("\\|")[2];
        try {
            out = new ByteArrayOutputStream();
            docWriter = PdfWriter.getInstance(doc, out);
            doc.addAuthor("Regione Basilicata");
            doc.addCreator("https://pagopa.regione.basilicata.it/pagamentionline");
            doc.addTitle("AVVISO DI PAGAMENTO");

            doc.open();

            String absoluteContext = "https://pagopa.regione.basilicata.it/pagamentionline";
            Image imageRB = Image.getInstance(absoluteContext + "/resources/img/logo_" + debito.getCodDominio() + ".jpg");
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

            Paragraph paragraphCFD = new Paragraph("Codice Fiscale Debitore: " + debito.getCodFiscale(), FontFactory.getFont("Calibri", 10, Font.NORMAL));
            paragraphCFD.setIndentationLeft(42.50f);
            doc.add(paragraphCFD);

            Paragraph paragraphCF = new Paragraph("Codice Fiscale Ente Creditore: " + debito.getCodDominio(), FontFactory.getFont("Calibri", 10, Font.NORMAL));
            paragraphCF.setIndentationLeft(42.50f);
            doc.add(paragraphCF);

            Paragraph paragraphCap = new Paragraph("Codice avviso pagamento: " + numeroAvviso, FontFactory.getFont("Calibri", 10, Font.NORMAL));
            paragraphCap.setIndentationLeft(42.50f);
            doc.add(paragraphCap);

            Paragraph paragraphIUV = new Paragraph("Codice IUV: " + debito.getIuv(), FontFactory.getFont("Calibri", 10, Font.NORMAL));
            paragraphIUV.setIndentationLeft(42.50f);
            doc.add(paragraphIUV);

            Paragraph paragraphImporto = new Paragraph("Importo versamento in Euro: " + debito.getImportoDovuto(), FontFactory.getFont("Calibri", 10, Font.NORMAL));
            paragraphImporto.setIndentationLeft(42.50f);
            doc.add(paragraphImporto);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Paragraph paragraphData = new Paragraph("Data scadenza: " + sdf.format(debito.getDataScadenza()), FontFactory.getFont("Calibri", 10, Font.NORMAL));
            paragraphData.setIndentationLeft(42.50f);
            doc.add(paragraphData);

            Paragraph paragraphCausale = new Paragraph("Causale: " + debito.getCausale(), FontFactory.getFont("Calibri", 10, Font.NORMAL));
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

            String myString = debito.getBarCode();
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

            String codice = debito.getQrCode();
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
        return out.toByteArray();
    }


    private static final DecimalFormat nFormatter = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));

    @RequestMapping(value = RequestMappings.PAGAMENTO_DEBITORIO_SEARCH_PRINT, method = RequestMethod.GET)
    public String printRicevuta(ModelMap model, HttpServletResponse response) throws IOException {
        if (AuthorityUtil.isAdminLogged()) {
            List<DebitoDTO> debiti = (List<DebitoDTO>) model.get("debiti");
            HSSFWorkbook workbook = new HSSFWorkbook();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            if (null != debiti && !debiti.isEmpty()) {

                HSSFSheet sheet = workbook.createSheet("Estrazione lista pagamenti in debito");
                sheet.setDefaultColumnWidth(30);

                CellStyle style = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                font.setFontName("Times New Roman");
                style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
                style.setFillPattern(CellStyle.SOLID_FOREGROUND);
                font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                font.setColor(HSSFColor.WHITE.index);
                style.setFont(font);

                HSSFRow header = sheet.createRow(0);

                header.createCell(0).setCellValue("Scadenza");
                header.getCell(0).setCellStyle(style);

                header.createCell(1).setCellValue("Causale - (Ente - Tributo)");
                header.getCell(1).setCellStyle(style);

                header.createCell(2).setCellValue("N. Operazione");
                header.getCell(2).setCellStyle(style);

                header.createCell(3).setCellValue("IUV");
                header.getCell(3).setCellStyle(style);

                header.createCell(4).setCellValue("Importo Dovuto");
                header.getCell(4).setCellStyle(style);

                header.createCell(5).setCellValue("Stato Pagamento");
                header.getCell(5).setCellStyle(style);

                int rowCount = 1;
                for (DebitoDTO debito : debiti) {

                    HSSFRow aRow = sheet.createRow(rowCount++);
                    if (null != debito.getDataScadenza()) {
                        aRow.createCell(0).setCellValue(sdf.format(debito.getDataScadenza()));
                    } else {
                        aRow.createCell(0).setCellValue("");
                    }
                    aRow.createCell(1).setCellValue(debito.getCausale());
                    aRow.createCell(2).setCellValue(debito.getCodVersamentoEnte());
                    if (null != debito.getIuv()) {
                        aRow.createCell(3).setCellValue(debito.getIuv());
                    } else {
                        aRow.createCell(3).setCellValue("");
                    }
                    aRow.createCell(4).setCellValue(debito.getImportoDovuto().doubleValue());
                    aRow.createCell(5).setCellValue(debito.getStato());
                }

                ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
                workbook.write(outByteStream);
                byte[] outArray = outByteStream.toByteArray();
                response.setContentType("application/ms-excel");
                response.setContentLength(outArray.length);
                response.setHeader("Expires:", "0");
                response.setHeader("Content-Disposition", "attachment; filename=estrazione_" + new Date().getTime() + ".xls");
                response.getOutputStream().write(outArray);
                response.getOutputStream().flush();
                response.getOutputStream().close();
            }

        }
        return ViewMappings.HOME;
    }


}
