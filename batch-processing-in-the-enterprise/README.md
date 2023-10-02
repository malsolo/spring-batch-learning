# Batching for the Modern Enterprise

Follow Michael Minella's video, [Batching for the Modern Enterprise](https://www.youtube.com/watch?v=dIx81HYdpq4)

## First steps

Create the project from start.spring.io, this time with a DB (Postgresql) for looking at the Job Repository.

### DB issue:

*[Spring Boot auto-configuration](https://docs.spring.io/spring-boot/docs/3.1.4/reference/htmlsingle/#using.auto-configuration) attempts to automatically configure your Spring application based on the jar dependencies that you have added. For example, if HSQLDB is on your classpath, and you have not manually configured any database connection beans, then Spring Boot auto-configures an in-memory database.*

Since we are using Postgresql, we'll face these issues:
* The Postgresql DB is configured with the tests,  so we'll need the driver
```
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'dataSource' defined in class path resource [org/springframework/boot/autoconfigure/jdbc/DataSourceConfiguration$Hikari.class]: Failed to instantiate [com.zaxxer.hikari.HikariDataSource]: Factory method 'dataSource' threw exception with message: Failed to determine a suitable driver class
...

```
This is because the datasource is not configured (and the DB is not an in-memory one)
We'll solve this by configuring the datasource ([Mkyong's example](https://mkyong.com/spring-boot/spring-boot-spring-data-jpa-postgresql/)).

```
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: postgres
```
 
* Once we have the driver in place, we'll need a DB instance running
```
Caused by: org.postgresql.util.PSQLException: The connection attempt failed.
...
Caused by: java.net.SocketException: Network is down
```
We'll solve this by using [Docker](https://www.docker.com/blog/how-to-use-the-postgres-docker-official-image/). 
See [docker/docker-compose.yml](docker/docker-compose.yml)

```
❯ docker exec -it spring-batch-postgres psql -h localhost -U postgres 
psql (16.0 (Debian 16.0-1.pgdg120+1))
Type "help" for help.

postgres=# \d
Did not find any relations.
postgres=# 
```

Note: to [initialize a Spring Batch Database](https://docs.spring.io/spring-boot/docs/3.1.4/reference/htmlsingle/#howto.data-initialization.batch):
```
spring:
  batch:
    jdbc:
      initialize-schema: "always"
```

You can check the results:
```
postgres=# \d
                      List of relations
 Schema |             Name             |   Type   |  Owner   
--------+------------------------------+----------+----------
 public | batch_job_execution          | table    | postgres
 public | batch_job_execution_context  | table    | postgres
 public | batch_job_execution_params   | table    | postgres
 public | batch_job_execution_seq      | sequence | postgres
 public | batch_job_instance           | table    | postgres
 public | batch_job_seq                | sequence | postgres
 public | batch_step_execution         | table    | postgres
 public | batch_step_execution_context | table    | postgres
 public | batch_step_execution_seq     | sequence | postgres
(9 rows)
```

* The tests we'll need the DB up and running.
We'll solve this by configuring an in-memory DB for the tests.

See application.yml files both in main and test resources.

See also:
- https://www.baeldung.com/spring-testing-separate-data-source
- https://www.baeldung.com/spring-boot-hsqldb

## DEMOS

### Basic demo

See [HelloWorldConfiguration](src/main/java/com/malsolo/springframework/batch/processingenterprise/HelloWorldConfiguration.java)

Just run the Spring boot application (*BatchProcessingInTheEnterpriseApplication*), but before,
**start the PostgreSQL database** with docker compose:
```
.../spring-batch-learning/batch-processing-in-the-enterprise
❯ cd docker

❯ docker compose up 
```

### Multi-step demo

See [MultipleStepsConfiguration](src/main/java/com/malsolo/springframework/batch/processingenterprise/MultipleStepsConfiguration.java)

Again, **start the PostgreSQL database** with docker compose:
```
.../spring-batch-learning/batch-processing-in-the-enterprise
❯ cd docker

❯ docker compose up 
```

And run the Spring boot application.

Watchout! you can only run one job. If there are several jobs configured, as in this case, the previous one and this one,
you can either start them manually or configure which one to run.

For the time being we'll use the latter by setting:
> spring.batch.job.name

### Multi-step demo

See [TransitionsConfiguration](src/main/java/com/malsolo/springframework/batch/processingenterprise/TransitionsConfiguration.java)

Same steps as above.


### IO demo

See [FileJdbcConfiguration](src/main/java/com/malsolo/springframework/batch/processingenterprise/FileJdbcConfiguration.java)

Steps needed:
* Run Postgresql
* Ensure the target table exists
  * ```
    postgres> CREATE TABLE ITEM  (
                  first VARCHAR(50) NOT NULL,
                  second VARCHAR(50) NOT NULL,
                  phone VARCHAR(14) NOT NULL
              )
    ```
* Run the Spring boot application.
* Check the results
> select count(*) from item;
```
count
1000
```

> select * from item;
```
first,second,phone
FALLON,GUTHRIE,(693) 791-3054
ZANE,HUFF,(101) 523-6517
BELL,GILES,(224) 200-9130

```

### RESTART demo

See [BatchProcessingInTheEnterpriseApplication](src/main/java/com/malsolo/springframework/batch/processingenterprise/BatchProcessingInTheEnterpriseApplication.java)

Steps needed:
* Run Postgresql
* Ensure the target table exists
```
postgres> CREATE TABLE ITEM  (
              first VARCHAR(50) NOT NULL,
              second VARCHAR(50) NOT NULL,
              phone VARCHAR(14) NOT NULL
          );
          
commit;
```
* Run the Spring boot application as it is.
* It should fail
```
org.springframework.dao.DataIntegrityViolationException: PreparedStatementCallback; SQL [INSERT INTO ITEM VALUES (?, ?, ?)]; Batch entry 35 INSERT INTO ITEM VALUES ('THIS_IS_A_NAME_THAT_IS_WAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAY_TOO_BIG', 'GOODMAN', '(919) 506-8202') was aborted: ERROR: value too long for type character varying(50)  Call getNextException to see other errors in the batch.
	at org.springframework.jdbc.support.SQLStateSQLExceptionTranslator.doTranslate(SQLStateSQLExceptionTranslator.java:110) ~[spring-jdbc-6.0.12.jar:6.0.12]
...
Caused by: java.sql.BatchUpdateException: Batch entry 35 INSERT INTO ITEM VALUES ('THIS_IS_A_NAME_THAT_IS_WAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAY_TOO_BIG', 'GOODMAN', '(919) 506-8202') was aborted: ERROR: value too long for type character varying(50)  Call getNextException to see other errors in the batch.
	at org.postgresql.jdbc.BatchResultHandler.handleError(BatchResultHandler.java:165) ~[postgresql-42.6.0.jar:42.6.0]
...
Caused by: org.postgresql.util.PSQLException: ERROR: value too long for type character varying(50)
	at org.postgresql.core.v3.QueryExecutorImpl.receiveErrorResponse(QueryExecutorImpl.java:2713) ~[postgresql-42.6.0.jar:42.6.0]
```

* Check the results
> select count(*) from item;
```
count
500
```

> select (*) from batch_job_instance
```
1,0,csvToDatabaseRestartJob,579da183cec028818d1710ce58f20022
```

> select * from batch_job_execution
```
1,2,1,2023-10-02 14:08:14.822761,2023-10-02 14:08:14.866020,2023-10-02 14:08:15.201230,FAILED,FAILED,"org.springframework.dao.DataIntegrityViolationException: PreparedStatementCallback; SQL [INSERT INTO ITEM VALUES (?, ?, ?)]; Batch entry 35 INSERT INTO ITEM VALUES ('THIS_IS_A_NAME_THAT_IS_WAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAY_TOO_BIG', 'GOODMAN', '(919) 506-8202') was aborted: ERROR: value too long for type character varying(50)  Call getNextException to see other errors in the batch...
```

> select * from batch_job_execution_params
```
1,run.id,java.lang.Long,1,Y
1,batch.input,java.lang.String,/invalid-input.csv,Y
```

Now, fix the [CSV file](src/main/resources/invalid-input.csv) (line 536)
From:
> THIS_IS_A_NAME_THAT_IS_WAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAY_TOO_BIG,Goodman,(919) 506-8202

To:
> John,Goodman,(919) 506-8202

Uncomment the parameter at [BatchProcessingInTheEnterpriseApplication](src/main/java/com/malsolo/springframework/batch/processingenterprise/BatchProcessingInTheEnterpriseApplication.java)
(line 12):
```
	public static void main(String[] args) {
		String[] realArgs = new String [] {
				"batch.input=/invalid-input.csv,java.lang.String,true"
				, "run.id=1,java.lang.Long,true"
		};
		SpringApplication.run(BatchProcessingInTheEnterpriseApplication.class, realArgs);
	}
```
And run again. It should work:
```
2023-10-02T14:12:14.201+02:00  INFO 60042 --- [           main] o.s.b.c.l.support.SimpleJobLauncher      : Job: [SimpleJob: [name=csvToDatabaseRestartJob]] completed with the following parameters: [{'run.id':'{value=1, type=class java.lang.Long, identifying=true}','batch.input':'{value=/invalid-input.csv, type=class java.lang.String, identifying=true}'}] and the following status: [COMPLETED] in 266ms
```

Check it:
> select count(*) from item;
```
count
1000
```

> select (*) from batch_job_instance
```
1,0,csvToDatabaseRestartJob,579da183cec028818d1710ce58f20022
```

> select * from batch_job_execution
```
1,2,1,2023-10-02 14:08:14.822761,2023-10-02 14:08:14.866020,2023-10-02 14:08:15.201230,FAILED,FAILED,"org.springframework.dao.DataIntegrityViolationException: PreparedStatementCallback; SQL [INSERT INTO ITEM VALUES (?, ?, ?)]; Batch entry 35 INSERT INTO ITEM VALUES ('THIS_IS_A_NAME_THAT_IS_WAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAY_TOO_BIG', 'GOODMAN', '(919) 506-8202') was aborted: ERROR: value too long for type character varying(50)  Call getNextException to see other errors in the batch...
2,2,1,2023-10-02 14:12:13.890894,2023-10-02 14:12:13.923291,2023-10-02 14:12:14.190108,COMPLETED,COMPLETED,"",2023-10-02 14:12:14.190207
```

> select * from batch_job_execution_params
```
1,run.id,java.lang.Long,1,Y
1,batch.input,java.lang.String,/invalid-input.csv,Y
2,run.id,java.lang.Long,1,Y
2,batch.input,java.lang.String,/invalid-input.csv,Y
```
