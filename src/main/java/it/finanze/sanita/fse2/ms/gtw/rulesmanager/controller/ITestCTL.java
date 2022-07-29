package it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller test.
 * 
 * @author vincenzoingenito
 */
@RequestMapping(path = "/v1")
@Tag(name = "Servizio di test")
public interface ITestCTL extends Serializable {
 
    @PostMapping("/update-data-ultimo-aggiornamento")
    @Operation(summary = "Update data ultimo aggiornamento schema e schematron", description = "Update data ultimo aggiornamento schema e schematron.")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = void.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update effettuato correttamente", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = void.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)) })
    void updateDataUltimoAggiornamento(HttpServletRequest request);
    
    
    @PostMapping("/run-scheduler")
    @Operation(summary = "Run scheduler", description = "Run scheduler.")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = void.class)))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update effettuato correttamente", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = void.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE)) })
    void runScheduler(HttpServletRequest request);
    
    
}
