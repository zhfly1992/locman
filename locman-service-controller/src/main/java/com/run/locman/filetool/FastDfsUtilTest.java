/*
 * File name: FastDfsUtil.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 田明 2017年08月30日 ... ... ...
 *
 ***************************************************/
package com.run.locman.filetool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerGroup;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.google.common.collect.Lists;

/**
 * @Description: 文件上传工具
 * @author: 田明
 * @version: 1.0, 2017年08月30日
 */
public class FastDfsUtilTest {
	private static final Logger	logger	= LoggerFactory.getLogger(FastDfsUtilTest.class);
	@ApolloConfig
	private static Config		config	= ConfigService.getAppConfig();

	@Autowired
	HttpServletResponse			response;

	private static final String	POINT	= ".";



	/**
	 * 上传文件
	 */
	public static String uploadFile(File file, String uploadFileName, long fileLength) throws IOException, MyException {
		// 连接超时的时限，单位为毫秒
		ClientGlobal.setG_connect_timeout(config.getIntProperty("fastdfs.connectTimeout", 10000));
		// 网络超时的时限，单位为毫秒
		ClientGlobal.setG_network_timeout(config.getIntProperty("fastdfs.networkTimeout", 100000));
		ClientGlobal.setG_anti_steal_token(config.getBooleanProperty("fastdfs.stealToken", false));
		// 字符集
		ClientGlobal.setG_charset(config.getProperty("fastdfs.charset", "UTF-8"));
		// HTTP访问服务的端口号
		ClientGlobal.setG_tracker_http_port(config.getIntProperty("fastdfs.httpPort", 8090));

		// Tracker服务器列表
		InetSocketAddress[] trackerServers = new InetSocketAddress[1];
		trackerServers[0] = new InetSocketAddress(config.getProperty("fastdfs.trackerHttpUrl", "files.locman.cn"),
				config.getIntProperty("fastdfs.trackerHttpPort", 22122));
		ClientGlobal.setG_tracker_group(new TrackerGroup(trackerServers));

		byte[] fileBuff = getFileBuffer(new FileInputStream(file), file.length());
		String[] files = null;
		String fileExtName = "";
		if (uploadFileName.contains(POINT)) {
			fileExtName = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
		} else {
			return null;
		}

		// 建立连接
		TrackerClient tracker = new TrackerClient();
		TrackerServer trackerServer = tracker.getConnection();
		StorageServer storageServer = null;
		StorageClient client = new StorageClient(trackerServer, storageServer);

		// 设置元信息
		NameValuePair[] metaList = new NameValuePair[3];
		metaList[0] = new NameValuePair("fileName", uploadFileName);
		metaList[1] = new NameValuePair("fileExtName", fileExtName);
		metaList[2] = new NameValuePair("fileLength", String.valueOf(file.length()));

		// 上传文件
		try {
			files = client.upload_file(fileBuff, fileExtName, metaList);
			logger.info("Upload file \"" + uploadFileName + "\"success");
		} catch (Exception e) {
			logger.error("Upload file \"" + uploadFileName + "\"fails");
			logger.error("uploadFile()->exception", e);
		}
		trackerServer.close();
		if (null == files || files.length <= 0) {
			return null;
		}
		String filePath = config.getProperty("fastdfs.clientUrlConfig", "http://files.locman.cn:8090/") + files[0] + "/"
				+ files[1];
		return filePath;
	}



	/**
	 * 上传base64压缩文件
	 */
	public static String uploadBase64File(byte[] stringToImage, String uploadFileName) throws IOException, MyException {
		
		String fileExtName = "";
		if (uploadFileName.contains(".")) {
			fileExtName = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
		} else {
			return null;
		}
		
/*		// 连接超时的时限，单位为毫秒
		ClientGlobal.setG_connect_timeout(config.getIntProperty("fastdfs.connectTimeout", 10000));
		// 网络超时的时限，单位为毫秒
		ClientGlobal.setG_network_timeout(config.getIntProperty("fastdfs.networkTimeout", 100000));
		ClientGlobal.setG_anti_steal_token(config.getBooleanProperty("fastdfs.stealToken", false));
		// 字符集
		ClientGlobal.setG_charset(config.getProperty("fastdfs.charset", "UTF-8"));
		// HTTP访问服务的端口号
		ClientGlobal.setG_tracker_http_port(config.getIntProperty("fastdfs.httpPort", 8090));

		// Tracker服务器列表
		InetSocketAddress[] trackerServers = new InetSocketAddress[1];
		trackerServers[0] = new InetSocketAddress(config.getProperty("fastdfs.trackerHttpUrl", "files.locman.cn"),
				config.getIntProperty("fastdfs.trackerHttpPort", 22122));
		ClientGlobal.setG_tracker_group(new TrackerGroup(trackerServers));
*/
		// 连接超时的时限，单位为毫秒
		ClientGlobal.setG_connect_timeout(10000);
		// 网络超时的时限，单位为毫秒
		ClientGlobal.setG_network_timeout(100000);
		ClientGlobal.setG_anti_steal_token(false);
		// 字符集
		ClientGlobal.setG_charset("UTF-8");
		// HTTP访问服务的端口号
		ClientGlobal.setG_tracker_http_port(8090);

		// Tracker服务器列表
		InetSocketAddress[] trackerServers = new InetSocketAddress[1];
		trackerServers[0] = new InetSocketAddress("140.246.139.40",
				22128);
		ClientGlobal.setG_tracker_group(new TrackerGroup(trackerServers));

		String[] files = null;


		// 建立连接
		TrackerClient tracker = new TrackerClient();
		TrackerServer trackerServer = tracker.getConnection();
		StorageServer storageServer = null;
		StorageClient client = new StorageClient(trackerServer, storageServer);

		// 设置元信息
		NameValuePair[] metaList = new NameValuePair[3];
		metaList[0] = new NameValuePair("fileName", uploadFileName);
		metaList[1] = new NameValuePair("fileExtName", fileExtName);
		metaList[2] = new NameValuePair("fileLength", String.valueOf(stringToImage.length));

		// 上传文件
		try {
			files = client.upload_file(stringToImage, fileExtName, metaList);
			logger.info("Upload file \"" + uploadFileName + "\"success");
		} catch (Exception e) {
			logger.error("Upload file \"" + uploadFileName + "\"fails");
		}
		trackerServer.close();
		if (files == null) {
			return null;
		}
		String filePath = config.getProperty("fastdfs.clientUrlConfig", "http://files.locman.cn:8090/") + files[0] + "/"
				+ files[1];
		return filePath;
	}



	/**
	 * 从图片服务器上删除文件
	 */
	public static int deleteFile(String filePath) throws IOException, MyException {
		// 连接超时的时限，单位为毫秒
		ClientGlobal.setG_connect_timeout(config.getIntProperty("fastdfs.connectTimeout", 10000));
		// 网络超时的时限，单位为毫秒
		ClientGlobal.setG_network_timeout(config.getIntProperty("fastdfs.networkTimeout", 100000));
		ClientGlobal.setG_anti_steal_token(config.getBooleanProperty("fastdfs.stealToken", false));
		// 字符集
		ClientGlobal.setG_charset(config.getProperty("fastdfs.charset", "UTF-8"));
		// HTTP访问服务的端口号
		ClientGlobal.setG_tracker_http_port(config.getIntProperty("fastdfs.httpPort", 8090));

		// Tracker服务器列表
		InetSocketAddress[] trackerServers = new InetSocketAddress[1];
		trackerServers[0] = new InetSocketAddress(config.getProperty("fastdfs.trackerHttpUrl", "files.locman.cn"),
				config.getIntProperty("fastdfs.trackerHttpPort", 22122));
		ClientGlobal.setG_tracker_group(new TrackerGroup(trackerServers));

		// 建立连接
		TrackerClient tracker = new TrackerClient();
		TrackerServer trackerServer = tracker.getConnection();
		StorageServer storageServer = null;
		StorageClient client = new StorageClient(trackerServer, storageServer);

		// 删除文件,返回值-1失败,2未查找到图片(失败),0成功
		int result = -1;
		try {
			String[] path = filePath.replaceFirst("http://", "").split("/", 3);
			String groupName = path[1];
			String remoteFileName = path[2];

			result = client.delete_file(groupName, remoteFileName);
			logger.info("Delete file \"" + groupName + remoteFileName + "\"success");
		} catch (Exception e) {
			logger.error("Delete file \"" + filePath + "\"fails");
		}
		trackerServer.close();
		return result;
	}

	
	/**
	 * 从图片服务器上删除文件
	 */
	public static List<String> deleteFiles(List<String> filePaths) throws IOException{
		/*// 连接超时的时限，单位为毫秒
		ClientGlobal.setG_connect_timeout(config.getIntProperty("fastdfs.connectTimeout", 10000));
		// 网络超时的时限，单位为毫秒
		ClientGlobal.setG_network_timeout(config.getIntProperty("fastdfs.networkTimeout", 100000));
		ClientGlobal.setG_anti_steal_token(config.getBooleanProperty("fastdfs.stealToken", false));
		// 字符集
		ClientGlobal.setG_charset(config.getProperty("fastdfs.charset", "UTF-8"));
		// HTTP访问服务的端口号
		ClientGlobal.setG_tracker_http_port(config.getIntProperty("fastdfs.httpPort", 8090));

		// Tracker服务器列表
		InetSocketAddress[] trackerServers = new InetSocketAddress[1];
		trackerServers[0] = new InetSocketAddress(config.getProperty("fastdfs.trackerHttpUrl", "files.locman.cn"),
				config.getIntProperty("fastdfs.trackerHttpPort", 22122));
		ClientGlobal.setG_tracker_group(new TrackerGroup(trackerServers));*/

		
		// 连接超时的时限，单位为毫秒
		ClientGlobal.setG_connect_timeout(10000);
		// 网络超时的时限，单位为毫秒
		ClientGlobal.setG_network_timeout(100000);
		ClientGlobal.setG_anti_steal_token( false);
		// 字符集
		ClientGlobal.setG_charset( "UTF-8");
		// HTTP访问服务的端口号
		ClientGlobal.setG_tracker_http_port(8090);

		// Tracker服务器列表
		InetSocketAddress[] trackerServers = new InetSocketAddress[1];
		trackerServers[0] = new InetSocketAddress("files.locman.cn",22122);
		ClientGlobal.setG_tracker_group(new TrackerGroup(trackerServers));

		// 建立连接
		TrackerClient tracker = new TrackerClient();
		TrackerServer trackerServer = tracker.getConnection();
		StorageServer storageServer = null;
		StorageClient client = new StorageClient(trackerServer, storageServer);

		List<String> resultList = Lists.newArrayList();
		// 删除文件,返回值-1失败,2未查找到图片(失败),0成功
		try {
			for (String filePath : filePaths) {
				int result = -1;
				/*String[] path = filePath.split("/", 3);
				String remoteFileName = path[1];
*/
				
				result = client.delete_file("groupA", filePath);
				if (0 == result) {
					logger.info("Delete file \"" + filePath + "\"success");
					resultList.add(filePath);
				} else {
					logger.info("Delete file \"" + filePath + "\"fail");
				}
			}
			
			
		} catch (Exception e) {
			logger.error("" + e);
		}
		trackerServer.close();
		return resultList;
	}
	
	


	private static byte[] getFileBuffer(FileInputStream inStream, long fileLength) throws IOException {

		byte[] buffer = new byte[256 * 1024];
		byte[] fileBuffer = new byte[(int) fileLength];

		int count = 0;
		int length = 0;

		while ((length = inStream.read(buffer)) != -1) {
			for (int i = 0; i < length; ++i) {
				fileBuffer[count + i] = buffer[i];
			}
			count += length;
		}
		return fileBuffer;
	}



	public static String downloadAndUpdate(String filePath) throws IOException {
		if (null == filePath || "".equals(filePath)) {
			logger.error("url不能为空");
			return null;
		}

		URL url = new URL(filePath);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 60 * 1000);
		InputStream fileInputStream = conn.getInputStream();
		byte[] buffer = readInputStream(fileInputStream);

		File file = new File(filePath);
		String name = file.getName();

		try {
			String uploadBase64File = uploadBase64File(buffer, name);
			logger.info("Upload file \"" + filePath + "\"success: " + uploadBase64File);
			return uploadBase64File;
		} catch (Exception e) {
			logger.error("Upload file \"" + filePath + "\"fail");
			return null;
		}

	}



	/**
	 * 从输入流中获取字节数组
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}
	  

}