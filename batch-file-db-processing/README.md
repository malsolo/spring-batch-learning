# Spring BATCH for processing a CSV file to a DB

Mainly, follow the Josh Long's video series for coding with Spring BATCH:
* [Part 1](https://www.youtube.com/watch?v=rz1l2fpZVJQ&list=PL_HF_bzvfUwZsRO-FsjSXBU6uVOYWwGE-&index=1&pp=iAQB)
* [Part 2](https://www.youtube.com/watch?v=_ra67pu5JO4&list=PL_HF_bzvfUwZsRO-FsjSXBU6uVOYWwGE-&index=2&t=718s&pp=iAQB)
* Source code available at [GitHub](https://github.com/coffee-software-show/lets-code-spring-batch)

# Run the project

## Start the Database

**NOTE**: watchout! Don't forget to annotate the _Bean_ that needs the _jobParameters_ with _@StepScope_ (the file reader in our case) 

**start the PostgreSQL database** with [docker compose](../docker/docker-compose.yml):
```
❯ docker compose up 
...
spring-batch-postgres  | 2023-10-05 09:40:21.052 UTC [1] LOG:  database system is ready to accept connections
```
* Create the target table, see [schema.sql](src/main/resources/schema.sql).
* Run the Spring boot application.
* Check the results
> Logs
```
2023-10-05T13:19:02.997+02:00  INFO 24821 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [csvToDbStep] executed in 34s747ms
2023-10-05T13:19:03.135+02:00  INFO 24821 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=csvToDbJob]] completed with the following parameters: [{'run.id':'{value=1, type=class java.lang.Long, identifying=true}','batch.input':'{value=/video_games_sales.csv, type=class java.lang.String, identifying=true}'}] and the following status: [COMPLETED] in 35s148ms
```

Or even better
```
2023-10-05T17:54:09.376+02:00  INFO 36106 --- [           main] o.s.batch.core.step.AbstractStep         : Step: [csvToDbStep] executed in 8s98ms
2023-10-05T17:54:09.413+02:00  INFO 36106 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=csvToDbJob]] completed with the following parameters: [{'run.id':'{value=13, type=class java.lang.Long, identifying=true}','batch.input':'{value=/video_games_sales.csv, type=class java.lang.String, identifying=true}'}] and the following status: [COMPLETED] in 8s192ms
```

> select count(*) from video_game_sales;
```
count
16598
```
(Yepes, the data is not totally correct)

> SELECT * FROM video_game_sales ORDER BY rank asc LIMIT 10

