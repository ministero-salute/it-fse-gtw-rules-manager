/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.TerminologyMapDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;

import java.util.List;

public interface ITerminologyRepo {

	List<TerminologyMapDTO> getAllCodeSystemVersions(String collection) throws EdsDbException;
	
}
