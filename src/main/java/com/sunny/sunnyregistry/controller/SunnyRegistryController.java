
package com.sunny.sunnyregistry.controller;

import com.sunny.sunnyregistry.cluster.Cluster;
import com.sunny.sunnyregistry.cluster.Server;
import com.sunny.sunnyregistry.cluster.Snapshot;
import com.sunny.sunnyregistry.model.InstanceMeta;
import com.sunny.sunnyregistry.service.RegistryService;
import com.sunny.sunnyregistry.service.SunnyRegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class SunnyRegistryController {

    RegistryService registryService;

    Cluster cluster;
    
    public SunnyRegistryController(final RegistryService registryService, final Cluster cluster) {
        this.registryService = registryService;
        this.cluster = cluster;
    }
    
    @RequestMapping("/reg")
    public InstanceMeta register(@RequestParam String service, @RequestBody InstanceMeta instance)
    {
        log.info(" ===> register {} @ {}", service, instance);
        checkLeader();
        return registryService.register(service, instance);
    }

    private void checkLeader() {
        if(!cluster.self().isLeader()) {
            throw new RuntimeException("current server is not a leader, the leader is " + cluster.leader().getUrl());
        }
    }

    @RequestMapping("/unreg")
    public InstanceMeta unregister(@RequestParam String service, @RequestBody InstanceMeta instance)
    {
        log.info(" ===> unregister {} @ {}", service, instance);
        checkLeader();
        return registryService.unregister(service, instance);
    }


    @RequestMapping("/findAll")
    public List<InstanceMeta> findAllInstances(@RequestParam String service)
    {
        log.info(" ===> findAllInstances {}", service);
        return registryService.getAllInstances(service);
    }

    @RequestMapping("/renew")
//    保活的接口
    public long renew(@RequestParam String service, @RequestBody InstanceMeta instance)
    {
        log.info(" ===> renew {} @ {}", service, instance);
        checkLeader();
        return registryService.renew(instance, service);
    }

    @RequestMapping("/renews")
    public long renews(@RequestParam String services, @RequestBody InstanceMeta instance)
    {
        log.info(" ===> renew {} @ {}", services, instance);
        checkLeader();
        return registryService.renew(instance, services.split(","));
    }

    @RequestMapping("/version")
    public long version(@RequestParam String service)
    {
        log.info(" ===> version {}", service);
        return registryService.version(service);
    }

    @RequestMapping("/versions")
    public Map<String, Long> versions(@RequestParam String services)
    {
        log.info(" ===> versions {}", services);
        return registryService.versions(services.split(","));
    }

    @RequestMapping("/info")
    public Server info()
    {
        log.info(" ===> info: {}", cluster.self());
        return cluster.self();
    }

    @RequestMapping("/cluster")
    public List<Server> cluster()
    {
        log.info(" ===> info: {}", cluster.getServers());
        return cluster.getServers();
    }

    @RequestMapping("/leader")
    public Server leader()
    {
        log.info(" ===> leader: {}", cluster.leader());
        return cluster.leader();
    }

    @RequestMapping("/sl")
    public Server sl()
    {
        cluster.self().setLeader(true);
        log.info(" ===> leader: {}", cluster.self());
        return cluster.self();
    }

    @RequestMapping("/snapshot")
    public Snapshot snapshot() {
        return SunnyRegistryService.snapshot();
    }


}
