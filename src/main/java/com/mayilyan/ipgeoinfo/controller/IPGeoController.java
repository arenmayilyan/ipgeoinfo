package com.mayilyan.ipgeoinfo.controller;

import com.mayilyan.ipgeoinfo.exception.InvalidIpException;
import com.mayilyan.ipgeoinfo.model.IPInfo;
import com.mayilyan.ipgeoinfo.service.GeoInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ip")
@RequiredArgsConstructor
public class IPGeoController {

    private final GeoInfoService geoInfoService;

    @GetMapping("/{ipAddress}")
    public ResponseEntity<?> getGeoInfo(@PathVariable
                                        String ipAddress) {
        try {
            if (!ipAddress.matches("^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$")) {
                throw new InvalidIpException("Invalid IPv4 address format");
            }
            IPInfo info = geoInfoService.getGeoInfo(ipAddress);
            return ResponseEntity.ok(info);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(429).body(e.getMessage()); // Rate limit
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Unexpected error");
        }
    }
}
