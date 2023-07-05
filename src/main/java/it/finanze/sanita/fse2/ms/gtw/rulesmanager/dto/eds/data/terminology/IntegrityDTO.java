package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.data.terminology;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
public class IntegrityDTO {

    private static final String PTT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private String traceID;
    private String spanID;

    @JsonFormat(pattern = PTT_ISO_8601)
    private Date timestamp;

    private List<Resources> resources;

    @Data
    @JsonInclude(NON_NULL)
    public static class Resources {
        String id;
        String version;
        String type;
        Long size;
        public String info() {
            return String.format("%s|%s/%s", type.substring(0, 3), id, version);
        }

        public Optional<Long> getSize() {
            return Optional.ofNullable(size);
        }
    }

    public boolean isEmpty() {
        return resources.isEmpty();
    }

}
