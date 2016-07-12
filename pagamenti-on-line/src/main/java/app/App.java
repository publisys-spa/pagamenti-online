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

    String PAGAMENTO_INVIA_RPT = PAGAMENTI + "/pay/invia-rpt";
    String PAGAMENTO_STATO = PAGAMENTI + "/pay/stato";
    String PAGAMENTO_STORICO = PAGAMENTI + "/pay/storico";
    String PAGAMENTO_PRINT = PAGAMENTI + "/pay/print";
    String PAGAMENTO_WISP_URL_RETURN = PAGAMENTI + "/pay/wisp";

    String TIPOLOGIE = PAGAMENTI + "/tip";
    String TIPOLOGIA = TIPOLOGIE + "/v";

    String TRIBUTI = PAGAMENTI + "/trib";
    String TRIBUTO = TRIBUTI + "/v";

    String RATE = TRIBUTI + "/rata";
    String RATA = RATE + "/v";

    String ENTI = PAGAMENTI + "/enti";
    String ENTE = ENTI + "/v";

    String PROVIDERS = PAGAMENTI + "/providers";
    String PROVIDER = PROVIDERS + "/v";

    String SEARCH = PAGAMENTI + "/search";

}
