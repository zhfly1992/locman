/*
 * File name: FactoryQueryServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guolei 2017年9月15日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.query.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.dto.AppTagDto;
import com.run.locman.api.entity.Factory;
import com.run.locman.api.query.repository.FactoryQueryRepository;
import com.run.locman.api.query.service.FactoryQueryService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FactoryConstants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.PublicConstants;

/**
 *
 * @Description: 厂家query实现类
 * @author: guolei
 * @version: 1.0, 2017年9月15日
 */
public class FactoryQueryServiceImpl implements FactoryQueryService {
	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FactoryQueryRepository	factoryQueryRepository;
	/**
	 * 需要改ip
	 */
	@Value("${api.host}")
	private String					ip;



	@Override
	public RpcResponse<Map<String, Object>> findById(String id) {
		logger.info(String.format("[findById()方法执行开始...,参数：【%s】]", id));
		try {
			if (StringUtils.isBlank(id)) {
				logger.error("[findById()->invalid：参数id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("参数id不能为空");
			}
			Map<String, Object> factory = factoryQueryRepository.findMapById(id);
			if (!CollectionUtils.isEmpty(factory)) {
				logger.info(String.format("[findById()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("厂家查询成功", factory);
			}
			logger.info(String.format("[findById()方法执行结束!]"));
			return RpcResponseBuilder.buildErrorRpcResp("该厂家不存在");
		} catch (Exception e) {
			logger.error("findById()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	@Override
	public RpcResponse<List<String>> queryAppTagForAccessSecret(String accessSecret) {
		logger.info(String.format("[queryAppTagForAccessSecret()方法执行开始...,参数：【%s】]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[queryAppTagForAccessSecret()->invalid：查询appTag信息时,接入方秘钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询appTag信息：接入方秘钥不能为空！");
			}
			List<String> appTagList = factoryQueryRepository.queryAppTagForAccessSecret(accessSecret);
			logger.info(String.format("[queryAppTagForAccessSecret()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询appTag集合成功", appTagList);
		} catch (Exception e) {
			logger.error("queryAppTagForAccessSecret()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<List<String>> findAppTagForAccessSecret(String accessSecret) {
		logger.info(String.format("[findAppTagForAccessSecret()方法执行开始...,参数：【%s】]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[findAppTagForAccessSecret()->invalid：查询appTag信息时,接入方秘钥不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询appTag信息：接入方秘钥不能为空！");
			}
			List<String> appTagList = factoryQueryRepository.findAppTagForAccessSecret(accessSecret);
			logger.info(String.format("[findAppTagForAccessSecret()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询appTag集合成功", appTagList);
		} catch (Exception e) {
			logger.error("findAppTagForAccessSecret()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<List<String>> queryFactoryByAppTag(String appTag) {
		logger.info(String.format("[queryFactoryByAppTag()方法执行开始...,参数：【%s】]", appTag));
		try {
			if (StringUtils.isBlank(appTag)) {
				logger.error("[queryFactoryByAppTags()->invalid：查询厂家信息时,appTags不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询厂家信息：appTags不能为空！");
			}
			List<String> appTagList = factoryQueryRepository.queryFactoryByAppTag(appTag);
			logger.info(String.format("[queryFactoryByAppTag()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询厂家集合成功", appTagList);
		} catch (Exception e) {
			logger.error("queryFactoryByAppTag()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<List<Map<String, Object>>> queryFactoryInfoByAppTag(String appTag) {
		logger.info(String.format("[queryFactoryInfoByAppTag()方法执行开始...,参数：【%s】]", appTag));
		try {
			if (StringUtils.isBlank(appTag)) {
				logger.error("[queryFactoryByAppTags()->invalid：查询厂家信息时,appTags不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("查询厂家信息：appTags不能为空！");
			}
			List<Map<String, Object>> appTagList = factoryQueryRepository.queryFactoryInfoByAppTag(appTag);
			logger.info(String.format("[queryFactoryInfoByAppTag()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询厂家集合成功", appTagList);
		} catch (Exception e) {
			logger.error("queryFactoryInfoByAppTag()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FactoryQueryService#queryAllappTag()
	 */
	@Override
	public RpcResponse<List<Map<String, Object>>> queryAllappTag() {
		logger.info(String.format("[queryAllappTag()方法执行开始...]"));
		try {
			List<Map<String, Object>> appTagList = factoryQueryRepository.queryAllappTag();
			logger.info(String.format("[queryAllappTag()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询appTag集合成功！", appTagList);
		} catch (Exception e) {
			logger.error("queryAllappTag()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FactoryQueryService#queryAccessSecretByDeviceId(java.lang.String)
	 */
	@Override
	public RpcResponse<String> queryAccessSecretByDeviceId(String deviceId) {
		logger.info(String.format("[queryAccessSecretByDeviceId()方法执行开始...,参数：【%s】]", deviceId));
		if (StringUtils.isBlank(deviceId)) {
			logger.error("[queryAccessSecretByDeviceId()->invalid：设备id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空!");
		}
		try {
			String accessSecret = factoryQueryRepository.queryAccessSecretByDeviceId(deviceId);
			logger.info(String.format("[queryAccessSecretByDeviceId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询接入方秘钥成功!", accessSecret);
		} catch (Exception e) {
			logger.error("queryAccessSecretByDeviceId()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FactoryQueryService#findFactoryByDeviceId(java.lang.String)
	 */
	@Override
	public RpcResponse<Factory> findFactoryByDeviceId(String deviceId, String accessSecret) {
		logger.info(String.format("[findFactoryByDeviceId()方法执行开始...,参数：【%s】【%s】]", deviceId, accessSecret));
		if (StringUtils.isBlank(deviceId)) {
			logger.error("[findFactoryByDeviceId()->invalid：设备id不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("设备id不能为空!");
		}
		if (StringUtils.isBlank(accessSecret)) {
			logger.error("[findFactoryByDeviceId()->invalid：接入方密钥不能为空!]");
			return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空!");
		}
		Map<String, Object> queryMap = Maps.newHashMap();
		queryMap.put(FactoryConstants.DEVICES_ID, deviceId);
		queryMap.put(FactoryConstants.ACCESS_SECRET, accessSecret);
		try {
			Factory factoryInfo = factoryQueryRepository.findFactoryByDeviceId(queryMap);
			logger.info(String.format("[findFactoryByDeviceId()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("查询厂家信息成功!", factoryInfo);
		} catch (Exception e) {
			logger.error("findFactoryByDeviceId()->Exception:", e);
			return RpcResponseBuilder.buildErrorRpcResp(e.getMessage());
		}
	}



	@Override
	public RpcResponse<List<Map<String, String>>> queryFactoryNameList(String accessSecret) {
		logger.info(String.format("[queryFactoryNameList()->进入方法，参数accessSecret:%s]", accessSecret));
		try {
			List<Map<String, String>> result = factoryQueryRepository.getFactoryNameList(accessSecret);
			if (null != result && !result.isEmpty()) {
				logger.info("[queryFactoryNameList()->success]");
				return RpcResponseBuilder.buildSuccessRpcResp("查询厂家名称列表成功", result);
			} else {
				logger.info("[queryFactoryNameList()->success:无厂家信息]");
				return RpcResponseBuilder.buildSuccessRpcResp("", new ArrayList<>());
			}
		} catch (Exception e) {
			logger.error("[queryFactoryNameList()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FactoryQueryService#checkAppTagExist(com.run.locman.api.dto.AppTagDto,
	 *      java.lang.String, java.util.Set<String>)
	 */
	@Override
	public RpcResponse<Boolean> checkAppTagExist(AppTagDto appTagDto, String accessSecret,
			Set<String> checkParamGatewayExist, String token) {
		logger.info(String.format("[checkAppTagExist()->进入方法，参数appTagDto:%s]", appTagDto));
		try {
			String appKey = appTagDto.getAppKey();
			String appId = appTagDto.getAppId();
			if (StringUtils.isBlank(appKey)) {
				logger.error("[checkAppTagExist()appKey不能为空!!!]");
				return RpcResponseBuilder.buildErrorRpcResp("appKey不能为空!!!");
			}
			if (StringUtils.isBlank(appId)) {
				logger.error("[checkAppTagExist()appId不能为空!!!]");
				return RpcResponseBuilder.buildErrorRpcResp("appId不能为空!!!");
			}
			String appTag = appTagDto.getAppTag();

			int num = factoryQueryRepository.checkAppTagExist(appTagDto);
			if (num > 0) {
				logger.info("[checkAppTagExist()方法执行结束!]");
				return RpcResponseBuilder.buildSuccessRpcResp(String.format("该appKey:%s已存在,不能使用", appKey), false);
			} else {
				Map<String, String> map = Maps.newHashMap();
				map.put("appKey", appKey);
				map.put(InterGatewayConstants.TOKEN, token);
				// 查询绑定的网关列表,获取产品id以及产品id和网关id的关系
				String uri2 = new StringBuffer().append("/v1/app-push/applications/").append(appId)
						.append("/gateways?page=1&per_page=100").toString();
				String deviceTypeInfoByGet = InterGatewayUtil.getHttpValueByGet(uri2, ip, map);
				List<String> gatewayIdList = Lists.newArrayList();
				if (null == deviceTypeInfoByGet) {
					logger.error("[checkAppTagExist()-->验证appKey有效性失败!查询iotrest接口方法返回参数为null]");
					return RpcResponseBuilder.buildErrorRpcResp("验证appKey有效性失败!");
				} else {
					// 查询appKey绑定的所有网关id
					getGatewayIds(appId, map, deviceTypeInfoByGet, gatewayIdList);
				}

				// 没有绑定网关时,不必过多校验
				if (gatewayIdList.isEmpty()) {
					logger.info("[checkAppTagExist()方法执行结束!]");
					return RpcResponseBuilder.buildSuccessRpcResp("该appKey可以使用", true);
				}

				// 单对appTag时,无需校验参数之间绑定网关的重复性
				if (null != checkParamGatewayExist) {
					for (String gatewayId : gatewayIdList) {
						boolean add = checkParamGatewayExist.add(gatewayId);
						if (!add) {
							logger.info("[checkAppTagExist()方法执行结束!]");
							return RpcResponseBuilder.buildSuccessRpcResp(
									String.format("该appKey:%s绑定的网关%s在参数中重复,不能使用", appKey, gatewayId), false);
						}
					}
				}

				List<String> existGateway = factoryQueryRepository.checkGatewayIdExist(gatewayIdList, accessSecret,
						appTag);

				if (!existGateway.isEmpty()) {
					logger.info("[checkAppTagExist()方法执行结束!]");
					return RpcResponseBuilder.buildSuccessRpcResp(
							String.format("该appKey:%s绑定的网关%s已经存在故不能使用", appKey, existGateway), false);
				} else {
					logger.info("[checkAppTagExist()方法执行结束!]");
					return RpcResponseBuilder.buildSuccessRpcResp("该appKey可以使用", true);
				}
			}
		} catch (Exception e) {
			logger.error("[checkAppTagExist()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private void getGatewayIds(String appId, Map<String, String> map, String deviceTypeInfoByGet,
			List<String> gatewayIdList) {
		JSONObject deviceTypeInfoJson = JSON.parseObject(deviceTypeInfoByGet);
		int total = deviceTypeInfoJson.getIntValue("total");
		// 页大小,受接口限制,最大只能是100
		int pageSize = 100;
		if (total <= pageSize) {
			getGatewayIds(gatewayIdList, deviceTypeInfoJson);
		} else {
			int totalPage = (int) (total / pageSize + ((total % pageSize == 0) ? 0 : 1));
			for (int i = 1; i < totalPage + 1; i++) {
				String uri3 = new StringBuffer().append("/v1/app-push/applications/").append(appId)
						.append("/gateways?page=").append(i).append("&per_page=100").toString();
				String deviceTypeInfoByGet1 = InterGatewayUtil.getHttpValueByGet(uri3, ip, map);
				if (null == deviceTypeInfoByGet1) {
					continue;
				} else {
					JSONObject deviceTypeInfoJson1 = JSON.parseObject(deviceTypeInfoByGet1);
					getGatewayIds(gatewayIdList, deviceTypeInfoJson1);
				}
			}
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private void getGatewayIds(List<String> gatewayIdList, JSONObject deviceTypeInfoJson) {
		// gateways 网关集合信息
		JSONArray deviceTypeInfoArray = deviceTypeInfoJson.getJSONArray("gateways");
		List<AppTagDto> deviceTypeInfoArrayList = deviceTypeInfoArray.toJavaList(AppTagDto.class);
		for (AppTagDto deviceTypeInfo : deviceTypeInfoArrayList) {
			String gatewayId = deviceTypeInfo.getGatewayId();
			// 有效的网关
			if (StringUtils.isNotBlank(gatewayId)) {
				gatewayIdList.add(gatewayId);
			}

		}
	}



	/**
	 * @see com.run.locman.api.query.service.FactoryQueryService#getAppTagByFactoryId(java.lang.String)
	 */
	@Override
	public RpcResponse<List<AppTagDto>> getAppTagByFactoryId(String factoryId) {
		logger.info(String.format("[getAppTagByFactoryId()->进入方法，参数factoryId:%s]", factoryId));
		if (StringUtils.isBlank(factoryId)) {
			logger.error("[getAppTagByFactoryId()->厂家id不能为空!!!");
			return RpcResponseBuilder.buildErrorRpcResp("厂家id不能为空!!!");
		}
		try {
			List<AppTagDto> appTagList = factoryQueryRepository.getAppTagByFactoryId(factoryId);
			if (null == appTagList) {
				logger.info("[getAppTagByFactoryId()方法执行结束!]");
				return RpcResponseBuilder.buildErrorRpcResp("查询失败");
			} else {
				logger.info("[getAppTagByFactoryId()方法执行结束!]");
				return RpcResponseBuilder.buildSuccessRpcResp("appTag查询成功", appTagList);
			}
		} catch (Exception e) {
			logger.error("[getAppTagByFactoryId()->Exception:]", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FactoryQueryService#newFactoryList(java.lang.String,
	 *      int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getNewFactoryList(String accessSecret, int pageNum, int pageSize,
			String searchKey, String manageState) {
		logger.info(String.format(
				"[getNewFactoryList()方法执行开始...,参数：accessSecret【%s】pageNum【%s】pageSize【%s】searchKey【%s】manageState【%s】]",
				accessSecret, pageNum, pageSize, searchKey, manageState));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[getNewFactoryList()->invalid: 接入方密钥不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空");
			}
			PageHelper.startPage(pageNum, pageSize);
			HashMap<String, Object> queryParams = Maps.newHashMap();
			queryParams.put("accessSecret", accessSecret);
			queryParams.put("searchKey", searchKey);
			queryParams.put("manageState", manageState);
			List<Map<String, Object>> factoryList = factoryQueryRepository.findByAccessSecretNew(queryParams);
			PageInfo<Map<String, Object>> page = new PageInfo<>(factoryList);
			logger.info(String.format("[getNewFactoryList()方法执行结束!]"));
			return RpcResponseBuilder.buildSuccessRpcResp("厂家查询成功", page);
		} catch (Exception e) {
			logger.error("getNewFactoryList()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FactoryQueryService#findFactoryById(java.lang.String)
	 */
	@Override
	public RpcResponse<Map<String, Object>> findFactoryById(String id) {
		logger.info(String.format("[findFactoryById()方法执行开始...,参数：【%s】]", id));
		try {
			if (StringUtils.isBlank(id)) {
				logger.error("[findFactoryById()->invalid：参数id不能为空]");
				return RpcResponseBuilder.buildErrorRpcResp("参数id不能为空");
			}
			Map<String, Object> factory = factoryQueryRepository.findMapById(id);
			if (!CollectionUtils.isEmpty(factory)) {
				List<Map<String, String>> appKeyAppId = factoryQueryRepository.findAppKeyAppId(id);
				factory.put("appKeyAndId", appKeyAppId);
				logger.info(String.format("[findFactoryById()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("厂家查询成功", factory);
			}
			logger.info(String.format("[findFactoryById()方法执行结束!]"));
			return RpcResponseBuilder.buildErrorRpcResp("该厂家不存在");
		} catch (Exception e) {
			logger.error("findFactoryById()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	/**
	 * @see com.run.locman.api.query.service.FactoryQueryService#downBoxFactory(java.lang.String)
	 */
	@Override
	public RpcResponse<List<Map<String, String>>> downBoxFactory(String accessSecret) {
		logger.info(String.format("[downBoxFactory()方法执行开始...,参数：【%s】]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[downBoxFactory()->invalid：参数accessSecret不能为空!]");
				return RpcResponseBuilder.buildErrorRpcResp("参数accessSecret不能为空!");
			}

			List<Map<String, String>> downBoxFactory = factoryQueryRepository.downBoxFactory(accessSecret);
			return RpcResponseBuilder.buildSuccessRpcResp(PublicConstants.PARAM_SUCCESS, downBoxFactory);
		} catch (Exception e) {
			logger.error("downBoxFactory()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}

}
