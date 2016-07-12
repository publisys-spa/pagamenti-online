package it.publisys.pagamentionline.restcontroller;

import it.govpay.servizi.pa.*;
import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.domain.user.User;
import it.publisys.pagamentionline.dto.DebitoDTO;
import it.publisys.pagamentionline.dto.GenericDTO;
import it.publisys.pagamentionline.dto.RataDTO;
import it.publisys.pagamentionline.dto.TributoResultDTO;
import it.publisys.pagamentionline.service.*;
import it.publisys.pagamentionline.transformer.RataTransformer;
import it.publisys.pagamentionline.transformer.TributoTransformer;
import it.publisys.pagamentionline.transformer.VersamentoTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Francesco A. Tabino
 */
@RestController
public class PosizioneDebitoriaRestController extends BaseController {

    private static final Logger _log = LoggerFactory.getLogger(PagamentoSpontaneoRestController.class);

    @Autowired
    private EnteService enteService;

    @Autowired
    private GovPayService govPayService;

    @Autowired
    private VersamentoTransformer versamentoTransformer;


    @Autowired
    private TributoService tributoService;


    @Autowired
    private TributoTransformer tributoTransformer;


    @Autowired
    private PagamentoService pagamentoService;

    @RequestMapping(value = RequestMappings.PAGAMENTO_DEBITORIO + "/{ente}",
            method = RequestMethod.GET)
    public ResponseEntity<List<GenericDTO>> listTributi(Model model, @PathVariable Long ente) {

        List<GenericDTO> _list = new ArrayList<>();
        if (enteService.exists(ente)) {
            Ente entity = enteService.getOne(ente);
            tributoService.findAllByEnte(entity)
                    .stream()
                    .forEach(t -> {
                        GenericDTO dto = tributoTransformer.transform(t);
                        _list.add(dto);
                    });

        }
        return new ResponseEntity<>(_list, HttpStatus.OK);
    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_DEBITORIO + "/{ente}/{tributo}",
            method = RequestMethod.GET)
    public ResponseEntity<TributoResultDTO> listRata(@PathVariable Long ente, @PathVariable Long tributo) {

        TributoResultDTO _result = new TributoResultDTO();
        if (enteService.exists(ente)) {
            List<RataDTO> _list = new ArrayList<>();
            Tributo entity = tributoService.getTributo(tributo);
            _result.setDto(tributoTransformer.transform(entity));
           // rataService.getAllRata(entity).stream().forEach(t -> _list.add(rataTransformer.transform(t)));
          //  _result.setRataDTOList(_list);
        }
        return new ResponseEntity<>(_result, HttpStatus.OK);
    }
//    @RequestMapping(value = RequestMappings.PAGAMENTO_DEBITORIO + "/{ente}",
//            method = RequestMethod.GET)
//    public ResponseEntity<List<DebitoDTO>> listTributi(Model model, @PathVariable Long ente, Authentication auth) {
//
//        List<DebitoDTO> _list = new ArrayList<>();
//        if (enteService.exists(ente)) {
//            User user = (User) auth.getPrincipal();
//
//            Ente entity = enteService.getOne(ente);
//
//            try {
//                PagamentiTelematiciGPPrt port = govPayService.getPagamentiTelematiciGPPrt(entity);
//
//                GpCercaVersamentiResponse gpCercaVersamentiResponse = port.gpCercaVersamenti(govPayService.gpCercaVersamentiRequest(user, entity));
//
//                gpCercaVersamentiResponse.getVersamento()
//                        .stream()
//                        .filter(v -> !v.getStato().value().equals(StatoPagamento.PAGAMENTO_ESEGUITO.value()))
//                        .sorted(Comparator.comparing(v -> v.getPagamento().getDataScadenza()))
//                        .forEach(v -> _list.add(versamentoTransformer.transofrm(v)));
//            } catch (Exception ingored) {
//                _log.error(String.format("ENTE %s non cofigurato per govpay", entity.getName()));
//            }
//        }
//        return new ResponseEntity<>(_list, HttpStatus.OK);
//    }
//
//    @RequestMapping(value = RequestMappings.PAGAMENTO_DEBITORIO + "/{ente}/{iuv}",
//            method = RequestMethod.GET)
//    public ResponseEntity<DebitoDTO> statoDebitorio(@PathVariable Long ente, @PathVariable String iuv) {
//
//        DebitoDTO _result = new DebitoDTO();
//        if (enteService.exists(ente)) {
//
//            Ente entity = enteService.getOne(ente);
//
//            PagamentiTelematiciGPPrt port = govPayService.getPagamentiTelematiciGPPrt(entity);
//
//            GpChiediStatoPagamento statoPagamentoRequest = govPayService.chiediStato(entity, iuv);
//
//            GpChiediStatoPagamentoResponse statoPagamentoResponse = port.gpChiediStatoPagamento(statoPagamentoRequest);
//            if (statoPagamentoResponse.getVersamento() != null) {
//                _result = versamentoTransformer.transofrm(statoPagamentoResponse.getVersamento());
//            }
//
//        }
//        return new ResponseEntity<>(_result, HttpStatus.OK);
//    }

}
