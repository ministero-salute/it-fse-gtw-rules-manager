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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base;

import static java.util.stream.StreamSupport.stream;

import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.IQueryEDS;

public final class EDSTestUtils {
    
    public static boolean compareDeeply(MongoCollection<Document> src, MongoCollection<Document> dest, IQueryEDS<?> query) {
        // Now compare deeply
        long count = src.countDocuments();
        Long comparisons = stream(src.find().spliterator(), false)
            .map(doc -> dest.countDocuments(query.getComparatorQuery(doc)))
            .reduce(Long::sum).orElse(0L);
        // Verify for each document we expect we found an equivalent one
        return comparisons == count;
    }

    public static boolean compareDeeply(List<Document> src, MongoCollection<Document> dest) {
        // Now compare deeply
        long count = src.size();
        Long comparisons = src.stream()
            // countDocuments takes a filter as input parameter that is already
            // provided by map, it is like dest.countDocuments(filter);
            .map(dest::countDocuments)
            .reduce(Long::sum).orElse(0L);
        // Verify for each document we expect we found an equivalent one
        return comparisons == count;
    }
}
