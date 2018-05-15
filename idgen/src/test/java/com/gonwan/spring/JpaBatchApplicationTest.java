package com.gonwan.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import com.gonwan.spring.generated.TUser;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaBatchApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(JpaBatchApplicationTest.class);

    @Autowired
    private TUserRepository repository;

    public void runBenchmark(int threads, int records_per_thread) throws InterruptedException {
        logger.info("Running: threads={}, records_per_threads={}", threads, records_per_thread);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        List<List<TUser>> usersList = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            List<TUser> users = new ArrayList<>(records_per_thread);
            for (int j = 0; j < records_per_thread; j++) {
                TUser user = new TUser();
                int index = records_per_thread * i + j;
                user.setField1("f1_" + index);
                user.setField2("f2_" + index);
                user.setField3("f3_" + index);
                user.setField4("f4_" + index);
                user.setField5("f5_" + index);
                user.setField6("f6_" + index);
                user.setField7("f7_" + index);
                user.setField8("f8_" + index);
                user.setField9("f9_" + index);
                user.setField10("f10_" + index);
                users.add(user);
            }
            usersList.add(users);
        }
        StopWatch sw = new StopWatch();
        sw.start();
        for (List<TUser> users : usersList) {
            executorService.submit(() -> {
                boolean batch = true;
                if (batch) {
                    repository.save(users);
                } else {
                    for (TUser user : users) {
                        repository.save(user);
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
        sw.stop();
        logger.info("Finished: threads={}, records_per_threads={}, duration_in_ms={}", threads, records_per_thread, sw.getTotalTimeMillis());
    }

    @Test
    public void testInsert() {
        try {
            runBenchmark(1, 2000);
            runBenchmark(2, 2000);
            runBenchmark(4, 2000);
            runBenchmark(8, 2000);
            runBenchmark(16, 2000);
            runBenchmark(32, 2000);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }

}
