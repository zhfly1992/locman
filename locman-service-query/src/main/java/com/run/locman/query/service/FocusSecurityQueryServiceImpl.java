/*
* File name: FocusSecurityQueryServiceImpl.java								
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
* 1.0			钟滨远		2020年4月28日
* ...			...			...
*
***************************************************/

package com.run.locman.query.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.run.entity.common.RpcResponse;
import com.run.entity.tool.RpcResponseBuilder;
import com.run.locman.api.query.repository.FocusSecurityQueryRepository;
import com.run.locman.api.query.service.FocusSecurityQueryService;
import com.run.locman.api.util.InterGatewayUtil;
import com.run.locman.api.util.UtilTool;
import com.run.locman.constants.CommonConstants;
import com.run.locman.constants.InterGatewayConstants;

/**
* @Description:	
* @author: 钟滨远
* @version: 1.0, 2020年4月28日
*/

public class FocusSecurityQueryServiceImpl implements FocusSecurityQueryService{
	
	private static final Logger			logger	= Logger.getLogger(CommonConstants.LOGKEY);
	@Autowired
	private FocusSecurityQueryRepository focusSecurityQueryRepository;
	
	@Value("${api.host}")
	private String							ip;

	/**
	 * @see com.run.locman.api.query.service.FocusSecurityQueryService#getFocusSecurityInfoPage(java.util.Map)
	 */
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> getFocusSecurityInfoPage(Map<String, Object> map) {
		logger.info("getFocusSecurityInfoPage()->方法执行开始！");
		
		try {
			int pageNum=Integer.parseInt(map.get("pageNum")+"");
			int pageSize=Integer.parseInt(map.get("pageSize")+"");
			String accessSecret=map.get("accessSecret")+"";
			Map<String,Object> queryMap=new HashMap<String,Object>();
			queryMap.put("accessSecret", accessSecret);
			PageHelper.startPage(pageNum, pageSize);
			List<Map<String, Object>> focusSecurityInfoPage = focusSecurityQueryRepository.getFocusSecurityInfoPage(queryMap);
			if(null !=focusSecurityInfoPage &&focusSecurityInfoPage.size() >0) {
				
				StringBuffer orgBuffer=new StringBuffer();
				List<String> listOrgId=Lists.newArrayList();
				for(Map<String, Object> infoMap:focusSecurityInfoPage) {
					String organization=infoMap.get("organization")+"";
					listOrgId.add(organization);
				}
				for(int i =0;i<listOrgId.size();i++) {
					if (i == listOrgId.size()) {
						orgBuffer.append(listOrgId.get(i));
					}
					orgBuffer.append(listOrgId.get(i) + ',');
				}
				//转中文
				JSONObject orgJson = new JSONObject();
				orgJson.put("organizationIds", orgBuffer);
				String httpValueByPost = InterGatewayUtil.getHttpValueByPost(
						InterGatewayConstants.U_ORGANIZATION_NAMES_BYID, orgJson, ip, map.get("token")+"");
				if (null == httpValueByPost) {
					logger.error("通过interGateway查询组织名失败");
					return RpcResponseBuilder.buildErrorRpcResp("查询组织名称失败！");
				} else {
					JSONObject nameJson = JSONObject.parseObject(httpValueByPost);
					for(Map<String, Object> infoMap1:focusSecurityInfoPage) {
						String organization=infoMap1.get("organization")+"";
						infoMap1.put("organization",nameJson.getString(organization));
					}
				}
				
				PageInfo<Map<String, Object>> page = new PageInfo<>(focusSecurityInfoPage);
				logger.info(String.format("[getFocusSecurityInfoPage()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("保障信息查询成功！", page);
			}
			logger.error("[getFocusSecurityInfoPage()方法执行结束!]");
			return RpcResponseBuilder.buildErrorRpcResp("保障信息查询失败！");
			
		}catch(Exception e) {
			logger.error("getFocusSecurityInfoPage()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

	/**
	 * @see com.run.locman.api.query.service.FocusSecurityQueryService#commandReceiveStates(java.util.Map)
	 */
	@Override
	public RpcResponse<PageInfo<Map<String, Object>>> commandReceiveStates(Map<String, Object> queryMap) {
		logger.info("commandReceiveStates()->方法执行开始！");
		
		try {
			int pageNum=Integer.parseInt(queryMap.get("pageNum")+"");
			int pageSize=Integer.parseInt(queryMap.get("pageSize")+"");
			Object organizationObject = queryMap.get(CommonConstants.ORGANIZATION_ID);
			List<String> findOrgChildId = Lists.newArrayList();
			if (null != organizationObject && StringUtils.isNotBlank(organizationObject + "") && StringUtils.isNotBlank(ip)) {
				findOrgChildId = UtilTool.findOrgChildId(organizationObject + "", queryMap.get(InterGatewayConstants.TOKEN) + "", logger, ip);
				
				findOrgChildId.add(organizationObject + "");
			}
			PageHelper.startPage(pageNum, pageSize);
			List<Map<String, Object>> focusSecurityInfoPage = focusSecurityQueryRepository.commandReceiveStates(queryMap,findOrgChildId);
			if(null !=focusSecurityInfoPage && focusSecurityInfoPage.size() > 0) {
				/*
				// 查询列表
				// 获取IP成功,查询组织名
				if (!StringUtils.isBlank(ip)) {
					JSONObject json = new JSONObject();
					if (findOrgChildId.size() > 0) {
						StringBuffer stringBuffer = new StringBuffer();
						for (String orgId : findOrgChildId) {
							stringBuffer.append(orgId).append(",");
						}
						StringBuffer resultOrganizationIds = stringBuffer
								.deleteCharAt(stringBuffer.lastIndexOf(","));
						json.put("organizationIds", resultOrganizationIds);
					} else {
						json.put("organizationIds", resultOrganizationIdsBuffer);
					}

					String httpValueByPost = InterGatewayUtil
							.getHttpValueByPost(InterGatewayConstants.U_ORGANIZATION_NAMES_BYID, json, ip, token);
					if (null == httpValueByPost) {
						// 没查到直接返回
						logger.error("[getAlarmInfoList()->invalid：组织查询失败!]");
						logger.info(MessageConstant.INFO_SERCH_SUCCESS);
						return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
					} else {
						JSONObject httpValueJson = JSON.parseObject(httpValueByPost);
						for (Map<String, Object> mapInfo : resPage) {
							if (null != mapInfo) {
								if (null != mapInfo && null != httpValueJson
										.getString("" + mapInfo.get(AlarmInfoConstants.ORG_ID))) {
									mapInfo.put(AlarmInfoConstants.ORG_NAME,
											"" + httpValueJson.getString("" + mapInfo.get(AlarmInfoConstants.ORG_ID)));
								} else {
									mapInfo.put(AlarmInfoConstants.ORG_NAME, "");
								}
							}
						}
						logger.info(MessageConstant.INFO_SERCH_SUCCESS);
						return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
					}
				} else { // 获取IP失败,直接返回
					logger.info(MessageConstant.INFO_SERCH_SUCCESS);
					return RpcResponseBuilder.buildSuccessRpcResp(MessageConstant.INFO_SERCH_SUCCESS, pages);
				}*/
				
				PageInfo<Map<String, Object>> page = new PageInfo<>(focusSecurityInfoPage);
				logger.info(String.format("[commandReceiveStates()方法执行结束!]"));
				return RpcResponseBuilder.buildSuccessRpcResp("保障信息查询成功！", page);
			} else {
				logger.error("[commandReceiveStates()方法执行结束!]");
				PageInfo<Map<String, Object>> page = new PageInfo<>(Lists.newArrayList());
				return RpcResponseBuilder.buildSuccessRpcResp("没有查询到数据！", page);
			}
			
		}catch(Exception e) {
			logger.error("commandReceiveStates()->Exception:", e);
			return RpcResponseBuilder.buildExceptionRpcResp(e);
		}
	}

}
