package com.gonwan.benchmark.springwebflux.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("world")
public class World {

    @Id
    @Column("id")
    private Integer id;

    @Column("random_number")
    private Integer randomNumber;

    @Column("random_text")
    private String randomText;

    /* java.sql.Timestamp not supported */
    @Column("update_time")
    private LocalDateTime updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(Integer randomNumber) {
        this.randomNumber = randomNumber;
    }

    public String getRandomText() {
        return randomText;
    }

    public void setRandomText(String randomText) {
        this.randomText = randomText;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

}
