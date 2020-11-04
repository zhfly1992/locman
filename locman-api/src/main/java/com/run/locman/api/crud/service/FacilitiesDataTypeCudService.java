package com.run.locman.api.crud.service;

import com.run.entity.common.RpcResponse;
import com.run.locman.api.entity.FacilitiesDataType;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年08月31日
 */
public interface FacilitiesDataTypeCudService {

    /**
     * @param facilitiesDataType 设施扩展对象
     * @return
     * @Description: 修改指定设施扩展的一个或多个参数的值
     */
     RpcResponse<FacilitiesDataType> updateFacilitiesDataType(FacilitiesDataType facilitiesDataType);

    /**
     * 新增设施扩展
     * @param facilitiesDataType
     * @return
     */
    RpcResponse<FacilitiesDataType> addFacilitiesDataType(FacilitiesDataType facilitiesDataType);
}
