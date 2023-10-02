package com.malsolo.springframework.batch.processingenterprise;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class RestartConfiguration {

    @Bean
    public Job csvToDatabaseRestartJob(JobRepository jobRepository, Step csvToDatabaseRestartStep) {
        return new JobBuilder("csvToDatabaseRestartJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(csvToDatabaseRestartStep)
                .build();
    }

    // page 54
    @Bean
    public Step csvToDatabaseRestartStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
            ItemReader<Item> csvItemReader,
            ItemProcessor<Item, Item> upperCaseItemProcessor,
            ItemWriter<Item> jdbcItemWriter) {
        return new StepBuilder("csvToDatabaseRestartStep", jobRepository)
                .<Item, Item>chunk(100, transactionManager)
                .reader(csvItemReader)
                .processor(upperCaseItemProcessor)
                .writer(jdbcItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Item> csvItemReader(@Value("#{jobParameters['batch.input']}") Resource resource) {
        return new FlatFileItemReaderBuilder<Item>()
                .resource(resource)
                .name("csvItemReader")
                .delimited()
                .names("first", "last", "phone")
                .targetType(Item.class)
                .build();
    }

    @Bean
    public ItemProcessor<Item, Item> upperCaseItemProcessor() {
        return item -> new Item(item.first().toUpperCase(), item.last().toUpperCase(), item.phone());
    }

    @Bean
    public JdbcBatchItemWriter<Item> jdbcItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Item>()
                .dataSource(dataSource)
                .sql("INSERT INTO ITEM VALUES (:first, :last, :phone)")
                .beanMapped()
                .build();

    }



    public record Item(String first, String last, String phone) {}

}
