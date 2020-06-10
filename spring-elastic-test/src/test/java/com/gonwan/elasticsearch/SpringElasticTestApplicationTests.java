package com.gonwan.elasticsearch;

import com.gonwan.elasticsearch.model.Article;
import com.gonwan.elasticsearch.model.ArticleRepository;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.util.StreamUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class SpringElasticTestApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(SpringElasticTestApplicationTests.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private ArticleRepository articleRepository;

    private List<Article> getArticles() {
        List<Article> articles = new ArrayList<>(3);
        Article article = new Article();
        article.setTitle("习近平会见美国总统奥巴马，学习国外经验");
        article.setContent("国家主席习近平9月3日在杭州西湖国宾馆会见前来出席二十国集团领导人杭州峰会的美国总统奥巴马...");
        articles.add(article);
        article = new Article();
        article.setTitle("北大迎4380名新生 农村学生700多人近年最多");
        article.setContent("昨天，背景大学迎来4380名来自全国各地及数十个国家的本科新生。其中，农村学生共700余名，为近年最多...");
        articles.add(article);
        article = new Article();
        article.setTitle("特朗普宣誓（Donald Trump）救人美国第45任总统");
        article.setContent("当地时间1月20日，唐纳德·特朗普在美国国会宣誓就职，正式成为美国第45任总统。");
        articles.add(article);
        return articles;
    }

    /*
     * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-create-index.html
     */
    @Test
    void testHLC() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("articles");
        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
        );
        Map<String, Object> message = new HashMap<>();
        message.put("type", "text");
        Map<String, Object> properties = new HashMap<>();
        properties.put("title", message);
        properties.put("content", message);
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);
        createIndexRequest.mapping(mapping);
        logger.info("Create index quest: {}", Strings.toString(createIndexRequest));
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        logger.info("Create index response: {}", Strings.toString(createIndexResponse));
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("articles");
        AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        logger.info("Acknowledged response: {}", Strings.toString(acknowledgedResponse));
    }

    @Test
    void testESTemplate() {
        /* index */
        Document settings = Document.create()
                .append("index.number_of_shards", 1)
                .append("index.number_of_replicas", 0);
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Article.class);
        indexOperations.create(settings);
        indexOperations.putMapping(indexOperations.createMapping());
        /* document */
        List<Article> articles = getArticles();
        List<IndexQuery> indexQueries = articles.stream()
                .map(x -> new IndexQueryBuilder().withObject(x).build())
                .collect(Collectors.toList());
        IndexCoordinates indexCoordinates = elasticsearchRestTemplate.getIndexCoordinatesFor(Article.class);
        List<String> ids = elasticsearchRestTemplate.bulkIndex(indexQueries, indexCoordinates);
        indexOperations.refresh(); /* ensure documents are indexed */
        logger.info("Document indexed: {}", ids);
        /* query */
        QueryBuilder queryBuilder = new MultiMatchQueryBuilder("美国", "title", "content").analyzer("ik_smart");
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();
        logger.info("Query: {}", Strings.toString(searchQuery.getQuery()));
        SearchHits<Article> searchHits = elasticsearchRestTemplate.search(searchQuery, Article.class);
        logger.info("Search result: {}", searchHits.stream().map(x -> x.getContent()).collect(Collectors.toList()));
        indexOperations.delete();
    }

    @Test
    void testESRepository() {
        /* index */
        Document settings = Document.create()
                .append("index.number_of_shards", 1)
                .append("index.number_of_replicas", 0);
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Article.class);
        indexOperations.create(settings);
        indexOperations.putMapping(indexOperations.createMapping());
        /* document */
        List<Article> articles = getArticles();
        Iterable<Article> resArticles = articleRepository.saveAll(articles);
        indexOperations.refresh(); /* ensure documents are indexed */
        List<String> ids = StreamUtils.createStreamFromIterator(resArticles.iterator()).map(x -> x.getId()).collect(Collectors.toList());
        logger.info("Document indexed: {}", ids);
        /* query */
        SearchHits<Article> searchHits = articleRepository.findAllByTitleAndContent("美国");
        logger.info("Search result: {}", searchHits.stream().map(x -> x.getContent()).collect(Collectors.toList()));
        indexOperations.delete();
    }

}
