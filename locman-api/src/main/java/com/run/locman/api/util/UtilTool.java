package com.run.locman.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.run.common.util.StringUtil;
import com.run.encryt.util.MD5;
import com.run.entity.tool.DateUtils;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.SwitchLockRecordConstants;

/**
 * 
 * @Description:基础工具类
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public class UtilTool {

	/**
	 * 时间长度
	 */
	private static final int	DATE_LENGTH	= 10;
	private static final String	REGEX		= "^-?\\d+(\\.\\d+)?$";
	public final static String	DATETIME_FORMAT			= "yyyy-MM-dd HH:mm:ss";
	public final static String  DATE_FORMAT             ="yyyy-MM-dd";


	/**
	 * 
	 * @Description:获取list不重复的单个key指定的值转化list
	 * @param list
	 * @param key
	 * @return
	 */
	public static List<Object> getListNoRepeat(List<Map<String, Object>> list, String key) {
		Set<Object> set = new HashSet<>();
		for (Map<String, Object> map : list) {
			set.add(map.get(key));
		}
		List<Object> listValue = new ArrayList<Object>();
		listValue.addAll(set);
		return listValue;
	}



	/**
	 * 
	 * @Description:从指定的list的Map中获取key所标识的obj，没有则返回key
	 * @param list
	 * @param key
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getStringByKey(List<Map> list, String key, String ordId) {
		for (Map<String, Object> map : list) {
			if (map.containsKey(key) && ((String) map.get(key)).equals(ordId)) {
				return map;
			}
		}
		return new JSONObject();
	}



	/**
	 * 
	 * @Description:获取UUID
	 * @return String
	 */
	public static String getUuId() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}



	public static String getCorn(int hour, int minute) {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, hour);
		calendar.add(Calendar.MINUTE, minute);

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int date = calendar.get(Calendar.DAY_OF_MONTH);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);

		String corn = second + " " + minutes + " " + hours + " " + date + " " + month + " ? " + year;
		return corn;

	}



	/**
	 * 
	 * @Description:新IOT设备id获取
	 * @param reportedMessage
	 * @return
	 */
	public static String getIotNewDeviceId(JSONObject reportedMessage) {
		if (null == reportedMessage || reportedMessage.isEmpty()) {
			return null;
		}
		String deviceId = null;
		JSONObject things = reportedMessage.getJSONObject("things");
		if (null != things) {
			deviceId = things.getString("subThingId");
		}
		return deviceId;
	}



	/**
	 * 
	 * @Description:新IOT网关id获取
	 * @param reportedMessage
	 * @return
	 */
	public static String getIotNewGatewayId(JSONObject reportedMessage) {
		if (null == reportedMessage || reportedMessage.isEmpty()) {
			return null;
		}
		String gatewayId = null;
		JSONObject things = reportedMessage.getJSONObject("things");
		if (null != things) {
			gatewayId = things.getString("gatewayId");
		}
		return gatewayId;
	}



	/**
	 * 
	 * @Description:新IOT设备上报数据点获取
	 * @param reportedMessage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getIotNewRepoted(JSONObject reportedMessage) {
		if (null == reportedMessage || reportedMessage.isEmpty()) {
			return null;
		}
		JSONObject reported = new JSONObject();
		JSONObject things = reportedMessage.getJSONObject("things");
		if (null != things) {
			JSONArray attributeInfo = reportedMessage.getJSONArray("attributeInfo");
			if (null != attributeInfo && !attributeInfo.isEmpty()) {
				List<Map<String, String>> attribute = JSONObject.parseObject(attributeInfo.toJSONString(), List.class);
				if (null != attribute && attribute.size() > 0) {
					for (Map<String, String> map : attribute) {
						if (null != map.get("attributeReportedValue")) {
							reported.put(map.get("attributeName"), map.get("attributeReportedValue"));
						}
					}
				}
			}
		}
		Object xx = reported.get("xavtv");
		Object yy = reported.get("yavtv");
		Object zz = reported.get("zavtv");
		
		if(StringUtil.isNotEmpty(xx)&&StringUtil.isNotEmpty(yy)&&StringUtil.isNotEmpty(zz)) {
			int x=Integer.parseInt(xx.toString());
			int y=Integer.parseInt(yy.toString());
			int z=Integer.parseInt(zz.toString());
			double ractv=Math.sqrt(x*x+y*y+z*z);
			reported.put("ractv", ractv);
		}
		return reported;
	}



	/**
	 * 
	 * @Description:原版IOT设备上报数据点获取
	 * @param reportedMessage
	 * @return JSONObject
	 */
	/*
	 * public static JSONObject getIotOldRepoted(JSONObject reportedMessage) {
	 * if (null == reportedMessage || reportedMessage.isEmpty()) { return null;
	 * } JSONObject reported = null; JSONObject state =
	 * reportedMessage.getJSONObject("state"); if (null != state) { reported =
	 * state.getJSONObject("reported"); } return reported; }
	 */

	/**
	 * 
	 * @Description:获取设备属性状态
	 * @param reportedMessage
	 * @return JSONObject
	 */
	public static JSONObject getReported(JSONObject reportedMessage) {
		if (null == reportedMessage || reportedMessage.isEmpty()) {
			return null;
		}
		// JSONObject reported = null;
		// String gatewayId = getIotNewGatewayId(reportedMessage);
		// 如果网关id为NULL值，则执行原版IOT解析设备状态数据，否则执行新版IOT解析设备状态数据。
		/*
		 * if (StringUtils.isBlank(gatewayId)) { reported =
		 * getIotOldRepoted(reportedMessage); } else {
		 */
		JSONObject reported = getIotNewRepoted(reportedMessage);
		// }
		return reported;
	}



	/**
	 * 
	 * @Description:新版IOT和原版IOT设备上报时间timestamp转换date
	 * @param reportedMessage
	 * @return String
	 */
	public static String timeStampToDate(Long timestamp) {
		if (null == timestamp || 0L == timestamp) {
			return "";
		}
		String date = "";
		if (timestamp.toString().length() > DATE_LENGTH) {
			date = DateUtils.formatDate(new Date(timestamp));
		} else {
			date = DateUtils.formatDate(new Date(timestamp * 1000L));
		}
		return date;
	}



	/**
	 * 
	 * @Description:Object对象转String
	 * @param obj
	 * @return
	 */
	public static String objectToString(Object obj) {
		if (null == obj) {
			return "";
		}
		return obj.toString();

	}



	/**
	 * 
	 * @Description:从请求头获取用户id(需要已经在网关中查询并传参)
	 * @param
	 * @return
	 */

	public static String getUserId(HttpServletRequest request) {

		String userId = request.getHeader("userId");

		if (userId != null) {
			return userId;
		} else {
			return "";
		}
	}



	/**
	 * 
	 * @Description:校验字符串是否为一切数字，如-123 -12.12 123
	 * @param string
	 * @return
	 */
	public static boolean isNumber(String string) {
		if (string == null) {
			return false;
		}

		Pattern pattern = Pattern.compile(REGEX);
		return pattern.matcher(string).matches();
	}



	/**
	 * 
	 * @Description: 生成固定的设备id
	 * @param gatewayId
	 *            网关id
	 * @param subThingId
	 *            子设备id
	 * @param appKey
	 *            应用key
	 * @param accessSecret
	 *            接入方密钥
	 * @return 设备id
	 */
	public static String getDeviceId(String gatewayId, String subThingId, String appKey, String accessSecret) {

		StringBuffer deviceIdBuffer = new StringBuffer();
		String idStr = deviceIdBuffer.append(gatewayId).append(subThingId).append(appKey).append(accessSecret)
				.toString();
		String id = MD5.encrytMD5(idStr);
		return id;
	}



	/**
	 * 
	 * @Description: 生成固定的设备类型id
	 * @param deviceTypeId
	 *            产品id
	 * @param accessSecret
	 *            接入方密钥
	 * @return 设备类型id
	 */
	public static String getDeviceTypeId(String deviceTypeId, String accessSecret) {

		StringBuffer deviceTypeIdBuffer = new StringBuffer();
		String idStr = deviceTypeIdBuffer.append(deviceTypeId).append(accessSecret).toString();
		String id = MD5.encrytMD5(idStr);
		return id;
	}
	
    /**
     * 按指定大小，分隔集合，将集合按规定个数分为n个部分
     * @param <T>
     * 
     * @param list
     * @param len
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int len) {
        
        if (list == null || list.isEmpty() || len < 1) {
            return Collections.emptyList();
        }

        List<List<T>> result = new ArrayList<>();

        int size = list.size();
        int count = (size + len - 1) / len;

        for (int i = 0; i < count; i++) {
            List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        
        return result;
    }
    
    public static int daysBetween(String smdate,String bdate) throws ParseException{  
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(sdf.parse(smdate));    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(sdf.parse(bdate));    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));     
    }
    
    
    public static String addDays(String timeStr, int days){
    	 try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			Calendar cd = Calendar.getInstance();   
            cd.setTime(sdf.parse(timeStr));   
            cd.add(Calendar.DATE, days);//增加一天   
            //cd.add(Calendar.MONTH, n);//增加一个月   
  
            return sdf.format(cd.getTime());   
		} catch (Exception e) {
			return null;
		}

    }
    
    /**
      * 
      * @Description: 检验开始时间结束时间与当前时间,开始时间大于当前时间/结束时间大于当前时间/开始时间大于结束时间时会提示
      * 
      * @param  startTimeStr 开始时间字符串  endTimeStr 结束时间字符串
      * @return
     * @throws ParseException 
      */
    public static String checkStartAndEndTime(String startTimeStr, String endTimeStr) throws ParseException{
   	 
   		SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
		Date startDate = sdf.parse(startTimeStr);
		long startTime = startDate.getTime();
		Date endDate = sdf.parse(endTimeStr);
		long endTime = endDate.getTime();
		Date nowDate = new Date();
		long nowTime = nowDate.getTime();
		if (startTime - endTime > 0) {
			return "开始时间不能大于结束时间";
		}
		if (startTime - nowTime > 0) {
			return "开始时间不能大于当前时间";
		}
		if (endTime - nowTime > 0) {
			return "结束时间不能大于当前时间";
		}
		return null;
		
	

   }
    
    /**
     * 
     * @Description: 检验开始时间结束时间与当前时间,开始时间大于当前时间/结束时间大于当前时间/开始时间大于结束时间时会提示
     * 
     * @param  startTimeStr 开始时间字符串  endTimeStr 结束时间字符串
     * @return
    * @throws ParseException 
     */
   public static String checkStartAndEndTimeByDay(String startTimeStr, String endTimeStr) throws ParseException{
  	 
  		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Date startDate = sdf.parse(startTimeStr);
		long startTime = startDate.getTime();
		Date endDate = sdf.parse(endTimeStr);
		long endTime = endDate.getTime();
		Date nowDate = new Date();
		long nowTime = nowDate.getTime();
		if (startTime - endTime > 0) {
			return "开始时间不能大于结束时间";
		}
		if (startTime - nowTime > 0) {
			return "开始时间不能大于当前时间";
		}
		if (endTime - nowTime > 0) {
			return "结束时间不能大于当前时间";
		}
		return null;
		
	

  }
   
   /**
    * 
   * @Description:
   * @param startTimeStr
   * @param endTimeStr
   * @return
 * @throws ParseException 
    */
   public static String  checkFocusSecurityStartTimeAndEndTime(String startTimeStr, String endTimeStr) throws ParseException {
	   	SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
		Date startDate = sdf.parse(startTimeStr);
		long startTime = startDate.getTime();
		Date endDate = sdf.parse(endTimeStr);
		long endTime = endDate.getTime();
		Date nowDate = new Date();
		long nowTime = nowDate.getTime();
		if(startTime - endTime >0) {
			return "结束时间必须大于开始时间";
		}
		if (nowTime - endTime> 0) {
			return "结束时间必须大于当前时间";
		}
		if (nowTime - startTime > 0) {
			return "开始时间必须大于当前时间";
		}
		return null;
   }
   
   
   /**
  * 
  * @Description:判断是否是数字,包括小数
  * @param 
  * @return
  */
public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]*");
	    if(str.indexOf(".")>0){//判断是否有小数点
	        if(str.indexOf(".")==str.lastIndexOf(".") && str.split("\\.").length==2){ //判断是否只有一个小数点
	            return pattern.matcher(str.replace(".","")).matches();
	        }else {
	            return false;
	        }
	    }else {
	        return pattern.matcher(str).matches();
	    }
	}
    

	/**
	 * 
	 * @Description:通过组织id查询子组织id
	 * @param orgId
	 * @return
	 * @throws Exception
	 */
	public static List<String> findOrgChildId(String orgId,String token,Logger logger, String ip) throws Exception {
	
		JSONObject orgJson = new JSONObject();
		orgJson.put(SwitchLockRecordConstants.COMMAND_ORG_ID_MONGO, orgId);
		String restValue = InterGatewayUtil.getHttpValueByPost(InterGatewayConstants.FIND_ORG_ID, orgJson, ip,
				token);
	
		if (StringUtils.isBlank(restValue)) {
			logger.error(String.format("[findOrgChildId()->error:请检查http请求,组织id = %s]", orgId));
			return new ArrayList<>();
		}
	
		logger.info("[findOrgChildId()->suc:查询http请求成功！]");
		JSONArray jsonArray = JSON.parseArray(restValue);
		List<String> organizationInfoList = jsonArray.toJavaList(String.class);
		return organizationInfoList;
	}
    
}
