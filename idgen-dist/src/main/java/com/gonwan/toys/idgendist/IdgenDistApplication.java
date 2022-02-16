package com.gonwan.toys.idgendist;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "com.gonwan.toys.idgendist", "com.netease.nim.camellia.id.gen.springboot" })
@MapperScan("com.netease.nim.camellia.id.gen.springboot.idloader")
public class IdgenDistApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdgenDistApplication.class, args);
    }

}
