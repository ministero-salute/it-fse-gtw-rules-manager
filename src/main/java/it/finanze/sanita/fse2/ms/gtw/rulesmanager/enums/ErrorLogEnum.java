package it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums;

public enum ErrorLogEnum implements ILogEnum {

	KO_EDS("KO-EDS", "Errore nella chiamata a EDS"),
	KO_SCHED("KO-SCHED", "Errore nell'esecuzione dello Scheduler"),
	KO_INVALID_DATA("KO-INV-DATA", "Errore: dati di input non validi"),	
	KO_MONGO_DB("KO-MONGO-DB", "Errore nella chiamata a MongoDB"),
	KO_MONGO_DB_NOT_FOUND("KO-MONGO-DB-NOT-FOUND", "Elemento non trovato sul MongoDB"),
	KO_REDIS("KO-REDIS", "Errore nella chiamata a Redis"),
	KO_REDIS_NOT_FOUND("KO-REDIS-NOT-FOUND", "Elemento non trovato in cache"); 

	private String code;
	
	public String getCode() {
		return code;
	}

	private String description;

	private ErrorLogEnum(String inCode, String inDescription) {
		code = inCode;
		description = inDescription;
	}

	public String getDescription() {
		return description;
	}

}

