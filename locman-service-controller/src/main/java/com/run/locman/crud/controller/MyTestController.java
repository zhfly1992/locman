/*
* File name: MyTestController.java								
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
* 1.0			guofeilong		2019年7月5日
* ...			...			...
*
***************************************************/

package com.run.locman.crud.controller;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.constants.common.ResultCodeConstants;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.crud.service.DeviceReportedCrudService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.UrlConstant;
import com.run.locman.crud.service.DeviceDataStorageCudRestService;
import com.run.locman.filetool.FastDfsUtil;
import com.run.locman.filetool.FastDfsUtilTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.print.DocFlavor.STRING;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2019年7月5日
*/
@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class MyTestController {
	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);
	
	@Autowired
	private DeviceDataStorageCudRestService deviceDataStorageCudRestService;
	
	@Autowired
	private DeviceReportedCrudService deviceReportedCrudService;
	
	private static ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	
	@RequestMapping(value = "/002/{b}", method = RequestMethod.GET)
	public void test(@PathVariable String b ) {
		String a = "===";
			executorService.execute(new Runnable() {
				
				@Override
				public void run() {
					System.out.println(a + b + "");
					try {
						Thread.sleep(120000);
						long id = Thread.currentThread().getId();
						System.out.println("执行完毕" + id);
					} catch (InterruptedException e) {
						
					}
				}
			});
		
		
	}
	
	
	
	@RequestMapping(value = "/003", method = RequestMethod.POST)
	public void test003(@RequestBody String b ) {
		try {
			/*JSONObject json = new JSONObject();
			json.put("accessSecret", "aecde01f-9ae2-4876-84b7-c08ea25a4788");
			
			
			String[] split = b.split(",");
			for (String string : split) {
				json.put("facilityIds", string);
				String httpValueByPost = InterGatewayUtil.getHttpValueByPost("/locman/facilities/synch", json, "http://140.246.137.19", "token-36533ab362724811a8c442a26ce05574");
				System.out.println(httpValueByPost);
				
				Thread.sleep(500);
			}*/
			
		/*	RpcResponse<String> messag = deviceReportedCrudService.messageReceiveThreadPool(b);
			System.out.println(messag);*/
		} catch (Exception e) {
			System.out.println(e);

		}
		
	}
	
	@RequestMapping(value = "/004", method = RequestMethod.POST)
	public void test004(@RequestParam("file") String file) {
		logger.info(String.format("[uploadBase64File()->request params:%s]", file));
		if (file == null) {
			return;
		}
		try {
			List<String> picUrl = new ArrayList<>();
			List<String> failList = new ArrayList<>();
			JSONArray jsonArray = JSONObject.parseArray(file);

			uploadBase64FileFor(picUrl, failList, jsonArray);

			if (picUrl != null && picUrl.size() > 0 && picUrl.size() == jsonArray.size() && !picUrl.contains(null)) {
				logger.info("[uploadBase64File()->success:图片上传成功]");
				return;
			}
			if (picUrl != null && picUrl.size() > 0 && failList != null && failList.size() > 0) {
				// return ResultBuilder.successResult(picUrl, "部分图片上传成功" +
				// failList);
				logger.error("[uploadBase64File()->fail:部分图片上传失败]");
				return;
			}
			logger.error("[uploadBase64File()->fail:图片上传失败]");
			return;
		} catch (IOException e) {
			logger.error("uploadBase64File()->exception", e);
			return;
		} catch (Exception e) {
			logger.error("uploadBase64File()->exception", e);
			return;
		}
		
	}
	
	private void uploadBase64FileFor(List<String> picUrl, List<String> failList, JSONArray jsonArray)
			throws IOException, MyException {
		for (int i = 0; i < jsonArray.size(); i++) {
			// 得到每个图片
			JSONObject json = JSONObject.parseObject(jsonArray.get(i).toString());
			// 修改工单时执行此判断,防止工单修改后,保存时图片数据丢失
			if (json.containsKey("imgStr") && !StringUtils.isBlank(json.getString("imgStr"))) {
				String imgURL = json.getString("imgStr");
				String end = imgURL.substring(imgURL.lastIndexOf(".") + 1);
				if (imgURL.startsWith("http://")) {
					if (end.equalsIgnoreCase("jpg") || end.equalsIgnoreCase("jpeg") || end.equalsIgnoreCase("png")) {
						picUrl.add(imgURL);
						continue;
					}
				}
			}
			if (!json.containsKey("imgName") || StringUtils.isBlank(json.getString("imgName"))) {
				failList.add(json.getString("imgName") + "上传失败");
				continue;
			}

			String imgName = json.getString("imgName");
			String end = imgName.substring(imgName.lastIndexOf(".") + 1);

			if (!end.equalsIgnoreCase("jpg") && !end.equalsIgnoreCase("jpeg") && !end.equalsIgnoreCase("png")) {
				failList.add(imgName + "上传失败");
				continue;
			}
			if (!json.containsKey("imgStr") || StringUtils.isBlank(json.getString("imgStr"))) {
				failList.add(imgName + "上传失败");
				continue;
			}
			if (!json.containsKey("imgLength") || StringUtils.isBlank(json.getString("imgLength"))
					|| json.getString("imgLength").length() > 10485760) {
				failList.add(imgName + "上传失败");
				continue;
			}

			String imgStr = json.getString("imgStr");
			String imgLength = json.getString("imgLength");

			if (Integer.parseInt(imgLength) != imgStr.length()) {
				failList.add(imgName + "上传失败");
				continue;
			}
			// File file = new File("/" + imgName);
			String resultStr = imgStr.substring(imgStr.indexOf(",") + 1);
			byte[] stringToImage = Base64Utils.decodeFromString(resultStr);
			String facilityTypeIco = FastDfsUtilTest.uploadBase64File(stringToImage, imgName);
			if (facilityTypeIco != null) {
				picUrl.add(facilityTypeIco);
			} else {
				failList.add(imgName + "上传失败" + facilityTypeIco);
			}
		}
	}
	
	
	@RequestMapping(value = "/001", method = RequestMethod.GET)
	public Result<String> deleteUnusedFile() throws IOException {
		
		/*try {
			FastDfsUtilTest.deleteFile(size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//logger.error("()->exception",e);
			
		} catch (MyException e) {
			// TODO Auto-generated catch block
			//logger.error("()->exception",e);
			
		}*/
		/*int parseInt = Integer.parseInt(size);
		String deleteUnusedFile = deviceDataStorageCudRestService.deleteUnusedFile(parseInt);
		*/
		String str = "12111111"
				+ "11111111111111111111111111111111"
				+ "111111111111111111111111111111111111111111111111111"
				+ "1111111111111111111111111111111111111111111111113121233"
				+ "333331111112321111111111111111111111111111111111111"
				+ "111111111111111112222asaaaaaaaaaaaaaaaaaaaaa"
				+ "asdaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "assssssssssssssssssssssssssssss"
				+ "csssssssssssssssssssssssssssssssssssssssssss"
				+ "ascssssssssssssssssssssssssssssssssssssssssssssss"
				+ "acsaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "caaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaccccccccccccccccccccccacaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa11111111111111111111111111111111111111111111111111111";
		
		String r = str + str + str + str + str + str + str + str  + str + str + str;
		String rs = r + r+ r+ r+ r+ r+ r+ r+ r+ r+ r+ r+ r+ r+ r+ r+ r+ r+ r+ r+ r;
		rs += rs;
		rs += rs;
		rs += rs;
		rs += rs;
		rs += rs;
		int a  = rs.getBytes().length;
		//return ResultBuilder.successResult(compress(rs), a +"");
		return ResultBuilder.successResult(rs, a +"");
	}
	
	
    /**
     * 字符串的压缩
     * 
     * @param str
     *            待压缩的字符串
     * @return    返回压缩后的字符串
     * @throws IOException
     */
    public static String compress(String str) throws IOException {
        if (null == str || str.length() <= 0) {
            return str;
        }
        // 创建一个新的 byte 数组输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 使用默认缓冲区大小创建新的输出流
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        // 将 b.length 个字节写入此输出流
        gzip.write(str.getBytes());
        gzip.close();
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return out.toString("ISO-8859-1");
    }
     
    /**
     * 字符串的解压
     * 
     * @param str
     *            对字符串解压
     * @return    返回解压缩后的字符串
     * @throws IOException
     */
    public static String unCompress(String str) throws IOException {
        if (null == str || str.length() <= 0) {
            return str;
        }
        // 创建一个新的 byte 数组输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组
        ByteArrayInputStream in = new ByteArrayInputStream(str
                .getBytes("ISO-8859-1"));
        // 使用默认缓冲区大小创建新的输入流
        GZIPInputStream gzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n = 0;
        while ((n = gzip.read(buffer)) >= 0) {// 将未压缩数据读入字节数组
            // 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte数组输出流
            out.write(buffer, 0, n);
        }
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return out.toString("GBK");
    }
	

}
