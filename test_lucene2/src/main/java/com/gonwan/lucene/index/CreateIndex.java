package com.gonwan.lucene.index;

import com.gonwan.lucene.analyzer.IKAnalyzer;
import com.gonwan.lucene.model.News;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class CreateIndex {

    public static Document getDocument(News news, FieldType idType, FieldType titleType, FieldType contentType) {
        Document doc = new Document();
        doc.add(new Field("id", news.getId() + "", idType));
        doc.add(new Field("title", news.getTitle(), titleType));
        doc.add(new Field("content", news.getContent(), contentType));
        doc.add(new IntPoint("reply", news.getReply()));
        doc.add(new StoredField("reply_display", news.getReply()));
        return doc;
    }

    public static void main(String[] args) {
        News news1 = new News();
        news1.setId(1);
        news1.setTitle("习近平会见美国总统奥巴马，学习国外经验");
        news1.setContent("国家主席习近平9月3日在杭州西湖国宾馆会见前来出席二十国集团领导人杭州峰会的美国总统奥巴马...");
        news1.setReply(672);
        News news2 = new News();
        news2.setId(2);
        news2.setTitle("北大迎4380名新生 农村学生700多人近年最多");
        news2.setContent("昨天，背景大学迎来4380名来自全国各地及数十个国家的本科新生。其中，农村学生共700余名，为近年最多...");
        news2.setReply(995);
        News news3 = new News();
        news3.setId(3);
        news3.setTitle("特朗普宣誓（Donald Trump）救人美国第45任总统");
        news3.setContent("当地时间1月20日，唐纳德·特朗普在美国国会宣誓就只，正式成为美国第45任总统。");
        news3.setReply(1872);
        /* index */
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory dir = null;
        IndexWriter indexWriter = null;
        Path indexPath = Paths.get("indexdir");
        Date start = new Date();
        try {
            if (!Files.exists(indexPath)) {
                Files.createDirectory(indexPath);
            }
            dir = FSDirectory.open(indexPath);
            indexWriter = new IndexWriter(dir, config);
            FieldType idType = new FieldType();
            idType.setIndexOptions(IndexOptions.DOCS);
            idType.setStored(true);
            FieldType titleType = new FieldType();
            titleType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            titleType.setStored(true);
            titleType.setTokenized(true);
            FieldType contentType = new FieldType();
            contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            contentType.setStored(true);
            contentType.setTokenized(true);
            contentType.setStoreTermVectors(true);
            contentType.setStoreTermVectorPositions(true);
            contentType.setStoreTermVectorOffsets(true);
            contentType.setStoreTermVectorPayloads(true);
            Document doc1 = getDocument(news1, idType, titleType, contentType);
            Document doc2 = getDocument(news2, idType, titleType, contentType);
            Document doc3 = getDocument(news3, idType, titleType, contentType);
            indexWriter.addDocument(doc1);
            indexWriter.addDocument(doc2);
            indexWriter.addDocument(doc3);
            indexWriter.commit();
            indexWriter.close();
            dir.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date end = new Date();
        System.out.printf("索引文档用时：%d ms\n", (end.getTime() - start.getTime()));
    }

}
