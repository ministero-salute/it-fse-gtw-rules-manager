package it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Arrays.stream;

@Component
public class ProfileUtility {
    @Autowired
    private Environment environment;

    public boolean isTestProfile() {
        // Get profiles
        String[] profiles = environment.getActiveProfiles();
        // Verify if exists the test profile
        Optional<String> exists = stream(profiles)
            .map(String::toLowerCase)
            .filter(i -> i.equals(Constants.Profile.TEST))
            .findFirst();
        // Return
        return exists.isPresent();
    }

    public boolean isDevProfile() {
        // Get profiles
        String[] profiles = environment.getActiveProfiles();
        // Verify if exists the test profile
        Optional<String> exists = stream(profiles)
            .map(String::toLowerCase)
            .filter(i -> i.equals(Constants.Profile.DEV))
            .findFirst();
        // Return
        return exists.isPresent();
    }
}
