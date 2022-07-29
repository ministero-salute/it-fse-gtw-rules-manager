package it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClientV2;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangeSetSpecCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * Concrete implementation for the eds client interface
 * 
 * @author G. Baittiner
 */
@Component
public class EDSClient implements IEDSClientV2 {

    @Autowired
    private RestTemplate client;

    @Override
    public <T> T getDocument(ChangeSetSpecCFG spec, String id, Class<T> type) throws EdsClientException {
        // Execute request
        ResponseEntity<T> response;
        try {
            response = client.getForEntity(spec.getDataReq(id), type);
        }catch (Exception e) {
            throw new EdsClientException("Error while executing the request", e);
        }
        // Return data
        return response.getBody();
    }

    @Override
    public <T> T getStatus(ChangeSetSpecCFG spec, Date lastUpdate, ParameterizedTypeReference<T> type) throws EdsClientException {
        // Execute request
        ResponseEntity<T> response;
        try {
            response = client.exchange(
                spec.getStatusReq(lastUpdate),
                HttpMethod.GET,
                null,
                type
            );
        }catch (Exception e) {
            throw new EdsClientException("Error while executing the request", e);
        }
        // Return data
        return response.getBody();
    }
}
