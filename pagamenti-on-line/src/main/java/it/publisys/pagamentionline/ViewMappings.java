package it.publisys.pagamentionline;

public interface ViewMappings {

    String REDIRECT = "redirect:";
    String PAGE = "/page";

    String ERROR_GENERIC = PAGE + "/error/generic";
    String ERROR_404 = PAGE + "/error/404";
    String ERROR_500 = PAGE + "/error/500";
    String ACCESS_DENIED = PAGE + "/error/denied";

    String INDEX = PAGE + "/index";
    String PUBLIC = PAGE + "/public";
    String CONTATTI = PAGE + "/public/contatti";
    String INFO = PAGE + "/public/info";

    String LOGIN_IMS = PAGE + "/auth/ims";
    String LOGIN = PAGE + "/auth/login";
    String LOGIN_FAILED = PAGE + "/auth/login";
    String LOGOUT = PAGE + "/auth/logout";

    String HOME = PAGE + "/home";

    String USERS = PAGE + "/users/list";
    String USER = PAGE + "/users/view";

    String RUOLO = PAGE + "/users/ruolo/view";

    String PAGAMENTI = PAGE + "/pay/trib/list";
    String PAGAMENTO = PAGE + "/pay/trib/view";
    String PAGAMENTO_PRINT = PAGE + "/pay/trib/print";

    String PAGAMENTO_SPONTANEO = PAGE + "/pay/spontaneo/view";
    String PAGAMENTO_SPONTANEO_DETAIL = PAGE + "/pay/spontaneo/detail";
    String PAGAMENTO_SPONTANEO_WISP = PAGE + "/pay/spontaneo/wisp";
    String PAGAMENTO_SPONTANEO_LIST = PAGE + "/pay/spontaneo/list";


    String PAGAMENTO_DEBITORIO = PAGE + "/pay/debito/view";
    String PAGAMENTO_DEBITORIO_DETAIL = PAGE + "/pay/debito/detail";

    String TIPOLOGIE = PAGE + "/tipologia/list";
    String TIPOLOGIA = PAGE + "/tipologia/view";

    String TRIBUTI = PAGE + "/tributo/list";
    String TRIBUTO = PAGE + "/tributo/view";

    String APPLICAZIONI = PAGE + "/applicazione/list";
    String APPLICAZIONE = PAGE + "/applicazione/view";

    String RATE = PAGE + "/tributo/rata/list";
    String RATA = PAGE + "/tributo/rata/view";

    String PROVIDERS = PAGE + "/provider/list";
    String PROVIDER = PAGE + "/provider/view";

    String ENTI = PAGE + "/ente/list";
    String ENTE = PAGE + "/ente/view";


    String SEARCH = PAGE + "/search";
    String SEARCH_DEBITORIO = PAGE + "/searchDebitorio";

     String SEARCH_FLUSSI = PAGE + "/flusso/list";
}
