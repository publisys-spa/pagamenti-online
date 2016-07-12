<html>
    <head>
        <title>RIEPILOGO DATI TRANSAZIONE</title>
    </head>
    <body style="background-color:#ffffff;color:#000000;">
        <div>
            <table style="border: 0; border-collapse: collapse; width:95%; margin: 35px;">
               
                <tr>
                    <td colspan="3"  style="text-align: center;font-weight:bold;font-size:25px;">DATI CONTRAVVENZIONE</td>
                </tr>
                
                <tr>
                    <td colspan="3">
                        TARGA: <b>${pagamentoContravvenzione.targa}</b>
                        <br/>
                        <br/>
                        <br/>
                        NUMERO VERBALE: <b>${pagamentoContravvenzione.numeroVerbale}</b>
                        <br/>
                        <br/>
                        <br/>
                        DATA INFRAZIONE: <b>${pagamentoContravvenzione.dataInfrazioneString}</b>
                        <br/>
                        <br/>
                        <br/>
                        IMPORTO: <b>${pagamentoContravvenzione.importo}</b>
                        <br/>
                        <br/>
                        <br/>
                      
                    </td>
                </tr>
                
               <tr>
                    <td colspan="3"  style="text-align: center;font-weight:bold;font-size:25px;">DATI CARTA DI CREDITO</td>
                </tr>
                <br/>
                <br/>
                <tr>
                    <td colspan="3">
                        TIPO DI CARTA: <b>${cartaCredito.tipo}</b>
                        <br/>
                        <br/>
                        <br/>
                        INTESTATARIO: <b>${cartaCredito.intestatario}</b>
                        <br/>
                        <br/>
                        <br/>
                        CODICE DI SICUREZZA: <b>${cartaCredito.codiceSicurezza}</b>
                        <br/>
                        <br/>
                        <br/>
                        
                      
                    </td>
                </tr>

            </table>
        </div>

    </body>
</html>