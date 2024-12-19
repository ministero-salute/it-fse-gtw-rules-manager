
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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface IExecutorRepo {
    void rename(MongoCollection<Document> src, String target) throws EdsDbException;
    void rename(String src, String target) throws EdsDbException;
    boolean exists(String name) throws EdsDbException;
    void drop(String name) throws EdsDbException;
    MongoCollection<Document> create(String name) throws EdsDbException;
    MongoCollection<Document> clone(String source, String dest) throws EdsDbException;
    Date getLastSync(String name) throws EdsDbException;
    void sync(String name, Date sync) throws EdsDbException;
    long countActiveDocuments(MongoCollection<Document> src) throws EdsDbException;
    long countActiveDocuments(String src) throws EdsDbException;
    List<ObjectId> getActiveDocumentsId(String src) throws EdsDbException;
}
