package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.impl.multi;

public final class StructureAction {

    private StructureAction() {}

    /**
     * Retrieve changeset using production parent last-sync value
     */
    public static final String CHANGESET_PARENT = "CHANGESET_PARENT";
    /**
     * Clone the parent production repository into a staging-one
     */
    public static final String STAGING_PARENT = "STAGING_PARENT";
}
