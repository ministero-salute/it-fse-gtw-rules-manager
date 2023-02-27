/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ITerminologyRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.DictionaryETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IDictionarySRV;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DictionarySRV implements IDictionarySRV {
	
	@Autowired
	private ITerminologyRepo repository;
	
	@Override
	public int syncCodeSystemVersions(String terminology, MongoCollection<Document> dictionary) throws EdsDbException {
		// Retrieve data
		List<Document> dictionaries = getDictionaries(terminology);
		// Execute insertion
		// Note: We need to check dictionaries.size() because insertMany() does not allow empty lists
		if(!dictionaries.isEmpty()) {
			try {
				dictionary.insertMany(dictionaries);
			} catch(MongoException ex) {
				throw new EdsDbException("Unable to sync code-system versions" , ex);
			}
		}
		return dictionaries.size();
	}

	private List<Document> getDictionaries(String repository) throws EdsDbException {
		return this.repository
				.getAllCodeSystemVersions(repository)
				.stream()
				.filter(Objects::nonNull)
				.map(DictionaryETY::fromMap)
				.collect(Collectors.toList());
	}
	
}
