package com.mayilyan.ipgeoinfo.ratelimiter;

import com.mayilyan.ipgeoinfo.exception.RateLimitExceededException;
import com.mayilyan.ipgeoinfo.model.IPInfo;
import com.mayilyan.ipgeoinfo.provider.IPInfoProvider;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RateLimiterSchedulerTest {

    private IPInfoProvider mockProvider;
    private RateLimiterScheduler scheduler;

    @BeforeEach
    void setUp() {
        mockProvider = mock(IPInfoProvider.class);
        when(mockProvider.getName()).thenReturn("TestProvider");

        scheduler = new RateLimiterScheduler(List.of(mockProvider)) {
            @Override
            public void initSchedulers() {
                // Disable background processing
            }
        };

        scheduler.getQueues().put("TestProvider", new LinkedBlockingQueue<>(1));
        scheduler.getProviderMap().put("TestProvider", mockProvider);
    }

    @AfterEach
    void tearDown() {
        scheduler.getQueues().get("TestProvider").clear();
    }

    @Test
    void shouldEnqueueRequestSuccessfullyWhenQueueNotFull() {
        CompletableFuture<IPInfo> future = scheduler.enqueueRequest("1.1.1.1", "TestProvider");
        assertNotNull(future);
        assertFalse(future.isDone());
    }

    @Test
    void shouldThrowRateLimitExceededExceptionWhenQueueIsFull() {
        scheduler.enqueueRequest("1.1.1.1", "TestProvider");

        assertThrows(RateLimitExceededException.class, () ->
                scheduler.enqueueRequest("1.1.1.2", "TestProvider"));
    }

    @Test
    void shouldProcessRequestInProcessNext() {
        CompletableFuture<IPInfo> future = scheduler.enqueueRequest("1.1.1.1", "TestProvider");

        IPInfo mockResponse = new IPInfo();
        mockResponse.setIpAddress("1.1.1.1");
        when(mockProvider.getIPInfo("1.1.1.1")).thenReturn(mockResponse);

        scheduler.processNext("TestProvider");

        assertTrue(future.isDone());
        assertEquals("1.1.1.1", future.join().getIpAddress());
    }
}

