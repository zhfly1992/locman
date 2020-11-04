/*
* File name: WingsIotDataTransform.java								
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
* 1.0			Administrator		2020年4月7日
* ...			...			...
*
***************************************************/

package com.run.locman.api.wingiot.service;

import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
* @Description:	
* @author: Administrator
* @version: 1.0, 2020年4月7日
*/
/*
 * 电信iot上报数据格式
 * 参考https://help.ctwing.cn/ding-yue-tui-song/tui-song-xiao-xi-ge-shi.html
 * {
	"IMEI": "861050042339310",
	"IMSI": "undefined",
	"deviceType": "",
	"messageType": "dataReport",
	"topic": "v1/up/ad19",
	"assocAssetId": "",
	"payload": {
		"manhole_cover_position_state": 0,
		"lean_angle": 0,
		"battery_voltage": 59,
		"water_level_state": 1,
		"Water_depth": 70,
		"sig": 13,
		"IMEI": "861050042339310",
		"IMSI": "460111174385652",
		"ecl": -97,
		"pci": 0,
		"rsrp": 100,
		"sinr": 0,
		"cell_id": 165115986,
		"AccX": -544,
		"AccY": -173,
		"AccZ": -587,
		"XAver": 0,
		"YAver": 0,
		"ZAver": 0,
		"YshackCount": 0,
		"ZshackCount": 0,
		"AllShackCount": 0
	},
	"upPacketSN": "",
	"upDataSN": "",
	"serviceId": 7,
	"tenantId": "10431117",
	"productId": "10045636",
	"deviceId": "59e0462b286a45e4bc5642109fd6ea90",
	"timestamp": 1586400800535,
	"protocol": "lwm2m"
}
 */
public class WingsIotDataTransform {
	//用于存储点英文标识和中文名字对照关系
///	private static Map<String, String> paraNameTransTable;
	//用于存储点新标识转换为旧标识
	private static Map<String, String> keyTransTable;
	
	static{
//		paraNameTransTable = new HashMap<String,String>();
//		paraNameTransTable.put("xgcm", "震动计数");
//		paraNameTransTable.put("pt", "协议类型");
//		paraNameTransTable.put("dt", "设备类型");
//		paraNameTransTable.put("dc", "产品编号");
//		paraNameTransTable.put("ygcm", "沉降计数");
//		paraNameTransTable.put("hardwareid", "硬件编码");
//		paraNameTransTable.put("zavtv", "Z轴震动时相对初始值的平均值");
//		paraNameTransTable.put("yavtv", "Y轴震动时相对初始值的平均值");
//		paraNameTransTable.put("xavtv", "X轴震动时相对初始值的平均值");
//		paraNameTransTable.put("sig", "信号强度");
//		paraNameTransTable.put("bv", "电池电压");
//		paraNameTransTable.put("rt", "上报类型");
//		paraNameTransTable.put("hv", "固件版本号");
//		paraNameTransTable.put("xgiv", "井盖状态");
//		paraNameTransTable.put("zavm", "z当前加速度");
//		paraNameTransTable.put("yavm", "y当前加速度");
//		paraNameTransTable.put("xavm", "x当前加速度");
//		paraNameTransTable.put("fv", "固件版本");
//		paraNameTransTable.put("fcn", "NB模组连接的频点");
//		paraNameTransTable.put("ygiv", "识别是否蓝牙上报");
//		paraNameTransTable.put("pci", "小区物理ID");
//		paraNameTransTable.put("cellid", "小区ID");
//		paraNameTransTable.put("sinr", "信噪比");
//		paraNameTransTable.put("rsrp", "信号接收功率");
//		paraNameTransTable.put("ecl", "覆盖范围");
//		paraNameTransTable.put("xgtv", "X轴当前角度阀值");
//		paraNameTransTable.put("ygtv", "Y轴当前角度阀值");
//		paraNameTransTable.put("yavtvAvg", "yavtv平均值");
//		paraNameTransTable.put("zgtv", "Z轴当前角度阀值");
//		paraNameTransTable.put("xavtvAvg", "xavtv平均值");
//		paraNameTransTable.put("aReport", "分析上报");
//		paraNameTransTable.put("zavtvAvg", "zavtv平均值");
//		paraNameTransTable.put("xgcmAvg", "xgcmAvg平均值");
//		paraNameTransTable.put("ygcmAvg", "ygcm平均值");	
//		paraNameTransTable.put("water_level_state", "水位状态");
//		paraNameTransTable.put("IMSI", "IMSI");	
//		paraNameTransTable.put("IMEI", "IMEI");	
//		paraNameTransTable.put("lean_angle", "倾斜角度");	
//		paraNameTransTable.put("AllShackCount", "总震动唤醒次数");
//		paraNameTransTable.put("Water_depth", "水深");
//		paraNameTransTable.put("sn", "消息序列号");
//		paraNameTransTable.put("pv", "协议版本");
//		paraNameTransTable.put("ct", "控制类型");
//		paraNameTransTable.put("SoftVer", "固件版本");
//		paraNameTransTable.put("AD3_value", "模拟量AD3");
//		paraNameTransTable.put("AD2_value", "模拟量AD2");
//		paraNameTransTable.put("K3_value", "开关量K3");
//		paraNameTransTable.put("K2_value", "开关量K2");
//		paraNameTransTable.put("reservedByte3", "预留字节3");
//		paraNameTransTable.put("reservedByte2", "预留字节2");
//		paraNameTransTable.put("reservedByte1", "预留字节1");
		
		keyTransTable = new HashMap<String,String>();
		keyTransTable.put("ZAver", "zavtv");
		keyTransTable.put("YAver", "yavtv");
		keyTransTable.put("XAver", "xavtv");
		keyTransTable.put("AccZ", "zavm");
		keyTransTable.put("AccY", "yavm");
		keyTransTable.put("AccX", "xavm");
		keyTransTable.put("ZshackCount", "ygcm");
		keyTransTable.put("YshackCount", "xgcm");
		keyTransTable.put("battery_voltage", "bv");
		keyTransTable.put("manhole_cover_position_state", "xgiv");
		keyTransTable.put("manhole_cover_state", "xgiv");
		keyTransTable.put("cell_id", "cellid");
//		Collections.unmodifiableMap(paraNameTransTable);
		Collections.unmodifiableMap(keyTransTable);
		
	}
	
	
	
	
	@SuppressWarnings("rawtypes")
	public static JSONObject transfrom(JSONObject reportedData,Map<String, String> paraNameTransTable) throws Exception{
//		System.out.println("接收到的iot平台上报数据");
//		System.out.println("-----------------");
//		System.out.println(reportedData.toJSONString());
		
		JSONObject finalData = new JSONObject();
		
		
		finalData.put("deviceId", reportedData.getString("deviceId"));
		finalData.put("productId", reportedData.getString("productId"));
		
		long timestamp = reportedData.getLongValue("timestamp");
		
		finalData.put("timestamp", timestamp);
		finalData.put("messageType", reportedData.getString("messageType"));
		finalData.put("tenantId", reportedData.getString("tenantId"));
		
		//为数据中添加things字段，暂时使用，用于匹配接受老iot数据的代码,后续更改后删除
		JSONObject things = new JSONObject();
		things.put("thingType", "WingsIot");
		things.put("subThingId", reportedData.getString("IMEI"));
		things.put("gatewayId", "pzGatewayId" + reportedData.getString("productId"));
		finalData.put("things", things);
		
		//用于存储转换后的数据点信息
		JSONArray attributeInfoArray = new JSONArray();
		//获取上报数据中的数据点数据
		JSONObject payload = reportedData.getJSONObject("payload");
		
		for (Map.Entry entry : payload.entrySet()) {
			JSONObject attribute = new JSONObject();
			String key = (String)entry.getKey();
			//如果获取的key存在于key转换表中，则需要将新的key转换为以前的key
			if (keyTransTable.containsKey(key)) {
				attribute.put("attributeName", keyTransTable.get(key));
			}
			else{
				//不存在，则不需转换，该key可直接使用
				attribute.put("attributeName", key);
			}
			if (key.equals("outlid_state")) {
				attribute.put("attributeAlias", paraNameTransTable.get(attribute.get("attributeName")));
				attribute.put("attributeReportedTime", timestamp);
				if (Integer.parseInt(entry.getValue().toString()) == 0) {
					attribute.put("attributeReportedValue", "close");
				}
				else if (Integer.parseInt(entry.getValue().toString()) == 1) {
					attribute.put("attributeReportedValue", "open");
				} 
				else{
					attribute.put("attributeReportedValue", entry.getValue());
				}
				attributeInfoArray.add(attribute);
			}
			else{
				attribute.put("attributeAlias", paraNameTransTable.get(attribute.get("attributeName")));
				attribute.put("attributeReportedTime", timestamp);
				attribute.put("attributeReportedValue", entry.getValue());
				attributeInfoArray.add(attribute);
			}
		
		}
		//增添fv属性，因为新上报的数据中无fv，Device_RealRported表中fv为必填字段
		JSONObject fv = new JSONObject();
		fv.put("attributeName", "fv");
		fv.put("attributeAlias", "固件版本");
		fv.put("attributeReportedTime", timestamp);
		fv.put("attributeReportedValue", 1);
		attributeInfoArray.add(fv);
		//增添pt属性，为支持演示开锁
		JSONObject pt = new JSONObject();
		pt.put("attributeName", "pt");
		pt.put("attributeAlias", "协议类型");
		pt.put("attributeReportedTime", timestamp);
		pt.put("attributeReportedValue", "device common protocol");
		attributeInfoArray.add(pt);
		//增添dt属性，为支持演示开锁
		JSONObject dt = new JSONObject();
		dt.put("attributeName", "dt");
		dt.put("attributeAlias", "设备类型");
		dt.put("attributeReportedTime", timestamp);
		dt.put("attributeReportedValue", "bluetooth manhole");
		attributeInfoArray.add(dt);
		
		String hardwareid = reportedData.getString("IMEI");
		String use = "000" + hardwareid;
		JSONObject hardwareID = new JSONObject();
		hardwareID.put("attributeName", "hardwareid");
		hardwareID.put("attributeAlias", "硬件id");
		hardwareID.put("attributeReportedTime", timestamp);
		hardwareID.put("attributeReportedValue", use);
		attributeInfoArray.add(hardwareID);
		
		
		finalData.put("attributeInfo", attributeInfoArray);
		
		
//		System.out.println("转换后的数据");
//		System.out.println("----------------");
//		System.out.println(finalData.toJSONString());
		return finalData;
		
	}

}