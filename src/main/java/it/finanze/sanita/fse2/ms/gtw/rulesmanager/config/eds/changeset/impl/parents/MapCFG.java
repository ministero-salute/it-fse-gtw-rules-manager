package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.impl.parents;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MapCFG extends ChangesetCFG {
    private static final String SCHEMA = "map";
    private static final String PARENT = "structures";
    protected MapCFG(
        @Value("${eds.changeset.map.status}")
        String status,
        @Value("${eds.changeset.map.data}")
        String data
    ) {
        super(status, data, SCHEMA, PARENT);
    }
}
