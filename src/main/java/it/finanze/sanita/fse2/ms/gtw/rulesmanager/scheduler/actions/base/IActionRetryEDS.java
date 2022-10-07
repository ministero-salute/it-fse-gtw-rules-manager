package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.actions.base;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors.base.ExecutorEDS;
import org.slf4j.Logger;

import java.util.Optional;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.eds.changeset.ChangesetCFG.RETRY_VALUE;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ActionRes.KO;
import static java.lang.String.format;

public interface IActionRetryEDS {
	
    static <T> Optional<T> retryOnException(IActionFnEDS<T> fn, ChangesetCFG config, Logger log, int times) {
        // Working var
        Optional<T> obj = Optional.empty();
        // Iterate until object is present or by max retryOnException value
        for (int i = 0; i < times && !obj.isPresent(); ++i) {
            try {
                // Retrieve object
                obj = Optional.ofNullable(fn.get());
            } catch (Exception e) {
                // Log me
                log.error(
                    format("[%s][#%s] Retrying operation due to exception", config.getTitle(), i + 1),
                    e
                );
            }
        }
        return obj;
    }

    static <T> Optional<T> retryOnException(IActionFnEDS<T> fn, ChangesetCFG config, Logger log) {
        return retryOnException(fn, config, log, RETRY_VALUE);
    }

    static ActionRes retryExecutorOnException(ExecutorEDS<?> executor, Logger log, int times) {
        // Working var
        ActionRes res = KO;
        // Execute
        for (int i = 0; i <= times && res == KO; i++) {
            // Invoke
            res = executor.execute();
            // Log me
            if(res == KO && i != times) {
                log.error("[Executor][#{}] Restarting executor due to exception", i + 1);
            }
        }
        // Bye
        return res;
    }

    static ActionRes retryExecutorOnException(ExecutorEDS<?> executor, Logger log) {
        return retryExecutorOnException(executor, log, RETRY_VALUE);
    }
}
