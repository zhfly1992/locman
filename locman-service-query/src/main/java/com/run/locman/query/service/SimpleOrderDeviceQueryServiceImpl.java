/*
 * File name: SimpleOrderDeviceQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2017年12月6日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.query.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.run.locman.api.query.repository.SimpleOrderDeviceQueryRepository;
import com.run.locman.api.query.service.SimpleOrderDeviceQueryService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description: 一般流程与设备绑定关系query实现类
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */

public class SimpleOrderDeviceQueryServiceImpl implements SimpleOrderDeviceQueryService {

	@SuppressWarnings("unused")
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	SimpleOrderDeviceQueryRepository	simpleOrderDeviceRepository;

}
