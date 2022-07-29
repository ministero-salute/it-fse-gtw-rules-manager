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
public class SchemaDTO {

    @JsonProperty
    private String traceID;
    @JsonProperty
    private String spanID;
    @JsonProperty
    private Payload data;

    @Data
    public static class Payload {
        @JsonProperty
        private Schema document;
    }

    @Data
    public static class Schema {
        @JsonProperty
        private String id;
        @JsonProperty
        private String nameSchema;
        @JsonProperty
        private String contentSchema;
        @JsonProperty
        private String typeIdExtension;
        @JsonProperty
        private Boolean rootSchema;
        @JsonProperty
        private Date lastUpdateDate;
    }
}
