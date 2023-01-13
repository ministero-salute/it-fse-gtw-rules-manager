package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.tools;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.ResponseDTO;
import lombok.Getter;

@Getter
public class RunSchedulerDTO extends ResponseDTO {
    private final String message;

    public RunSchedulerDTO(LogTraceInfoDTO info, String message) {
        super(info);
        this.message = message;
    }

}
