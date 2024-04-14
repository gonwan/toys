package com.gonwan.springjpatest.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "t_user", schema = "test", catalog = "")
public class TUser {

    private Long id;
    private String username;
    private String password;
    private Long version;
    private Date createTime;
    private Timestamp updateTime;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "username", nullable = true, length = 50)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "password", nullable = true, length = 50)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //@Version
    @Basic
    @Column(name = "version", nullable = true)
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @CreatedDate
    @Basic
    @Column(name = "create_time", nullable = false, updatable = false)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Version
    @LastModifiedDate
    @Basic
    @Column(name = "update_time", nullable = false)
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TUser tUser = (TUser) o;
        return Objects.equals(id, tUser.id) &&
                Objects.equals(username, tUser.username) &&
                Objects.equals(password, tUser.password) &&
                Objects.equals(version, tUser.version) &&
                Objects.equals(createTime, tUser.createTime) &&
                Objects.equals(updateTime, tUser.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, version, createTime, updateTime);
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return String.format("{id=%d,username=%s,password=%s,version=%d,createtime=%s,updatetime=%s}", id, username, password, version, sdf.format(createTime), sdf.format(updateTime));
    }

}
