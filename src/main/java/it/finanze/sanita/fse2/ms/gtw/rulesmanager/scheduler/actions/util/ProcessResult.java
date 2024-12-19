
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
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.util;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.eds.changeset.chunk.ChangeSetChunkDTO;
import lombok.Getter;

import static java.lang.String.format;

@Getter
public final class ProcessResult {

    private final int insertions;
    private final int deletions;
    private final int operations;

    private final int expectedInsertions;
    private final int expectedDeletions;
    private final int expectedOperations;

    public ProcessResult(
        int insertions, int deletions,
        int expectedInsertions, int expectedDeletions
    ) {
        this.insertions = insertions;
        this.deletions = deletions;
        this.operations = insertions + deletions;
        this.expectedInsertions = expectedInsertions;
        this.expectedDeletions = expectedDeletions;
        this.expectedOperations = expectedInsertions + expectedDeletions;
    }

    public boolean isValid() {
        return operations == expectedOperations;
    }

    public String getInfo() {
        return format(
            "[ Insertions: %d | Deletions: %d ]",
            insertions,
            deletions
        );
    }

    public String getExpectedInfo() {
        return format(
                "[ Insertions: %d | Deletions: %d ]",
                expectedInsertions,
                expectedDeletions
            );
    }

    public static String info(ChangeSetDTO<?> changeset) {
        return format(
            "[ Insertions: %d | Deletions: %d ]",
            changeset.getInsertions().size(),
            changeset.getDeletions().size()
        );
    }

    public static String info(ChangeSetChunkDTO snapshot) {
        return format(
            "[ Insertions: %d | Deletions: %d ]",
            snapshot.getChunks().getInsertions().getChunksItems(),
            snapshot.getChunks().getDeletions().getChunksItems()
        );
    }

}
