package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.entities;

import org.bson.Document;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public abstract class AbstractEntityHandler<T> {
    public static String TEST_COLL_A = "eds-test-0";
    public static String TEST_COLL_B = "eds-test-1";
    public static String TEST_BASE_COLLECTION = "eds-test-2";

    private final List<T> entities;

    public AbstractEntityHandler() {
        this.entities = new ArrayList<>();
    }

    public void initTestEntities() throws Exception {
        // Consistency check
        if(entities.isEmpty()) {
            // List of all files inside the sample modified directory
            try (Stream<Path> files = Files.list(getResourceDir())) {
                // Convert to list
                List<Path> samples = files.collect(Collectors.toList());
                // Add to each map and convert
                for (Path path : samples) {
                    this.entities.add(asInitEntity(path));
                }
            }
        }
    }

    public void clearTestEntities() {
        this.entities.clear();
    }

    public List<T> getEntities() {
        return new ArrayList<>(entities);
    }

    public List<Document> getEntitiesAsDocuments() {
        return new ArrayList<>(entities).stream().map(asDocument()).collect(Collectors.toList());
    }

    public List<Document> getModifiedEntitiesAsDocuments() {
        return entities.stream().map(asModifiedEntity()).map(asDocument()).collect(Collectors.toList());
    }

    protected abstract T asInitEntity(Path path) throws Exception;
    protected abstract Function<T, T> asModifiedEntity();
    protected abstract Function<T, Document> asDocument();
    protected abstract Path getResourceDir();

}
