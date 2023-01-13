/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.error;


import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.dto.error.base.ErrorResponseDTO;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Logs.ERR_TASK_REJECTED;
import static org.apache.http.HttpStatus.SC_LOCKED;

/**
 * Builder class converting a given {@link Exception} into its own {@link ErrorResponseDTO} representation
 *
 */
public final class ErrorBuilderDTO {

    /**
     * Private constructor to disallow to access from other classes
     */
    private ErrorBuilderDTO() {}

    public static ErrorResponseDTO createTaskRejectedError(LogTraceInfoDTO trace) {
        // Return associated information
        return new ErrorResponseDTO(
            trace,
            ErrorType.IO.getType(),
            ErrorType.IO.getTitle(),
            ERR_TASK_REJECTED,
            SC_LOCKED,
            ErrorType.IO.toInstance(ErrorInstance.IO.QUEUE)
        );
    }
}
