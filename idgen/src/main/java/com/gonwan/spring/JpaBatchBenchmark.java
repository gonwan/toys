package com.gonwan.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.gonwan.spring.generated.TUser;

/*
 * Reference:
 * https://github.com/spring-cloud/spring-cloud-sleuth/blob/master/benchmarks/src/main/java/org/springframework/cloud/sleuth/benchmarks/jmh/benchmarks/RestTemplateBenchmark.java
 */
public class JpaBatchBenchmark {

    @State(Scope.Benchmark)
    public static class Context {

        TUserRepository repository;

        @Setup
        public void setup() throws Exception {
            ApplicationContext appCtx = SpringApplication.run(JpaBatchApplication.class);
            repository = appCtx.getBean(TUserRepository.class);
        }

        @TearDown
        public void tearDown() throws Exception {

        }

    }

    @State(Scope.Benchmark)
    public static class Params {

        @Param(value = { "1", "4", "8", "16", "32" })
        String threads;

        @Param(value = "2000")
        String records_per_thread;

    }

    @Benchmark
    public void runBenchmark(Context context, Params params) throws InterruptedException {
        int threads = Integer.valueOf(params.threads);
        int records_per_thread = Integer.valueOf(params.records_per_thread);
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
        for (List<TUser> users : usersList) {
            executorService.submit(() -> {
                boolean batch = true;
                if (batch) {
                    context.repository.save(users);
                } else {
                    for (TUser user : users) {
                        context.repository.save(user);
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JpaBatchBenchmark.class.getSimpleName() + ".*")
                .mode(Mode.AverageTime)
                .warmupIterations(2)
                .measurementIterations(5)
                .forks(1)
                .build();
        new Runner(opt).run();
    }

}
