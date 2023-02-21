/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.tools.RunSchedulerDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.RouteUtility.API_RUN_SCHEDULER_FULL;

/**
 * Controller test.
 */
@Tag(name = "Servizio di test")
public interface ISchedulerCTL {

    @PostMapping(API_RUN_SCHEDULER_FULL)
    @Operation(summary = "Run cfg items scheduler", description = "Run cft items scheduler.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Richiesta update in coda", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RunSchedulerDTO.class))),
        @ApiResponse(responseCode = "423", description = "Richiesta update rigettata", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    })
    RunSchedulerDTO run();
    
}
