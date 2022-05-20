package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VocabularyEntryDTO {

	private String code;
	
	private String description;
}
