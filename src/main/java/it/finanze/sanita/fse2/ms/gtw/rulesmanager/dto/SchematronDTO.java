package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * DTO of configuration schematron items retured from EDS Client.
 * 
 * @author vincenzoingenito
 */
@Data
@Builder
public class SchematronDTO {
    
	/**
     * Version.
     */
	private String templateIdRoot;
	
	/**
     * Father schematron.
     */
	private SchematronEntryDTO fatherSchematron;
	
    
    /**
     * List of schematrons child.
     */
    private List<SchematronEntryDTO> childrenSchematronList;
    
    /**
     * Xsd Version.
     */
	private String xsdVersion;
 
}