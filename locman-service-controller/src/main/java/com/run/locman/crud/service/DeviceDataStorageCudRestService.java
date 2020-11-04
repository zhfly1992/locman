/*
 * File name: DeviceDataStorageCudRestService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年11月26日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.crud.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.DeviceDataStorageCudService;
import com.run.locman.api.dto.DeviceDataDto;
import com.run.locman.api.entity.DeviceDataStorage;

import com.run.locman.api.query.service.DeviceDataStorageQueryService;

import com.run.locman.api.util.CheckParameterUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.filetool.FastDfsUtil;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年11月26日
 */
@Service
public class DeviceDataStorageCudRestService {

	private Logger							logger		= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private DeviceDataStorageCudService		deviceDataStorageCudService;

	@Autowired
	private DeviceDataStorageQueryService	deviceDataStorageQueryService;

	// 测试代码
	@Autowired
	private HttpServletRequest				request;

	private int								oneMinute	= 60 * 1000;

	private int								fiveM		= 5242880;

	private static String					IMG			= "img";

	private static String					VIDEO		= "video";



	public Result<Boolean> addDeviceData(String deviceDataInfo) {
		// 参数校验
		logger.info(String.format("[addDeviceData()->request params:%s]", deviceDataInfo));
		try {
			if (StringUtils.isBlank(deviceDataInfo)) {
				logger.error("请求参数为空!!");
				return ResultBuilder.failResult("请求参数为空!!");
			}
			List<DeviceDataStorage> deviceDataStorageList = Lists.newArrayList();
			List<JSONObject> jsonArray = JSONObject.parseArray(deviceDataInfo, JSONObject.class);
			// 用来校验请求参数中序列号及设备编号是否重复
			int countDeviceNumber = 0;
			int countSerialNumber = 0;
			HashSet<String> addDeviceNumber = Sets.newHashSet();
			HashSet<String> addSerialNumber = Sets.newHashSet();
			for (JSONObject json : jsonArray) {
				RpcResponse<Object> containsParamKey = CheckParameterUtil.containsParamKey(logger, "addDeviceData",
						json, "deviceId", "deviceNumber", "bluetooth", "deviceAddress", "longitude", "latitude",
						"ipPort", "serialNumber", "status", "properties", "deviceTypeId");
				if (null != containsParamKey) {
					logger.error(String.format("[addDeviceData->error:%s]", containsParamKey.getMessage()));
					return ResultBuilder.failResult(containsParamKey.getMessage());
				} else {
					DeviceDataStorage deviceDataStorage = JSONObject.toJavaObject(json, DeviceDataStorage.class);

					String deviceNumber = deviceDataStorage.getDeviceNumber();
					if (StringUtils.isNotBlank(deviceNumber)) {
						++countDeviceNumber;
						addDeviceNumber.add(deviceNumber);
						if (addDeviceNumber.size() < countDeviceNumber) {
							logger.error(String.format("[addDeviceData->error:请求参数中设备编号重复:%s]", deviceNumber));
							return ResultBuilder.failResult("请求参数中设备编号重复:" + deviceNumber);
						}
					}
					String serialNumber = deviceDataStorage.getSerialNumber();
					if (StringUtils.isBlank(serialNumber)) {
						// 暂时允许设施序列号为空
					} else {
						++countSerialNumber;
						addSerialNumber.add(serialNumber);
						if (addSerialNumber.size() < countSerialNumber) {
							logger.error(String.format("[addDeviceData->error:请求参数中序列号重复:%s]", serialNumber));
							return ResultBuilder.failResult("请求参数中序列号重复:" + serialNumber);
						}
					}
					Result<Boolean> checkExist = checkExist(deviceNumber, serialNumber);
					if (null != checkExist) {
						return checkExist;
					}

					/*
					 * String areaCode =
					 * AddressTool.getAreaCode(deviceDataStorage.getLongitude(),
					 * deviceDataStorage.getLatitude()); if
					 * (StringUtils.isBlank(areaCode)) {
					 * logger.error("[addDeviceData->error:未能根据经纬度解析到区域信息]");
					 * return ResultBuilder.failResult("未能根据经纬度解析到区域信息"); }
					 */
					String areaCode = "510000,510100,510182";

					// 解析扩展属性,并转存图片,视频等数据到locman的文件服务器
					Result<Boolean> dealProperties = dealProperties(deviceDataStorageList, deviceDataStorage, areaCode);
					if (null != dealProperties) {
						return dealProperties;
					}

				}
			}
			RpcResponse<Integer> addDeviceData = deviceDataStorageCudService.addDeviceData(deviceDataStorageList);
			Integer successValue = addDeviceData.getSuccessValue();
			if (!addDeviceData.isSuccess() || null == successValue || 0 >= successValue) {
				logger.error("[addDeviceData->error:数据添加失败!]");
				return ResultBuilder.failResult("数据添加失败!");
			} else {
				logger.info("[addDeviceData->error:数据添加成功!]");
				return ResultBuilder.successResult(true, "数据添加成功!");
			}
		} catch (Exception e) {
			logger.error("addDeviceData()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private Result<Boolean> dealProperties(List<DeviceDataStorage> deviceDataStorageList,
			DeviceDataStorage deviceDataStorage, String areaCode) throws IOException {
		deviceDataStorage.setAreaId(areaCode);
		deviceDataStorage.setSynchronizationState("enable");
		deviceDataStorage.setErrorInfo("");
		String properties = deviceDataStorage.getProperties();
		if (StringUtils.isBlank(properties)) {
			deviceDataStorage.setShowExtend("[]");
			deviceDataStorage.setExtend("{}");
		} else {
			List<JSONObject> propertiesList = JSONObject.parseArray(properties, JSONObject.class);
			JSONObject extendjson = new JSONObject();
			// List<JSONObject> showExtendList = Lists.newArrayList();
			// List<JSONObject> showExtendList = Lists.newArrayList();
			JSONArray showExtendList = new JSONArray();
			for (JSONObject propertiesJson : propertiesList) {
				JSONObject showExtendJson = new JSONObject();
				String sign = propertiesJson.getString("sign");
				// String value = propertiesJson.getString("value");
				Object value = propertiesJson.get("value");
				extendjson.put(sign, value);
				String name = propertiesJson.getString("name");
				showExtendJson.put("name", name);
				showExtendJson.put("value", value);
				String type = propertiesJson.getString("type");
				switch (type) {
				case "img":
					showExtendJson.put("type", "image");
					Result<List<String>> resultWhenImage = parseThenDowwnloadAndUpdate(propertiesJson, type);
					if (!"0000".equals(resultWhenImage.getResultStatus().getResultCode())) {
						return ResultBuilder.failResult(resultWhenImage.getResultStatus().getResultMessage());
					} else {
						showExtendJson.put("value", resultWhenImage.getValue());
						extendjson.put(sign, resultWhenImage.getValue());
					}
					break;
				case "text":
					showExtendJson.put("type", "input");
					break;
				case "select":
					showExtendJson.put("type", "select");
					break;
				case "coord":
					showExtendJson.put("type", "coord");
					break;
				case "video":
					showExtendJson.put("type", "video");
					// TODO 加解析
					// 工程版可以传多个视频地址,但是locman只保存一个
					Result<List<String>> resultWhenVideo = parseThenDowwnloadAndUpdate(propertiesJson, type);
					if (!"0000".equals(resultWhenVideo.getResultStatus().getResultCode())) {
						return ResultBuilder.failResult(resultWhenVideo.getResultStatus().getResultMessage());
					} else if (resultWhenVideo.getValue().isEmpty()) {
						showExtendJson.put("value", "");
						extendjson.put(sign, "");
					} else {
						showExtendJson.put("value", resultWhenVideo.getValue().get(0));
						extendjson.put(sign, resultWhenVideo.getValue().get(0));
					}
					break;
				default:
					break;
				}
				showExtendList.add(showExtendJson);
			}
			deviceDataStorage.setExtend(extendjson.toJSONString());
			deviceDataStorage.setShowExtend(showExtendList.toString());
		}

		deviceDataStorageList.add(deviceDataStorage);
		return null;
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	private Result<List<String>> parseThenDowwnloadAndUpdate(JSONObject propertiesJson, String type)
			throws IOException {
		JSONArray jsonArray = propertiesJson.getJSONArray("value");
		List<String> urls = jsonArray.toJavaList(String.class);
		Result<List<String>> result = downloadAndUpdate(urls, type);
		return result;
	}



	private Result<List<String>> downloadAndUpdate(List<String> filePaths, String type) throws IOException {
		if (filePaths.isEmpty()) {
			logger.error("url不能为空");
			return ResultBuilder.failResult("url不能为空");
		}
		try {
			List<String> newFilePaths = Lists.newArrayList();

			for (String filePath : filePaths) {

				File file = new File(filePath);
				String name = file.getName();

				String suffix = name.substring(name.lastIndexOf(".") + 1);
				boolean isImg = IMG.equals(type) && !"jpg".equalsIgnoreCase(suffix) && !"jpeg".equalsIgnoreCase(suffix)
						&& !"png".equalsIgnoreCase(suffix);
				if (isImg) {
					logger.error("图片文件不是规定的格式:" + suffix);
					return ResultBuilder.failResult("图片文件不是规定的格式,仅支持jpg, jpeg, png三种格式的图片");
				}
				boolean isVideo = VIDEO.equals(type) && !"mp4".equalsIgnoreCase(suffix)
						&& !"mov".equalsIgnoreCase(suffix);
				if (isVideo) {
					logger.error("视频文件不是规定的格式:" + suffix);
					return ResultBuilder.failResult("视频文件不是规定的格式,仅支持mp4,mov格式的视频");
				}

				URL url = new URL(filePath);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(oneMinute);

				InputStream fileInputStream = conn.getInputStream();
				byte[] buffer = FastDfsUtil.readInputStream(fileInputStream);
				int length = buffer.length;

				if (length <= 0) {
					logger.error("解析文件地址获取的文件大小为0");
					return ResultBuilder.failResult("url不能为空");
				}
				if (IMG.equals(type) && length > fiveM) {
					logger.error("解析文件地址获取的文件大于15M");
					return ResultBuilder.failResult("解析文件地址获取的图片文件大于5M");
				}
				if (VIDEO.equals(type) && length > FacilitiesContants.FIFTEEN_M) {
					logger.error("解析文件地址获取的文件大于15M");
					return ResultBuilder.failResult("解析文件地址获取的视频文件大于15M");
				}

				String uploadBase64File = FastDfsUtil.uploadBase64File(buffer, name);
				logger.info("下载并上传\"" + filePath + "\"成功: " + uploadBase64File);
				if (null == uploadBase64File) {
					return ResultBuilder.failResult("上传文件失败:" + filePath);
					// 工程版可以传多个视频地址,但是locman只保存一个
				} else if (VIDEO.equals(type)) {
					newFilePaths.add(uploadBase64File);
					return ResultBuilder.successResult(newFilePaths, "");
				} else {
					newFilePaths.add(uploadBase64File);
				}
			}

			return ResultBuilder.successResult(newFilePaths, "");

		} catch (Exception e) {
			logger.error("下载并上传 \"" + filePaths + "\"失败");
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * @Description:在数据库中校验设备编号与序列号是否重复
	 * @param deviceNumber
	 *            设备编号 serialNumber 序列号
	 * @return
	 * @return
	 */

	private Result<Boolean> checkExist(String deviceNumber, String serialNumber) {
		if (StringUtils.isNotBlank(deviceNumber)) {
			RpcResponse<Boolean> checkDeviceNumberResult = deviceDataStorageQueryService
					.checkDeviceNumberExist(deviceNumber, null);
			if (!checkDeviceNumberResult.isSuccess()) {
				logger.error("[addDeviceData->error:校验设备编号是否存在失败]");
				return ResultBuilder.failResult("校验设备编号是否存在失败");
			} else if (checkDeviceNumberResult.getSuccessValue()) {
				logger.error("[addDeviceData->error:已存在该设备编号]");
				return ResultBuilder.failResult("已存在该设备编号:" + deviceNumber);
			}
		}
		// TODO 暂时允许设施序列号为空
		if (StringUtils.isNotBlank(serialNumber)) {
			RpcResponse<Boolean> checkSerialNumberResult = deviceDataStorageQueryService
					.checkSerialNumberExist(serialNumber, null);
			if (!checkSerialNumberResult.isSuccess()) {
				logger.error("[addDeviceData->error:校验设施序列号是否存在失败]");
				return ResultBuilder.failResult("校验设施序列号是否存在失败");
			} else if (checkSerialNumberResult.getSuccessValue()) {
				logger.error("[addDeviceData->error:已存在该设施序列号]");
				return ResultBuilder.failResult("已存在该设施序列号:" + serialNumber);
			}
		}
		return null;

	}



	public Result<Boolean> deleteById(String deviceDataId) {
		// 参数校验
		logger.info(String.format("[deleteById()->request params:%s]", deviceDataId));
		try {
			if (StringUtils.isBlank(deviceDataId)) {
				logger.error("[deleteById->error:id不能为空!!!]");
				return ResultBuilder.invalidResult();
			}
			String userId = UtilTool.getUserId(request);
			if (StringUtils.isBlank(userId)) {
				logger.error("[deleteById->error:请求失败,找不到用户信息]");
				return ResultBuilder.failResult("请求失败,找不到用户信息");
			}
			RpcResponse<Integer> result = deviceDataStorageCudService.deleteById(deviceDataId, userId);
			if (!result.isSuccess() || null == result.getSuccessValue() || result.getSuccessValue() < 0) {
				logger.error("[deleteById->error:数据删除失败!]");
				return ResultBuilder.failResult("数据删除失败!");
			} else {
				logger.info("[deleteById->success:数据删除成功!]");
				return ResultBuilder.successResult(true, "数据删除成功!");
			}
		} catch (Exception e) {
			logger.error("deleteById()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	public Result<Boolean> updateDeviceData(String deviceDataInfo) {
		// 参数校验
		logger.info(String.format("[updateDeviceData()->request params:%s]", deviceDataInfo));
		try {
			JSONObject json = JSONObject.parseObject(deviceDataInfo);
			RpcResponse<Object> containsParamKey = CheckParameterUtil.containsParamKey(logger, "updateDeviceData", json,
					"id", "deviceNumber", "deviceAddress", "longitude", "latitude", "serialNumber", "deviceTypeId",
					"areaId", "showExtend", "extend");
			String userId = UtilTool.getUserId(request);
			if (StringUtils.isBlank(userId)) {
				logger.error("[updateDeviceData->error:请求失败,找不到用户信息]");
				return ResultBuilder.failResult("请求失败,找不到用户信息");
			}
			if (null != containsParamKey) {
				logger.error(String.format("[updateDeviceData->error:%s]", containsParamKey.getMessage()));
				return ResultBuilder.failResult(containsParamKey.getMessage());
			} else {
				DeviceDataStorage deviceDataStorage = JSONObject.toJavaObject(json, DeviceDataStorage.class);
				if (StringUtils.isBlank(deviceDataStorage.getId())) {
					logger.error("[updateDeviceData->error:id不能为空]");
					return ResultBuilder.emptyResult();
				}
				deviceDataStorage.setUpdateBy(userId);

				/*
				 * String areaCode =
				 * AddressTool.getAreaCode(deviceDataStorage.getLongitude(),
				 * deviceDataStorage.getLatitude()); if
				 * (StringUtils.isBlank(areaCode)) {
				 * logger.error("[updateDeviceData->error:未能根据经纬度解析到区域信息]");
				 * return ResultBuilder.failResult("未能根据经纬度解析到区域信息"); }
				 * deviceDataStorage.setAreaId(areaCode);
				 */
				RpcResponse<Integer> updateDeviceData = deviceDataStorageCudService.updateDeviceData(deviceDataStorage);
				Integer successValue = updateDeviceData.getSuccessValue();
				if (!updateDeviceData.isSuccess() || null == successValue || 0 >= successValue) {
					logger.error("[updateDeviceData->error:数据修改失败!]");
					return ResultBuilder.failResult("数据修改失败!");
				} else {
					logger.info("[updateDeviceData->error:数据修改成功!]");
					return ResultBuilder.successResult(true, "数据修改成功!");
				}
			}

		} catch (Exception e) {
			logger.error("updateDeviceData()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	public Result<Map<String, Object>> synchronizeToFacilities(String synchronizationParam) {
		try {
			Result<Map<String, Object>> checkRequestParam = ExceptionChecked.checkRequestParam(synchronizationParam);
			if (null != checkRequestParam) {
				logger.error("参数信息不能为空");
				return checkRequestParam;
			}
			JSONObject paramJson = JSONObject.parseObject(synchronizationParam);

			String accessSecret = paramJson.getString("accessSecret");
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("接入方密钥不能为空");
				return ResultBuilder.emptyResult();
			}
			String userId = UtilTool.getUserId(request);
			if (StringUtils.isBlank(userId)) {
				logger.error("[updateDeviceData->error:请求失败,找不到用户信息]");
				return ResultBuilder.failResult("请求失败,找不到用户信息");
			}
			/*
			 * String userId = paramJson.getString("userId"); if
			 * (StringUtils.isBlank(userId)) { logger.error("用户id不能为空"); return
			 * ResultBuilder.emptyResult(); }
			 */
			String organizationId = paramJson.getString("organizationId");
			if (StringUtils.isBlank(organizationId)) {
				logger.error("组织id不能为空");
				return ResultBuilder.emptyResult();
			}
			JSONArray idArray = paramJson.getJSONArray("ids");
			if (idArray.isEmpty()) {
				logger.error("同步数据id不能为空");
				return ResultBuilder.emptyResult();
			}
			List<String> idList = idArray.toJavaList(String.class);
			RpcResponse<List<DeviceDataDto>> deviceDataStorages = deviceDataStorageQueryService
					.getDeviceDataStorageById(idList);
			if (null == deviceDataStorages) {
				logger.error("[synchronizeToFacilities()->fail:查询失败!!!]");
				return ResultBuilder.failResult("查询失败");
			} else if (!deviceDataStorages.isSuccess() || deviceDataStorages.getSuccessValue() == null) {
				logger.error(String.format("[synchronizeToFacilities()->fail:%s]", deviceDataStorages.getMessage()));
				return ResultBuilder.failResult(deviceDataStorages.getMessage());
			} else {
				int successCount = 0;
				int failCount = 0;
				logger.info(String.format("[synchronizeToFacilities()->success:%s]", deviceDataStorages.getMessage()));
				List<DeviceDataDto> deviceDataDtoList = deviceDataStorages.getSuccessValue();
				Map<String, Object> resultMap = Maps.newHashMap();
				deviceDataDtoListFor(accessSecret, userId, organizationId, successCount, failCount, deviceDataDtoList,
						resultMap);
				return ResultBuilder.successResult(resultMap, "数据同步完成!");
			}
		} catch (Exception e) {
			logger.error("getDeviceDataStorageById()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * @Description:
	 * @param accessSecret
	 * @param userId
	 * @param organizationId
	 * @param successCount
	 * @param failCount
	 * @param deviceDataDtoList
	 * @param resultMap
	 */

	private void deviceDataDtoListFor(String accessSecret, String userId, String organizationId, int successCount,
			int failCount, List<DeviceDataDto> deviceDataDtoList, Map<String, Object> resultMap) {
		for (DeviceDataDto deviceDataDto : deviceDataDtoList) {

			RpcResponse<Boolean> result = deviceDataStorageCudService.synchronizeToFacilities(deviceDataDto,
					accessSecret, userId, organizationId);

			DeviceDataStorage deviceDataStorage = new DeviceDataStorage();
			deviceDataStorage.setSynchBy(userId);
			deviceDataStorage.setId(deviceDataDto.getId());

			if (!result.isSuccess()) {
				logger.error(String.format("[synchronizeToFacilities()->fail:%s]", result.getMessage()));
				// error 修改同步状态为同步异常
				deviceDataStorage.setSynchronizationState("error");
				if (null != result.getException()) {
					deviceDataStorage.setErrorInfo("系统异常");
				} else {
					deviceDataStorage.setErrorInfo(result.getMessage());
				}
				deviceDataStorageCudService.updateDeviceData(deviceDataStorage);
				++failCount;
			} else {
				logger.info(String.format("[synchronizeToFacilities()->success:%s]", result.getMessage()));
				// disable 修改同步状态为已同步,不能再同步
				deviceDataStorage.setSynchronizationState("disable");
				deviceDataStorageCudService.updateDeviceData(deviceDataStorage);
				++successCount;
			}
		}

		resultMap.put("success", successCount);
		resultMap.put("fail", failCount);
	}


	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	/*
	 * public String deleteUnusedFile(int size) {
	 * 
	 * try { int total = deviceDataStorageCudService.getUnusedFileCount();
	 * logger.info("需要删除的文件总数:" + total); Pages<Object> page = new Pages<>();
	 * page.setPageSize(size); page.setTotal(total); int pages =
	 * page.getPages(); int needDeleteNum = 0; int deleteNum = 0; int updateNum
	 * = 0;
	 * 
	 * for (int i = 0; i < pages; i++) {
	 * 
	 * List<String> deleteUnusedFile =
	 * deviceDataStorageCudService.deleteUnusedFile(size); needDeleteNum +=
	 * deleteUnusedFile.size(); List<String> deleteFiles =
	 * FastDfsUtilTest.deleteFiles(deleteUnusedFile); deleteNum +=
	 * deleteFiles.size(); if (!deleteFiles.isEmpty()) {
	 * 
	 * int updateDeleteState =
	 * deviceDataStorageCudService.updateDeleteState(deleteFiles); updateNum +=
	 * updateDeleteState; logger.info(updateDeleteState); }
	 * 
	 * Thread.sleep(2000);
	 * 
	 * }
	 * 
	 * return "需要删除的路径数量:" + needDeleteNum + ",已删除:" + deleteNum + "已修改:" +
	 * updateNum ;
	 * 
	 * } catch (Exception e) { // TODO: handle exception return "" + e; } //
	 * TODO Auto-generated method stub
	 * 
	 * }
	 */

}
