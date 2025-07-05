package com.mayilyan.ipgeoinfo.service;

import com.mayilyan.ipgeoinfo.model.IPInfo;

import java.util.concurrent.ExecutionException;

public interface GeoInfoService {

    IPInfo getGeoInfo(String ip, String providerName);

}
