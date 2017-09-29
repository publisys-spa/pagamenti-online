package it.publisys.pagamentionline.controller.impl;

import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.PagamentiOnlineKey;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.enumeration.MailStatusEnum;
import it.publisys.pagamentionline.domain.enumeration.MailTypeEnum;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.user.Mail;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.dto.DebitoDTO;
import it.publisys.pagamentionline.service.*;
import it.publisys.pagamentionline.transformer.VersamentoTransformer;
import it.publisys.pagamentionline.util.sec.SecurityUtil;
import it.publisys.spring.mail.MailEngine;
import it.publisys.spring.util.view.PageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Francesco A. Tabino
 */
@Controller
@SessionAttributes(value = "pagamenti", types = List.class)
public class PagamentoController extends BaseController {

    @Autowired
    private PagamentoService pagamentoService;
    @Autowired
    private EnteService enteService;

    @Autowired
    private GovPayService govPayService;

    @Autowired
    private VersamentoTransformer versamentoTransformer;

    @Autowired
    private MailService mailService;

    private static final Logger _log = LoggerFactory.getLogger(LoginImpl.class.getName());

    @RequestMapping(value = RequestMappings.PAGAMENTO_STORICO,
            method = RequestMethod.GET)
    public String storico(ModelMap model, Authentication auth, HttpServletRequest request, Pageable pageable) {
        String esito = request.getParameter(ModelMappings.ESITO);
        String esitoPsp = request.getParameter(ModelMappings.ESITO_PSP);
        User _user = (User) auth.getPrincipal();
        String keyPA = null;
        String idDominio = null;
        String type = null;
        if (!StringUtils.isEmpty(esito)) {
            keyPA = request.getParameter("keyPA");
            type = request.getParameter("type");
            //TODO: Utilizzare il type per categorizzare l'errore ricevuto
            if (null != keyPA) {
                model.addAttribute(ModelMappings.ERROR, "Si è verificato un errore durante il pagamento con numero Operazione: " + keyPA + ". Si consiglia di consultare il dettaglio del pagamento.");
            } else {
                model.addAttribute(ModelMappings.ERROR, "Si è verificato un errore durante il pagamento. Si consiglia di consultare il dettaglio del pagamento.");
            }
        }
        if (null != esitoPsp && !esitoPsp.isEmpty()) {
            idDominio = request.getParameter("idDominio");
            Ente ente = enteService.findByCodDominio(idDominio);
            String testo = "";
            Mail mail = new Mail();
            mail.setDate(new Date());
            mail.setType(MailTypeEnum.NOTIFY);
            mail.setStatus(MailStatusEnum.READY);
            mail.setName(_user.getFirstname());
            mail.setUsername(_user.getUsername());
            mail.setFiscalcode(_user.getFiscalcode());
            mail.setEmail(_user.getEmail());
            String message = "";
            if (esitoPsp.equals(PagamentiOnlineKey.ESITO_DIFFERITO)) {
                message += "Pagamento eseguito. L'esito del pagamento eseguito presso il Portale PSP sarà noto solo al ricevimento della RT. " +
                        " Si consiglia pertanto di entrare nel dettaglio del pagamento e controllarne lo stato.";
                model.addAttribute(ModelMappings.MESSAGE, message);
                testo = "<p>Gentile " + _user.getFirstname() + " " + _user.getLastname() + ",</p></br><p>la ringraziamo per aver utilizzato il portale dei pagamenti online.</p></br>" +
                        "<p>La informiamo che l'esito del pagamento eseguito presso il Portale PSP sarà noto solo al ricevimento della Ricevuta di Transazione da parte del Nodo Centrale dei pagamenti.</p></br>" +
                        "</br>" +
                        "<p>Nei prossimi giorni riceverà per email un avviso se l'esito della transazione e' positivo.</p></br>" +
                        "</br>" +
                        "Cordiali saluti</br>";
            }
            if (esitoPsp.equals(PagamentiOnlineKey.ESITO_ERROR)) {
                message += "Il pagamento presso il Portale PSP non è stato eseguito con successo. A breve verrà ricevuta una RT con l'esito negativo della transazione, " +
                        "si consiglia pertanto di entrare nel dettaglio del pagamento e controllarne lo stato.";
                model.addAttribute(ModelMappings.ERROR, message);
                testo = "<p>Gentile " + _user.getFirstname() + " " + _user.getLastname() + ",</p></br><p>la ringraziamo per aver utilizzato il portale dei pagamenti online.</p></br>" +
                        "<p>La informiamo che pagamento presso il Portale PSP non è stato eseguito con successo. Può ritentare il pagamento in qualsiasi momento sul portale</p></br>" +
                        "</br>" +
                        "Cordiali saluti</br>";

            }
            if (esitoPsp.equals(PagamentiOnlineKey.ESITO_OK)) {
                message += "Il pagamento presso il Portale PSP è stato eseguito con successo. A breve verrà ricevuta una RT con l'esito positivo della transazione, " +
                        "si consiglia pertanto di entrare nel dettaglio del pagamento e controllarne lo stato.";
                model.addAttribute(ModelMappings.MESSAGE, message);
                testo = "<p>Gentile " + _user.getFirstname() + " " + _user.getLastname() + ",</p></br><p>la ringraziamo per aver utilizzato il portale dei pagamenti online.</p></br>" +
                        "<p>Le confermiamo di aver effettuato il pagamento da Lei richiesto e l'importo e' stato addebitato tramite servizio di pagamento PagoPA gestito da Agid (Agenzia per l'Italia Digitale).</p></br>" +
                        "</br>" +
                        "<p>Nei prossimi giorni riceverà per email un avviso di conferma del pagamento con il relativo dettaglio.</p></br>" +
                        "</br>" +
                        "Cordiali saluti</br>";
            }
            if(null != ente){
                testo += "<p>" + ente.getName() + "</p>";
            }
            testo += "<p>CONTATTI:</p>" +
                    "<p>Telefono: 800 29 20 20 - 0971 471372</p>" +
                    "<p>Fax: 0971 010002</p>" +
                    "<p>P.IVA: 00975860768</p>" +
                    "<p>Help-desk: <a href=\"http://www.ibasilicata.it/web/guest/centro-servizi-help-desk\">Centro servizi</p>";
            mail.setText(testo);
            mailService.create(mail);
            try {
                mailService.send(mail, "Conferma effettuazione pagamento con il portale dei pagamenti online.");
            } catch (Throwable throwable) {
                throwable.printStackTrace();            }

        }
        PageWrapper<Pagamento> _page = new PageWrapper<>(pagamentoService.findAllByUser(pageable, (User) auth.getPrincipal()),
                RequestMappings.PAGAMENTO_STORICO);
        model.addAttribute(ModelMappings.PAGE, _page);
        model.addAttribute(ModelMappings.PAGAMENTI, _page.getContent());
        if (auth != null && auth.isAuthenticated()) {
            SecurityUtil.listaDebiti(auth, model, govPayService, versamentoTransformer);
        }
        return ViewMappings.PAGAMENTO_SPONTANEO_LIST;
    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_STORICO,
            method = RequestMethod.POST)
    public String storicoReturn(ModelMap model, Authentication auth, Pageable pageable, @ModelAttribute("keyPA") String keyPA, @ModelAttribute("idDominio") String idDominio,
                                @ModelAttribute("type") String type) {
        PageWrapper<Pagamento> _page = new PageWrapper<>(pagamentoService.findAllByUser(pageable, (User) auth.getPrincipal()),
                RequestMappings.PAGAMENTO_STORICO);
        if (!type.isEmpty()) {
            String error = "Pagamento con numero Operazione: " + keyPA + " non eseguito";
            if (type.equals(PagamentiOnlineKey.TYPE_TIMEOUT)) {
                error += " per superamento tempo massimo a disposizione.";
            }
            if (type.equals(PagamentiOnlineKey.TYPE_IBAN)) {
                error += " per via dell'IBAN non presente.";
            }
            if (type.equals(PagamentiOnlineKey.TYPE_ANNULLO)) {
                error += " per operazione annullata.";
            }
            model.addAttribute(ModelMappings.ERROR, error);
        }

        model.addAttribute(ModelMappings.PAGE, _page);
        model.addAttribute(ModelMappings.PAGAMENTI, _page.getContent());
        if (auth != null && auth.isAuthenticated()) {
            SecurityUtil.listaDebiti(auth, model, govPayService, versamentoTransformer);
        }
        return ViewMappings.PAGAMENTO_SPONTANEO_LIST;
    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_STORICO_SEARCH, method = RequestMethod.POST)
    public String pagamentoDebitorioSearch(@RequestParam(ModelMappings.IUV) String iuv, ModelMap model, Authentication auth, Pageable pageable) {

        if (auth != null && auth.isAuthenticated()) {

            List<Pagamento> pagamenti = pagamentoService.findAllByUserIuv((User) auth.getPrincipal(), iuv.trim());
            PageWrapper<Pagamento> _page = new PageWrapper<Pagamento>(new PageImpl<Pagamento>(pagamenti), RequestMappings.PAGAMENTO_STORICO_SEARCH);
            model.addAttribute(ModelMappings.PAGE, _page);
            model.addAttribute(ModelMappings.PAGAMENTI, _page.getContent());
        }
        return ViewMappings.PAGAMENTO_SPONTANEO_LIST;
    }

}
