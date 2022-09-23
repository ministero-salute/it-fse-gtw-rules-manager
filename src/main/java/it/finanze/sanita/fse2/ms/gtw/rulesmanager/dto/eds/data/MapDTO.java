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
public class MapDTO {

    @JsonProperty
    private String traceID;
    @JsonProperty
    private String spanID;
    @JsonProperty
    private Map document;

    @Data
    public static class Map {
        @JsonProperty
        private String id;
        @JsonProperty
        private String filenameMap;
        @JsonProperty
        private String nameMap;
        @JsonProperty
        private String contentMap;
        @JsonProperty
        private String rootMap;
        @JsonProperty
        private String extensionMap;
        @JsonProperty
        private Date lastUpdateDate;
    }
}
