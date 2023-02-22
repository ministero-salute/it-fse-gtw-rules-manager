package it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds;

import com.mongodb.client.MongoCollection;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IEDSClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.eds.base.db.impl.EDSTermsDB;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock.MockChunksExecutor;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants.Profile.TEST;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest
@ActiveProfiles(TEST)
@TestInstance(PER_CLASS)
public class EDSChunksExecutorTest {

    @SpyBean
    private MongoTemplate mongo;
    @MockBean
    private IEDSClient client;
    @Autowired
    private MockChunksExecutor executor;
    @Autowired
    private EDSTermsDB db;

    @BeforeAll
    public void init() { resetDB(); }

    @AfterEach
    public void reset() { resetDB(); }

    @Test
    void changeset() {}

    private void setupProduction() {
        assertDoesNotThrow(() -> db.setupTestRepository(executor.getConfig().getProduction()));
    }

    private MongoCollection<Document> getProduction() {
        return mongo.getCollection(executor.getConfig().getProduction());
    }

    private void resetDB() {
        mongo.dropCollection(executor.getConfig().getProduction());
        mongo.dropCollection(executor.getConfig().getStaging());
        assertFalse(mongo.collectionExists(executor.getConfig().getStaging()));
        assertFalse(mongo.collectionExists(executor.getConfig().getProduction()));
    }

}
