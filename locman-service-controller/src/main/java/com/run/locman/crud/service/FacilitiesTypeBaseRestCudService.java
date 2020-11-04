/*
 * File name: FacilitiesTypeBaseRestCudService.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 qulong 2017年8月31日 ... ...
 * ...
 *
 ***************************************************/

package com.run.locman.crud.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.run.common.util.ParamChecker;
import com.run.constants.common.ResultCodeConstants;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.FacilitiesTypeBaseCudService;
import com.run.locman.api.entity.FacilitiesTypeBase;
import com.run.locman.api.query.service.FacilitiesTypeBaseQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.FacilitiesContants;
import com.run.locman.filetool.FastDfsUtil;
import com.run.usc.base.query.UserBaseQueryService;

/**
 * @Description:基础设施类型cud
 * @author: qulong
 * @version: 1.0, 2017年8月31日
 */

@Service
public class FacilitiesTypeBaseRestCudService {

	@Autowired
	private FacilitiesTypeBaseQueryService	facilitiesTypeBaseQueryService;

	@Autowired
	private FacilitiesTypeBaseCudService	facilitiesTypeBaseCudService;

	@Autowired
	private UserBaseQueryService			userQueryRpcService;

	private Logger							logger	= Logger.getLogger(CommonConstants.LOGKEY);



	/**
	 * 
	 * @Description: 改变基础设施类型的管理状态
	 * @param params
	 * @return
	 */
	public Result<FacilitiesTypeBase> changeState(String params) {
		logger.info(String.format("[changeState()->request params:%s]", params));
		if (ParamChecker.isBlank(params)) {
			return ResultBuilder.emptyResult();
		}
		if (ParamChecker.isNotMatchJson(params)) {
			return ResultBuilder.invalidResult();
		}
		JSONObject paramsJson = JSONObject.parseObject(params);
		if (!paramsJson.containsKey(CommonConstants.ID)) {
			return ResultBuilder.noBusinessResult();
		}
		String id = paramsJson.getString(CommonConstants.ID);
		if (!paramsJson.containsKey(CommonConstants.STATE)) {
			return ResultBuilder.noBusinessResult();
		}
		String state = paramsJson.getString(CommonConstants.STATE);

		if (!paramsJson.containsKey(CommonConstants.USERID)) {
			return ResultBuilder.noBusinessResult();
		}
		String userId = paramsJson.getString(CommonConstants.USERID);

		try {
			@SuppressWarnings("rawtypes")
			RpcResponse userByUserIdResult = userQueryRpcService.getUserByUserId(userId);
			if (userByUserIdResult == null || !userByUserIdResult.isSuccess()) {
				logger.error("[changeState()->fail:用户信息查询失败]");
				return ResultBuilder.failResult("用户信息查询失败");
			}
			Map<String, String> map = Maps.newHashMap();
			map.put("id", id);
			map.put("manageState", state);
			map.put("createTime", DateUtils.formatDate(new Date()));
			map.put("createUser", userId);
			RpcResponse<FacilitiesTypeBase> updateFacilitiesTypeBaseResult = facilitiesTypeBaseCudService
					.updateFacilitiesTypeBase(map);
			if (updateFacilitiesTypeBaseResult != null && updateFacilitiesTypeBaseResult.isSuccess()) {
				logger.info("[changeState()->success:更新成功]");
				return ResultBuilder.successResult(null, "更新成功");
			}
			logger.error("[changeState()->fail:更新失败]");
			return ResultBuilder.failResult("更新失败");
		} catch (Exception e) {
			logger.error("changeState()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<FacilitiesTypeBase> add(String params) {

		logger.info(String.format("[add()->request params:%s]", params));
		if (ParamChecker.isBlank(params)) {
			return ResultBuilder.emptyResult();
		}
		if (ParamChecker.isNotMatchJson(params)) {
			return ResultBuilder.invalidResult();
		}
		JSONObject paramsJson = JSONObject.parseObject(params);
		if (!paramsJson.containsKey(CommonConstants.FACILITYTYPENAME)) {
			return ResultBuilder.noBusinessResult();
		}
		String facilityTypeName = paramsJson.getString(CommonConstants.FACILITYTYPENAME);

		if (!paramsJson.containsKey(CommonConstants.USERID)) {
			return ResultBuilder.noBusinessResult();
		}
		String userId = paramsJson.getString(CommonConstants.USERID);

		if (!paramsJson.containsKey(CommonConstants.FACILITYTYPEICO)) {
			return ResultBuilder.noBusinessResult();
		}
		String facilityTypeIco = paramsJson.getString(CommonConstants.FACILITYTYPEICO);

		try {
			@SuppressWarnings("rawtypes")
			RpcResponse userByUserIdResult = userQueryRpcService.getUserByUserId(userId);
			if (userByUserIdResult == null || !userByUserIdResult.isSuccess()) {
				logger.error("[add()->fail:用户信息查询失败]");
				return ResultBuilder.failResult("用户信息查询失败");
			}

			RpcResponse<Integer> validFacilitiesTypeNameResult = facilitiesTypeBaseQueryService
					.validFacilitiesTypeName(facilityTypeName);
			if (!validFacilitiesTypeNameResult.isSuccess()) {
				logger.error(String.format("[add()->fail:%s]", validFacilitiesTypeNameResult.getMessage()));
				return ResultBuilder.failResult(validFacilitiesTypeNameResult.getMessage());
			}

			FacilitiesTypeBase facilitiesTypeBase = new FacilitiesTypeBase();
			facilitiesTypeBase.setId(UtilTool.getUuId());
			facilitiesTypeBase.setFacilityTypeName(facilityTypeName);
			facilitiesTypeBase.setFacilityTypeIco(facilityTypeIco);
			facilitiesTypeBase.setManageState("enabled");
			facilitiesTypeBase.setCreateTime(DateUtils.formatDate(new Date()));
			facilitiesTypeBase.setCreateUser(userId);

			RpcResponse<FacilitiesTypeBase> addFacilitiesTypeBaseResult = facilitiesTypeBaseCudService
					.addFacilitiesTypeBase(facilitiesTypeBase);
			if (addFacilitiesTypeBaseResult != null && addFacilitiesTypeBaseResult.isSuccess()) {
				logger.info("[add()->success:基础设施类型添加成功]");
				return ResultBuilder.successResult(addFacilitiesTypeBaseResult.getSuccessValue(), "基础设施类型添加成功");
			}
			logger.error("[add()->fail:基础设施类型添加失败]");
			return ResultBuilder.failResult("基础设施类型添加失败");
		} catch (Exception e) {
			logger.error("add()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<FacilitiesTypeBase> update(String params) {
		logger.info(String.format("[update()->request params:%s]", params));
		if (ParamChecker.isBlank(params)) {
			return ResultBuilder.emptyResult();
		}
		if (ParamChecker.isNotMatchJson(params)) {
			return ResultBuilder.invalidResult();
		}
		JSONObject paramsJson = JSONObject.parseObject(params);
		if (StringUtils.isBlank(paramsJson.getString(CommonConstants.ID))) {
			logger.error("[update()->fail:基础设施类型修改参数id不能为空!]");
			return ResultBuilder.failResult("基础设施类型修改参数id不能为空!");
		}
		if (StringUtils.isBlank(paramsJson.getString(CommonConstants.FACILITYTYPENAME))) {
			logger.error("[update()->fail:基础设施类型修改参数facilityTypeName不能为空!]");
			return ResultBuilder.failResult("基础设施类型修改参数facilityTypeName不能为空!");
		}
		if (StringUtils.isBlank(paramsJson.getString(CommonConstants.FACILITYTYPEICO))) {
			logger.error("[update()->fail:基础设施类型修改参数facilityTypeIco不能为空!]");
			return ResultBuilder.failResult("基础设施类型修改参数facilityTypeIco不能为空!");
		}
		if (StringUtils.isBlank(paramsJson.getString(CommonConstants.USERID))) {
			logger.error("[update()->fail:基础设施类型修改参数userId不能为空!]");
			return ResultBuilder.failResult("基础设施类型修改参数userId不能为空!");
		}

		String id = paramsJson.getString(CommonConstants.ID);
		String facilityTypeName = paramsJson.getString(CommonConstants.FACILITYTYPENAME);
		String facilityTypeIco = paramsJson.getString(CommonConstants.FACILITYTYPEICO);
		String userId = paramsJson.getString(CommonConstants.USERID);

		RpcResponse<Boolean> check = facilitiesTypeBaseQueryService.updateFacilitiesTypeBaseCheck(paramsJson);
		if (check.isSuccess() && !check.getSuccessValue()) {
			logger.error("[update()->fail:该基础设施类型重复]");
			return ResultBuilder.failResult(check.getMessage());
		} else if (!check.isSuccess()) {
			logger.error("[update()->fail:校验设施类型名称重复性失败!]");
			return ResultBuilder.failResult("校验设施类型名称重复性失败!");
		}

		try {
			RpcResponse<FacilitiesTypeBase> facilitiesTypeBaseById = facilitiesTypeBaseQueryService
					.getFacilitiesTypeBaseById(id);
			if (facilitiesTypeBaseById != null && facilitiesTypeBaseById.isSuccess()) {
				if (!CommonConstants.ENABLED.equals(facilitiesTypeBaseById.getSuccessValue().getManageState())) {
					logger.error("[update()->fail:该基础设施类型已被禁用]");
					return ResultBuilder.failResult("该基础设施类型已被禁用");
				}
			} else {
				logger.error("[update()->fail:基础设施类型查询失败]");
				return ResultBuilder.failResult("基础设施类型查询失败");
			}
			@SuppressWarnings("rawtypes")
			RpcResponse userByUserIdResult = userQueryRpcService.getUserByUserId(userId);
			if (userByUserIdResult == null || !userByUserIdResult.isSuccess()) {
				logger.error("[update()->fail:用户信息查询失败]");
				return ResultBuilder.failResult("用户信息查询失败");
			}

			Map<String, String> map = Maps.newHashMap();
			map.put("id", id);
			map.put("facilityTypeName", facilityTypeName);
			map.put("facilityTypeIco", facilityTypeIco);
			map.put("createTime", DateUtils.formatDate(new Date()));
			map.put("createUser", userId);
			RpcResponse<FacilitiesTypeBase> updateFacilitiesTypeBaseResult = facilitiesTypeBaseCudService
					.updateFacilitiesTypeBase(map);
			if (updateFacilitiesTypeBaseResult != null && updateFacilitiesTypeBaseResult.isSuccess()) {
				logger.info("[update()->success:修改成功]");
				return ResultBuilder.successResult(null, "修改成功");
			}
			logger.error("[update()->fail:修改失败]");
			return ResultBuilder.failResult("修改失败");
		} catch (Exception e) {
			logger.error("update()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * @Description:单文件上传
	 * @param file
	 * @return
	 */
	public Result<String> uploadFile(MultipartFile mFile) {
		if (mFile == null) {
			logger.error("[uploadFile()->error:空参数]");
			return ResultBuilder.noBusinessResult();
		}
		logger.info(String.format("[uploadFile()->file name:%s]", mFile.getName()));
		try {
			File file = new File("/" + mFile.getOriginalFilename());

			FileUtils.copyInputStreamToFile(mFile.getInputStream(), file);

			String facilityTypeIco = FastDfsUtil.uploadFile(file, file.getName(), file.length());
			if (facilityTypeIco != null && !"".equals(facilityTypeIco)) {
				logger.info("[uploadFile()->success:图片上传成功]");
				return ResultBuilder.successResult(facilityTypeIco, "图片上传成功");
			}
			logger.error("[uploadFile()->fail:图片上传失败]");
			return ResultBuilder.failResult("图片上传失败");
		} catch (IOException e) {
			logger.error(String.format("[uploadFile()->exception:%s]", e));
			return ResultBuilder.exceptionResult(e);
		} catch (Exception e) {
			logger.error("uploadFile()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:使用base64进行文件上传(限制小于10M的.jpg,.jpge,.png格式的图片,支持多文件上传)
	 * @param {file:[{imgStr:"",imgLength:"",
	 *            imgName:""},{imgStr:"",imgLength:"", imgName:""}]}
	 * @return
	 */
	public Result<List<String>> uploadBase64File(String jsonFile) {
		logger.info(String.format("[uploadBase64File()->request params:%s]", jsonFile));
		if (jsonFile == null) {
			return ResultBuilder.noBusinessResult();
		}
		try {
			List<String> picUrl = new ArrayList<>();
			List<String> failList = new ArrayList<>();
			JSONArray jsonArray = JSONObject.parseArray(jsonFile);

			uploadBase64FileFor(picUrl, failList, jsonArray);

			if (picUrl != null && picUrl.size() > 0 && picUrl.size() == jsonArray.size() && !picUrl.contains(null)) {
				logger.info("[uploadBase64File()->success:图片上传成功]");
				return ResultBuilder.successResult(picUrl, "图片上传成功");
			}
			if (picUrl != null && picUrl.size() > 0 && failList != null && failList.size() > 0) {
				// return ResultBuilder.successResult(picUrl, "部分图片上传成功" +
				// failList);
				logger.error("[uploadBase64File()->fail:部分图片上传失败]");
				return ResultBuilder.getResult(picUrl, ResultCodeConstants.RE_CODE_0050, "部分图片上传失败：" + failList, null);
			}
			logger.error("[uploadBase64File()->fail:图片上传失败]");
			return ResultBuilder.failResult("图片上传失败" + failList);
		} catch (IOException e) {
			logger.error("uploadBase64File()->exception", e);
			return ResultBuilder.exceptionResult(e);
		} catch (Exception e) {
			logger.error("uploadBase64File()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * @Description:
	 * @param picUrl
	 * @param failList
	 * @param jsonArray
	 * @throws IOException
	 * @throws MyException
	 */

	private void uploadBase64FileFor(List<String> picUrl, List<String> failList, JSONArray jsonArray)
			throws IOException, MyException {
		for (int i = 0; i < jsonArray.size(); i++) {
			// 得到每个图片
			JSONObject json = JSONObject.parseObject(jsonArray.get(i).toString());
			// 修改工单时执行此判断,防止工单修改后,保存时图片数据丢失
			if (json.containsKey("imgStr") && !StringUtils.isBlank(json.getString("imgStr"))) {
				String imgURL = json.getString("imgStr");
				String end = imgURL.substring(imgURL.lastIndexOf(".") + 1);
				if (imgURL.startsWith("http://")) {
					if (end.equalsIgnoreCase("jpg") || end.equalsIgnoreCase("jpeg") || end.equalsIgnoreCase("png")) {
						picUrl.add(imgURL);
						continue;
					}
				}
			}
			if (!json.containsKey("imgName") || StringUtils.isBlank(json.getString("imgName"))) {
				failList.add(json.getString("imgName") + "上传失败");
				continue;
			}

			String imgName = json.getString("imgName");
			String end = imgName.substring(imgName.lastIndexOf(".") + 1);

			if (!end.equalsIgnoreCase("jpg") && !end.equalsIgnoreCase("jpeg") && !end.equalsIgnoreCase("png")) {
				failList.add(imgName + "上传失败");
				continue;
			}
			if (!json.containsKey("imgStr") || StringUtils.isBlank(json.getString("imgStr"))) {
				failList.add(imgName + "上传失败");
				continue;
			}
			if (!json.containsKey("imgLength") || StringUtils.isBlank(json.getString("imgLength"))
					|| json.getString("imgLength").length() > 10485760) {
				failList.add(imgName + "上传失败");
				continue;
			}

			String imgStr = json.getString("imgStr");
			String imgLength = json.getString("imgLength");

			if (Integer.parseInt(imgLength) != imgStr.length()) {
				failList.add(imgName + "上传失败");
				continue;
			}
			// File file = new File("/" + imgName);
			String resultStr = imgStr.substring(imgStr.indexOf(",") + 1);
			byte[] stringToImage = Base64Utils.decodeFromString(resultStr);
			String facilityTypeIco = FastDfsUtil.uploadBase64File(stringToImage, imgName);
			if (facilityTypeIco != null) {
				picUrl.add(facilityTypeIco);
			} else {
				failList.add(imgName + "上传失败" + facilityTypeIco);
			}
		}
	}



	/**
	 * @Description:多个文件同时上传，建议不共用接口，为后期改进预留空间
	 * @param file
	 * @return
	 */
	public Result<List<String>> uploadFile(MultipartFile[] mFile) {
		if (mFile == null) {
			logger.error("[uploadFile()->error:空参数]");
			return ResultBuilder.noBusinessResult();
		}
		try {
			List<String> picUrl = new ArrayList<>();
			for (MultipartFile multipartFile : mFile) {
				File file = new File("/" + multipartFile.getOriginalFilename());

				FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);

				String facilityTypeIco = FastDfsUtil.uploadFile(file, file.getName(), file.length());
				picUrl.add(facilityTypeIco);
			}

			if (picUrl != null && picUrl.size() > 0 && picUrl.size() == mFile.length) {
				return ResultBuilder.successResult(picUrl, "图片上传成功");
			}
			return ResultBuilder.failResult("图片上传失败");
		} catch (IOException e) {
			logger.error("uploadFile()->exception", e);
			return ResultBuilder.exceptionResult(e);
		} catch (Exception e) {
			logger.error("uploadFile()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Integer> deleteFile(String filePath) {
		if (filePath == null) {
			logger.error("[deleteFile()->error:文件路径为空]");
			return ResultBuilder.noBusinessResult();
		}
		logger.info(String.format("[deleteFile()->文件路径:%s]", filePath));
		try {
			JSONObject json = JSONObject.parseObject(filePath);
			String path = json.getString("filePath");

			int deleteFile = FastDfsUtil.deleteFile(path);
			logger.info("[deleteFile()->success]");
			return ResultBuilder.successResult(deleteFile, "");
		} catch (Exception e) {
			logger.error("deleteFile()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<String> uploadVideos(MultipartFile mFile) {
		if (mFile == null) {
			logger.error("[uploadFile()->error:文件参数为空]");
			return ResultBuilder.noBusinessResult();
		}
		try {
			String originalFilename = mFile.getOriginalFilename();
			String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
			if (!"mp4".equalsIgnoreCase(suffix) && !"mov".equalsIgnoreCase(suffix)) {
				return ResultBuilder.failResult(String.format("文件:%s,不是规定的视频格式,仅支持mp4,mov格式的视频", originalFilename));
			}
			long size = mFile.getSize();
			if (size > FacilitiesContants.FIFTEEN_M) {
				return ResultBuilder.failResult(String.format("上传: %s 失败,视频文件不能大于15M", originalFilename));
			}

			byte[] bytes = mFile.getBytes();

			String videoUrl = FastDfsUtil.uploadBase64File(bytes, originalFilename);

			if (videoUrl != null) {
				return ResultBuilder.successResult(videoUrl, "视频上传成功");
			} else {
				return ResultBuilder.failResult("视频上传失败:" + originalFilename);
			}

		} catch (IOException e) {
			logger.error("uploadFile()->exception", e);
			return ResultBuilder.exceptionResult(e);
		} catch (Exception e) {
			logger.error("uploadFile()->exception", e);
			return ResultBuilder.exceptionResult(e);
		}
	}

}
