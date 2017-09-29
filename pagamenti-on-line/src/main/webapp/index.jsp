<%@ page import="it.publisys.pagamentionline.ModelMappings" %>
<%
    String esito = request.getParameter(ModelMappings.ESITO);
    String idDominio = request.getParameter("idDominio");
    String idSession = request.getParameter("idSession");

    if(null != esito){
        String redirect = "app/pag/pay/storico/?esitoPsp=" + esito;
        if(idDominio != null){
            redirect += "&idDominio=" + idDominio;
        }
        if(idDominio != null){
            redirect += "&idSession=" + idSession;
        }
        response.sendRedirect(redirect);
    }else {
        response.sendRedirect("index");
    }

%>
