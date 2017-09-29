package it.publisys.pagamentionline.controller.impl;

import app.App;
import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.Applicazione;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.TipologiaTributo;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.service.ApplicazioneService;
import it.publisys.pagamentionline.service.EnteService;
import it.publisys.pagamentionline.service.TributoService;
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
import java.util.List;

@Controller
public class ApplicazioneController
        extends BaseController {

    private static final Logger _log = LoggerFactory.getLogger(ApplicazioneController.class);


    @Autowired
    private ApplicazioneService applicazioneService;


    @RequestMapping(value = RequestMappings.APPLICAZIONI,
            method = RequestMethod.GET)
    public String toList(Model model, Pageable pageable, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            PageWrapper<Applicazione> _page = new PageWrapper<>(applicazioneService.getAllApplicazioni(pageable),
                    RequestMappings.APPLICAZIONI);
            model.addAttribute(ModelMappings.PAGE, _page);
            model.addAttribute(ModelMappings.APPLICAZIONI, _page.getContent());
        }
        return ViewMappings.APPLICAZIONI;
    }

    @RequestMapping(value = RequestMappings.APPLICAZIONE,
            method = RequestMethod.GET)
    public String toView(ModelMap model, Authentication auth) {
        return toView(null, null, model, auth);
    }

    @RequestMapping(value = RequestMappings.APPLICAZIONE + "/{id}",
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

            Applicazione applicazione;

            if (id != null) {
                applicazione = applicazioneService.getApplicazione(id);
            } else {
                applicazione = new Applicazione();
            }
            model.addAttribute(ModelMappings.APPLICAZIONE, applicazione);
        }
        return ViewMappings.APPLICAZIONE;
    }

    @RequestMapping(value = RequestMappings.APPLICAZIONE,
            method = RequestMethod.POST)
    public String toSave(
            @ModelAttribute(value = ModelMappings.APPLICAZIONE) @Valid Applicazione applicazione,
            BindingResult result, ModelMap model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            if (result.hasErrors()) {
                model.addAttribute(ModelMappings.APPLICAZIONE, applicazione);
                return ViewMappings.APPLICAZIONE;
            }
            String _user = SecurityUtil.getPrincipal().getUsername();
            applicazione = applicazioneService.save(applicazione, _user);
            model.addAttribute(ModelMappings.MESSAGE, "Salvataggio effettuato");
            model.addAttribute(ModelMappings.APPLICAZIONE, applicazione);
        }
        return ViewMappings.APPLICAZIONE;
    }

    private String toDelete(Long id, ModelMap model) {
        if (AuthorityUtil.isAuthenticated() && AuthorityUtil.isAdminLogged()) {
            String _user = SecurityUtil.getPrincipal().getUsername();
            applicazioneService.delete(id, _user);
            return ViewMappings.REDIRECT + RequestMappings.APPLICAZIONI;
        }
        return ViewMappings.HOME;
    }

}
