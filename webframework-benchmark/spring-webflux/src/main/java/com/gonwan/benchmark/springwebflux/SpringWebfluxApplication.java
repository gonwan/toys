package com.gonwan.benchmark.springwebflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcRepositories
@SpringBootApplication
public class SpringWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringWebfluxApplication.class, args);
    }

}
