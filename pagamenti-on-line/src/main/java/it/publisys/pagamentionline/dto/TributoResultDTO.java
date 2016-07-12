package it.publisys.pagamentionline.dto;

import java.util.List;

/**
 * @author Francesco A. Tabino
 */
public class TributoResultDTO {

    private TributoDTO dto;
    private List<RataDTO> rataDTOList;

    public TributoDTO getDto() {
        return dto;
    }

    public void setDto(TributoDTO dto) {
        this.dto = dto;
    }

    public List<RataDTO> getRataDTOList() {
        return rataDTOList;
    }

    public void setRataDTOList(List<RataDTO> rataDTOList) {
        this.rataDTOList = rataDTOList;
    }
}
