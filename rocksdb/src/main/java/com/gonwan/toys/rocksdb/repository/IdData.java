package com.gonwan.toys.rocksdb.repository;

public class IdData {

    private String newId;

    private String oldId;

    public IdData(String newId, String oldId) {
        this.newId = newId;
        this.oldId = oldId;
    }

    public String getNewId() {
        return newId;
    }

    public void setNewId(String newId) {
        this.newId = newId;
    }

    public String getOldId() {
        return oldId;
    }

    public void setOldId(String oldId) {
        this.oldId = oldId;
    }

}
