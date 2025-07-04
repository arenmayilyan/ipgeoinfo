package com.mayilyan.ipgeoinfo.service;

import com.mayilyan.ipgeoinfo.model.IPInfo;
import org.springframework.stereotype.Service;

import java.time.Duration;

public interface GeoInfoService {

    public IPInfo getGeoInfo(String ipAddress);

}
