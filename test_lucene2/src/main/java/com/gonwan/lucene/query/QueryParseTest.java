package com.gonwan.lucene.query;

import com.gonwan.lucene.analyzer.IKAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QueryParseTest {

    public static void main(String[] args) throws IOException, ParseException {
        String field = "title";
        Path indexPath = Paths.get("indexdir");
        Directory dir = FSDirectory.open(indexPath);
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new IKAnalyzer(true);
        QueryParser queryParser = new QueryParser(field, analyzer);
        queryParser.setDefaultOperator(QueryParser.Operator.AND);
        Query query = queryParser.parse("农村学生");
        System.out.println("Query: " + query.toString());
        TopDocs topDocs = searcher.search(query, 10);
        for (ScoreDoc sd : topDocs.scoreDocs) {
            Document doc = searcher.doc(sd.doc);
            System.out.println("DocID:" + sd.doc);
            System.out.println("id:" + doc.get("id"));
            System.out.println("title:" + doc.get("title"));
            System.out.println("文档评分:" + sd.score);
        }
        dir.close();
        reader.close();
    }

}
