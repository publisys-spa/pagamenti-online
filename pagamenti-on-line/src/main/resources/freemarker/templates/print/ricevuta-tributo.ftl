<html>
    <head>
        <title>RICEVUTA PAGAMENTO</title>
    </head>
    <body style="font-family: 'Arial';letter-spacing: 1px;">

    <center>

        <div style="margin: 0 auto; width: 600px; border: 1px solid #CCC; border-radius: 10px; padding: 20px;">
            <div style="text-align: left;">
                <div style="border-bottom: 1px solid #ccc; font-size: 15px;  margin: 0; padding-bottom: 5px; text-align: center;">
                    <center>
                    <img style="height: auto; width: 200px;" src="http://localhost:8181/pagamentionline/resources/img/logo_comune.png" alt="Stemma"/>
                    </center>
                    <h1 style="font-size: 15px; margin: 10px;">Comune di Demo <br/>Ricevuta di Pagamento Online</h1>
                </div>
                <div>
                    <h1 style="font-size: 16px; font-weight: normal; margin: 5px 0 0;">PAGAMENTO EFFETTUATO PER: <span style="font-weight: bold;">${pagamento.tributo.nome}</span></h1>
                </div>
                <div>
                    <h1 style="font-size: 16px; font-weight: normal; margin: 5px 0 0;">RATA: <span style="font-weight: bold;">${pagamento.rata.nome}</span></h1>
                </div>
                <div style="border-top: 1px solid #ccc; margin-top: 5px;">
                    <h1 style="font-size: 14px; font-weight: normal; margin: 5px 0 0;">EFFETTUATO SUL C/C n. <span style="font-weight: bold;">${pagamento.tributo.contoCorrente.numeroConto}</span></h1>
                </div>
                <div>
                    <h1 style="font-size: 14px; font-weight: normal; margin: 5px 0 0;">DI &euro; <span style="font-weight: bold;">${pagamento.importo}</span></h1>
                </div>
                <div>
                    <h1 style="font-size: 14px; font-weight: normal; margin: 5px 0 0;">INTESTATO A: <span style="font-weight: bold;">${pagamento.tributo.contoCorrente.intestato}</span></h1>
                </div>
                <div style="border-top: 1px solid #ccc;margin-top: 5px;">
                    <h1 style="font-size: 14px; font-weight: normal; margin: 5px 0 0;">ESEGUITO DA: <span style="font-weight: bold;">${pagamento.beneficiario.nome} ${pagamento.beneficiario.cognome} (${pagamento.beneficiario.codiceFiscale})</span></h1>
                </div>
                <div>
                    <h1 style="font-size: 14px; font-weight: normal; margin: 5px 0 0;">IN DATA: <span style="font-weight: bold;">${pagamento.dateProcessed}</span></h1>
                </div>
                <div>
                    <h1 style="font-size: 14px; font-weight: normal; margin: 5px 0 0;">N. OPERAZIONE: <span style="font-weight: bold;">${pagamento.iuv}</span></h1>
                </div>
                <div>
                    <h1 style="font-size: 14px; font-weight: normal; margin: 5px 0 0;">CAUSALE: <span style="font-weight: bold;">${pagamento.causale}</span></h1>
                </div>
            </div>
        </div>
    </center>
</body>
</html>