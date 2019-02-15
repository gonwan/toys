package com.gonwan.snippet.json.demo.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gonwan.snippet.json.demo.converter.JsonToMapConverter;

@Entity
@Table(name = "t_test", schema = "testjson", catalog = "")
public class TTest2 {

    private long id;
    private String name;
    private HashMap<String, Object> properties;

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

    /*
     * @JsonUnwrapped only works with POJO, not maps. Use @JsonAnyGetter to workaround:
     * https://github.com/FasterXML/jackson-databind/issues/171
     * Please also ignore the Intellij error checking.
     */
    @JsonIgnore
    @Column(name = "properties", columnDefinition = "json")
    @Convert(converter = JsonToMapConverter.class)
    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    @Transient
    @JsonAnyGetter
    public Map<String, Object> getAllProperties() {
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TTest2 tTest = (TTest2) o;
        return id == tTest.id &&
                Objects.equals(name, tTest.name) &&
                Objects.equals(properties, tTest.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, properties);
    }

}
