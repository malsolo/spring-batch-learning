package com.malsolo.springframework.batch.processingenterprise;

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
public class MultipleStepsConfiguration {

    @Bean
    public Job multipleStepJob(JobRepository jobRepository, Step step1, Step step2, Step step3) {
        return new JobBuilder("multiStepJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .next(step2)
                .next(step3)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return stepi(1, jobRepository, transactionManager);
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return stepi(2, jobRepository, transactionManager);
    }

    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return stepi(3, jobRepository, transactionManager);
    }

    private Step stepi(int i, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder(String.format("step%d", i), jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.printf("Step%d was executed!\n", i);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
