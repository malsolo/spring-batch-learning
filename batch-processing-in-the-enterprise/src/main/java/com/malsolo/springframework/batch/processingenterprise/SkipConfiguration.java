package com.malsolo.springframework.batch.processingenterprise;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SkipConfiguration {

    @Bean
    public Job csvToDatabaseSkipJob(JobRepository jobRepository, Step csvToDatabaseSkipStep) {
        return new JobBuilder("csvToDatabaseSkipJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(csvToDatabaseSkipStep)
                .build();
    }

    /**
     * Note: we'll use the reader, processor, and writer from @link RestartConfiguration
     */
    @Bean
    public Step csvToDatabaseSkipStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
            ItemReader<RestartConfiguration.Item> csvItemReader,
            ItemProcessor<RestartConfiguration.Item, RestartConfiguration.Item> upperCaseItemProcessor,
            ItemWriter<RestartConfiguration.Item> jdbcItemWriter) {
        return new StepBuilder("csvToDatabaseSkipStep", jobRepository)
                .<RestartConfiguration.Item, RestartConfiguration.Item>chunk(100, transactionManager)
                .reader(csvItemReader)
                .processor(upperCaseItemProcessor)
                .writer(jdbcItemWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(5)
                .build();
    }
}
