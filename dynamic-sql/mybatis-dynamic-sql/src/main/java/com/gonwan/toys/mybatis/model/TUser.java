package com.gonwan.toys.mybatis.model;

import java.util.Date;
import javax.annotation.Generated;

public class TUser {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.username")
    private String username;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.password")
    private String password;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.version")
    private Long version;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.create_time")
    private Date createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.update_time")
    private Date updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.username")
    public String getUsername() {
        return username;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.username")
    public void setUsername(String username) {
        this.username = username;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.password")
    public String getPassword() {
        return password;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.password")
    public void setPassword(String password) {
        this.password = password;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.version")
    public Long getVersion() {
        return version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.version")
    public void setVersion(Long version) {
        this.version = version;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.create_time")
    public Date getCreateTime() {
        return createTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.create_time")
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.update_time")
    public Date getUpdateTime() {
        return updateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: t_user.update_time")
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}