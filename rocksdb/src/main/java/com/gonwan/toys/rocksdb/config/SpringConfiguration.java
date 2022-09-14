package com.gonwan.toys.rocksdb.config;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Configuration
public class SpringConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SpringConfiguration.class);

    private static final long MIN_CACHE_SIZE = 256 * 1024 * 1024;

    /* override the default one to log deserialization failures. */
    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addHandler(new DeserializationProblemHandler() {
                    /* to work with @JsonFormat */
                    @Override
                    public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType, String valueToConvert, String failureMsg) throws IOException {
                        logger.warn("Failed to parse {}={}: {}", ctxt.getParser().getCurrentName(), valueToConvert, failureMsg);
                        return null;
                    }
                })
                .build();
    }

    @Bean(destroyMethod = "close")
    public RocksDB rocksDB(Config config) {
        RocksDB.loadLibrary();
        Options options = new Options();
        options.setCreateIfMissing(true);
        /*
         * also see: Options#PrepareForBulkLoad()
         */
        options.setMaxOpenFiles(-1);
        options.setMaxBackgroundJobs(4);
        options.setCompactionStyle(CompactionStyle.LEVEL);
        options.setMaxWriteBufferNumber(4);
        options.setMinWriteBufferNumberToMerge(3);
        options.setWriteBufferSize(64*1024*1024);
        options.setTargetFileSizeBase(256*1024*1024);
        options.setLevelCompactionDynamicLevelBytes(true);
        long cacheSize = config.getRocksdbCacheSize().toBytes();
        if (cacheSize > 0) {
            /*
             * see: https://github.com/facebook/rocksdb/wiki/Setup-Options-and-Basic-Tuning
             * see: https://github.com/apache/flink/blob/master/flink-state-backends/flink-statebackend-rocksdb/src/main/java/org/apache/flink/contrib/streaming/state/PredefinedOptions.java
             */
            if (cacheSize < MIN_CACHE_SIZE) {
                cacheSize = MIN_CACHE_SIZE;
            }
            BlockBasedTableConfig tableConfig = new BlockBasedTableConfig();
            tableConfig.setBlockCacheSize(cacheSize);
            tableConfig.setBlockSize(16384); /* 16k */
            options.setTableFormatConfig(tableConfig);
        }
        File dbDir = new File(config.getRocksdbDirectory());
        try {
            Files.createDirectories(dbDir.getAbsoluteFile().toPath());
            return RocksDB.open(options, dbDir.getAbsolutePath());
        } catch(IOException | RocksDBException e) {
            throw new RuntimeException("Failed to initialize rocksdb", e);
        }
    }

}
