/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.eds.EdsDbException;

public interface ITerminologySRV {
    void applyIndexes(String collection) throws EdsDbException;
}
