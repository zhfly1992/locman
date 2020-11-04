/*
* File name: FacilitiesDataTypeQueryController.java								
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
* 1.0			wangsheng		2017年8月8日
* ...			...			...
*
***************************************************/
package com.run.locman.query.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.github.pagehelper.PageInfo;
import com.run.entity.common.Result;
import com.run.locman.api.entity.FacilitiesDataType;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.FacilitiesDataTypeQueryRestService;

import java.util.List;

/**
 * @Description: 设施数据类型查询Contro
 * @author: wangsheng
 * @version: 1.0, 2017年8月8日
 */
@CrossOrigin(maxAge = 3600 , origins = "*")
@RestController
@RequestMapping(value = UrlConstant.FACILITY)
public class FacilitiesDataTypeQueryController {

	@Autowired
	FacilitiesDataTypeQueryRestService facilitiesDataTypeQueryRestService;

	/**
	 * @Description:不分页获取所有设施数据类型
	 * @param facilitiesDataTypeInfo
	 * @return public Result<PageInfo<FacilitiesDataType>>
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.GETALLFACILITIESDATATYPE, method = RequestMethod.POST)
	public Result<List<FacilitiesDataType>> getAllFacilitiesDataType(@RequestBody String facilitiesDataTypeInfo) {
		return facilitiesDataTypeQueryRestService.getAllFacilitiesDataType(facilitiesDataTypeInfo);
	}

	/**
	 * @Description:分页获取所有设施数据类型
	 * @param facilitiesDataTypeInfo
	 * @return public Result<PageInfo<FacilitiesDataType>>
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.FACILITIESDATATYPE_LIST, method = RequestMethod.POST)
	public Result<PageInfo<FacilitiesDataType>> getFacilitiesDataTypeList(@RequestBody String facilitiesDataTypeInfo) {
		return facilitiesDataTypeQueryRestService.getFacilitiesDataTypeList(facilitiesDataTypeInfo);
	}

	/**
	 * @Description:根据设施扩展id查询设施扩展详情
	 * @param id
	 * @return
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = UrlConstant.FIND_BY_FACDATATYPE_ID, method = RequestMethod.POST)
	public Result<FacilitiesDataType> findById(@RequestBody String id) throws Exception {
		return facilitiesDataTypeQueryRestService.getById(id);
	}

	/**
	 * @Description:校验设施扩展类型标识名,或显示名是否重复
	 * @param facilitiesDataTypeInfo "facilitiesTypeId", 设施类型id
							"sign" 标识名,
							"name" 显示名
	 * @return
	 */
	@RequestMapping(value = UrlConstant.VALID_FACILITIES_DATA_TYPE_NAME, method = RequestMethod.POST)
	public Result<Boolean> validFacilitiesDataTypeName(@RequestBody String facilitiesDataTypeInfo){
		return facilitiesDataTypeQueryRestService.validFacilitiesDataTypeName(facilitiesDataTypeInfo);
	}
	
}
