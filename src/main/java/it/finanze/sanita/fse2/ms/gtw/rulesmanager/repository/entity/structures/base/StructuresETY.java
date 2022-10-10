//package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.structures.base;
//
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.structures.StructureDefinitionETY;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.structures.StructureMapETY;
//import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.structures.StructureValuesetETY;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.data.mongodb.core.mapping.Field;
//
//import java.util.Date;
//import java.util.List;
//
//@Document(collection = "#{@structuresBean}")
//@Data
//@NoArgsConstructor
//public class StructuresETY {
//
//    public static final String FIELD_ID = "_id";
//
//    public static final String FIELD_LAST_UPDATE = "last_update_date";
//    public static final String FIELD_MAP = "map";
//    public static final String FIELD_VALUESET = "valueset";
//    public static final String FIELD_DEFINITION = "definition";
//
//    @Id
//    private String id;
//
//    @Field(name = FIELD_LAST_UPDATE)
//    private Date lastUpdate;
//
//    @Field(name = FIELD_MAP)
//    private List<StructureMapETY> map;
//
//    @Field(name = FIELD_VALUESET)
//    private List<StructureValuesetETY> valueset;
//
//    @Field(name = FIELD_DEFINITION)
//    private List<StructureDefinitionETY> definition;
//
//}
