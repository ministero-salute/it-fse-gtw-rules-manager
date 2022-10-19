/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk.base.ChunksDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeSetChunkDTO {

    private String traceID;
    private String spanID;

    private Date lastUpdate;
    private Date timestamp;

    private ChunksDTO chunks;

    private int totalNumberOfElements;
}
