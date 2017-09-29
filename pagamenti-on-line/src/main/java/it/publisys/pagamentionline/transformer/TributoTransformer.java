package it.publisys.pagamentionline.transformer;

import it.publisys.pagamentionline.domain.impl.Tributo;
import it.publisys.pagamentionline.dto.TributoDTO;
import org.springframework.stereotype.Component;

/**
 * @author Francesco A. Tabino
 */
@Component
public class TributoTransformer {

    public TributoDTO transform(Tributo entity) {
        TributoDTO dto = new TributoDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getNome());
        dto.setDescrizione(entity.getDescrizione());
        dto.setAllegati(entity.getAllegati());
        return dto;
    }

}
