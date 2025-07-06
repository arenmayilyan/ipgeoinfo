package com.mayilyan.ipgeoinfo.service.impl;

import com.mayilyan.ipgeoinfo.exception.InvalidIpException;
import com.mayilyan.ipgeoinfo.model.IPInfo;
import com.mayilyan.ipgeoinfo.provider.IPInfoProvider;
import com.mayilyan.ipgeoinfo.ratelimiter.RateLimiterScheduler;
import com.mayilyan.ipgeoinfo.service.GeoInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Slf4j
@Service
public class GeoInfoServiceImpl implements GeoInfoService {

    private final List<IPInfoProvider> providers;
    private final RateLimiterScheduler rateLimiter;

    /**
     *
     * @param ip
     * @param providerName
     * @return geo information based on ip address and providerName
     */
    @Cacheable(value = "ipInfoCache", key = "#ip + '_' + #providerName")
    public IPInfo getGeoInfo(String ip, String providerName) {

        log.info("Looking up IP: {} using provider: {}", ip, providerName);

        boolean providerExists = providers.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(providerName));

        if (!providerExists) {
            throw new RuntimeException("Provider not found: " + providerName);
        }

        if (!ip.matches("^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$")) {
            throw new InvalidIpException("Invalid IPv4 address format");
        }

        CompletableFuture<IPInfo> future = rateLimiter.enqueueRequest(ip, providerName);
        IPInfo info = null;
        try {
            info = future.get();
            if (info == null) {
                throw new RuntimeException("No response received from " + providerName);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("No response received from " + providerName);
        }
        return info;
    }
}
