package com.kariqu.categorycenter.domain.util;

import org.junit.Test;

import java.util.List;

/**
 * User: Asion
 * Date: 11-9-19
 * Time: 下午12:05
 */
public class PidVidJsonUtilTest {

    @Test
    public void testPidVidJson() {
        PidVid pidvid = PidVidJsonUtil.restore("{\"pidvid\":[25769803907,12884902017,30064771205,21474836485,107374182535,107374182536,111669149833,111669149834,111669149835],\"multiPidVidMap\":{\"25\":[107374182535,107374182536],\"26\":[111669149833,111669149834,111669149835]},\"singlePidVidMap\":{\"3\":12884902017,\"5\":21474836485,\"6\":25769803907,\"7\":30064771205}}");
        System.out.println(pidvid.getPidvid());
        System.out.println(pidvid.getSinglePidVidMap());
        System.out.println(pidvid.getMultiPidVidMap());
    }
}
