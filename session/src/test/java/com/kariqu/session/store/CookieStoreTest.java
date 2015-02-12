package com.kariqu.session.store;

import com.kariqu.session.*;
import com.kariqu.session.config.SessionConfigEntry;
import org.junit.Before;
import org.junit.Test;
import org.unitils.easymock.annotation.Mock;

import javax.servlet.http.Cookie;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.unitils.easymock.EasyMockUnitils.replay;

/**
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-20 下午10:44
 */
public class CookieStoreTest extends SessionBaseTest {

    @Mock
    private KariquSessionServletRequest requestMock;

    @Mock
    private KariquSessionServletResponse reponseMock;

    private CookieStore cookieStore;

    private KariquSession sessionMock;

    private Cookie[] cookies;


    @Before
    public void init() {
        sessionMock = new KariquSession(sessionConfigMock, null);
        cookies = new Cookie[]{
                new KariquCookie("s", "sessionIdValue"),
                new KariquCookie("il", "true"),
                new KariquCookie("combinedKey", "key1=value1&key2=value2")
        };
        requestMock.setSession(sessionMock);
        expect(requestMock.getCookies()).andReturn(cookies);
        expect(requestMock.getSession()).andReturn(sessionMock).anyTimes();
        replay();
        requestMock.setSession(sessionMock);
        cookieStore = new CookieStore(requestMock, reponseMock);
        cookieStore.init();
    }

    @Test
    public void testGetAttribute() throws Exception {
        assertEquals("sessionIdValue", cookieStore.getAttribute(sessionConfigMock.getSessionConfigEntry("sessionId")));
        assertEquals("true", cookieStore.getAttribute(sessionConfigMock.getSessionConfigEntry("isLogin")));
        assertEquals("value1", cookieStore.getAttribute(sessionConfigMock.getSessionConfigEntry("name1")));
        assertEquals("value2", cookieStore.getAttribute(sessionConfigMock.getSessionConfigEntry("name2")));
    }

    @Test
    public void testSetAttribute() throws Exception {
        final SessionConfigEntry isLoginEntry = sessionConfigMock.getSessionConfigEntry("isLogin");
        cookieStore.setAttribute(isLoginEntry, "false");
        assertEquals("false", cookieStore.getAttribute(isLoginEntry));
    }

}
