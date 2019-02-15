package com.gonwan.snippet.json.demo;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Suppose we do not have the source code of <code>Target</code>.
 */
class Target {

    private String p1;

    private String p2;

    public Target(String p1, String p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public String getP1() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public String getP2() {
        return p2;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }

}

class MixinSource {

    @JsonProperty("username")
    private String p1;

    @JsonProperty("password")
    private String p2;

}

public class JsonMixinTest {

    @Test
    public void test() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(Target.class, MixinSource.class);
        Target target = new Target("user1", "pass1");
        String s = objectMapper.writeValueAsString(target);
        System.out.println(s);
    }

}
