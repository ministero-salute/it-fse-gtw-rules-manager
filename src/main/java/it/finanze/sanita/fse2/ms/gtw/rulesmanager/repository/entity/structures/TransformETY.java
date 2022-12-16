package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.structures;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "#{@structuresBean}")
@Data
@NoArgsConstructor
public class TransformETY {
    public static final String FIELD_ID = "_id";
    public static final String FIELD_MAPS = "maps";
    public static final String FIELD_DEFINITIONS = "definitions";
    public static final String FIELD_VALUESETS = "valuesets";
    public static final String FIELD_VERSION = "version";
    public static final String FIELD_TEMPLATE_ID_ROOT = "template_id_root";
    public static final String FIELD_LAST_UPDATE = "last_update_date";
    public static final String FIELD_ROOT_MAP = "root_map";
    public static final String FIELD_DELETED = "deleted";
    public static final String FIELD_LAST_SYNC = "last_sync";

}
