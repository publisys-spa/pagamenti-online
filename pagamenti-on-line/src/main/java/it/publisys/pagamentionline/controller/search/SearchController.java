package it.publisys.pagamentionline.controller.search;

import it.govpay.servizi.commons.FlussoRendicontazione;
import it.govpay.servizi.v2_3.PagamentiTelematiciGPRnd;
import it.govpay.servizi.v2_3.gpapp.GpChiediListaFlussiRendicontazioneResponse;
import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.Applicazione;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.domain.search.Filter;
import it.publisys.pagamentionline.domain.search.SearchResults;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.dto.DebitoDTO;
import it.publisys.pagamentionline.service.GovPayService;
import it.publisys.pagamentionline.service.SearchService;
import it.publisys.pagamentionline.util.sec.AuthorityUtil;
import it.publisys.pagamentionline.util.sec.SecurityUtil;
import it.govpay.servizi.v2_3.gprnd.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author mcolucci
 */
@Controller
@SessionAttributes(value = ModelMappings.SEARCH_RESULTS, types = List.class)
public class SearchController
        extends BaseController {

    private static final Logger _log = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    @Autowired
    private GovPayService govPayService;

    @RequestMapping(value = RequestMappings.SEARCH,
            method = RequestMethod.GET)
    public String toView(ModelMap model) {
        initSearch(model);
        return ViewMappings.SEARCH;
    }

    @RequestMapping(value = RequestMappings.SEARCH_DEBITORIO,
            method = RequestMethod.GET)
    public String toViewDebitorio(ModelMap model) {
        List<DebitoDTO> debiti = new ArrayList<>();
        model.addAttribute(ModelMappings.DEBITI, debiti);
        initSearch(model);
        return ViewMappings.SEARCH_DEBITORIO;
    }

    @RequestMapping(value = RequestMappings.SEARCH_USERS,
            method = RequestMethod.GET)
    public String toViewUsers(ModelMap model) {
        initSearch(model);
        Filter _filter = (Filter) model.get(ModelMappings.FILTER);
        _filter.setTipo(ModelMappings.SEARCH_TYPE_USER);
        return ViewMappings.USERS;
    }

    @RequestMapping(value = RequestMappings.SEARCH_APPLICAZIONI,
            method = RequestMethod.GET)
    public String toViewApp(ModelMap model) {
        initSearch(model);
        Filter _filter = (Filter) model.get(ModelMappings.FILTER);
        _filter.setTipo(ModelMappings.SEARCH_TYPE_APP);
        return ViewMappings.APPLICAZIONI;
    }

    @RequestMapping(value = RequestMappings.SEARCH_TRIBUTI,
            method = RequestMethod.GET)
    public String toViewAppTributi(ModelMap model) {
        initSearch(model);
        Filter _filter = (Filter) model.get(ModelMappings.FILTER);
        _filter.setTipo(ModelMappings.SEARCH_TYPE_TRIBUTI);
        return ViewMappings.TRIBUTI;
    }

    @RequestMapping(value = RequestMappings.SEARCH_FLUSSI,
            method = RequestMethod.GET)
    public String toViewAppFlussi(ModelMap model) {
        initSearch(model);
        Filter _filter = (Filter) model.get(ModelMappings.FILTER);
        _filter.setTipo(ModelMappings.SEARCH_TYPE_FLUSSI);
        return ViewMappings.SEARCH_FLUSSI;
    }

    private void initSearch(ModelMap model) {
        model.addAttribute(ModelMappings.SEARCH_RESULTS, new ArrayList<>());
        Filter _filter = new Filter();
        model.addAttribute(ModelMappings.FILTER, _filter);
    }


    @RequestMapping(value = RequestMappings.SEARCH, method = RequestMethod.POST)
    public String toSearch(
            @ModelAttribute(value = ModelMappings.FILTER) Filter filter,
            BindingResult result, ModelMap model) {

        String pageReturn = ViewMappings.SEARCH;
        if (ModelMappings.SEARCH_TYPE_USER.equals(filter.getTipo())) {
            pageReturn = ViewMappings.USERS;
        }
        if (ModelMappings.SEARCH_TYPE_APP.equals(filter.getTipo())) {
            pageReturn = ViewMappings.APPLICAZIONI;
        }
        if (ModelMappings.SEARCH_TYPE_TRIBUTI.equals(filter.getTipo())) {
            pageReturn = ViewMappings.TRIBUTI;
        }
        if (ModelMappings.SEARCH_TYPE_FLUSSI.equals(filter.getTipo())) {
            pageReturn = ViewMappings.SEARCH_FLUSSI;
        }

        if (result.hasErrors()) {
            model.addAttribute(ModelMappings.FILTER, filter);
            return pageReturn;
        }

        if (!AuthorityUtil.isAdminLogged()) {
            User _user = SecurityUtil.getPrincipal();
            filter.setCodiceFiscale(_user.getFiscalcode());
        }

        SearchResults _sresults = null;

        if (ModelMappings.SEARCH_TYPE_TRIB.equals(filter.getTipo())) {
            _sresults = new SearchResults<>(filter.getTipo(), searchService.searchPagamenti(filter));
        }
        if (ModelMappings.SEARCH_TYPE_USER.equals(filter.getTipo())) {
            _sresults = new SearchResults<>(filter.getTipo(), searchService.searchUsers(filter));
        }
        if (ModelMappings.SEARCH_TYPE_APP.equals(filter.getTipo())) {
            _sresults = new SearchResults<>(filter.getTipo(), searchService.searchApplicazioni(filter));
        }
        if (ModelMappings.SEARCH_TYPE_TRIBUTI.equals(filter.getTipo())) {
            _sresults = new SearchResults<>(filter.getTipo(), searchService.searchTributi(filter));
        }
        if (ModelMappings.SEARCH_TYPE_FLUSSI.equals(filter.getTipo())) {
            _sresults = new SearchResults<>(filter.getTipo(), flussiSearch(filter, model));
        }

        model.addAttribute(ModelMappings.SEARCH_RESULTS, _sresults);

        model.addAttribute(ModelMappings.FILTER, filter);

        if (_sresults.isEmpty()) {
            String message = "Nessun risultato presente per i criteri indicati. ";
            model.addAttribute(ModelMappings.MESSAGE, message);
        }
        return pageReturn;
    }

    private List<it.govpay.servizi.v2_3.commons.FlussoRendicontazione> flussiSearch(Filter filter, ModelMap model) {
        List<it.govpay.servizi.v2_3.commons.FlussoRendicontazione> flussi = new ArrayList<it.govpay.servizi.v2_3.commons.FlussoRendicontazione>();
        PagamentiTelematiciGPRnd gpGPRnd = govPayService.getPagamentiTelematiciGPRnd();
        it.govpay.servizi.v2_3.gprnd.GpChiediListaFlussiRendicontazioneResponse gpListaFlussiResponse =
                gpGPRnd.gpChiediListaFlussiRendicontazione(govPayService.gpChiediListaFlussiRendicontazione(filter.getCodice(), filter.getCodDominio()));
        gpListaFlussiResponse.getFlussoRendicontazione().stream().forEach(f -> flussi.add(gpGPRnd.gpChiediFlussoRendicontazione
                (govPayService.gpChiediFlussoRendicontazione(filter.getCodice(), f.getCodFlusso())).getFlussoRendicontazione()));
        return flussi;

    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_SEARCH_PRINT, method = RequestMethod.GET)
    public String printRicevuta(ModelMap model, HttpServletResponse response) throws IOException {
        if (AuthorityUtil.isAdminLogged()) {
            SearchResults _sresults = (SearchResults) model.get(ModelMappings.SEARCH_RESULTS);
            HSSFWorkbook workbook = new HSSFWorkbook();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            if (null != _sresults && !_sresults.isEmpty()) {

                String tipoRicerca = _sresults.getType();

                HSSFSheet sheet = workbook.createSheet("Estrazione");
                sheet.setDefaultColumnWidth(30);


                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setFontName("Times New Roman");
                style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
                style.setFillPattern(CellStyle.SOLID_FOREGROUND);
                font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                font.setColor(HSSFColor.WHITE.index);
                style.setFont(font);

                HSSFRow header = sheet.createRow(0);

                header.createCell(0).setCellValue("ID");
                header.getCell(0).setCellStyle(style);

                header.createCell(1).setCellValue("IUV");
                header.getCell(1).setCellStyle(style);

                header.createCell(2).setCellValue("Importo");
                header.getCell(2).setCellStyle(style);

                header.createCell(3).setCellValue("Beneficiario");
                header.getCell(3).setCellStyle(style);

                header.createCell(4).setCellValue("Data");
                header.getCell(4).setCellStyle(style);

                header.createCell(5).setCellValue("Stato");
                header.getCell(5).setCellStyle(style);

                if (tipoRicerca.equals(ModelMappings.SEARCH_TYPE_TRIB)) {
                    header.createCell(6).setCellValue("Tributo");
                    header.getCell(6).setCellStyle(style);

                    header.createCell(7).setCellValue("Rata");
                    header.getCell(7).setCellStyle(style);
                } else if (tipoRicerca.equals(ModelMappings.SEARCH_TYPE_CONTR)) {
                    header.createCell(6).setCellValue("Targa");
                    header.getCell(6).setCellStyle(style);

                    header.createCell(7).setCellValue("Verbale");
                    header.getCell(7).setCellStyle(style);

                    header.createCell(8).setCellValue("Data Infrazione");
                    header.getCell(8).setCellStyle(style);
                }
                int rowCount = 1;
                for (Object sresult : _sresults.getRecords()) {

                    Pagamento p = (Pagamento) sresult;
                    HSSFRow aRow = sheet.createRow(rowCount++);
                    aRow.createCell(0).setCellValue(p.getPid());
                    if (null != p.getIuv()) {
                        aRow.createCell(1).setCellValue(p.getIuv());
                    } else {
                        aRow.createCell(1).setCellValue("");
                    }
                    aRow.createCell(2).setCellValue(p.getImporto());
                    aRow.createCell(3).setCellValue(p.getEsecutore().getFirstname() + " " + p.getEsecutore().getLastname());
                    if (null != p.getDataPagamento()) {
                        aRow.createCell(4).setCellValue(sdf.format(p.getDataPagamento()));
                    } else {
                        aRow.createCell(4).setCellValue("");
                    }
                    aRow.createCell(5).setCellValue(p.getStatoPagamento());
                    if (tipoRicerca.equals(ModelMappings.SEARCH_TYPE_TRIB)) {
                        aRow.createCell(6).setCellValue(p.getTributo().getNome());
                        aRow.createCell(7).setCellValue(p.getRata().getNome());
                    } else if (tipoRicerca.equals(ModelMappings.SEARCH_TYPE_CONTR)) {
                        //todo: cosa mettere per targa, verbale e data infrazione
                        aRow.createCell(6).setCellValue("Targa");
                        aRow.createCell(7).setCellValue("Verbale");
                        aRow.createCell(8).setCellValue("Data Infrazione");
                    }
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


    @ModelAttribute(ModelMappings.TRIBUTI)
    public List<Tributo> tributi() {
        return searchService.getTributoService().getAllTributi();
    }

    @ModelAttribute(ModelMappings.APPLICAZIONI)
    public List<Applicazione> applicazioni() {
        return searchService.getApplicazioneService().getAllApplicazioni();
    }

    @ModelAttribute(ModelMappings.ENTI)
    public List<Ente> enti() {
        return searchService.getEnteService().findAll();
    }

}
