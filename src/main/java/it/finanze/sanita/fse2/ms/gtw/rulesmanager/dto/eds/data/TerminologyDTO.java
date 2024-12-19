
/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright (C) 2023 Ministero della Salute
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
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
