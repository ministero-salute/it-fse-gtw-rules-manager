/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.DictionaryETY;

public interface IDictionaryRepo {

	void renewCodeSystemVersions(List<DictionaryETY> codeSystemVersions);
	
}
