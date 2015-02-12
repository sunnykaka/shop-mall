package com.kariqu.buyer.web.controller.search;

import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.common.cache.CacheService;
import com.kariqu.common.trie.Trie;
import com.kariqu.searchengine.domain.AutoCompeleteResult;
import com.kariqu.searchengine.domain.CategoryResult;
import com.kariqu.searchengine.domain.SuggestResult;
import com.kariqu.searchengine.exception.SearchException;
import com.kariqu.searchengine.service.SearchEngineQueryService;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 搜索建议控制器
 * 支持拼音搜索
 * User: Asion
 * Date: 12-6-8
 * Time: 下午1:26
 */
@Controller
public class SuggestController {

    private static Log logger = LogFactory.getLog(SuggestController.class);

    @Autowired
    private SearchEngineQueryService searchEngineQueryService;

    /**
     * 输入联想，自动补全
     * @param keyword 用户输入的关键字
     * @param size 类目提示最大返回条数,参考值---京东此值为:2
     * @param count 联想词条最大返回条数,参考值--京东此值是:9
     * @return 自动补全结果
     */
    @RequestMapping(value = "/suggest")
    public void suggest(@RequestParam("keyword") String keyword, String size,String count, HttpServletResponse response) throws IOException {
        //现在将facetSize(类目提示最大返回条数)的个数设置为0，以后有需要再改变
        int facetSize = NumberUtils.toInt(size, 0);
        int commonTermCount = NumberUtils.toInt(count, 9);
        AutoCompeleteResult autoCompeleteResult = searchEngineQueryService.suggest(keyword, facetSize, commonTermCount);
        List<SuggestResult> textSuggestResultList = autoCompeleteResult.getTextSuggestResults();
        List<String> textSuggests = new ArrayList<String>();
        for(SuggestResult suggestResult : textSuggestResultList) {
            textSuggests.add(suggestResult.getTerm());
        }
        new JsonResult(true).addData("textSuggestResults", textSuggests).toJson(response);
    }

}
