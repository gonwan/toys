package com.gonwan.toys.rocksdb.controller;

import com.gonwan.toys.rocksdb.repository.IdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rocksdb")
public class RocksDBController {

    @Autowired
    private IdRepository idRepository;

    @GetMapping("/init")
    public ResponseEntity<?> init() {
        idRepository.benchInit();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/query")
    public ResponseEntity<?> query(String id) {
        String oldId = idRepository.getByNewId(id);
        return ResponseEntity.ok().body(oldId);
    }

}
