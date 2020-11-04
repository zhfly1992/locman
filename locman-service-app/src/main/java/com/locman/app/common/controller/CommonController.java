package com.locman.app.common.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.locman.app.common.service.CommonService;
import com.run.entity.common.Result;

@RestController
@RequestMapping("common")
@SuppressWarnings("rawtypes")
public class CommonController {

	@Resource
	CommonService commonService;



	@RequestMapping("/updateImage")
	public Result<List<String>> uploadImg(@RequestParam("files") MultipartFile[] files) {
		return commonService.uploadFile(files);
	}



	@CrossOrigin("*")
	@GetMapping("/getMapValue/{type}")
	public Result getCommonValue(@PathVariable String type) {
		return commonService.getCommonValues(type);
	}



	@RequestMapping("/updateVideoAPP")
	public Result<List<String>> updateVideoAPP(@RequestParam("videoFiles") MultipartFile[] videoFiles) {
		return commonService.updateVideoAPP(videoFiles);
	}



	@RequestMapping("/deleteFile")
	public Result<Integer> deleteFile(@RequestBody String filePath) {
		return commonService.deleteFile(filePath);
	}
}
