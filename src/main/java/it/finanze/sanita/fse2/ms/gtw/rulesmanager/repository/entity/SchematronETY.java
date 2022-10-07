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
 * Model to save schematron.
 */
@Document(collection = "#{@schematronBean}")
@Data
@NoArgsConstructor
public class SchematronETY {

	public static final String FIELD_ID = "_id";
    public static final String FIELD_FILENAME = "name_schematron";
    public static final String FIELD_CONTENT = "content_schematron";
    public static final String VERSION = "version";
    public static final String FIELD_ROOT = "template_id_root";
    public static final String FIELD_LAST_UPDATE = "last_update_date";
    public static final String FIELD_LAST_SYNC = "last_sync";

	@Id
	private String id;
	@Field(name = FIELD_CONTENT)
	private Binary contentSchematron;
	@Field(name = FIELD_FILENAME)
	private String nameSchematron;
	@Field(name = FIELD_ROOT)
	private String templateIdRoot;
	@Field(name = VERSION)
	private String version;
	@Field(name = FIELD_LAST_UPDATE)
	private Date lastUpdateDate;
	@Field(name = FIELD_LAST_SYNC)
    private Date lastSync;

	public void setContentSchemaFromPath(Path path) throws IOException {
        this.contentSchematron = new Binary(Files.readAllBytes(path));
    }

    public static SchematronETY fromPath(Path path, String version, String root) throws IOException {
        SchematronETY entity = new SchematronETY();
        entity.setNameSchematron(path.getFileName().toString());
        entity.setContentSchemaFromPath(path);
        entity.setVersion(version);
        entity.setTemplateIdRoot(root);
        entity.setLastUpdateDate(new Date());
        entity.setLastSync(new Date());
        return entity;
    }
	 
}