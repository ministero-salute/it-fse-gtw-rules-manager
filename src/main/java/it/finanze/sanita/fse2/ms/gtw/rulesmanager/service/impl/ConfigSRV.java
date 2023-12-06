package it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.impl;

import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.routes.base.ClientRoutes.Config.PROPS_NAME_CONTROL_LOG_ENABLED;
import static it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ConfigItemTypeEnum.GENERIC;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.client.IConfigClient;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.service.IConfigSRV;

@Service
public class ConfigSRV implements IConfigSRV {

    private static final long DELTA_MS = 300_000L;

    @Autowired
    private IConfigClient client;

    private long lastUpdate;
    
	private final Map<String, Pair<Long, Object>> props;

	public ConfigSRV() {
		this.props = new HashMap<>();
	}

    
    @PostConstruct
    public void postConstruct() {
    	for(ConfigItemTypeEnum en : ConfigItemTypeEnum.values()) {
    		for(Entry<String, String> el : client.getConfigurationItems(en).getItems().entrySet()) {
        		props.put(el.getKey(), Pair.of(new Date().getTime(), el.getValue()));
        	}
    	}
    }

    
    
    private void refreshControlLogPersistenceEnable() {
        Boolean previous = props.get(PROPS_NAME_CONTROL_LOG_ENABLED)!=null ? (Boolean)props.get(PROPS_NAME_CONTROL_LOG_ENABLED).getValue() : null;
        Boolean controlLogEnabled = (Boolean)client.getProps(GENERIC, PROPS_NAME_CONTROL_LOG_ENABLED,previous);
		props.put(PROPS_NAME_CONTROL_LOG_ENABLED, Pair.of(new Date().getTime(), controlLogEnabled));
        
    }
    

    @Override
    public Boolean isControlLogPersistenceEnable() {
        if (new Date().getTime() - lastUpdate >= DELTA_MS) {
            synchronized(ConfigSRV.class) {
            	if (new Date().getTime() - lastUpdate >= DELTA_MS) {
            		refreshControlLogPersistenceEnable();	
            	}
            }
        }
        return (Boolean)props.get(PROPS_NAME_CONTROL_LOG_ENABLED).getValue();
    }

  
}
