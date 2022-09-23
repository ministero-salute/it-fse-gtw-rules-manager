package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Model to save schema documents
 */
@Data
@NoArgsConstructor
public class ValuesetDTO {

    @JsonProperty
    private String traceID;
    @JsonProperty
    private String spanID;
    @JsonProperty
    private Valueset document;

    @Data
    public static class Valueset {
        @JsonProperty
        private String id;
        @JsonProperty
        private String filenameValueset;
        @JsonProperty
        private String nameValueset;
        @JsonProperty
        private String contentValueset;
        @JsonProperty
        private Date lastUpdateDate;
    }
}
