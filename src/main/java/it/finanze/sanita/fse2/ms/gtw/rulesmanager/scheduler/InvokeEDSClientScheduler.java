package it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IEDSClientSRV;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

/**
 * 
 * Invoke EDS Client Scheduler, handles the invocations to EDS Client endpoints.
 * 
 */
@Slf4j
@Component
public class InvokeEDSClientScheduler {

    @Autowired
    private IEDSClientSRV edsClientSRV;

    @Autowired
	private Environment environment;

    @PostConstruct
    public void postConstruct() {
    	String profile = getActiveProfiles();
    	if(!Constants.Profile.TEST.equals(profile)) {
    		log.info("EDS CLIENT SCHEDULER - Executing postCostruct");
    		action();
    	}
    }
    
    @Scheduled(cron = "${scheduler.invoke-eds}")
	@SchedulerLock(name = "invokeEDSClientScheduler" , lockAtMostFor = "60m")
	public void action() {
		run(); 
	}

    public void run() {
        log.info("EDS CLIENT SCHEDULER - EDS Client Scheduler starting...");
        try {
            edsClientSRV.saveEDSConfigurationItems();
        } catch (Exception e) {
            log.error("EDS CLIENT SCHEDULER - Error executing InvokeEDSClientScheduler", e);
        }
        log.info("EDS CLIENT SCHEDULER - EDS Client Scheduler finished");
        
    }

    public String getActiveProfiles() {
		String out = null;
		if (environment!=null && environment.getActiveProfiles()!=null && environment.getActiveProfiles().length>0) {
			out= environment.getActiveProfiles()[0];
		}
		return out;
	}
    
}
