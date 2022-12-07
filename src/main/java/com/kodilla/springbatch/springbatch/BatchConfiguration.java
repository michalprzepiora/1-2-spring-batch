package com.kodilla.springbatch.springbatch;

import com.kodilla.springbatch.model.PersonAge;
import com.kodilla.springbatch.model.PersonBirthDate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    private static final int CHUNK_SIZE = 100;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    FlatFileItemReader<PersonBirthDate> reader() {
        FlatFileItemReader<PersonBirthDate> reader = new FlatFileItemReader<>();
        reader.setEncoding("UTF-8");
        reader.setResource(new ClassPathResource("input.csv"));

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("id", "name", "surname", "birthDate");

        BeanWrapperFieldSetMapper<PersonBirthDate> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(PersonBirthDate.class);

        DefaultLineMapper<PersonBirthDate> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(mapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    PersonProcessor processor(){
        return new PersonProcessor();
    }

    @Bean
    FlatFileItemWriter<PersonAge> writer() {
        BeanWrapperFieldExtractor<PersonAge> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[] {"id", "name", "surname", "age"});

        DelimitedLineAggregator<PersonAge> aggregator = new DelimitedLineAggregator<>();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(extractor);

        FlatFileItemWriter<PersonAge> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("output.csv"));
        writer.setShouldDeleteIfExists(true);
        writer.setLineAggregator(aggregator);

        return writer;
    }

    @Bean
    Step convertDateToAge(
            ItemReader<PersonBirthDate> reader,
            ItemProcessor<PersonBirthDate, PersonAge> processor,
            ItemWriter<PersonAge> writer) {

        return stepBuilderFactory.get("priceChange")
                .<PersonBirthDate, PersonAge>chunk(CHUNK_SIZE)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    Job changeBirthDateToAgeJob(Step priceChange) {
        return jobBuilderFactory.get("changeBirthDateToAgeJob")
                .incrementer(new RunIdIncrementer())
                .flow(priceChange)
                .end()
                .build();
    }
}
