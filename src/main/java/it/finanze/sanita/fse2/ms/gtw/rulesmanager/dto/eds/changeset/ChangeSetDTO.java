package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.base.BaseSetDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * DTO for Change Set status endpoint response.
 *
 * @author Riccardo Bonesi
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeSetDTO<T> {

    /**
     * Trace id log.
     */
    private String traceID;

    /**
     * Span id log.
     */
    private String spanID;

    private Date lastUpdate;
    private Date timestamp;

    private List<BaseSetDTO<T>> insertions;

    private List<BaseSetDTO<T>> deletions;

    private int totalNumberOfElements;

}