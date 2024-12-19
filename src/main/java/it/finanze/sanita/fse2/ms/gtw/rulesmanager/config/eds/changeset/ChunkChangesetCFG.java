
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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset;

import lombok.Getter;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Getter
public abstract class ChunkChangesetCFG extends ChangesetCFG {

    private final String chunkInsert;
    private final String chunkDelete;

    protected ChunkChangesetCFG(String status, String chunkInsert, String chunkDelete, String production) {
        super(status, null, production);
        this.chunkInsert = chunkInsert;
        this.chunkDelete = chunkDelete;
    }

    public URI getChunkIns(String id, int idx) throws URISyntaxException {
        return new URI(chunkInsert + new URIBuilder().setPathSegments(id, Integer.toString(idx)).build());
    }

    public URI getChunkDel(String id, int idx) throws URISyntaxException {
        return new URI(chunkDelete + new URIBuilder().setPathSegments(id, Integer.toString(idx)).build());
    }

}
