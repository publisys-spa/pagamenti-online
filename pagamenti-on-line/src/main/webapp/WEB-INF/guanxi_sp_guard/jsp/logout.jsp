<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:import url="/resources/commons/head.jsp">
    <c:param name="title" value="Logout IMS"/>
</c:import>

<div id="content">
    <div style="width: 80%; padding: 20px; margin: 0 auto;text-align: center;font-size: 1.2em;">
        Logout dal Service Provider effettuato con successo
        <br/>
        <p class="message-logout-subtitle">Si consiglia di chiudere adesso tutte le finestre del browser per proteggere il proprio account o nel caso si voglia accedere con un'altro account.</p>
        
        <br/><br/>
        <a href="<c:url value="/portal/service" />">Portale dei Servizi</a>
        <br/><br/>
        <a href="<%= it.publisys.ibasho.guard.LogoutGuard.getIdpServiceLogout(request) %>" target="_blank">Effettua la logout dall'Idp</a>
        
    </div>
    <div class="clearer"></div>
</div>

<%
    it.publisys.ibasho.guard.LogoutGuard.logout(request);
    
    response.sendRedirect(it.publisys.ibasho.guard.LogoutGuard.getIdpServiceLogout(request));
%>

<c:import url="/resources/commons/footer.jsp"/>
