package com.kariqu.session;

import com.kariqu.session.config.DefaultSessionConfig;
import com.kariqu.session.config.SessionConfigEntry;
import org.junit.BeforeClass;
import org.unitils.UnitilsJUnit4;

/**
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-20 下午1:44
 */
public abstract class SessionBaseTest extends UnitilsJUnit4 {

    protected static DefaultSessionConfig sessionConfigMock;

    @BeforeClass
    public static void initConfig() {
        sessionConfigMock = new DefaultSessionConfig();
        SessionConfigEntry configEntryOne = new SessionConfigEntry();
        configEntryOne.setName("sessionId");
        configEntryOne.setKey("s");
        configEntryOne.setStoreType(StoreType.cookie);

        SessionConfigEntry configEntryTwo = new SessionConfigEntry();
        configEntryTwo.setName("isLogin");
        configEntryTwo.setKey("il");
        configEntryTwo.setStoreType(StoreType.cookie);

        SessionConfigEntry combinedEntryOne = new SessionConfigEntry();
        combinedEntryOne.setCombineKey("combinedKey");
        combinedEntryOne.setName("name1");
        combinedEntryOne.setKey("key1");
        combinedEntryOne.setStoreType(StoreType.cookie);
        combinedEntryOne.setCommbine(true);

        SessionConfigEntry combinedEntryTwo = new SessionConfigEntry();
        combinedEntryTwo.setCombineKey("combinedKey");
        combinedEntryTwo.setName("name2");
        combinedEntryTwo.setKey("key2");
        combinedEntryTwo.setStoreType(StoreType.cookie);
        combinedEntryTwo.setCommbine(true);

        sessionConfigMock.addSessionConfigEntry(configEntryOne);
        sessionConfigMock.addSessionConfigEntry(configEntryTwo);
        sessionConfigMock.addSessionConfigEntry(combinedEntryOne);
        sessionConfigMock.addSessionConfigEntry(combinedEntryTwo);

        sessionConfigMock.addGlobalInfo("blowfish_key","dhDMNcMlHx7DR0Y=");

    }
}
