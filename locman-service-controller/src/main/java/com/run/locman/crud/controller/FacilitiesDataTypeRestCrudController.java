/*
* File name: FacilitiesDataTypeRestCrudController.java								
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
* 1.0			田明		2017年08月31日
* ...			...			...
*
***************************************************/
package com.run.locman.crud.controller;

import com.run.entity.common.Result;
import com.run.locman.api.entity.FacilitiesDataType;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.FacilitiesDataTypeRestCudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 设施扩展管理
 * @author: 田明
 * @version: 1.0, 2017年08月31日
 */
@RestController
@RequestMapping(UrlConstant.FACILITES)
public class FacilitiesDataTypeRestCrudController {

    @Autowired
    private FacilitiesDataTypeRestCudService facilitiesDataTypeRestCudService;

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/switchFacilitiesDataTypeState")
    public Result<FacilitiesDataType> switchFacilitiesDataTypeState(@RequestBody String param) {
        return facilitiesDataTypeRestCudService.switchFacilitiesDataTypeState(param);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/updateFacilityDataType")
    public Result<FacilitiesDataType> updateFacilityDataType(@RequestBody String param) {
        return facilitiesDataTypeRestCudService.updateFacilityDataType(param);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/addFacilityDataType")
    public Result<FacilitiesDataType> addFacilityDataType(@RequestBody String param) {
        return facilitiesDataTypeRestCudService.addFacilitiesDataType(param);
    }
}
