package com.malsolo.springframework.batch.batchfiledbprocessing;

import javax.sql.DataSource;

import com.malsolo.springframework.batch.batchfiledbprocessing.avro.VideoGame;
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
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.kafka.KafkaItemWriter;
import org.springframework.batch.item.kafka.builder.KafkaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import static com.malsolo.springframework.batch.batchfiledbprocessing.KafkaConfiguration.VIDEO_GAME_TOPIC_NAME;

@Configuration
@Slf4j
public class BatchDbToKafkaConfiguration {

    public static final int FIRST_YEAR = 1979;

    @Bean
    public Job dbToKafkaJob(JobRepository jobRepository, Step dbToKafkaStep) {
        return new JobBuilder("dbToKafkaJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(dbToKafkaStep)
                .build();
    }

    @Bean
    public Step dbToKafkaStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
            ItemReader<VideoGameSale> dbVideoGameSaleReader,
            ItemProcessor<VideoGameSale, VideoGame> videoGameForKafkaProcessor,
            ItemWriter<VideoGame> kafkaVideoGameWriter) {
        return new StepBuilder("dbToKafkaStep", jobRepository)
                .<VideoGameSale, VideoGame>chunk(100, transactionManager)
                .reader(dbVideoGameSaleReader)
                .processor(videoGameForKafkaProcessor)
                .writer(kafkaVideoGameWriter)
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<VideoGameSale> dbVideoGameSaleReader(DataSource dataSource, @Value("#{jobParameters['run.id']}") Long runId) {
        return new JdbcCursorItemReaderBuilder<VideoGameSale>()
                .name("dbVideoGameSaleReader")
                .dataSource(dataSource)
                .sql(String.format("select * from video_game_sales where year = '%d'", FIRST_YEAR + runId))
                //.preparedStatementSetter(yearSetter(null))
                .rowMapper(new VideoGameSaleRowMapper()) //.beanRowMapper(VideoGameSale.class)
                .build();
    }

    /*
    @Bean
    @StepScope
    public ArgumentPreparedStatementSetter yearSetter(@Value("#{jobParameters['run.id']}") Long runId) {
        return new ArgumentPreparedStatementSetter(new Object [] {FIRST_YEAR + runId});
    }
     */

    @Bean
    public ItemProcessor<VideoGameSale, VideoGame> videoGameForKafkaProcessor() {
        return videoGameSale -> VideoGame.newBuilder()
                .setRank(videoGameSale.rank())
                .setName(videoGameSale.name())
                .setPlatform(videoGameSale.platform())
                .setYear(Integer.parseInt(videoGameSale.year()))
                .setGenre(videoGameSale.genre())
                .build();
    }

    @Bean
    public KafkaItemWriter<String, VideoGame> kafkaVideoGameWriter(KafkaTemplate<String, VideoGame> kafkaTemplate) {
        kafkaTemplate.setDefaultTopic(VIDEO_GAME_TOPIC_NAME);
        return new KafkaItemWriterBuilder<String, VideoGame>()
                .kafkaTemplate(kafkaTemplate)
                .itemKeyMapper(VideoGame::getPlatform)
                .delete(false)
                .build();
    }


}
