package com.mayilyan.ipgeoinfo.config;

import com.mayilyan.ipgeoinfo.provider.IPInfoProvider;
import com.mayilyan.ipgeoinfo.ratelimiter.RateLimiterScheduler;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.List;

@TestConfiguration
public class TestRateLimiterConfig {

    @Bean
    @Primary
    public RateLimiterScheduler testRateLimiterScheduler(List<IPInfoProvider> providers) {
        return new RateLimiterScheduler(providers) {
            @Override
            public void initSchedulers() {
                // Prevent background queue draining
            }
        };
    }
}
