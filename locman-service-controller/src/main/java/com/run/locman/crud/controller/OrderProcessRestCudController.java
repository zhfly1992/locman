/*
 * File name: OrderProcessRestCudController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tianming 2018年02月02日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.crud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.OrderProcessRestCudService;

/**
 * @Description: 工单流程cud Rest
 * @author: 田明
 * @version: 1.0, 2018年02月02日
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = UrlConstant.ORDER_PROCESS_CONFIG)
public class OrderProcessRestCudController {

    @Autowired
    public OrderProcessRestCudService orderProcessRestCudService;


    @RequestMapping(value = UrlConstant.ADD_ORDERPROCESS, method = RequestMethod.POST)
    public Result<String> addOrderProcess(@RequestBody String orderProcessInfo) {
        return orderProcessRestCudService.addOrderProcess(orderProcessInfo);
    }

    @RequestMapping(value = UrlConstant.UPDATE_STATE, method = RequestMethod.POST)
    public Result<String> updateState(@RequestBody String uodateInfo) {
        return orderProcessRestCudService.updateState(uodateInfo);
    }
    
    @RequestMapping(value = UrlConstant.PARSE_ORDERPROCESS_TMPLATE, method = RequestMethod.POST)
    public Result<List<JSONObject>> parseXmlTmplate(@RequestParam("file") MultipartFile file) {
        return orderProcessRestCudService.parseXmlTmplate(file);
    }

    @RequestMapping(value = UrlConstant.UPDATE_ORDERPROCESS, method = RequestMethod.POST)
    public Result<String> updateOrderProcess(@RequestBody String orderProcessInfo) {
        return orderProcessRestCudService.updateOrderProcess(orderProcessInfo);
    }
    
    @RequestMapping(value = UrlConstant.SAVE_ORDERPROCESS_TMPLATE, method = RequestMethod.POST)
    public Result<String> saveXmlTmplate(@RequestParam("file") MultipartFile file, @PathVariable("accessSecret") String accessSecret) {
        return orderProcessRestCudService.saveXmlTmplate(file, accessSecret);
    }
}
