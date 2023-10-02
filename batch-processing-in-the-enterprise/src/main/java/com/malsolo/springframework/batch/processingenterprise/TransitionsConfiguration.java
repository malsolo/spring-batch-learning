package com.malsolo.springframework.batch.processingenterprise;

import java.util.Random;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransitionsConfiguration {

    @Bean
    public Job transitionsJob(JobRepository jobRepository, Step start, Step failure, Step success) {
        return new JobBuilder("transitionsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(start)
                .on("FAILED").to(failure)
                .from(start).on("*").to(success)
                .end()
                .build();
    }

    @Bean
    public Step start(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("start", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (new Random().nextInt(10) < 5) {
                        System.out.println("STARTED!");
                        return RepeatStatus.FINISHED;
                    }
                    else {
                        System.err.println("COULD NOT START!");
                        throw new RuntimeException("Something went wrong");
                    }
                }, transactionManager)
                .build();
    }

    @Bean
    public Step failure(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("failure", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("FAILED!");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step success(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("success", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("SUCCESS!");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
