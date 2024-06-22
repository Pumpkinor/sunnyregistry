package com.sunny.sunnyregistry.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * registry config properties.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/4/16 20:24
 */

@Data
@ConfigurationProperties(prefix = "sunnyregistry")
public class SunnyRegistryConfigProperties {
    private List<String> serverList;

}
