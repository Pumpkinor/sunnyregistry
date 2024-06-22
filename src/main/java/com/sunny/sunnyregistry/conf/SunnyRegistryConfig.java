package com.sunny.sunnyregistry.conf;

import com.sunny.sunnyregistry.cluster.Cluster;
import com.sunny.sunnyregistry.health.HealthChecker;
import com.sunny.sunnyregistry.health.SunnyHealthChecker;
import com.sunny.sunnyregistry.service.RegistryService;
import com.sunny.sunnyregistry.service.SunnyRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SunnyRegistryConfig {

    @Bean
    public RegistryService registryService()
    {
        return new SunnyRegistryService();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public HealthChecker healthChecker(@Autowired RegistryService registryService) {
        return new SunnyHealthChecker(registryService);
    }

    @Bean(initMethod = "init")
    public Cluster cluster(@Autowired SunnyRegistryConfigProperties registryConfigProperties) {
        return new Cluster(registryConfigProperties);
    }

}
