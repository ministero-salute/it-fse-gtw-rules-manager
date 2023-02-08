package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "#{@structuresBean}")
@Data
@NoArgsConstructor
public class TransformETY {
    public static final String FIELD_ID = "_id";
    public static final String FIELD_URI = "uri";
    public static final String FIELD_VERSION = "version";
    public static final String FIELD_FILENAME = "filename";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_TEMPLATE_ID_ROOT = "template_id_root";
    public static final String FIELD_LAST_UPDATE = "last_update_date";
    public static final String FIELD_DELETED = "deleted";
    public static final String FIELD_LAST_SYNC = "last_sync";

}
