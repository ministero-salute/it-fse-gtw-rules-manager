/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Collections;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ICounterRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ICounterSRV;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CounterSRV implements ICounterSRV {

	@Autowired
	private ICounterRepo counterRepo;
	
	@Override
	public Map<String, Integer> countCfgItems() {
		Map<String, Integer> out = new HashMap<>();
		try {
			for(Field f : Collections.class.getDeclaredFields()) {
				String collection = (String)f.get(f.getName());
				out.put(f.getName(), counterRepo.countCfgItems(collection));
			}
		} catch(Exception ex) {
			log.error("Error while perform count cfg items in srv" , ex);
			throw new BusinessException("Error while perform count cfg items in srv" , ex);
		}
		return out;
	}

	
}
