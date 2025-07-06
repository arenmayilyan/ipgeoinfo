package com.mayilyan.ipgeoinfo.controller;

import com.mayilyan.ipgeoinfo.config.TestRateLimiterConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestRateLimiterConfig.class)
class IPGeoRateLimitIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn429WhenRateLimitExceeded() throws Exception {
        // Run the first request in a separate thread so it doesn't block test execution
        new Thread(() -> {
            try {
                mockMvc.perform(get("/api/ip/1.1.1.5")
                                .param("provider", "FreeIPAPI"))
                        .andReturn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Small delay to ensure the queue is filled
        Thread.sleep(100);

        // Now run the second request — it should immediately return 429
        mockMvc.perform(get("/api/ip/1.1.1.6")
                        .param("provider", "FreeIPAPI"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error").value("Too many requests"));
    }
}