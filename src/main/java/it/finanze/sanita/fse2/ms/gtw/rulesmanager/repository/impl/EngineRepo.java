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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.impl;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IEngineRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.engine.sub.EngineMap;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TransformETY.*;

@Repository
public class EngineRepo implements IEngineRepo {

    @Autowired
    private IExecutorRepo executor;

    @Autowired
    private MongoTemplate mongo;

    @Override
    public void insert(MongoCollection<Document> src, Document doc) throws EdsDbException {
        try {
            src.insertOne(doc);
        }catch (MongoException e) {
            throw new EdsDbException("Unable to insert document", e);
        }
    }

    @Override
    public List<EngineMap> getActiveMaps(String source) throws EdsDbException {
        List<EngineMap> maps = new ArrayList<>();
        // Verify we are working on an existing collection
        if(!executor.exists(source)) {
            throw new EdsDbException("The source collection does not exists: " + source);
        }
        // Get collection
        MongoCollection<Document> c = mongo.getCollection(source);
        FindIterable<Document> docs = c.find(
            and(ne(FIELD_DELETED, true), exists(FIELD_TEMPLATE_ID_ROOT))
        );
        for (Document doc : docs) {
            maps.add(
                new EngineMap(
                    doc.getObjectId(FIELD_ID),
                    doc.getList(FIELD_TEMPLATE_ID_ROOT, String.class),
                    doc.getString(FIELD_URI),
                    doc.getString(FIELD_VERSION)
                )
            );
        }
        return maps;
    }
}
