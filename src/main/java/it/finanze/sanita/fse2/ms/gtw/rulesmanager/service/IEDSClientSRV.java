package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service;

import java.io.Serializable;

public interface IEDSClientSRV extends Serializable {

    /**
     * Save on database the configuration items retrieve from EDS Client invocation
     */
    public Boolean saveEDSConfigurationItems();
    
}
