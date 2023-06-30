package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology.IntegrityDTO.Resources;

@Value
public class IntegrityResultDTO {

    List<Resources> missing;

    public IntegrityResultDTO() {
        this.missing = new ArrayList<>();
    }

    public boolean isSynced() {
        return missing.isEmpty();
    }
}
