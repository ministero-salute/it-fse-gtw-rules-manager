package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	 
 }
