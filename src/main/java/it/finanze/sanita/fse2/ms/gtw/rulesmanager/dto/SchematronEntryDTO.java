package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import org.bson.types.Binary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchematronEntryDTO {

	private Binary contentSchematron;

	private String nameSchematron;

	private String cdaCode;

	private String cdaCodeSystem;
	
	private String templateIdExtension;

}
