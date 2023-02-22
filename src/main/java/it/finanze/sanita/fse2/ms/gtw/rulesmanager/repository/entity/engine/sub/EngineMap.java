/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.engine.sub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EngineMap {

    public static final String FIELD_OID = "oid";
    public static final String FIELD_ROOT = "template_id_root";
    public static final String FIELD_URI = "uri";
    public static final String FIELD_VERSION = "version";


    @Field(FIELD_OID)
    private ObjectId oid;

    @Field(FIELD_ROOT)
    private List<String> root;

    @Field(FIELD_URI)
    private String uri;

    @Field(FIELD_VERSION)
    private String version;

    public static Document from(EngineMap map) {
        return new Document()
            .append(FIELD_OID, map.getOid())
            .append(FIELD_ROOT, map.getRoot())
            .append(FIELD_URI, map.getUri())
            .append(FIELD_VERSION, map.getVersion());
    }

    public static List<Document> from(List<EngineMap> maps) {
        return maps.stream().map(EngineMap::from).collect(Collectors.toList());
    }

}
