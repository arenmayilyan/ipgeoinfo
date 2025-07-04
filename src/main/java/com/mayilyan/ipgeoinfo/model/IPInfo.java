package com.mayilyan.ipgeoinfo.model;

import lombok.Data;

@Data
public class IPInfo {

    private String ipAddress;

    private String continent;

    private String countryName;

    private String regionName;
    private String cityName;

    private Double latitude;
    private Double longitude;
}
