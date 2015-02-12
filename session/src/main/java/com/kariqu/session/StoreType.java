package com.kariqu.session;

import com.kariqu.session.store.CacheStore;
import com.kariqu.session.store.CookieStore;

/**
 * 会话数据的保存类型
 * 一种是保存在cookie中，一种是保存在缓存中
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-17 下午7:18
 */
public enum StoreType {

    cookie {
        @Override
        SessionStore newSessionStore(KariquSessionServletRequest krqRequest, KariquSessionServletResponse krqReponse) {
            return new CookieStore(krqRequest, krqReponse);
        }
    },

    /**
     * cache store
     */
    cache {
        @Override
        SessionStore newSessionStore(KariquSessionServletRequest krqRequest, KariquSessionServletResponse krqReponse) {
            return new CacheStore(krqRequest, krqReponse);
        }
    };

    abstract SessionStore newSessionStore(KariquSessionServletRequest krqRequest, KariquSessionServletResponse krqReponse);
}
