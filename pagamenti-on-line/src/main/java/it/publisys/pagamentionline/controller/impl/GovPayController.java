package it.publisys.pagamentionline.controller.impl;


import it.publisys.pagamentionline.domain.impl.Pagamento;
import it.publisys.pagamentionline.service.PagamentoService;
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
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;

/**
 * @author Francesco A. Tabino
 */
@Controller
public class GovPayController {

    private static final Logger _log = LoggerFactory.getLogger(GovPayController.class);

    @Autowired
    private PagamentoService pagamentoService;

//    @RequestMapping(value = "/public/pag/esito/service",
//            method = RequestMethod.POST)
//    @ResponseStatus(HttpStatus.OK)
//    public void esitoPost(HttpServletRequest request) throws JAXBException, SOAPException, IOException {
//
//        StringWriter writer = new StringWriter();
//        IOUtils.copy(request.getInputStream(), writer);
//        String theString = writer.toString();
//
//        String replace = theString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
//
//        SOAPMessage sm = MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(replace.getBytes()));
//        sm.writeTo(System.out);
//
//        SOAPBody body = sm.getSOAPBody();
//
//        JAXBContext jbc = JAXBContext.newInstance(PaInviaEsitoPagamento.class);
//        Unmarshaller um = jbc.createUnmarshaller();
//
//        PaInviaEsitoPagamento esitoPagamento = (PaInviaEsitoPagamento) um.unmarshal(body.getFirstChild());
//
//        if (esitoPagamento != null) {
//            PaInviaEsitoPagamento.Pagamento pagamento = esitoPagamento.getPagamento();
//            pagamento.getSingoloPagamento().stream().forEach((p)->aggiornaPagamento(esitoPagamento.getStato(), p));
//        }
//
//    }

//    private void aggiornaPagamento(StatoPagamento sp, SingoloPagamento p) throws IllegalArgumentException {
//        _log.info("Ricevuto un esito per -> " + p.getCodVersamentoEnte());
//        Optional<Pagamento> optional = pagamentoService.findByPid(p.getCodVersamentoEnte());
//        Pagamento pagamento = optional.orElseThrow(() ->
//                new IllegalArgumentException("Pagamento non presente -> " + p.getCodVersamentoEnte())
//        );
//        _log.info("ESITO ok per -> " + p.getCodVersamentoEnte());
//        pagamento.setStatoPagamento(sp.value());
//        pagamento.setIur(p.getIur());
//        pagamentoService.save(pagamento, "govpay");
//    }


    @RequestMapping(value = "/public/pag/verifica/service",
            method = RequestMethod.POST)
    public String verificaPost(HttpServletRequest request) {


        return "";
    }

}
