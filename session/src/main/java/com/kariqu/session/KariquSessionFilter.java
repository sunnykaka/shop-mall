package com.kariqu.session;

import com.kariqu.session.config.SessionConfig;
import com.kariqu.session.config.SessionConfigFactory;
import com.kariqu.session.store.CacheStore;
import com.kariqu.session.store.CookieStore;
import com.kariqu.session.util.SessionConstants;
import com.kariqu.session.util.SessionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * session框架的过滤器
 * 需要配置在最前面
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-17 下午6:51
 */
public class KariquSessionFilter implements Filter {

    private static Log logger = LogFactory.getLog(KariquSessionFilter.class);


    private SessionStoreFactory sessionStoreFactory;

    /**
     * session框架配置信息
     */
    private SessionConfig sessionConfig;

    private FilterConfig filterConfig;

    private static final String DEFAULT_CONFIG_FILE_NAME = "session-config.xml";

    /**
     * 用于配置是否需要buffer响应,设置为buffer以后等最后刷新到输出流，如果不设置
     * 则利用容器或者MVC框架的机制去刷新输出流
     */
    private boolean needResponseBuffered;

    /**
     * 配置不需要进行session过滤的URL
     */
    private String[] forbiddenUrlSuffixes;

    /**
     * 是否需要登录检测
     */
    private boolean needLoginCheck;

    /**
     * 默认的session超期时间
     */
    private int sessionExpireTime = 3600;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        initSessionStoreFactory(filterConfig);
        initSessionConfig(filterConfig);
    }

    private void initSessionConfig(FilterConfig filterConfig) {
        String configFileName = filterConfig.getInitParameter("configFileName");
        if (StringUtils.isEmpty(configFileName)) {
            configFileName = DEFAULT_CONFIG_FILE_NAME;
        }
        sessionConfig = SessionConfigFactory.readSessionConfig(configFileName);
        sessionConfig.init(filterConfig);
        needResponseBuffered = "true".equalsIgnoreCase(filterConfig.getInitParameter("needResponseBuffered"));
        if (StringUtils.isNotBlank(filterConfig.getInitParameter("forbiddenUrlSuffixes"))) {
            forbiddenUrlSuffixes = filterConfig.getInitParameter("forbiddenUrlSuffixes").split(",");
        }
        needLoginCheck = "true".equalsIgnoreCase(filterConfig.getInitParameter("needLoginCheck"));
        final String sessionExpireTime = filterConfig.getInitParameter("sessionExpireTime");
        if (StringUtils.isNotBlank(sessionExpireTime)) {
            try {
                this.sessionExpireTime = Integer.parseInt(sessionExpireTime);
            } catch (NumberFormatException e) {
                logger.error("session框架超期时间设置有误，请确认是否是数字类型", e);
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        KariquSessionServletRequest krqRequest = new KariquSessionServletRequest((HttpServletRequest) request);
        KariquSessionServletResponse krqResponse = new KariquSessionServletResponse((HttpServletResponse) response);
        if (letitgo(request, response, chain, krqRequest)) return;
        if (needResponseBuffered) {
            if (logger.isDebugEnabled()) {
                logger.debug("session framework responseBuffered is on");
            }
            krqResponse.setWriterBuffered(true);
        }
        KariquSession kariquSession = createKariquSession(krqRequest, krqResponse);
        if (needLoginCheck) {
            doLoginCheck(kariquSession);
        }
        try {
            chain.doFilter(krqRequest, krqResponse);
            if (null != kariquSession) {
                if (logger.isDebugEnabled()) {
                    logger.debug("session framework start to commit session--" + "kariquSession.commit");
                }
                kariquSession.commit();
            }
        } catch (Exception ex) {
            logger.error("session framework occur exception", ex);
            throw new RuntimeException(ex);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("session framework start to commit buffer--" + "krqResponse.commitBuffer");
            }
            krqResponse.commitBuffer();
        }
    }

    /**
     * 进行登录校验
     *
     * @param kariquSession
     */
    private void doLoginCheck(KariquSession kariquSession) {
        if (SessionUtils.isLogin(kariquSession)) {
            String lastTime = (String) kariquSession.getAttribute(SessionConstants.LAST_VISIT_TIME);
            int lastVisitTime = 0;
            if (lastTime != null) {
                try {
                    lastVisitTime = Integer.parseInt(lastTime);
                } catch (NumberFormatException e1) {
                    lastVisitTime = 0;
                }
            }
            if ((System.currentTimeMillis() / 1000 - lastVisitTime) >= sessionExpireTime) {
                kariquSession.invalidate();
            }
        }
        // 更新上次访问时间
        kariquSession.setAttribute(SessionConstants.LAST_VISIT_TIME, Long.toString(System.currentTimeMillis() / 1000));
    }

    /**
     * 判断是否需要经过sessionFilter处理
     *
     * @param request
     * @param response
     * @param chain
     * @param krqRequest
     * @return
     * @throws IOException
     * @throws ServletException
     */
    private boolean letitgo(ServletRequest request, ServletResponse response, FilterChain chain, KariquSessionServletRequest krqRequest) throws IOException, ServletException {
        if (null == forbiddenUrlSuffixes) {
            return false;
        }
        String requestURI = krqRequest.getRequestURI();
        for (String forbiddenSuffix : forbiddenUrlSuffixes) {
            if (StringUtils.isNotBlank(requestURI)) {
                if (requestURI.endsWith(forbiddenSuffix)) {
                    chain.doFilter(request, response);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }


    private KariquSession createKariquSession(KariquSessionServletRequest krqRequest, KariquSessionServletResponse krqReponse) {
        KariquSession kariquSession = new KariquSession(sessionConfig, filterConfig.getServletContext());
        kariquSession.setSessionStores(sessionStoreFactory.createSessionStores(krqRequest, krqReponse));
        krqRequest.setSession(kariquSession);
        krqReponse.setSession(kariquSession);
        kariquSession.init();
        return kariquSession;
    }

    /**
     * init sessionStoreFactory
     *
     * @param filterConfig
     */
    private void initSessionStoreFactory(FilterConfig filterConfig) {
        sessionStoreFactory = new SessionStoreFactory(new HashMap<StoreType, Class<? extends SessionStore>>() {
            {
                put(StoreType.cookie, CookieStore.class);
                put(StoreType.cache, CacheStore.class);
            }
        });
    }
}
