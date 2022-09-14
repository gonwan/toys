package com.gonwan.toys.rocksdb;

import com.gonwan.toys.rocksdb.repository.IdData;
import com.gonwan.toys.rocksdb.repository.IdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RocksDBTest {

    private static final Logger logger = LoggerFactory.getLogger(RocksDBTest.class);

    @Autowired
    private IdRepository idRepository;

    @Test
    public void testWritePerformance() {
        int size = 100 * 10000;
        int prefix = 100000000;
        List<IdData> idData = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            IdData d = new IdData(prefix + i + "", i + "");
            idData.add(d);
        }
        logger.info("Begin to write...");
        idRepository.batchAdd(idData);
        logger.info("Finished writing...size={}", size);
        logger.info("Get 123123: value={}", idRepository.getByNewId("123123"));
    }

}
