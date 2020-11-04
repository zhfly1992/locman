package com.secbro.drools.config;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class defaultLoadDrools implements ApplicationListener<ContextRefreshedEvent> {

	@Resource
    private ReloadDroolsRules rules;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		
		if(event.getApplicationContext().getParent() == null){//root application context 没有parent，他就是老大.
			try {
				rules.reload();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			}
		
	}


	

}
