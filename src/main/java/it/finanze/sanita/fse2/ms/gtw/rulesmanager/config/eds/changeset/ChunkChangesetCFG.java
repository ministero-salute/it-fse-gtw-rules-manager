package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset;

import lombok.Getter;

@Getter
public abstract class ChunkChangesetCFG extends ChangesetCFG {

    private final String chunkInsert;
    private final String chunkDelete;

    protected ChunkChangesetCFG(String status, String chunkInsert, String chunkDelete, String production) {
        super(status, null, production);
        this.chunkInsert = chunkInsert;
        this.chunkDelete = chunkDelete;
    }
}
