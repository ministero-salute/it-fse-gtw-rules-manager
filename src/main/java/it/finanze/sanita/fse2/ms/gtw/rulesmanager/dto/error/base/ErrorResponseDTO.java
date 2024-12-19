
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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.error.base;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.LogTraceInfoDTO;
import lombok.Data;
import org.apache.http.HttpStatus;

/**
 * Error response
 */
@Data
public class ErrorResponseDTO {

    /**
     * Trace id log.
     */
    @Schema(
        description = "Identificativo univoco della richiesta dell'utente"
    )
    private String traceID;

    /**
     * Span id log.
     */
    @Schema(
        description = "Identificativo univoco di un task della richiesta dell'utente (differisce dal traceID solo in caso di chiamate sincrone in cascata)"
    )
    private String spanID;

    /**
     * Issue identifier
     */
    @Schema(
        description = "Identificativo del problema verificatosi"
    )
    private String type;

    /**
     * Issue summary
     */
    @Schema(
        description = "Sintesi del problema (invariante per occorrenze diverse dello stesso problema)"
    )
    private String title;

    /**
     * Issue description
     */
    @Schema(
        description = "Descrizione del problema"
    )
    private String detail;

    /**
     * HTTP status
     */
    @Schema(
        description = "Stato http",
        minimum = HttpStatus.SC_CONTINUE + "",
        maximum = HttpStatus.SC_INSUFFICIENT_STORAGE + ""
    )
    private Integer status;

    /**
     * Additional information
     */
    @Schema(
        description = "URI che potrebbe fornire ulteriori informazioni riguardo l'occorrenza del problema"
    )
    private String instance;

    public ErrorResponseDTO(final LogTraceInfoDTO traceInfo, final String inType, final String inTitle, final String inDetail, final Integer inStatus, final String inInstance) {
        traceID = traceInfo.getTraceID();
        spanID = traceInfo.getSpanID();
        type = inType;
        title = inTitle;
        detail = inDetail;
        status = inStatus;
        instance = inInstance;
    }

}

