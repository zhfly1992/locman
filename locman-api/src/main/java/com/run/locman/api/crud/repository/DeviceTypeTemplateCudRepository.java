package com.run.locman.api.crud.repository;

import com.run.locman.api.entity.DeviceTypeTemplate;

import java.util.Map;

/**
 * @Description:
 * @author: 田明
 * @version: 1.0, 2017年09月15日
 */
public interface DeviceTypeTemplateCudRepository extends BaseCrudRepository<DeviceTypeTemplate, String>  {

    /**
     *   * @Description:解绑设备属性模板与设备类型关系
     * @param params
     * @return
     * @throws Exception
     */
    int unbindDeviceTypePropertyAndTemplate(Map<String, String> params) throws Exception;

    /**
     *   * @Description:绑定设备属性模板与设备类型关系
     * @param params
     * @return
     * @throws Exception
     */
    int bindDeviceTypePropertyAndTemplate(Map<String, String> params) throws Exception;
}
