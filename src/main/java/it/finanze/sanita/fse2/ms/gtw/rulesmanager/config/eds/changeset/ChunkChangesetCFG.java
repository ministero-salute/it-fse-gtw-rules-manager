package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset;

import lombok.Getter;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Getter
public abstract class ChunkChangesetCFG extends ChangesetCFG {

    private final String chunkInsert;
    private final String chunkDelete;

    protected ChunkChangesetCFG(String status, String chunkInsert, String chunkDelete, String production) {
        super(status, null, production);
        this.chunkInsert = chunkInsert;
        this.chunkDelete = chunkDelete;
    }

    public URI getChunkIns(String id, int idx) throws URISyntaxException {
        return new URI(chunkInsert + new URIBuilder().setPathSegments(id, Integer.toString(idx)).build());
    }

    public URI getChunkDel(String id, int idx) throws URISyntaxException {
        return new URI(chunkDelete + new URIBuilder().setPathSegments(id, Integer.toString(idx)).build());
    }

}
