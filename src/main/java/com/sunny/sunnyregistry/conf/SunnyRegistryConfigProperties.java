package com.sunny.sunnyregistry.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * registry config properties.
 */

@Data
@ConfigurationProperties(prefix = "sunny-registry")
public class SunnyRegistryConfigProperties {
    private List<String> serverList;
}
