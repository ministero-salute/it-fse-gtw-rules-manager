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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity;

import org.bson.Document;

public interface IQueryEDS<T> {
    /**
     * Used by the executor to upsert the dto instance
     * @param dto The retrieved dto from {@code client.getDocument()}
     * @return The query to upsert the given dto
     */
    Document getUpsertQuery(T dto);
    /**
     * Used by the executor to find a given dto
     * @param id The retrieved id from {@code client.getDocument()}
     * @return The query to find a given dto
     */
    Document getFilterQuery(String id);
    /**
     * Used by the executor to find and delete a given dto
     * @param id The retrieved id from {@code client.getDocument()}
     * @return The query to find and delete a given dto
     */
    Document getDeleteQuery(String id);
    /**
     * Used mainly for testing purpose to deep compare documents among cloned collections
     * @param doc The document retrieved from the collection
     * @return The query to deeply compare a given document into another collection
     */
    Document getComparatorQuery(Document doc);
}
