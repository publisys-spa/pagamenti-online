package it.publisys.pagamentionline.controller.impl;

import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.service.EnteService;
import it.publisys.pagamentionline.util.sec.AuthorityUtil;
import it.publisys.pagamentionline.util.sec.SecurityUtil;
import it.publisys.spring.util.view.PageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Francesco A. Tabino
 */
@Controller
public class EnteController extends BaseController {

    private static final Logger _log = LoggerFactory.getLogger(EnteController.class);

    @Autowired
    private EnteService enteService;

    @RequestMapping(value = RequestMappings.ENTI,
            method = RequestMethod.GET)
    public String toList(Model model, Pageable pageable) {

        PageWrapper<Ente> _page = new PageWrapper<>(enteService.findAll(pageable),
                RequestMappings.ENTI);
        model.addAttribute(ModelMappings.PAGE, _page);
        model.addAttribute(ModelMappings.ENTI, _page.getContent());

        return ViewMappings.ENTI;
    }

    @RequestMapping(value = RequestMappings.ENTE,
            method = RequestMethod.GET)
    public String toView(ModelMap model) {
        return toView(null, null, model);
    }

    @RequestMapping(value = RequestMappings.ENTE + "/{id}",
            method = RequestMethod.GET)
    public String toView(@PathVariable(value = "id") Long id,
                         @RequestParam(value = "op", required = false) String op,
                         ModelMap model) {

        if (op != null) {
            if ("D".equalsIgnoreCase(op)) {
                return toDelete(id);
            }
        }

        Ente ente;

        if (id != null && enteService.exists(id)) {
            ente = enteService.getOne(id);
        } else {
            ente = new Ente();
        }

        model.addAttribute(ModelMappings.ENTE, ente);

        return ViewMappings.ENTE;
    }

    @RequestMapping(value = RequestMappings.ENTE,
            method = RequestMethod.POST)
    public String toSave(
            @ModelAttribute(value = ModelMappings.ENTE) @Valid Ente ente,
            BindingResult result, ModelMap model) {

        if (result.hasErrors()) {
            model.addAttribute(ModelMappings.ENTE, ente);
            return ViewMappings.ENTE;
        }

        String _user = SecurityUtil.getPrincipal().getUsername();
        ente = enteService.save(ente, _user);
        model.addAttribute(ModelMappings.MESSAGE, "Salvataggio effettuato");
        model.addAttribute(ModelMappings.ENTE, ente);
        _log.info("Salvataggio dell'ente: " + ente.getName() + " effettuato correttamente");
        return ViewMappings.ENTE;
    }

    private String toDelete(Long id) {

        if (AuthorityUtil.isAuthenticated() && AuthorityUtil.isAdminLogged()) {
            String _user = SecurityUtil.getPrincipal().getUsername();
            enteService.delete(id, _user);

            return ViewMappings.REDIRECT + RequestMappings.ENTI;
        }
        return ViewMappings.HOME;
    }

}
