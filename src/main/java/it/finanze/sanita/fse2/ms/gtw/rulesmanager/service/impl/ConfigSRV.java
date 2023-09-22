package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;


import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IConfigClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.EdsStrategyEnum;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IConfigSRV;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ConfigSRV implements IConfigSRV {

	private static final Long DELTA_MS = 300000L;

	@Autowired
	private IConfigClient client;

	private String edsStrategy;

	private long lastUpdate;

	@Async
	@EventListener(ApplicationStartedEvent.class)
	void initialize() {
		refreshEdsStrategy();
	}

	private void refreshEdsStrategy() {
		synchronized (this) {
			edsStrategy = client.getEDSStrategy();
			lastUpdate = new Date().getTime();
		}
	}

	@Override
	public String getEdsStrategy() {
		long passedTime = new Date().getTime() - lastUpdate;
		if (passedTime >= DELTA_MS) {
			synchronized (this) {
				refreshEdsStrategy();
			}
		}
		return edsStrategy;
	}


	@Override
	public boolean isNoEdsWithLogs() {
		// Trigger refresh if necessary
		String out = getEdsStrategy();
		// Evaluate
		return StringUtils.isNotBlank(out) || EdsStrategyEnum.NO_EDS_WITH_LOG.name().equalsIgnoreCase(out);
	}
 
}
