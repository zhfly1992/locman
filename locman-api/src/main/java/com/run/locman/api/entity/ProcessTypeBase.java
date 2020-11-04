/*
 * File name: ProcessTypeBase.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 tianming 2018年02月01日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.io.Serializable;

/**
 * @Description: 工单流程基础类型实体类
 * @author: 田明
 * @version: 1.0, 2018年02月01日
 */
public class ProcessTypeBase implements Serializable {

	private static final long	serialVersionUID	= 1L;

	/** 主键id */
	private String				id;
	/** 流程类型 */
	private String				processType;



	public ProcessTypeBase() {
	}



	public ProcessTypeBase(String id, String processType) {
		this.id = id;
		this.processType = processType;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getProcessType() {
		return processType;
	}



	public void setProcessType(String processType) {
		this.processType = processType;
	}
}
