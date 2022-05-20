package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IEDSClientSRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ISchemaSRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ISchematronSRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IVocabularySRV;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IXslTransformSRV;
import lombok.extern.slf4j.Slf4j;

/**
 *
 *	Service used to handle EDS client invocation data.
 */
@Slf4j
@Service
public class EDSClientSRV extends AbstractService implements IEDSClientSRV {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -5123980192395443300L;
 
	@Autowired
	private IEDSClient edsClient;

	@Autowired
	private ISchemaSRV schemaSRV;

	@Autowired
	private ISchematronSRV schematronSRV;

	@Autowired
	private IVocabularySRV vocabularySRV;

	@Autowired
	private IXslTransformSRV xslTransformSRV;

	@Override
	public Boolean saveEDSConfigurationItems() {

		ConfigItemDTO configurationItems;

		try {
			configurationItems = edsClient.getConfigurationItems(); 
		} catch (Exception e) {
			String error = "Error retrieving configuration items from EDS Client";
			log.error(error);
			throw new BusinessException(error, e);
		}

		if(configurationItems == null) {
			String error = "The configuration items object retrieved from EDS Client is null";
			log.error(error);
			return false;
		}

		try {
			schemaSRV.saveNewVersionSchema(configurationItems.getSchema()); 
			schematronSRV.saveNewVersionSchematron(configurationItems.getSchematron());
			xslTransformSRV.saveNewVersionXslTransform(configurationItems.getXslTransform());
			vocabularySRV.saveNewVocabularySystems(configurationItems.getVocabulary());
		} catch (Exception e) {
			String error = "Error saving on database the configuration items object received from EDS Client";
			log.error(error);
			throw new BusinessException(error);
		}

		return true;

	}

}
