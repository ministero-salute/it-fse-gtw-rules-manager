package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * DTO of configuration items retured from EDS Client.
 * 
 * @author Simone Lungarella
 */
@Data
@Builder
public class ConfigItemDTO { 
	 
	/**
	 * Vocabulary.
	 */
	private List<VocabularyDTO> vocabulary;
	 
 
}
