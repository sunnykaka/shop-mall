package com.kariqu.searchengine;

import org.apache.commons.lang.math.NumberRange;
import org.apache.lucene.LucenePackage;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.NumericTokenStream;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * User: Asion
 * Date: 11-6-26
 * Time: 下午12:19
 */
public class LuceneTest {

    @Test
    public void testLucene() throws IOException, ParseException {

        Directory directory = new RAMDirectory();
        //Directory directory = FSDirectory.open(new File("D://tmp"));
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_33, new StandardAnalyzer(Version.LUCENE_33));
        IndexWriter writer = new IndexWriter(directory, indexWriterConfig);

        Document doc1 = new Document();
        doc1.add(new Field("productName", "china china china hibernate java spring", Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
        doc1.add(new Field("id", "2", Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.YES));
        doc1.add(new NumericField("pid").setIntValue(3));


        writer.addDocument(doc1);
        doc1.setBoost(2);
        writer.addDocument(doc1);

        writer.close();

        IndexReader reader = IndexReader.open(directory);

        IndexSearcher searcher = new IndexSearcher(reader);

        IndexReader indexReader = searcher.getIndexReader();


        QueryParser queryParser = new QueryParser(Version.LUCENE_33, "productName", new StandardAnalyzer(Version.LUCENE_33));
        Query query = queryParser.parse("id:2");


        NumericRangeQuery<Integer> numericRangeQuery = NumericRangeQuery.newIntRange("pid", 0, 3, true, true);
        TopDocs rs = searcher.search(query, 10);

        System.out.println("搜到文档:" + rs.totalHits);
        System.out.println("最大得分:" + rs.getMaxScore());


        for (ScoreDoc doc : rs.scoreDocs) {
            Document document = searcher.doc(doc.doc);
            TermFreqVector productName = searcher.getIndexReader().getTermFreqVector(doc.doc, "productName");
            for (String s : productName.getTerms()) {
                System.out.println(s);
            }
            for (int i : productName.getTermFrequencies()) {
                System.out.println(i);
            }
            System.out.println("商品名称:" + document.getFieldable("productName").stringValue());
            System.out.println("得分:" + doc.score);
            System.out.println(searcher.explain(query, doc.doc));
        }

    }

    @Test
    public void testAnalyzer() throws IOException {
        //Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_36);
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
        TokenStream ts = analyzer.tokenStream("myfield", new StringReader("基因组序列中蛋白质编码区域的预测"));
        System.out.println(ts);
        while (ts.incrementToken()) {
            System.out.println("token: " + ts);
        }
    }


}
