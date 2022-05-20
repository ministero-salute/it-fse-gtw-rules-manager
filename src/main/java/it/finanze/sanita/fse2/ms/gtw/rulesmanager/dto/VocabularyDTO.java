package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VocabularyDTO {

	private String system;

	List<VocabularyEntryDTO> entryDTO;
	
}
