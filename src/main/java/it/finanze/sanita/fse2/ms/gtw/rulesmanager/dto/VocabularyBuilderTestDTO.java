package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto;

import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class VocabularyBuilderTestDTO {

	@CsvBindByPosition(position = 0)
	private String code;

	@CsvBindByPosition(position = 10)
	private String description;
}
