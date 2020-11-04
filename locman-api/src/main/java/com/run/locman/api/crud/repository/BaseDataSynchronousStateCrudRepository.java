/*
 * File name: BaseDataSynchronousStateCrudRepository.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年4月25日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.crud.repository;

import com.run.locman.api.entity.BaseDataSynchronousState;
import com.run.locman.api.query.repository.BaseQueryRepository;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年4月25日
 */

public interface BaseDataSynchronousStateCrudRepository extends BaseCrudRepository<BaseDataSynchronousState, String>,
		BaseQueryRepository<BaseDataSynchronousState, String> {
	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	public BaseDataSynchronousState getSynchronousStateByAS(String accessSecret);



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	public int addSynchronousStateinfo(BaseDataSynchronousState baseDataSynchronousState);
}
