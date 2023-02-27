/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.ConverterETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.AbstractEntityHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.raw.Fixtures;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class EDSTermsHandler extends AbstractEntityHandler<TerminologyETY> {

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
