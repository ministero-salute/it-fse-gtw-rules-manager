/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.impl;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChunkChangesetCFG;
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
 */
@Component
public class EDSClient implements IEDSClient {

    @Autowired
    private RestTemplate client;

    @Override
    public <T> T getDocument(ChangesetCFG spec, String id, Class<T> type) throws EdsClientException {
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
    public <T> T getSnapshot(ChunkChangesetCFG spec, Date lastUpdate, Class<T> type) throws EdsClientException {
        // Execute request
        ResponseEntity<T> response;
        try {
            response = client.getForEntity(spec.getStatusReq(lastUpdate), type);
        }catch (Exception e) {
            throw new EdsClientException("Error while executing the request", e);
        }
        // Return data
        return response.getBody();
    }

    @Override
    public <T> T getChunkIns(ChunkChangesetCFG spec, String id, int idx,  Class<T> type) throws EdsClientException {
        // Execute request
        ResponseEntity<T> response;
        try {
            response = client.getForEntity(spec.getChunkIns(id, idx), type);
        }catch (Exception e) {
            throw new EdsClientException("Error while executing the request", e);
        }
        // Return data
        return response.getBody();
    }

    @Override
    public <T> T getChunkDel(ChunkChangesetCFG spec, String id, int idx, Class<T> type) throws EdsClientException {
        // Execute request
        ResponseEntity<T> response;
        try {
            response = client.getForEntity(spec.getChunkDel(id, idx), type);
        }catch (Exception e) {
            throw new EdsClientException("Error while executing the request", e);
        }
        // Return data
        return response.getBody();
    }

    @Override
    public <T> T getStatus(ChangesetCFG spec, Date lastUpdate, ParameterizedTypeReference<T> type) throws EdsClientException {
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
