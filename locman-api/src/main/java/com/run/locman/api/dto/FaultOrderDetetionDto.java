/*
* File name: FaultOrderDetetionDto.java								
*
* Purpose:
*
* Functions used and called:	
* Name			Purpose
* ...			...
*
* Additional Information:
*
* Development History:
* Revision No.	Author		Date
* 1.0			guofeilong		2018年7月10日
* ...			...			...
*
***************************************************/

package com.run.locman.api.dto;

/**
* @Description:	
* @author: guofeilong
* @version: 1.0, 2018年7月10日
*/

public class FaultOrderDetetionDto {
	/**
	 * 故障工单id
	 */
	private String id;
	/**
	 * 流程id
	 */
	private String processId;
	/**
	 * 用户id
	 */
	private String userId;
	/**
	 * 密钥
	 */
	private String accessSecret;
	/**
	 * 
	 */
	public FaultOrderDetetionDto() {
		super();
		
	}
	/**
	 * @param id
	 * @param processId
	 * @param userId
	 * @param accessSecret
	 */
	public FaultOrderDetetionDto(String id, String processId, String userId, String accessSecret) {
		super();
		this.id = id;
		this.processId = processId;
		this.userId = userId;
		this.accessSecret = accessSecret;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the processId
	 */
	public String getProcessId() {
		return processId;
	}
	/**
	 * @param processId the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the accessSecret
	 */
	public String getAccessSecret() {
		return accessSecret;
	}
	/**
	 * @param accessSecret the accessSecret to set
	 */
	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FaultOrderDetetionDto [toString()=" + super.toString() + "]";
	}

	
}
