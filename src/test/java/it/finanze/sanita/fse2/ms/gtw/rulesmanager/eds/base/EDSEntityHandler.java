/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;
import org.bson.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EDSEntityHandler {

    public static final String TEST_COLL_A = "eds-test-0";
    public static final String TEST_COLL_B = "eds-test-1";
    public static final String TEST_BASE_COLLECTION = "eds-test-2";

    public static final String SCHEMA_TEST_EXTS = "POCD_MT000040UV02";

    /**
     * Sample parameter for multiple tests
     */
    public static final String SCHEMA_TEST_ROOT = "CDA.xsd";

    public static final int SCHEMA_TEST_SIZE = 10;

    /**
     * Directory containing sample files to upload as test
     */
    public static final Path SCHEMA_SAMPLE_FILES = Paths.get(
        "src",
        "test",
        "resources",
        "Files",
        "schema",
        "standard");

    private final List<SchemaETY> entities;

    protected EDSEntityHandler() {
        this.entities = new ArrayList<>();
    }

    protected void initTestEntities() throws IOException {
        // List of all files inside the sample modified directory
        try (Stream<Path> files = Files.list(SCHEMA_SAMPLE_FILES)) {
            // Convert to list
            List<Path> samples = files.collect(Collectors.toList());
            // Add to each map and convert
            for (Path path : samples) {
                this.entities.add(
                    SchemaETY.fromPath(
                        path,
                        SCHEMA_TEST_EXTS,
                        SCHEMA_TEST_ROOT.equals(path.getFileName().toString())
                    )
                );
            }
        }
    }

    protected void clearTestEntities() {
        this.entities.clear();
    }

    protected List<SchemaETY> getEntities() {
        return new ArrayList<>(entities);
    }

    protected List<Document> getModifiedEntitiesAsDocuments() {
        AtomicInteger i = new AtomicInteger(0);
        return entities.stream().map(entity -> {
            // Rename
            entity.setNameSchema(entity.getNameSchema() +  " - " + i.getAndIncrement());
            // Return it
            return entity;
        }).map(SchemaETY::toDocument).collect(Collectors.toList());
    }

    protected List<Document> getEntitiesAsDocuments() {
        return new ArrayList<>(entities).stream().map(SchemaETY::toDocument).collect(Collectors.toList());
    }
}
