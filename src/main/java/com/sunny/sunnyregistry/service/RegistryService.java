package com.sunny.sunnyregistry.service;

import com.sunny.sunnyregistry.model.service.InstanceMeta;

import java.util.List;
import java.util.Map;

/**
 * Interface of registry service
 * 注册服务的接口
 */
public interface RegistryService {

    /**
     * 注册服务实例
     * @param service 服务名
     * @param instance 实例的元数据
     * @return 实例的元数据
     */
    InstanceMeta register(String service, InstanceMeta instance);
    
    /**
     * 取消注册服务实例
     * @param service 服务名
     * @param instance 实例的元数据
     * @return 实例的元数据
     */
    InstanceMeta unregister(String service, InstanceMeta instance);
    
    /**
     * 获取指定服务的所有实例
     * @param service 服务名
     * @return 所有实例的列表
     */
    List<InstanceMeta> getAllInstances(String service);
    
    /**
     * 刷新服务实例
     *
     * @param instance 实例的元数据
     * @param service 服务名
     * @return 刷新后的xx
     */
    Long renew(InstanceMeta instance, String... service);
    
    /**
     * 获取版本号
     * @param service 服务名
     * @return 版本号
     */
    Long version(String service);
    
    /**
     * 获取版本号
     * @param services 服务名
     * @return 实例名称以及对应的版本号
     */
    Map<String, Long> versions(String... services);

}
