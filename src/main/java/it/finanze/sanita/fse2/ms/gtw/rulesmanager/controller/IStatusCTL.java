package it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.DictionaryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.RouteUtility.API_STATUS_DICTIONARY;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.RouteUtility.API_STATUS_MAPPER;

@Tag(name = "Data status")
@RequestMapping(API_STATUS_MAPPER)
public interface IStatusCTL {

    @GetMapping(
        value = API_STATUS_DICTIONARY,
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Operation(
        summary = "Return the current saved dictionaries",
        description = "Return the current dictionaries at the time of the request"
    )
    List<DictionaryDTO> dictionaries() throws EdsDbException;

}
