<html>
<head>
    <title>RIEPILOGO DATI TRANSAZIONE</title>
</head>
<body style="background-color:#ffffff;color:#000000;">
<div>
    <table style="border: 0; border-collapse: collapse; width:95%; margin: 35px;">

        <tr>
            <td colspan="3" style="text-align: center;font-weight:bold;font-size:25px;">DATI TRIBUTO</td>
        </tr>

        <tr>
            <td colspan="3">
                ID TIPOLOGIA TRIBUTO: <b>${pagamento.rata.tributo.tipologiaTributo.nome}</b>
                <br/>
                <br/>
                <br/>
                RATA TRIBUTO: <b>${pagamento.rata.nome}</b>
                <br/>
                <br/>
                <br/>
                IMPORTO: <b>${pagamento.importo}</b>
                <br/>
                <br/>
                <br/>
                COMUNE: <b>${pagamento.comune}</b>
                <br/>
                <br/>
                <br/>
                ATTO DI ACCERTAMENTO: <b>${pagamento.attoAccertamento}</b>
                <br/>
                <br/>
                <br/>
            </td>
        </tr>

        <tr>
            <td colspan="3" style="text-align: center;font-weight:bold;font-size:25px;">DATI BENEFICIARIO</td>
        </tr>

        <tr>
            <td colspan="3">
                NOME BENEFICIARIO: <b>${pagamento.beneficiario.nome}</b>
                <br/>
                <br/>
                <br/>
                COGNOME BENEFICIARIO: <b>${pagamento.beneficiario.cognome}</b>
                <br/>
                <br/>
                <br/>
                CODICE FISCALE BENEFICIARIO: <b>${pagamento.beneficiario.codFiscale}</b>
                <br/>
                <br/>
                <br/>
                E-MAIL BENEFICIARIO: <b>${pagamento.beneficiario.email}</b>
                <br/>
                <br/>
                <br/>
                INDIRIZZO BENEFICIARIO: <b>${pagamento.beneficiario.indirizzo}</b>
                <br/>
                <br/>
                <br/>
                PROVINCIA BENEFICIARIO: <b>${pagamento.beneficiario.provincia}</b>
                <br/>
                <br/>
                <br/>
            </td>
        </tr>

        <tr>
            <td colspan="3" style="text-align: center;font-weight:bold;font-size:25px;">DATI ESECUTORE</td>
        </tr>

        <tr>
            <td colspan="3">
                NOME ESECUTORE: <b>${pagamento.user.firstname}</b>
                <br/>
                <br/>
                <br/>
                COGNOME ESECUTORE: <b>${pagamento.user.lastname}</b>
                <br/>
                <br/>
                <br/>
                E-MAIL ESECUTORE: <b>${pagamento.user.email}</b>
                <br/>
                <br/>
                <br/>
            </td>
        </tr>
        <tr>
            <td colspan="3" style="text-align: center;font-weight:bold;font-size:25px;">DATI CARTA DI CREDITO</td>
        </tr>

    </table>
</div>

</body>
</html>