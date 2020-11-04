package com.run.locman.api.query.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONObject;
import com.run.locman.api.dto.AppTagDto;
import com.run.locman.api.entity.Factory;

/**
 * 
 * @Description:查询厂家
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface FactoryQueryRepository extends BaseQueryRepository<Factory, String> {

	List<Map<String, Object>> findByAccessSecret(Map<String, Object> queryParams);



	List<Map<String, Object>> findByAccessSecretNew(Map<String, Object> queryParams);



	int checkAppTag(String appTag);



	int upgateCheckAppTag(JSONObject jsonParam);



	int checkAppTagExist(AppTagDto appTagDto);



	List<String> queryAppTagForAccessSecret(String accessSecret);



	List<String> findAppTagForAccessSecret(String accessSecret);



	Map<String, Object> findMapById(String id);



	List<Map<String, String>> findAppKeyAppId(String id);



	List<String> queryFactoryByAppTag(String appTag);



	/**
	 * 
	 * @Description:查询appTage完整信息
	 * @param appTag
	 * @return
	 */

	List<Map<String, Object>> queryFactoryInfoByAppTag(String appTag);



	/**
	 * 
	 * @Description:查询全部与厂家绑定的appTag
	 * @return
	 */
	List<Map<String, Object>> queryAllappTag();



	/**
	 * 
	 * @Description:根据设备id查询接入方秘钥
	 * @param deviceId
	 * @return
	 */
	String queryAccessSecretByDeviceId(String deviceId);



	/**
	 * 
	 * @Description: deviceId 设备id
	 * @param
	 * @return
	 */

	Factory findFactoryByDeviceId(Map<String, Object> map);



	/**
	 * 
	 * @Description: 查询接入方下厂家id和厂家名称
	 * @param
	 * @return
	 */
	List<Map<String, String>> getFactoryNameList(String accessSecret);



	public List<AppTagDto> getAppTagByFactoryId(String factoryId);



	/**
	 * 
	 * @Description:通过接入方密钥加载厂家（启用）
	 * @param accessSecret
	 * @return
	 */
	List<Map<String, String>> downBoxFactory(String accessSecret);



	/**
	 * 
	 * @Description: 校验接入方下是否存在网关(单个接入方网关不能重复)
	 * @param
	 * @return
	 */

	List<String> checkGatewayIdExist(@Param("gatewayId") List<String> gatewayIdList,
			@Param("accessSecret") String accessSecret, @Param("appTag") String appTag);
}