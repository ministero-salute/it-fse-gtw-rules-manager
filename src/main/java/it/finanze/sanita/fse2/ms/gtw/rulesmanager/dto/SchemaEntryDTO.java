package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import lombok.Builder;
import lombok.Data;
import org.bson.types.Binary;

@Data
@Builder
public class SchemaEntryDTO {

	private String cdaType;
	
	private String nameSchema;
	
	private Binary contentSchema;
	
}
