package it.finanze.sanita.fse2.ms.gtw.rulesmanager.client;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Date;

/**
 * EDS Client interface used to retrieve data according
 * the specific onChangeset subtype
 * @author G. Baittiner
 */
public interface IEDSClient {
    <T> T getDocument(ChangesetCFG spec, String id, Class<T> type) throws EdsClientException;
    <T> T getStatus(ChangesetCFG spec, Date lastUpdate, ParameterizedTypeReference<T> type) throws EdsClientException;
}
