package com.run.locman.service.crud;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONObject;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.DateUtils;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.crud.repository.SmsRegistCrudRepository;
import com.run.locman.api.crud.service.SmsRegistService;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.SmsConstants;

/**
 * @Description:短信网关授权接口impl，调用短信网关创建授权接口
 * @author: 张贺
 * @version: 2018年6月22日
 */
public class SmsRegistServiceImpl implements SmsRegistService {

	private static final Logger		logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private SmsRegistCrudRepository	smsRegistCrudRepository;
	@Value("${sms.environmentValue}")
	public String					environmentValue;	
	
	private  final  int RESULT_KEY_ERROR = 1; 
	
	private  final  int RESULT_PARAMETER_ERRO = 2;
	
	private  final  int RESULT_INTERFACE_ERROR = 3;




	@Override
	public RpcResponse<String> smsRegist(String accessName, String accessSecret, String catelogId) {
		logger.info(String.format("[smsRegist()->进入方法,参数:accessName:%s,accessSecret:%s,catelogId:%s]", accessName,
				accessSecret, catelogId));
		String smsLimit = "";
		String authLimit = "";
		// 接入方密匙加上环境值用于业务ID,用于区分环境和接入方
		String uniqueId = accessSecret + "-" + environmentValue;
		// 密匙key固定
		String key = SmsConstants.KEY;
		// 参数和拼接顺序参考文档中心的短信网关
		String str = authLimit + catelogId + accessName + smsLimit + uniqueId + key;
		// 生成md值
		String span = DigestUtils.md5Hex(str);
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(SmsConstants.REGIST_URL);
		// 指定HttpClient请求的编码方式未"utf-8",解决POST中的参数中有中文的问题
		method.getParams().setContentCharset("utf-8");
		method.setRequestHeader("ContentType", "application/X-WWW-FORM-URLENCODED");
		NameValuePair[] data = { new NameValuePair("name", accessName), new NameValuePair("catelogId", catelogId),
				new NameValuePair("uniqueId", uniqueId), new NameValuePair("smsLimit", smsLimit),
				new NameValuePair("authLimit", authLimit), new NameValuePair("md5", span) };
		method.setRequestBody(data);
		try {
			client.executeMethod(method);
			String submitResult = method.getResponseBodyAsString();
			if (submitResult != null) {
				JSONObject json = JSONObject.parseObject(submitResult);
				if (json.getIntValue(SmsConstants.CODE) == 0) {
					String url = json.getString("data");
					String uuid = UUID.randomUUID().toString().replaceAll("-", "");
					Map<String, String> map = new HashMap<>(16);
					map.put("id", uuid);
					map.put("accessSecret", accessSecret);
					map.put("smsUrl", url);
					map.put("creatTime", DateUtils.formatDate(new Date()));
					int res = smsRegistCrudRepository.saveSmsRegistInformation(map);
					if (res > 0) {
						logger.info("smsRegist->success:授权地址保存成功");
						return RpcResponseBuilder.buildSuccessRpcResp("smsRegist success", url);
					} else {
						logger.error("smsRegist->fail:授权地址插入数据库失败");
						return RpcResponseBuilder.buildErrorRpcResp("授权地址插入数据库失败");
					}
				}
				if (json.getIntValue(SmsConstants.CODE) == RESULT_KEY_ERROR) {
					logger.error("smsRegist->fail : key错误");
					return RpcResponseBuilder.buildErrorRpcResp(json.getString(SmsConstants.MSG));
				}
				if (json.getIntValue(SmsConstants.CODE) == RESULT_PARAMETER_ERRO) {
					logger.error("smsRegist->fail :参数不能为空");
					return RpcResponseBuilder.buildErrorRpcResp(json.getString(SmsConstants.MSG));
				}
				if (json.getIntValue(SmsConstants.CODE) == RESULT_INTERFACE_ERROR) {
					logger.error("smsRegist->fail :接口错误");
					return RpcResponseBuilder.buildErrorRpcResp(json.getString(SmsConstants.MSG));
				} else {
					logger.error("smsRegist->fail :接口受限");
					return RpcResponseBuilder.buildErrorRpcResp(json.getString(SmsConstants.MSG));
				}
			}
			logger.error("smsRegist->fail :无返回信息");
			return RpcResponseBuilder.buildErrorRpcResp("error");
		} catch (HttpException e) {
			logger.error("smsRegist()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		} catch (IOException e) {
			logger.error("smsRegist()->exception", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}
}