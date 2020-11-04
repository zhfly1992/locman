package com.run.locman.api.crud.repository;

import java.util.Map;

import com.run.locman.api.entity.FactoryAppTag;

/**
 * 
 * @Description:厂家apptagcrud
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface FactoryAppTagCrudRepository extends BaseCrudRepository<FactoryAppTag, String> {

	int saveBatch(Map<String, Object> addParams);



	int deleteByFactoryId(String factoryId);



	int addFactoryRsAppTag(Map<String, Object> addParams);
}