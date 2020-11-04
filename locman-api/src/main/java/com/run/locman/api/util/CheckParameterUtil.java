/*
* File name: CheckParameter.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			Administrator		2018年3月28日
* ...			...			...
*
***************************************************/

package com.run.locman.api.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.constants.PublicConstants;

/**
 * @Description: 基础参数校验
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月28日
 */

public class CheckParameterUtil {

	/**
	 * 
	 * @Description:基础必填参数校验
	 * @param logger
	 * @param methodName
	 * @param json
	 * @param keys
	 * 
	 */
	public static <T> RpcResponse<T> checkBusinessKey(Logger logger, String methodName, JSONObject json,
			String... keys) {
		for (String key : keys) {
			if (StringUtils.isBlank(json.getString(key))) {
				logger.error(String.format("[%s()->error:%s:%s]", methodName, PublicConstants.BUSINESS_INVALID, key));
				return RpcResponseBuilder
						.buildErrorRpcResp(String.format("[%s:%s]", PublicConstants.BUSINESS_INVALID, key));
			}
		}
		return null;
	}

	/**
	 * 
	 * @Description:分页参数校验
	 * @param logger
	 * @param methodName
	 * @param json
	 * @param keys
	 */
	public static <T> RpcResponse<T> checkPageKey(Logger logger, String methodName, JSONObject json, String... keys) {
		for (String key : keys) {
			if (!StringUtils.isNumeric(json.getString(key))) {
				logger.error(String.format("[%s()->error:%s:%s]", methodName, PublicConstants.PAGE_INVALID, key));
				return RpcResponseBuilder
						.buildErrorRpcResp(String.format("[%s:%s]", PublicConstants.PAGE_INVALID, key));
			}
		}
		return null;
	}

	/**
	 * 
	 * @Description:判断是否包含参数,可以为空
	 * @param logger
	 * @param methodName
	 * @param json
	 * @param keys
	 * @return
	 */
	public static <T> RpcResponse<T> containsParamKey(Logger logger, String methodName, JSONObject json,
			String... keys) {
		for (String key : keys) {
			if (!json.containsKey(key)) {
				logger.error(String.format("[%s()->error:%s:%s]", methodName, PublicConstants.PARAM_INVALID, key));
				return RpcResponseBuilder
						.buildErrorRpcResp(String.format("[%s:%s]", PublicConstants.PARAM_INVALID, key));
			}
		}
		return null;
	}

	/**
	 * 
	 * @Description:校验业务参数以及分页参数
	 * @param logger
	 * @param methodName
	 *            方法名
	 * @param obj
	 *            对象
	 * @param pageNot
	 *            是否校验分页参数
	 * @param keys
	 *            需要校验的业务参数key
	 * 
	 * @throws Exception
	 * 
	 */
	public static <T> RpcResponse<T> checkObjectBusinessKey(Logger logger, String methodName, Object obj,
			boolean pageNot, String... keys) throws Exception {

		// 校验对象
		if (obj == null) {
			return RpcResponseBuilder.buildErrorRpcResp(String.format("[%s:%s]", obj, PublicConstants.OBJ_NULL));
		}

		// 校验业务参数
		JSONObject jsonParam = (JSONObject) JSONObject.toJSON(obj);
		for (String key : keys) {
			
			if (StringUtils.isBlank(jsonParam.getString(key))) {
				logger.error(String.format("[%s()->error:%s:%s]", methodName, PublicConstants.BUSINESS_INVALID, key));
				return RpcResponseBuilder
						.buildErrorRpcResp(String.format("[%s:%s]", PublicConstants.BUSINESS_INVALID, key));
			}
		}

		// 校验分页参数
		if (pageNot) {
			if (!StringUtils.isNumeric(jsonParam.getString(PublicConstants.PUBLIC_PAGE_SIZE))
					|| !StringUtils.isNumeric(jsonParam.getString(PublicConstants.PUBLIC_PAGE_NUM))) {
				logger.error(String.format("[%s()->error:%s]", methodName, PublicConstants.PAGE_INVALID));
				return RpcResponseBuilder.buildErrorRpcResp(String.format("[%s]", PublicConstants.PAGE_INVALID));
			}
		}

		return null;
	}

	/**
	 * 
	 * @Description:校验业务参数以及分页参数
	 * @param logger
	 * @param methodName
	 *            方法名
	 * @param obj
	 *            对象
	 * 
	 * @param keys
	 *            需要校验的业务参数key
	 * 
	 * @throws Exception
	 * 
	 */
	public static <T> RpcResponse<T> checkObjectBusinessKey(Logger logger, String methodName, Object obj,
			String... keys) throws Exception {

		// 校验对象
		if (obj == null) {
			return RpcResponseBuilder.buildErrorRpcResp(String.format("[%s:%s]", obj, PublicConstants.OBJ_NULL));
		}

		// 校验业务参数
		JSONObject jsonParam = (JSONObject) JSONObject.toJSON(obj);
		for (String key : keys) {
			if (StringUtils.isBlank(jsonParam.getString(key))) {
				logger.error(String.format("[%s()->error:%s:%s]", methodName, PublicConstants.BUSINESS_INVALID, key));
				return RpcResponseBuilder
						.buildErrorRpcResp(String.format("[%s:%s]", PublicConstants.BUSINESS_INVALID, key));
			}
		}

		return null;
	}

}
