package it.publisys.pagamentionline.controller.common;

import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.domain.enumeration.MailTypeEnum;
import it.publisys.pagamentionline.domain.user.Mail;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.service.MailService;
import it.publisys.pagamentionline.util.sec.SecurityUtil;
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
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author mcolucci
 */
@Controller
@SessionAttributes(value = ModelMappings.MAIL,
        types = Mail.class)
public class PublicController {

    private static final Logger _log = LoggerFactory.getLogger(PublicController.class);

    @Autowired
    private MailService mailService;

    @RequestMapping(value = RequestMappings.PUBLIC + "/{page}",
            method = RequestMethod.GET)
    public String go(@PathVariable(value = "page") String page,
            ModelMap model) {

        if ("contatti".equalsIgnoreCase(page)) {

            if (SecurityUtil.isAuthenticated()) {
                User _user = SecurityUtil.getPrincipal();
                model.addAttribute(ModelMappings.MAIL,
                        new Mail(String.format("%s %s", _user.getFirstname(), _user.getLastname()),
                                _user.getFiscalcode(),
                                _user.getUsername(), _user.getEmail()));
            } else {
                model.addAttribute(ModelMappings.MAIL, new Mail("anonymous"));
            }

        }

        return ViewMappings.PUBLIC + "/" + page;
    }

    @RequestMapping(value = RequestMappings.CONTATTI_AUTH,
            method = RequestMethod.GET)
    public String contatti(ModelMap model) {

        if (SecurityUtil.isAuthenticated()) {
            User _user = SecurityUtil.getPrincipal();
            model.addAttribute(ModelMappings.MAIL,
                    new Mail(String.format("%s %s", _user.getFirstname(), _user.getLastname()),
                            _user.getFiscalcode(),
                            _user.getUsername(), _user.getEmail()));
        } else {
            model.addAttribute(ModelMappings.MAIL, new Mail("anonymous"));
        }

        return ViewMappings.CONTATTI;
    }

    @RequestMapping(value = RequestMappings.CONTATTI_AUTH,
            method = RequestMethod.POST)
    public String inviaMailSupporto(
            @ModelAttribute(value = ModelMappings.MAIL) @Valid Mail mail,
            BindingResult result, ModelMap model) {

        if (result.hasErrors()) {
            model.addAttribute(ModelMappings.MAIL, mail);
            return ViewMappings.CONTATTI;
        }

        try {
            mail.setType(MailTypeEnum.SUPPORT);
            mail.setText(_mailSupport(mail));
            mailService.create(mail);
            model.addAttribute(ModelMappings.MESSAGE, "Mail inoltrata correttamente");
        } catch (Exception e) {
            model.addAttribute(ModelMappings.ERROR, "Non e' stato possibile inoltrare la mail in questo momento.");
            _log.warn("Impossibile inserire la mail in coda.", e);
        }

        model.addAttribute(ModelMappings.MAIL,
                new Mail(mail.getName(), mail.getFiscalcode(), mail.getUsername(), mail.getEmail()));

        return ViewMappings.CONTATTI;
    }

    private String _mailSupport(Mail mail) {
        StringBuilder _sb = new StringBuilder();
        _sb.append("<b>Utente</b>: ").append(mail.getUsername()).append("<br/>");
        _sb.append("<b>Nominativo</b>: ").append(mail.getName()).append("<br/>");
        _sb.append("<b>Email</b>: ").append(mail.getEmail()).append("<br/>");
        _sb.append("<br/>");
        _sb.append("<b>Testo</b>: ").append(mail.getText()).append("<br/>");
        return _sb.toString();
    }
}
