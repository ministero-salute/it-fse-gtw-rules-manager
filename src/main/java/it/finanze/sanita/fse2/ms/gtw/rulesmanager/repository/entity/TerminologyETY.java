/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

/**
 * Model to save terminology.
 */
@Document(collection = "#{@terminologyBean}")
@Data
@NoArgsConstructor
public class TerminologyETY {

	public static final String FIELD_ID = "_id";
    public static final String FIELD_SYSTEM = "system";
	public static final String FIELD_SYSTEM_ID_REF = FIELD_ID + "." + FIELD_SYSTEM;
    public static final String FIELD_VERSION = "version";
	public static final String FIELD_VERSION_ID_REF = FIELD_ID + "." + FIELD_VERSION;
	public static final String FIELD_CODE = "code";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_RELEASE_DATE = "release_date";
    public static final String FIELD_LAST_UPDATE = "last_update_date";
    public static final String FIELD_LAST_SYNC = "last_sync";
    public static final String FIELD_DELETED = "deleted";


	@Id
	private String id;
	@Field(name = FIELD_SYSTEM)
	private String system;
	@Field(name = FIELD_VERSION)
	private String version;
	@Field(name = FIELD_CODE)
	private String code;
	@Field(name = FIELD_DESCRIPTION)
	private String description;
	@Field(name = FIELD_RELEASE_DATE)
    private Date releaseDate;
	@Field(name = FIELD_LAST_UPDATE)
    private Date lastUpdateDate;
    @Field(name = FIELD_LAST_SYNC)
    private Date lastSync;
    @Field(name = FIELD_DELETED)
    private Boolean deleted;


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TerminologyETY other = (TerminologyETY) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (system == null) {
			if (other.system != null)
				return false;
		} else if (!system.equals(other.system))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((system == null) ? 0 : system.hashCode());
		return result;
	} 
	
	
    public static TerminologyETY fromPath(Path path, String system, String version,
    		String code, String description) throws IOException {
    	TerminologyETY entity = new TerminologyETY();
        entity.setSystem(system); 
        entity.setVersion(version); 
        entity.setCode(code); 
        entity.setDescription(description); 
        entity.setLastUpdateDate(new Date());
        entity.setLastSync(new Date());
        return entity;
    }
}
