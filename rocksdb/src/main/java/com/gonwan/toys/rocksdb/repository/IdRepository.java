package com.gonwan.toys.rocksdb.repository;

import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/*
 * maps old/new id, local cache using rocksdb.
 */
@Component
public class IdRepository {

    private static final Logger logger = LoggerFactory.getLogger(IdRepository.class);

    public static final String VERSION = "1.0";

    public static final String VERSION_KEY = "version";

    public static final String NID_PREFIX = "nid_";

    public static final String OID_PREFIX = "oid_";

    private RocksDB rocksDB;

    @Autowired
    public IdRepository(RocksDB rocksDB) {
        this.rocksDB = rocksDB;
    }

    public void flush() {
        try {
            rocksDB.flush(new FlushOptions());
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public String getVersion() {
        return VERSION;
    }

    public void setRepoVersion(String version) {
        try {
            rocksDB.put(VERSION_KEY.getBytes(), version.getBytes());
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRepoVersion() {
        byte[] byKey = VERSION_KEY.getBytes();
        try {
            byte[] byValue = rocksDB.get(byKey);
            return (byValue == null) ? null : new String(byValue);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public void batchAdd(List<IdData> idData) {
        try (WriteBatch writeBatch = new WriteBatch()) {
            for (IdData d : idData) {
                byte[] byNewId = (NID_PREFIX + d.getNewId()).getBytes();
                byte[] byOldId = (OID_PREFIX + d.getOldId()).getBytes();
                writeBatch.put(byNewId, d.getOldId().getBytes());
                writeBatch.put(byOldId, d.getNewId().getBytes());
            }
            rocksDB.write(new WriteOptions(), writeBatch);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public String getByNewId(String newId) {
        byte[] byNewId = (NID_PREFIX + newId).getBytes();
        try {
            byte[] byValue = rocksDB.get(byNewId);
            return (byValue == null) ? null : new String(byValue);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public String getByOldId(String oldId) {
        byte[] byOldId = (OID_PREFIX + oldId).getBytes();
        try {
            byte[] byValue = rocksDB.get(byOldId);
            return (byValue == null) ? null : new String(byValue);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public void benchInit() {
        int batch = 10000;
        int batchSize = 10000;
        int prefix = 1000000000;
        for (int i = 0; i < batch; i++) {
            List<IdData> idData = new ArrayList<>(batchSize);
            int begin = batchSize * i;
            int end = batchSize * (i + 1);
            for (int j = begin; j < end; j++) {
                IdData d = new IdData(prefix + j + "", j + "");
                idData.add(d);
            }
            this.batchAdd(idData);
            logger.info("Finished running batch: {}/{}", i, batch);
        }
        this.flush();
    }

}
