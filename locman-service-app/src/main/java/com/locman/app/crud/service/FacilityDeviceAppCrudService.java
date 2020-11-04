package com.locman.app.crud.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.locman.app.base.BaseAppController;
import com.locman.app.common.service.CommonService;
import com.locman.app.common.util.Constant;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.base.query.FacilitesService;

@Service
public class FacilityDeviceAppCrudService extends BaseAppController {

	@Autowired
	private FacilitesService	facilitiesService;

	@Autowired
	private CommonService		commonService;



	/**
	 * 修改设施扩展属性 <method description>
	 *
	 * @param reqParam
	 * @return
	 */
	public Result<String> updateFacilitiesExtension(String reqParam, String id) {
		try {
			if (StringUtils.isEmpty(id)) {
				return ResultBuilder.invalidResult();
			}
			Result<String> result = ExceptionChecked.checkRequestParam(reqParam);
			if (result != null) {
				return ResultBuilder.invalidResult();
			}
			JSONObject jsonObject = JSONObject.parseObject(reqParam);
			if (!jsonObject.containsKey("showExtend")) {
				return ResultBuilder.noBusinessResult();
			}
			String extendString = jsonObject.getString("showExtend");
			if (StringUtils.isEmpty(extendString) || StringUtils.isEquals(extendString, "[]")) {
				return ResultBuilder.failResult("设施扩展属性不能为空");
			}
			if (StringUtils.isEmpty(extendString) || StringUtils.isEquals(extendString, "[]")) {
				return ResultBuilder.failResult("设施扩展属性不能为空");
			}
			String token = request.getHeader("token");
			RpcResponse<Map<String, Object>> response = facilitiesService.getFacilitesInfoByFacId(id, token);
			Map<String, Object> respMap = response.getSuccessValue();
			if (respMap == null || respMap.isEmpty()) {
				return ResultBuilder.failResult("设施信息为空");
			}
			// 设施对象
			JSONObject facJSON = (JSONObject) JSONObject.toJSON(respMap);
			JSONObject extendJSONS = castFacExtend(extendString);
			if(extendJSONS.isEmpty() || StringUtils.isEmpty(extendJSONS.toJSONString()) || StringUtils.isEquals(extendJSONS.toJSONString(), "[]")) {
				return ResultBuilder.failResult("设施属性解析失败");
			}
			facJSON.put("extend", extendJSONS.getString("extend"));
			facJSON.put("showExtend", extendJSONS.getString("showExtend"));
			String handResut = commonService.requestRestPut(Constant.LOCMAN_PORT, "facilities/updateFacilities", facJSON.toJSONString());
			Map<String, String> resultMap = commonService.checkResult(JSONObject.parseObject(handResut));
			if(resultMap.containsKey("success")) {
				String value = resultMap.get("success");
				return ResultBuilder.successResult(value, "设施扩展属性修改成功");
			}
			return ResultBuilder.failResult(resultMap.get("error"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 转换设施扩展属性与扩展属性值 <method description>
	 *
	 * @param extend
	 * @param extendString
	 * @return
	 */
	public JSONObject castFacExtend(String extendString) {
		JSONObject resultJSON = new JSONObject();
		JSONArray extendJSON = JSONArray.parseArray(extendString);
		JSONObject extend = new JSONObject();
		JSONArray showExtend = new JSONArray();
		for (Object object : extendJSON) {
			JSONObject tempShowExtend = null;
			JSONObject tempJSON = (JSONObject) object;
			String type = tempJSON.getString("dataType");
			String value = tempJSON.getString("value");
			String sign = tempJSON.getString("sign");
			String initialValue = tempJSON.getString("initialValue");
			if (StringUtils.isEquals(type, "checkbox")) {//扩展属性类型为checkbox时解析属性及属性值
				String[] values = value.split(",");
				for (String str : values) {
					tempShowExtend = new JSONObject();
					extend.put(sign + "_" + str, true);
					if (!StringUtils.isEmpty(initialValue) && !StringUtils.isEquals(initialValue, "[]")) {
						JSONArray initValues = JSONArray.parseArray(initialValue);
						for (Object obj : initValues) {
							JSONObject tempInit = (JSONObject) obj;
							if (StringUtils.isEquals(str, tempInit.getString("value"))) {
								value = tempInit.getString("name");
								break;
							}
						}
						tempShowExtend.put("name", tempJSON.getString("name"));
						tempShowExtend.put("type", type);
						tempShowExtend.put("value", value);
						showExtend.add(tempShowExtend);
					}
				}
			} else {
				tempShowExtend = new JSONObject();
				extend.put(sign, value);
				tempShowExtend.put("name", tempJSON.getString("name"));
				tempShowExtend.put("type", type);
				tempShowExtend.put("value", value);
				showExtend.add(tempShowExtend);
			}
		}
		resultJSON.put("extend", extend);
		resultJSON.put("showExtend", showExtend);
		return resultJSON;
	}
}
