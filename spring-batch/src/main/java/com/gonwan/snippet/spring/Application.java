package com.gonwan.snippet.spring;

import java.lang.reflect.Method;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gonwan.snippet.spring.model.PBondListInfo;

import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

/* For SQL tracing */
class DatasourceProxyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof DataSource) {
            ProxyFactory proxyFactory = new ProxyFactory(bean);
            proxyFactory.setProxyTargetClass(true);
            proxyFactory.addAdvice(new ProxyDataSourceInterceptor((DataSource) bean));
            return proxyFactory.getProxy();
        }
        return bean;
    }

    private static class ProxyDataSourceInterceptor implements MethodInterceptor {

        private final DataSource dataSource;

        public ProxyDataSourceInterceptor(DataSource dataSource) {
            this.dataSource = ProxyDataSourceBuilder.create(dataSource).logQueryBySlf4j(SLF4JLogLevel.INFO).build();
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method method = ReflectionUtils.findMethod(dataSource.getClass(), invocation.getMethod().getName());
            if (method != null) {
                return method.invoke(dataSource, invocation.getArguments());
            }
            return invocation.proceed();
        }
    }
}

/*
 * TODO:
 * 1. use JdbcPagingItemReader to read..
 * 2. parallel execution???: https://docs.spring.io/spring-batch/4.0.x/reference/html/scalability.html#scalabilityParallelSteps
 * 3. retry?? continue???
 */
@SpringBootApplication
@EnableBatchProcessing
public class Application {

    @Bean
    @ConditionalOnProperty(prefix = "application", name = "datasource-proxy-enabled", havingValue = "true")
    public BeanPostProcessor datasourceProxyBeanPostProcessor() {
        return new DatasourceProxyBeanPostProcessor();
    }

    /* Do not use @Autowired fields, the interceptor will fail. */
    @Bean(destroyMethod = "")
    public JpaPagingItemReader<PBondListInfo> itemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<PBondListInfo>()
                .name("bondReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select b from PBondListInfo b") /* HQL uses entity names */
                .pageSize(2000)
                .build();
    }

    @Bean
    public ItemProcessor<PBondListInfo, String> itemProcessor(ObjectMapper mapper) {
        return item -> mapper.writeValueAsString(item);
    }

    @Bean
    public FlatFileItemWriter<String> itemWriter() {
        return new FlatFileItemWriterBuilder<String>()
                .name("itemWriter")
                .resource(new FileSystemResource("target/test-outputs/output.txt"))
                .lineAggregator(new PassThroughLineAggregator<>())
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory,
                      JpaPagingItemReader<PBondListInfo> itemReader, ItemProcessor<PBondListInfo, String> itemProcessor) {
        return stepBuilderFactory.get("step1")
                .<PBondListInfo, String>chunk(500) /* commits every 500 records */
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter())
                .build();
    }

	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory, Step step1) {
		return jobBuilderFactory.get("job1")
				.incrementer(new RunIdIncrementer())
				.start(step1)
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
