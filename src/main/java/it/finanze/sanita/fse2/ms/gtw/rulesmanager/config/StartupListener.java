package it.finanze.sanita.fse2.ms.gtw.rulesmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.repository.entity.TerminologyETY;

@Component
public class StartupListener {

    
    @Autowired
    private MongoTemplate mongoTemplate;
     

	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
		ensureIndexes();
	}

	private void ensureIndexes() {
		mongoTemplate.indexOps(TerminologyETY.class).ensureIndex(new Index().on("system", Direction.DESC).on("code", Direction.DESC).background());
	}
	 
}
