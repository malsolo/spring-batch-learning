package com.malsolo.springframework.batch.batchfiledbprocessing;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Slf4j
public class KafkaConfiguration {

    public static final String VIDEO_GAME_TOPIC_NAME = "video_game";

    @Bean
    public NewTopic videoGameTopic() {
        return TopicBuilder.name(VIDEO_GAME_TOPIC_NAME)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
