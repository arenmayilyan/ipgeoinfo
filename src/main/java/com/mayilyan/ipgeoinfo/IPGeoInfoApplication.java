package com.mayilyan.ipgeoinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class IPGeoInfoApplication {

	public static void main(String[] args) {
		SpringApplication.run(IPGeoInfoApplication.class, args);
	}

}
