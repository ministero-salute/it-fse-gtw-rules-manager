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
 * 
 * @author vincenzoingenito
 *
 * Controller test.
 */
@RequestMapping(path = "/v1")
@Tag(name = "Servizio di test")
public interface ITestCTL extends Serializable {
	
	@PostMapping("/add_schema_schematron_version")
    @Operation(summary = "Add schema e schematron version", description = "Servizio che consente di aggiungere gli schema e schematron alla base dati.")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = void.class)))
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Duplicazione avvenuta con successo", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = void.class)))})
    void addSchemaAndSchematronVersion(HttpServletRequest request);
	
	@PostMapping("/update_data_ultimo_aggiornamento")
    @Operation(summary = "Update data ultimo aggiornamento schema e schematron", description = "Update data ultimo aggiornamento schema e schematron.")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = void.class)))
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Update effettuato correttamente", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = void.class)))})
    void updateDataUltimoAggiornamento(HttpServletRequest request);
}
