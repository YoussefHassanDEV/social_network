package com.youssef.socialnetwork.config.Redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimitingProperties {
    // default values; can be overridden in application.yml
    private int loginMaxAttempts = 5;      // attempts per window
    private int loginWindowSeconds = 60;   // window size in seconds
}
