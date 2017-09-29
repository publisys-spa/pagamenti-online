package it.publisys.pagamentionline.transformer;

import it.publisys.pagamentionline.domain.impl.Rata;
import it.publisys.pagamentionline.dto.RataDTO;
import org.springframework.stereotype.Component;

/**
 * @author Francesco A. Tabino
 */
@Component
public class RataTransformer {

    public RataDTO transform(Rata entity) {
        RataDTO dto = new RataDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getNome());
        dto.setCausale(entity.getCausale());
        dto.setDescrizione(entity.getDescrizione());
        dto.setNote(entity.getNote());
        return dto;
    }

}
