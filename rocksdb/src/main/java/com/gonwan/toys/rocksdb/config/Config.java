package com.gonwan.toys.rocksdb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component("application")
@ConfigurationProperties(prefix = "application")
public class Config {

    private String rocksdbDirectory;

    private DataSize rocksdbCacheSize;

    public DataSize getRocksdbCacheSize() {
        return rocksdbCacheSize;
    }

    public void setRocksdbCacheSize(DataSize rocksdbCacheSize) {
        this.rocksdbCacheSize = rocksdbCacheSize;
    }

    public String getRocksdbDirectory() {
        return rocksdbDirectory;
    }

    public void setRocksdbDirectory(String rocksdbDirectory) {
        this.rocksdbDirectory = rocksdbDirectory;
    }

}
