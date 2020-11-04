package com.run.locman.api.query.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.run.entity.common.Pagination;

/**
 * 
 * @Description:Repository父类
 * @author: zhaoweizhi
 * @version: 1.0, 2019年2月22日
 */
public interface BaseQueryRepository<T, PK extends Serializable> {
	/**
	 * 按唯一编号查找
	 * 
	 * @param id
	 *            主键ID
	 */
	public T findById(PK id) throws Exception;



	/**
	 * 根据实体其他条件查询
	 * 
	 * @param model
	 *            实体bean
	 */
	public List<T> findByModel(T model) throws Exception;



	/**
	 * 查找所有
	 */
	public List<T> findAll() throws Exception;



	/**
	 * 分页查询
	 * 
	 * @param T
	 * @param PageEntity
	 */
	public List<T> findListPage(T query, Pagination<T> page);



	/**
	 * 分页查询
	 * 
	 * @param Map
	 * @param PageEntity
	 */
	public List<T> findListPage(Map<Object, Object> map, Pagination<T> page);



	/**
	 * 
	 * 
	 * @Description:多表分页查询
	 * @param map
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> findListMapPage(String sqlKey, Map<Object, Object> map, Pagination<T> page);



	/**
	 * @Description:带条件不分页查询
	 * @param paramMap
	 * @return
	 */
	public List<T> findByParams(Map<String, Object> paramMap);

}
