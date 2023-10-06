package com.malsolo.springframework.batch.batchfiledbprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchFileDbProcessingApplication {

	public static void main(String[] args) {
		String[] realArgs = new String [] {"batch.input=/video_games_sales.csv,java.lang.String,true"};
		SpringApplication.run(BatchFileDbProcessingApplication.class, realArgs);
	}

}
