package it.publisys.pagamentionline.controller.impl;

import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.TipologiaTributo;
import it.publisys.pagamentionline.service.TipologiaTributoService;
import it.publisys.pagamentionline.util.sec.AuthorityUtil;
import it.publisys.pagamentionline.util.sec.SecurityUtil;
import it.publisys.spring.util.view.PageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class TipologiaController
        extends BaseController {

    private static final Logger _log = LoggerFactory.getLogger(TipologiaController.class);

    @Autowired
    private TipologiaTributoService tipologiaTributoService;

    @RequestMapping(value = RequestMappings.TIPOLOGIE,
            method = RequestMethod.GET)
    public String toList(Model model, Pageable pageable,Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            PageWrapper<TipologiaTributo> _page = new PageWrapper<>(tipologiaTributoService.getAllTipologie(pageable),
                    RequestMappings.TIPOLOGIE);
            model.addAttribute(ModelMappings.PAGE, _page);
            model.addAttribute(ModelMappings.TIPOLOGIE, _page.getContent());
        }
        return ViewMappings.TIPOLOGIE;
    }

    @RequestMapping(value = RequestMappings.TIPOLOGIA,
            method = RequestMethod.GET)
    public String toView(ModelMap model,Authentication auth) {
        return toView(null, null, model, auth);
    }

    @RequestMapping(value = RequestMappings.TIPOLOGIA + "/{id}",
            method = RequestMethod.GET)
    public String toView(@PathVariable(value = "id") Long id,
                         @RequestParam(value = "op", required = false) String op,
                         ModelMap model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            if (op != null) {
                if ("D".equalsIgnoreCase(op)) {
                    return toDelete(id, model);
                }
            }
            TipologiaTributo _tipologia;
            if (id != null) {
                _tipologia = tipologiaTributoService.getTipologia(id);
            } else {
                _tipologia = new TipologiaTributo();
            }

            model.addAttribute(ModelMappings.TIPOLOGIA, _tipologia);
        }
        return ViewMappings.TIPOLOGIA;
    }

    @RequestMapping(value = RequestMappings.TIPOLOGIA,
            method = RequestMethod.POST)
    public String toSave(
            @ModelAttribute(value = ModelMappings.TIPOLOGIA) @Valid TipologiaTributo tipologia,
            BindingResult result, ModelMap model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            if (result.hasErrors()) {
                model.addAttribute(ModelMappings.TIPOLOGIA, tipologia);
                return ViewMappings.TIPOLOGIA;
            }

            String _user = SecurityUtil.getPrincipal().getUsername();
            tipologia = tipologiaTributoService.save(tipologia, _user);
            model.addAttribute(ModelMappings.MESSAGE, "Salvataggio effettuato");
            model.addAttribute(ModelMappings.TIPOLOGIA, tipologia);
        }
        return ViewMappings.TIPOLOGIA;
    }

    private String toDelete(Long id, ModelMap model) {
        if (AuthorityUtil.isAuthenticated() && AuthorityUtil.isAdminLogged()) {
            String _user = SecurityUtil.getPrincipal().getUsername();
            tipologiaTributoService.delete(id, _user);

            return ViewMappings.REDIRECT + RequestMappings.TIPOLOGIE;
        }
        return ViewMappings.HOME;
    }
}
