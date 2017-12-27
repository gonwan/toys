package com.gonwan.spring;

import org.hibernate.dialect.MySQL5Dialect;

/*
 * Copied from org.hibernate.dialect.PostgreSQL81Dialect.
 */
public class MariaDB103Dialect extends MySQL5Dialect {

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public boolean supportsPooledSequences() {
        return true;
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "select " + getSelectSequenceNextValString(sequenceName);
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) {
        return "nextval (`" + sequenceName + "`)";
    }

}
