package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service;

import java.io.Serializable;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.ConfigItemDTO;

public interface IMockSRV extends Serializable {

	ConfigItemDTO mockConfigurationItem();
}
