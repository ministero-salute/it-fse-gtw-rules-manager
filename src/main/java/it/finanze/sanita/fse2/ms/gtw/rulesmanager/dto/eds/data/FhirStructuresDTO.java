/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private Transform document;

    @Data
    public static class Transform {
        @JsonProperty
        private String id;
        @JsonProperty
        private String uri;
        @JsonProperty
        private String version;
        @JsonProperty
        private String templateIdRoot;
        @JsonProperty
        private String content;
        @JsonProperty
        private String filename;
        @JsonProperty
        private String type;
        @JsonProperty
        private Date lastUpdateDate;
        @JsonProperty
        private Boolean deleted;

    }
}
