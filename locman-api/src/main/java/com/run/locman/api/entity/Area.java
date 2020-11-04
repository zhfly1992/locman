package com.run.locman.api.entity;

import java.io.Serializable;

/**
* @Description:	区域实体
* @author: wangsheng
* @version: 1.0, 2017年8月8日
*/
public class Area implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private String				id;
	private String				areaCode;
	private String				areaName;



	public Area(String id, String areaCode, String areaName) {
		super();
		this.id = id;
		this.areaCode = areaCode;
		this.areaName = areaName;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getAreaCode() {
		return areaCode;
	}



	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}



	public String getAreaName() {
		return areaName;
	}



	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
}