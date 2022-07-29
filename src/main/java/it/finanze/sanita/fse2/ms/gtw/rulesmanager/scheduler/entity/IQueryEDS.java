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
