package it.publisys.pagamentionline.controller.impl;

import it.govpay.bd.model.SingoloVersamento;
import it.govpay.servizi.PagamentiTelematiciGPPrt;
import it.govpay.servizi.commons.Versamento;
import it.govpay.servizi.gpprt.*;
import it.govpay.servizi.pa.*;
import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.service.EnteService;
import it.publisys.pagamentionline.service.GovPayService;
import it.publisys.pagamentionline.service.PagamentoService;
import it.publisys.pagamentionline.util.sec.SecurityUtil;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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

    @ModelAttribute(ModelMappings.ENTI)
    public List<Ente> listaEnti() {
        return enteService.findAll();
    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_SPONTANEO,
            method = RequestMethod.GET)
    public String toView(ModelMap model, HttpServletRequest request) {

        Pagamento pagamento = new Pagamento();
        String pid = request.getParameter("pid");
        System.out.println("pid:" + pid);
        if (!StringUtils.isEmpty(pid)) {
            pagamento = pagamentoService.findByPid(pid).get();
            model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
            return ViewMappings.PAGAMENTO_SPONTANEO_DETAIL;
        }
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

            model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
            return ViewMappings.PAGAMENTO_SPONTANEO;
        }

        User _user = (User) auth.getPrincipal();
        pagamento.setEsecutore(_user);
        pagamento.setStatoPagamento("IN ATTESA DI SCELTA DEL WISP");
        pagamento = pagamentoService.save(pagamento, _user.getUsername());
        Properties prop = pagamentoService.loadProperties(pagamento);
        model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
        model.addAttribute(ModelMappings.URLBACK, prop.getProperty("urlBack"));
        model.addAttribute(ModelMappings.URLRETURN, prop.getProperty("urlReturn"));
        model.addAttribute(ModelMappings.CODDOMINIO, prop.getProperty("codDominio"));
        return ViewMappings.PAGAMENTO_SPONTANEO_WISP;

    }



    @RequestMapping(value = RequestMappings.PAGAMENTO_SPONTANEO_ELIMINA,
            method = RequestMethod.POST)
    public String elimina(@ModelAttribute(ModelMappings.PAGAMENTO) @Valid Pagamento pagamento,
                         BindingResult result, ModelMap model, Authentication auth) {


        User _user = (User) auth.getPrincipal();
        pagamento.setEsecutore(_user);
        pagamento.setStatoPagamento("ELIMINATO");
        System.out.println("eliminato:" + pagamento.getPid());
        pagamento = pagamentoService.save(pagamento, _user.getUsername());
       return "redirect:" + RequestMappings.INDEX;

    }


    @RequestMapping(value = RequestMappings.PAGAMENTO_STATO,
            method = RequestMethod.POST)
    public String chiediStato(@ModelAttribute(ModelMappings.PAGAMENTO) @Valid Pagamento pagamento,
                              BindingResult result, ModelMap model, Authentication auth) {

        User _user = (User) auth.getPrincipal();
        GpChiediStatoTransazione statoPagamentoRequest = govPayService.chiediStato(pagamento);

        PagamentiTelematiciGPPrt port = govPayService.getPagamentiTelematiciGPPrt(pagamento);
        GpChiediStatoTransazioneResponse gpChiediStatoPagamentoResponse = port.gpChiediStatoTransazione(statoPagamentoRequest);

        //TODO investigare sul cambio da pagamento a versamento
        if (gpChiediStatoPagamentoResponse.getTransazione().getPagamento() == null) {
            model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
            return ViewMappings.PAGAMENTO_SPONTANEO_DETAIL;
        }
        List<it.govpay.servizi.commons.Pagamento> singoloPagamento = gpChiediStatoPagamentoResponse.getTransazione().getPagamento();

        Optional<it.govpay.servizi.commons.Pagamento> optional = singoloPagamento.stream().filter(sp -> (pagamento.getPid() + "_1").equals(sp.getCodSingoloVersamentoEnte())).findFirst();

        if (optional.isPresent()) {
            //se c'è vuol dire che è eseguito
            pagamento.setStatoPagamento(it.govpay.bd.model.Versamento.StatoVersamento.ESEGUITO.toString());
            //String value = stato.value();
            Pagamento pag = pagamentoService.save(pagamento, _user.getUsername());
            model.addAttribute(ModelMappings.PAGAMENTO, pag);
        } else {
            pagamento.setStatoPagamento(it.govpay.bd.model.Versamento.StatoVersamento.NON_ESEGUITO.toString());
            //String value = stato.value();
            Pagamento pag = pagamentoService.save(pagamento, _user.getUsername());
            model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
        }
        return ViewMappings.PAGAMENTO_SPONTANEO_DETAIL;

    }


//    @RequestMapping(value = RequestMappings.PAGAMENTO_STATO,
//            method = RequestMethod.POST)
//    public String chiediStato(@ModelAttribute(ModelMappings.PAGAMENTO) @Valid Pagamento pagamento,
//                              BindingResult result, ModelMap model, Authentication auth) {
//
//        User _user = (User) auth.getPrincipal();
//
//        GpChiediStatoPagamento statoPagamentoRequest = govPayService.chiediStato(pagamento);
//
//        PagamentiTelematiciGPPrt port = govPayService.getPagamentiTelematiciGPPrt(pagamento);
//        GpChiediStatoPagamentoResponse gpChiediStatoPagamentoResponse = port.gpChiediStatoPagamento(statoPagamentoRequest);
//
//        //TODO investigare sul cambio da pagamento a versamento
//        if (gpChiediStatoPagamentoResponse.getVersamento() == null) {
//            model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
//            return ViewMappings.PAGAMENTO_SPONTANEO_DETAIL;
//        }
//
//        List<Versamento.Pagamento.SingoloPagamento> singoloPagamento = gpChiediStatoPagamentoResponse
//                .getVersamento()
//                .getPagamento().getSingoloPagamento();
//
//        Optional<Versamento.Pagamento.SingoloPagamento> optional =
//                singoloPagamento.stream()
//                        .filter(sp -> pagamento.getPid().equals(sp.getCodVersamentoEnte()))
//                        .findFirst();
//
//        if (optional.isPresent()) {
//            pagamento.setStatoPagamento(optional.get().getStato().value());
//            //String value = stato.value();
//            Pagamento pag = pagamentoService.save(pagamento, _user.getUsername());
//            model.addAttribute(ModelMappings.PAGAMENTO, pag);
//        } else {
//            model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
//        }
//        return ViewMappings.PAGAMENTO_SPONTANEO_DETAIL;
//
//    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_PRINT + "/{id}",
            method = RequestMethod.GET)
    public String printRicevuta(@PathVariable Long id,
                                ModelMap model, Authentication auth) {

        User _user = (User) auth.getPrincipal();
        Pagamento pagamento = pagamentoService.getPagamento(id);

        if (!pagamento.getEsecutore().getId().equals(_user.getId())) {
            return ViewMappings.ACCESS_DENIED;
        }

        model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
        return ViewMappings.PAGAMENTO_PRINT;
    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_WISP_URL_RETURN,
            method = RequestMethod.POST)
    public String wispUrlReturn(@ModelAttribute("keyWISP") String keyWISP, @ModelAttribute("keyPA") String keyPA,
                                Authentication auth, Model model) {

        User _user = (User) auth.getPrincipal();
        Pagamento pagamento = pagamentoService.findByPid(keyPA).get();
        pagamento.setKeyWisp(keyWISP);
        pagamento.setStatoPagamento("IN ATTESA DI VERIFICA PAGAMENTO");
        pagamento = pagamentoService.save(pagamento, SecurityUtil.getPrincipalName());

        PagamentiTelematiciGPPrt port = govPayService.getPagamentiTelematiciGPPrt(pagamento);
        GpChiediSceltaWispResponse gpSceltaWISPResponse = port.gpChiediSceltaWisp(govPayService.gpSceltaWISP(pagamento));
        if (gpSceltaWISPResponse.getCodEsitoOperazione().value().equals("OK")) {
            GpAvviaTransazionePagamento rptRequest = govPayService.gpGeneraRpt(pagamento, _user, gpSceltaWISPResponse.getCanale());
            GpAvviaTransazionePagamentoResponse response = port.gpAvviaTransazionePagamento(rptRequest);
            if (response.getCodEsitoOperazione().value().equals("OK")) {
                System.out.println("sono nell'if");
                pagamento.setRefnumber(response.getRifTransazione().get(0).getIuv());
                pagamento.setCcp(response.getRifTransazione().get(0).getCcp());
                pagamentoService.save(pagamento, SecurityUtil.getPrincipalName());
                System.out.println("sono nell'if: " +response.getUrlRedirect());
                return "redirect:" + response.getUrlRedirect();
            } else {
                if (!CollectionUtils.isEmpty(response.getRifTransazione())) {
                    pagamento.setRefnumber(response.getRifTransazione().get(0).getIuv());
                    pagamento.setCcp(response.getRifTransazione().get(0).getCcp());
                }
                System.out.println("sono nell'else di ok");
                pagamento.setStatusResponse(response.getCodEsitoOperazione() + " " + response.getDescrizioneEsitoOperazione());
                pagamento.setStatoPagamento(response.getCodEsitoOperazione().value());
                pagamento = pagamentoService.save(pagamento, SecurityUtil.getPrincipalName());
                model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
              //  return "redirect:" + RequestMappings.PAGAMENTO_SPONTANEO + "?pid=" + pagamento.getPid();
                return "redirect:" + RequestMappings.PAGAMENTO_STORICO;
            }
        } else {
            System.out.println("sono nell'else");
            pagamento.setStatusResponse(gpSceltaWISPResponse.getCodEsitoOperazione() + " " + gpSceltaWISPResponse.getDescrizioneEsitoOperazione());
            pagamento.setStatoPagamento(gpSceltaWISPResponse.getCodEsitoOperazione().value());
            pagamento = pagamentoService.save(pagamento, SecurityUtil.getPrincipalName());

            model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
            return "redirect:" + RequestMappings.PAGAMENTO_STORICO;
            //return "redirect:" + RequestMappings.PAGAMENTO_SPONTANEO + "?pid=" + pagamento.getPid();
        }


    }

//    @RequestMapping(value = RequestMappings.PAGAMENTO_WISP_URL_RETURN,
//            method = RequestMethod.POST)
//    public String wispUrlReturn(@ModelAttribute("keyWISP") String keyWISP, @ModelAttribute("keyPA") String keyPA,
//                                Authentication auth, Model model) {
//
//        User _user = (User) auth.getPrincipal();
//        Pagamento pagamento = pagamentoService.findByPid(keyPA).get();
//        pagamento.setKeyWisp(keyWISP);
//        pagamento.setStatoPagamento("IN ATTESA DI VERIFICA PAGAMENTO");
//        pagamento = pagamentoService.save(pagamento, SecurityUtil.getPrincipalName());
//
//        PagamentiTelematiciGPPrt port = govPayService.getPagamentiTelematiciGPPrt(pagamento);
//        GpSceltaWISPResponse gpSceltaWISPResponse = port.gpSceltaWISP(govPayService.gpSceltaWISP(pagamento));
//
//        if (gpSceltaWISPResponse.getCodEsito() == CodEsito.OK) {
//            GpGeneraRpt rptRequest = govPayService.gpGeneraRpt(pagamento, _user, gpSceltaWISPResponse.getCanale());
//            GpGeneraRptResponse response = port.gpGeneraRpt(rptRequest);
//
//            if (response.getCodEsito() == CodEsito.OK) {
//                pagamento.setRefnumber(response.getIdPagamento().get(0).getCodApplicazione());
//                pagamentoService.save(pagamento, SecurityUtil.getPrincipalName());
//                return "redirect:" + response.getUrl();
//            } else {
//                if (!CollectionUtils.isEmpty(response.getIdPagamento())) {
//                    pagamento.setRefnumber(response.getIdPagamento().get(0).getCodApplicazione());
//                }
//                pagamento.setStatusResponse(response.getCodErrore().value() +" "+ response.getDescrizioneErrore());
//                pagamento.setStatoPagamento(response.getCodErrore().value());
//                pagamento = pagamentoService.save(pagamento, SecurityUtil.getPrincipalName());
//
//                model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
//                return "redirect:" + RequestMappings.PAGAMENTO_SPONTANEO + "?pid=" + pagamento.getPid();
//            }
//        } else {
//            pagamento.setStatusResponse(gpSceltaWISPResponse.getCodErrore().value()+" "+ gpSceltaWISPResponse.getDescrizioneErrore());
//            pagamento.setStatoPagamento(gpSceltaWISPResponse.getCodErrore().value());
//            pagamento = pagamentoService.save(pagamento, SecurityUtil.getPrincipalName());
//
//            model.addAttribute(ModelMappings.PAGAMENTO, pagamento);
//            return "redirect:" + RequestMappings.PAGAMENTO_SPONTANEO + "?pid=" + pagamento.getPid();
//        }
//
//
//    }

}