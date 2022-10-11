package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.logging.LoggerHelper;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.IExecutorRepo;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public final class BridgeEDS {
    @Autowired
    private IExecutorRepo repository;
    @Autowired
    private IEDSClient client;
    @Autowired
    private LoggerHelper kafkaLogger;
}
