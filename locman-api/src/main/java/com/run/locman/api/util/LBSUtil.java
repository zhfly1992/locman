package com.run.locman.api.util;

/**
 * 
 * LBS工具类
 * 
 * @author: liaodan
 * @version: 1.0, 2017年9月20日
 */
public class LBSUtil {
	
	/** 赤道半径（单位米） */
	public static final double	EARTH_RADIUS	= 6378137;
	/** 纬度1度的距离，111公里 */
	public static final double	LATITUDE_ONE	= 111000;



	/**
	 * 获取纬度1度的距离,纬度距离固定
	 * 
	 * @return 纬度1度的距离
	 */
	public static final double getOneLaMeter() {
		return LATITUDE_ONE;
	}



	/**
	 * 获取经度1度的距离，计算方式为1纬度的距离乘以所在纬度的余弦
	 * 
	 * @param latitude
	 *            所在纬度
	 * @return 1经度的距离，单位为米
	 */
	public static final double getOneLoMeter(double latitude) {
		double ala = getOneLaMeter();
		double alo = ala * Math.cos(rad(latitude));
		return alo;
	}



	/**
	 * 获取1米对应的纬度数
	 * 
	 * @return 对应纬度度数
	 */
	public static double getMeterOneLa() {
		double la1 = 1 / LATITUDE_ONE;
		return la1;
	}



	/**
	 * 获取1米对应的经度数，与所在纬度有关
	 * 
	 * @param 所在纬度
	 * @return 所在纬度1米对应经度数
	 */
	public static double getMeterOneLo(double latitude) {
		double lo1 = 1 / getOneLoMeter(latitude);
		return lo1;
	}



	/**
	 * 转换为弧度
	 * 
	 * @param tude
	 *            度数
	 * @return 弧度数
	 */
	private static double rad(double tude) {
		return tude * Math.PI / 180.0;
	}



	/**
	 * 计算两个经纬度之间的距离
	 * 
	 * @param srcLon
	 *            源经度
	 * @param srcLati
	 *            源纬度
	 * @param destLon
	 *            目标经度
	 * @param destLati
	 *            目标纬度
	 * @return
	 */
	public static double calculate(double srcLon, double srcLati, double destLon, double destLati) {
		double radSrcLati = rad(srcLati);
		double radDestLati = rad(destLati);
		double a = radSrcLati - radDestLati;
		double b = rad(srcLon) - rad(destLon);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radSrcLati) * Math.cos(radDestLati) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		return s;
	}



	/**
	 * 根据两个位置的经纬度，来计算两地的距离（单位为KM） 参数为String类型
	 * 
	 * @param lat1Str
	 *            用户经度
	 * @param lng1Str
	 *            用户纬度
	 * @param lat2Str
	 *            商家经度
	 * @param lng2Str
	 *            商家纬度
	 * @return
	 */
	public static String getDistance(String lat1Str, String lng1Str, String lat2Str, String lng2Str) {
		Double lat1 = Double.parseDouble(lat1Str);
		Double lng1 = Double.parseDouble(lng1Str);
		Double lat2 = Double.parseDouble(lat2Str);
		Double lng2 = Double.parseDouble(lng2Str);

		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double difference = radLat1 - radLat2;
		double mdifference = rad(lng1) - rad(lng2);
		double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(difference / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(mdifference / 2), 2)));
		distance = distance * EARTH_RADIUS;
		distance = Math.round(distance * 10000) / 10000;
		String distanceStr = distance + "";
		distanceStr = distanceStr.substring(0, distanceStr.indexOf("."));

		return distanceStr;
	}

}
