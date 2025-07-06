package com.mayilyan.ipgeoinfo.service;

import com.mayilyan.ipgeoinfo.model.IPInfo;

public interface GeoInfoService {

    IPInfo getGeoInfo(String ip, String providerName);

}
