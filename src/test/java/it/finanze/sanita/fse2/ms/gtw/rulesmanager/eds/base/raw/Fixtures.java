
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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.raw;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public enum Fixtures {
    BASE_PATH(Paths.get("src", "test", "resources", "fixtures")),
    SCHEMA(Paths.get(BASE_PATH.toString(), "schema.json")),
    TERMINOLOGY(Paths.get(BASE_PATH.toString(), "terminology.json")),
	TRANSFORM(Paths.get(BASE_PATH.toString(), "transform.json"));

    private final Path path;

    Fixtures(Path path) {
        this.path = path;
    }

    public File file() {
        return path.toFile();
    }

    @Override
    public String toString() {
        return path.toString();
    }

    public List<Document> asDocuments() throws IOException {
        List<Document> docs = new ArrayList<>();
        JsonNode root = new ObjectMapper().readTree(this.file());
        for (JsonNode node : root) {
            docs.add(Document.parse(node.toString()));
        }
        return docs;
    }
}
