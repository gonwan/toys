package com.gonwan.toys.rocksdb.repository;

import org.apache.commons.lang3.StringUtils;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    public static final String TTL_PREFIX = "ttl_";

    private RocksDB rocksDB;

    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    public IdRepository(RocksDB rocksDB) {
        this.rocksDB = rocksDB;
        this.taskScheduler = new TaskSchedulerBuilder().poolSize(1).build();
        this.taskScheduler.initialize();
        this.initTtlThread();
    }

    public void flush() {
        try {
            /* flush() also triggers compact, and leads to high cpu usage. */
            rocksDB.flushWal(true);
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

    private void initTtlThread() {
        /* simple and dummy implementation */
        taskScheduler.scheduleWithFixedDelay(() -> {
            List<byte[]> keys = new ArrayList<>(16);
            byte[] bySeekKey = (TTL_PREFIX + System.currentTimeMillis()).getBytes();
            RocksIterator it = rocksDB.newIterator();
            for (it.seekForPrev(bySeekKey); it.isValid(); it.prev()) {
                String strKey = new String(it.key());
                if (!strKey.startsWith(TTL_PREFIX)) {
                    break;
                }
                keys.add(it.key());
                String[] ks = StringUtils.split(new String(it.value()), ',');
                if (ks != null) {
                    for (String k : ks) {
                        keys.add(k.getBytes());
                    }
                }
            }
            if (!keys.isEmpty()) {
                try (WriteBatch writeBatch = new WriteBatch()) {
                    for (byte[] k : keys) {
                        writeBatch.delete(k);
                    }
                    rocksDB.write(new WriteOptions(), writeBatch);
                } catch (RocksDBException e) {
                    throw new RuntimeException(e);
                }
            }
        }, Duration.ofSeconds(1));
    }

    public String get(String key) {
        byte[] byKey = key.getBytes();
        try {
            byte[] byValue = rocksDB.get(byKey);
            return (byValue == null) ? null : new String(byValue);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAndExpireAt(String key, String value, long expireAt) {
        try (WriteBatch writeBatch = new WriteBatch()) {
            byte[] byKey = key.getBytes();
            byte[] byTtlKey = (TTL_PREFIX + expireAt).getBytes();
            writeBatch.put(byKey, value.getBytes());
            writeBatch.merge(byTtlKey, byKey); /* merge here, multiple values separated with ','. */
            rocksDB.write(new WriteOptions(), writeBatch);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAndExpireAfter(String key, String value, Duration duration) {
        setAndExpireAt(key, value, System.currentTimeMillis() + duration.toMillis());
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

    public void ttltest() {
        this.setAndExpireAfter("test1", "value1", Duration.ofSeconds(3));
        for (int i = 0; i < 5; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String v = this.get("test1");
            logger.info("Get test1: {}", v);
        }
    }

}
