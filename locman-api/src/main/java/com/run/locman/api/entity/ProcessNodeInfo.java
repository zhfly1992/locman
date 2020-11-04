/*
 * File name: ProcessNodeInfo.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年8月22日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.api.entity;

import java.util.List;

/**
 * @Description: 接收工作流参数
 * @author: zhaoweizhi
 * @version: 1.0, 2018年8月22日
 */

public class ProcessNodeInfo {

	private String			node;

	private List<String>	personId;



	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProcessNodeInfo [node=" + node + ", personId=" + personId + "]";
	}



	/**
	 * @return the node
	 */
	public String getNode() {
		return node;
	}



	/**
	 * @param node
	 *            the node to set
	 */
	public void setNode(String node) {
		this.node = node;
	}



	/**
	 * @return the personId
	 */
	public List<String> getPersonId() {
		return personId;
	}



	/**
	 * @param personId
	 *            the personId to set
	 */
	public void setPersonId(List<String> personId) {
		this.personId = personId;
	}

}
