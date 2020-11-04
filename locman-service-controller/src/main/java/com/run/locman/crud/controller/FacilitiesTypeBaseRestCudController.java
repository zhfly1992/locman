/*
 * File name: FacilitiesTypeBaseRestCudController.java
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

package com.run.locman.crud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.run.entity.common.Result;
import com.run.locman.api.entity.FacilitiesTypeBase;
import com.run.locman.crud.service.FacilitiesTypeBaseRestCudService;


/**
 * @Description: 基础设施类型cud
 * @author: qulong
 * @version: 1.0, 2017年8月31日
 */
@RestController
@CrossOrigin(value = "*")
@RequestMapping(value = "/facilitiesTypeBase")
public class FacilitiesTypeBaseRestCudController {

	@Autowired
	private FacilitiesTypeBaseRestCudService facilitiesTypeBaseRestCudService;



	
	@PostMapping(value = "/changeState")
	public Result<FacilitiesTypeBase> changeState(@RequestBody String params) {
		return facilitiesTypeBaseRestCudService.changeState(params);
	}



	@PostMapping(value = "/add")
	public Result<FacilitiesTypeBase> add(@RequestBody String params) {
		return facilitiesTypeBaseRestCudService.add(params);
	}



	@PostMapping(value = "/update")
	public Result<FacilitiesTypeBase> update(@RequestBody String params) {
		return facilitiesTypeBaseRestCudService.update(params);
	}



	@PostMapping(value = "/uploadFile")
	public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
		return facilitiesTypeBaseRestCudService.uploadFile(file);
	}



	/**
	 * @Description:
	 * @author: guofeilong
	 * @version: 1.0, 2017年12月20日
	 */
	@PostMapping(value = "/uploadBaseFiles")
	public Result<List<String>> uploadBaseFile(@RequestParam("file") String file) {
		return facilitiesTypeBaseRestCudService.uploadBase64File(file);
	}
	
	
	@PostMapping(value = "/uploadFiles")
	public Result<List<String>> uploadFile(@RequestParam("file") MultipartFile[] file) {
		return facilitiesTypeBaseRestCudService.uploadFile(file);
	}

	@PostMapping(value = "/deleteFile")
	public Result<Integer> deleteFile(@RequestBody String params) {
		return facilitiesTypeBaseRestCudService.deleteFile(params);
	}
	
	/**
	 * @Description:
	 * @author: guofeilong
	 * @version: 1.0, 2019年2月27日
	 */
	@PostMapping(value = "/uploadVideo")
	public Result<String> uploadVideo(@RequestParam("file") MultipartFile file) {
		return facilitiesTypeBaseRestCudService.uploadVideos(file);
	}
}
