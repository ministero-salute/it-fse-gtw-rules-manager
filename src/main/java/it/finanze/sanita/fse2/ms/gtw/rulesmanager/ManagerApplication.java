package it.finanze.sanita.fse2.ms.gtw.rulesmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class ManagerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ManagerApplication.class, args);
	}
}
