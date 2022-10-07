package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.chunks;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.TerminologyDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model to save terminology documents
 */
@Data
@NoArgsConstructor
public class TerminologyChunkInsDTO {
    @JsonProperty
    private String traceID;
    @JsonProperty
    private String spanID;
    @JsonProperty
    private List<TerminologyDTO.Terminology> documents;
}
