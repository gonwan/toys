package com.gonwan.snippet.json.demo;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

class Item3 {

    private int id;

    private String dolarItemName;

    private String dolarOwnerName;

    @JsonCreator
    public Item3(@JsonProperty("id") int id, @JsonProperty("itemName") String itemName, @JsonProperty("ownerName") String ownerName) {
        this.id = id;
        this.dolarItemName = "$" + itemName;
        this.dolarOwnerName = "$" + ownerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDolarItemName() {
        return dolarItemName;
    }

    public void setDolarItemName(String dolarItemName) {
        this.dolarItemName = dolarItemName;
    }

    public String getDolarOwnerName() {
        return dolarOwnerName;
    }

    public void setDolarOwnerName(String dolarOwnerName) {
        this.dolarOwnerName = dolarOwnerName;
    }

}

public class JsonCreatorTest {

    @Test
    public void test() throws IOException {
        String s = "{ \"id\": 1, \"itemName\": \"book\", \"ownerName\": \"John\" }";
        ObjectMapper objectMapper = new ObjectMapper();
        Item3 item = objectMapper.readValue(s, Item3.class);
        String s2 = objectMapper.writeValueAsString(item);
        System.out.println(s2);
    }

}
