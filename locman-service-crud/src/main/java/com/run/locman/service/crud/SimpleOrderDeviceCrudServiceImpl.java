/*
 * File name: SimpleOrderDeviceCrudServiceImpl.java
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

package com.run.locman.service.crud;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.run.locman.api.crud.repository.SimpleOrderDeviceCrudRepository;
import com.run.locman.api.crud.service.SimpleOrderDeviceCrudService;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author:王胜
 * @version: 1.0, 2017年12月6日
 */

public class SimpleOrderDeviceCrudServiceImpl implements SimpleOrderDeviceCrudService {

	@SuppressWarnings("unused")
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@SuppressWarnings("unused")
	@Autowired
	private SimpleOrderDeviceCrudRepository	simpleOrderDeviceRepository;

}
