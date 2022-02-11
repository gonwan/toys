package org.apache.lucene.demo;

public class TestLucene {

    public static void main(String[] args) throws Exception {
        IndexFiles.main(new String[] { "-docs", "docs" });
        SearchFiles.main(new String[0]);
    }

}
