package com.sunny.sunnyregistry.service;

import com.sunny.sunnyregistry.model.cluster.Snapshot;
import com.sunny.sunnyregistry.model.service.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * default registry service
 */
@Slf4j
public class SunnyRegistryService implements RegistryService {
    
    /**
     * 存储服务以及对应的实例
     */
    final static MultiValueMap<String, InstanceMeta> REGISTRY = new LinkedMultiValueMap<>();
    /**
     * 数据版本号 - 全局来做一个递增 全局递增序列 
     * 这里的版本号不是服务的版本号 而是当前注册中心的实例节点的数据版本号 
     * 也就是说 该服务对应的实例的注册信息发生改变 这个版本号也要跟着发生改变
     * 用于在注册中心集群中表明当前实例的数据是否最新
     */
    public final static AtomicLong VERSION = new AtomicLong(0);
    /**
     * 服务对应的实例数据的版本号
     */
    final static Map<String, Long> VERSIONS = new ConcurrentHashMap<>();
    /**
     * 服务版本号对应的时间戳
     */
    public final static Map<String, Long> TIMESTAMPS = new ConcurrentHashMap<>();
    
    @Override
    public synchronized InstanceMeta register(String service, InstanceMeta instance) {
        List<InstanceMeta> metas = REGISTRY.get(service);
        if(metas != null && !metas.isEmpty()) {
            if(metas.contains(instance)) {
                log.info(" ====> instance {} already registered", instance.toUrl());
//                这里应该是找到list里的那个instance对象然后修改它的状态 并返回当前服务存储的实例对象
//                instance.setStatus(true);
                Optional<InstanceMeta> meta = metas.stream().filter(m -> m.equals(instance)).findFirst();
                meta.ifPresent(m -> m.setStatus(true));
                return meta.orElseThrow();
            }
        }
        log.info(" ====> register instance {}", instance.toUrl());
        REGISTRY.add(service, instance);
        instance.setStatus(true);
        renew(instance, service);
//        每次注册instance都版本 service的实例信息数据版本都要更新
        VERSIONS.put(service, VERSION.incrementAndGet());
        return instance;
    }

    @Override
    public synchronized InstanceMeta unregister(String service, InstanceMeta instance) {
        List<InstanceMeta> metas = REGISTRY.get(service);
        if(metas == null || metas.isEmpty()) {
            return null;
        }
        log.info(" ====> unregister instance {}", instance.toUrl());
        metas.removeIf( m -> m.equals(instance));
        instance.setStatus(false);
        renew(instance, service);
        VERSIONS.put(service, VERSION.incrementAndGet());
        return instance;
    }

    @Override
    public List<InstanceMeta> getAllInstances(String service) {
        return REGISTRY.get(service);
    }
    
    @Override
    public synchronized Long renew(InstanceMeta instance, String... services) {
        long now = System.currentTimeMillis();
        for (String service : services) {
            TIMESTAMPS.put(service+"@"+instance.toUrl(), now);
        }
        return now;
    }
    
    @Override
    public Long version(String service) {
        return VERSIONS.get(service);
    }
    
    @Override
    public Map<String, Long> versions(String... services) {
        return Arrays.stream(services)
                .collect(Collectors.toMap(x->x, VERSIONS::get, (a, b)->b));
    };
    
    public static synchronized Snapshot snapshot() {
        LinkedMultiValueMap<String, InstanceMeta> registry = new LinkedMultiValueMap<>();
        registry.addAll(REGISTRY);
        Map<String, Long> versions = new HashMap<>(VERSIONS);
        Map<String, Long> timestamps = new HashMap<>(TIMESTAMPS);
        return new Snapshot(registry, versions, timestamps, VERSION.get());
    }

    public static synchronized long restore(Snapshot snapshot) {
        REGISTRY.clear();
        REGISTRY.addAll(snapshot.getREGISTRY());
        VERSIONS.clear();
        VERSIONS.putAll(snapshot.getVERSIONS());
        TIMESTAMPS.clear();
        TIMESTAMPS.putAll(snapshot.getTIMESTAMPS());
        VERSION.set(snapshot.getVersion());
        return snapshot.getVersion();
    }


}
