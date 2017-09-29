package it.publisys.pagamentionline.transformer;

import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.dto.EnteDTO;
import org.springframework.stereotype.Component;

/**
 * Created by acoviello on 21/10/2016.
 */
@Component
public class EnteTransformer {

    public EnteDTO transform(Ente entity) {
        EnteDTO dto = new EnteDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
}
