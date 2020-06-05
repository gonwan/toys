package com.gonwan.lucene.query;

import com.gonwan.lucene.analyzer.IKAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class HighlighterTest {

    public static void main(String[] args) throws Exception {
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
        QueryScorer queryScorer = new QueryScorer(query, field);
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"color: red\">", "</span>");
        Highlighter highlighter = new Highlighter(formatter, queryScorer);
        TopDocs topDocs = searcher.search(query, 10);
        for (ScoreDoc sd : topDocs.scoreDocs) {
            Document doc = searcher.doc(sd.doc);
            System.out.println("DocID:" + sd.doc);
            System.out.println("id:" + doc.get("id"));
            System.out.println("title:" + doc.get("title"));
            TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), sd.doc, field, analyzer);
            Fragmenter fragmenter = new SimpleSpanFragmenter(queryScorer);
            highlighter.setTextFragmenter(fragmenter);
            String str = highlighter.getBestFragment(tokenStream, doc.get(field));
            System.out.println("高亮片段:" + str);
        }
        dir.close();
        reader.close();
    }

}
