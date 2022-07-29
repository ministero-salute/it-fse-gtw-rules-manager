package it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds;

public class EdsClientException extends Exception {
    /**
     * Message constructor.
     *
     * @param message Message to be logged.
     * @param cause the cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A {@code null} value is
     *        permitted, and indicates that the cause is nonexistent or
     *        unknown.)
     */
    public EdsClientException(String message, Exception cause) {
        super(message, cause);
    }
}
