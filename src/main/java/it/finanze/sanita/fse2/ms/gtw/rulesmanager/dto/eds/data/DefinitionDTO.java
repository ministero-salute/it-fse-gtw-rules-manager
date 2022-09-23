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
public class DefinitionDTO {

    @JsonProperty
    private String traceID;
    @JsonProperty
    private String spanID;
    @JsonProperty
    private Definition document;

    @Data
    public static class Definition {
        @JsonProperty
        private String id;
        @JsonProperty
        private String filenameDefinition;
        @JsonProperty
        private String nameDefinition;
        @JsonProperty
        private String contentDefinition;
        @JsonProperty
        private String versionDefinition;
        @JsonProperty
        private Date lastUpdateDate;
    }
}
