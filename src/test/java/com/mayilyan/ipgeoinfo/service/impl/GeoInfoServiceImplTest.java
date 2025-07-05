package com.mayilyan.ipgeoinfo.service.impl;

import com.mayilyan.ipgeoinfo.exception.RateLimitExceededException;
import com.mayilyan.ipgeoinfo.model.IPInfo;
import com.mayilyan.ipgeoinfo.provider.IPInfoProvider;
import com.mayilyan.ipgeoinfo.ratelimiter.RateLimiterScheduler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CompletableFuture;

class GeoInfoServiceImplTest {

    private IPInfoProvider mockProvider;
    private RateLimiterScheduler mockScheduler;
    private GeoInfoServiceImpl service;

    @BeforeEach
    void setup() {
        mockProvider = Mockito.mock(IPInfoProvider.class);
        Mockito.when(mockProvider.getName()).thenReturn("FreeIPAPI");

        mockScheduler = Mockito.mock(RateLimiterScheduler.class);
        service = new GeoInfoServiceImpl(List.of(mockProvider), mockScheduler);
    }

    @Test
    void shouldReturnIPInfoWhenValid() throws Exception {
        IPInfo expected = new IPInfo();
        expected.setIpAddress("1.1.1.1");
        expected.setCityName("TestCity");

        Mockito.when(mockScheduler.enqueueRequest("1.1.1.1", "FreeIPAPI"))
                .thenReturn(CompletableFuture.completedFuture(expected));

        IPInfo result = service.getGeoInfo("1.1.1.1", "FreeIPAPI");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("TestCity", result.getCityName());
    }

    @Test
    void shouldThrowWhenProviderNotFound() {
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () ->
                service.getGeoInfo("1.1.1.1", "UnknownProvider"));

        Assertions.assertTrue(ex.getMessage().contains("Provider not found"));
    }

    @Test
    void shouldThrowWhenRateLimiterReturnsNull() throws Exception {
        Mockito.when(mockScheduler.enqueueRequest("1.1.1.1", "FreeIPAPI"))
                .thenReturn(CompletableFuture.completedFuture(null));

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () ->
                service.getGeoInfo("1.1.1.1", "FreeIPAPI"));

        Assertions.assertTrue(ex.getMessage().contains("No response"));
    }

    @Test
    void shouldThrowRateLimitExceededException() {
        Mockito.when(mockScheduler.enqueueRequest("1.1.1.1", "FreeIPAPI"))
                .thenThrow(new RateLimitExceededException("Rate limit exceeded"));

        Assertions.assertThrows(RateLimitExceededException.class, () ->
                service.getGeoInfo("1.1.1.1", "FreeIPAPI"));
    }
}
