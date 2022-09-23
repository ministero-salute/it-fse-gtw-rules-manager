package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.util;

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

}
