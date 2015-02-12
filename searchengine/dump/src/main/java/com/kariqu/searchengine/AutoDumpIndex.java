package com.kariqu.searchengine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.List;

/**
 * 自动dump商品索引
 * User: Asion
 * Date: 12-8-1
 * Time: 下午2:16
 */

public class AutoDumpIndex {

    private static final Log logger = LogFactory.getLog(AutoDumpIndex.class);

    /**
     * 标志是否停止dump,
     */
    private boolean forbid = true;

    @Autowired
    private SearchEngineUpdateService searchEngineUpdateService;

    private List<DumpPreHandler> preHandlerList;

    /**
     * 10分钟执行一次
     * 商品增量dump,类目全量dump
     */
   // @Scheduled(fixedDelay = 10 * 60 * 1000)
    //改成用操作系统的crontab做定时任务全量dump
    public void execute() {
        if (!forbid) {
            try {
                if (preHandlerList != null) {
                    for (DumpPreHandler dumpPreHandler : preHandlerList) {
                        dumpPreHandler.process();
                    }
                }
                searchEngineUpdateService.doProductDataDump();
                searchEngineUpdateService.doCategoryDataDump();
                logger.debug("发送了一次增量dump");
            } catch (IOException e) {
                logger.error("dump商品索引的时候出现异常", e);
            }
        }
    }

    public boolean isForbid() {
        return forbid;
    }

    public void setForbid(boolean forbid) {
        this.forbid = forbid;
    }

    public List<DumpPreHandler> getPreHandlerList() {
        return preHandlerList;
    }

    public void setPreHandlerList(List<DumpPreHandler> preHandlerList) {
        this.preHandlerList = preHandlerList;
    }

}
