package com.sunny.sunnyregistry;

import com.sunny.sunnyregistry.conf.SunnyRegistryConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({SunnyRegistryConfigProperties.class})
public class SunnyregistryApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SunnyregistryApplication.class, args);
    }
    
}
