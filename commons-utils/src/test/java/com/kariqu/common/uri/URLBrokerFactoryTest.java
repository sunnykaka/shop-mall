package com.kariqu.common.uri;

import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-5-9
 * Time: 下午10:43
 */
public class URLBrokerFactoryTest {

    @Test
    public void testInit() throws Exception {
        URLBrokerFactory factory = new URLBrokerFactory();
        factory.setUrlConfigName("url.xml");
        factory.init();
        assertEquals("http://www.ejuhang.com", factory.getUrl("moduleEdit").getServerUrl());
        assertEquals("{moduleInstanceId}", factory.getUrl("moduleEdit").getPath());
        assertEquals(1, factory.getUrl("moduleEdit").getTokens().size());
        assertEquals("http://www.ejuhang.com/2",factory.getUrl("moduleEdit").addQueryData("moduleInstanceId",2).toString());
    }

    @Test
    public void testHome() {
        URLBrokerFactory factory = new URLBrokerFactory();
        factory.setUrlConfigName("url.xml");
        factory.init();
        assertEquals("http://login.kariqu.com/krq",factory.getUrl("home").toString());
        assertEquals("http://www.taobao.com",factory.getUrl("test").toString());
    }


}
