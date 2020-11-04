/*
 * File name: NewFaultOrderQueryRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhongbinyuan 2019年12月7日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: zhongbinyuan
 * @version: 1.0, 2019年12月7日
 */

public interface NewFaultOrderQueryRepository {

	List<Map<String, Object>> getFaultListInfo(Map<String, Object> map);

}
