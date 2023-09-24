package com.malsolo.springframework.batch.gettingstarted;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class JobCompletionNotificationListener implements JobExecutionListener {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            jdbcTemplate.query("SELECT first_name, last_name FROM people",
                            (rs, rowNum) -> new Person(
                                    rs.getString("first_name"),
                                    rs.getString("last_name"))
                    )
                    .forEach(person -> log.info("Found <{{}}> in the database.", person));
        }
    }
}
