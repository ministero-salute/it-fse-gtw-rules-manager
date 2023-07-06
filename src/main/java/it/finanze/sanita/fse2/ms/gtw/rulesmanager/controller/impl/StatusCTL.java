package it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.IStatusCTL;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.DictionaryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ITerminologyRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.DictionaryETY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Profile.DEV;

@Profile(DEV)
@RestController
public class StatusCTL implements IStatusCTL {

    @Autowired
    private ITerminologyRepo repository;

    @Override
    public List<DictionaryDTO> dictionaries() throws EdsDbException {
        return DictionaryETY.toMap(repository.getDictionaries());
    }
}
