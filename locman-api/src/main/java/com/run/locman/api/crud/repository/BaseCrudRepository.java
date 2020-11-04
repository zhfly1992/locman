package com.run.locman.api.crud.repository;

import java.io.Serializable;

/**
 * 
 * @Description:Repository父类
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface BaseCrudRepository<T, PK extends Serializable> {
	/**
	 * 保存实体类全部属性值
	 * 
	 * @param model
	 *            实体bean
	 */
	public int insertModel(T model) throws Exception;



	/**
	 * 保存实体类部分属性值
	 * 
	 * @param model
	 *            实体bean
	 */
	public int insertModelPart(T model) throws Exception;



	/**
	 * 更新全部实体属性值
	 * 
	 * @param model
	 *            实体bean
	 */
	public int updateModel(T model) throws Exception;



	/**
	 * 更新部分实体属性值
	 * 
	 * @param model
	 *            实体bean
	 */
	public int updatePart(T model) throws Exception;



	/**
	 * 删除bean
	 * 
	 * @param id
	 *            主键id
	 */
	public int deleteById(PK id) throws Exception;
}
