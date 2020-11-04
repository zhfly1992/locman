package com.run.locman.query.service;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.run.locman.api.drools.service.TestInterface;
import com.secbro.drools.config.ReloadDroolsRules;
import com.secbro.drools.model.fact.AddressCheckResult;
import com.secbro.drools.utils.KieUtils;

@Service
public class TestInterfaceImpl implements TestInterface {

	@Autowired
	private ReloadDroolsRules rules;



	@Override
	public Boolean run(String id) {
		KieSession kieSession = KieUtils.getKieContainer().newKieSession();

		/*
		 * Address address = new Address(); address.setPostcode("99451");
		 */

		AddressCheckResult result = new AddressCheckResult();
		result.setPostCodeResult(true);
		/*
		 * result.setTest("99654"); kieSession.insert(address);
		 */
		kieSession.insert(result);
		@SuppressWarnings("unused")
		int ruleFiredCount = kieSession.fireAllRules();
		kieSession.dispose();
		if (!result.isPostCodeResult()) {
			return false;
		} else {
			return true;
		}

	}



	@Override
	public String reload() throws Exception {
		rules.reload();
		return "ok";
	}

}
