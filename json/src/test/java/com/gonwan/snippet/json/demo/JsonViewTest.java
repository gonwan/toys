package com.gonwan.snippet.json.demo;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

class Views {

    public static class Public {
    }

    public static class Internal extends Public {
    }

}

/*
 * @JsonView also works with spring controllers, see:
 * https://www.baeldung.com/jackson-json-view-annotation
 */
class Item {

    @JsonView(Views.Public.class)
    private int id;

    @JsonView(Views.Public.class)
    private String itemName;

    @JsonView(Views.Internal.class)
    private String ownerName;

    public Item(int id, String itemName, String ownerName) {
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

public class JsonViewTest {

    @Test
    public void test() throws IOException {
        Item item = new Item(1, "book", "John");
        ObjectMapper objectMapper = new ObjectMapper();
        String s1 = objectMapper.writerWithView(Views.Public.class).writeValueAsString(item);
        String s2 = objectMapper.writerWithView(Views.Internal.class).writeValueAsString(item);
        System.out.println(s1);
        System.out.println(s2);
    }

}
