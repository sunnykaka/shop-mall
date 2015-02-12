package com.kariqu.searchengine;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * User: Asion
 * Date: 11-6-26
 * Time: 下午12:25
 */
public class SolrTest {

    @Test
    public void testDirectSolrConnection() throws Exception, SAXException, ParserConfigurationException {
       /* //System.setProperty("solr.solr.home", "D:\\search\\apache-solr-3.2.0\\example\\solr");
        System.setProperty("solr.solr.home", "D:\\search\\apache-solr-3.2.0\\example\\example-DIH\\solr");
        CoreContainer coreContainer = new CoreContainer.Initializer().initialize();
        DirectSolrConnection directSolrConnection = new DirectSolrConnection(coreContainer.getCore("db"));
        System.out.println(directSolrConnection.request("/select?qt=/dataimport&command=delta-import&clean=false&commit=true", null));

        System.out.println(directSolrConnection.request("/select?q=*:*", null));*/

    }

    @Test
    public void testEmbeddedSolrServer() throws IOException, SAXException, ParserConfigurationException, SolrServerException {
       /* System.setProperty("solr.solr.home", "D:\\search\\apache-solr-3.3.0\\example\\solr");
        CoreContainer coreContainer = new CoreContainer.Initializer().initialize();
        SolrServer server = new EmbeddedSolrServer(coreContainer, "");

        SolrQuery query = new SolrQuery();
        query.setQuery("sell:3");
        //query.setFilterQueries("sell:3");
        System.out.println(server.query(query));*/

    }

    @Test
    public void testHttpServer() throws IOException, SolrServerException {
        /*SolrServer server = new CommonsHttpSolrServer("http://localhost:8034/solr");
        SolrQuery solrQuery = new SolrQuery("3");
        QueryResponse query = server.query(solrQuery);
        System.out.println(query.getResults().getNumFound());
        System.out.println(query);*/
    }


    @Test
    public void testPatternTokenizer() throws IOException {
       /* PatternTokenizer patternTokenizer = new PatternTokenizer(new StringReader("(笔记本)(面)的"), Pattern.compile("\\(([^\\(]+)\\)"),0);
        while (patternTokenizer.incrementToken()) {
            System.out.println(patternTokenizer);
        }*/
    }
}
