/*
 * File name: ActiveMqReceiveServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年10月19日 ...
 * ... ...
 *
 ***************************************************/

package com.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * @Description: springboot
 * @author: zhaoweizhi
 * @version: 1.0, 2018年10月19日
 */

@SpringBootApplication
@ComponentScan(basePackages = { "com.run" })
@ImportResource("classpath:config/spring-context.xml")
public class Main {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Main.class, args);
	}
}
