package it.finanze.sanita.fse2.ms.gtw.rulesmanager.client;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangeSetSpecCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Date;

/**
 * EDS Client interface used to retrieve data according
 * the specific onChangeset subtype
 * @author G. Baittiner
 */
public interface IEDSClientV2 {
    <T> T getDocument(ChangeSetSpecCFG spec, String id, Class<T> type) throws EdsClientException;
    <T> T getStatus(ChangeSetSpecCFG spec, Date lastUpdate, ParameterizedTypeReference<T> type) throws EdsClientException;
}
