/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
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
