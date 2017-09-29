package it.publisys.pagamentionline.controller.impl;

import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.Applicazione;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.TipologiaTributo;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.domain.search.Filter;
import it.publisys.pagamentionline.domain.search.SearchResults;
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
public class TributoController
        extends BaseController {

    private static final Logger _log = LoggerFactory.getLogger(TributoController.class);

    @Autowired
    private RataController rataController;

    @Autowired
    private TributoService tributoService;

    @Autowired
    private ApplicazioneService applicazioneService;

    @Autowired
    private EnteService enteService;

    @RequestMapping(value = RequestMappings.TRIBUTI,
            method = RequestMethod.GET)
    public String toList(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            SearchResults _sresults = null;
            Filter filter = new Filter();
            filter.setTipo(ModelMappings.SEARCH_TYPE_TRIBUTI);
            model.addAttribute(ModelMappings.FILTER, filter);
            _sresults = new SearchResults<>(filter.getTipo(), tributoService.getAllTributi());
            model.addAttribute(ModelMappings.SEARCH_RESULTS, _sresults);
        }
        return ViewMappings.TRIBUTI;
    }

    @RequestMapping(value = RequestMappings.TRIBUTO,
            method = RequestMethod.GET)
    public String toView(ModelMap model,Authentication auth) {

        return toView(null, null, model, auth);
    }

    @RequestMapping(value = RequestMappings.TRIBUTO + "/{id}",
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

            Tributo _tributo;
            if (id != null) {
                _tributo = tributoService.getTributo(id);
                rataController.toList(_tributo, model);
            } else {
                _tributo = new Tributo();
            }
            List<Ente> entes = enteService.findAll();
            List<Applicazione> applicazioni = applicazioneService.getAllApplicazioni();
            model.addAttribute(ModelMappings.TRIBUTO, _tributo);
            model.addAttribute(ModelMappings.ENTI, entes);
            model.addAttribute(ModelMappings.APPLICAZIONI, applicazioni);
        }
        return ViewMappings.TRIBUTO;
    }

    @RequestMapping(value = RequestMappings.TRIBUTO,
            method = RequestMethod.POST)
    public String toSave(
            @ModelAttribute(value = ModelMappings.TRIBUTO) @Valid Tributo tributo,
            BindingResult result, ModelMap model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            List<Ente> entes = enteService.findAll();
            List<Applicazione> applicazioni = applicazioneService.getAllApplicazioni();
            if (result.hasErrors()) {
                model.addAttribute(ModelMappings.TRIBUTO, tributo);
                model.addAttribute(ModelMappings.ENTI, entes);
                model.addAttribute(ModelMappings.APPLICAZIONI, applicazioni);
                return ViewMappings.TRIBUTO;
            }

            String _user = SecurityUtil.getPrincipal().getUsername();
            tributo = tributoService.save(tributo, _user);
            model.addAttribute(ModelMappings.MESSAGE, "Salvataggio effettuato");
            // carico lista rate del tributo
            rataController.toList(tributo, model);

            model.addAttribute(ModelMappings.TRIBUTO, tributo);
            model.addAttribute(ModelMappings.ENTI, entes);
            model.addAttribute(ModelMappings.APPLICAZIONI, applicazioni);
        }
        return ViewMappings.TRIBUTO;
    }

    private String toDelete(Long id, ModelMap model) {
        if (AuthorityUtil.isAuthenticated() && AuthorityUtil.isAdminLogged()) {
            String _user = SecurityUtil.getPrincipal().getUsername();
            tributoService.delete(id, _user);
            return ViewMappings.REDIRECT + RequestMappings.TRIBUTI;
        }
        return ViewMappings.HOME;
    }

    @ModelAttribute(ModelMappings.TIPOLOGIE)
    public List<TipologiaTributo> tipologieTributi() {
        return tributoService.getTipologiaTributoService().getAllTipologie();
    }

    @ModelAttribute(ModelMappings.APPLICAZIONI)
    public List<Applicazione> applicazioni() {
        return applicazioneService.getAllApplicazioni();
    }

}
