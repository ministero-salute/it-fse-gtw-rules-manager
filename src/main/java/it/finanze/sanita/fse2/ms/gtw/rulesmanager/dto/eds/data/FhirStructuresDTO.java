package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model to save fhir documents
 */
@Data
@NoArgsConstructor
public class FhirStructuresDTO {

    @JsonProperty
    private String traceID;
    @JsonProperty
    private String spanID;
    @JsonProperty
    private List<FhirStructures> maps;
    @JsonProperty
    private List<Definition> definitions;
    @JsonProperty
    private List<Valueset> valuesets;
    @JsonProperty
    private String id;
    @JsonProperty
    private Date lastUpdateDate;
    @JsonProperty
    private String version;
    @JsonProperty
    private String templateIdRoot;
    @JsonProperty
    private String rootMap;
    @JsonProperty
    private Boolean deleted;

    @Data
    public static class Valueset {
        @JsonProperty
        private String filenameValueset;
        @JsonProperty
        private String nameValueset;
        @JsonProperty
        private String contentValueset;
    }

    @Data
    public static class Definition {
        @JsonProperty
        private String filenameDefinition;
        @JsonProperty
        private String nameDefinition;
        @JsonProperty
        private String contentDefinition;
    }

    @Data
    public static class FhirStructures {
        @JsonProperty
        private String filenameMap;
        @JsonProperty
        private String contentMap;
        @JsonProperty
        private String nameMap;
        
    }
}
