package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.SchematronDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.SchematronEntryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ISchematronRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.SchematronETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.ISchematronSRV;
import lombok.extern.slf4j.Slf4j;


/**
 *	@author vincenzoingenito
 *
 *	Schemamatron service.
 */
@Service
@Slf4j
public class SchematronSRV implements ISchematronSRV {
	
	/**
	 * Serial version uid. 
	 */
	private static final long serialVersionUID = 8182215768557569504L;

	@Autowired
	private ISchematronRepo schematronRepo;
	
	@Override
	public SchematronETY insert(final SchematronETY ety) {
		SchematronETY output = null;
		try {
			output = schematronRepo.insert(ety);
		} catch(Exception ex) {
			log.error("Error inserting ety schematron :" , ex);
			throw new BusinessException("Error inserting ety schematron :" , ex);
		}
		return output;
	}

	@Override
	public void insertAll(List<SchematronETY> etys) {
		try {
			schematronRepo.insertAll(etys);
		} catch(Exception ex) {
			log.error("Error inserting all ety schemamatron :" , ex);
			throw new BusinessException("Error inserting all ety schemamatron :" , ex);
		}
	}

	@Override
	public SchematronETY findById(final String id) {
		SchematronETY output = null;
		try {
			output = schematronRepo.findById(id);
		} catch(Exception ex) {
			log.error("Error find by id ety schematron :" , ex);
			throw new BusinessException("Error find by id ety schematron :" , ex);
		}
		return output;
	}

	@Override
	public Integer saveNewVersionSchematron(final List<SchematronDTO> schematronListDTO) {
		Integer counter = 0;
		try {
			if(schematronListDTO!=null && !schematronListDTO.isEmpty()) {
				for(SchematronDTO schematronDTO : schematronListDTO) {
					boolean exist = schematronRepo.existByTemplateIdRoot(schematronDTO.getTemplateIdRoot());
					Date dataUltimoAggiornamento = new Date();
					if(Boolean.FALSE.equals(exist)) {
						SchematronETY schematronFather = buildDtoToEty(schematronDTO.getFatherSchematron(), true, schematronDTO.getTemplateIdRoot(), schematronDTO.getXsdVersion(),dataUltimoAggiornamento);
						schematronRepo.insert(schematronFather);
						
						List<SchematronETY> schematronChildren = buildDtoToEty(schematronDTO.getChildrenSchematronList(), false, schematronDTO.getTemplateIdRoot(), schematronDTO.getXsdVersion(),dataUltimoAggiornamento);
						schematronRepo.insertAll(schematronChildren);
					}
				}
 			}
		} catch(Exception ex) {
			log.error("Error while execute save new version schema :" , ex);
			throw new BusinessException("Error while execute save new version schema :" , ex);
		}
		return counter;
	}
	
	private List<SchematronETY> buildDtoToEty(List<SchematronEntryDTO> schematronEntriesDTO, Boolean rootSchema, 
			String templateIdRoot, String xsdSchemaVersion,Date dataUltimoAggiornamento) {
		List<SchematronETY> output = new ArrayList<>();
		
		for(SchematronEntryDTO schematronEntryDTO : schematronEntriesDTO) {
			output.add(buildDtoToEty(schematronEntryDTO, rootSchema,templateIdRoot,
					xsdSchemaVersion,dataUltimoAggiornamento));
		}
		
		return output;
	}
	
	private SchematronETY buildDtoToEty(SchematronEntryDTO schematronEntryDTO, Boolean rootSchema, 
			String templateIdRoot,String xsdSchemaVersion, final Date dataUltimoAggiornamento) {
		SchematronETY output = new SchematronETY();
		output.setId(null);
		output.setCdaCode(schematronEntryDTO.getCdaCode());
		output.setCdaCodeSystem(schematronEntryDTO.getCdaCodeSystem());
		output.setContentSchematron(schematronEntryDTO.getContentSchematron());
		output.setNameSchematron(schematronEntryDTO.getNameSchematron());
		output.setRootSchematron(rootSchema);
		output.setTemplateIdRoot(templateIdRoot);
		output.setTemplateIdExtension(schematronEntryDTO.getTemplateIdExtension());
		output.setDataUltimoAggiornamento(dataUltimoAggiornamento);
		return output;
	}
}
