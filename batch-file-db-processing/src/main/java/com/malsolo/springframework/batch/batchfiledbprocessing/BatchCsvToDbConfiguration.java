package com.malsolo.springframework.batch.batchfiledbprocessing;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BatchCsvToDbConfiguration {

    @Bean
    public Job csvToDbJob(JobRepository jobRepository, Step csvToDbStep) {
        return new JobBuilder("csvToDbJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(csvToDbStep)
                .build();
    }

    @Bean
    public Step csvToDbStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
            ItemReader<VideoGameSale> csvVideoGameSaleReader,
            ItemProcessor<VideoGameSale, VideoGameSale> videoGameSaleProcessor,
            ItemWriter<VideoGameSale> jdbcVideoGameSaleWriter) {
        return new StepBuilder("csvToDbStep", jobRepository)
                .<VideoGameSale, VideoGameSale>chunk(100, transactionManager)
                .reader(csvVideoGameSaleReader)
                .processor(videoGameSaleProcessor)
                .writer(jdbcVideoGameSaleWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<VideoGameSale> csvVideoGameSaleReader(@Value("#{jobParameters['batch.input']}") Resource resource) {
        return new FlatFileItemReaderBuilder<VideoGameSale>()
                .resource(resource)
                .name("csvVideoGameSaleReader")
                .delimited()
                .names("rank,name,platform,year,genre,publisher,naSales,euSales,jpSales,otherSales,globalSales".split(","))
                .linesToSkip(1)
                .targetType(VideoGameSale.class)
                .build();
    }

    @Bean
    public ItemProcessor<VideoGameSale, VideoGameSale> videoGameSaleProcessor() {
        return videoGameSale -> {
            log.info("Processing {}", videoGameSale);
            return videoGameSale;
        };
    }

    @Bean
    public JdbcBatchItemWriter<VideoGameSale> jdbcVideoGameSaleWriter(DataSource dataSource) {
        var sql = """
                insert into video_game_sales(
                                    rank          ,
                                    name          ,
                                    platform      ,
                                    year          ,
                                    genre         ,
                                    publisher     ,
                                    na_sales      ,
                                    eu_sales      ,
                                    jp_sales      ,
                                    other_sales   ,
                                    global_sales
                                )
                                 values (
                                    :rank,
                                    :name,
                                    :platform,
                                    :year,
                                    :genre,
                                    :publisher,
                                    :naSales,
                                    :euSales,
                                    :jpSales,
                                    :otherSales,
                                    :globalSales
                                 )
                """;
        return new JdbcBatchItemWriterBuilder<VideoGameSale>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }

}
