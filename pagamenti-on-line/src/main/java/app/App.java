package app;

/**
 * @author mcolucci
 */
public interface App {

    //
    String ERROR = "/error";
    String ERROR_404 = "/404";
    String ERROR_500 = "/500";
    String ACCESS_DENIED = "/denied";

    String ROOT_INDEX = "/";
    String INDEX = "/index";
    String HOME = "/app/home";
    String ADMIN_HOME = "/app/home/admin";
    String SPID= "/app/spid";

    String LOGIN_IMS = "/ims";
    String LOGIN = "/login";
    String LOGIN_FAILED = "/login/failed";
    String LOGOUT = "/logout";

    String PUBLIC = "/public";
    String APP = "/app";


    String INFO = PUBLIC + "/info";
    String CONTATTI = PUBLIC + "/contatti";
    String CONTATTI_AUTH = APP + "/contatti";
    String USER = APP + "/admin/user";


    String PAGAMENTI = APP + "/pag";

    String PAGAMENTI_TRIBUTI = PAGAMENTI + "/pay/trib";
    String PAGAMENTO_TRIBUTO = PAGAMENTI_TRIBUTI + "/v";
    String PAGAMENTO_TRIBUTO_STEP = PAGAMENTO_TRIBUTO + "/step";

    String PAGAMENTO_SPONTANEO = PAGAMENTI + "/pay/spontaneo";
    String PAGAMENTO_SPONTANEO_ELIMINA = PAGAMENTI + "/pay/spontaneo/elimina";

    String PAGAMENTO_DEBITORIO = PAGAMENTI + "/pay/debitorio";
    String PAGAMENTO_DEBITORIO_SEARCH = PAGAMENTI + "/pay/debitorio/search";
    String PAGAMENTO_DEBITORIO_DETAIL = PAGAMENTI + "/pay/debitorio/detail";
    String PAGAMENTO_DEBITORIO_PRINT = PAGAMENTI + "/pay/debitorio/print";
    String PAGAMENTO_DEBITORIO_ANNULLA = PAGAMENTI + "/pay/debitorio/annulla";
    String PAGAMENTO_DEBITORIO_ELIMINA = PAGAMENTI + "/pay/debitorio/elimina";

    String PAGAMENTO_INVIA_RPT = PAGAMENTI + "/pay/invia-rpt";
    String PAGAMENTO_STATO = PAGAMENTI + "/pay/stato";
    String PAGAMENTO_STORICO = PAGAMENTI + "/pay/storico";
    String PAGAMENTO_STORICO_SEARCH = PAGAMENTI + "/pay/storico/search";
    String PAGAMENTO_PRINT = PAGAMENTI + "/pay/print";
    String PAGAMENTO_RT = PAGAMENTI + "/pay/rt";
    String PAGAMENTO_VIEW_RT = PAGAMENTI + "/pay/view/rt";
    String PAGAMENTO_WISP_URL_RETURN = PAGAMENTI + "/pay/wisp";

    String TIPOLOGIE = PAGAMENTI + "/tip";
    String TIPOLOGIA = TIPOLOGIE + "/v";

    String TRIBUTI = PAGAMENTI + "/trib";
    String TRIBUTO = TRIBUTI + "/v";
    String SEARCH_TRIBUTI = PAGAMENTI + "/searchTributi";

    String APPLICAZIONI = PAGAMENTI + "/applicazioni";
    String APPLICAZIONE = APPLICAZIONI + "/v";
    String SEARCH_APPLICAZIONI = PAGAMENTI + "/searchApplicazioni";

    String RATE = TRIBUTI + "/rata";
    String RATA = RATE + "/v";

    String ENTI = PAGAMENTI + "/enti";
    String ENTE = ENTI + "/v";

    String PROVIDERS = PAGAMENTI + "/providers";
    String PROVIDER = PROVIDERS + "/v";

    String USERS = PAGAMENTI + "/users";

    String RUOLO = USERS + "/ruolo";

    String SEARCH_USERS = PAGAMENTI + "/searchUsers";

    String SEARCH = PAGAMENTI + "/search";
    String SEARCH_FLUSSI = PAGAMENTI + "/searchFlussi";
    String SEARCH_DEBITORIO = PAGAMENTI + "/searchDebitorio";
    String PAGAMENTO_SEARCH_PRINT = PAGAMENTI + "/search/print";
    String PAGAMENTO_DEBITORIO_SEARCH_PRINT = SEARCH_DEBITORIO + "/print";

}
