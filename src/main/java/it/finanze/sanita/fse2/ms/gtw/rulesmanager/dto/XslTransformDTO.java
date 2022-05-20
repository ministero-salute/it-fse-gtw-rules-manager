package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XslTransformDTO {
    
	/**
     * Version.
     */
	private String version;
	
	/**
     * Xsl transform list.
     */
	List<XslTransformEntryDTO> entryListDTO;
	
}
