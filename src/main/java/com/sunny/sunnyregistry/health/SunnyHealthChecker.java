package com.sunny.sunnyregistry.health;

import com.sunny.sunnyregistry.model.service.InstanceMeta;
import com.sunny.sunnyregistry.service.RegistryService;
import com.sunny.sunnyregistry.service.SunnyRegistryService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * default implementation of HealthChecker
 */
@Slf4j
public class SunnyHealthChecker implements HealthChecker {

    RegistryService registryService;

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    long timeout = 20_000;
    
    public SunnyHealthChecker(RegistryService registryService) {
        this.registryService = registryService;
    }
    @Override
    public void start() {
        executor.scheduleWithFixedDelay(
                () -> {
                    log.info(" ===> Health checker running...");
                    long now = System.currentTimeMillis();
                    SunnyRegistryService.TIMESTAMPS.keySet().forEach(serviceAndInst -> {
                        long timestamp = SunnyRegistryService.TIMESTAMPS.get(serviceAndInst);
                        if (now - timestamp > timeout) {
                            log.info(" ===> Health checker: {} is down", serviceAndInst);
                            int index = serviceAndInst.indexOf("@");
                            String service = serviceAndInst.substring(0, index);
                            String url = serviceAndInst.substring(index + 1);
                            InstanceMeta instance = InstanceMeta.from(url);
                            registryService.unregister(service, instance);
                            SunnyRegistryService.TIMESTAMPS.remove(serviceAndInst);
                        }
                    });

                },
                10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        executor.shutdown();
    }
}
