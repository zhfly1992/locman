package com.run.locman.query.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.Result;
import com.run.locman.constants.UrlConstant;
import com.run.locman.query.service.VerificationCodeRestService;

@RestController
@CrossOrigin(value="*")
@RequestMapping(value = UrlConstant.VERIFICATION)
public class VerificationCodeController {
	
	
	@Autowired
	VerificationCodeRestService verificationCodeRestService;
	
	
	
	
    @GetMapping(UrlConstant.GETPIC)
    public Result<Map<String,Object>>  getPic() {
        return verificationCodeRestService.getPic();
    }


    @PostMapping(UrlConstant.CHECKCAPCODE)
    public Result<Map<String,Object>> checkcapcode(@RequestBody JSONObject json) {
        return verificationCodeRestService.checkcapcode(json);
    }   

}
