package com.mayilyan.ipgeoinfo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class IPGeoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnGeoInfoForValidIp() throws Exception {
        mockMvc.perform(get("/api/ip/136.159.0.0")
                        .param("provider", "FreeIPAPI"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ipAddress").value("136.159.0.0"));
    }

    @Test
    void shouldReturn400ForInvalidIp() throws Exception {
        mockMvc.perform(get("/api/ip/999.999.999.999")
                        .param("provider", "FreeIPAPI"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid input"));
    }

    @Test
    void shouldReturn500WhenProviderFails() throws Exception {
        mockMvc.perform(get("/api/ip/1.1.1.1")
                        .param("provider", "FailingProvider"))
                .andExpect(status().isInternalServerError());
    }
}
