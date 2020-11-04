package com.run.locman.api.query.service;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.FaultOrderProcessType;

import java.util.List;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月06日
 */
public interface FaultOrderProcessTypeQueryService {

    /**
     *  @Description： 获取故障工单类型
     * @return
     */
    RpcResponse<List<FaultOrderProcessType>> getFaultOrderType();

    /**
     * @Description: 根據id查询对应工单类型信息
     * @param id
     * @return
     */
    RpcResponse<FaultOrderProcessType> findById(String id);
}
