package it.publisys.pagamentionline.controller.impl;


import it.gov.digitpa.schemas._2011.pagamenti.CtRicevutaTelematica;
import it.gov.digitpa.schemas._2011.pagamenti.CtRichiestaPagamentoTelematico;
import it.govpay.servizi.commons.Pagamento;
import it.govpay.servizi.commons.StatoVersamento;
import it.govpay.servizi.commons.Transazione;
import it.govpay.servizi.pa.PaNotificaTransazione;
import it.publisys.pagamentionline.ModelMappings;
import it.publisys.pagamentionline.PagamentiOnlineKey;
import it.publisys.pagamentionline.domain.enumeration.MailStatusEnum;
import it.publisys.pagamentionline.domain.enumeration.MailTypeEnum;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.domain.user.Mail;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.service.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Francesco A. Tabino
 */
@Controller
public class GovPayController {


    private static final Logger _log = LoggerFactory.getLogger(GovPayController.class);


    @Autowired
    private EnteService enteService;
    @Autowired
    private UserService userService;
    @Autowired
    private TributoService tributoService;
    @Autowired
    private PagamentoService pagamentoService;


    @RequestMapping(value = "/public/pag/esito/service",
            method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseStatus(HttpStatus.OK)
    public void esitoPost(HttpServletRequest request) throws JAXBException, SOAPException, IOException {

        StringWriter writer = new StringWriter();
        IOUtils.copy(request.getInputStream(), writer);

        String theString = writer.toString();
        String replace = theString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");

        SOAPMessage sm = MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(replace.getBytes()));
        sm.writeTo(System.out);

        SOAPBody body = sm.getSOAPBody();
        JAXBContext jbc = JAXBContext.newInstance(PaNotificaTransazione.class);
        Unmarshaller um = jbc.createUnmarshaller();
        JAXBElement<PaNotificaTransazione> unmarshal = um.unmarshal(body.getFirstChild(), PaNotificaTransazione.class);
        PaNotificaTransazione notificaTransazione = unmarshal.getValue();

        if (notificaTransazione != null) {
            List<Pagamento> pagamenti = notificaTransazione.getTransazione().getPagamento();
            pagamenti.stream().forEach((p) -> aggiornaPagamento(notificaTransazione.getCodVersamentoEnte(), p,
                    notificaTransazione.getTransazione()));
        }

    }

    private void aggiornaPagamento(String codVersamentoEnte, Pagamento p, Transazione transazione) throws IllegalArgumentException {
        _log.info("Ricevuto un esito per -> " + codVersamentoEnte);
        Optional<it.publisys.pagamentionline.domain.impl.Pagamento> optional = pagamentoService.findByCodVersamentoEnte(codVersamentoEnte);
        it.publisys.pagamentionline.domain.impl.Pagamento pagamento = new it.publisys.pagamentionline.domain.impl.Pagamento();
        try {
            pagamento = optional.orElseGet(() -> inserisciPagamento(p, transazione)
            );
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        _log.info("Esito ok per -> " + codVersamentoEnte + "transazione.getEsito().value()-> " + transazione.getEsito().value());
        if (transazione.getEsito().value().equals("PAGAMENTO_ESEGUITO")) {
            pagamento.setStatoPagamento(StatoVersamento.ESEGUITO.value());
        }
        if (transazione.getEsito().value().equals("PAGAMENTO_NON_ESEGUITO")) {
            pagamento.setStatoPagamento(StatoVersamento.NON_ESEGUITO.value());
        }
        _log.info("IUR " + p.getIur() + " CODPSP: " + transazione.getCanale().getCodPsp());
        pagamento.setIur(p.getIur());
        pagamento.setCodPsp(transazione.getCanale().getCodPsp());
        pagamentoService.save(pagamento, "govpay");

    }


    private it.publisys.pagamentionline.domain.impl.Pagamento inserisciPagamento(Pagamento p, Transazione transazione) {
        String str = new String(transazione.getRpt(), StandardCharsets.UTF_8);
        SOAPMessage smrpt = null;
        CtRichiestaPagamentoTelematico rpt = null;
        SOAPBody bodyrpt = null;
        try {
            smrpt = MessageFactory.newInstance().createMessage(null,
                    new ByteArrayInputStream(("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>" + str + "</soap:Body></soap:Envelope>").getBytes()));
            bodyrpt = smrpt.getSOAPBody();
            JAXBContext jbc2 = JAXBContext.newInstance(CtRichiestaPagamentoTelematico.class);
            Unmarshaller um2 = jbc2.createUnmarshaller();
            JAXBElement<CtRichiestaPagamentoTelematico> unmarshal2 = um2.unmarshal(bodyrpt.getFirstChild(), CtRichiestaPagamentoTelematico.class);
            rpt = unmarshal2.getValue();
        } catch (SOAPException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        it.publisys.pagamentionline.domain.impl.Pagamento pagamento = new it.publisys.pagamentionline.domain.impl.Pagamento();
        if (null != rpt) {
            User user = userService.getUserByFiscalcode(rpt.getSoggettoPagatore().getIdentificativoUnivocoPagatore().getCodiceIdentificativoUnivoco());
            pagamento.setEsecutore(user);
        }
        if (null != rpt.getDatiVersamento().getDatiSingoloVersamento().get(0).getDatiSpecificiRiscossione()) {
            Tributo tributo = tributoService.findByCodice(rpt.getDatiVersamento().getDatiSingoloVersamento().get(0).getDatiSpecificiRiscossione().split("\\/")[1]);
            pagamento.setTributo(tributo);
        }
        if (null != rpt.getDatiVersamento().getDatiSingoloVersamento().get(0).getCausaleVersamento()) {
            pagamento.setCausale(rpt.getDatiVersamento().getDatiSingoloVersamento().get(0).getCausaleVersamento());
        } else {
            pagamento.setCausale("-");
        }
        pagamento.setCcp(transazione.getCcp());
        pagamento.setImporto(Double.parseDouble(p.getImportoPagato().toString()));
        pagamento.setIuv(transazione.getIuv());
        if (null != transazione.getCodDominio()) {
            pagamento.setEnte(enteService.findByCodDominio(transazione.getCodDominio()));
        } else {
            //TODO: Impostare un ente di default
            pagamento.setEnte(enteService.getOne(new Long(2)));
        }
        return pagamento;
    }


    @RequestMapping(value = "/public/pag/verifica/service",
            method = RequestMethod.POST)
    public String verificaPost() {
        return "";
    }

}
