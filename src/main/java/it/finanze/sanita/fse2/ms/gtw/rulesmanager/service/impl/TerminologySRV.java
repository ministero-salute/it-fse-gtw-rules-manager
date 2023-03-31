package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ITerminologyRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ITerminologySRV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TerminologySRV implements ITerminologySRV {

    @Autowired
    private ITerminologyRepo repository;

    @Override
    public void applyIndexes(String collection) throws EdsDbException {
        repository.applyIndexes(collection);
    }
}
