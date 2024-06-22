package com.sunny.sunnyregistry.cluster;

import com.sunny.sunnyregistry.conf.SunnyRegistryConfigProperties;
import com.sunny.sunnyregistry.service.SunnyRegistryService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class Cluster {

    @Value("${server.port}")
    String port;

    String host;

    Server MYSELF;

    SunnyRegistryConfigProperties registryConfigProperties;

    public Cluster(SunnyRegistryConfigProperties registryConfigProperties) {
        this.registryConfigProperties = registryConfigProperties;
    }

    @Getter
    private List<Server> servers;

    public void init() {
        try {
            host = new InetUtils(new InetUtilsProperties())
                    .findFirstNonLoopbackHostInfo().getIpAddress();
            log.info(" ===> findFirstNonLoopbackHostInfo = " + host);
        } catch (Exception e) {
            host = "127.0.0.1";
        }

        MYSELF = new Server("http://" + host + ":" + port, true, false, -1L);
        log.info(" ===> myself = " + MYSELF);

        initServers();
        new ServerHealth(this).checkServerHealth();
    }

    private void initServers() {
        List<Server> servers = new ArrayList<>();
        for (String url : registryConfigProperties.getServerList()) {
            Server server = new Server();
            if(url.contains("localhost")) {
                url = url.replace("localhost", host);
            } else if(url.contains("127.0.0.1")) {
                url = url.replace("127.0.0.1", host);
            }
            if(url.equals(MYSELF.getUrl())) {
                servers.add(MYSELF);
            } else {
                server.setUrl(url);
                server.setStatus(false);
                server.setLeader(false);
                server.setVersion(-1L);
                servers.add(server);
            }
        } 
        // todo ...
        this.servers = servers;
    }


    public Server self() {
        MYSELF.setVersion(SunnyRegistryService.VERSION.get());
        return MYSELF;
    }

    public Server leader() {
        return this.servers.stream().filter(Server::isStatus)
                .filter(Server::isLeader).findFirst().orElse(null);
    }


}
