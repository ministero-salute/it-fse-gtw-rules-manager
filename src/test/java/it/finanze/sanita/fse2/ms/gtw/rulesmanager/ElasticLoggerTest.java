package it.finanze.sanita.fse2.ms.gtw.rulesmanager;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ILogEnum;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.OperationLogEnum;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ResultLogEnum;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.logging.ElasticLoggerHelper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {Constants.ComponentScan.BASE})
@ActiveProfiles(Constants.Profile.TEST)
class ElasticLoggerTest {

	@Autowired
	private ElasticLoggerHelper elasticLogger;

	@Test
	void contextLoads() {
	    elasticLogger.info("messaggio per elk", OperationLogEnum.CALL_EDS, ResultLogEnum.OK, new Date(), new Date());
	    elasticLogger.debug("messaggio ko", OperationLogEnum.MONGO, ResultLogEnum.KO, new Date(), new Date());
	    elasticLogger.warn("messaggio ok", OperationLogEnum.REDIS, ResultLogEnum.OK, new Date(), new Date());
	    elasticLogger.error("messaggio errore", OperationLogEnum.CALL_EDS, ResultLogEnum.OK, new Date(), new Date(), new ILogEnum() {

			@Override
			public String getCode() {
				return null;
			}

			@Override
			public String getDescription() {
				return null;
			}

	    });

	}
}
