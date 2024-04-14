package com.gonwan.toys;

import org.rocksdb.RocksDB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/db")
class DbController {

    @GetMapping("/version")
    public Object version() {
        return RocksDB.rocksdbVersion();
    }

}

@SpringBootApplication
public class RocksDBApplication {

    public static void main(String[] args) {
        SpringApplication.run(RocksDBApplication.class, args);
    }

}
