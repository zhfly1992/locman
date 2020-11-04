package com.run.locman.constants;

/**
 * @Description:interGateway访问常量类
 * @author: guofeilong
 * @version: 1.0, 2017年11月2日
 */

public class InterGatewayConstants {
	/** 访问端口 */
	public static final String	PORT						= ":8002";
	/** IP地址 */
	public static final String	IP							= "ip";
	/** Token */
	public static final String	TOKEN						= "Token";

	/** 用户中心相关常量 */

	/** 人员id */
	public static final String	USER_ID						= "_id";
	/** 组织id */
	public static final String	ORGANIZATION_ID				= "_id";
	/** 组织名 */
	public static final String	ORGANIZATION_NAME			= "sourceName";
	/** 人员id--根据人员查询其厂家id专用 */
	public static final String	USC_IDS						= "uscIds";

	/** 组织ids 查询组织名 */
	public static final String	ORGANIZATION_IDS			= "organizationIds";

	/** 用户中心相关URL */

	/** 查询组织及其所有子组织信息 /organization/ownAndSonInfo/{organizationId} */
	public static final String	U_OWN_AND_SON_INFO			= "/interGateway/v3/organization/ownAndSonInfo/";
	/** 批量查询组织名 /organizationNames/organizationIds */
	public static final String	U_ORGANIZATION_NAMES_BYID	= "/interGateway/v3/organizationNames/organizationIds";

	/** 查询组织架构 /organizations/{accessSecret} */
	public static final String	U_ORGANIZATIONS				= "/interGateway/v3/organizations/";

	/** 根据组织id查询组织信息/organization/{organizationId} */
	public static final String	U_ORGANIZATION_INFO_BYID	= "/interGateway/v3/organization/";

	/** 根据人员id查询厂家id/user/factoryId */
	public static final String	U_FACTORY_IDS				= "/interGateway/v3/user/factoryId";
	/** 根据人员id查询人员信息 /user/info/{userId} */
	public static final String	U_PERSONINFO				= "/interGateway/v3/user/info/";
	/** 根据人员id批量查询人员信息 /user/info post请求 */
	public static final String	U_PERSONINFO_S				= "/interGateway/v3/user/info";
	
	/** 根据人员id批量查询人员信息 /user/info post请求 */
	public static final String	U_GET_USER_BY_TOKEN			= "/interGateway/v3/user/";

	/** 通过关键字模糊查询用户名返回id */
	public static final String	FIND_USER_KEYWORD			= "/interGateway/v3/user/findUserKeyword";
	/** 查询用户的名称->ids */
	public static final String	FIND_USER_NAME				= "/interGateway/v3/user/findUserName";
	/** 查询该组织id下所有的组织id */
	public static final String	FIND_ORG_ID					= "/interGateway/v3/tenmentResource/findOrgId";
	/** 查询组织信息名称 */
	public static final String	FIND_ORG_NAME				= "/interGateway/v3/tenmentResource/findOrgName";
	/** 根据接入方密钥查询接入方信息 */
	public static final String	GET_ASINFO_BY_AS			= "/interGateway/v3/accessInformation/{accessSecret}";
	
	/** 根据接入方密钥查询一级组织信息 */
	public static final String	GET_ORGINFO_BY_AS			= "/interGateway/v3/organization/users/{accessSecret}?&organizationId=&organizationName=&pageNo=1&pageSize=2000&state=valid";
	
	/** 用户登录post */
	public static final String	LOGIN						= "/interGateway/v3/user/authentication";
	/** 账号可用验证 */
	public static final String	CHECK_USER_EXIT				= "/interGateway/v3/user/emailMobExist?emailMob=";
}
