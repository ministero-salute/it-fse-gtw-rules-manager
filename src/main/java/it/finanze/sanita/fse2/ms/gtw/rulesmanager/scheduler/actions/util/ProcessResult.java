package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.util;

import lombok.Getter;

import static java.lang.String.format;

@Getter
public final class ProcessResult {

    private final int insertions;
    private final int modifications;
    private final int deletions;
    private final int operations;

    private final int expectedInsertions;
    private final int expectedModifications;
    private final int expectedDeletions;
    private final int expectedOperations;

    public ProcessResult(
        int insertions, int modifications, int deletions,
        int expectedInsertions, int expectedModifications, int expectedDeletions
    ) {
        this.insertions = insertions;
        this.modifications = modifications;
        this.deletions = deletions;
        this.operations = insertions + modifications + deletions;
        this.expectedInsertions = expectedInsertions;
        this.expectedModifications = expectedModifications;
        this.expectedDeletions = expectedDeletions;
        this.expectedOperations = expectedInsertions + expectedModifications + expectedDeletions;
    }

    public boolean isValid() {
        return operations == expectedOperations;
    }

    public String getInfo() {
        return format(
            "[ Insertions: %d | Modifications: %d | Deletions: %d ]",
            insertions,
            modifications,
            deletions
        );
    }

    public String getExpectedInfo() {
        return format(
                "[ Insertions: %d | Modifications: %d | Deletions: %d ]",
                expectedInsertions,
                expectedModifications,
                expectedDeletions
            );
    }

}
