package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.executors;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClientV2;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.ICollectionsRepo;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public final class ExecutorBridgeEDS {
    @Autowired
    private ICollectionsRepo repository;
    @Autowired
    private IEDSClientV2 client;
}
