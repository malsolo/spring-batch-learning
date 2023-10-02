package com.malsolo.springframework.batch.processingenterprise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchProcessingInTheEnterpriseApplication {

	public static void main(String[] args) {
		String[] realArgs = new String [] {
				"batch.input=/invalid-input.csv,java.lang.String,true"
				//, "run.id=1,java.lang.Long,true"
		};
		SpringApplication.run(BatchProcessingInTheEnterpriseApplication.class, realArgs);
	}

}
