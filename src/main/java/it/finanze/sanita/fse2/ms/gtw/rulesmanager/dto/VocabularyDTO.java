package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@Builder
@Setter
public class VocabularyDTO {

	private String system;

	List<VocabularyEntryDTO> entryDTO;
	
}
