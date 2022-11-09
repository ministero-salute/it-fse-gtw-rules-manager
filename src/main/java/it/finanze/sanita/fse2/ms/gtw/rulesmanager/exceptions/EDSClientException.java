/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions;

/**
 * Exception Class to handle errors in resposnes from EDS Client.
 * 
 */
public class EDSClientException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Message constructor.
     * 
     * @param message Message to be logged.
     */
    public EDSClientException(String message) {
        super(message);
    }

    /**
     * Complete constructor.
     * 
     * @param message Message to be logged.
     * @param cause   Cause of the exception.
     */
    public EDSClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
