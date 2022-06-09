package it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums;

public enum OperationLogEnum implements ILogEnum {

	CALL_EDS("CALL-EDS", "Chiamata EDS"),
	REDIS("REDIS", "Salvataggio/Query su Redis"),
	MONGO("MONGO", "Salvataggio/Query su MongoDB"); 

	private String code;
	
	public String getCode() {
		return code;
	}

	private String description;

	private OperationLogEnum(String inCode, String inDescription) {
		code = inCode;
		description = inDescription;
	}

	public String getDescription() {
		return description;
	}

}

