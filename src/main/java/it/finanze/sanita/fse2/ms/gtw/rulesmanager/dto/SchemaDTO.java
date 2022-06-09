package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;


/**
 * DTO of configuration schema items retured from EDS Client.
 * 
 * @author vincenzoingenito
 */
@Data
@Builder
public class SchemaDTO {
    
	/**
     * Type id extension.
     */
	private String typeIdExtension;

	/**
     * Schema father.
     */
	SchemaEntryDTO schemaFatherEntryDTO;
	
	/**
     * Schema child.
     */
	List<SchemaEntryDTO> schemaChildEntryDTO;
	 
}