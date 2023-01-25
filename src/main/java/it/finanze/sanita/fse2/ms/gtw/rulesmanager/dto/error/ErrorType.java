/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    IO("/msg/io", "Errore IO");

    private final String type;
    private final String title;

    public String toInstance(String instance) {
        return UriComponentsBuilder
            .fromUriString(instance)
            .build()
            .toUriString();
    }
}
