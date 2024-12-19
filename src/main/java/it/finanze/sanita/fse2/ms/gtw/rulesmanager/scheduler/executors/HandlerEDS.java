
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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors;

import static com.mongodb.client.model.Updates.set;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.OK;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionRetryEDS.retryOnException;
import static java.lang.String.format;

import java.util.Optional;

import org.slf4j.Logger;

import com.mongodb.MongoException;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base.IActionHandlerEDS;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.entity.IQueryEDS;

public final class HandlerEDS {

    private HandlerEDS() { }

    public static <K, V> IActionHandlerEDS<K> onInsertion (
        Logger log,
        Class<V> clazz,
        IEDSClient client,
        IQueryEDS<V> query,
        ChangesetCFG config
    ) {
        return (staging, info) -> {
            // Working var
            Optional<V> dto;
            // Retrieve data
            dto = retryOnException(() -> client.getDocument(config, info.getId(), clazz), config, log);
            // Verify
            ActionRes res = dto.isPresent() ? OK : KO;
            // Insert into db if request didn't fail
            if(res == OK) {
                try {
                    // Insert
                    staging.insertOne(query.getUpsertQuery(dto.get()));
                }catch (MongoException ex) {
                    log.error(
                        format("[EDS][%s] Unable to insert document", config.getTitle()),
                        ex
                    );
                    // Set flag
                    res = KO;
                }
            }else {
                log.error("[EDS][{}] Unable to retrieve document for insertion", config.getTitle());
            }
            // Bye, have a nice day
            return res;
        };
    }

    public static <K, V> IActionHandlerEDS<K> onDeletions(
        Logger log,
        IQueryEDS<V> query,
        ChangesetCFG config
    ) {
        return (staging, info) -> {
            // Working var
            ActionRes res = KO;
            try {
                // Logical Deletion 
                staging.findOneAndUpdate(query.getFilterQuery(info.getId()), set("deleted", true)); 
                // Set the flag
                res = OK;
            }catch (MongoException ex) {
                log.error(
                    format("[EDS][%s] Unable to delete document", config.getTitle()),
                    ex
                );
            }
            return res;
        };
    }
}
