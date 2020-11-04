/*
 * File name: WingsIotConstansQueryImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2020年6月10日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.timer.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.run.locman.api.timer.query.repository.WingsIotConstansQueryRepository;
import com.run.locman.api.timer.query.service.WingsIotConstansQuery;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: Administrator
 * @version: 1.0, 2020年6月10日
 */
@Component
@EnableScheduling
public class WingsIotConstansQueryImpl implements WingsIotConstansQuery {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private WingsIotConstansQueryRepository	wingsIotConstansQueryRepository;

	// 用于存储产品和apiKey对应关系表
	private static Map<String, String>		productApiKeyTable;



	@Scheduled(fixedRate = 900 * 1000)
	protected void freshProductApiKeyTable() {
		try {
			logger.info("freshProductApiKeyTable()->进入方法");
			List<Map<String, Object>> res = wingsIotConstansQueryRepository.getProductApiKeyTable();
			productApiKeyTable = new HashMap<>();
			for (Map<String, Object> map1 : res) {
				productApiKeyTable.put((String) map1.get("productId"), (String) map1.get("ApiKey"));
			}
			logger.info("freshProductApiKeyTable()->success");
		} catch (Exception e) {
			logger.error("[freshProductApiKeyTable()->exception]", e);
		}
	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceReportedCrudService#getTransTable()
	 */
	@Override
	public Map<String, String> getProductApiKeyTable() {
		// TODO Auto-generated method stub
		return productApiKeyTable;
	}
}
