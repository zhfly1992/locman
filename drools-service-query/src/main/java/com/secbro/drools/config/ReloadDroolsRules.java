package com.secbro.drools.config;

import com.run.locman.api.query.repository.DroolsRepository;
import com.secbro.drools.utils.KieUtils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by neo on 17/7/31.
 */

@Service
public class ReloadDroolsRules {

	@Autowired
	private DroolsRepository droolsRepository;



	public void reload(String rule) throws UnsupportedEncodingException {
		KieServices kieServices = KieServices.Factory.get();
		KieFileSystem kfs = kieServices.newKieFileSystem();
		kfs.write("src/main/resources/rules/temp.drl", rule);
		KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
		Results results = kieBuilder.getResults();
		if (results.hasMessages(Message.Level.ERROR)) {
			throw new IllegalStateException("### errors ###");
		}
		KieUtils.setKieContainer(kieServices.newKieContainer(getKieServices().getRepository().getDefaultReleaseId()));
	}



	public void reload() throws UnsupportedEncodingException {
		KieServices kieServices = KieServices.Factory.get();
		KieFileSystem kfs = kieServices.newKieFileSystem();
		kfs.write("src/main/resources/rules/temp.drl", loadRules());
		KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
		Results results = kieBuilder.getResults();
		if (results.hasMessages(Message.Level.ERROR)) {
			throw new IllegalStateException("### errors ###");
		}

		KieUtils.setKieContainer(kieServices.newKieContainer(getKieServices().getRepository().getDefaultReleaseId()));
	}



	private String loadRules() throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		sb.append("package plausibcheck.adress \n ");

		// 从数据库加载的规则
		List<Map<String, String>> maps = droolsRepository.getAllDrools();
		if (maps != null && !maps.isEmpty()) {
			for (Map<String, String> map : maps) {
				sb.append(map.get("rule"));
			}
		}
		/*
		 * sb.
		 * append("import com.secbro.drools.model.Address; \n import com.secbro.drools.model.fact.TestDiaoYong; \n rule \"Postcode1\" \n when \n address : Address(postcode != null, postcode matches \"([0-9]{5})\") \n  then \n System.out.println(\"第一个规则：logs in rule ：check success123!\");\n end\n"
		 * ); sb.
		 * append("import com.secbro.drools.model.Address; \n import com.secbro.drools.model.fact.TestDiaoYong; \n rule \"Postcode2\" \n when \n address : Address(postcode != null, postcode matches \"([0-9]{5})\") \n  then \n System.out.println(\"第二个规则：logs in rule ：check success123!\");\n end\n"
		 * ); sb.
		 * append("import com.secbro.drools.model.Address; \n import com.secbro.drools.model.fact.TestDiaoYong; \n rule \"Postcode3\" \n when \n address : Address(postcode != null, postcode matches \"([0-9]{5})\") \n  then \n System.out.println(\"第三个规则：logs in rule ：check success123!\");\n end\n"
		 * );
		 */
		return StringEscapeUtils.unescapeJava(sb.toString());
	}



	private KieServices getKieServices() {
		return KieServices.Factory.get();
	}

}
