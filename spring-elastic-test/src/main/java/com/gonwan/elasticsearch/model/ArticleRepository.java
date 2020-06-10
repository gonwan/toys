package com.gonwan.elasticsearch.model;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends ElasticsearchRepository<Article, String> {

    /* very very hard to use... */
    //@Query("{\"match\": {\"title\": {\"query\": \"?0\"}}}")
    @Query("{\"multi_match\": { \"query\": \"?0\", \"analyzer\": \"ik_smart\", \"fields\": [ \"title\", \"content\"] }}")
    SearchHits<Article> findAllByTitleAndContent(String str);

}
