/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.AbstractEntityHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Component
public class EDSSchemaHandler extends AbstractEntityHandler<SchemaETY> {

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
        "standard"
    );

    @Override
    protected SchemaETY asInitEntity(Path path) throws IOException {
        return SchemaETY.fromPath(
            path,
            SCHEMA_TEST_EXTS,
            SCHEMA_TEST_ROOT.equals(path.getFileName().toString())
        );
    }

    @Override
    protected Function<SchemaETY, SchemaETY> asModifiedEntity() {
        AtomicInteger i = new AtomicInteger(0);
        return entity -> {
            // Rename
            entity.setNameSchema(entity.getNameSchema() +  " - " + i.getAndIncrement());
            // Return it
            return entity;
        };
    }

    @Override
    protected Function<SchemaETY, Document> asDocument() {
        return SchemaETY::toDocument;
    }

    @Override
    protected Path getResourceDir() {
        return SCHEMA_SAMPLE_FILES;
    }
}
