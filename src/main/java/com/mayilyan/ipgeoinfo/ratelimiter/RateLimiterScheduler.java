package com.mayilyan.ipgeoinfo.ratelimiter;

import com.mayilyan.ipgeoinfo.exception.RateLimitExceededException;
import com.mayilyan.ipgeoinfo.model.IPInfo;
import com.mayilyan.ipgeoinfo.provider.IPInfoProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Component
public class RateLimiterScheduler {

    private final Map<String, BlockingQueue<RateLimitedRequest>> queues = new HashMap<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final Map<String, IPInfoProvider> providerMap;

    Map<String, BlockingQueue<RateLimitedRequest>> getQueues() {
        return this.queues;
    }

    Map<String, IPInfoProvider> getProviderMap() {
        return this.providerMap;
    }

    public RateLimiterScheduler(List<IPInfoProvider> providers) {
        providerMap = new HashMap<>();
        for (IPInfoProvider p : providers) {
            providerMap.put(p.getName(), p);
            queues.put(p.getName(), new LinkedBlockingQueue<>());
        }
    }

    @PostConstruct
    public void initSchedulers() {
        for (String provider : queues.keySet()) {
            executor.scheduleAtFixedRate(() -> processNext(provider), 0, 1, TimeUnit.SECONDS);
        }
    }

    void processNext(String providerName) {
        try {
            BlockingQueue<RateLimitedRequest> queue = queues.get(providerName);
            RateLimitedRequest task = queue.poll();
            if (task != null) {
                log.info("Processing rate-limited request for {} - IP: {}", providerName, task.getIp());
                IPInfoProvider provider = providerMap.get(providerName);
                IPInfo info = provider.getIPInfo(task.getIp());
                task.getFuture().complete(info);
            }
        } catch (Exception e) {
            log.error("Error processing request for provider " + providerName, e);
        }
    }

    public CompletableFuture<IPInfo> enqueueRequest(String ip, String providerName) {
        CompletableFuture<IPInfo> future = new CompletableFuture<>();
        RateLimitedRequest request = new RateLimitedRequest(ip, future);
        boolean offered = queues.get(providerName).offer(request);

        if (!offered) {
            throw new RateLimitExceededException("Rate limit exceeded — request queue is full");
        }
        return future;
    }
}
