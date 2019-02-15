package com.gonwan.snippet.json.demo.model;

import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.gonwan.snippet.json.demo.converter.Props;
import com.gonwan.snippet.json.demo.converter.PropsConverter;

@Entity
@Table(name = "t_test", schema = "testjson", catalog = "")
public class TTest {

    private long id;
    private String name;
    private Props properties;

    @Id
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = true, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonUnwrapped
    @Column(name = "properties", columnDefinition = "json")
    @Convert(converter = PropsConverter.class)
    public Props getProperties() {
        return properties;
    }

    public void setProperties(Props properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TTest tTest = (TTest) o;
        return id == tTest.id &&
                Objects.equals(name, tTest.name) &&
                Objects.equals(properties, tTest.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, properties);
    }

}
