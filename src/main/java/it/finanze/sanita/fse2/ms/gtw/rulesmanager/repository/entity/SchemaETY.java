package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

/**
 * Model to save schema documents
 */
@Document(collection = "#{@schemaBean}")
@Data
@NoArgsConstructor
public class SchemaETY {

    public static final String FIELD_ID = "_id";
    public static final String FIELD_FILENAME = "name_schema";
    public static final String FIELD_CONTENT = "content_schema";
    public static final String FIELD_TYPE_ID_EXT = "type_id_extension";
    public static final String FIELD_ROOT_SCHEMA = "root_schema";
    public static final String FIELD_LAST_UPDATE = "last_update_date";
    public static final String FIELD_LAST_SYNC = "last_sync";

    @Id
    private String id;
    @Field(name = FIELD_FILENAME)
    private String nameSchema;
    @Field(name = FIELD_CONTENT)
    private Binary contentSchema;
    @Field(name = FIELD_TYPE_ID_EXT)
    private String typeIdExtension;
    @Field(name = FIELD_ROOT_SCHEMA)
    private Boolean rootSchema;
    @Field(name = FIELD_LAST_UPDATE)
    private Date lastUpdateDate;
    @Field(name = FIELD_LAST_SYNC)
    private Date lastSync;

    public void setContentSchemaFromPath(Path path) throws IOException {
        this.contentSchema = new Binary(Files.readAllBytes(path));
    }

    public static SchemaETY fromPath(Path path, String extension, boolean root) throws IOException {
        SchemaETY entity = new SchemaETY();
        entity.setNameSchema(path.getFileName().toString());
        entity.setContentSchemaFromPath(path);
        entity.setTypeIdExtension(extension);
        entity.setRootSchema(root);
        entity.setLastUpdateDate(new Date());
        entity.setLastSync(new Date());
        return entity;
    }

}
