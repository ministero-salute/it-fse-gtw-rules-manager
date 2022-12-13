/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Model to save schematron documents
 */
@Data
@NoArgsConstructor
public class SchematronDTO {

    @JsonProperty
    private String traceID;
    @JsonProperty
    private String spanID;
    @JsonProperty
    private Schematron document;

    @Data
    public static class Schematron {
        @JsonProperty
        private String id;
        @JsonProperty
        private String name;
        @JsonProperty
        private String content;
        @JsonProperty
        private String version;
        @JsonProperty
        private String templateIdRoot;
        @JsonProperty
        private Date lastUpdateDate;
        @JsonProperty
        private Boolean deleted;
    }
}
