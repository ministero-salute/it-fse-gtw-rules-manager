/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds;

/**
 * Mainly used to catch database issues, it's used to describe a data-layer failure
 * By-design MongoDB drivers uses {@link RuntimeException} which are difficult to handle
 * without a consistent compiler-enforcing policy (e.g Checked Exceptions)
 * To simplify the handling of operations issues, this class takes in the {@link com.mongodb.MongoException}
 * and add a reasonable descriptive message to find the routine which lead to the error.
 * This exception is supposed to be generated and re-thrown as soon as a {@link com.mongodb.MongoException} is caught.
 */
public class EdsDbException extends Exception {

    /**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 3549009136273681390L;

	/**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public EdsDbException(String message) {
        super(message);
    }

    /**
     * Message constructor.
     *
     * @param message Message to be logged.
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public EdsDbException(String message, Exception cause) {
        super(message, cause);
    }
}
