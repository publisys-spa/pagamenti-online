package it.publisys.pagamentionline.controller.impl;

import com.itextpdf.text.pdf.codec.Base64;
import it.gov.digitpa.schemas._2011.pagamenti.CtRicevutaTelematica;
import it.gov.digitpa.schemas._2011.pagamenti.CtRichiestaPagamentoTelematico;
import it.govpay.core.utils.JaxbUtils;
import it.govpay.servizi.PagamentiTelematiciGPPrt;
import it.govpay.servizi.commons.StatoVersamento;

import it.govpay.servizi.v2_3.gpprt.*;
import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.impl.TipologiaTributo;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.dto.DebitoDTO;
import it.publisys.pagamentionline.service.*;
import it.publisys.pagamentionline.transformer.VersamentoTransformer;
import it.publisys.pagamentionline.util.sec.AuthorityUtil;
import it.publisys.pagamentionline.util.sec.SecurityUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * @author Francesco A. Tabino
 */
@Controller
@SessionAttributes(types = {Pagamento.class}, value = {ModelMappings.PAGAMENTO})
public class PagamentoSpontaneoController extends BaseController {

    private static final Logger _log = LoggerFactory.getLogger(PagamentoSpontaneoController.class);

    @Autowired
    private EnteService enteService;

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private GovPayService govPayService;

    @Autowired
    private TipologiaTributoService tipologiaTributoService;

    @Autowired
    private VersamentoTransformer versamentoTransformer;

    @ModelAttribute(ModelMappings.ENTI)
    public List<Ente> listaEnti() {
        return enteService.findAll();
    }

    @ModelAttribute(ModelMappings.TIPOLOGIE)
    public List<TipologiaTributo> listaTipologie() {
        return tipologiaTributoService.getAllTipologie();
    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_SPONTANEO,
            method = RequestMethod.GET)
    public String toView(ModelMap model, HttpServletRequest request, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            User user = (User) auth.getPrincipal();
            SecurityUtil.listaDebiti(auth, model, govPayService, versamentoTransformer);

        }

        Pagamento pagamento = new Pagamento();

        String pid = request.getParameter("pid");
        if (!StringUtils.isEmpty(pid)) {
            pagamento = pagamentoService.findByPid(pid).get();
            model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
            return ViewMappings.PAGAMENTO_SPONTANEO_DETAIL;
        }
        //TODO da cambiare!!!
        pagamento.setEnte(enteService.findByCodDominio("80002950766"));
        model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
        return ViewMappings.PAGAMENTO_SPONTANEO;
    }


    @RequestMapping(value = RequestMappings.PAGAMENTO_SPONTANEO,
            method = RequestMethod.POST)
    public String create(@ModelAttribute(ModelMappings.PAGAMENTO) @Valid Pagamento pagamento,
                         BindingResult result, ModelMap model, Authentication auth) {
        if (pagamento.getEnte() != null && pagamento.getEnte().getId() == null) {
            result.addError(new ObjectError("Ente", "Ente obbligatorio"));
        }
        if (pagamento.getTributo() != null && pagamento.getTributo().getId() == null) {
            result.addError(new ObjectError("Tributo", "Tributo obbligatorio"));
        }
        if (pagamento.getRata() != null && pagamento.getRata().getId() == null) {
            result.addError(new ObjectError("Rata", "Rata obbligatorio"));
        }

        if (result.hasErrors()) {
            //TODO vedere se migliorare il javascript di selezione enti
            pagamento.setEnte(null);
            pagamento.setTributo(null);
            pagamento.setRata(null);
            //TODO da cambiare!!!
            pagamento.setEnte(enteService.findByCodDominio("80002950766"));
            model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
            model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
            return ViewMappings.PAGAMENTO_SPONTANEO;
        }

        User _user = (User) auth.getPrincipal();
        pagamento.setEsecutore(_user);
        if (null == pagamento.getStatoPagamento()) {
            pagamento.setStatoPagamento("IN ATTESA DI SCELTA DEL WISP");
        }
        pagamento = pagamentoService.save(pagamento, _user.getUsername());
        Properties prop = pagamentoService.loadProperties(pagamento);
        model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
        model.addAttribute(ModelMappings.URL_BACK, prop.getProperty(ModelMappings.URL_BACK));
        model.addAttribute(ModelMappings.URL_RETURN, prop.getProperty(ModelMappings.URL_RETURN));
        model.addAttribute(ModelMappings.PAGE_WISP, prop.getProperty(ModelMappings.PAGE_WISP));
        return ViewMappings.PAGAMENTO_SPONTANEO_WISP;

    }


    @RequestMapping(value = RequestMappings.PAGAMENTO_SPONTANEO_ELIMINA,
            method = RequestMethod.POST)
    public String elimina(@ModelAttribute(ModelMappings.PAGAMENTO) @Valid Pagamento pagamento,
                          BindingResult result, ModelMap model, Authentication auth) {
        User _user = (User) auth.getPrincipal();
        pagamento.setEsecutore(_user);
        if (!pagamento.getStatoPagamento().equals(StatoVersamento.NON_ESEGUITO)) {
            pagamento.setStatoPagamento("ELIMINATO");
        }
        pagamentoService.save(pagamento, _user.getUsername());
        return "redirect:" + RequestMappings.INDEX;

    }


    @RequestMapping(value = RequestMappings.PAGAMENTO_STATO,
            method = RequestMethod.POST)
    public String chiediStato(@ModelAttribute(ModelMappings.PAGAMENTO) @Valid Pagamento pagamento,
                              BindingResult result, ModelMap model, Authentication auth) {

        User _user = (User) auth.getPrincipal();
        GpChiediStatoTransazione statoPagamentoRequest = govPayService.chiediStato(pagamento);

        it.govpay.servizi.v2_3.PagamentiTelematiciGPPrt port = govPayService.getPagamentiTelematiciGPPrt();
        GpChiediStatoTransazioneResponse gpChiediStatoPagamentoResponse = port.gpChiediStatoTransazione(statoPagamentoRequest);

        if (gpChiediStatoPagamentoResponse.getTransazione() == null) {
            model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
            return ViewMappings.PAGAMENTO_SPONTANEO_DETAIL;
        }
        List<it.govpay.servizi.commons.Pagamento> singoloPagamento = gpChiediStatoPagamentoResponse.getTransazione().getPagamento();

        Optional<it.govpay.servizi.commons.Pagamento> optional = singoloPagamento.stream().filter(sp -> (pagamento.getCodVersamentoEnte() + "_1").equals(sp.getCodSingoloVersamentoEnte()) || (pagamento.getCodVersamentoEnte()).equals(sp.getCodSingoloVersamentoEnte())).findFirst();

        if (optional.isPresent()) {
            //se c'è vuol dire che è eseguitoop
            pagamento.setCodPsp(gpChiediStatoPagamentoResponse.getTransazione().getCanale().getCodPsp());
            pagamento.setStatoPagamento(it.govpay.bd.model.Versamento.StatoVersamento.ESEGUITO.toString());
            pagamento.setIur(optional.get().getIur());
            Pagamento pag = pagamentoService.save(pagamento, _user.getUsername());
            model.addAttribute(ModelMappings.PAGAMENTO, pag);
        } else {
            if (null != gpChiediStatoPagamentoResponse.getTransazione().getRt() && gpChiediStatoPagamentoResponse.getTransazione().getRt().length != 0) {
                pagamento.setStatoPagamento(it.govpay.bd.model.Versamento.StatoVersamento.NON_ESEGUITO.toString());
            } else {
                pagamento.setStatoPagamento("IN ATTESA DI VERIFICA PAGAMENTO");
            }
            Pagamento pag = pagamentoService.save(pagamento, _user.getUsername());
            model.addAttribute(ModelMappings.PAGAMENTO, pag);
        }
        return ViewMappings.PAGAMENTO_SPONTANEO_DETAIL;

    }


    @RequestMapping(value = RequestMappings.PAGAMENTO_PRINT + "/{id}",
            method = RequestMethod.GET)
    public String printRicevuta(@PathVariable Long id,
                                ModelMap model, Authentication auth) {

        User _user = (User) auth.getPrincipal();
        Pagamento pagamento = pagamentoService.getPagamento(id);

        if (!pagamento.getEsecutore().getId().equals(_user.getId()) && !AuthorityUtil.isAdminLogged()) {
            return ViewMappings.ACCESS_DENIED;
        }
        if (null == pagamento.getIur()) {

        }
        model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
        return ViewMappings.PAGAMENTO_PRINT;
    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_VIEW_RT + "/{id}",
            method = RequestMethod.GET)
    public String printRT(@PathVariable Long id,
                          ModelMap model, Authentication auth, HttpServletResponse response) {

        if (AuthorityUtil.isAdminLogged()) {
            //TODO: DA RIVEDERE
            Pagamento pagamento = pagamentoService.getPagamento(id);

            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                TransformerFactory tFactory = TransformerFactory.newInstance();

                ClassLoader classLoader = getClass().getClassLoader();
                Transformer transformer = tFactory.newTransformer(new javax.xml.transform.stream.StreamSource(classLoader.getResource("howto.xsl").getFile()));

                byte[] out = buffer.toByteArray();
                response.setContentType("text/html");
                response.setContentLength(out.length);
                response.setHeader("Expires:", "0");
                response.getOutputStream().write(out);
                response.getOutputStream().flush();
                response.getOutputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ViewMappings.SEARCH;
    }

     @RequestMapping(value = RequestMappings.PAGAMENTO_RT + "/{id}", method = RequestMethod.GET)
    public String printRT(@PathVariable Long id, HttpServletResponse response) throws IOException {
        if (AuthorityUtil.isAdminLogged()) {
            Pagamento pagamento = pagamentoService.getPagamento(id);

            GpChiediStatoTransazione statoPagamentoRequest = govPayService.chiediStato(pagamento);

            it.govpay.servizi.v2_3.PagamentiTelematiciGPPrt port = govPayService.getPagamentiTelematiciGPPrt();
            GpChiediStatoTransazioneResponse gpChiediStatoPagamentoResponse = port.gpChiediStatoTransazione(statoPagamentoRequest);

            byte[] outArray = gpChiediStatoPagamentoResponse.getTransazione().getRt();
            response.setContentType("application/xml");
            response.setContentLength(outArray.length);
            response.setHeader("Expires:", "0");
            response.setHeader("Content-Disposition", "attachment; filename=estrazione_" + new Date().getTime() + ".xml");
            response.getOutputStream().write(outArray);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }

        return ViewMappings.SEARCH;
    }


    @RequestMapping(value = RequestMappings.PAGAMENTO_WISP_URL_RETURN,
            method = RequestMethod.POST)
    public String wispUrlReturn(@ModelAttribute("keyWISP") String keyWISP, @ModelAttribute("keyPA") String keyPA, Authentication auth, Model model) {

        if (null == keyPA) {
            return "redirect:" + RequestMappings.PAGAMENTO_STORICO;
        }
        User _user = (User) auth.getPrincipal();
        Pagamento pagamento = pagamentoService.findByPid(keyPA).get();
        pagamento.setKeyWisp(keyWISP);
        pagamento.setStatoPagamento("IN ATTESA DI VERIFICA PAGAMENTO");
        pagamento = pagamentoService.save(pagamento, SecurityUtil.getPrincipalName());
        it.govpay.servizi.v2_3.PagamentiTelematiciGPPrt port = govPayService.getPagamentiTelematiciGPPrt();
        GpChiediSceltaWispResponse gpSceltaWISPResponse = port.gpChiediSceltaWisp(govPayService.gpSceltaWISP(pagamento));
        if (gpSceltaWISPResponse.getCodEsito().equals("OK")) {

            GpAvviaTransazionePagamento rptRequest = govPayService.gpGeneraRpt(pagamento, _user);
            GpAvviaTransazionePagamentoResponse response = port.gpAvviaTransazionePagamento(rptRequest, null);
            if (response.getCodEsito().equals("OK")) {
                pagamento.setIuv(response.getRifTransazione().get(0).getIuv());
                pagamento.setCcp(response.getRifTransazione().get(0).getCcp());
                pagamento.setIdSessione(response.getPspSessionId());
                pagamentoService.save(pagamento, SecurityUtil.getPrincipalName());
                return "redirect:" + response.getUrlRedirect();
            } else {
                if (!CollectionUtils.isEmpty(response.getRifTransazione())) {
                    pagamento.setIuv(response.getRifTransazione().get(0).getIuv());
                    pagamento.setCcp(response.getRifTransazione().get(0).getCcp());
                }
                pagamento.setIdSessione(response.getPspSessionId());
                pagamento.setStatusResponse(response.getCodEsito() );
                pagamento.setStatoPagamento(response.getCodEsito());
                pagamento = pagamentoService.save(pagamento, SecurityUtil.getPrincipalName());
                model.addAttribute(ModelMappings.ESITO, "ERRORE");
                model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
                return "redirect:" + RequestMappings.PAGAMENTO_STORICO;
            }
        } else {
            pagamento.setStatusResponse(gpSceltaWISPResponse.getCodEsito());
            pagamento.setStatoPagamento(gpSceltaWISPResponse.getCodEsito());
            pagamento = pagamentoService.save(pagamento, SecurityUtil.getPrincipalName());
            model.addAttribute(ModelMappings.ESITO, "ERRORE");
            model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
            return "redirect:" + RequestMappings.PAGAMENTO_STORICO;
        }
    }


}