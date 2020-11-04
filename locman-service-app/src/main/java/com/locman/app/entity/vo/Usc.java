package com.locman.app.entity.vo;

import java.util.List;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

public class Usc extends JdkSerializationRedisSerializer{

	private String		userId;
	private String		loginAccount;
	private String		activateState;	// 激活状态
	private String		isDelete;		// 删除状态
	private String		mobile;
	private String		email;
	private String		userType;
	private String		userCode;		// 用户编码
	private String		state;			// 禁用、启用状态
	private String		token;
	private String		userName;
	private Access		access;
	private List<Role>	role;


	@Override
	public byte[] serialize(Object object) {
		return super.serialize(object);
	}

	public String getUserId() {
		return userId;
	}



	public void setUserId(String userId) {
		this.userId = userId;
	}



	public String getUserName() {
		return userName;
	}



	public void setUserName(String userName) {
		this.userName = userName;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getLoginAccount() {
		return loginAccount;
	}



	public void setLoginAccount(String loginAccount) {
		this.loginAccount = loginAccount;
	}



	public String getActivateState() {
		return activateState;
	}



	public void setActivateState(String activateState) {
		this.activateState = activateState;
	}



	public String getIsDelete() {
		return isDelete;
	}



	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}



	public String getMobile() {
		return mobile;
	}



	public void setMobile(String mobile) {
		this.mobile = mobile;
	}



	public String getUserType() {
		return userType;
	}



	public void setUserType(String userType) {
		this.userType = userType;
	}



	public String getUserCode() {
		return userCode;
	}



	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}



	public String getState() {
		return state;
	}



	public void setState(String state) {
		this.state = state;
	}



	public String getToken() {
		return token;
	}



	public void setToken(String token) {
		this.token = token;
	}



	public Access getAccess() {
		return access;
	}



	public void setAccess(Access access) {
		this.access = access;
	}



	public List<Role> getRole() {
		return role;
	}



	public void setRole(List<Role> role) {
		this.role = role;
	}

}
