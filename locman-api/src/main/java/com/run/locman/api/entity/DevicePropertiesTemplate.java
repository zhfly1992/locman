package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @author
 */
public class DevicePropertiesTemplate implements Serializable {
	private String				id;

	/**
	 * 模版名称
	 */
	private String				templateName;

	/**
	 * 创建时间
	 */
	private String				creationTime;

	/**
	 * 修改时间
	 */
	private String				editorTime;

	/**
	 * 管理状态
	 */
	private String				manageState;

	private String				accessSecret;

	private static final long	serialVersionUID	= 1L;



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getTemplateName() {
		return templateName;
	}



	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}



	public String getCreationTime() {
		return creationTime;
	}



	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}



	public String getEditorTime() {
		return editorTime;
	}



	public void setEditorTime(String editorTime) {
		this.editorTime = editorTime;
	}



	public String getManageState() {
		return manageState;
	}



	public void setManageState(String manageState) {
		this.manageState = manageState;
	}



	public String getAccessSecret() {
		return accessSecret;
	}



	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DevicePropertiesTemplate [id=" + id + ", templateName=" + templateName + ", creationTime="
				+ creationTime + ", editorTime=" + editorTime + ", manageState=" + manageState + ", accessSecret="
				+ accessSecret + "]";
	}
	
	
}