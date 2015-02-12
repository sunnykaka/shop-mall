package com.kariqu.common.uri;

import junit.framework.TestCase;


/**
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-22 下午09:20:41
 */
public class URLBrokerTest extends TestCase {

    public void testRender() {
        URLBroker urlBroker = new DefaultURLBroker();
        urlBroker.addToken("resourceId");
        urlBroker.addToken("templateId");
        urlBroker.setServerUrl("http://www.kariqu.com");
        urlBroker.setPath("{templateId}/{resourceId}");
        urlBroker.addQueryData("t", "20110422.css");
        urlBroker.addQueryData("key", "value");
        urlBroker.addQueryData("resourceId", 100);
        urlBroker.addQueryData("templateId", 200);
        assertEquals("http://www.kariqu.com/200/100?t=20110422.css&key=value", urlBroker.toString());
    }


}
