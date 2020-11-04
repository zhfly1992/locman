/*
 * File name: SendTextMessageTaskServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 Administrator 2019年10月21日
 * ... ... ...
 *
 ***************************************************/

package com.run.locman.timer.cud;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.locman.api.timer.crud.repository.SendTextMessageTaskRepository;
import com.run.locman.api.timer.crud.service.SendTextMessageTaskService;
import com.run.locman.api.util.SendSms;
import com.run.locman.constants.CommonConstants;

/**
 * @Description:
 * @author: Administrator
 * @version: 1.0, 2019年10月21日
 */

public class SendTextMessageTaskServiceImpl implements SendTextMessageTaskService {

	/**
	 * @see com.run.locman.api.timer.crud.service.SendTextMessageTaskService#sendAlarmTextMessage()
	 */
	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	SendTextMessageTaskRepository	sendTextMessageTaskRepository;



	@Override
	public void sendAlarmTextMessage() {
		// TODO Auto-generated method stub
		try {
			//logger.info(String.format("[进入sendAlarmTextMessage->时间:%s]", DateUtils.formatDate(new Date())));

			// 从告警短信记录表中查找需要发送的告警短信
			List<Map<String, Object>> alarmTextMessages = sendTextMessageTaskRepository.getAlarmTextMessageForSend();

			if (alarmTextMessages == null || alarmTextMessages.size() == 0) {
//				logger.info(
//						String.format("[sendAlarmTextMessage->没有检测到需要发送的告警短信,time:%s]", DateUtils.formatDate(new Date())));
				return;
			}

			String id, phoneNumber, content, url;
			// 循环发送告警短信
			for (Map<String, Object> alarmMessage : alarmTextMessages) {
				// 无需校验id,phoneNumber,content，url为空，表中这些字段不能为空
				id = alarmMessage.get("id").toString();
				phoneNumber = alarmMessage.get("phoneNumber").toString();
				content = alarmMessage.get("smsContent").toString();
				url = alarmMessage.get("gatewayUrl").toString();
				RpcResponse<String> sendMessage = SendSms.sendMessage(phoneNumber, content, url);
				if (sendMessage.isSuccess()) {
					logger.info(String.format("[sendAlarmTextMessage->短信发送成功,记录id:%s]", id));
					System.out.println(String.format("[sendAlarmTextMessage->短信发送成功,记录id:%s]", id));
					// state=1表示发送成功
					int updateResult = sendTextMessageTaskRepository.updateTextMessageRecordState(id, 1,
							DateUtils.formatDate(new Date()), null);
					if (updateResult > 0) {
						logger.info(String.format("[sendAlarmTextMessage->短信发送成功，短信记录修改成功,id:%s]", id));
						break;
					} else {
						logger.error(String.format("[sendAlarmTextMessage->短信发送成功,短信记录修改失败,id:%s]", id));
						break;
					}
				} else {
					logger.error(String.format("[sendAlarmTextMessage->短信发送失败,记录id:%s]", id));
					// state=3表示发送失败
					int updateResult = sendTextMessageTaskRepository.updateTextMessageRecordState(id, 3,
							DateUtils.formatDate(new Date()), sendMessage.getMessage());
					if (updateResult > 0) {
						logger.info(String.format("[sendAlarmTextMessage->短信发送失败，短信记录修改成功,id:%s]", id));
						break;
					} else {
						logger.error(String.format("[sendAlarmTextMessage->短信发送失败,短信记录修改失败,id:%s]", id));
						break;
					}
				}

			}
		} catch (Exception e) {
			logger.error("[sendAlarmTextMessage()->exception]", e);
		}
	}

}
