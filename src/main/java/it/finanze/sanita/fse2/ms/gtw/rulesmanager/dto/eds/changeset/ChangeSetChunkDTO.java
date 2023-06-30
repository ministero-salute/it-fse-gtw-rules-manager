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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeSetChunkDTO {

    private String traceID;
    private String spanID;

    private Date lastUpdate;
    private Date timestamp;

    List<HistoryInsertDTO> insertions;
    List<HistoryDeleteDTO> deletions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HistoryInsertDTO {
        String id;
        String version;
        String type;

        @Override
        public String toString() {
            return String.format("%s|%s/%s", type.substring(0, 3), id, version);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(NON_NULL)
    public static class HistoryDeleteDTO {
        String id;
        String type;
        String omit;
        @Override
        public String toString() {
            return String.format("%s|%s/omit=%s", type.substring(0, 3), id, omit == null ? "none" : omit);
        }
    }

    public boolean isEmpty() {
        return insertions.isEmpty() && deletions.isEmpty();
    }
}
