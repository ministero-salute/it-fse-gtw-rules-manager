/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Model to save terminology documents
 */
@Data
@NoArgsConstructor
public class TerminologyDTO {

    @JsonProperty
    private String traceID;
    @JsonProperty
    private String spanID;
    @JsonProperty
    private Terminology document;

    @Data
    public static class Terminology {
        @JsonProperty
        private String id;
        @JsonProperty
        private String system;
        @JsonProperty
        private String version;
        @JsonProperty
        private String code;
        @JsonProperty
        private String description;
        @JsonProperty
        private Date releaseDate;
        @JsonProperty
        private Date lastUpdateDate;
        @JsonProperty
        private Boolean deleted;
    }
}
