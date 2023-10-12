package com.malsolo.springframework.batch.batchfiledbprocessing.avro;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VideoGameTest {

    final Logger logger = LoggerFactory.getLogger(VideoGameTest.class);

    @Test
    public void testCreationSerializationAndDeserializationVideoGame() throws IOException {
        var videoGame = VideoGame.newBuilder()
                .setRank(1)
                .setName("Video Game name")
                .setPlatform("PS5")
                .setYear(2023)
                .setGenre("RPG")
                .build();

        logger.info("Video game created: \n{}", videoGame);

        var fileName = "target/users.avro";
        var path = Paths.get(fileName);

        if (Files.exists(path)) {
            Files.delete(path);
        }

        var videoGameDatumWriter = new SpecificDatumWriter<>(VideoGame.class);
        var videoGameDataFileWriter = new DataFileWriter<>(videoGameDatumWriter);
        videoGameDataFileWriter.create(videoGame.getSchema(), path.toFile());
        videoGameDataFileWriter.append(videoGame);
        videoGameDataFileWriter.close();

        Assertions.assertTrue(Files.exists(path));

        var videoGameDatumReader = new SpecificDatumReader<>(VideoGame.class);
        var videoGameDataFileReader = new DataFileReader<>(path.toFile(), videoGameDatumReader);

        VideoGame videoGameRead = null;
        while (videoGameDataFileReader.hasNext()) {
            // Reuse user object by passing it to next(). This saves us from
            // allocating and garbage collecting many objects for files with
            // many items.
            videoGameRead = videoGameDataFileReader.next(videoGameRead);
            logger.info("Video game read: \n{}", videoGameRead);
        }

        Assertions.assertEquals(videoGame, videoGameRead);
    }

}
