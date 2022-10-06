package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChunkStatsDTO {
    private int chunksCount;
    private int chunksAvgSize;
    private int chunksItems;
}
