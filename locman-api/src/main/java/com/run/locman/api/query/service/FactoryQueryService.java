package com.run.locman.api.query.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.github.pagehelper.PageInfo;
import com.run.entity.common.RpcResponse;
import com.run.locman.api.dto.AppTagDto;
import com.run.locman.api.entity.Factory;

/**
 * 
 * @Description:厂家查询类
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface FactoryQueryService {

	/**
	 * 
	 * @Description:分页查询新厂家列表
	 * @param accessSecret
	 *            密钥
	 * @param pageNum
	 *            页数
	 * @param pageSize
	 *            页大小
	 * @param searchKey
	 *            查询关键字:厂家名
	 * @param manageState
	 *            启用/停用
	 * @return
	 */
	RpcResponse<PageInfo<Map<String, Object>>> getNewFactoryList(String accessSecret, int pageNum, int pageSize,
			String searchKey, String manageState);



	/**
	 * 
	 * @Description:验证apptag唯一性,用于对接新的iot
	 * @param AppTagDto:属性:此方法factoryId设置为id使用,设置appId,appKey参数可以得到appTag,
	 *            checkGatewayExist用于多个appTag循环查询网关时校验参数绑定的网关是否有重复 token 接口验证
	 * @return
	 */
	RpcResponse<Boolean> checkAppTagExist(AppTagDto appTagDto, String accessSecret, Set<String> checkGatewayExist,
			String token);



	/**
	 * 根据id查询厂家信息
	 *
	 * @param id
	 * @return
	 * @Description:
	 */
	RpcResponse<Map<String, Object>> findById(String id);



	/**
	 * 
	 * @Description: 根据id查询厂家(新)
	 * @param id
	 *            厂家id
	 * @return
	 */
	RpcResponse<Map<String, Object>> findFactoryById(String id);



	/**
	 * @param accessSecret
	 *            接入方秘钥
	 * @return appTag
	 * @Description ；根据接入方秘钥查询appTag(只需要将启用厂家查出来)
	 */
	RpcResponse<List<String>> queryAppTagForAccessSecret(String accessSecret);



	/**
	 * @param accessSecret
	 *            接入方秘钥
	 * @return appTag
	 * @Description ；根据接入方秘钥查询appTag（启用/停用的厂家都需要查出来）
	 */
	RpcResponse<List<String>> findAppTagForAccessSecret(String accessSecret);



	/**
	 * 
	 * @Description:根据apptag数组查询厂家名
	 * @param appTags
	 * @return
	 */
	RpcResponse<List<String>> queryFactoryByAppTag(String appTag);



	/**
	 * 
	 * @Description:查询所有AppTage信息
	 * @param appTag
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> queryFactoryInfoByAppTag(String appTag);



	/**
	 * 
	 * @Description:查询全部与厂家绑定的appTag
	 * @return
	 */
	RpcResponse<List<Map<String, Object>>> queryAllappTag();



	/**
	 * 
	 * @Description:根据设备id查询接入方秘钥
	 * @param deviceId
	 * @return
	 */
	RpcResponse<String> queryAccessSecretByDeviceId(String deviceId);



	/**
	 * 根据设备id查询厂家信息
	 *
	 * @param deviceId
	 *            设备id
	 * @return
	 * @Description:
	 */
	RpcResponse<Factory> findFactoryByDeviceId(String deviceId, String accessSecret);



	/**
	 * 根据 accessSecret查询所有厂家名字
	 *
	 * @param deviceId
	 *            设备id
	 * @return
	 * @Description:
	 */
	RpcResponse<List<Map<String, String>>> queryFactoryNameList(String accessSecret);



	/**
	 * 
	 * @Description: 根据厂家id查询厂家绑定的所有appTag
	 * @param
	 * @return
	 */
	RpcResponse<List<AppTagDto>> getAppTagByFactoryId(String factoryId);



	/**
	 * 
	 * @Description:根据接入方密钥加载启用的厂家
	 * @param accessSecret
	 * @return
	 */
	RpcResponse<List<Map<String, String>>> downBoxFactory(String accessSecret);
}
