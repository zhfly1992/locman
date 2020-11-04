
package com.run.locman.query.service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.ResultBuilder;
import com.run.locman.api.query.service.InfoSummaryQueryService;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.InterGatewayConstants;
import com.run.locman.constants.OrderConstants;
import com.run.locman.filetool.ExcelView;

/**
 * 
 * @Description:
 * @author: Administrator
 * @version: 1.0, 2018年11月21日
 */
@Service
public class InfoSummaryRestQueryService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private InfoSummaryQueryService	infoSummaryQueryService;

	@Autowired
	private HttpServletRequest		request;



	public Result<Map<String, Object>> getDeviceCount(String accessSecret) {
		logger.info(String.format("[getDeviceCount()->request params--accessSecret:%s]", accessSecret));
		try {
			RpcResponse<Map<String, Object>> res = infoSummaryQueryService.getDeviceCountByAccessSecret(accessSecret);
			if (res == null || !res.isSuccess()) {
				logger.error("infoSummaryQueryService.getDeviceCountByaccessSecret-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			logger.info(String.format("[getDeviceCountByaccessSecret()success:--->%s]", res.getSuccessValue()));
			return ResultBuilder.successResult(res.getSuccessValue(), "查询成功");
		} catch (Exception e) {
			logger.error("getDeviceCountByaccessSecret()->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, Object>> getFacilityCount(String accessSecret) {
		logger.info(String.format("[getFacilityCount()->request params--accessSecret:%s]", accessSecret));
		try {
			RpcResponse<Map<String, Object>> res = infoSummaryQueryService.getFacilityCountByAccessSecret(accessSecret);
			if (res == null || !res.isSuccess()) {
				logger.error("infoSummaryQueryService.getFacilityCountByAccessSecret-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			logger.info(String.format("[getFacilityCountByAccessSecret()success:--->%s]", res.getSuccessValue()));
			return ResultBuilder.successResult(res.getSuccessValue(), "查询成功");
		} catch (Exception e) {
			logger.error("getFacilityCountByAccessSecret->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, Object>> getUnprocessedOrderCount(String accessSecret) {
		logger.info(String.format("[getUnprocessedOrderCount()->request params--accessSecret:%s]", accessSecret));
		try {
			RpcResponse<Map<String, Object>> res = infoSummaryQueryService
					.getUnprocessedOrderCountByAccessSecret(accessSecret);
			if (res == null || !res.isSuccess()) {
				logger.error("infoSummaryQueryService.getUnprocessedOrderCountByAccessSecret-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			logger.info(
					String.format("[getUnprocessedOrderCountByAccessSecret()success:--->%s]", res.getSuccessValue()));
			return ResultBuilder.successResult(res.getSuccessValue(), "查询成功");
		} catch (Exception e) {
			logger.error("getUnprocessedOrderCountByAccessSecret->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, String>> getDeviceCountNotReportInSetDay(String accessSecret) {
		logger.info(
				String.format("[getDeviceCountNotReportInSetDay()->request params--accessSecret:%s]", accessSecret));
		try {
			Map<String, String> data = Maps.newHashMap();
			RpcResponse<String> res = infoSummaryQueryService
					.getDeviceCountNotReportInSetDayByAccessSecret(accessSecret);
			if (res == null || !res.isSuccess()) {
				logger.error("infoSummaryQueryService.getDeviceCountNotReportInSetDayByAccessSecret-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			data.put("deviceNotReportIn10Day", res.getSuccessValue());
			logger.info(String.format("[getDeviceCountNotReportInSetDayByAccessSecret()success:--->%s]",
					res.getSuccessValue()));
			return ResultBuilder.successResult(data, "查询成功");
		} catch (Exception e) {
			logger.error("getDeviceCountNotReportInSetDayByAccessSecret->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, String>> getNormalDeviceCount(String accessSecret) {
		logger.info(String.format("[getNormalDeviceCount()->request params--accessSecret:%s]", accessSecret));
		try {
			Map<String, String> data = Maps.newHashMap();
			RpcResponse<String> res = infoSummaryQueryService.getNormalDeviceCountByAccessSecret(accessSecret);
			if (res == null || !res.isSuccess()) {
				logger.error("infoSummaryQueryService.getNormalDeviceCountByAccessSecre-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			data.put("normalDeviceCount", res.getSuccessValue());
			logger.info(String.format("[getNormalDeviceCountByAccessSecre()success:--->%s]", res.getSuccessValue()));
			return ResultBuilder.successResult(data, "查询成功");
		} catch (Exception e) {
			logger.error("getNormalDeviceCountByAccessSecre->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, String>> getUserNumber(String accessSecret) {
		logger.info(String.format("[getUserNumber()->request params--accessSecret:%s]", accessSecret));
		try {
			Map<String, String> data = Maps.newHashMap();
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			RpcResponse<String> res = infoSummaryQueryService.getUserNumberByAccessSecret(accessSecret, token);
			if (res == null || !res.isSuccess()) {
				logger.error("infoSummaryQueryService.getUserNumberByAccessSecret-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			data.put("userNumber", res.getSuccessValue());
			logger.info(String.format("[getUserNumberByAccessSecret()success:--->%s]", res.getSuccessValue()));
			return ResultBuilder.successResult(data, "查询成功");
		} catch (Exception e) {
			logger.error("getUserNumberByAccessSecret->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, String>> getAccessInformation(String accessSecret) {
		logger.info(String.format("[getAccessInformation()->request params--accessSecret:%s]", accessSecret));
		try {
			Map<String, String> data = Maps.newHashMap();
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			RpcResponse<String> res = infoSummaryQueryService.getAccessInformationByAccessSecret(accessSecret, token);
			if (res == null || !res.isSuccess()) {
				logger.error("infoSummaryQueryService.getAccessInformationByAccessSecret-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			data.put("accessInformation", res.getSuccessValue());
			logger.info(String.format("[getAccessInformationByAccessSecret()success:--->%s]", res.getSuccessValue()));
			return ResultBuilder.successResult(data, "查询成功");
		} catch (Exception e) {
			logger.error("getAccessInformationByAccessSecret->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, String>> getUsageRate(String accessSecret) {
		logger.info(String.format("[getUsageRate()->request params--accessSecret:%s]", accessSecret));
		try {
			Map<String, String> data = Maps.newHashMap();
			String token = request.getHeader(InterGatewayConstants.TOKEN);
			RpcResponse<String> res = infoSummaryQueryService.getUsageRateByAccessSecret(accessSecret, token);
			if (res == null || !res.isSuccess()) {
				logger.error("infoSummaryQueryService.getUsageRateByAccessSecret-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			data.put("usageRate", res.getSuccessValue());
			logger.info(String.format("[getUsageRateByAccessSecret()success:--->%s]", res.getSuccessValue()));
			return ResultBuilder.successResult(data, "查询成功");
		} catch (Exception e) {
			logger.error("getUsageRateByAccessSecret->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, Object>> getDailyAlarmCountInMonth(String accessSecret) {
		logger.info(String.format("[getDailyAlarmCountInMonth()->request params--accessSecret:%s]", accessSecret));
		try {
			RpcResponse<Map<String, Object>> result = infoSummaryQueryService.getDailyAlarmCountInMonth(accessSecret);
			if (result == null || !result.isSuccess()) {
				logger.error("infoSummaryQueryService.getDailyAlarmCountInMonth()-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			logger.info(String.format("[getDailyAlarmCountInMonth()success:--->%s]", result.getSuccessValue()));
			return ResultBuilder.successResult(result.getSuccessValue(), "查询成功");
		} catch (Exception e) {
			logger.error("getDailyAlarmCountInMonth->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, Object>> getDailyFaultCountInMonth(String accessSecret) {
		logger.info(String.format("[getDailyFaultCountInMonth()->request params--accessSecret:%s]", accessSecret));
		try {
			RpcResponse<Map<String, Object>> result = infoSummaryQueryService.getDailyFaultCountInMonth(accessSecret);
			if (result == null || !result.isSuccess()) {
				logger.error("infoSummaryQueryService.getDailyFaultCountInMonth()-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			}
			logger.info(String.format("[getDailyFaultCountInMonth()success:--->%s]", result.getSuccessValue()));
			return ResultBuilder.successResult(result.getSuccessValue(), "查询成功");
		} catch (Exception e) {
			logger.error("getDailyFaultCountInMonth->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:
	 * @param
	 * @return
	 */

	public Result<Map<String, Object>> countAlarmNumByDate(String accessSecret, String startTime, String endTime) {
		try {
			logger.info(String.format(
					"[countAlarmNumByDate()->request params--accessSecret:%s , startTime:%s , endTime:%s]",
					accessSecret, startTime, endTime));
			int dayNum = 30;
			if (StringUtils.isNotBlank(startTime)) {
				// if (!DateUtils.isValidDate(startTime)) {
				// logger.error("[getHistorySateByPage()->开始日期格式错误!]");
				// return ResultBuilder.failResult("开始日期格式错误!");
				// }

				if (StringUtils.isNotBlank(endTime)) {
					// if (!DateUtils.isValidDate(endTime)) {
					// logger.error("[getHistorySateByPage()->结束日期格式错误!]");
					// return ResultBuilder.failResult("结束日期格式错误!");
					// }

					String checkStartAndEndTime = UtilTool.checkStartAndEndTimeByDay(startTime, endTime);

					if (null != checkStartAndEndTime) {
						return ResultBuilder.failResult(checkStartAndEndTime);
					}

					dayNum = UtilTool.daysBetween(startTime, endTime) + 1;
					if (dayNum > 30) {
						logger.error("countAlarmNumByDate-->error,开始日期和结束日期相差不能超过30天");
						return ResultBuilder.failResult("查询失败,开始日期和结束日期相差不能超过30天");
					}

				} else {
					// 只有开始时间时往后推一个月,算出结束时间
					String dateString = DateUtils.getDateString(DateUtils.END);
					int daysBetween = UtilTool.daysBetween(startTime, dateString);
					String addDays = UtilTool.addDays(startTime, 29);
					if (daysBetween < 29) {
						addDays = UtilTool.addDays(startTime, daysBetween);
						dayNum = UtilTool.daysBetween(startTime, addDays) + 1;
					}

					if (null != addDays) {
						endTime = addDays;
					} else {
						logger.error("[getHistorySateByPage()->日期计算错误!]");
						return ResultBuilder.failResult("日期计算错误");
					}

				}
			}
			RpcResponse<Map<String, Object>> result = infoSummaryQueryService.countAlarmNumByDate(accessSecret, endTime,
					dayNum);
			if (result == null) {
				logger.error("infoSummaryQueryService.countAlarmNumByDate()-->error,查询失败");
				return ResultBuilder.failResult("查询失败");
			} else if (!result.isSuccess()) {
				logger.error("infoSummaryQueryService.countAlarmNumByDate()-->error," + result.getMessage());
				return ResultBuilder.failResult(result.getMessage());
			} else {
				logger.info(String.format("[countAlarmNumByDate()success:--->%s]", result.getSuccessValue()));
				return ResultBuilder.successResult(result.getSuccessValue(), "查询成功");
			}

		} catch (Exception e) {
			logger.error("countAlarmNumByDate->Exception:", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * 
	 * @Description:获取工单处理情况统计，统计每天未处理和已处理工单数量(最多30天)，可获取告警工单或者故障工单，由传入参数控制
	 * @param jsonObject
	 * @return
	 */
	public Result<List<Map<String, Object>>> getOrderStateStatistic(JSONObject jsonObject) {
		logger.info(String.format("[进入getOrderStateStatistic()->参数:%s]", jsonObject.toJSONString()));
		try {
			String startTime = jsonObject.getString(CommonConstants.STARTTIME);
			String endTime = jsonObject.getString(CommonConstants.ENDTIME);
			String accessSecret = jsonObject.getString(CommonConstants.ACCESSSECRET);
			String orderType = jsonObject.getString(OrderConstants.ORDER_TYPE);
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[getOrderStateStatistic->error:接入方密匙为空]");
				return ResultBuilder.failResult("接入方秘钥为空");
			}
			if (StringUtils.isBlank(orderType)) {
				logger.error("[getOrderStateStatistic->error:工单类型为空]");
				return ResultBuilder.failResult("工单类型为空");
			}
			if (!orderType.equals(OrderConstants.ALARM) && !orderType.equals(OrderConstants.FAULT)) {
				logger.error("[getOrderStateStatistic->error: orderType传入错误]");
				return ResultBuilder.failResult("工单类型传入错误");
			}
			// 天数,默认为30
			int dayCount = 30;
			if (StringUtils.isNotBlank(startTime)) {
				// if (!DateUtils.isValidDate(startTime)) {
				// logger.error("[getOrderStateStatistic()->error:开始日期格式错误!]");
				// return ResultBuilder.failResult("开始日期格式错误!");
				// }

				if (StringUtils.isNotBlank(endTime)) {
					// if (!DateUtils.isValidDate(endTime)) {
					// logger.error("[getOrderStateStatistic()->error:结束日期格式错误!]");
					// return ResultBuilder.failResult("结束日期格式错误!");
					// }

					dayCount = UtilTool.daysBetween(startTime, endTime) + 1;
					if (dayCount > 30) {
						logger.error("[getOrderStateStatistic()->error:时间间隔不能大于30天]");
						return ResultBuilder.failResult("日期间隔不能大于30");
					}
				} else {
					// 只有开始时间时往后推一个月,算出结束时间
					String addDays = UtilTool.addDays(startTime, 29);

					// 得到当前时间
					String now = DateUtils.formatDate(new Date());
					if (null != addDays) {

						// 若是当前时间与开始时间间隔大于30天，则用算出来的结束时间
						if (UtilTool.daysBetween(startTime, now) > 30) {
							endTime = addDays;
						} else {

							// 若是当前时间与开始时间间隔小于30天，则用当前时间作为结束时间
							endTime = now;
							// 重新计算天数
							dayCount = UtilTool.daysBetween(startTime, endTime) + 1;
						}
					} else {
						logger.error("[getOrderStateStatistic()->error:日期计算错误!]");
						return ResultBuilder.failResult("日期计算错误");
					}
				}
			}

			RpcResponse<List<Map<String, Object>>> result = infoSummaryQueryService
					.getOrderProcessStateNumStatistic(dayCount, endTime, accessSecret, orderType);
			if (result == null) {
				logger.error("[getOrderStateStatistic()-->error,查询失败]");
				return ResultBuilder.failResult("查询失败");
			} else if (!result.isSuccess()) {
				logger.error("[getOrderStateStatistic()-->error," + result.getMessage() + "]");
				return ResultBuilder.failResult(result.getMessage());
			} else {
				logger.info("[getOrderStateStatistic()--success]");
				return ResultBuilder.successResult(result.getSuccessValue(), "查询成功");
			}
		} catch (Exception e) {
			logger.error("[getOrderStateStatistic->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, Object>> countStreetAndStreetOfficeNum(JSONObject jsonObject) {
		logger.info(String.format("[countStreetAndStreetOfficeNum->进入方法，参数:%s]", jsonObject.toString()));
		try {
			if (StringUtils.isBlank(jsonObject.getString(CommonConstants.ACCESSSECRET))) {
				logger.error("[countStreetAndStreetOfficeNum->error:接入方秘钥为空]");
				ResultBuilder.failResult("查询错误，接入方秘钥为空");
			}
			String accessSecret = jsonObject.getString(CommonConstants.ACCESSSECRET);
			String token = request.getHeader(InterGatewayConstants.TOKEN);

			RpcResponse<Map<String, Object>> countStreetAndStreetOfficeNum = infoSummaryQueryService
					.countStreetAndStreetOfficeNum(accessSecret, token);
			if (countStreetAndStreetOfficeNum == null) {
				logger.error("[countStreetAndStreetOfficeNum()-->error,查询失败]");
				return ResultBuilder.failResult("查询失败");
			} else if (!countStreetAndStreetOfficeNum.isSuccess()) {
				logger.error(
						"[countStreetAndStreetOfficeNum()-->error," + countStreetAndStreetOfficeNum.getMessage() + "]");
				return ResultBuilder.failResult(countStreetAndStreetOfficeNum.getMessage());
			} else {
				logger.info("[countStreetAndStreetOfficeNum()-->success]");
				return ResultBuilder.successResult(countStreetAndStreetOfficeNum.getSuccessValue(), "查询成功");
			}
		} catch (Exception e) {
			logger.error("[countStreetAndStreetOfficeNum->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<Map<String, Object>> countFacAlarmNum(String accessSecret) {
		logger.info(String.format("[countFacAlarmNum->进入方法,accessSecret:%s]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[countFacAlarmNum->error:接入方秘钥为空]");
				ResultBuilder.failResult("查询错误，接入方秘钥为空");
			}
			RpcResponse<Map<String, Object>> countFacAlarmNum = infoSummaryQueryService.countFacAlarmNum(accessSecret);
			if (countFacAlarmNum == null) {
				logger.error("[countFacAlarmNum()-->error,查询失败]");
				return ResultBuilder.failResult("查询失败");
			} else if (!countFacAlarmNum.isSuccess()) {
				logger.error("[countFacAlarmNum()-->error," + countFacAlarmNum.getMessage() + "]");
				return ResultBuilder.failResult(countFacAlarmNum.getMessage());
			} else {
				logger.info("[countFacAlarmNum()-->success]");
				return ResultBuilder.successResult(countFacAlarmNum.getSuccessValue(), "查询成功");
			}
		} catch (Exception e) {
			logger.error("[countFacAlarmNum->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	 * @Description:
	 * @param param
	 * @return
	 */

	public Result<Map<String, Object>> getcountDeviceReportNum(String param) {

		logger.info(String.format("[getcountDeviceReportNum->进入方法,param:%s]", param));
		try {
			if (StringUtils.isBlank(param)) {
				logger.error("[getcountDeviceReportNum->error:接入方秘钥为空]");
				ResultBuilder.failResult("查询错误，接入方秘钥为空");
			}
			RpcResponse<Map<String, Object>> getcountDeviceReportNum = infoSummaryQueryService
					.countDeviceReportNum(param);
			if (getcountDeviceReportNum == null) {
				logger.error("[getcountDeviceReportNum()-->error,查询失败]");
				return ResultBuilder.failResult("查询失败");
			} else if (!getcountDeviceReportNum.isSuccess()) {
				logger.error("[getcountDeviceReportNum()-->error," + getcountDeviceReportNum.getMessage() + "]");
				return ResultBuilder.failResult(getcountDeviceReportNum.getMessage());
			} else {
				logger.info("[getcountDeviceReportNum()-->success]");
				return ResultBuilder.successResult(getcountDeviceReportNum.getSuccessValue(), "查询成功");
			}
		} catch (Exception e) {
			logger.error("[getcountDeviceReportNum->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<JSONObject> countDeviceNumByAlarmLevelInSevenDays(String accessSecret) {
		logger.info(String.format("[countDeviceNumByAlarmLevelInSevenDay()s->进入方法,accessSecret:%s]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[countDeviceNumByAlarmLevelInSevenDays()->error,accessSecret为空]");
				return ResultBuilder.failResult("接入方秘钥不能为空");
			}
			RpcResponse<JSONObject> res = infoSummaryQueryService.countDeviceByAlarmLevelInSevenDays(accessSecret);
			if (!res.isSuccess()) {
				logger.error(String.format("[countDeviceNumByAlarmLevelInSevenDays->fail]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
			logger.info("[countDeviceNumByAlarmLevelInSevenDays->success]");
			return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
		} catch (Exception e) {
			logger.error("[countDeviceNumByAlarmLevelInSevenDays->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	public Result<JSONObject> countAlarmOrderNumByStateAndAlarm(String accessSecret) {
		logger.info(String.format("[countAlarmOrderNumByStateAndAlarm()->进入方法,accessSecret:%s]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[countAlarmOrderNumByStateAndAlarm()->error,accessSecret为空]");
				return ResultBuilder.failResult("接入方秘钥不能为空");
			}
			RpcResponse<JSONObject> res = infoSummaryQueryService.countAlarmOrderNumByStateAndAlarm(accessSecret);
			if (!res.isSuccess()) {
				logger.error(String.format("[countAlarmOrderNumByStateAndAlarm()->fail]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
			logger.info("[countAlarmOrderNumByStateAndAlarm()->success]");
			return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
		} catch (Exception e) {
			logger.error("[countAlarmOrderNumByStateAndAlarm()->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	public Result<Map<String ,Object>> countAlarmFacByOrg(String accessSecret) {
		logger.info(String.format("[countAlarmFacByOrg()->进入方法,accessSecret:%s]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[countAlarmFacByOrg()->error,accessSecret为空]");
				return ResultBuilder.failResult("接入方秘钥不能为空");
			}
			String token=request.getHeader("Token");
			RpcResponse<Map<String ,Object>> res = infoSummaryQueryService.countAlarmFacByOrg(accessSecret,token);
			if (!res.isSuccess()) {
				logger.error(String.format("[countAlarmFacByOrg()->fail]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
			logger.info("[countAlarmFacByOrg()->success]");
			return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
		} catch (Exception e) {
			logger.error("[countAlarmFacByOrg()->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	
	public Result<List<Map<String,Object>>> countAlarmByIdAndTime(JSONObject jsonObject){
		logger.info(String.format("[countAlarmByIdAndTime()->进入方法,jsonObject:%s]", jsonObject.toJSONString()));
		try {
			if (StringUtils.isBlank(jsonObject.toJSONString())) {
				logger.error("[countAlarmByIdAndTime()->error,accessSecret为空]");
				return ResultBuilder.failResult("接入方秘钥不能为空");
			}
			RpcResponse<List<Map<String,Object>>> res = infoSummaryQueryService.countAlarmByIdAndTime(jsonObject);
			if (!res.isSuccess()) {
				logger.error(String.format("[countAlarmByIdAndTime()->fail]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
			logger.info("[countAlarmByIdAndTime()->success]");
			return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
		} catch (Exception e) {
			logger.error("[countAlarmByIdAndTime()->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}



	/**
	* @Description:
	* @param accessSecret
	* @return
	*/
	
	public Result<List<Map<String, Object>>> countAlarmDevByRule(String accessSecret) {
		logger.info(String.format("[countAlarmDevByRule()->进入方法,accessSecret:%s]", accessSecret));
		try {
			if (StringUtils.isBlank(accessSecret)) {
				logger.error("[countAlarmDevByRule()->error,accessSecret为空]");
				return ResultBuilder.failResult("接入方秘钥不能为空");
			}
			RpcResponse<List<Map<String ,Object>>> res = infoSummaryQueryService.countAlarmDevByRule(accessSecret);
			if (!res.isSuccess()) {
				logger.error(String.format("[countAlarmDevByRule()->fail]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
			logger.info("[countAlarmDevByRule()->success]");
			return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
		} catch (Exception e) {
			logger.error("[countAlarmDevByRule()->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	/**
	 * 
	* @Description:
	* @param accessSecret
	* @return
	 */
	public Result<List<Map<String, Object>>> countTriggerTop(String params) {
		logger.info(String.format("[countTriggerTop()->进入方法,accessSecret:%s]", params));
		try {
			if (StringUtils.isBlank(params)) {
				logger.error("[countTriggerTop()->error,accessSecret为空]");
				return ResultBuilder.failResult("参数不能为空");
			}
			RpcResponse<List<Map<String ,Object>>> res = infoSummaryQueryService.countTriggerTop(params);
			if (!res.isSuccess()) {
				logger.error(String.format("[countTriggerTop()->fail]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());
			}
			logger.info("[countTriggerTop()->success]");
			return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
		} catch (Exception e) {
			logger.error("[countTriggerTop()->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	/**
	 * 
	* @Description:
	* @param params
	* @return
	 */
	@SuppressWarnings("unchecked")
	public Result<Map<String, Object>> detailsByDeviceId(JSONObject json) {
		logger.info(String.format("[detailsByDeviceId()->进入方法,json:%s]", json.toJSONString()));
		try {
			if (StringUtils.isBlank(json.toJSONString())) {
				logger.error("[detailsByDeviceId()->error,json为空]");
				return ResultBuilder.failResult("参数不能为空");
			}

			RpcResponse<Map<String ,Object>> res = infoSummaryQueryService.detailsByDeviceId(json);
			Map<String, Object> dateMap=(Map<String, Object>) res.getSuccessValue().get("dateMap");
			Map<String, Object> dateMap1 = new TreeMap<String, Object>(
	                new Comparator<String>() {
	                    public int compare(String obj1, String obj2) {
	                        // 降序排序
	                        return obj2.compareTo(obj1);
	                    }
	                });
			dateMap1.putAll(dateMap);
			res.getSuccessValue().put("dateMap", dateMap1);

			if (!res.isSuccess()) {
				logger.error(String.format("[detailsByDeviceId()->fail]", res.getMessage()));
				return ResultBuilder.failResult(res.getMessage());

			}
			logger.info("[detailsByDeviceId()->success]");

			return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
		} catch (Exception e) {
			logger.error("[detailsByDeviceId()->Exception:]", e);
			return ResultBuilder.exceptionResult(e);
		}
	}
	/**
	 * 
	* @Description:
	* @param json
	* @param model
	* @return
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView exportOpenDetails(JSONObject json, ModelMap model) {
		logger.info(String.format("[exportOpenDetails()->进入方法,json:%s]", json.toJSONString()));

		try {
			if (StringUtils.isBlank(json.toJSONString())) {
				logger.error("[exportOpenDetails()->error,json为空],检查数据参数");
				return null;
			}
			if (StringUtils.isBlank(json.getString("deviceId"))) {
				logger.error("[exportOpenDetails()->error:设备Id为空]");
			}
			if (StringUtils.isBlank(json.getString("days"))) {
				logger.error("[exportOpenDetails()->error:时间段为空]");
			}
			RpcResponse<Map<String ,Object>> res =infoSummaryQueryService.detailsByDeviceId(json);
			Map<String, Object> successValue = res.getSuccessValue();
			String deviceId=successValue.get("deviceId")+"";
			String address=successValue.get("address")+"";
			String facilitiesCode=successValue.get("facilitiesCode")+"";
			String deviceName=successValue.get("deviceName")+"";
//			String defenseState=successValue.get("defenseState")+"";
			List<String> dateList=(List<String>) successValue.get("date");
			List<Map<String,Object>> resultList=new ArrayList<Map<String,Object>>();
			for(String dateString:dateList) {
				Map<String,Object> maps=new HashMap<>();
				maps.put("deviceId", deviceId);
				maps.put("address", address);
				maps.put("facilitiesCode", facilitiesCode);
				maps.put("deviceName", deviceName);
//				maps.put("defenseState", defenseState);
				maps.put("date", dateString);
				resultList.add(maps);
			}
			Map<String ,Object> map=new LinkedHashMap<>();
			map.put("deviceId", "设备Id");
			map.put("address", "设施地址");
			map.put("facilitiesCode", "设施序列号");
			map.put("deviceName", "设备蓝牙");
//			map.put("defenseState", "设施状态");
			map.put("date", "开井时间");
			model.put(ExcelView.EXCEL_DATASET, resultList);
			model.put(ExcelView.EXCEL_NAME, "开井时间excel");
			model.put(ExcelView.EXCEL_COLUMNHEADING, map);
			View excelView = new ExcelView();
			
			return new ModelAndView(excelView);
			
		}catch(Exception e) {
			logger.error("exportDeviceState()->exception", e);
			return null;
		}
		
	}
}
