package com.gonwan.toys.rocksdb.controller;

import com.gonwan.toys.rocksdb.repository.IdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rocksdb")
public class RocksDBController {

    @Autowired
    private IdRepository idRepository;

    @GetMapping("/init")
    public void init() {
        idRepository.benchInit();
    }

    @GetMapping("/query")
    public String query(String id) {
        String oldId = idRepository.getByNewId(id);
        return oldId;
    }

}
