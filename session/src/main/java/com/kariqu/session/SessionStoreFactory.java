package com.kariqu.session;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * SessionStore factory,which is used to create sessionStore instance.
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-18 下午6:32
 */
public class SessionStoreFactory {

    private Map<StoreType, Class<? extends SessionStore>> storeTypeClassMap = new HashMap<StoreType, Class<? extends SessionStore>>();

    public SessionStoreFactory(HashMap<StoreType, Class<? extends SessionStore>> storeTypeClassMap) {
        this.storeTypeClassMap = storeTypeClassMap;
    }

    public Map<StoreType, SessionStore> createSessionStores(KariquSessionServletRequest krqRequest, KariquSessionServletResponse krqReponse) {

        Map<StoreType, SessionStore> sessionStores = new HashMap<StoreType, SessionStore>();
        for (Map.Entry<StoreType, Class<? extends SessionStore>> entry : storeTypeClassMap.entrySet()) {
            try {
                sessionStores.put(entry.getKey(), entry.getKey().newSessionStore(krqRequest, krqReponse));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sessionStores;

    }
}
