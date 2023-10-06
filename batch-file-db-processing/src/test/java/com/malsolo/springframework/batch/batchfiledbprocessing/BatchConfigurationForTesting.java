package com.malsolo.springframework.batch.batchfiledbprocessing;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BatchConfigurationForTesting {

    @Bean
    public Job emptyJob(JobRepository jobRepository, Step emptyStep) {
        return new JobBuilder("emptyJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(emptyStep)
                .build();
    }

    @Bean
    public Step emptyStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("emptyStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("\n\n>>> EMPTY STEP: Just do nothing\n");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
