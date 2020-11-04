/*
 * File name: FactoryRestCrudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 李康诚 2017年11月20日 ... ... ...
 *
 ***************************************************/
package com.run.locman.crud.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.run.common.util.ParamChecker;
import com.run.common.util.UUIDUtil;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.DeviceInfoCudService;
import com.run.locman.api.crud.service.FactoryCrudService;
import com.run.locman.api.dto.AppTagDto;
import com.run.locman.api.dto.DeviceTypeInfoDto;
import com.run.locman.api.entity.Device;
import com.run.locman.api.entity.DeviceType;
import com.run.locman.api.entity.Factory;
import com.run.locman.api.query.service.FactoryQueryService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceTypeContants;
import com.run.locman.constants.FactoryConstants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.LogMessageContants;

/**
 * @Description: 厂家添加修改
 * @author: 李康诚
 * @version: 1.0, 2017年11月20日
 */
@Service
@EnableApolloConfig
public class FactoryRestCrudService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private FactoryCrudService		factoryCrudService;

	@Autowired
	private DeviceInfoCudService	deviceInfoCudService;

	@Autowired
	private FactoryQueryService		factoryQueryService;

	@Value("${api.host}")
	private String					ip;

	@Autowired
	private HttpServletRequest		request;

	private String					appKey;



	/**
	 * 分割List
	 * 
	 * @param list
	 *            待分割的list
	 * @param pageSize
	 *            每段list的大小
	 * @return List<<List<T>>
	 */
	public <T> List<List<T>> splitList(List<T> list, int pageSize) {
		// list的大小
		int listSize = list.size();
		// 页数
		int page = (listSize + (pageSize - 1)) / pageSize;
		// 创建list数组,用来保存分割后的list
		List<List<T>> listArray = new ArrayList<List<T>>();
		// 按照数组大小遍历
		for (int i = 0; i < page; i++) {
			// 数组每一位放入一个分割后的list
			List<T> subList = new ArrayList<T>();
			// 遍历待分割的list
			for (int j = 0; j < listSize; j++) {
				// 当前记录的页码(第几页)
				int pageIndex = ((j + 1) + (pageSize - 1)) / pageSize;
				// 当前记录的页码等于要放入的页码时
				if (pageIndex == (i + 1)) {
					// 放入list中的元素到分割后的list(subList)
					subList.add(list.get(j));
				}
				// 当放满一页时退出当前循环
				if ((j + 1) == ((j + 1) * pageSize)) {
					break;
				}
			}
			// 将分割后的list放入对应的数组的位中
			listArray.add(subList);
		}
		return listArray;
	}



	/**
	 * 
	 * @Description: 添加厂家(新)
	 * @param
	 * @return
	 */
	public Result<String> addFactory(String accessSecret, String addParams) {
		logger.info(
				String.format("[addFactory()->request params--addParams:%s,accessSecret:%s]", addParams, accessSecret));
		if (ParamChecker.isBlank(addParams) || ParamChecker.isNotMatchJson(addParams)) {
			logger.error(String.format("[addFactory()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
					LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject paramsJson = JSON.parseObject(addParams);
			String userId = getUserId();
			if (StringUtils.isBlank(userId)) {
				logger.error("添加厂家失败,人员验证失败或未找到人员信息!");
				return ResultBuilder.failResult("添加厂家失败,人员验证失败或未找到人员信息!");
			}

			JSONArray appTagInfoArray = paramsJson.getJSONArray("appTag");
			List<AppTagDto> appTagDtoList = appTagInfoArray.toJavaList(AppTagDto.class);
			List<String> checkParamAppTagExist = Lists.newArrayList();
			// 校验appTag参数绑定的网关是否重复
			Set<String> checkParamGatewayExist = Sets.newHashSet();
			// 检查appTag是否重复
			for (AppTagDto appTagInfo : appTagDtoList) {
				// 校验参数中是否存在重复的appTag
				String appTag = appTagInfo.getAppTag();
				if (!checkParamAppTagExist.contains(appTag)) {
					checkParamAppTagExist.add(appTag);
				} else {
					logger.error(String.format("[addFactory()->请求参数中appId-appKey重复(%s-%s),不能添加!]",
							appTagInfo.getAppId(), appTagInfo.getAppKey()));
					return ResultBuilder.failResult(String.format("请求参数中appId-appKey重复(%s-%s),不能添加!",
							appTagInfo.getAppId(), appTagInfo.getAppKey()));
				}
				String token = request.getHeader(InterGatewayConstants.TOKEN);
				RpcResponse<Boolean> checkAppTag = factoryQueryService.checkAppTagExist(appTagInfo, accessSecret,
						checkParamGatewayExist, token);

				if (checkAppTag.isSuccess() && !checkAppTag.getSuccessValue()) {
					logger.error("[addFactory()-> error:" + checkAppTag.getMessage() + "]");
					return ResultBuilder.failResult(checkAppTag.getMessage());
				} else if (!checkAppTag.isSuccess()) {
					logger.error(String.format("[addFactory()->error:%s]", checkAppTag.getMessage()));
					return ResultBuilder.failResult(checkAppTag.getMessage());
				}

			}
			// 存储设备基础数据
			List<Device> devices = Lists.newArrayList();
			// 存储设备类型数据
			List<DeviceType> deviceTypes = Lists.newArrayList();

			synchroData(accessSecret, userId, appTagDtoList, devices, deviceTypes);
			paramsJson.remove("appTag");
			Factory factory = JSON.parseObject(paramsJson.toJSONString(), Factory.class);
			factory.setAccessSecret(accessSecret);
			RpcResponse<String> addFactory = factoryCrudService.addFactory(factory, devices, deviceTypes, appTagDtoList,
					accessSecret);
			if (addFactory.isSuccess()) {
				logger.info("[addFactory()->success:添加厂家成功]");
				return ResultBuilder.successResult(addFactory.getSuccessValue(), "添加厂家成功!");
			} else {
				logger.error(String.format("[addFactory()->fail:添加厂家失败:%s]", addFactory.getMessage()));
				return ResultBuilder.failResult(addFactory.getMessage());
			}

		} catch (Exception e) {
			logger.error("addFactory()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:从请求头获取用户id(需要已经在网关中查询并传参)
	 * @param
	 * @return
	 */

	private String getUserId() {

		String userId = request.getHeader("userId");

		if (userId != null) {
			logger.info("[getUserId()-->userId获取成功:" + userId + "]");
			return userId;
		} else {
			logger.error("[getUserId()-->userId获取失败]");
			return "";
		}
	}



	/**
	 * 
	 * @Description: 同步数据,包括设备基础数据,设备类型数据
	 * @param accessSecret:接入方密钥
	 * @param userId:设备类型创建人,修改人Id
	 * @param appTagDtoList:appTag信息集合
	 * @param devices:设备基础数据存储
	 * @param deviceTypes:设备类型基础数据存储
	 * @param gatewayIdRsProductId:存储产品id和网关id关系,用来给设备类型id赋值
	 * @return
	 * @throws Exception 
	 */

	private void synchroData(String accessSecret, String userId, List<AppTagDto> appTagDtoList, List<Device> devices,
			List<DeviceType> deviceTypes) throws Exception {
		// 存储不重复的产品id
		Set<String> deviceTypeIds = Sets.newHashSet();
		// 存储产品id和网关id关系,用来给设备类型id赋值
		Map<String, String> gatewayIdRsProductId = Maps.newHashMap();
		// 获取到一组可用的appId和appKey
		String appId = null;
		String appKey = null;
		for (AppTagDto appTagInfo : appTagDtoList) {
			if (StringUtils.isBlank(appTagInfo.getAppId()) || StringUtils.isBlank(appTagInfo.getAppKey())) {
				continue;
			}
			appTagInfo.setId(UUIDUtil.getUUID());
			Map<String, String> map = Maps.newHashMap();
			String id = appTagInfo.getAppId();
			String key = appTagInfo.getAppKey();
			map.put("appKey", key);
			map.put(InterGatewayConstants.TOKEN, request.getHeader(InterGatewayConstants.TOKEN));
			// 存储网关id
			List<String> gatewayIdList = Lists.newArrayList();
			// 查询单组appId,appKey绑定的设备
			String uri = new StringBuffer().append("/v1/app-push/applications/").append(id)
					.append("/subThings?page=1&per_page=100").toString();
			String deviceInfoByGet = InterGatewayUtil.getHttpValueByGet(uri, ip, map);
			if (null == deviceInfoByGet) {
				
				throw new Exception("查询平台数据失败或返回值为空");
			} else {
				JSONObject deviceInfo = JSON.parseObject(deviceInfoByGet);
				// total 总数量
				int total = deviceInfo.getIntValue("total");
				// 页大小,受接口限制,最大只能是100
				int pageSize = 100;
				this.appKey = key;
				if (total <= pageSize) {
					dealData(accessSecret, devices, appTagInfo, gatewayIdList, deviceInfo);
				} else {
					dealData(accessSecret, devices, appTagInfo, map, gatewayIdList, total);
				}
			}

			// 查询绑定的网关列表,获取产品id以及产品id和网关id的关系
			String uri2 = new StringBuffer().append("/v1/app-push/applications/").append(id)
					.append("/gateways?page=1&per_page=100").toString();
			String deviceTypeInfoByGet = InterGatewayUtil.getHttpValueByGet(uri2, ip, map);
			if (null == deviceTypeInfoByGet) {
				throw new Exception("查询平台数据失败或返回值为空");
			} else {
				// 解析数据,获取产品id以及产品id和网关id的关系
				getGatewayRsProductId(accessSecret, userId, deviceTypeIds, gatewayIdRsProductId, appTagInfo, map,
						gatewayIdList, deviceTypeInfoByGet);
				// 能运行到此的id和key一定是有效的,用作循环查询所有的产品信息
				if (null == appId || null == appKey) {
					appId = appTagInfo.getAppId();
					appKey = appTagInfo.getAppKey();
				}
			}
		}

		Map<String, DeviceTypeInfoDto> deviceIdRsInfo = Maps.newHashMap();

		if (StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(appKey) && !deviceTypeIds.isEmpty()) {
			for (String deviceTypeId : deviceTypeIds) {
				getProductInfo(accessSecret, userId, deviceTypes, appId, appKey, deviceIdRsInfo, deviceTypeId);
			}
		}

		// 组装设备的设备类型,协议类型,开放/私有协议
		for (Device device : devices) {
			String deviceTypeId = gatewayIdRsProductId.get(device.getGatewayId());
			device.setDeviceType(deviceTypeId);
			DeviceTypeInfoDto deviceTypeInfoDto = deviceIdRsInfo.get(deviceTypeId);
			if (null != deviceTypeInfoDto) {
				device.setProtocolType(deviceTypeInfoDto.getProductProtocolType());
				device.setOpenProtocols(deviceTypeInfoDto.getProductLevel());
			}
		}

	}



	/**
	 * 
	 * @Description: 根据appKey,appId和产品id查询产品信息并封装到设备类型集合
	 * @param
	 * @return
	 */

	private void getProductInfo(String accessSecret, String userId, List<DeviceType> deviceTypes, String appId,
			String appKey, Map<String, DeviceTypeInfoDto> deviceIdRsInfo, String deviceTypeId) {

		Map<String, String> map = Maps.newHashMap();
		map.put("appKey", appKey);
		map.put(InterGatewayConstants.TOKEN, request.getHeader(InterGatewayConstants.TOKEN));
		String getProductInfoUrl = new StringBuffer().append("/v1/app-push/applications/").append(appId)
				.append("/products/").append(deviceTypeId).toString();
		String deviceTypeInfoByGet = InterGatewayUtil.getHttpValueByGet(getProductInfoUrl, ip, map);
		if (null == deviceTypeInfoByGet) {

			return;
		} else {
			putDeviceType(accessSecret, userId, deviceTypes, deviceIdRsInfo, deviceTypeId, deviceTypeInfoByGet);
		}
	}



	/**
	 * 
	 * @Description: 获取网关与产品id的关系
	 * @param
	 * @return
	 */

	private void getGatewayRsProductId(String accessSecret, String userId, Set<String> deviceTypeIds,
			Map<String, String> gatewayIdRsProductId, AppTagDto appTagInfo, Map<String, String> map,
			List<String> gatewayIdList, String deviceTypeInfoByGet) {
		JSONObject deviceTypeInfoJson = JSON.parseObject(deviceTypeInfoByGet);
		int total = deviceTypeInfoJson.getIntValue("total");
		// 页大小,受接口限制,最大只能是100
		int pageSize = 100;
		if (total <= pageSize) {
			Map<String, String> dealDeviceType = dealDeviceType(deviceTypeIds, gatewayIdList, deviceTypeInfoJson,
					accessSecret);
			gatewayIdRsProductId.putAll(dealDeviceType);
		} else {
			Map<String, String> dealDeviceType = dealDeviceType(deviceTypeIds, appTagInfo, map, gatewayIdList, total,
					accessSecret);
			gatewayIdRsProductId.putAll(dealDeviceType);
		}
	}



	/**
	 * 
	 * @Description: 封装设备类型基础数据和设备基础信息需要的协议类型等参数集合
	 * @param
	 * @return
	 */

	private void putDeviceType(String accessSecret, String userId, List<DeviceType> deviceTypes,
			Map<String, DeviceTypeInfoDto> deviceIdRsInfo, String deviceTypeId, String deviceTypeInfoByGet) {

		JSONObject deviceTypeInfoJson = JSON.parseObject(deviceTypeInfoByGet);
		String productProtocolType = deviceTypeInfoJson.getString("productProtocolType");
		String productLevel = deviceTypeInfoJson.getString("productLevel");
		String productName = deviceTypeInfoJson.getString("productName");

		String finalDeviceTypeId = UtilTool.getDeviceTypeId(deviceTypeId, accessSecret);

		DeviceTypeInfoDto deviceTypeInfoDto = new DeviceTypeInfoDto();
		deviceTypeInfoDto.setId(finalDeviceTypeId);
		deviceTypeInfoDto.setProductName(productName);
		deviceTypeInfoDto.setProductProtocolType(productProtocolType);
		deviceTypeInfoDto.setProductLevel(productLevel);
		deviceIdRsInfo.put(finalDeviceTypeId, deviceTypeInfoDto);

		DeviceType deviceType = new DeviceType();
		deviceType.setId(finalDeviceTypeId);
		deviceType.setCreateTime(DateUtils.formatDate(new Date()));
		deviceType.setUpdateTime(DateUtils.formatDate(new Date()));
		deviceType.setCreateBy(userId);
		deviceType.setUpdateBy(userId);
		deviceType.setDeviceTypeName(productName);
		deviceType.setParentId("");
		deviceType.setAccessSecret(accessSecret);
		deviceTypes.add(deviceType);
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private Map<String, String> dealDeviceType(Set<String> deviceTypIds, AppTagDto appTagInfo, Map<String, String> map,
			List<String> gatewayIdList, int total, String accessSecret) {
		// 页大小,受接口限制,最大只能是100
		int pageSize = 100;
		Map<String, String> gatewayIdRsProductId = Maps.newHashMap();
		int totalPage = (int) (total / pageSize + ((total % pageSize == 0) ? 0 : 1));
		for (int i = 1; i < totalPage + 1; i++) {
			String uri3 = new StringBuffer().append("/v1/app-push/applications/").append(appTagInfo.getAppId())
					.append("/gateways?page=").append(i).append("&per_page=100").toString();
			String deviceTypeInfoByGet1 = InterGatewayUtil.getHttpValueByGet(uri3, ip, map);
			if (null == deviceTypeInfoByGet1) {
				continue;
			} else {
				JSONObject deviceTypeInfoJson1 = JSON.parseObject(deviceTypeInfoByGet1);
				gatewayIdRsProductId = dealDeviceType(deviceTypIds, gatewayIdList, deviceTypeInfoJson1, accessSecret);
			}
		}
		return gatewayIdRsProductId;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private Map<String, String> dealDeviceType(Set<String> deviceTypIds, List<String> gatewayIdList,
			JSONObject deviceTypeInfoJson, String accessSecret) {
		Map<String, String> gatewayIdRsProductId = Maps.newHashMap();
		// gateways 网关集合信息
		JSONArray deviceTypeInfoArray = deviceTypeInfoJson.getJSONArray("gateways");
		List<AppTagDto> deviceTypeInfoArrayList = deviceTypeInfoArray.toJavaList(AppTagDto.class);
		for (AppTagDto deviceTypeInfo : deviceTypeInfoArrayList) {
			String gatewayId = deviceTypeInfo.getGatewayId();

			// 有效的网关
			if (StringUtils.isNotBlank(gatewayId) && gatewayIdList.contains(gatewayId)) {
				String deviceTypeId = UtilTool.getDeviceTypeId(deviceTypeInfo.getProductId(), accessSecret);
				deviceTypIds.add(deviceTypeInfo.getProductId());
				gatewayIdRsProductId.put(gatewayId, deviceTypeId);
			}

		}
		return gatewayIdRsProductId;
	}



	/**
	 * 
	 * @Description:当设备总数大于100时,循环组装设备基础数据
	 * @param
	 * @return
	 * @throws Exception 
	 */

	private void dealData(String accessSecret, List<Device> devices, AppTagDto appTagInfo, Map<String, String> map,
			List<String> gatewayIdList, int total) throws Exception {
		// 页大小,受接口限制,最大只能是100
		int pageSize = 100;
		int totalPage = (int) (total / pageSize + ((total % pageSize == 0) ? 0 : 1));
		for (int i = 1; i < totalPage + 1; i++) {

			String uri = new StringBuffer().append("/v1/app-push/applications/").append(appTagInfo.getAppId())
					.append("/subThings?page=").append(i).append("&per_page=100").toString();
			String deviceInfoByGet = InterGatewayUtil.getHttpValueByGet(uri, ip, map);

			if (null == deviceInfoByGet) {
				throw new Exception("查询平台数据失败或返回值为空");
			} else {
				JSONObject deviceInfo = JSON.parseObject(deviceInfoByGet);
				dealData(accessSecret, devices, appTagInfo, gatewayIdList, deviceInfo);
			}
		}
	}



	/**
	 * 
	 * @Description:组装设备基础数据
	 * @param
	 * @return
	 */

	private void dealData(String accessSecret, List<Device> devices, AppTagDto appTagInfo, List<String> gatewayIdList,
			JSONObject deviceInfo) {
		// subThings设备信息集合
		JSONArray subThings = deviceInfo.getJSONArray("subThings");
		List<AppTagDto> appTagDtoList = subThings.toJavaList(AppTagDto.class);

		for (AppTagDto appTagDto : appTagDtoList) {
			// 存储每个设备基础信息
			Device device = new Device();
			String id = UtilTool.getDeviceId(appTagDto.getGatewayId(), appTagDto.getSubThingId(), this.appKey,
					accessSecret);

			device.setId(id);
			device.setDeviceName(appTagDto.getSubThingName());
			device.setDeviceKey("");
			device.setProtocolType("协议类型");
			device.setOpenProtocols("开放/私有协议");
			device.setDeviceType("设备类型");
			device.setAppTag(appTagInfo.getAppTag());
			device.setManageState("enabled");
			device.setAccessSecret(accessSecret);
			device.setGatewayId(appTagDto.getGatewayId());
			device.setSubDeviceId(appTagDto.getSubThingId());
			devices.add(device);
			// 保存网关id,用来查询产品id(设备类型id)
			if (!gatewayIdList.contains(appTagDto.getGatewayId())) {
				gatewayIdList.add(appTagDto.getGatewayId());
			}
		}
	}



	/**
	 * 
	 * @Description: 同步接口
	 * @param
	 * @return
	 */
	public Result<String> synchroData(String appTagParam) {
		logger.info(String.format("[synchroData()->request params:%s]", appTagParam));
		// 参数验证
		if (ParamChecker.isBlank(appTagParam) || ParamChecker.isNotMatchJson(appTagParam)) {
			logger.error(String.format("[synchroData()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
					LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			JSONObject paramsObject = JSON.parseObject(appTagParam);
			if (StringUtils.isBlank(paramsObject.getString(AppTagDto.APPTAGS))) {
				logger.error("[synchro()->invalid: appTag不能为空]");
				return ResultBuilder.failResult("appTag不能为空!");
			}
			if (StringUtils.isBlank(paramsObject.getString(AppTagDto.ACCESSSECRET))) {
				logger.error("[synchroData()->invalid: 接入方秘钥不能为空]");
				return ResultBuilder.failResult("接入方秘钥不能为空!");
			}

			// 判断apptag集合
			String appTagStr = paramsObject.getString(AppTagDto.APPTAGS);
			String[] appTagArrary = appTagStr.split(",");
			List<String> appTagList = Arrays.asList(appTagArrary);
			if (null == appTagList || appTagList.isEmpty()) {
				logger.error("[synchroData()->fail:同步失败");
				return ResultBuilder.failResult("同步失败!获取的appTag集合为空");
			}

			List<AppTagDto> appTagDtoList = Lists.newArrayList();
			// 解析appTag获取appId和appKey并赋值到对象,保存到集合中
			Result<String> dealData = dealData(appTagList, appTagDtoList);
			if (null != dealData) {
				return dealData;
			}

			String accessSecret = paramsObject.getString(AppTagDto.ACCESSSECRET);

			String userId = getUserId();
			if (StringUtils.isBlank(userId)) {
				logger.error("同步设备失败,人员验证失败或未找到人员信息!");
				return ResultBuilder.failResult("同步设备失败,人员验证失败或未找到人员信息!");
			}

			// 存储设备基础数据
			List<Device> devices = Lists.newArrayList();
			// 存储设备类型数据
			List<DeviceType> deviceTypes = Lists.newArrayList();
			// 获取设备
			synchroData(accessSecret, userId, appTagDtoList, devices, deviceTypes);

			// 同步设备
			RpcResponse<Boolean> synchroDevices = deviceInfoCudService.synchroDevices(devices, deviceTypes, appTagList,
					accessSecret);
			if (synchroDevices.isSuccess() && synchroDevices.getSuccessValue()) {
				logger.info("[synchroData()->success: 设备同步成功!!!]");
				return ResultBuilder.successResult("设备同步成功!!!", "设备同步成功!!!");
			} else {
				logger.error("[synchroData()->fail: 设备同步失败!!!]");
				return ResultBuilder.successResult("设备同步失败!!!", "设备同步失败!!!");
			}

		} catch (Exception e) {
			logger.error("synchroData()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:解析appTag获取appId和appKey并赋值到对象,保存到集合中
	 * @param
	 * @return
	 * @return
	 */

	private Result<String> dealData(List<String> appTagList, List<AppTagDto> appTagDtoList) {

		List<String> checkParamAppTagExist = Lists.newArrayList();
		for (String appTag : appTagList) {
			String subAppTag = null;
			if (!appTag.endsWith(AppTagDto.DATACHANGE)) {
				logger.error(String.format("[synchroData()->error:错误的appTag:%s]", appTag));
				continue;
			} else {
				subAppTag = appTag.replace(AppTagDto.DATACHANGE, "");
			}
			String[] split = subAppTag.split("\\.");
			if (split.length != 2) {
				logger.error(String.format("[synchroData()->error:错误的appTag:%s]", appTag));
				continue;
			} else {
				AppTagDto appTagDto = new AppTagDto();
				appTagDto.setAppId(split[0]);
				appTagDto.setAppKey(split[1]);
				appTagDtoList.add(appTagDto);
				// 校验参数中是否存在重复的appTag
				String realAppTag = appTagDto.getAppTag();
				if (!checkParamAppTagExist.contains(realAppTag)) {
					checkParamAppTagExist.add(realAppTag);
				} else {
					logger.error(String.format("[updateFactory()->请求参数中appId-appKey重复(%s-%s),不能同步!]",
							appTagDto.getAppId(), appTagDto.getAppKey()));
					return ResultBuilder.failResult(String.format("请求参数中appId-appKey重复(%s-%s),不能同步!",
							appTagDto.getAppId(), appTagDto.getAppKey()));
				}
			}

		}
		return null;
	}



	/**
	 * 
	 * @Description: 修改厂家(新)
	 * @param
	 * @return
	 */
	public Result<String> updateFactory(String factoryId, String updateParams) {
		logger.info(String.format("[updateFactory()->request params--updateParams:%s,factoryId:%s]", updateParams,
				factoryId));
		// 参数验证
		if (ParamChecker.isBlank(updateParams) || ParamChecker.isNotMatchJson(updateParams)) {
			logger.error(String.format("[updateFactory()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
					LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		JSONObject paramsJson = JSON.parseObject(updateParams);
		if (StringUtils.isBlank(paramsJson.getString(FactoryConstants.FACTORY_APPTAG))) {
			logger.error("[updateFactory()->invalid: appTag参数不能为空!]");
			return ResultBuilder.failResult("appTag参数不能为空!");
		}
		try {
			JSONArray jsonArray = paramsJson.getJSONArray(FactoryConstants.FACTORY_APPTAG);
			List<AppTagDto> newAppTagInfo = jsonArray.toJavaList(AppTagDto.class);
			List<AppTagDto> newAppTagList = Lists.newArrayList();
			if (newAppTagInfo.isEmpty()) {
				logger.error("[updateFactory()->invalid: appTag参数不能为空!]");
				return ResultBuilder.failResult("appTag参数不能为空!");
			}
			String accessSecret = paramsJson.getString(DeviceTypeContants.ACCESSSECRET);
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[updateFactory()->invalid: accessSecret参数不能为空!]");
				return ResultBuilder.failResult("接入方密钥不能为空!");
			}
			// 根据厂家id获取接入方秘钥
			RpcResponse<Map<String, Object>> factoryMap = factoryQueryService.findById(factoryId);
			if (!factoryMap.isSuccess() || null == factoryMap.getSuccessValue()) {
				logger.error("[updateFactory()->invalid: 根据厂家id查询接入方秘钥失败!]");
				return ResultBuilder.failResult("根据厂家id查询接入方秘钥失败!");
			}
			String accessSecretValue = factoryMap.getSuccessValue().get(FactoryConstants.ACCESS_SECRET).toString();
			if (!accessSecret.equals(accessSecretValue)) {
				logger.error(
						String.format("[updateFactory()->invalid: 厂家不在该接入方下!!accessSecret:%s,accessSecretValue:%s]",
								accessSecret, accessSecretValue));
				return ResultBuilder.failResult("厂家不在此接入方下!");
			}
			String userId = getUserId();
			if (StringUtils.isBlank(userId)) {
				logger.error("添加厂家失败,人员验证失败或未找到人员信息!");
				return ResultBuilder.failResult("添加厂家失败,人员验证失败或未找到人员信息!");
			}
			// String userId = paramsJson.getString(DeviceTypeContants.USERID);
			// 校验参数
			List<String> checkParamAppTagExist = Lists.newArrayList();
			// 校验appTag参数绑定的网关是否重复
			Set<String> checkParamGatewayExist = Sets.newHashSet();
			// 校验appTag是否重复
			Result<String> updateFactoryFor = updateFactoryFor(factoryId, newAppTagInfo, newAppTagList, accessSecret,
					checkParamAppTagExist, checkParamGatewayExist);
			if (null != updateFactoryFor) {
				return ResultBuilder.failResult(updateFactoryFor.getResultStatus().getResultMessage());
			}

			RpcResponse<List<AppTagDto>> appTagByFactoryId = factoryQueryService.getAppTagByFactoryId(factoryId);
			if (!appTagByFactoryId.isSuccess() || appTagByFactoryId.getSuccessValue() == null) {
				logger.error("[updateFactory()->invalid: appId和appKey重复检验失败!]");
				return ResultBuilder.failResult(appTagByFactoryId.getMessage());
			}
			return updateFactoryHandle(factoryId, paramsJson, newAppTagInfo, newAppTagList, accessSecret, userId,
					appTagByFactoryId);

		} catch (Exception e) {
			logger.error("updateFactory()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param factoryId
	 * @param paramsJson
	 * @param newAppTagInfo
	 * @param newAppTagList
	 * @param accessSecret
	 * @param userId
	 * @param appTagByFactoryId
	 * @return
	 * @throws Exception 
	 */

	private Result<String> updateFactoryHandle(String factoryId, JSONObject paramsJson, List<AppTagDto> newAppTagInfo,
			List<AppTagDto> newAppTagList, String accessSecret, String userId,
			RpcResponse<List<AppTagDto>> appTagByFactoryId) throws Exception {
		List<AppTagDto> oldAppTagList = appTagByFactoryId.getSuccessValue();
		// 处理数据:对比新添加的appTag和旧的appTag,原有的appTag不再同步设备,删除的appTag删除对应设备,新增对应的设备
		dealDate(newAppTagInfo, oldAppTagList);
		// 存储设备基础数据
		List<Device> devices = Lists.newArrayList();
		// 存储设备类型数据
		List<DeviceType> deviceTypes = Lists.newArrayList();

		synchroData(accessSecret, userId, newAppTagInfo, devices, deviceTypes);

		// 要删除的APPTag oldAppTagList;要添加的APPtag newAppTagList;要添加的设备 devices
		// 要删除的设备 根据oldAppTagList删除;要添加的设备类型 deviceTypes;要修改的厂家信息factory
		paramsJson.remove(FactoryConstants.FACTORY_APPTAG);
		Factory factory = JSON.parseObject(paramsJson.toJSONString(), Factory.class);
		factory.setId(factoryId);
		RpcResponse<String> updateFactory = factoryCrudService.updateFactory(factory, devices, deviceTypes,
				newAppTagList, oldAppTagList, accessSecret);
		if (updateFactory.isSuccess()) {
			logger.info("[updateFactory()->success: 厂家信息修改成功!]");
			return ResultBuilder.successResult("厂家信息修改成功!", "厂家信息修改成功!");
		} else {
			logger.error("[updateFactory()->invalid: 厂家信息修改失败!]");
			return ResultBuilder.failResult(updateFactory.getMessage());
		}
	}



	/**
	 * @Description:
	 * @param factoryId
	 * @param newAppTagInfo
	 * @param newAppTagList
	 * @param accessSecret
	 * @param checkParamAppTagExist
	 * @param checkParamGatewayExist
	 */

	private Result<String> updateFactoryFor(String factoryId, List<AppTagDto> newAppTagInfo,
			List<AppTagDto> newAppTagList, String accessSecret, List<String> checkParamAppTagExist,
			Set<String> checkParamGatewayExist) {
		for (AppTagDto appTagDto : newAppTagInfo) {
			// 校验参数中是否存在重复的appTag
			String appTag = appTagDto.getAppTag();
			if (!checkParamAppTagExist.contains(appTag)) {
				checkParamAppTagExist.add(appTag);
			} else {
				logger.error(String.format("[updateFactory()->请求参数中appId-appKey重复(%s-%s),不能添加!]", appTagDto.getAppId(),
						appTagDto.getAppKey()));
				return ResultBuilder.failResult(
						String.format("请求参数中appId-appKey重复(%s-%s),不能添加!", appTagDto.getAppId(), appTagDto.getAppKey()));
			}

			appTagDto.setId(factoryId);
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			RpcResponse<Boolean> checkAppTagExist = factoryQueryService.checkAppTagExist(appTagDto, accessSecret,
					checkParamGatewayExist, token);

			if (!checkAppTagExist.isSuccess()) {
				logger.error("[updateFactory()->invalid: appId和appKey重复检验失败!]");
				return ResultBuilder.failResult(checkAppTagExist.getMessage());
			} else if (!checkAppTagExist.getSuccessValue()) {
				logger.error(String.format("[update()->invalid: appId重复: %s,appKey重复: %s]", appTagDto.getAppId(),
						appTagDto.getAppKey()));
				return ResultBuilder.failResult(checkAppTagExist.getMessage());
			}
			appTagDto.setId(UUIDUtil.getUUID());
			newAppTagList.add(appTagDto);
		}
		return null;
	}



	/**
	 * 
	 * @Description:处理数据:对比新添加的appTag和旧的appTag,原有的appTag不再同步设备,删除的appTag删除对应设备,新增对应的设备
	 * @param
	 * @return
	 */

	private void dealDate(List<AppTagDto> newAppTagInfo, List<AppTagDto> oldAppTagList) {
		Iterator<AppTagDto> newIterator = newAppTagInfo.iterator();
		while (newIterator.hasNext()) {
			AppTagDto appTagDto = newIterator.next();
			Iterator<AppTagDto> oldIterator = oldAppTagList.iterator();
			while (oldIterator.hasNext()) {
				AppTagDto oldAppTag = oldIterator.next();
				boolean theSame = appTagDto.getAppTag().equals(oldAppTag.getAppTag());
				if (theSame) {
					newIterator.remove();
					oldIterator.remove();
				}
			}
		}
	}



	public Result<String> updateState(String id, String updateParams) {
		logger.info(String.format("[updateState()->request params--updateParams:%s,id:%s]", updateParams, id));
		if (ParamChecker.isBlank(updateParams) || ParamChecker.isNotMatchJson(updateParams)) {
			logger.error(String.format("[updateState()->error:%s or %s]", LogMessageContants.NO_PARAMETER_EXISTS,
					LogMessageContants.PARAMETERS_OF_ILLEGAL));
			return ResultBuilder.invalidResult();
		}
		try {
			Factory factory = JSON.parseObject(updateParams, Factory.class);
			factory.setId(id);
			RpcResponse<String> rpcResponse = factoryCrudService.updateState(factory);
			if (rpcResponse.isSuccess()) {
				logger.info("[updateState()->success:]" + rpcResponse.getMessage());
				return ResultBuilder.successResult(rpcResponse.getSuccessValue(), rpcResponse.getMessage());
			}
			logger.error("[updateState()->fail:]" + rpcResponse.getMessage());
			return ResultBuilder.failResult(rpcResponse.getMessage());
		} catch (Exception e) {
			logger.error("updateState()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
