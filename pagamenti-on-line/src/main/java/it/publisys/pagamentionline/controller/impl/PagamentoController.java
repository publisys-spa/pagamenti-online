package it.publisys.pagamentionline.controller.impl;

import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.service.PagamentoService;
import it.publisys.spring.util.view.PageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Francesco A. Tabino
 */
@Controller
public class PagamentoController extends BaseController {

    @Autowired
    private PagamentoService pagamentoService;

    @RequestMapping(value = RequestMappings.PAGAMENTO_STORICO,
            method = RequestMethod.GET)
    public String storico(ModelMap model, Authentication auth, Pageable pageable) {

        PageWrapper<Pagamento> _page = new PageWrapper<>(pagamentoService.findAllByUser(pageable, (User) auth.getPrincipal()),
                RequestMappings.PAGAMENTO_STORICO);
        model.addAttribute(ModelMappings.PAGE, _page);
        model.addAttribute(ModelMappings.PAGAMENTI, _page.getContent());

        return ViewMappings.PAGAMENTO_SPONTANEO_LIST;
    }

}
