package com.youssef.socialnetwork.config;

import com.youssef.socialnetwork.config.Redis.RateLimitingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RateLimitingProperties.class)
public class FeatureFlagsConfig { }
