package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import org.bson.types.Binary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XslTransformEntryDTO {

	private String cdaType;
	
	private String nameXslTransform;
	
	private Binary contentXslTransform;
}
