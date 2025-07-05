package com.mayilyan.ipgeoinfo.ratelimiter;

import com.mayilyan.ipgeoinfo.model.IPInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.CompletableFuture;

@Data
@AllArgsConstructor
public class RateLimitedRequest {
    private String ip;
    private CompletableFuture<IPInfo> future;
}
