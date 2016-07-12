package it.publisys.pagamentionline.restcontroller;

import it.publisys.pagamentionline.RequestMappings;
import it.publisys.pagamentionline.controller.BaseController;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.dto.GenericDTO;
import it.publisys.pagamentionline.dto.RataDTO;
import it.publisys.pagamentionline.dto.TributoResultDTO;
import it.publisys.pagamentionline.service.EnteService;
import it.publisys.pagamentionline.service.PagamentoService;
import it.publisys.pagamentionline.service.RataService;
import it.publisys.pagamentionline.service.TributoService;
import it.publisys.pagamentionline.transformer.RataTransformer;
import it.publisys.pagamentionline.transformer.TributoTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco A. Tabino
 */
@RestController
public class PagamentoSpontaneoRestController extends BaseController {

    private static final Logger _log = LoggerFactory.getLogger(PagamentoSpontaneoRestController.class);

    @Autowired
    private EnteService enteService;

    @Autowired
    private TributoService tributoService;

    @Autowired
    private RataService rataService;

    @Autowired
    private TributoTransformer tributoTransformer;

    @Autowired
    private RataTransformer rataTransformer;

    @Autowired
    private PagamentoService pagamentoService;

    @RequestMapping(value = RequestMappings.PAGAMENTO_SPONTANEO + "/{ente}",
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

    @RequestMapping(value = RequestMappings.PAGAMENTO_SPONTANEO + "/{ente}/{tributo}",
            method = RequestMethod.GET)
    public ResponseEntity<TributoResultDTO> listRata(@PathVariable Long ente, @PathVariable Long tributo) {

        TributoResultDTO _result = new TributoResultDTO();
        if (enteService.exists(ente)) {
            List<RataDTO> _list = new ArrayList<>();
            Tributo entity = tributoService.getTributo(tributo);
            _result.setDto(tributoTransformer.transform(entity));
            rataService.getAllRata(entity).stream().forEach(t -> _list.add(rataTransformer.transform(t)));
            _result.setRataDTOList(_list);
        }
        return new ResponseEntity<>(_result, HttpStatus.OK);
    }

    @RequestMapping(value = RequestMappings.PAGAMENTO_SPONTANEO + "/{ente}/{tributo}/{rata}",
            method = RequestMethod.GET)
    public ResponseEntity<RataDTO> rataDetail(@PathVariable Long ente, @PathVariable Long tributo, @PathVariable Long rata) {
        RataDTO dto = new RataDTO();
        if (enteService.exists(ente)) {
            dto = rataTransformer.transform(rataService.getRata(rata));
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    /*
    @RequestMapping(value = RequestMappings.PAGAMENTO_SPONTANEO,
            method = RequestMethod.POST)
    public ResponseEntity<WispDTO> create(@ModelAttribute(ModelMappings.PAGAMENTO) @Valid Pagamento pagamento,
                                          BindingResult result, ModelMap model, Authentication auth) throws URISyntaxException {
        WispDTO dto = new WispDTO();




        User _user = (User) auth.getPrincipal();
        pagamento.setEsecutore(_user);
        pagamento.setStatoPagamento("IN ATTESA DI VERIFICA");
        pagamento = pagamentoService.save(pagamento, _user.getUsername());

        Properties prop = pagamentoService.loadProperties(pagamento.getEnte());

        dto.setIdDominio(prop.getProperty("dominio"));
        dto.setEnteCreditore(pagamento.getEnte().getName());
        dto.setUrlBack("wisp url back");
        dto.setUrlReturn("wisp url return");

        dto.setKeyPA(pagamento.getPid());
        dto.setBolloDigitale("NO");
        dto.setPrimitiva("nodoInviaRPT");
        dto.setNumPagamentiRPT("1");
        dto.setStornoPagamento("SI");
        dto.setTerzoModelloPagamento("NO");

        HttpHeaders headers = new HttpHeaders();
        //headers.add("Location", "http://serviziclienti.link.it/govpay-ndp-sym/wisp.jsp");

        headers.setLocation(ServletUriComponentsBuilder
                .fromHttpUrl("http://serviziclienti.link.it/govpay-ndp-sym/wisp.jsp").build().toUri());


        headers.add("Content-Type", "application/x-www-form-urlencoded");

        RequestEntity<WispDTO> requestEntity = new RequestEntity<>(dto, headers, HttpMethod.POST, new URI("http://serviziclienti.link.it/govpay-ndp-sym/wisp.jsp"));

        //requestEntity.


        ResponseEntity<WispDTO> responseEntity = new ResponseEntity<>(dto, headers, HttpStatus.FOUND);


        return responseEntity;

    }*/


}
