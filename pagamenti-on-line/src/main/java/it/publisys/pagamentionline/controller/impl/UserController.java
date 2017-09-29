package it.publisys.pagamentionline.controller.impl;

import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.*;
import it.publisys.pagamentionline.domain.search.Filter;
import it.publisys.pagamentionline.domain.search.SearchResults;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.domain.user.UserRole;
import it.publisys.pagamentionline.service.UserService;
import it.publisys.pagamentionline.util.sec.AuthorityUtil;
import it.publisys.pagamentionline.util.sec.SecurityUtil;
import it.publisys.spring.util.view.PageWrapper;
import org.apache.commons.lang.StringUtils;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Controller
public class UserController
        extends BaseController {

    private static final Logger _log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;


    @RequestMapping(value = RequestMappings.USER + "/{id}",
            method = RequestMethod.GET)
    public String toView(@PathVariable(value = "id") Long id, ModelMap model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            User _user;
            if (id != null) {
                _user = userService.getUser(id);
                List<UserRole> ruoli = userService.getRoles(_user);
                model.addAttribute(ModelMappings.RUOLI, ruoli);
            } else {
                _user = new User();
                model.addAttribute(ModelMappings.RUOLI, null);
            }
            model.addAttribute(ModelMappings.USER, _user);
        }
        return ViewMappings.USER;
    }

    @RequestMapping(value = RequestMappings.USER,
            method = RequestMethod.POST)
    public String toSave(
            @ModelAttribute(value = ModelMappings.USER) @Valid User _user,
            BindingResult result, ModelMap model) {

        if (result.hasErrors()) {
            model.addAttribute(ModelMappings.USER, _user);
            List<UserRole> ruoli = userService.getRoles(_user);
            model.addAttribute(ModelMappings.RUOLI, ruoli);
            return ViewMappings.USER;
        }

        model.addAttribute(ModelMappings.MESSAGE, "Salvataggio effettuato");

        model.addAttribute(ModelMappings.USERS, _user);
        return ViewMappings.USER;
    }

    @RequestMapping(value = RequestMappings.USER + "/{id}" + "/{idRuolo}",
            method = RequestMethod.GET)
    public String toView(@PathVariable(value = "id") Long id, @PathVariable(value = "idRuolo") Long idRuolo,
                         @RequestParam(value = "op", required = false) String op,
                         Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            if (op != null) {
                if ("D".equalsIgnoreCase(op)) {
                    return toDelete(id, idRuolo);
                }
            }
        }
        return ViewMappings.USER;
    }

    private String toDelete(Long id, Long idRuolo) {
        if (AuthorityUtil.isAuthenticated() && AuthorityUtil.isAdminLogged()) {
            String _user = SecurityUtil.getPrincipal().getUsername();
            userService.delete(id, _user, idRuolo);
            return ViewMappings.REDIRECT + RequestMappings.TRIBUTI;
        }
        return ViewMappings.USER;
    }

    @RequestMapping(value = RequestMappings.RUOLO + "/{userid}",
            method = RequestMethod.GET)
    public String toViewRole(@PathVariable(value = "userid") Long userid,
                             @RequestParam(value = "op", required = false) String op,
                             ModelMap model) {
        if (AuthorityUtil.isAuthenticated() && AuthorityUtil.isAdminLogged()) {
            User _user = userService.getUser(userid);
            UserRole role;
            role = new UserRole();
            role.setUser(_user);

            model.addAttribute(ModelMappings.RUOLO, role);
            model.addAttribute(ModelMappings.USER, _user);
        }
        return ViewMappings.RUOLO;
    }

    @RequestMapping(value = RequestMappings.RUOLO,
            method = RequestMethod.POST)
    public String toSaveRole(
            @ModelAttribute(value = ModelMappings.RUOLO) @Valid UserRole ruolo,
            BindingResult result, ModelMap model) {
        if (AuthorityUtil.isAuthenticated() && AuthorityUtil.isAdminLogged()) {
            if (result.hasErrors()) {
                model.addAttribute(ModelMappings.RUOLO, ruolo);
                return ViewMappings.RUOLO;
            }
            ruolo = userService.saveRole(ruolo);
            model.addAttribute(ModelMappings.MESSAGE, "Salvataggio effettuato");
            model.addAttribute(ModelMappings.RUOLO, ruolo);
        }
        return ViewMappings.RUOLO;
    }


}
