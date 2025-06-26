package com.project.stationery_be_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StationeryBeServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(StationeryBeServerApplication.class, args);
	}

}
