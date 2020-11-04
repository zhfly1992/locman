package com.locman.app.crud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.locman.app.base.BaseAppController;
import com.locman.app.common.util.Constant;
import com.run.common.util.ExceptionChecked;
import com.run.entity.common.Result;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.ResultBuilder;
import com.run.usc.api.base.crud.UserBaseCurdService;
import com.run.usc.base.query.UserBaseQueryService;

@Service
@SuppressWarnings("rawtypes")
public class UscCrudService extends BaseAppController {

	@Autowired
	private UserBaseCurdService		userRpcCrud;
	@Autowired
	private UserBaseQueryService	userBaseQueryService;



	/**
	 * 
	 * <method description> 忘记密码
	 *
	 * @param param
	 *            {"code":"","newPassword":""}
	 * @return
	 */
	public Result appRestPassword(String param) {
		try {
			Result<String> result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return result;
			}
			JSONObject jsonObject = JSONObject.parseObject(param);
			if (!jsonObject.containsKey("code")) {
				return ResultBuilder.noBusinessResult();
			}
			String code = jsonObject.getString("code");
			if (!jsonObject.containsKey("newPassword")) {
				return ResultBuilder.noBusinessResult();
			}
			String newPassword = jsonObject.getString("newPassword");
			RpcResponse response = userRpcCrud.resetPasswordByAuthz(newPassword, code, Constant.MOBILE);
			if(response!=null && response.isSuccess()) {
				return ResultBuilder.successResult(response.getSuccessValue(), response.getMessage());
			}
			return ResultBuilder.failResult(response.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * <method description> 修改密码
	 *
	 * @param param
	 *            {"oldPassword":"","newPassword":""}
	 * @return
	 */
	public Result appChangePassword(String param) {
		try {
			Result result = ExceptionChecked.checkRequestParam(param);
			if (result != null) {
				return result;
			}
			JSONObject jsonObject = JSONObject.parseObject(param);
			if (!jsonObject.containsKey("oldPassword")) {
				return ResultBuilder.noBusinessResult();
			}
			if (!jsonObject.containsKey("newPassword")) {
				return ResultBuilder.noBusinessResult();
			}
			String oldPassword = jsonObject.getString("oldPassword");
			String newPassword = jsonObject.getString("newPassword");
			String token = request.getHeader("token");
			RpcResponse userResp = userBaseQueryService.getUserByToken(token);
			if (userResp.getSuccessValue() == null) {
				return ResultBuilder.failResult("无效的token");
			}
			JSONObject userJson = (JSONObject) JSONObject.toJSON(userResp.getSuccessValue());
			String oldPwd = userJson.getString("password");
			if (!oldPwd.equals(oldPassword)) {
				return ResultBuilder.failResult("原密码错误");
			}
			RpcResponse<JSONObject> response = userRpcCrud.updatePassword(token, newPassword);
			if(response!=null && response.isSuccess()) {
				return ResultBuilder.successResult(response.getSuccessValue(), response.getMessage());
			}
			return ResultBuilder.failResult(response.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResultBuilder.exceptionResult(e);
		}

	}



	/**
	 * 
	 * <method description> 注销用户
	 *
	 * @return 操作结果信息
	 */
	public Result appLoginOut() {
		String token = request.getHeader("token");
		try {
			if (token == null || "".equals(token)) {
				return ResultBuilder.failResult("Header token不能为空");
			}
			RpcResponse response = userBaseQueryService.getUserByToken(token);
			if (!response.isSuccess()) {
				return ResultBuilder.failResult("无效的token");
			}
			RpcResponse res = userRpcCrud.loginout(token);
			if (res.isSuccess()) {
				return ResultBuilder.successResult(res.getSuccessValue(), res.getMessage());
			} else {
				return ResultBuilder.failResult(res.getMessage());
			}
		} catch (Exception e) {
			return ResultBuilder.exceptionResult(e);
		}
	}
}
