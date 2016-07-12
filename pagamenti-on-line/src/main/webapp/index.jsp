<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="it.publisys.pagamentionline.util.bnl.IPGIntegrationUtil"%>
<%
    response.sendRedirect("index");
    
    /*
    Double _imp1 = 10.59;
    Double _imp2 = 1010.0;
    Double _imp3 = 211.24;
    Double _imp4 = 4651410.59;
    Double _imp5 = 0.01;
    
    NumberFormat _format = new DecimalFormat("0.00");
    
    out.write(_format.format(_imp1).replaceAll(",", "."));
    out.write("<br/>");
    out.write(_format.format(_imp2).replaceAll(",", "."));
    out.write("<br/>");
    out.write(_format.format(_imp3).replaceAll(",", "."));
    out.write("<br/>");
    out.write(_format.format(_imp4).replaceAll(",", "."));
    out.write("<br/>");
    out.write(_format.format(_imp5).replaceAll(",", "."));
    out.write("<br/>");
    */
%>

<%--

<%
    String storeId = "06840346_S";
    String ksig = "xHosiSb08fs8BQmt9Yhq3Ub99E8=";
    String charge = "0.01";
    String currency = "EUR";

    IPGIntegrationUtil util = new IPGIntegrationUtil(storeId, ksig, charge, currency);
%>

<!DOCTYPE HTML>
<html>
    <head>
        <title>Pagina di esempio di e-POSitivity</title>
    </head>
    <body>

        <h2>Prima transazione con e-POSitivity</h2>

        <form method="post" action="https://pftest.bnlpositivity.it/service/">
            <input type="hidden" name="txntype" value="PURCHASE"/>
            <input type="hidden" name="timezone" value="CET"/>
            <input type="hidden" name="txndatetime" value="<%= util.getFmtDate()%>"/>
            <input type="hidden" name="hash" value='<%=util.createHash()%>'/>
            <input type="hidden" name="storename" value="<%=storeId%>"/>
            <input type="hidden" name="mode" value="payonly"/>
            <input type="hidden" name="currency" value="<%=currency%>"/>
            <input type="hidden" name="language" value="IT"/>
            <input type="hidden" name="responseSuccessURL" value="http://demo.publisys.it/pagamenti-online/esitoOK.php"/>
            <input type="hidden" name="responseFailURL" value="http://demo.publisys.it/pagamenti-online/esitoKO.php"/>
            <input type="text" name="chargetotal" value="<%=charge%>"/>
            <input type="submit" value="Acquista"/>
        </form>

    </body>
</html>

--%>
