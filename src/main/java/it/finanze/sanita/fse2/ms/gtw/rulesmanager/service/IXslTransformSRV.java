package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service;

import java.io.Serializable;
import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.XslTransformDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.XslTransformETY;

public interface IXslTransformSRV extends Serializable {

	XslTransformETY insert(XslTransformETY ety);
	
	void insertAll(List<XslTransformETY> etys);
	
	XslTransformETY findById(String id);
	
	Integer saveNewVersionXslTransform(List<XslTransformDTO> xslTranformDTO);

}
