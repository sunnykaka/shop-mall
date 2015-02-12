package com.kariqu.searchengine;

import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Transformer;

import java.util.Map;

/**
 * User: Asion
 * Date: 11-12-13
 * Time: 下午5:05
 */
public class KeyWordTransformer extends Transformer {

    @Override
    public Object transformRow(Map<String, Object> row, Context context) {
        String keyWord = (String) row.get("keyWord");
        String[] split = keyWord.split(",|，");
        StringBuilder sb = new StringBuilder();
        for (String key : split) {
            sb.append("(" + key + ")");
        }
        row.put("keyWord", sb.toString());
        return row;
    }
}
