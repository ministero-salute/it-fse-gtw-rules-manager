package it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.controller.ITestCTL;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.scheduler.InvokeEDSClientScheduler;

@RestController
public class TestCTL implements ITestCTL {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 3260806709541019186L;

	
	@Autowired
	private InvokeEDSClientScheduler edsClientScheduler;
	

	@Override
	public void runScheduler(HttpServletRequest request) {
		edsClientScheduler.action();
	}

	
}
