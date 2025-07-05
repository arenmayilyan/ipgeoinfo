package com.mayilyan.ipgeoinfo.provider;

import com.mayilyan.ipgeoinfo.model.IPInfo;

public interface IPInfoProvider {
    IPInfo getIPInfo(String ip);
    String getName();
}
