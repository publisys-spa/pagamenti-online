package it.publisys.pagamentionline.controller.search;

import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.ViewMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.domain.search.Filter;
import it.publisys.pagamentionline.domain.search.SearchResults;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.service.SearchService;
import it.publisys.pagamentionline.util.sec.AuthorityUtil;
import it.publisys.pagamentionline.util.sec.SecurityUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author mcolucci
 */
@Controller
public class SearchController
    extends BaseController {

    private static final Logger _log = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    @RequestMapping(value = RequestMappings.SEARCH,
        method = RequestMethod.GET)
    public String toView(ModelMap model) {

        Filter _filter = new Filter();
        model.addAttribute(ModelMappings.FILTER, _filter);

        return ViewMappings.SEARCH;
    }

    @RequestMapping(value = RequestMappings.SEARCH,
        method = RequestMethod.POST)
    public String toSearch(
        @ModelAttribute(value = ModelMappings.FILTER) Filter filter,
        BindingResult result, ModelMap model) {

        if (result.hasErrors()) {
            model.addAttribute(ModelMappings.FILTER, filter);
            return ViewMappings.SEARCH;
        }

        if(!AuthorityUtil.isAdminLogged()) {
            User _user = SecurityUtil.getPrincipal();
            filter.setCodiceFiscale(_user.getFiscalcode());
        }

        SearchResults _sresults = null;

        if (ModelMappings.SEARCH_TYPE_TRIB.equals(filter.getTipo())) {
            _sresults = new SearchResults<>(filter.getTipo(), searchService.searchPagamenti(filter));
        }

        model.addAttribute(ModelMappings.SEARCH_RESULTS, _sresults);

        model.addAttribute(ModelMappings.FILTER, filter);
        return ViewMappings.SEARCH;
    }

    @ModelAttribute(ModelMappings.TRIBUTI)
    public List<Tributo> tributi() {
        return searchService.getTributoService().getAllTributi();
    }

}
