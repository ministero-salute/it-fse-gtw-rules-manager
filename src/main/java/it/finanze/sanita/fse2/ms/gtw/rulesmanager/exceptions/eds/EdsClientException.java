/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds;

public class EdsClientException extends Exception {
	
    /**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -7988139678079793606L;

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
