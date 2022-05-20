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
	 * Xsd schemas.
	 */
	private SchemaDTO schema;

	/**
	 * Schematron.
	 */
	private List<SchematronDTO> schematron;

	/**
	 * Xsl transform.
	 */
	private List<XslTransformDTO> xslTransform;
	
	/**
	 * Vocabulary.
	 */
	private List<VocabularyDTO> vocabulary;
 
}
