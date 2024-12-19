
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
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Component
public class EDSSchemaHandler extends AbstractEntityHandler<SchemaETY> {

    public static final String SCHEMA_TEST_EXTS = "POCD_MT000040UV02";

    /**
     * Sample parameter for multiple tests
     */
    public static final String SCHEMA_TEST_ROOT = "CDA.xsd";

    public static final int SCHEMA_TEST_SIZE = 10;

    @Override
    protected Function<SchemaETY, SchemaETY> asModifiedEntity() {
        AtomicInteger i = new AtomicInteger(0);
        return entity -> {
            // Rename
            entity.setNameSchema(entity.getNameSchema() +  " - " + i.getAndIncrement());
            // Return it
            return entity;
        };
    }

    @Override
    protected Function<SchemaETY, Document> toDocument() {
        return ConverterETY::fromSchemaToDoc;
    }

    @Override
    protected Fixtures getFixtures() {
        return Fixtures.SCHEMA;
    }

	@Override
	protected Function<Document, SchemaETY> toEntity() {
		return ConverterETY::docToSchema;
	}
}
