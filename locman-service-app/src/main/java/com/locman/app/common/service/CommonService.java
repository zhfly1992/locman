package com.locman.app.common.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.locman.app.base.BaseAppController;
import com.locman.app.common.util.Constant;
import com.locman.app.common.util.HttpClientTool;
import com.locman.app.fileTool.FastDFSUtil;
import com.run.activity.api.query.ActivityProgressQuery;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.entity.Device;
import com.run.locman.api.query.service.DeviceQueryService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;

@Service
@SuppressWarnings("rawtypes")
public class CommonService extends BaseAppController {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private ActivityProgressQuery	activityProgressQuery;

	@Autowired
	JSONObjectUtiles				jsonObjectUtil;
	@Autowired
	DeviceQueryService				deviceQueryService;

	@Value(value = "classpath:json/CommonObject.json")
	private Resource				resource;

	/** REST接口路径 */
	@Value("${locman.api.host}")
	private String					baseUrl;



	/**
	 * @Description:多个文件同时上传
	 * @param file
	 * @return
	 */
	public Result<List<String>> uploadFile(MultipartFile[] mFile) {
		if (mFile == null) {
			return ResultBuilder.noBusinessResult();
		}
		try {
			List<String> picUrl = new ArrayList<>();
			for (MultipartFile multipartFile : mFile) {
				File file = new File("/" + multipartFile.getOriginalFilename());
				FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
				String img = "";
				if (file != null) {
					img = FastDFSUtil.uploadFile(file, file.getName(), file.length());
					picUrl.add(img);
				}
			}
			if (picUrl != null && picUrl.size() > 0 && picUrl.size() == mFile.length) {
				return ResultBuilder.successResult(picUrl, "图片上传成功");
			}
			return ResultBuilder.failResult("图片上传失败");
		} catch (IOException e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * app上传视频 <method description>
	 *
	 * @param files
	 * @return
	 */
	public Result<List<String>> updateVideoAPP(MultipartFile[] files) {
		if (files == null) {
			return ResultBuilder.noBusinessResult();
		}
		try {
			List<String> videoUrls = new ArrayList<>();
			for (MultipartFile multipartFile : files) {
				if (multipartFile != null) {
					String originalFilename = multipartFile.getOriginalFilename();
					String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
					if (!"mp4".equalsIgnoreCase(suffix) && !"mov".equalsIgnoreCase(suffix)) {
						continue;
					}
					long size = multipartFile.getSize();
					if (size > FacilitiesContants.FIFTEEN_M) {
						continue;
					}
					byte[] bytes = multipartFile.getBytes();
					String videoUrl = FastDFSUtil.uploadBase64File(bytes, originalFilename);
					if (StringUtils.isNotEmpty(videoUrl)) {
						videoUrls.add(videoUrl);
					}
				}
			}
			if (videoUrls != null && !videoUrls.isEmpty() && videoUrls.size() > 0 && videoUrls.size() == files.length) {
				return ResultBuilder.successResult(videoUrls, "视频上传成功");
			} else if (videoUrls != null && !videoUrls.isEmpty() && videoUrls.size() > 0
					&& videoUrls.size() <= files.length) {
				return ResultBuilder.successResult(videoUrls, "视频未全部上传成功");
			} else {
				return ResultBuilder.failResult("视频上传失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 删除服务器上传文件 <method description>
	 *
	 * @param filePath
	 * @return 返回值-1失败,2未查找到图片(失败),0成功
	 */
	public Result<Integer> deleteFile(String filePath) {
		if (filePath == null) {
			logger.error("[deleteFile()->error:文件路径为空]");
			return ResultBuilder.noBusinessResult();
		}
		logger.info(String.format("[deleteFile()->文件路径:%s]", filePath));
		try {
			int deleteFile = -1;
			JSONObject json = JSONObject.parseObject(filePath);
			String path = json.getString("filePath");
			String[] filePaths = path.split(",");
			for (String pathStr : filePaths) {
				deleteFile = FastDFSUtil.deleteFile(pathStr);
				logger.info("[deleteFile()->success]");
			}

			return ResultBuilder.successResult(deleteFile, "");
		} catch (Exception e) {
			logger.error("deleteFile()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 获取工单是否未审批过 <method description>
	 *
	 * @param processId
	 * @return
	 */
	public String getProcessState(String processId) {
		JSONObject jsonParam = new JSONObject();
		jsonParam.put("processId", processId);
		String operaType = "";
		RpcResponse<Map<String, Object>> result = activityProgressQuery.getJudgmentProcess(jsonParam);
		if (result.isSuccess() && result.getSuccessValue() != null) {
			operaType = result.getSuccessValue().get("state").toString();
		}
		return operaType;
	}



	/**
	 * 获取公共参数类 <method description>
	 *
	 * @param type
	 * @return
	 */
	public Result getCommonValues(String type) {
		if (StringUtils.isBlank(type)) {
			return ResultBuilder.noBusinessResult();
		}
		JSONObject jsonObject = new JSONObject();
		JSONArray resutJson = new JSONArray();
		if (Constant.PROCESSTYPENAME.FAULT_TYPE_NAME.equals(type)) {
			String result = requestRest(Constant.LOCMAN_PORT, "order/faultOrder/getFaultOrderTypeList", null);
			if (result != null && !"".equals(result)) {
				jsonObject = JSONObject.parseObject(result);
				if (result != null) {
					Map<String, String> map = checkResult(jsonObject);
					if (map.containsKey("success")) {
						resutJson = JSONObject.parseArray(map.get("success"));
					}
				}
			}
		} else if (Constant.PROCESSTYPENAME.SIMPLE_TYPE_NAME.equals(type)) {
			String result = requestRest(Constant.LOCMAN_PORT, "order/simpleOrder/getOrderTypeList", null);
			if (result != null && !"".equals(result)) {
				jsonObject = JSONObject.parseObject(result);
				if (result != null) {
					Map<String, String> map = checkResult(jsonObject);
					if (map.containsKey("success")) {
						resutJson = JSONObject.parseArray(map.get("success"));
					}
				}
			}
		}
		if (resutJson.isEmpty()) {
			return ResultBuilder.failResult("查询失败");
		}
		return ResultBuilder.successResult(resutJson, "查询成功");
	}



	/**
	 * rest接口post访问公共方法 <method description>
	 *
	 * @param url
	 * @param parseObject
	 * @return
	 * @throws Exception
	 */
	public String requestRest(String port, String url, String parseObject) {
		String token = request.getHeader("token");
		if (parseObject == null) {
			parseObject = "";
		}
		String result = HttpClientTool.POSTMethodByJSON(baseUrl + port + url, parseObject, null, token);
		if (result == null || "".equals(result)) {
			logger.error("post-->rest访问请求失败: " + result);
			return null;
		}
		return result;
	}



	/**
	 * rest接口get访问公共方法 <method description>
	 *
	 * @param url
	 * @param parseObject
	 * @return
	 * @throws Exception
	 */
	public String requestRestGet(String port, String url, JSONObject parseObject)  {
		String token = request.getHeader("token");
		String result = null;
		try {
			result = HttpClientTool.GETMethodByJson(baseUrl + port + url, parseObject, token);
			if (result == null || "".equals(result)) {
				logger.error("get-->rest访问请求失败: " + result);
				return null;
			}
		} catch (Exception e) {
			logger.error("get-->rest访问请求异常: " + e);
			return null;
		}
		return result;
	}



	/**
	 * rest接口put访问公共方法 <method description>
	 *
	 * @param url
	 * @param parseObject
	 * @return
	 * @throws Exception
	 */
	public String requestRestPut(String port, String url, String parseObject) {
		String token = request.getHeader("token");
		if (parseObject == null) {
			parseObject = "";
		}
		String result = HttpClientTool.PUTMethodByJSON(baseUrl + port + url, parseObject, null, token);
		if (result == null || "".equals(result)) {
			logger.error("post-->rest访问请求失败: " + result);
			return null;
		}
		return result;
	}



	public Map<String, String> checkResult(JSONObject jsonObject) {
		Map<String, String> resultMap = Maps.newHashMap();
		JSONObject resultStatus = null;
		if (jsonObject != null) {
			resultStatus = jsonObject.getJSONObject("resultStatus");
			if (resultStatus == null) {
				logger.error("异常：" + jsonObject.toJSONString());
				resultMap.put("error", jsonObject.toJSONString());
			} else if (resultStatus != null && !"0000".equals(resultStatus.get("resultCode"))) {
				logger.error("异常：" + resultStatus.getString("resultMessage"));
				resultMap.put("error", resultStatus.getString("resultMessage"));
				if (resultStatus.getString("resultMessage") == null
						|| "null".equals(resultStatus.getString("resultMessage"))) {
					resultMap.put("error", "服务异常");
				}
			} else {
				String value = jsonObject.getString("value");
				if (value == null || "".equals(value)) {
					logger.error("rest请求返回空：" + resultStatus.getString("resultMessage"));
					resultMap.put("error", "返回数据失败");
				} else {
					resultMap.put("success", value);
				}
			}
		}
		return resultMap;
	}



	/**
	 * 验证是否是平衡告警设备 <method description>
	 *
	 * @param deviceTypeId
	 * @return
	 * @throws Exception
	 */
	public boolean isBalanceDevice(String deviceId) throws Exception {
		RpcResponse<Device> device = deviceQueryService.queryDeviceByDeviceId(deviceId);
		if (device.isSuccess() && null != device.getSuccessValue()) {
			return true;
		} else if (!device.isSuccess()) {
			throw new Exception(device.getMessage());
		}
		return false;
	}
}
