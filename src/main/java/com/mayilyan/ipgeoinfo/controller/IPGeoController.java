package com.mayilyan.ipgeoinfo.controller;

import com.mayilyan.ipgeoinfo.exception.InvalidIpException;
import com.mayilyan.ipgeoinfo.model.IPInfo;
import com.mayilyan.ipgeoinfo.service.GeoInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ip")
@RequiredArgsConstructor
public class IPGeoController {

    private final GeoInfoService geoInfoService;

    @Operation(
            summary = "Get geo information for a given IPv4 address",
            description = "Returns continent, country, region, city, latitude, and longitude for the given IP address."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful geolocation lookup",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IPInfo.class),
                            examples = @ExampleObject(value = """
                {
                  "ipAddress": "136.159.0.0",
                  "continentName": "Americas",
                  "countryName": "Canada",
                  "regionName": "Alberta",
                  "cityName": "Calgary",
                  "latitude": 51.075153,
                  "longitude": -114.12841
                }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid IP address format",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                  "error": "Invalid input",
                  "details": "Invalid IPv4 address format"
                }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Rate limit exceeded",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                  "error": "Too many requests",
                  "details": "Rate limit exceeded"
                }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error or no response from external API",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                  "error": "Server error",
                  "details": "No response received from API"
                }
            """)
                    )
            )
    })
    @GetMapping("/{ipAddress}")
    public ResponseEntity<?> getGeoInfo(@PathVariable String ipAddress,
                                        @RequestParam(defaultValue = "FreeIPAPI") String provider) {

            IPInfo info = geoInfoService.getGeoInfo(ipAddress, provider);
            return ResponseEntity.ok(info);
    }
}
