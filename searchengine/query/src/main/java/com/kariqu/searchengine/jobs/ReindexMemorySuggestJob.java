package com.kariqu.searchengine.jobs;

import com.kariqu.searchengine.service.SearchEngineQueryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by play.liu on 2014/7/3.
 * Description:
 */
public class ReindexMemorySuggestJob {
    private static final Log logger = LogFactory.getLog(ReindexMemorySuggestJob.class);

    private SearchEngineQueryService searchEngineQueryService;


    public SearchEngineQueryService getSearchEngineQueryService() {
        return searchEngineQueryService;
    }

    public void setSearchEngineQueryService(SearchEngineQueryService searchEngineQueryService) {
        this.searchEngineQueryService = searchEngineQueryService;
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000)//10分钟执行一次
    public void reIndex(){
        logger.info("更新memory suggest索引");
        searchEngineQueryService.reIndexMemorySuggest();

    }

}
