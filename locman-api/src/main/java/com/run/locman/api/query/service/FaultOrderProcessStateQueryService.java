package com.run.locman.api.query.service;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.FaultOrderProcessState;

import java.util.List;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年12月07日
 */
public interface FaultOrderProcessStateQueryService {

    /**
     *  @Description： 获取故障工单流程状态
     * @return
     */
    RpcResponse<List<FaultOrderProcessState>> getFaultOrderStateList();


    /**
     * @Description: 根據sign查询对应工单状态信息
     * @param sign
     * @return
     */
    RpcResponse<FaultOrderProcessState> findBySign(String sign);

}
