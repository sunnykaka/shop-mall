package com.kariqu.usercenter;

import com.kariqu.common.oauth.jointlogin.OuterUserInfo;
import com.kariqu.common.oauth.jointlogin.services.JointLoginService;
import com.kariqu.common.oauth.jointlogin.services.impl.*;
import com.kariqu.usercenter.domain.AccountType;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Oauth的外部服务类
 * <p/>
 * 项目中要把它配置在spring容器中
 */

public class Oauth implements InitializingBean {

    private String jointLoginConfigName;

    private static Map<AccountType, JointLoginService> JoinLoginServiceHolder = new HashMap<AccountType, JointLoginService>();

    public static JointLoginService join(AccountType type) {
        JointLoginService jointLoginService = JoinLoginServiceHolder.get(type);
        if (jointLoginService == null) {
            jointLoginService = new JointLoginService() {
                @Override
                public OuterUserInfo userInfo(String code) {
                    return null;
                }

                @Override
                public String auth() {
                    return "";
                }
            };
        }
        return jointLoginService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Properties props = new Properties();
        props.load(this.getClass().getClassLoader().getResourceAsStream(jointLoginConfigName));

        JoinLoginServiceHolder.put(AccountType.QQ, new QQServiceImpl(props));
        JoinLoginServiceHolder.put(AccountType.RenRen, new RenRenServiceImpl(props));
        JoinLoginServiceHolder.put(AccountType.Sina, new SinaServiceImpl(props));
        JoinLoginServiceHolder.put(AccountType.TaoBao, new TaoBaoServiceImpl(props));
        JoinLoginServiceHolder.put(AccountType.JD, new JDServiceImpl(props));
//        JoinLoginServiceHolder.put(AccountType.WeiXin, new WeiXinServiceImpl(props));

    }


    public String getJointLoginConfigName() {
        return jointLoginConfigName;
    }

    public void setJointLoginConfigName(String jointLoginConfigName) {
        this.jointLoginConfigName = jointLoginConfigName;
    }

}
