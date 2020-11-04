/*
 * File name: SwitchLockRecordCrudServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年8月3日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.service.crud;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.SwitchLockRecordCrudRepository;
import com.run.locman.api.crud.service.SwitchLockRecordCrudService;
import com.run.locman.api.entity.ManholeCoverSwitch;
import com.run.locman.api.entity.SwitchLockRecord;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.PublicConstants;
import com.run.locman.constants.SwitchLockRecordConstants;

/**
 * @Description:开关锁记录crud实现类
 * @author: zhaoweizhi
 * @version: 1.0, 2018年8月3日
 */

@Transactional(rollbackFor = Exception.class)
public class SwitchLockRecordCrudServiceImpl implements SwitchLockRecordCrudService {

	private static final Logger				logger	= Logger.getLogger(CommonConstants.LOGKEY);

	@Autowired
	private SwitchLockRecordCrudRepository	switchLockRecordCrudRepository;

	@Value("${status.flag.test}")
	private String							testFlag;
	/**
	 *  特殊处理的设备类型 (智能条形锁)对应的deviceTypeId
	 */
	@Value("${deviceType.barLock}")
	private String							deviceTypeIdList;



	/**
	 * @see com.run.locman.api.crud.service.SwitchLockRecordCrudService#insertSwitchLockInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<Boolean> insertSwitchLockInfo(String deviceId, JSONObject reported) {

		try {
			// 解析设备状态数据->触发上报
			logger.info(String.format("[insertSwitchLockInfo()->进入方法,参数:%s,%s]", deviceId, reported));
			if (StringUtils.isBlank(deviceId)) {
				logger.error(String.format("[analysisDeviceData()->error:%s--%s]", SwitchLockRecordConstants.DEVICE_ID,
						PublicConstants.BUSINESS_INVALID));
				return RpcResponseBuilder.buildErrorRpcResp(PublicConstants.BUSINESS_INVALID);
			}
			if (reported == null) {
				logger.error(String.format("[analysisDeviceData()->error:%s--%s]",
						SwitchLockRecordConstants.DATA_REPORTED, PublicConstants.BUSINESS_INVALID));
				return RpcResponseBuilder.buildErrorRpcResp(PublicConstants.BUSINESS_INVALID);
			}

			// 1. 通过设备Id 获取命令表中最新一条的记录
			Map<String, String> findRemoteControlRecord = switchLockRecordCrudRepository
					.findRemoteControlRecord(deviceId);

			if (findRemoteControlRecord == null
					|| findRemoteControlRecord.get(SwitchLockRecordConstants.SWITCH_CONTROLITEM) == null) {
				logger.info(String.format("[insertSwitchLockInfo()->suc:%s]", SwitchLockRecordConstants.SWITCH_NULL));
				return RpcResponseBuilder.buildSuccessRpcResp(SwitchLockRecordConstants.SWITCH_NULL, false);
			}

			// 2. 通过设备Id获取到设备类型Id
			String deviceType = switchLockRecordCrudRepository.findDeviceType(deviceId);

			// 3. 通过设备类型以及锁状态数据和远程命令下发的key进行逻辑判断确认是否关还是开
			String judgeSwitchState = judgeSwitchState(deviceType, reported,
					findRemoteControlRecord.get(SwitchLockRecordConstants.SWITCH_CONTROLITEM));

			// 4.开：判断命令时间》当前时间-3分钟，并且命令有效，关：查询开关锁记录表最新一条记录为open
			Boolean checkLockState = checkLockState(judgeSwitchState, findRemoteControlRecord);

			// 5. 判断是否有效，封装数据保存到开关锁记录中
			if (!checkLockState) {
				logger.info(
						String.format("[insertSwitchLockInfo()->suc:%s]", SwitchLockRecordConstants.SWITCH_NO_SACE));
				return RpcResponseBuilder.buildSuccessRpcResp(SwitchLockRecordConstants.SWITCH_NO_SACE, false);
			}

			findRemoteControlRecord.put(SwitchLockRecordConstants.DEVICE_LOCKSTATE, judgeSwitchState);
			SwitchLockRecord anSwitchLockRecord = anSwitchLockRecord(findRemoteControlRecord);

			// 6. 插入数据
			int insertSwitchLockRecord = switchLockRecordCrudRepository.insertSwitchLockRecord(anSwitchLockRecord);
			if (insertSwitchLockRecord > 0) {
				logger.info(String.format("[insertSwitchLockInfo()->suc:%s]", SwitchLockRecordConstants.SWITCH_SUC));
				return RpcResponseBuilder.buildSuccessRpcResp(
						String.format("[insertSwitchLockInfo()->suc:%s]", SwitchLockRecordConstants.SWITCH_SUC), true);
			}

			logger.error(String.format("[insertSwitchLockInfo()->error:%s]", SwitchLockRecordConstants.SWITCH_NO));
			return RpcResponseBuilder.buildErrorRpcResp(
					String.format("[insertSwitchLockInfo()->error:%s]", SwitchLockRecordConstants.SWITCH_NO));

		} catch (Exception e) {
			logger.error("insertSwitchLockInfo()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}

	}



	/**
	 * 
	 * @Description:通过设备类型以及数据点进行逻辑判断是否开关-》针对光交以及后期增加的新类型做出特殊判断 ,针对于状态上报开启有多个属性，关闭只有close
	 * @param deviceType
	 * @param reported
	 * @return
	 */
	private String judgeSwitchState(String deviceType, JSONObject reported, String deviceStateKey) throws Exception {
		if (deviceTypeIdList.contains(deviceType))
		// if
		// (SwitchLockRecordConstants.DEVICE_LIGHTIN_SPECIAL.equals(deviceType))
		{
			// 光交设备类型特殊处理

			if (!reported.containsKey(SwitchLockRecordConstants.SWITCH_LS_1)
					|| !reported.containsKey(SwitchLockRecordConstants.SWITCH_LS_2)) {
				logger.error(String.format("[judgeSwitchState()->error:%s]", SwitchLockRecordConstants.SWITCH_LS_NULL));
				return null;
			}

			// 原则单开为开，双关为关
			if (reported.getString(SwitchLockRecordConstants.SWITCH_LS_1).equals(SwitchLockRecordConstants.SWITCH_CLOSE)
					&& reported.getString(SwitchLockRecordConstants.SWITCH_LS_2)
							.equals(SwitchLockRecordConstants.SWITCH_CLOSE)) {
				return SwitchLockRecordConstants.SWITCH_CLOSE;
			} else {
				return SwitchLockRecordConstants.SWITCH_OPEN;
			}

		} else {
			// 普通设备类型处理
			if (!reported.containsKey(deviceStateKey)) {
				logger.error(String.format("[judgeSwitchState()->error:%s]", SwitchLockRecordConstants.SWITCH_LS_NULL));
				return null;
			}

			// 判断状态
			if (SwitchLockRecordConstants.SWITCH_CLOSE.equals(reported.getString(deviceStateKey))) {
				return SwitchLockRecordConstants.SWITCH_CLOSE;
			} else {
				return SwitchLockRecordConstants.SWITCH_OPEN;
			}

		}

	}



	/**
	 * 
	 * @Description: 校验lockState开关的有效性
	 * @param lockState
	 * @return
	 * @throws Exception
	 */
	private Boolean checkLockState(String lockState, Map<String, String> findRemoteControlRecord) throws Exception {

		if (SwitchLockRecordConstants.SWITCH_OPEN.equals(lockState)) {

			// 当前时间-3分钟 小于等于命令下发时间->并且有效
			Calendar beforeTime = Calendar.getInstance();
			// 3分钟之前的时间
			beforeTime.add(Calendar.MINUTE, -3);
			Date date = beforeTime.getTime();
			String formatDate = DateUtils.formatDate(date);
			String controlTime = findRemoteControlRecord.get(SwitchLockRecordConstants.COMMAND_CONTROLTIME);
			if (formatDate.compareTo(controlTime) <= 0 && SwitchLockRecordConstants.COMMAND_VALID
					.equals(findRemoteControlRecord.get(SwitchLockRecordConstants.COMMAND_CONTROLSTATE))) {
				return true;
			}

			return false;
		} else if (SwitchLockRecordConstants.SWITCH_CLOSE.equals(lockState)) {

			// 获取开关锁记录中最新的一条是否为开-> 设备id查询

			Map<String, String> findSwitchLockRecord = switchLockRecordCrudRepository
					.findSwitchLockRecord(findRemoteControlRecord.get(SwitchLockRecordConstants.DEVICE_ID));
			if (findSwitchLockRecord == null) {
				logger.info(String.format("[checkLockState()->suc:%s]", SwitchLockRecordConstants.SWITCH_INVALID));
				return false;
			}

			lockState = findSwitchLockRecord.get(SwitchLockRecordConstants.DEVICE_LOCKSTATE);

			// TODO 暂时性屏蔽一体化设备上报数据时错误（在上一条记录是开的时候，当前上报状态的关相差小于20s，那么不记录）\
			if (Boolean.valueOf(testFlag)) {
				String date = findSwitchLockRecord.get(SwitchLockRecordConstants.SWITCH_EXPROT_REPORTTIME) + "";
				Boolean calculationDate = calculationDate(date);
				if (calculationDate) {
					return false;
				}
			}

			// 上一条是开才是一个正常的关记录
			if (SwitchLockRecordConstants.SWITCH_OPEN.equals(lockState)) {
				return true;
			} else {
				return false;
			}

		}

		logger.error(
				String.format("[checkLockState()->suc:%s=%s]", SwitchLockRecordConstants.DEVICE_LOCKSTATE, lockState));
		return false;

	}



	/**
	 * @Description:暂时性屏蔽20s开之后上报的关数据
	 * @param date
	 * @param newDate
	 */

	private Boolean calculationDate(String date) throws Exception {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date parse = df.parse(date);
		Date res = new Date(System.currentTimeMillis() - 20000);
		if (parse.getTime() > res.getTime()) {
			return true;
		}
		return false;
	}



	/**
	 * 
	 * @Description:封装开关锁实体对象
	 * @param remoteControlRecordMap
	 * @return
	 */
	private SwitchLockRecord anSwitchLockRecord(Map<String, String> remoteControlRecordMap) throws Exception {

		SwitchLockRecord switchLockRecord = new SwitchLockRecord();
		switchLockRecord.setId(UtilTool.getUuId());
		switchLockRecord.setArrangeUserId(remoteControlRecordMap.get(SwitchLockRecordConstants.COMMAND_CONTROLUSERID));
		switchLockRecord.setDeviceId(remoteControlRecordMap.get(SwitchLockRecordConstants.DEVICE_ID));
		switchLockRecord.setLockState(remoteControlRecordMap.get(SwitchLockRecordConstants.DEVICE_LOCKSTATE));
		switchLockRecord.setRemoteControlRecordId(remoteControlRecordMap.get(SwitchLockRecordConstants.ID));
		switchLockRecord.setReportTime(DateUtils.formatDate(new Date()));
		switchLockRecord.setAccessSecret(remoteControlRecordMap.get(SwitchLockRecordConstants.COMMAND_ACCESSSECRET));
		return switchLockRecord;
	}



	/**
	 * @see com.run.locman.api.crud.service.SwitchLockRecordCrudService#insertManholeSwitch(java.lang.String, com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public RpcResponse<Integer> insertManholeSwitch(String deviceId, JSONObject reported,String timestamp) {
		logger.info(String.format("[insertManholeSwitch()->进入方法,参数:%s,%s，%s]", deviceId, reported,timestamp));
		try {
			String rt=reported.getString("rt");
			String xgiv=reported.getString("xgiv");
			String time=DateUtils.stampToDate(timestamp);
			Map<String ,Object> mapInfo=switchLockRecordCrudRepository.findManholeSwitchByDeviceID(deviceId);
			if(mapInfo !=null &&mapInfo.size()>0) {
				if("trigger".equals(rt) && "1".equals(xgiv)) {
					if( mapInfo.get("closeTime")!=null) {
						ManholeCoverSwitch manholeCoverSwitch=new ManholeCoverSwitch();
						manholeCoverSwitch.setId(UtilTool.getUuId());
						manholeCoverSwitch.setDeviceId(deviceId);
						manholeCoverSwitch.setOpenTime(time);
						int insertManholeSwitch=switchLockRecordCrudRepository.insertManholeSwitch(manholeCoverSwitch);
						if(insertManholeSwitch>0) {
							logger.info("insertManholeSwitch()->插入开启记录成功");
							return RpcResponseBuilder.buildSuccessRpcResp("插入开启记录成功！", insertManholeSwitch);
						}else {
							logger.error("insertManholeSwitch()->插入开启记录失败");
							return RpcResponseBuilder.buildErrorRpcResp("插入开启记录失败！");
						}
					} 
				}else if("2".equals(xgiv) ) {
					if(mapInfo.get("closeTime") == null) {
						String id=mapInfo.get("id")+"";
						String openTime=mapInfo.get("openTime")+"";
						String openTimeStamp=DateUtils.dateToStamp(openTime);
						Long differenceStamp=Long.valueOf(timestamp)-Long.valueOf(openTimeStamp);
						String differenceTime=(differenceStamp/1000)+"";
						Map<String,Object> mapUpdate =new HashMap<String,Object>();
						mapUpdate.put("differenceTime", differenceTime);
						mapUpdate.put("closeTime",time);
						mapUpdate.put("id", id);
						int updateManholeSwitch=switchLockRecordCrudRepository.updateManholeSwitch(mapUpdate);
						if(updateManholeSwitch>0) {
							logger.info("insertManholeSwitch()->插入关闭记录成功");
							return RpcResponseBuilder.buildSuccessRpcResp("插入关闭记录成功！", updateManholeSwitch);  
						}else {
							logger.error("insertManholeSwitch()->插入关闭记录失败");
							return RpcResponseBuilder.buildErrorRpcResp("插入关闭记录失败！");
						}
					}
				}
			}else {
			logger.info("insertManholeSwitch()->之前未产生开井记录！");
			if("trigger".equals(rt) && "1".equals(xgiv)) {
					ManholeCoverSwitch manholeCoverSwitch=new ManholeCoverSwitch();
					manholeCoverSwitch.setId(UtilTool.getUuId());
					manholeCoverSwitch.setDeviceId(deviceId);
					manholeCoverSwitch.setOpenTime(time);
					int insertManholeSwitch=switchLockRecordCrudRepository.insertManholeSwitch(manholeCoverSwitch);
					if(insertManholeSwitch>0) {
						logger.info("insertManholeSwitch()->插入开启记录成功");
						return RpcResponseBuilder.buildSuccessRpcResp("插入开启记录成功！", insertManholeSwitch);
					}else {
						logger.error("insertManholeSwitch()->插入开启记录失败");
						return RpcResponseBuilder.buildErrorRpcResp("插入开启记录失败！");
					}
				}
			}
			return null;
		}catch(Exception e) {
			logger.error("insertManholeSwitch()->exception", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
