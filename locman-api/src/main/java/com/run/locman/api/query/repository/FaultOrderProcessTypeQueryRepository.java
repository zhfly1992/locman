package com.run.locman.api.query.repository;

import com.run.locman.api.entity.FaultOrderProcessType;

import java.util.List;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月06日
 */
public interface FaultOrderProcessTypeQueryRepository extends BaseQueryRepository<FaultOrderProcessType, String> {

    /**
     * @Description： 获取故障工单类型
     * @return
     */
	List<FaultOrderProcessType> getFaultOrderTypeList();

    /**
     * @Description: 根據id查询对应工单类型信息
     * @param id
     * @return
     */
    FaultOrderProcessType findById(String id);
}
