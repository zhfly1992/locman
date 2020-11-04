/*
 * File name: WingsIotConstansQueryRepository.java
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

package com.run.locman.api.timer.query.repository;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: zh
 * @version: 1.0, 2020年6月10日
 */

public interface WingsIotConstansQueryRepository {
	/**
	 * 
	 * @Description:获取产品表和
	 * @return
	 */
	public List<Map<String, Object>> getProductApiKeyTable();
}
