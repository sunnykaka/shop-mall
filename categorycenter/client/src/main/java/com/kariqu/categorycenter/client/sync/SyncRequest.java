package com.kariqu.categorycenter.client.sync;

/**
 * 同步请求对象
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-7-13 上午11:43
 */
public class SyncRequest {

    private SyncType syncType;

    public SyncRequest(SyncType syncType) {
        this.syncType = syncType;
    }

    public SyncType getSyncType() {
        return syncType;
    }
}
