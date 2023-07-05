package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology.IntegrityDTO.Resources;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;

@Getter
public class IntegrityResultDTO {

    private final List<Resources> missing;
    private final List<Resources> mismatch;
    private ActionRes synced;

    public IntegrityResultDTO() {
        this.missing = new ArrayList<>();
        this.mismatch = new ArrayList<>();
        this.synced = KO;
    }

    public ActionRes isSynced() {
        return synced;
    }

    public void setSynced(ActionRes synced) {
        this.synced = synced;
    }

    public boolean noMissingResources() {
        return missing.isEmpty();
    }

    public boolean noSizeMismatchResources() {
        return mismatch.isEmpty();
    }
}
