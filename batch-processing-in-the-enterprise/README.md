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

* The tests we'll need the DB up and running.
We'll solve this by configuring an in-memory DB for the tests.
**_TODO_**

