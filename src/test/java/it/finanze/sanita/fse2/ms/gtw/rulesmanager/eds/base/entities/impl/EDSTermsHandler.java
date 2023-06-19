/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.ConverterETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.AbstractEntityHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.raw.Fixtures;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Component
public class EDSTermsHandler extends AbstractEntityHandler<TerminologyETY> {
	
	public static final int EXPECTED_DICTIONARIES = 2;
	public static final List<String> EXPECTED_SYSTEMS = Arrays.asList(
		"1.2.840.10008.2.16.4",
		"2.16.840.1.113883.6.73"
	);

    @Override
    protected Function<TerminologyETY, TerminologyETY> asModifiedEntity() {
        throw new UnsupportedOperationException("asModifiedEntity() not implemented");
    }

    @Override
    protected Function<TerminologyETY, Document> toDocument() {
        return ConverterETY::fromTermsToDoc;
    }

	@Override
	protected Fixtures getFixtures() {
		return Fixtures.TERMINOLOGY;
	}

	@Override
	protected Function<Document, TerminologyETY> toEntity() {
		return ConverterETY::docToTerms;
	}
}
