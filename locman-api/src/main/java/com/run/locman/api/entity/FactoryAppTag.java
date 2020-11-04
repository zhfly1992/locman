package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @author
 */
public class FactoryAppTag implements Serializable {
	private String				id;

	/**
	 * 厂家主键
	 */
	private String				factoryId;

	/**
	 * 厂家提供的apptag
	 */
	private String				appTag;

	private static final long	serialVersionUID	= 1L;



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getFactoryId() {
		return factoryId;
	}



	public void setFactoryId(String factoryId) {
		this.factoryId = factoryId;
	}



	public String getAppTag() {
		return appTag;
	}



	public void setAppTag(String appTag) {
		this.appTag = appTag;
	}
}