package com.gonwan.snippet.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gonwan.snippet.spring.model.PBondListInfo;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManagerFactory;

/*
 * TODO:
 * 1. use JdbcPagingItemReader to read..
 * 2. parallel execution???: https://docs.spring.io/spring-batch/4.0.x/reference/html/scalability.html#scalabilityParallelSteps
 * 3. retry?? continue???
 */
@SpringBootApplication
@EnableBatchProcessing
public class Application {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
    private EntityManagerFactory entityManagerFactory;

	@Autowired
    private ObjectMapper objectMapper;

    @Bean
    public JpaPagingItemReader<PBondListInfo> itemReader() {
        return new JpaPagingItemReaderBuilder<PBondListInfo>()
                .name("bondReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select b from PBondListInfo b") /* HQL uses entity names */
                .pageSize(1000)
                .build();
    }

    @Bean
    public ItemProcessor<PBondListInfo, String> itemProcessor() {
        return item -> objectMapper.writeValueAsString(item);
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
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                .<PBondListInfo, String>chunk(2)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

	@Bean
	public Job job(Step step1) {
		return jobBuilderFactory.get("job1")
				.incrementer(new RunIdIncrementer())
				.start(step1)
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
