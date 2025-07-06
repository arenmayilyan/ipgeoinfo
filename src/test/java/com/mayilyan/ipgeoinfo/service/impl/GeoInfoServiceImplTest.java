package com.mayilyan.ipgeoinfo.service.impl;

import com.mayilyan.ipgeoinfo.exception.RateLimitExceededException;
import com.mayilyan.ipgeoinfo.model.IPInfo;
import com.mayilyan.ipgeoinfo.provider.IPInfoProvider;
import com.mayilyan.ipgeoinfo.ratelimiter.RateLimiterScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GeoInfoServiceImplTest {

    private IPInfoProvider mockProvider;
    private RateLimiterScheduler mockScheduler;
    private GeoInfoServiceImpl service;

    @BeforeEach
    void setUp() {
        mockProvider = mock(IPInfoProvider.class);
        when(mockProvider.getName()).thenReturn("FreeIPAPI");

        mockScheduler = mock(RateLimiterScheduler.class);
        service = new GeoInfoServiceImpl(List.of(mockProvider), mockScheduler);
    }

    @Test
    void shouldReturnIPInfoWhenValid() throws Exception {
        IPInfo mockInfo = new IPInfo();
        mockInfo.setIpAddress("1.1.1.1");
        mockInfo.setCityName("Calgary");

        when(mockScheduler.enqueueRequest("1.1.1.1", "FreeIPAPI"))
                .thenReturn(CompletableFuture.completedFuture(mockInfo));

        IPInfo result = service.getGeoInfo("1.1.1.1", "FreeIPAPI");

        assertNotNull(result);
        assertEquals("Calgary", result.getCityName());
    }

    @Test
    void shouldThrowWhenProviderNotFound() {
        Exception ex = assertThrows(RuntimeException.class, () ->
                service.getGeoInfo("1.1.1.1", "UnknownProvider"));

        assertTrue(ex.getMessage().contains("Provider not found"));
    }

    @Test
    void shouldThrowWhenFutureReturnsNull() throws Exception {
        when(mockScheduler.enqueueRequest("1.1.1.1", "FreeIPAPI"))
                .thenReturn(CompletableFuture.completedFuture(null));

        Exception ex = assertThrows(RuntimeException.class, () ->
                service.getGeoInfo("1.1.1.1", "FreeIPAPI"));

        assertTrue(ex.getMessage().contains("No response received"));
    }

    @Test
    void shouldThrowRateLimitExceeded() {
        when(mockScheduler.enqueueRequest("1.1.1.1", "FreeIPAPI"))
                .thenThrow(new RateLimitExceededException("Queue full"));

        assertThrows(RateLimitExceededException.class, () ->
                service.getGeoInfo("1.1.1.1", "FreeIPAPI"));
    }
}
