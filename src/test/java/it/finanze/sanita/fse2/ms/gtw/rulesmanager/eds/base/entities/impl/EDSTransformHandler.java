package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.impl;

import java.util.function.Function;

import org.bson.Document;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.ConverterETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities.AbstractEntityHandler;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.raw.Fixtures;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TransformETY;

@Component
public class EDSTransformHandler  extends AbstractEntityHandler<TransformETY> {
	
	public static final int EXPECTED_ENGINE_FILES = 5;
	public static final int EXPECTED_ENGINE_ROOTS = 1;

	@Override
    protected Function<TransformETY, TransformETY> asModifiedEntity() {
		throw new UnsupportedOperationException("asModifiedEntity() not implemented");
    }

    @Override
    protected Function<TransformETY, Document> toDocument() {
        return ConverterETY::fromTransformToDoc;
    }

    @Override
    protected Fixtures getFixtures() {
        return Fixtures.TRANSFORM;
    }

	@Override
	protected Function<Document, TransformETY> toEntity() {
		return ConverterETY::docToTransform;
	}
}
