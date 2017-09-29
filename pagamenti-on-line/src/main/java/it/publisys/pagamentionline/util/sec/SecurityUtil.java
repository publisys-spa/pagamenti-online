package it.publisys.pagamentionline.util.sec;


import it.govpay.servizi.commons.*;
import it.govpay.servizi.v2_3.gpprt.GpChiediListaVersamentiResponse;
import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.domain.search.Filter;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.dto.DebitoDTO;
import it.publisys.pagamentionline.service.GovPayService;
import it.publisys.pagamentionline.transformer.VersamentoTransformer;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mcolucci
 */
public class SecurityUtil {

    protected static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean isAnonymous() {
        return getAuthentication() instanceof AnonymousAuthenticationToken;
    }

    public static boolean isAuthenticated() {
        try {
            return !isAnonymous() && getAuthentication().isAuthenticated();
        } catch (Exception e) {
            return false;
        }
    }

    public static User getPrincipal() {
        if (isAuthenticated()) {
            return (User) getAuthentication().getPrincipal();
        } else {
            return null;
        }
    }

    public static String getPrincipalName() {
        if (isAuthenticated()) {
            User _principal = (User) getAuthentication().getPrincipal();
            return _principal.getUsername();
        } else {
            return "guest";
        }
    }

    public static String md5PasswordEncoder(String val) {
        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        return encoder.encodePassword(val, null);
    }
    public static void listaDebiti(Authentication auth, ModelMap model,GovPayService govPayService,VersamentoTransformer versamentoTransformer) {
        if (auth != null && auth.isAuthenticated()) {
            User user = (User) auth.getPrincipal();
            Filter _filter = new Filter();
            model.addAttribute(ModelMappings.FILTER, _filter);
            List<DebitoDTO> debiti = new ArrayList<>();
            it.govpay.servizi.v2_3.PagamentiTelematiciGPPrt port = govPayService.getPagamentiTelematiciGPPrt();
            GpChiediListaVersamentiResponse gpCercaVersamentiResponse = port.gpChiediListaVersamenti(govPayService.gpCercaVersamentiRequest(user, StatoVersamento.NON_ESEGUITO, StatoVersamento.ANNULLATO));
            gpCercaVersamentiResponse.getVersamento().stream().forEach(v -> debiti.add(versamentoTransformer.transofrmToPagamento(v, user.getFiscalcode())));
            model.addAttribute(ModelMappings.DEBITI, debiti);
        }

    }

}
