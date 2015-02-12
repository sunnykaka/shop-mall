package com.kariqu.session.config;

import com.kariqu.session.StoreType;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-6-14 下午5:14
 */
public class SessionConfigFactoryTest {

    @Test
    public void testReadSessionConfig() throws Exception {
        final SessionConfig sessionConfig = SessionConfigFactory.readSessionConfig("session-config.xml");
        assertTrue(sessionConfig.getAttributeNames().size() > 0);
        assertEquals("kariqu.com", sessionConfig.getSessionConfigEntry("sessionId").getDomain());
        assertEquals(-1, sessionConfig.getSessionConfigEntry("sessionId").getLifeCycle());
        assertEquals(StoreType.cookie, sessionConfig.getSessionConfigEntry("sessionId").getStoreType());
    }
}
