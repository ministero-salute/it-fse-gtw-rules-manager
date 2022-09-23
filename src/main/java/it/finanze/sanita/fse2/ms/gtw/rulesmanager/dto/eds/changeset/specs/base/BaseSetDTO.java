package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseSetDTO<T> {
    private String id;
    private T description;
}
