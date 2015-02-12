package com.kariqu.searchengine;

import com.google.common.collect.Maps;
import com.kariqu.categorycenter.domain.util.PropertyValueUtil;
import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataSource;
import org.apache.solr.handler.dataimport.Transformer;
import com.kariqu.categorycenter.domain.util.PidVidJsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;

/**
 * 转换商品属性中的json pidvid
 * User: Asion
 * Date: 11-9-15
 * Time: 下午1:54
 */
public class PidVidTransformer extends Transformer {

    private DataSource productDump = null;
    private static final Logger LOG = LoggerFactory.getLogger(PidVidTransformer.class);

    @Override
    public Object transformRow(Map<String, Object> row, Context context) {
        try {
            productDump.getData("select 1");
        }catch (Exception e){
            productDump = context.getDataSource("productDump");
            LOG.error("创建连接");
        }
        LOG.info("dump pidvid");
        Map<String,String> propertyMap = buildPropertyMap(productDump);
        Map<String,String> valueMap = buildValueMap(productDump);
        List<String> produceAreas  = getAllowProduceArea(productDump);

        String properties = (String) row.get("json");
        List<Long> pidvids = PidVidJsonUtil.restore(properties).getPidvid();
        String produceAreaName = null;
        for (Long pidvid : pidvids) {
            PropertyValueUtil.PV pv = PropertyValueUtil.parseLongToPidVid(pidvid);
            String propertyName = propertyMap.get(String.valueOf(pv.pid));
            if(propertyName != null && "品牌所属地".equals(propertyName.trim())){
                String valueName = valueMap.get(String.valueOf(pv.vid));
                if(valueName != null && produceAreas.contains(valueName)) {
                    produceAreaName = valueName;
                }
            }
        }
        row.put("produceArea",produceAreaName == null ?"" : produceAreaName);
        row.put("pidvid", pidvids);
        return row;
    }


    private Map<String,String> buildPropertyMap(DataSource dataSource){
        String selectAllPropterty = "select id,name from property";
        Iterator<Map<String,Object>> mapIterator = (Iterator<Map<String,Object>>)dataSource.getData(selectAllPropterty);
        Map<String,String>  propertyIdNameMap = Maps.newHashMap();
        while(mapIterator.hasNext()){
            Map<String,Object> tmp = mapIterator.next();
            propertyIdNameMap.put(String.valueOf(tmp.get("id")),String.valueOf(tmp.get("name")));
        }
        return propertyIdNameMap;
    }
    private Map<String,String> buildValueMap(DataSource dataSource){
        String selectAllPropterty = "select id,valueName from value";
        Iterator<Map<String,Object>> mapIterator = (Iterator<Map<String,Object>>)dataSource.getData(selectAllPropterty);
        Map<String,String>  valueIdNameMap = Maps.newHashMap();
        while(mapIterator.hasNext()){
            Map<String,Object> tmp = mapIterator.next();
            valueIdNameMap.put(String.valueOf(tmp.get("id")),String.valueOf(tmp.get("valueName")));
        }
        return valueIdNameMap;
    }

    private List<String> getAllowProduceArea(DataSource dataSource){
        String sql = "select constValue from Const where constKey='productAddress'";
        Iterator<Map<String,Object>> result = (Iterator<Map<String,Object>>)dataSource.getData(sql);
        while(result.hasNext()){
            String[] areas = result.next().get("constValue").toString().split(",");
            List<String> list = Arrays.asList(areas);
            return list;
        }
        return new ArrayList<String>();
    }
}
