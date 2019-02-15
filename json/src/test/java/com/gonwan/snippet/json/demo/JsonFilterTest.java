package com.gonwan.snippet.json.demo;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/*
 * @JsonFilter also works with spring. see: Jackson2ObjectMapperBuilder#filters().
 */
@JsonFilter("myFilter")
class Item2 {

    private int id;

    private String itemName;

    private String ownerName;

    public Item2(int id, String itemName, String ownerName) {
        this.id = id;
        this.itemName = itemName;
        this.ownerName = ownerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

}

public class JsonFilterTest {

    @Test
    public void test() throws IOException {
        Item2 item = new Item2(1, "book", "John");
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("myFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id", "itemName"));
        objectMapper.setFilterProvider(filterProvider);
        String s = objectMapper.writeValueAsString(item);
        System.out.println(s);
    }

}
