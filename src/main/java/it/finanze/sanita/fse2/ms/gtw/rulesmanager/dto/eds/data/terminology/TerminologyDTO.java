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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

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
    private String resourceId;
    @JsonProperty
    private String versionId;

    @JsonProperty
    private ResourceMetaDTO meta;
    @JsonProperty
    private List<TerminologyItemDTO> items;

    @JsonProperty
    private PaginationLinks links;

    @Data
    public static class ResourceMetaDTO {
        private String oid;
        private String version;
        private String type;
        private Date released;
        private boolean whitelist;
    }

    @Data
    public static class TerminologyItemDTO {
        private String code;
        private String display;
    }

    @Data
    public static class PaginationLinks {
        private String next;
    }

    public String info() {
        return String.format("%s|%s/%s", meta.type.substring(0, 3), resourceId, versionId);
    }

}