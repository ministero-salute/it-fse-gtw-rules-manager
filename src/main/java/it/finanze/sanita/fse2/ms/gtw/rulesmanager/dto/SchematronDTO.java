package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import java.util.List;

import org.bson.types.Binary;

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
     * Cda template id.
     */
	private String templateIdRoot;
	
	/**
     * Template id extension.
     */
	private String templateIdExtension;
	
	/**
     * Content schematron.
     */
	private Binary contentSchematron;

	/**
     * Name schematron.
     */
	private String nameSchematron;
	
 
}