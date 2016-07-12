package it.publisys.pagamentionline.controller.impl;

import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.Rata;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.service.RataService;
import it.publisys.pagamentionline.util.sec.AuthorityUtil;
import it.publisys.pagamentionline.util.sec.SecurityUtil;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RataController
    extends BaseController {

    private static final Logger _log = LoggerFactory.getLogger(RataController.class);

    @Autowired
    private RataService rataService;

    public void toList(Tributo tributo, ModelMap model) {
        List<Rata> _rate = rataService.getAllRata(tributo);
        model.addAttribute(ModelMappings.RATE, _rate);
    }

    @RequestMapping(value = RequestMappings.RATA,
        method = RequestMethod.GET)
    public String toView(
        @RequestParam(value = "tribid", required = false) Long tribid,
        ModelMap model) {
        return toView(null, tribid, null, model);
    }

    @RequestMapping(value = RequestMappings.RATA + "/{id}",
        method = RequestMethod.GET)
    public String toView(@PathVariable(value = "id") Long id,
                         @RequestParam(value = "tribid", required = false) Long tribid,
                         @RequestParam(value = "op", required = false) String op,
                         ModelMap model) {

        if (op != null) {
            if ("D".equalsIgnoreCase(op)) {
                return toDelete(id, model);
            }
        }

        Rata _rata;

        if (id != null) {
            _rata = rataService.getRata(id);
        } else {
            _rata = new Rata();
            _rata.setTributo(new Tributo(tribid));
        }

        model.addAttribute(ModelMappings.RATA, _rata);

        return ViewMappings.RATA;
    }

    @RequestMapping(value = RequestMappings.RATA,
        method = RequestMethod.POST)
    public String toSave(
        @ModelAttribute(value = ModelMappings.RATA) @Valid Rata rata,
        BindingResult result, ModelMap model) {

        if (result.hasErrors()) {
            model.addAttribute(ModelMappings.RATA, rata);
            return ViewMappings.RATA;
        }

        String _user = SecurityUtil.getPrincipal().getUsername();
        rata = rataService.save(rata, _user);
        model.addAttribute(ModelMappings.MESSAGE, "Salvataggio effettuato");
        model.addAttribute(ModelMappings.RATA, rata);
        return ViewMappings.RATA;
    }

    private String toDelete(Long id, ModelMap model) {
        if (AuthorityUtil.isAuthenticated() && AuthorityUtil.isAdminLogged()) {
            String _user = SecurityUtil.getPrincipal().getUsername();
            rataService.delete(id, _user);

            return ViewMappings.REDIRECT + RequestMappings.RATE;
        }
        return ViewMappings.HOME;
    }

}
