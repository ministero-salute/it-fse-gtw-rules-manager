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

import java.util.function.Function;

import org.bson.Document;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.ConverterETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.AbstractEntityHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.raw.Fixtures;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TransformETY;

@Component
public class EDSTransformHandler  extends AbstractEntityHandler<TransformETY> {
	
	public static final int EXPECTED_ENGINE_FILES = 5;
	public static final int EXPECTED_ENGINE_ROOTS = 1;

	@Override
    protected Function<TransformETY, TransformETY> asModifiedEntity() {
		throw new UnsupportedOperationException("asModifiedEntity() not implemented");
    }

    @Override
    protected Function<TransformETY, Document> toDocument() {
        return ConverterETY::fromTransformToDoc;
    }

    @Override
    protected Fixtures getFixtures() {
        return Fixtures.TRANSFORM;
    }

	@Override
	protected Function<Document, TransformETY> toEntity() {
		return ConverterETY::docToTransform;
	}
}
