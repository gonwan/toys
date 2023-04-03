package com.gonwan.toys.querydsl.model.generated;

import javax.annotation.Generated;

/**
 * TUser is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class TUser {

    private java.sql.Timestamp createTime;

    private Long id;

    private String password;

    private java.sql.Timestamp updateTime;

    private String username;

    private Long version;

    public java.sql.Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.sql.Timestamp createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public java.sql.Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(java.sql.Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}

