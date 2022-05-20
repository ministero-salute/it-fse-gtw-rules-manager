package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.SchemaDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.SchemaEntryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ISchemaRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ISchemaSRV;
import lombok.extern.slf4j.Slf4j;

/**
 *	@author vincenzoingenito
 *
 *	Schema service.
 */
@Service
@Slf4j
public class SchemaSRV implements ISchemaSRV {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 1491502156280529977L;

	@Autowired
	private ISchemaRepo schemaRepo;
	
	@Override
	public SchemaETY insert(final SchemaETY ety) {
		SchemaETY output = null;
		try {
			output = schemaRepo.insert(ety);
		} catch(Exception ex) {
			log.error("Error inserting ety schema :" , ex);
			throw new BusinessException("Error inserting ety schemas :" , ex);
		}
		return output;
	}

	@Override
	public void insertAll(final List<SchemaETY> etys) {
		try {
			schemaRepo.insertAll(etys);
		} catch(Exception ex) {
			log.error("Error inserting all ety schema :" , ex);
			throw new BusinessException("Error inserting all ety schemas :" , ex);
		}
		
	}

	@Override
	public Integer saveNewVersionSchema(final SchemaDTO schemaDTO) {
		Integer counter = 0;
		try {
			if(schemaDTO!=null) {
				final String version = schemaDTO.getVersion();
				boolean exist = schemaRepo.existByVersion(version);
				
				Date dataUltimoAggiornamento = new Date();
				if(Boolean.FALSE.equals(exist)) {
					SchemaETY fatherToSave = buildDtoToETY(schemaDTO.getSchemaFatherEntryDTO(), true, version,dataUltimoAggiornamento);
					schemaRepo.insert(fatherToSave);
					
					List<SchemaETY> childrenToSave = buildDtoToETYS(schemaDTO.getSchemaChildEntryDTO(), false, version,dataUltimoAggiornamento);
					schemaRepo.insertAll(childrenToSave);
				}
			}
		} catch(Exception ex) {
			log.error("Error while execute save new version schema :" , ex);
			throw new BusinessException("Error while execute save new version schema :" , ex);
		}
		return counter;
	}
	
	private List<SchemaETY> buildDtoToETYS(final List<SchemaEntryDTO> schemaEntriesDTO, final Boolean rootSchema, final String version, final Date dataUltimoAggiornamento){
		List<SchemaETY> output = new ArrayList<>();
		for(SchemaEntryDTO schemaEntryDTO : schemaEntriesDTO) {
			output.add(buildDtoToETY(schemaEntryDTO, rootSchema, version,dataUltimoAggiornamento));
		}
		return output;
	}
	
	private SchemaETY buildDtoToETY(final SchemaEntryDTO schemaEntryDTO, final Boolean rootSchema, final String version,final Date dataUltimoAggiornamento) {
		SchemaETY output = new SchemaETY();
		output.setId(null);
		output.setCdaType(schemaEntryDTO.getCdaType());
		output.setContentSchema(schemaEntryDTO.getContentSchema());
		output.setNameSchema(schemaEntryDTO.getNameSchema());
		output.setVersion(version);
		output.setRootSchema(rootSchema);
		output.setDataUltimoAggiornamento(dataUltimoAggiornamento);
		return output;
	}
}
