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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Arrays.stream;

@Component
public class ProfileUtility {

    @Autowired
    private Environment environment;

    public boolean isTestProfile() {
        // Get profiles
        String[] profiles = environment.getActiveProfiles();
        // Verify if exists the test profile
        Optional<String> exists = stream(profiles)
            .map(String::toLowerCase)
            .filter(i -> i.equals(Constants.Profile.TEST))
            .findFirst();
        // Return
        return exists.isPresent();
    }

    public boolean isDevOrDockerProfile() {
        // Get profiles
        String[] profiles = environment.getActiveProfiles();
        // Verify if exists the test profile
        Optional<String> exists = stream(profiles)
            .map(String::toLowerCase)
            .filter(i -> (i.equals(Constants.Profile.DEV) || i.equals(Constants.Profile.DOCKER)))
            .findFirst();
        // Return
        return exists.isPresent();
    }
}
