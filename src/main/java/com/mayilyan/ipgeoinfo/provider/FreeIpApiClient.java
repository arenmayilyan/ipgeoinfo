package com.mayilyan.ipgeoinfo.provider;

import com.mayilyan.ipgeoinfo.model.IPInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Component
public class FreeIpApiClient implements IPInfoProvider {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://free.freeipapi.com")
            .defaultHeader("User-Agent", "Mozilla/5.0")
            .defaultHeader("Accept", "application/json")
            .build();

    @Override
    public IPInfo getIPInfo(String ipAddress) {
        try {
            IPInfo response = webClient.get()
                    .uri("/api/json/{ip}", ipAddress)
                    .retrieve()
                    .bodyToMono(IPInfo.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("No response received from freeipapi.com");
            }

            return response;
        } catch (WebClientResponseException.BadRequest e) {
            throw new IllegalArgumentException("Invalid IP address");
        }
    }

    @Override
    public String getName() {
        return "FreeIPAPI";
    }

}
