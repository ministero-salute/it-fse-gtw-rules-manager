/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
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
