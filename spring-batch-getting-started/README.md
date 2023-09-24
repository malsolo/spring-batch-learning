# Getting started with Spring BATCH

Following the guide for [Creating a Batch Service](https://spring.io/guides/gs/batch-processing/).

**Note:** as stated in the [Spring Batch - Reference Documentation](https://docs.spring.io/spring-batch/docs/current/reference/html/index-single.html)
there is a [new configuration class for infrastructure beans](https://docs.spring.io/spring-batch/docs/current/reference/html/index-single.html#new-configuration-class),
an alternative to using *@EnableBatchProcessing* (for which there are [new annotation attributes in EnableBatchProcessing](https://docs.spring.io/spring-batch/docs/current/reference/html/index-single.html#new-attributes-enable-batch-processing), 
if you still want to take advantage of it)

With the current versions, Spring Batch 5.0.3 and Spring Boot 3.1.4, there's no need of using any of these alternatives, neither 
*@EnableBatchProcessing* nor *DefaultBatchConfiguration*, because everything is done for you by the [BatchAutoConfiguration](https://github.com/spring-projects/spring-boot/blob/845c4dd057e65f0a6b3d9c895b2f531d61ddc8eb/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/batch/BatchAutoConfiguration.java#L113)
in the inner class [SpringBootBatchConfiguration](https://github.com/spring-projects/spring-boot/blob/845c4dd057e65f0a6b3d9c895b2f531d61ddc8eb/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/batch/BatchAutoConfiguration.java#L113):

```
package org.springframework.boot.autoconfigure.batch;
...

@AutoConfiguration(after = { HibernateJpaAutoConfiguration.class, TransactionAutoConfiguration.class })
@ConditionalOnClass({ JobLauncher.class, DataSource.class, DatabasePopulator.class })
@ConditionalOnBean({ DataSource.class, PlatformTransactionManager.class })
@ConditionalOnMissingBean(value = DefaultBatchConfiguration.class, annotation = EnableBatchProcessing.class)
@EnableConfigurationProperties(BatchProperties.class)
@Import(DatabaseInitializationDependencyConfigurer.class)
public class BatchAutoConfiguration {
...

	@Configuration(proxyBeanMethods = false)
	static class SpringBootBatchConfiguration extends DefaultBatchConfiguration {

		private final DataSource dataSource;

		private final PlatformTransactionManager transactionManager;

		private final BatchProperties properties;

		private final List<BatchConversionServiceCustomizer> batchConversionServiceCustomizers;
```

That's why you won't find these classes in our source code.
