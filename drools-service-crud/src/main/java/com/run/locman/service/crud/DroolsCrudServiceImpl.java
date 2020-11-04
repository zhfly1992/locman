/*
 * File name: DroolsCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2017年10月16日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.DroolsRepository;
import com.run.locman.api.crud.service.DroolsCrudSerivce;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.MessageConstant;
import com.run.locman.constants.RuleContants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @Description:规则crud类
 * @author: zhabing
 * @version: 1.0, 2017年10月16日
 */

public class DroolsCrudServiceImpl implements DroolsCrudSerivce {

	private static final Logger	logger	= Logger.getLogger(DroolsCrudServiceImpl.class);

	@Autowired
	private DroolsRepository	droolsRepository;

	@Autowired
	private MongoTemplate tenementTemplate;


	/**
	 *
	 * @param map
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RpcResponse<Map<String, Object>> addDrools(Map map) {
		try {
			logger.info(String.format("[addDrools()->进入方法,参数:%s]", map));
//			String id = UUIDUtil.getUUID();
			tenementTemplate.insert(map,"AlarmInfo");
//			map.put(CommonConstants.ID, id);
//			// 参数校验
//			String rule = map.getString(RuleContants.RULE);
//			String ruleName = map.getString(RuleContants.RULE_NAME);
//
//			if (StringUtils.isEmpty(ruleName)) {
//				logger.debug(String.format("[addDrools()->fail:%s]", MessageConstant.NO_BUSINESS));
//				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.NO_BUSINESS);
//			}
//			if (StringUtils.isEmpty(rule)) {
//				logger.debug(String.format("[addDrools()->fail:%s]", MessageConstant.NO_BUSINESS));
//				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.NO_BUSINESS);
//			}
//			tenementTemplate.insert(map);
//			droolsRepository.addDrools(map);
			return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.FACILITIES_OPERATE_SUCCESS, map);

		} catch (Exception e) {
			logger.error(String.format("[saveAccSourceInfo()->exception:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<Map<String, Object>> updateDrools(Map map) {
		logger.info(String.format("[updateDrools()->进入方法,参数:%s]", map));
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("id").is(map.get("id")));
			Update update = new Update();
			update.set("id",map.get("id"));
			update.set("accessSecret",map.get("accessSecret"));
			update.set("alarmLevel",map.get("alarmLevel"));
			update.set("crateTime",map.get("crateTime"));
			update.set("deviceTypeId",map.get("deviceTypeId"));
			update.set("isDelete",map.get("isDelete"));
			update.set("manageState",map.get("manageState"));
			update.set("oderNum",map.get("oderNum"));
			update.set("publishState",map.get("publishState"));
			update.set("remark",map.get("remark"));
			update.set("rule",map.get("rule"));
			update.set("ruleContent",map.get("ruleContent"));
			update.set("ruleName",map.get("ruleName"));
			update.set("updateTime",map.get("updateTime"));
			update.set("userId",map.get("userId"));
			tenementTemplate.updateFirst(query, update,"AlarmInfo");
			// 参数校验
			String id = map.get(CommonConstants.ID).toString();
			String rule = map.get(RuleContants.RULE).toString();
			String ruleName = map.get(RuleContants.RULE_NAME).toString();

			if (StringUtils.isEmpty(ruleName)) {
				logger.debug(String.format("[updateDrools()->fail:%s]", MessageConstant.NO_BUSINESS));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(rule)) {
				logger.debug(String.format("[updateDrools()->fail:%s]", MessageConstant.NO_BUSINESS));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(id)) {
				logger.debug(String.format("[updateDrools()->fail:%s]", MessageConstant.NO_BUSINESS));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.NO_BUSINESS);
			}
			int result = droolsRepository.updateDrools(map);
			if (result > 0) {
				logger.info(String.format("[updateDrools()->:%s]", MessageConstant.FACILITIES_OPERATE_SUCCESS));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.FACILITIES_OPERATE_SUCCESS, map);
			} else {
				logger.error(String.format("[updateDrools()->:%s]", MessageConstant.FACILITIES_OPERATE_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.FACILITIES_OPERATE_FAIL);
			}

		} catch (Exception e) {
			logger.error(String.format("[updateDrools()->exception:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	@Override
	public RpcResponse<JSONObject> swateDroolsState(String id, String state) {
		try {
			logger.info(String.format("[updateDrools()->进入方法,参数:id:%s,state:%s]", id,state));
			// 参数校验
			if (StringUtils.isEmpty(id)) {
				logger.debug(String.format("[swateDroolsState()->fail:%s]", MessageConstant.NO_BUSINESS));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.NO_BUSINESS);
			}
			if (StringUtils.isEmpty(state)) {
				logger.debug(String.format("[swateDroolsState()->fail:%s]", MessageConstant.NO_BUSINESS));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.NO_BUSINESS);
			}
			JSONObject map = new JSONObject();
			map.put(RuleContants.STATE, state);
			map.put(CommonConstants.ID, id);
			int result = droolsRepository.updateDrools(map);
			if (result > 0) {
				logger.info(String.format("[swateDroolsState()->:%s]", MessageConstant.FACILITIES_OPERATE_SUCCESS));
				return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.FACILITIES_OPERATE_SUCCESS, map);
			} else {
				logger.error(String.format("[swateDroolsState()->:%s]", MessageConstant.FACILITIES_OPERATE_FAIL));
				return RpcResponseBuilder.buildErrorRpcResp(MessageConstant.FACILITIES_OPERATE_FAIL);
			}

		} catch (Exception e) {
			logger.error(String.format("[swateDroolsState()->exception:%s]", e.getMessage()));
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}

}
