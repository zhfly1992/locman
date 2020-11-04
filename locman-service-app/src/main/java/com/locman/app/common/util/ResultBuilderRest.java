package com.locman.app.common.util;

import com.run.entity.common.Result;
import com.run.entity.tool.ResultBuilder;

public class ResultBuilderRest extends ResultBuilder {

	/**
	 * rest访问Result返回成功
	* <method description>
	*
	* @param data
	* @param resultCode
	* @param msgDetail
	* @return
	 */
	public static <T> Result<T> successResultRest(T data, String resultCode, String msgDetail) {
		return getResult(data, resultCode, msgDetail, null);
	}
}
