package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.specs;

import lombok.Data;

@Data
public class SchemaSetDTO {
    String id;
    Payload description;
    @Data
    public static class Payload {
        String extension;
        String filename;
    }
}
