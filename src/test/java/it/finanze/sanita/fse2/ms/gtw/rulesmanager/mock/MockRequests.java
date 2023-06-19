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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.RouteUtility.API_RUN_SCHEDULER_FULL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public final class MockRequests {
    public static MockHttpServletRequestBuilder runScheduler() {
        return post(API_RUN_SCHEDULER_FULL).contentType(MediaType.APPLICATION_JSON);
    }

}
