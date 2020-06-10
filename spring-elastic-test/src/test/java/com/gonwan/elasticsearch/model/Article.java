package com.gonwan.elasticsearch.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Document(indexName = "article", replicas = 0, shards = 1, createIndex = false)
public class Article {

    @Id
    private String id;

    @MultiField(
            mainField = @Field(value = "title", type = FieldType.Text, analyzer = "ik_smart"),
            otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword)
    )
    private String title;

    @Field(value = "content", type = FieldType.Text, analyzer = "ik_smart")
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

}
