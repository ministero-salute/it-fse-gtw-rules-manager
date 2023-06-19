/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
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

    public static final String BACKUP_QUALIFIER = "-backup";

    public static final String PARENT_SEPARATOR = "|";

    public static final int RETRY_VALUE = 3;

    private final String title;
    private final String production;
    private final String staging;
    private final String statusURL;
    private final String dataURL;
    private final ChangesetCFG parent;
    private final String backup;

    protected ChangesetCFG(String status, String data, String production) {
        this.title = capitalize(production);
        this.production = production;
        this.staging = production + STAGING_QUALIFIER;
        this.backup = production + BACKUP_QUALIFIER;
        this.statusURL = status;
        this.dataURL = data;
        this.parent = null;
    }

    protected ChangesetCFG(String status, String data, String production, ChangesetCFG parent) {
        this.title = String.format("%s %s %s", parent.getTitle(), PARENT_SEPARATOR, capitalize(production));
        this.production = production;
        this.staging = production + STAGING_QUALIFIER;
        this.backup = production + BACKUP_QUALIFIER;
        this.statusURL = status;
        this.dataURL = data;
        this.parent = parent;
    }

    private String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    public ChangesetCFG getParent() {
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
