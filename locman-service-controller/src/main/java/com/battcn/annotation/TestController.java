/*
 * File name: BookController.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2018年8月17日 ... ...
 * ...
 *
 ***************************************************/

package com.battcn.annotation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.run.entity.common.Result;
import com.run.entity.tool.ResultBuilder;

/**
 * @Description:
 * @author: zhabing
 * @version: 1.0, 2018年8月17日
 */
@RestController
@RequestMapping("/books")
public class TestController {

	@CacheLock(prefix = "books")
	@GetMapping
	public Result<String> query(@CacheParam(name = "token") @RequestParam String token) {
		return ResultBuilder.successResult(token, "操作成功！！！");
	}



	@CacheLock(prefix = "books")
	@PostMapping("/queryJson")
	public Result<String> queryJson(@CacheParam @RequestBody String json) {

		return ResultBuilder.successResult(json, "操作成功！！！");
	}



	@PostMapping("/queryJson1")
	public Result<String> queryJson1(@RequestBody String json) {
		return ResultBuilder.successResult(json, "操作成功！！！");
	}

}
