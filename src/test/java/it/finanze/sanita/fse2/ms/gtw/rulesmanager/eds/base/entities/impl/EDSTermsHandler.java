/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.AbstractEntityHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

@Component
public class EDSTermsHandler extends AbstractEntityHandler<TerminologyETY> {

    public static final Path VOCAB_SAMPLE_FILES = Paths.get(
        "src",
        "test",
        "resources",
        "Files",
        "vocabulary"
    );

    @Override
    protected TerminologyETY asInitEntity(Path path) {
        return TerminologyETY.fromPath(path, "system", "version", "code", "description");
    }

    @Override
    protected Function<TerminologyETY, TerminologyETY> asModifiedEntity() {
        throw new UnsupportedOperationException("asModifiedEntity() not implemented");
    }

    @Override
    protected Function<TerminologyETY, Document> asDocument() {
        return TerminologyETY::toDocument;
    }

    @Override
    protected Path getResourceDir() {
        return VOCAB_SAMPLE_FILES;
    }
}
