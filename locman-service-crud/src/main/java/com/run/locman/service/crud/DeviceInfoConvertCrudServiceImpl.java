/*
 * File name: DeviceInfoConvertServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2018年3月28日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.DeviceInfoConvertCrudRepository;
import com.run.locman.api.crud.repository.DeviceInfoCudRepository;
import com.run.locman.api.crud.repository.DeviceTypeCudRepository;
import com.run.locman.api.crud.repository.FactoryAppTagCrudRepository;
import com.run.locman.api.crud.repository.FactoryCrudRepository;
import com.run.locman.api.crud.service.DeviceInfoConvertCrudService;
import com.run.locman.api.entity.Device;
import com.run.locman.api.entity.DeviceType;
import com.run.locman.api.entity.Factory;
import com.run.locman.api.entity.FactoryAppTag;
import com.run.locman.api.model.DeviceInfoConvertModel;
import com.run.locman.api.query.service.DeviceInfoConvertQueryService;
import com.run.locman.api.query.service.FactoryQueryService;
import com.run.locman.api.timer.query.service.WingsIotConstansQuery;
import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.api.wingiot.service.WingIotDevice;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.DeviceInfoConvertConstans;

/**
 * @Description:数据转换实现类rpc
 * @author: zhaoweizhi
 * @version: 1.0, 2018年3月28日
 */
@Transactional(rollbackFor = Exception.class)
public class DeviceInfoConvertCrudServiceImpl implements DeviceInfoConvertCrudService {

	private Logger							logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceInfoConvertCrudRepository	deviceInfoConvertCrudRepository;

	@Autowired
	private DeviceInfoConvertQueryService	deviceInfoConvertQueryService;

	@Autowired
	private DeviceInfoCudRepository			deviceInfoCudRepository;

	@Autowired
	private DeviceTypeCudRepository			deviceTypeCudRepository;
	@Autowired
	private WingsIotConstansQuery           wingsIotConstansQuery;
	@Autowired
	private FactoryCrudRepository 			factoryCrudRepository;
	
	@Autowired
	private FactoryQueryService 			factoryQueryService;
	
	@Autowired
	private FactoryAppTagCrudRepository		factoryAppTagCrudRepository;

	/**
	 * @see com.run.locman.api.crud.service.DeviceInfoConvertCrudService#addConvertInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> addConvertInfo(DeviceInfoConvertModel deviceInfoConvertModel) {
		try {

			// 必填参数校验
			RpcResponse<String> checkObjectBusinessKey = CheckParameterUtil.checkObjectBusinessKey(logger,
					"addConvertInfo", deviceInfoConvertModel, false, DeviceInfoConvertConstans.CONVERT_DIC_KEY,
					DeviceInfoConvertConstans.CONVERT_DIC_VALUE, DeviceInfoConvertConstans.CONVERT_ACCESSSECRET);

			if (checkObjectBusinessKey != null) {
				return checkObjectBusinessKey;
			}

			// 判断dicKey是否存在
			RpcResponse<String> existConvertInfo = deviceInfoConvertQueryService
					.existConvertInfo(deviceInfoConvertModel);

			if (!existConvertInfo.isSuccess()) {
				return RpcResponseBuilder.buildErrorRpcResp("校验dicKey失败！");
			}

			if (!StringUtils.isBlank(existConvertInfo.getSuccessValue())) {
				return RpcResponseBuilder.buildErrorRpcResp("dicKey已经存在,请不要重复添加！");
			}
			// 插入数据库
			logger.info(String.format("[addConvertInfo()->info:即将保存数据转换配置,参数%s]", deviceInfoConvertModel));
			if (deviceInfoConvertCrudRepository.insertModel(deviceInfoConvertModel) > 0) {
				logger.info(
						String.format("[addConvertInfo()->success:%s]", DeviceInfoConvertConstans.OPERATION_SUCCESS));
				return RpcResponseBuilder.buildSuccessRpcResp(DeviceInfoConvertConstans.OPERATION_SUCCESS,
						deviceInfoConvertModel.getId());
			} else {
				logger.error(String.format("[addConvertInfo()->error:%s]", DeviceInfoConvertConstans.OPERATION_ERROR));
				return RpcResponseBuilder.buildErrorRpcResp(DeviceInfoConvertConstans.OPERATION_ERROR);
			}

		} catch (Exception e) {
			logger.error("addConvertInfo()->Exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceInfoConvertCrudService#updateConvertInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> updateConvertInfo(DeviceInfoConvertModel deviceInfoConvertModel) {
		try {
			// 必填参数校验
			RpcResponse<String> checkBusinessKey = CheckParameterUtil.checkObjectBusinessKey(logger, "addConvertInfo",
					deviceInfoConvertModel, false, DeviceInfoConvertConstans.CONVERT_DIC_KEY,
					DeviceInfoConvertConstans.CONVERT_DIC_VALUE, DeviceInfoConvertConstans.CONVERT_DIC_ID,
					DeviceInfoConvertConstans.CONVERT_ACCESSSECRET);
			if (checkBusinessKey != null) {
				return checkBusinessKey;
			}

			// 判断dicKey是否存在
			RpcResponse<String> existConvertInfo = deviceInfoConvertQueryService
					.existConvertInfo(deviceInfoConvertModel);
			//
			if (!existConvertInfo.isSuccess()) {
				return RpcResponseBuilder.buildErrorRpcResp("校验dicKey失败！");
			}

			// 不为null并且id和当前id不同。
			if (!StringUtils.isBlank(existConvertInfo.getSuccessValue())
					&& !deviceInfoConvertModel.getId().equals(existConvertInfo.getSuccessValue())) {
				return RpcResponseBuilder.buildErrorRpcResp("dicKey已经存在,请不要重复添加！");
			}
			// 调用mapper
			logger.info(String.format("[addConvertInfo()->info:即将更新数据配置,参数%s]", deviceInfoConvertModel));
			if (deviceInfoConvertCrudRepository.updatePart(deviceInfoConvertModel) > 0) {
				logger.info(
						String.format("[addConvertInfo()->success:%s]", DeviceInfoConvertConstans.OPERATION_SUCCESS));
				return RpcResponseBuilder.buildSuccessRpcResp(DeviceInfoConvertConstans.OPERATION_SUCCESS,
						deviceInfoConvertModel.getId());
			} else {
				logger.error(String.format("[addConvertInfo()->error:%s]", DeviceInfoConvertConstans.OPERATION_ERROR));
				return RpcResponseBuilder.buildErrorRpcResp(DeviceInfoConvertConstans.OPERATION_ERROR);
			}

		} catch (Exception e) {
			logger.error("updateConvertInfo()->Exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * @see com.run.locman.api.crud.service.DeviceInfoConvertCrudService#deleteConvertInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<String> deleteConvertInfo(DeviceInfoConvertModel deviceInfoConvertModel) {
		try {

			// 获取批量删除id
			List<String> ids = deviceInfoConvertModel.getIds();

			if (ids == null || ids.size() <= 0) {
				return RpcResponseBuilder.buildErrorRpcResp("数组id为null!");
			}
			logger.info(String.format("[addConvertInfo()->info:即将删除数据配置,参数:%s]", ids));
			if (deviceInfoConvertCrudRepository.deleteConvertInfo(ids) > 0) {
				logger.info(
						String.format("[addConvertInfo()->success:%s]", DeviceInfoConvertConstans.OPERATION_SUCCESS));
				return RpcResponseBuilder.buildSuccessRpcResp(DeviceInfoConvertConstans.OPERATION_SUCCESS,
						ids.toString());
			} else {
				logger.error(String.format("[addConvertInfo()->error:%s]", DeviceInfoConvertConstans.OPERATION_ERROR));
				return RpcResponseBuilder.buildErrorRpcResp(DeviceInfoConvertConstans.OPERATION_ERROR);
			}

		} catch (Exception e) {
			logger.error("deleteConvertInfo()->Exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}



	// 彭州临时使用

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public RpcResponse<String> deviceSynchronization(JSONObject deviceInfo) {
		try {
			String productId = deviceInfo.getString("productId");
			String accessSecret = deviceInfo.getString("accessSecret");
			String userId = deviceInfo.getString("userId");
			String productName = deviceInfo.getString("productName");
			if (StringUtils.isBlank(productId)) {
				logger.error("[deviceSynchronization()->productId不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("产品Id不能为空！");
			}
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[deviceSynchronization()->accessSecret不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("接入方密钥不能为空！");
			}
			if (StringUtils.isBlank(productName)) {
				logger.error("[deviceSynchronization()->productName不能为空！]");
				return RpcResponseBuilder.buildErrorRpcResp("产品名字不能为空！");
			}
			//同步厂家（一个接入方对应一个厂家）
			
			//查询该接入方是否存在
			RpcResponse<List<Map<String, String>>> downBoxFactory = factoryQueryService.downBoxFactory(accessSecret);
			//厂家id
			String factoryId="";
			if(downBoxFactory.isSuccess()) {
				List<Map<String, String>> successValue = downBoxFactory.getSuccessValue();
				if(null !=successValue && successValue.size()>0) {
					logger.info("[deviceSynchronization()->厂家已经存在!]");
					factoryId=successValue.get(0).get("id")+"";
				}else {
					Factory factory = new Factory();
					factory.setAccessSecret(accessSecret); 
					factory.setAddress("成都市");
					factory.setContacts("sefon"); 
					factory.setContactsPhone("1234");
					factory.setFactoryName("四方");
					factory.setId(UtilTool.getUuId());
					factory.setManageState("enabled");
					factory.setRemark("1");
					int insertModel = factoryCrudRepository.insertNewFactory(factory);
					if(insertModel > 0) {
						logger.info("厂家新增成功！");
						factoryId=factory.getId();
					}
				}
			}
				
			
			Map<String, String> productApiKeyTable = wingsIotConstansQuery.getProductApiKeyTable();
			// 查询设备集合
			logger.info("productId:" + productId + "getProductApiKeyTable:" + productApiKeyTable);
			RpcResponse<JSONObject> deviceInfoJson = WingIotDevice.getDeviceListByProductId(productId,productApiKeyTable);
			if(!deviceInfoJson.isSuccess()) {
				logger.info("[deviceSynchronization()->设备对接IOT参数错误！！");
				return RpcResponseBuilder.buildErrorRpcResp(deviceInfoJson.getMessage());
			}
			JSONObject json = deviceInfoJson.getSuccessValue();
			List<Map> lists = JSONObject.parseArray(json.getString("list") + "", Map.class);
			// 根据productId（用做设备类型ID）查询设备类型
			List<String> deviceTypeIdList = deviceInfoConvertCrudRepository
					.deviceTypeIdListBYAccessSecret(accessSecret);
			// 存储设备类型数据
			List<DeviceType> deviceTypes = Lists.newArrayList();
			if (!deviceTypeIdList.contains(productId)) {
				DeviceType deviceType = new DeviceType();
				deviceType.setAccessSecret(accessSecret);
				deviceType.setId(productId);
				deviceType.setUpdateTime(DateUtils.formatDate(new Date()));
				deviceType.setCreateTime(DateUtils.formatDate(new Date()));
				deviceType.setCreateBy(userId);
				deviceType.setUpdateBy(userId);
				deviceType.setParentId("");
				deviceType.setDeviceTypeName(productName);
				deviceTypes.add(deviceType);
				
				//存factory_apptag中间表
				FactoryAppTag factoryApptag = new FactoryAppTag();
				factoryApptag.setId(UtilTool.getUuId());
				factoryApptag.setAppTag("pz"+productId);
				factoryApptag.setFactoryId(factoryId);
			
				factoryAppTagCrudRepository.insertModel(factoryApptag);
			}else {
				Map<String ,Object> mapName=new HashMap<String,Object>();
				mapName.put("productId", productId);
				mapName.put("productName", productName);
				Integer  updateTypeName=deviceInfoConvertCrudRepository.updateDeviceTypeNameByProductId(mapName);
				if(updateTypeName<0) {
					logger.error("[deviceSynchronization()->fail: 设备类型修改失败,设备同步失败!!!]");
					return RpcResponseBuilder.buildErrorRpcResp("设备类型修改失败,设备同步失败!!!");
				}
			}
			
			
			if (!deviceTypes.isEmpty()) {
				Integer addDeviceTypeNum = deviceTypeCudRepository.addDeviceType(deviceTypes);
				if (addDeviceTypeNum < 0) {
					logger.error("[deviceSynchronization()->fail: 设备类型保存失败,设备同步失败!!!]");
					return RpcResponseBuilder.buildErrorRpcResp("设备类型保存失败,设备同步失败!!!");
				}
				logger.info("[deviceSynchronization()->success: 设备类型保存成功]");
			}
			// 根据设备类型和接入方密钥查询设备ID。
			// 设备Id查询queryMap
			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("accessSecret", accessSecret);
			queryMap.put("productId", productId);
//			List<String> deviceIdList=deviceInfoConvertCrudRepository.deviceIdListByAccAndType(queryMap);
			
			//查询原绑定设施的设备Id集合 oldDeviceIds
			List<String> oldDeviceIds = deviceInfoConvertCrudRepository.deviceIdListByAccAndType(queryMap);
			
			// 删除设备
			Integer deleteDevice = deviceInfoConvertCrudRepository.deleteDeviceByAccAndType(queryMap);
			logger.info(String.format("[deviceSynchronization --> success: 删除原有设备数量: %s]", deleteDevice));

			// 存储设备数据
			List<Device> devices = Lists.newArrayList();
			//打印deviceId
			List<String> deviceIds = Lists.newArrayList();
			if (null != lists && lists.size() > 0) {
				for (Map<String, Object> map : lists) {
					if (null != map && map.size() > 0) {
						String deviceId = map.get("deviceId") + "";
						Device device = new Device();
						device.setId(deviceId);
						device.setDeviceName(map.get("deviceName") + "");
						device.setDeviceKey("");
						device.setProtocolType("HTTP");
						device.setOpenProtocols("PUBLIC");
						device.setDeviceType(productId);
						device.setAppTag("pz" + productId);
						device.setManageState("enabled");
						device.setAccessSecret(accessSecret);
						device.setGatewayId("pzGatewayId" + productId);
						device.setSubDeviceId(map.get("imei") + "");
						device.setDeviceDefendState("normal");
						deviceIds.add(deviceId);
						devices.add(device);
					}
				}
				//删除二次同步已经没有了的设备的设施设备中间表数据
				//需要删除的中间表设备ID  deleteDeviceIds
				List<String> deleteDeviceIds = Lists.newArrayList();
				for(String oldDeviceId:oldDeviceIds) {
					if(!deviceIds.contains(oldDeviceId)) {
						deleteDeviceIds.add(oldDeviceId);
					}
				}
				//删除中间表信息
				if(null != deleteDeviceIds && deleteDeviceIds.size() >0) {
					int deleteFacDevice = deviceInfoConvertCrudRepository.deleteFacDevice(deleteDeviceIds);
					if(deleteFacDevice <0) {
						logger.error("deviceSynchronization()->fail: 删除设施设备中间表信息失败!!!");
					}
				}
				
				if (!devices.isEmpty()) {
					Integer saveDeviceBatch = deviceInfoCudRepository.saveDeviceBatch(devices);
					if (saveDeviceBatch < 0) {
						logger.error("[deviceSynchronization()->fail: 设备同步失败!!!]");
						return RpcResponseBuilder.buildErrorRpcResp("设备同步失败!!!");
					}
					logger.info("[deviceSynchronization()->success: 设备同步成功]");
					return RpcResponseBuilder.buildSuccessRpcResp("设备同步成功", deviceIds.toString());
				}
			}
			logger.info("[deviceSynchronization()->success:同步设备为空!!!");
			return RpcResponseBuilder.buildSuccessRpcResp("设备同步成功，但同步设备为空！！", lists.toString());

		} catch (Exception e) {
			logger.error("deviceSynchronization()->Exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}
