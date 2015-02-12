package com.kariqu.session;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Tiger
 * @version 1.0
 * @since 13-4-10 上午10:20
 */
public interface KariquSessionLoginCallback {

    HttpSession getHttpSession();

    Serializable getUserId();

    void beforeLogin();

    void afterLogin();


}
