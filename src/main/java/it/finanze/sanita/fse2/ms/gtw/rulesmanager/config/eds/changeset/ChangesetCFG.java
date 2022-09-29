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
import java.util.Objects;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@Getter
public abstract class ChangesetCFG {
    public static final String LAST_UPDATE_FIELD = "lastUpdate";
    public static final String STAGING_QUALIFIER = "-staging";

    public static final int RETRY_VALUE = 3;

    private final String title;
    private final String production;
    private final String staging;
    private final String statusURL;
    private final String dataURL;
    private final String parent;

    protected ChangesetCFG(String status, String data, String production) {
        this.title = production.substring(0, 1).toUpperCase() + production.substring(1);
        this.production = production;
        this.staging = production + STAGING_QUALIFIER;
        this.statusURL = status;
        this.dataURL = data;
        this.parent = null;
    }

    protected ChangesetCFG(String status, String data, String production, String parent) {
        this.title = production.substring(0, 1).toUpperCase() + production.substring(1);
        this.production = production;
        this.staging = production + STAGING_QUALIFIER;
        this.statusURL = status;
        this.dataURL = data;
        this.parent = parent;
    }

    public String getParent() {
        // Minor check to prevent issues
        Objects.requireNonNull(parent, "The parent property hasn't been specified on this configuration");
        return parent;
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
