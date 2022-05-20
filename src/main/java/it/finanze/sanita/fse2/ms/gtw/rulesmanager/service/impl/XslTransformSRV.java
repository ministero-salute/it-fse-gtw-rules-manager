package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.XslTransformDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.XslTransformEntryDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IXslTransformRepo;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.XslTransformETY;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IXslTransformSRV;
import lombok.extern.slf4j.Slf4j;

/**
 *	@author vincenzoingenito
 *
 *	Xsl transform service.
 */
@Service
@Slf4j
public class XslTransformSRV implements IXslTransformSRV {
	
	/**
	 * Serial version uid. 
	 */
	private static final long serialVersionUID = 5095391867459703755L;

	@Autowired
	private IXslTransformRepo xslTransformRepo;

	@Override
	public XslTransformETY insert(final XslTransformETY ety) {
		XslTransformETY output = null;
		try {
			output = xslTransformRepo.insert(ety);
		} catch(Exception ex) {
			log.error("Error inserting ety xsl transform :" , ex);
			throw new BusinessException("Error inserting ety xsl transform :" , ex);
		}
		return output;
	}

	@Override
	public void insertAll(List<XslTransformETY> etys) {
		try {
			xslTransformRepo.insertAll(etys);
		} catch(Exception ex) {
			log.error("Error inserting all ety xsl transform :" , ex);
			throw new BusinessException("Error inserting all ety xsl transform :" , ex);
		}
	}

	@Override
	public XslTransformETY findById(String id) {
		XslTransformETY output = null;
		try {
			output = xslTransformRepo.findById(id);
		} catch(Exception ex) {
			log.error("Error find by id ety xsltransform :" , ex);
			throw new BusinessException("Error find by id ety xsltransform :" , ex);
		}
		return output;
	}

	@Override
	public Integer saveNewVersionXslTransform(final List<XslTransformDTO> xslTranformEntriesDTO) {
		Integer counter = 0;
		try {
			if(xslTranformEntriesDTO!=null && !xslTranformEntriesDTO.isEmpty()) {
				for(XslTransformDTO xslTranformEntryDTO : xslTranformEntriesDTO) {
					boolean exist = xslTransformRepo.existByVersion(xslTranformEntryDTO.getVersion());
					if(Boolean.FALSE.equals(exist)) {
						List<XslTransformETY> toAdd = buildDtoToETYS(xslTranformEntryDTO.getEntryListDTO(), xslTranformEntryDTO.getVersion());
						xslTransformRepo.insertAll(toAdd);
					}
				}
			} 
		} catch(Exception ex) {
			log.error("Error while execute save new version xsl tranform :" , ex);
			throw new BusinessException("Error while execute save new version xsl tranform :" , ex);
		}
		return counter;
	}
	
	private List<XslTransformETY> buildDtoToETYS(List<XslTransformEntryDTO> xslTrasformEntriesDTO, String version) {
		List<XslTransformETY> output = new ArrayList<>();
		for(XslTransformEntryDTO xslTrasformEntryDTO : xslTrasformEntriesDTO) {
			XslTransformETY entry = new XslTransformETY();
			entry.setId(null);
			entry.setCdaType(xslTrasformEntryDTO.getCdaType());
			entry.setContentXslTransform(xslTrasformEntryDTO.getContentXslTransform());
			entry.setNameXslTransform(xslTrasformEntryDTO.getNameXslTransform());
			entry.setVersion(version);
			output.add(entry);
		}
		return output;
	}
}
