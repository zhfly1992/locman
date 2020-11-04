package com.locman.app.fileTool;

/*
 * File name: FastDFSUtil.java
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
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.run.locman.constants.CommonConstants;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @Description: 文件上传工具
 * @author: 田明
 * @version: 1.0, 2017年08月30日
 */
public class FastDFSUtil {
	private static final Logger	logger	= LoggerFactory.getLogger(CommonConstants.LOGKEY);
	@ApolloConfig
	private static Config		config	= ConfigService.getAppConfig();



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
		InetSocketAddress[] tracker_servers = new InetSocketAddress[1];
		tracker_servers[0] = new InetSocketAddress(config.getProperty("fastdfs.trackerHttpUrl", "files.locman.cn"),
				config.getIntProperty("fastdfs.trackerHttpPort", 22122));
		ClientGlobal.setG_tracker_group(new TrackerGroup(tracker_servers));

		byte[] fileBuff = getFileBuffer(new FileInputStream(file), file.length());
		String[] files = null;
		String fileExtName = "";
		if (uploadFileName.contains(".")) {
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
		}
		trackerServer.close();
		String filePath = config.getProperty("fastdfs.clientUrlConfig", "http://files.locman.cn:8090/") + files[0] + "/"
				+ files[1];
		return filePath;
	}



	/**
	 * 上传base64压缩文件
	 */
	public static String uploadBase64File(byte[] stringToImage, String uploadFileName) throws IOException, MyException {
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
		InetSocketAddress[] tracker_servers = new InetSocketAddress[1];
		tracker_servers[0] = new InetSocketAddress(config.getProperty("fastdfs.trackerHttpUrl", "files.locman.cn"),
				config.getIntProperty("fastdfs.trackerHttpPort", 22122));
		ClientGlobal.setG_tracker_group(new TrackerGroup(tracker_servers));

		// byte[] fileBuff = getFileBuffer(new FileInputStream(file),
		// file.length());
		String[] files = null;
		String fileExtName = "";
		if (uploadFileName.contains(".")) {
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
		InetSocketAddress[] tracker_servers = new InetSocketAddress[1];
		tracker_servers[0] = new InetSocketAddress(config.getProperty("fastdfs.trackerHttpUrl", "files.locman.cn"),
				config.getIntProperty("fastdfs.trackerHttpPort", 22122));
		ClientGlobal.setG_tracker_group(new TrackerGroup(tracker_servers));

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
			logger.info("Upload file \"" + groupName + remoteFileName + "\"success");
		} catch (Exception e) {
			logger.error("Upload file \"" + filePath + "\"fails");
		}
		trackerServer.close();
		return result;
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

}