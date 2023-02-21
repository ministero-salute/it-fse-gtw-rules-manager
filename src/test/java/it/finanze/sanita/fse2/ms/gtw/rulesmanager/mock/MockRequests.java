package it.finanze.sanita.fse2.ms.gtw.rulesmanager.mock;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.RouteUtility.API_RUN_SCHEDULER_FULL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public final class MockRequests {
    public static MockHttpServletRequestBuilder runScheduler() {
        return post(API_RUN_SCHEDULER_FULL).contentType(MediaType.APPLICATION_JSON);
    }

}
