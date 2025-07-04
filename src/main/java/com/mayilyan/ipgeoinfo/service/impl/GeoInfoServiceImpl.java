package com.mayilyan.ipgeoinfo.service.impl;

import com.mayilyan.ipgeoinfo.client.FreeIpApiClient;
import com.mayilyan.ipgeoinfo.exception.RateLimitExceededException;
import com.mayilyan.ipgeoinfo.model.IPInfo;
import com.mayilyan.ipgeoinfo.service.GeoInfoService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
@Service
public class GeoInfoServiceImpl implements GeoInfoService {

    private final FreeIpApiClient freeIpApiClient;

    private final Bucket bucket = Bucket.builder()
            .addLimit(Bandwidth.classic(1, Refill.intervally(1, Duration.ofSeconds(1))))
            .build();


    @Cacheable(value = "ipInfoCache", key = "#ipAddress")
    public IPInfo getGeoInfo(String ipAddress) {

        log.info("Looking up IP: {}", ipAddress);

        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }

        IPInfo info = freeIpApiClient.getIPInfo(ipAddress);

        if (info == null) {
            throw new RuntimeException("No response received from FreeIPAPI");
        }

        return info;
    }
}
