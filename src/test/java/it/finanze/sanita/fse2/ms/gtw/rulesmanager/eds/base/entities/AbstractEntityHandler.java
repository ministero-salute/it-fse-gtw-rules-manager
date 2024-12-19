
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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.raw.Fixtures;

@Component
public abstract class AbstractEntityHandler<T> {

    private final List<T> entities;
    private final List<Document> documents;

    public AbstractEntityHandler() {
        this.entities = new ArrayList<>();
        this.documents = new ArrayList<>();
    }

    public void initTestEntities() throws Exception {
        // Consistency check
        if(entities.isEmpty()) {
        	this.documents.addAll(getFixtures().asDocuments());
            this.entities.addAll(
            		documents.stream().map(toEntity()).collect(Collectors.toList())
            	);
        }
    }

    public void clearTestEntities() {
        this.entities.clear();
        this.documents.clear();
    }

    public List<T> getEntities() {
        return new ArrayList<>(entities);
    }

    public List<Document> getDocuments() {
        return new ArrayList<>(documents);
    }

    public List<Document> getModifiedEntitiesAsDocuments() {
        return entities.stream().map(asModifiedEntity()).map(toDocument()).collect(Collectors.toList());
    }

    protected abstract Function<T, T> asModifiedEntity();
    protected abstract Function<T, Document> toDocument();
    protected abstract Fixtures getFixtures();
    protected abstract Function<Document, T> toEntity();

}
