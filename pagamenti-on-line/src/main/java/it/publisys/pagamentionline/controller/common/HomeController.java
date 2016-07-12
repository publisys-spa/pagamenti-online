package it.publisys.pagamentionline.controller.common;

import it.govpay.servizi.PagamentiTelematiciGPPrt;
import it.govpay.servizi.commons.StatoVersamento;
import it.govpay.servizi.gpprt.GpChiediListaVersamentiResponse;
import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.dto.DebitoDTO;
import it.publisys.pagamentionline.service.GovPayService;
import it.publisys.pagamentionline.service.PagamentoService;
import it.publisys.pagamentionline.transformer.VersamentoTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mcolucci
 */
@Controller
public class HomeController {

    private static final Logger _log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private GovPayService govPayService;

    @Autowired
    private VersamentoTransformer versamentoTransformer;

    @RequestMapping(value = "/netbeans-tomcat-status-test")
    public String netbeans(ModelMap model) {
        return ViewMappings.INDEX;
    }

    @RequestMapping(value = RequestMappings.INDEX, method = RequestMethod.GET)
    public String index(ModelMap model, Authentication auth) {

        if (auth != null && auth.isAuthenticated()) {
            User user = (User) auth.getPrincipal();
            List<DebitoDTO> debiti = new ArrayList<>();
            //TODO: mettere nel db questo pagamento 66l
            Pagamento pagamento = pagamentoService.getPagamento(66L);
            PagamentiTelematiciGPPrt port = govPayService.getPagamentiTelematiciGPPrt(pagamento);
            GpChiediListaVersamentiResponse gpCercaVersamentiResponse = port.gpChiediListaVersamenti(govPayService.gpCercaVersamentiRequest(user, pagamento.getEnte()));

            gpCercaVersamentiResponse.getVersamento().stream()
                    .filter(v -> !v.getStato().value().equals(StatoVersamento.ESEGUITO.value())).limit(5).forEach(v ->
                debiti.add(versamentoTransformer.transofrmToPagamento(v)));


            model.addAttribute("debiti", debiti);

            List<Pagamento> fisrt5ByUser = pagamentoService.findFisrt5ByUser(user);
            model.addAttribute(ModelMappings.PAGAMENTI, fisrt5ByUser);
        }

        return ViewMappings.INDEX;
    }


    @RequestMapping(value = RequestMappings.HOME, method = RequestMethod.GET)
    public String home(ModelMap model, HttpServletRequest request, Authentication auth) {

        if (request.isUserInRole("ROLE_ADMIN")) {
            return ViewMappings.HOME;
        }
        return index(model, auth);
    }

}