/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.logging;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * A strategy that can create byte array key for a given {@link ILoggingEvent}.
 */
public interface KeyingStrategy<E> {

    /**
     * creates a byte array key for the given {@link ILoggingEvent}
     * @param e the logging event
     * @return a key
     */
    byte[] createKey(E e);

}