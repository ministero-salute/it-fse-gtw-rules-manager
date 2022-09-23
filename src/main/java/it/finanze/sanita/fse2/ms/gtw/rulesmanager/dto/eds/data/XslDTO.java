package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Model to save xsl documents
 */
@Data
@NoArgsConstructor
public class XslDTO {

    @JsonProperty
    private String traceID;
    @JsonProperty
    private String spanID;
    @JsonProperty
    private Xsl document;

    @Data
    public static class Xsl {
        @JsonProperty
        private String id;
        @JsonProperty
        private String templateIdRoot;
        @JsonProperty
        private String nameXslTransform;
        @JsonProperty
        private String contentXslTransform;
        @JsonProperty
        private String version;
        @JsonProperty
        private Date lastUpdateDate;
    }
}
