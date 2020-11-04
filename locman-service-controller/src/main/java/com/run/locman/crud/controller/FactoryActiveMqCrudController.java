/*
 * File name: FactoryNewAMQPCrudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年10月23日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.crud.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.FactoryActiveMqCrudService;

/**
 * @Description:厂家启动activemq客户端
 * @author: zhaoweizhi
 * @version: 1.0, 2018年10月23日
 */
@RestController
@RequestMapping(UrlConstant.ACTIVEMQ_APPTAG)
@CrossOrigin(origins = "*")
public class FactoryActiveMqCrudController {

	@Autowired
	private FactoryActiveMqCrudService factoryActiveMqCrudService;



	@PostMapping(value = UrlConstant.ACTIVEMQ_APPTAG_START)
	public Result<Boolean> addStartConsumerAMQPS(@RequestBody Map<String, Object> appQueue) {
		return factoryActiveMqCrudService.addStartConsumerAMQPS(appQueue);
	}



	@PostMapping(value = UrlConstant.ACTIVEMQ_APPTAG_UPDATE)
	public Result<Boolean> updateStartConsumerAMQPS(@RequestBody Map<String, Object> appQueue) {
		return factoryActiveMqCrudService.updateStartConsumerAMQPS(appQueue);
	}

}
