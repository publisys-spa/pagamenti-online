package it.publisys.pagamentionline.controller.impl;

import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.Provider;
import it.publisys.pagamentionline.service.EnteService;
import it.publisys.pagamentionline.service.ProviderService;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

@Controller
public class ProviderController
        extends BaseController {

    private static final Logger _log = LoggerFactory.getLogger(ProviderController.class);

    @Autowired
    private ProviderService providerService;

    @Autowired
    private EnteService enteService;



    @RequestMapping(value = RequestMappings.PROVIDERS,
            method = RequestMethod.GET)
    public String toList(Model model, Pageable pageable) {
        if (AuthorityUtil.isAuthenticated() && AuthorityUtil.isAdminLogged()) {
            PageWrapper<Provider> _page = new PageWrapper<>(providerService.findAll(pageable),
                    RequestMappings.PROVIDERS);
            model.addAttribute(ModelMappings.PAGE, _page);
            model.addAttribute(ModelMappings.PROVIDERS, _page.getContent());
        }
        return ViewMappings.PROVIDERS;
    }

    @RequestMapping(value = RequestMappings.PROVIDER,
            method = RequestMethod.GET)
    public String toView(ModelMap model) {
        return toView(null, null, model);
    }

    @RequestMapping(value = RequestMappings.PROVIDER + "/{id}",
            method = RequestMethod.GET)
    public String toView(@PathVariable(value = "id") Long id,
                         @RequestParam(value = "op", required = false) String op,
                         ModelMap model) {
        if (AuthorityUtil.isAuthenticated() && AuthorityUtil.isAdminLogged()) {
            if (op != null) {
                if ("D".equalsIgnoreCase(op)) {
                    return toDelete(id);
                }
            }

            Provider _provider;

            if (id != null && providerService.exists(id)) {
                _provider = providerService.getOne(id);
            } else {
                _provider = new Provider();
            }

            model.addAttribute(ModelMappings.ENTI, enteService.findAll());
            model.addAttribute(ModelMappings.PROVIDER, _provider);
        }
        return ViewMappings.PROVIDER;
    }

    @RequestMapping(value = RequestMappings.PROVIDER,
            method = RequestMethod.POST)
    public String toSave(
            @ModelAttribute(value = ModelMappings.PROVIDER) @Valid Provider provider,
            BindingResult result, ModelMap model) {

        List<Ente> entes = enteService.findAll();

        if (result.hasErrors()) {
            model.addAttribute(ModelMappings.PROVIDER, provider);
            model.addAttribute(ModelMappings.ENTI, entes);
            return ViewMappings.PROVIDER;
        }

        Properties prop = new Properties();
        try {
            prop.load(new ByteArrayInputStream(provider.getProperties().getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            model.addAttribute(ModelMappings.PROVIDER, provider);
            model.addAttribute(ModelMappings.ENTI, entes);
            model.addAttribute(ModelMappings.MESSAGE, "Errore nella lettura delle properties");
            return ViewMappings.PROVIDER;
        }

        String _user = SecurityUtil.getPrincipal().getUsername();
        provider = providerService.save(provider, _user);
        model.addAttribute(ModelMappings.MESSAGE, "Salvataggio effettuato");
        model.addAttribute(ModelMappings.PROVIDER, provider);
        model.addAttribute(ModelMappings.ENTI, entes);
        return ViewMappings.PROVIDER;
    }

    private String toDelete(Long id) {

        if (AuthorityUtil.isAuthenticated() && AuthorityUtil.isAdminLogged()) {
            String _user = SecurityUtil.getPrincipal().getUsername();
            providerService.delete(id, _user);

            return ViewMappings.REDIRECT + RequestMappings.PROVIDERS;
        }
        return ViewMappings.HOME;
    }
}
