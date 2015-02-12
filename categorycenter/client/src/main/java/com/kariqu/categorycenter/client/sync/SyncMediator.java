package com.kariqu.categorycenter.client.sync;

import com.kariqu.categorycenter.domain.service.CategorySyncService;

/**
 * 同步类目数据，负责将类目数据从数据库同步到内存缓存中
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-7-13 上午11:32
 */
public interface SyncMediator {

    SyncResult sync(SyncRequest syncRequest);

    void injectDomainCategorySyncService(CategorySyncService syncService);
}
