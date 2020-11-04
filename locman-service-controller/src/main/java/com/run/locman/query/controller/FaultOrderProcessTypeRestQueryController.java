/*
* File name: FaultOrderProcessTypeRestQueryController.java								
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
* 1.0			田明		2017年12月07日
* ...			...			...
*
***************************************************/
package com.run.locman.query.controller;

import com.run.entity.common.Result;
import com.run.locman.api.entity.FaultOrderProcessType;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.FaultOrderProcessTypeRestQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 获取故障类型 
 * @author: 田明
 * @version: 1.0, 2017年12月07日
 */
@RestController
@RequestMapping(UrlConstant.ORDER_FAULT)
public class FaultOrderProcessTypeRestQueryController {

    @Autowired
    private FaultOrderProcessTypeRestQueryService faultOrderProcessTypeRestQueryService;

    @CrossOrigin(origins = "*")
    @PostMapping(UrlConstant.GET_FAULT_ORDER_TYPE)
    public Result<List<FaultOrderProcessType>>getFaultOrderTypeList(){
        return faultOrderProcessTypeRestQueryService.getFaultOrderTypeList();
    }
}
