package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TerminologyMapDTO {
	private String system;
	private String version;
	private String code;
	private Date releaseDate;
	private Date creationDate;
}