package it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds;

public class EdsSchedulerRunningException extends IllegalStateException {
    /**
     * Constructs an IllegalStateException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     *
     * @param s the String that contains a detailed message
     */
    public EdsSchedulerRunningException(String s) {
        super(s);
    }
}
