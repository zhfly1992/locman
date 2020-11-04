package com.run.locman.query.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.run.Main;
import com.run.entity.common.Result;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.util.MD5Util;
import com.run.locman.api.util.VerifyImageUtil;
import com.run.locman.constants.CommonConstants;


@Service
public class VerificationCodeRestService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	// 保存横轴位置用于对比，并设置最大数量为10000，多了就先进先出，并设置超时时间为70秒
	public static Cache<String, Integer>	cacheg	= CacheBuilder.newBuilder().expireAfterWrite(300, TimeUnit.SECONDS)
			.maximumSize(10000).build();



	public Result<Map<String, Object>> getPic() {
		try {
			/*
			 * 此处为编辑器测试代码 // 读取图库目录 Resource resource = new
			 * ClassPathResource("sliderimage/targets/"); InputStream
			 * inputStream = resource.getInputStream(); File imgCatalog =
			 * resource.getFile(); File[] files = imgCatalog.listFiles(); //
			 * 随机选择需要切的图 int randNum = new Random().nextInt(files.length); File
			 * targetFile = files[randNum];
			 * 
			 * // 随机选择剪切模版 Random r = new Random(); int num = r.nextInt(6) + 1;
			 * File tempImgFile = new File(
			 * ResourceUtils.getURL("classpath:").getPath() +
			 * "sliderimage\\templates\\" + num + "-w.png");
			 */
			// 随机选择需要切的图
			logger.info("[getPic()->access method]");
			int randNumTarget = new Random().nextInt(23) + 1;
			File targetFile = getTargetsPic(randNumTarget);
			int randNumTemplate = new Random().nextInt(6) + 1;
			File tempImgFile = getTemplatesPic(randNumTemplate);

			// 根据模板裁剪图片
			try {
				Map<String, Object> resultMap = VerifyImageUtil.pictureTemplatesCut(tempImgFile, targetFile);
				logger.info("[getPic()->验证图片处理成功]");
				// 生成流水号，这里就使用时间戳代替
				String lno = Calendar.getInstance().getTimeInMillis() + "";
				cacheg.put(lno, Integer.valueOf(resultMap.get("xWidth") + ""));
				resultMap.put("capcode", lno);
				// 移除横坐标送前端
				resultMap.remove("xWidth");
				logger.info("[getPic()->验证图片获取成功]");
				return ResultBuilder.successResult(resultMap, "获取图片成功");
			} catch (Exception e) {
				return ResultBuilder.exceptionResult(e);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, Object>> checkcapcode(JSONObject json) {
		try {
			if (json.isEmpty() || json == null) {
				logger.error("[checkcapcode()->传入参数为空]");
				return ResultBuilder.emptyResult();
			}
			if (!json.containsKey("capcode")) {
				logger.error("[checkcapcode()->传入参数无capcode]");
				return ResultBuilder.noBusinessResult();
			}
			String capcode = json.getString("capcode");
			if (StringUtils.isEmpty(capcode)) {
				logger.error("[checkcapcode()->capcode为空]");
				return ResultBuilder.failResult("参数capcode不能为空");
			}
			if (!json.containsKey("xpos")) {
				logger.error("[checkcapcode()->传入参数无xpos]");
				return ResultBuilder.noBusinessResult();
			}
			int xpos = json.getIntValue("xpos");
			Map<String, Object> result = new HashMap<String, Object>();
			Integer x = cacheg.getIfPresent(capcode);
			String mdCode = MD5Util.MD5(String.valueOf(System.currentTimeMillis()));
			if (x == null) {
				// 超期
				result.put("code", 3);
				result.put("msg", "验证超时");
				mdCode = MD5Util.MD5("3");
			} else if (xpos - x > 5 || xpos - x < -5) {
				// 验证失败
				result.put("code", 2);
				result.put("msg", "验证失败");
				mdCode = MD5Util.MD5("2");
			} else {
				// 验证成功
				result.put("code", 1);
				result.put("msg", "验证成功");
				mdCode = MD5Util.MD5("1");
				// .....做自己的操作，发送验证码
			}
			result.put("mdCode", mdCode);
			return ResultBuilder.successResult(result, "操作成功");
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ResultBuilder.exceptionResult(e);
		}
	}



	private static File getTargetsPic(int num) throws Exception {

		URL url = Main.class.getClassLoader().getResource("sliderimage/targets/");
		String jarPath = url.toString().substring(0, url.toString().indexOf("!/") + 2);
		System.out.println(jarPath);
		URL jarURL = new URL(jarPath);
		JarURLConnection jarCon = (JarURLConnection) jarURL.openConnection();
		JarFile jarFile = jarCon.getJarFile();
		logger.info("[getTargetsPic()->从jarfile获取]");
		Enumeration<JarEntry> jarEntrys = jarFile.entries();
		File file = null;
		while (jarEntrys.hasMoreElements()) {
			JarEntry entry = jarEntrys.nextElement();
			String name = entry.getName();
			if (name.startsWith("BOOT-INF/classes/sliderimage/targets/") && !entry.isDirectory()) {
				// 分割后获取图片名，选取名字等于随机数的图片
				String[] split = name.split("/");
				String filename = split[split.length - 1];
				String[] split2 = filename.split("\\.");

				if (Integer.valueOf(split2[0]) == num) {
					InputStream resourceAsStream = Main.class.getClassLoader().getResourceAsStream(name);
			//		System.out.println("获取到输入流");
					file = File.createTempFile("tempfile", ".tmp");
			//		System.out.println("临时文件创建成功");
					OutputStream out = new FileOutputStream(file);
					int read;
					byte[] bytes = new byte[1024];
					while ((read = resourceAsStream.read(bytes)) != -1) {
						out.write(bytes, 0, read);
					}
					logger.info("[getTargetsPic()->获取目标图片成功]");
					resourceAsStream.close();
					out.close();
				}
			}
		}
		return file;
	}



	private static File getTemplatesPic(int num) throws Exception {
		URL url = Main.class.getClassLoader().getResource("sliderimage/templates/");
		String jarPath = url.toString().substring(0, url.toString().indexOf("!/") + 2);
		URL jarURL = new URL(jarPath);
		JarURLConnection jarCon = (JarURLConnection) jarURL.openConnection();
		JarFile jarFile = jarCon.getJarFile();
		logger.info("[getTemplatesPic()->从jarfile获取]");
		Enumeration<JarEntry> jarEntrys = jarFile.entries();
		File file = null;
		char cNumber = (char) (num + '0');
		while (jarEntrys.hasMoreElements()) {
			JarEntry entry = jarEntrys.nextElement();
			String name = entry.getName();
			if (name.startsWith("BOOT-INF/classes/sliderimage/templates/") && !entry.isDirectory()) {
				// templates中只有6个模块，只需判断随机数是否包含在文件名中
				if (name.indexOf(String.valueOf(cNumber)) != -1) {
					InputStream resourceAsStream = Main.class.getClassLoader().getResourceAsStream(name);
					file = File.createTempFile("tempfile", ".tmp");
					OutputStream out = new FileOutputStream(file);
					int read;
					byte[] bytes = new byte[1024];
					while ((read = resourceAsStream.read(bytes)) != -1) {
						out.write(bytes, 0, read);
					}
					logger.info("[getTargetsPic()->获取模板图片成功]");
					resourceAsStream.close();
					out.close();
				}
			}
		}
		return file;
	}

}
