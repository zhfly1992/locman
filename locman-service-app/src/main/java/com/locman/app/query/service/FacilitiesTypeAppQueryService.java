package com.locman.app.query.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.locman.app.entity.vo.FacilitiesTypeVo;
import com.locman.app.entity.vo.FacilitiesVo;
import com.locman.app.common.service.CommonService;
import com.locman.app.common.util.Constant;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.query.service.FacilitiesQueryService;

@Service
public class FacilitiesTypeAppQueryService {

	@Autowired
	private FacilitiesQueryService		facilitiesQueryService;
	@Autowired
	private CommonService				commonService;



	/**
	 *
	 * <method description> 根据接入方钥查询所有设施类型
	 * 
	 * @return 设施类型集合
	 */
	public Result<List<FacilitiesTypeVo>> findFacilitiesTypes(String accessSecret) {
		List<FacilitiesTypeVo> facilitiesTypeList = new ArrayList<FacilitiesTypeVo>();
		try {
			JSONObject parseObject = new JSONObject();
			parseObject.put("accessSecret", accessSecret);
			String resultHttp = commonService.requestRest(Constant.LOCMAN_PORT, "facilitiesType/facilitiesTypeList",
					parseObject.toJSONString());
			Map<String, String> map = commonService.checkResult(JSONObject.parseObject(resultHttp));
			FacilitiesTypeVo facilitiesType = null;
			if (map.containsKey("success")) {
				JSONArray array = JSONObject.parseArray(map.get("success"));
				if (array != null && !array.isEmpty()) {
					for (Object object : array) {
						JSONObject jsonObject = (JSONObject) object;
						facilitiesType = new FacilitiesTypeVo();
						facilitiesType.setFacilitiesId(jsonObject.getString("id"));
						facilitiesType.setFacilityTypeAlias(jsonObject.getString("facilityTypeAlias"));
						if(!"enabled".equals(jsonObject.getString("manageState"))) {
							continue;
						}
						facilitiesType.setManageState(jsonObject.getString("manageState"));
						facilitiesTypeList.add(facilitiesType);
					}
				}
			}
			if (facilitiesTypeList != null && !facilitiesTypeList.isEmpty()) {
				return ResultBuilder.successResult(facilitiesTypeList, "查询成功");
			}
			return ResultBuilder.failResult("查询失败");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * <method description> 获取设施地图显示数据
	 *
	 * @param param
	 * @return
	 */
	public Result<List<FacilitiesVo>> findMapFacilities(String param) {
		try {
			Result<List<FacilitiesVo>> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return result;
			}
			JSONObject jsonObject = JSONObject.parseObject(param);
			if (!jsonObject.containsKey("longitude")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!jsonObject.containsKey("latitude")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!jsonObject.containsKey("scopeValue")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!jsonObject.containsKey("codeAddress")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!jsonObject.containsKey("facilitiesTypeId")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!jsonObject.containsKey("organizationId")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!jsonObject.containsKey("accessId")) {
				return ResultBuilder.noBusinessResult();
			}
			String longitude = jsonObject.getString("longitude");
			String latitude = jsonObject.getString("latitude");
			String accessSecret = jsonObject.getString("accessId");
			if (longitude == null || "".equals(longitude) || latitude == null || "".equals(latitude)) {
				return ResultBuilder.failResult("经纬度数据异常");
			}
			Double Longitude = Double.parseDouble(longitude);
			Double Latitude = Double.parseDouble(latitude);
			if (Latitude > 90 || Latitude < -90) {
				return ResultBuilder.failResult("纬度异常");
			}
			if (Longitude > 180 || Longitude < -180) {
				return ResultBuilder.failResult("经度异常");
			}
			Integer scopeValue = jsonObject.getInteger("scopeValue");
			if (scopeValue <= 0) {
				return ResultBuilder.failResult("搜索范围异常");
			} else {
				scopeValue = scopeValue * 100;
			}
			String facilitiesTypeId = jsonObject.getString("facilitiesTypeId");
			String codeAddress = jsonObject.getString("codeAddress");
			String organizationId = jsonObject.getString("organizationId");
			RpcResponse<List<Map<String, Object>>> rpcResponse = null;
			List<Map<String, Object>> facilitiesCodeList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> addressList = new ArrayList<Map<String, Object>>();
			System.out.println(param.toString());
			if ((facilitiesTypeId == null || "".equals(facilitiesTypeId))
					&& (codeAddress == null || "".equals(codeAddress))) {
				rpcResponse = facilitiesQueryService.queryMapFacilitiesToApp(longitude, latitude, scopeValue,
						accessSecret, facilitiesTypeId, codeAddress, organizationId, codeAddress);
				if (rpcResponse.getSuccessValue() != null) {
					return ResultBuilder.successResult(castFacilities(rpcResponse.getSuccessValue()),
							rpcResponse.getMessage());
				}
				return ResultBuilder.failResult(rpcResponse.getMessage());
			} else {
				if (codeAddress != null && !"".equals(codeAddress)) {
					rpcResponse = facilitiesQueryService.queryMapFacilitiesToApp("", "", 0, accessSecret,
							facilitiesTypeId, codeAddress, organizationId, "");
					facilitiesCodeList = rpcResponse.getSuccessValue();
					rpcResponse = facilitiesQueryService.queryMapFacilitiesToApp("", "", 0, accessSecret,
							facilitiesTypeId, "", organizationId, codeAddress);
					addressList = rpcResponse.getSuccessValue();
					if (facilitiesCodeList == null && addressList == null) {
						return ResultBuilder.failResult(rpcResponse.getMessage());
					}
					List<FacilitiesVo> facCodeList = castFacilities(facilitiesCodeList);
					List<FacilitiesVo> addrList = castFacilities(addressList);
					if (facCodeList == null && addrList != null) {
						return ResultBuilder.successResult(addrList, rpcResponse.getMessage());
					}
					if (facCodeList != null && addrList == null) {
						return ResultBuilder.successResult(facCodeList, rpcResponse.getMessage());
					}
					for (FacilitiesVo facVo : facCodeList) {
						for (FacilitiesVo fVo : addrList) {
							if (facVo.getFacilitiesId().equals(fVo.getFacilitiesId())) {
								addrList.remove(fVo);
								break;
							}
						}
					}
					if (addrList.size() > 0) {
						facCodeList.addAll(addrList);
					}
					return ResultBuilder.successResult(facCodeList, rpcResponse.getMessage());
				}
				rpcResponse = facilitiesQueryService.queryMapFacilitiesToApp("", "", 0, accessSecret, facilitiesTypeId,
						codeAddress, organizationId, codeAddress);
				if (rpcResponse.getSuccessValue() != null) {
					return ResultBuilder.successResult(castFacilities(rpcResponse.getSuccessValue()),
							rpcResponse.getMessage());
				}
				return ResultBuilder.failResult(rpcResponse.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}

	}



	public List<FacilitiesVo> castFacilities(List<Map<String, Object>> objList) {
		if (objList != null) {
			List<FacilitiesVo> facilitiesVos = new ArrayList<FacilitiesVo>();
			FacilitiesVo facilitiesVo = null;
			for (Map<String, Object> map : objList) {
				facilitiesVo = new FacilitiesVo();
				facilitiesVo.setAddress((String) map.get("address"));
				facilitiesVo.setFacilitiesCode((String) map.get("facilitiesCode"));
				facilitiesVo.setFacilitiesId((String) map.get("id"));
				facilitiesVo.setLatitude((String) map.get("latitude"));
				facilitiesVo.setLongitude((String) map.get("longitude"));
				facilitiesVo.setManageState((String) map.get("manageState"));
				facilitiesVo.setFacilityTypeIco((String) map.get("facilityTypeIco"));
				facilitiesVo.setFacilitiesTypeName((String) map.get("facilityTypeAlias"));
				facilitiesVo.setAlarmWorstLevel((Integer) map.get("alarmWorstLevel"));
				facilitiesVos.add(facilitiesVo);
			}
			return facilitiesVos;
		}
		return null;
	}

}
