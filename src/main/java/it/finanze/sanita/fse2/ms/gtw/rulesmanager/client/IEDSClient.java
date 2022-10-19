/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.client;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChunkChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsClientException;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Date;

/**
 * EDS Client interface used to retrieve data according
 * the specific onChangeset subtype
 * @author G. Baittiner
 */
public interface IEDSClient {
    // Standard interface
    <T> T getStatus(ChangesetCFG spec, Date lastUpdate, ParameterizedTypeReference<T> type) throws EdsClientException;
    <T> T getDocument(ChangesetCFG spec, String id, Class<T> type) throws EdsClientException;
    // Chunk interface
    <T> T getSnapshot(ChunkChangesetCFG spec, Date lastUpdate, Class<T> type) throws EdsClientException;
    <T> T getChunkIns(ChunkChangesetCFG spec, String id, int idx, Class<T> type) throws EdsClientException;
    <T> T getChunkDel(ChunkChangesetCFG spec, String id, int idx, Class<T> type) throws EdsClientException;
}
