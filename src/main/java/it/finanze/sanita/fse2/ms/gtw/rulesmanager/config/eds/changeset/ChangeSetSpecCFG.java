package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset;

import lombok.Getter;
import org.apache.http.client.utils.URIBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@Getter
public abstract class ChangeSetSpecCFG {
    public static String LAST_UPDATE_FIELD = "lastUpdate";
    public static String STAGING_QUALIFIER = "-staging";

    public static int RETRY_VALUE = 3;

    private final String title;
    private final String schema;
    private final String staging;
    private final String statusURL;
    private final String dataURL;

    protected ChangeSetSpecCFG(String status, String data, String schema) {
        this.title = schema.substring(0, 1).toUpperCase() + schema.substring(1);
        this.schema = schema;
        this.staging = schema + STAGING_QUALIFIER;
        this.statusURL = status;
        this.dataURL = data;
    }

    public URL getStatusURL() throws MalformedURLException {
        return new URL(statusURL);
    }

    public URL getDataURL() throws MalformedURLException {
        return new URL(dataURL);
    }

    public URI getStatusURI() throws URISyntaxException, MalformedURLException {
        return getStatusURL().toURI();
    }

    public URI getDataURI() throws URISyntaxException, MalformedURLException {
        return getDataURL().toURI();
    }

    public String getLastUpdateFormatted(Date lastUpdate) {
        // Working var
        String res = "null";
        // Handle nullable lastUpdate
        if(lastUpdate != null) {
            // Set timezone
            // Truncate to millis otherwise SpringBoot DateTimeFormat start crying
            OffsetDateTime update = lastUpdate
                .toInstant()
                .atOffset(ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.MILLIS);
            // Format
            res = ISO_DATE_TIME.format(update);
        }
        // Bye
        return res;
    }

    public URI getStatusReq(Date lastUpdate) throws MalformedURLException, URISyntaxException {
        // Create URI
        URI uri = getStatusURI();
        // Verify if timeframe is given or if it's a new copy
        if(lastUpdate != null) {
            // Return encoded
            uri = new URIBuilder(uri)
                .addParameter(
                    LAST_UPDATE_FIELD, getLastUpdateFormatted(lastUpdate)
                ).build();
        }
        return uri;
    }

    public URI getDataReq(String id) throws MalformedURLException, URISyntaxException {
        return getDataURI().resolve(id);
    }
}
