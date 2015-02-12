package com.kariqu.searchengine.domain;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by play.liu on 2014/6/30.
 * Description: 自动补全结果对象
 */
public class AutoCompeleteResult {

     public AutoCompeleteResult(){

     }
     public AutoCompeleteResult(List<SuggestResult> list){
         this.textSuggestResults = list;
     }
     public AutoCompeleteResult(String[] terms){
         List<SuggestResult> list = Lists.newArrayList();
         for (String term : terms) {
             SuggestResult suggestResult = new SuggestResult(term,0);
             list.add(suggestResult);
         }
         this.textSuggestResults = list;
     }
    //查询文字智能提示
    private List<SuggestResult> textSuggestResults = Lists.newArrayList();
    //根据用户输入的文字对分类(categoryId)facet结果，只取前两个结果facet
    private List<Long> categoryIdSuggestResults = Lists.newArrayList();

    public List<Long> getCategoryIdSuggestResults() {
        return categoryIdSuggestResults;
    }

    public void setCategoryIdSuggestResults(List<Long> categoryIdSuggestResults) {
        this.categoryIdSuggestResults = categoryIdSuggestResults;
    }

    public List<SuggestResult> getTextSuggestResults() {
        return textSuggestResults;
    }

    public void setTextSuggestResults(List<SuggestResult> textSuggestResults) {
        this.textSuggestResults = textSuggestResults;
    }

    public String toString() {
        return "{categoryIds:" + categoryIdSuggestResults + ", terms: " + textSuggestResults + "}";
    }
}
