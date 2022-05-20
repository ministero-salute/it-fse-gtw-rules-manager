package it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IMockSRV;
import lombok.extern.slf4j.Slf4j;

/**
 * Test implemention of EDS Client.
 * 
 * @author Simone Lungarella
 */
@Slf4j
@Component
@Profile(Constants.Profile.DEV)
public class EDSMockClient extends AbstractClient implements IEDSClient {

    /**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -1094030146435617088L;
	
	@Autowired
    private IMockSRV mockSRV;
 
    /**
     * Returns mocked data as configuration items for testing purpose.
     */
    @Override
    public ConfigItemDTO getConfigurationItems() {
        log.info("Calling Mocked EDS Client to retrieve configuration items...");
        return mockSRV.mockConfigurationItem();
    }
 

}
