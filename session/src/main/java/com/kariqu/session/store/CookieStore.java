package com.kariqu.session.store;

import com.kariqu.session.KariquCookie;
import com.kariqu.session.KariquSessionServletRequest;
import com.kariqu.session.KariquSessionServletResponse;
import com.kariqu.session.SessionStore;
import com.kariqu.session.config.SessionConfig;
import com.kariqu.session.config.SessionConfigEntry;
import com.kariqu.session.util.BlowfishUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 *
 * The cookie implement of sessionStore,this store use the cookie to
 * store the session info.
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-18 下午6:09
 */
public class CookieStore implements SessionStore {

    private static Log logger = LogFactory.getLog(CookieStore.class);

    private static final String URL_ENCODING = "UTF-8";
    private static final char COMBINE_SEPARATOR = '&';
    private static final char KEY_VALUE_SEPARATOR = '=';
    /**
     * undecodedCookies means the navtive cookies,which directly comes from HttpRequest,
     * the session Framework need to process it deeply.
     */
    private Map<String, String> undecodedCookies = new HashMap<String, String>();
    private Map<String, Attribute> attributes = new HashMap<String, Attribute>();
    private final KariquSessionServletRequest krqRequest;
    private final KariquSessionServletResponse krqResponse;
    /**
     * record the modified cookie.
     */
    private Set<String> modifiedCookies = new HashSet<String>();
    private String DEFAULT_PATH = "/";
    private static final String BLOWFISH_ENCRYPT_KEY = "blowfish_key";
    private String blowfishKey;


    public CookieStore(KariquSessionServletRequest krqRequest, KariquSessionServletResponse krqResponse) {
        this.krqRequest = krqRequest;
        this.krqResponse = krqResponse;
    }


    @Override
    public void setAttribute(SessionConfigEntry sessionConfigEntry, Object value) {
        final String stringValue = ObjectUtils.toString(value, null);
        this.attributes.put(sessionConfigEntry.getName(), new Attribute(sessionConfigEntry, stringValue));
        this.modifiedCookies.add(sessionConfigEntry.getName());
    }

    @Override
    public Object getAttribute(SessionConfigEntry sessionConfigEntry) {
        Attribute value = this.attributes.get(sessionConfigEntry.getName());
        if (null == value) {
            if (sessionConfigEntry.isCommbine()) {
                Map<String, Attribute> cookies = decodeCombinedCookie(sessionConfigEntry);
                attributes.putAll(cookies);
                value = cookies.get(sessionConfigEntry.getName());
            } else {
                value = decodeSimpleCookie(sessionConfigEntry, undecodedCookies);
                attributes.put(sessionConfigEntry.getName(), value);
            }
        }
        return value.getValue();
    }

    /**
     * parse the combined cookies
     *
     * @param sessionConfigEntry
     * @return the map which contains the uncombined cookies.
     */
    private Map<String, Attribute> decodeCombinedCookie(SessionConfigEntry sessionConfigEntry) {
        Map<String, Attribute> cookies = new HashMap<String, Attribute>();
        if (!sessionConfigEntry.isCommbine())
            throw new UnsupportedOperationException("not supporting decode the non combined cookie");
        final SessionConfig sessionConfig = getSessionConfig();
        if (null == sessionConfig)
            throw new IllegalStateException("sessionConfig is null,pls config it.");
        final Collection<SessionConfigEntry> configEntries = sessionConfig.getCombinedConfigEntries(sessionConfigEntry.getCombineKey());
        if (null == configEntries) {
            throw new IllegalStateException("the combined cookie key not config,config it first pls.");
        }
        Map<String, String> unCombinedCookies = unCombinedCookeValue(undecodedCookies.get(sessionConfigEntry.getCombineKey()));
        for (SessionConfigEntry configEntry : configEntries) {
            /**
             * if the cookie has been modified,the value will be the modified value,don't need
             * to decoded from cookies
             */
            if (modifiedCookies.contains(configEntry.getName()))
                continue;
            cookies.put(configEntry.getName(), decodeSimpleCookie(configEntry, unCombinedCookies));
        }
        return cookies;

    }

    /**
     * parse combined cookie
     *
     * @param undecodedCookieValue
     * @return the uncombined cookie map,the key is the single cookie name,
     *         the value is the single cookie value.
     */
    private Map<String, String> unCombinedCookeValue(String undecodedCookieValue) {
        Map<String, String> unCombinedCookies = new HashMap<String, String>();
        final String[] cookies = StringUtils.split(undecodedCookieValue, COMBINE_SEPARATOR);
        if (!ArrayUtils.isEmpty(cookies)) {
            for (String cookie : cookies) {
                final String[] keyValue = StringUtils.split(cookie, KEY_VALUE_SEPARATOR);
                if (ArrayUtils.getLength(keyValue) == 2) {
                    unCombinedCookies.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return unCombinedCookies;

    }


    private Attribute decodeSimpleCookie(SessionConfigEntry sessionConfigEntry, Map<String, String> undecodedCookies) {
        final String undecodedCookieValue = undecodedCookies.get(sessionConfigEntry.getKey());
        return new Attribute(sessionConfigEntry, decodeCookieValue(undecodedCookieValue, sessionConfigEntry));

    }

    private void initCookies() {
        Cookie[] cookies = getCookiesFromRequest();
        if (null == cookies) {
            return;
        }
        for (Cookie cookie : cookies) {
            undecodedCookies.put(cookie.getName(), cookie.getValue());
        }
    }


    private Cookie[] getCookiesFromRequest() {
        if (krqRequest == null)
            return new Cookie[0];
        return krqRequest.getCookies();
    }

    @Override
    public void commit() {
        String[] tempCookieArray = modifiedCookies.toArray(new String[0]);
        /*the combined cookie may be processed during the process of combined cookie*/
        for (String modifiedCookieName : tempCookieArray) {
            if (modifiedCookies.contains(modifiedCookieName)) {
                final SessionConfigEntry sessionConfigEntry = getSessionConfig().getSessionConfigEntry(modifiedCookieName);
                if (sessionConfigEntry == null) {
                    logger.error("write the cookie,name=" + modifiedCookieName + " which can't be configured");
                    continue;
                }
                KariquCookie cookie = buildCookie(sessionConfigEntry);
                if (null == cookie) {
                    continue;
                }
                krqResponse.addCookie(cookie);
            }
        }
        /**
         * remove the blowfishUtils threadlocal cache
         */
        BlowfishUtils.remove();

    }


    private KariquCookie buildCookie(SessionConfigEntry sessionConfigEntry) {
        if (sessionConfigEntry.isCommbine()) {
            return buildCombinedCookie(sessionConfigEntry);
        } else {
            return buildSingleCookie(sessionConfigEntry);
        }
    }


    private KariquCookie buildSingleCookie(SessionConfigEntry sessionConfigEntry) {
        final Object cookieValue = getAttribute(sessionConfigEntry);
        boolean isRemoved = null == cookieValue;
        String value = encodeCookieValue(cookieValue == null ? StringUtils.EMPTY : cookieValue.toString(), sessionConfigEntry);
        /**
         * it just remove the cookie which has existed.
         */
        if (isRemoved && !undecodedCookies.containsKey(sessionConfigEntry.getKey())) {
            return null;
        }
        removeFromModifiedCookie(sessionConfigEntry);
        return buildCookie(sessionConfigEntry, isRemoved, sessionConfigEntry.getKey(), value);

    }

    private KariquCookie buildCookie(SessionConfigEntry sessionConfigEntry, boolean removed, String key, String value) {
        String path = sessionConfigEntry.getPath();
        boolean isHttpOnly = sessionConfigEntry.isHttpOnly();
        int maxAge = removed ? 0 : sessionConfigEntry.getLifeCycle();
        KariquCookie kariquCookie = new KariquCookie(key, value);
        kariquCookie.setHttpOnly(isHttpOnly);
        if (StringUtils.isNotBlank(sessionConfigEntry.getDomain())) {
            kariquCookie.setDomain(sessionConfigEntry.getDomain());
        }
        kariquCookie.setPath(StringUtils.isBlank(path) ? DEFAULT_PATH : path);
        kariquCookie.setMaxAge(maxAge);
        return kariquCookie;
    }

    private KariquCookie buildCombinedCookie(SessionConfigEntry sessionConfigEntry) {
        final String combineKey = sessionConfigEntry.getCombineKey();
        final Collection<SessionConfigEntry> combinedConfigEntries = getSessionConfig().getCombinedConfigEntries(combineKey);
        StringBuilder combinedCookieValue = new StringBuilder();
        int i = 0;
        for (SessionConfigEntry configEntry : combinedConfigEntries) {
            removeFromModifiedCookie(configEntry);
            final Object attribute = getAttribute(configEntry);
            String cookieValue = attribute == null ? null : attribute.toString();
            final String encodedValue = encodeCookieValue(cookieValue, configEntry);
            if (null == encodedValue) {
                continue;
            }
            if (i > 0) {
                combinedCookieValue.append("&");
            }
            combinedCookieValue.append(configEntry.getKey()).append('=').append(encodedValue);
            i++;
        }
        final String value = combinedCookieValue.toString();
        return buildCookie(getSessionConfig().getSessionConfigEntry(combineKey), StringUtils.isBlank(value), combineKey, value);
    }

    private void removeFromModifiedCookie(SessionConfigEntry configEntry) {
        this.modifiedCookies.remove(configEntry.getName());
    }

    public SessionConfig getSessionConfig() {
        return krqRequest.getSession().getSessionConfig();
    }


    private String encodeCookieValue(String cookieValue, SessionConfigEntry sessionConfigEntry) {
        if (StringUtils.isBlank(cookieValue)) {
            return cookieValue;
        }
        if (sessionConfigEntry.isEncrypt()) {
            cookieValue = BlowfishUtils.encryptBlowfish(cookieValue, getBlowfishKey());
        }

        try {
            cookieValue = URLEncoder.encode(cookieValue, URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error("urlencode failure,the value is :" + cookieValue, e);
            return null;
        }
        return cookieValue;

    }


    private String decodeCookieValue(String cookieValue, SessionConfigEntry sessionConfigEntry) {
        if (StringUtils.isBlank(cookieValue)) {
            return cookieValue;
        }
        try {
            cookieValue = URLDecoder.decode(cookieValue, URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error("urldecode failure,the value is :" + cookieValue, e);
            return null;
        }
        if (sessionConfigEntry.isEncrypt()) {
            cookieValue = BlowfishUtils.decryptBlowfish(cookieValue, getBlowfishKey());
        }

        return cookieValue;
    }

    @Override
    public void init() {
        initCookies();
        initEncryptKey();
    }

    private void initEncryptKey() {
        this.blowfishKey = getBlowfishKey();

    }

    public String  getBlowfishKey() {
        if (!StringUtils.isBlank(blowfishKey)) {
            return blowfishKey;
        }
        final String blowfish_key = this.getSessionConfig().getGlobalConfigInfo(BLOWFISH_ENCRYPT_KEY);
        if (StringUtils.isBlank(blowfish_key)) {
            throw new IllegalStateException("blowfish key not configured,the blowfish_key must be configured");
        }
        this.blowfishKey = BlowfishUtils.decryptBlowfish(blowfish_key, "tiger");
        return blowfishKey;
    }

    private class Attribute {

        SessionConfigEntry sessionConfigEntry;

        Object value;

        private Attribute(SessionConfigEntry sessionConfigEntry, Object value) {
            this.sessionConfigEntry = sessionConfigEntry;
            this.value = value;
        }

        public SessionConfigEntry getSessionConfigEntry() {
            return sessionConfigEntry;
        }

        public Object getValue() {
            return value;
        }
    }
}
