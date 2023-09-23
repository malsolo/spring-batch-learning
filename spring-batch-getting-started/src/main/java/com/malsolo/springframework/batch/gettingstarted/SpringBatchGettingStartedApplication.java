package com.malsolo.springframework.batch.gettingstarted;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBatchGettingStartedApplication {

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(SpringBatchGettingStartedApplication.class, args)));
	}

}
