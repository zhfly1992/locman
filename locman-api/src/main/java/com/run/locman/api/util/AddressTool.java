/*
 * File name: AddressTool.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 guofeilong 2018年11月26日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.run.http.client.util.HttpClientUtil;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: guofeilong
 * @version: 1.0, 2018年11月26日
 */

public class AddressTool {

	private static final Logger logger = Logger.getLogger(CommonConstants.LOGKEY);



	/**
	 * 获取位置
	 * 
	 * @param log
	 *            大: 经度
	 * @param lat
	 *            小：纬度
	 * @return
	 */
	public static String getAddress(String log, String lat) {
		/**
		 * 百度根据经纬度获取地区名接口：
		 * http://api.map.baidu.com/geocoder/v2/?ak=6LcbSspFbz8KHmr42ZVbl65W53IBRyon&location="
		 * + log + "," + lat + "&output=json&pois=0" pois
		 * 是否召回传入坐标周边的poi，0为不召回，1为召回。 ak 用户申请注册的key
		 * 
		 * 详细参数查看http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding
		 * 
		 */
		/** type(100代表道路，010代表POI，001代表门址，111前面都是) */
		String urlString = "http://api.map.baidu.com/geocoder/v2/?ak=6LcbSspFbz8KHmr42ZVbl65W53IBRyon&location=" + log
				+ "," + lat + "&output=json&pois=0";
		String addressStr = HttpClientUtil.getInstance().doGet(urlString, "");
		logger.info(addressStr);
		return addressStr;
	}



	/**
	 * 获取区域id
	 * 
	 * @param log
	 *            大: 经度
	 * @param lat
	 *            小：纬度
	 * @return
	 */
	public static String getAreaCode(String log, String lat) {
		String address = getAddress(lat, log);
		if (StringUtils.isBlank(address)) {
			logger.error("地图api返回值为空!!!");
			return "";
		}
		logger.info("地图api返回值:" + address);
		JSONObject addressJson = JSONObject.parseObject(address);
		String status = addressJson.getString("status");
		// 正确返回结果
		String trueStatus = "0";
		if (!trueStatus.equals(status)) {
			logger.error("查询地图api返回非正确结果!!!");
			return "";
		}
		JSONObject result = addressJson.getJSONObject("result");
		JSONObject addressComponent = result.getJSONObject("addressComponent");
		String admCode = addressComponent.getString("adcode");
		if (StringUtils.isNotBlank(admCode)) {
			String province = admCode.substring(0, 2) + "0000";
			String city = admCode.substring(0, 4) + "00";
			return new StringBuffer().append(province).append(",").append(city).append(",").append(admCode).toString();
		} else {
			return "";
		}
	}



	/**
	 * 
	 * @Description: 获取省 市 县
	 * @param log
	 *            大: 经度
	 * @param lat
	 *            小：纬度
	 * @return
	 */
	public static String getPCD(String log, String lat) {
		String address = getAddress(lat, log);
		if (StringUtils.isBlank(address)) {
			logger.error("百度地图api返回值为空!!!");
			return "";
		}
		JSONObject addressJson = JSONObject.parseObject(address);
		JSONObject result = addressJson.getJSONObject("result");
		JSONObject addressComponent = result.getJSONObject("addressComponent");
		StringBuffer resultPCD = new StringBuffer();
		String province = addressComponent.getString("province");
		String city = addressComponent.getString("city");
		String district = addressComponent.getString("district");
		resultPCD.append(province).append(city).append(district);
		return resultPCD.toString();

	}

	/*	*//**
			 * 获取位置
			 * 
			 * @param log
			 *            大: 纬度
			 * @param lat
			 *            小：经度
			 * @return
			 */
	/*
	 * public static String getAddress(String log, String lat) {
	 *//**
		 * 阿里云根据经纬度获取地区名接口：
		 * http://gc.ditu.aliyun.com/regeocoding?l=39.938133,116.395739&type=001
		 * 阿里云根据地区名获取经纬度接口： http://gc.ditu.aliyun.com/geocoding?a=苏州市
		 */

	/*
	*//** type(100代表道路，010代表POI，001代表门址，111前面都是) *//*
													 * String addressStr = "";
													 * String urlString =
													 * "http://gc.ditu.aliyun.com/regeocoding?l="
													 * + log + "," + lat +
													 * "&type=010"; try { URL
													 * url = new URL(urlString);
													 * HttpURLConnection conn =
													 * (HttpURLConnection)
													 * url.openConnection();
													 * conn.setDoInput(true);
													 * conn.setRequestMethod(
													 * "POST"); BufferedReader
													 * in = new
													 * BufferedReader(new
													 * InputStreamReader(conn.
													 * getInputStream(),
													 * "utf-8")); String line;
													 * while ((line =
													 * in.readLine()) != null) {
													 * addressStr += line +
													 * "\n"; } in.close();
													 * 
													 * } catch (Exception e) {
													 * logger.error(e); }
													 * logger.info(addressStr);
													 * return addressStr; }
													 */

	private static final double EARTH_RADIUS = 6378.137;



	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}



	public static double distance(String nowLng, String nowLat, String latitude, String longitude) {
		double long1;
		double lat1;
		double long2;
		double lat2;
		double a, b, sa2, sb2, d = 0;
		try {
			long1 = Double.valueOf(nowLng);
			lat1 = Double.valueOf(nowLat);
			long2 = Double.valueOf(latitude);
			lat2 = Double.valueOf(longitude);
			lat1 = rad(lat1);
			lat2 = rad(lat2);
			a = lat1 - lat2;
			b = rad(long1 - long2);
			sa2 = Math.sin(a / 2.0);
			sb2 = Math.sin(b / 2.0);
			d = 2 * EARTH_RADIUS * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));

		} catch (Exception e) {

			e.printStackTrace();
		}
		return d;
	}
}
