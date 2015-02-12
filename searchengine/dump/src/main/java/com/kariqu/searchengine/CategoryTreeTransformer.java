package com.kariqu.searchengine;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kariqu.categorycenter.domain.util.PidVidJsonUtil;
import com.kariqu.categorycenter.domain.util.PropertyValueUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.util.DateUtil;
import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.DataSource;
import org.apache.solr.handler.dataimport.Transformer;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 递归读取商品在类目树上的所有类目ID，后台所有类目层级都建立索引
 * User: Asion
 * Date: 11-6-28
 * Time: 下午1:09
 */
public class CategoryTreeTransformer extends Transformer {
    //得到spring的bean

    private static DataSource productDump = null;
    private Currency currency = Currency.getInstance("CNY");
    private Random random = new Random();

    private static final Logger LOG = LoggerFactory.getLogger(CategoryTreeTransformer.class);

    @Override
    public Object transformRow(Map<String, Object> row, Context context) {
       /* if (row.size() == 1) {
            return row;
        }*/
        //得到数据源

        /*if (null == productDump) {*/
        try {
            productDump.getData("select 1");
        }catch (Exception e){
            if(productDump != null) {
                productDump.close();
            }
            productDump = context.getDataSource("productDump");
            LOG.error("创建连接");
        }
        LOG.debug("dump product");
        /*}*/

        Set<Integer> categoryIds = new HashSet<Integer>();
        Long productId = (Long) row.get("id");
        //当前产品对应的sku
        List<Map<String, Object>> stockKeepingUnits = getSkuIdAndSkuReadableStr(productId, productDump);

        Integer categoryId = (Integer) row.get("categoryId");
        //row.put("leafId", categoryId); //叶子ID
        categoryIds.add(categoryId);
        Map<String,Object> skuMarketingPrice  = null;
        //算评论数
        try {
           skuMarketingPrice =  getActivityPrice(productId,productDump);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String,Object> productPVAndProduceArea = getProductPVAndProduceArea(productId,productDump);
        Iterator<Map<String, Object>> realComment = (Iterator<Map<String, Object>>)productDump.getData("select count(id) memo from valuation where  isDelete = 0 and productId="+productId);
        Iterator<Map<String, Object>> fakeComment = (Iterator<Map<String, Object>>)productDump.getData("select count(id) memo from t_import_valuation where  isDelete = 0 and productId="+productId + " and now()>updateDate");
        long sumComment = 0;
        if(realComment.hasNext()){
            sumComment+=(Long)realComment.next().get("memo");
        }
        if(fakeComment.hasNext()){
            sumComment+=(Long)fakeComment.next().get("memo");
        }
        row.put("valuation",sumComment);
        int loop = categoryId;
        while (true) {
            //循环读取类目表的父亲ID，将ID加入索引
            Iterator<Map<String, Object>> data = (Iterator<Map<String, Object>>) productDump.getData("select parentId from productcategory where id = " + loop);
            if (data.hasNext()) {
                Map<String, Object> result = data.next();
                Integer parentId = (Integer) result.get("parentId");
                if (parentId != -1) {
                    loop = parentId;
                    categoryIds.add(parentId);
                } else
                    break;
            } else {
                break;
            }
        }
        //row.put("categoryId", categoryIds);

        List<Map<String, Object>> list = Lists.newArrayList();
        for (Map<String, Object> skus : stockKeepingUnits) {
            Map<String, Object> result = Maps.newHashMap();
            result.putAll(row);
            result.put("leafId", categoryId); //叶子ID
            result.put("categoryId", categoryIds);
            result.put("activityPrice", skuMarketingPrice.get(skus.get("skuId").toString()));
            result.put("$docBoost",1+random.nextInt(10000));//权重
            result.putAll(skus);
            result.putAll(productPVAndProduceArea);
            list.add(result);
        }

       // productDump.close();
        return list;
    }

    private List<Map<String, Object>> getSkuIdAndSkuReadableStr(Long productId, DataSource dataSource) {

        List<Map<String, Object>> list = Lists.newArrayList();

        Iterator<Map<String, Object>> results = (Iterator<Map<String, Object>>) dataSource.getData("select id,skuPropertiesInDb,price from StockKeepingUnit where skuState='NORMAL' and productId=" + productId);
        while (results.hasNext()) {
            Map<String, Object> result = results.next();
            Map<String, Object> map = Maps.newHashMap();
            BigInteger skuId = (BigInteger)result.get("id");
            map.put("price",result.get("price"));
            map.put("skuId", skuId.longValue());
            String skuProperties = (String) result.get("skuPropertiesInDb");
            StringBuilder skuPropertyToString = new StringBuilder();
            if (StringUtils.isNotEmpty(skuProperties)) {
                String[] split = skuProperties.split(",");
                if (split.length > 0) {
                    skuPropertyToString.append(" ");
                    for (String pv : split) {
                        PropertyValueUtil.PV pvResult = PropertyValueUtil.parseLongToPidVid(Long.valueOf(pv));

                        Iterator<Map<String, String>> propertyNameMap = (Iterator<Map<String, String>>) dataSource.getData("select name from property where isDelete=0 and id=" + pvResult.pid);
                        Iterator<Map<String, String>> valueNameMap = (Iterator<Map<String, String>>) dataSource.getData("select valueName from value where isDelete=0 and id=" + pvResult.vid);
                        if (propertyNameMap.hasNext() && valueNameMap.hasNext()) {
                            skuPropertyToString.append(valueNameMap.next().get("valueName")).append(" ");
                        }
                    }
                }
            }

            map.put("skuDesc", skuPropertyToString.toString());
            //sku销量
            Iterator<Map<String, Object>> tradeCountMap = (Iterator<Map<String, Object>>)dataSource.getData("select sum(number) tradeCount from skutraderesult where skuId=" + skuId);
            map.put("sell",0);
            if (tradeCountMap.hasNext()){
                map.put("sell",tradeCountMap.next().get("tradeCount"));
            }

            //查mainPic; 逻辑：先按sku去查 如果查到了 则以sku的图作为当前记录的pictureList 并且 number值最小的为主图


            Iterator<Map<String,Object>> pictureMap = (Iterator<Map<String,Object>>) dataSource.getData("select pictureUrl ,mainPic,minorPic from productPicture where skuId like '%"+skuId.intValue() + "%'");
            List<String> allPictureUrls = Lists.newArrayList();
            boolean hasSetSkuPic = false;
            int index = 0;
            while(pictureMap.hasNext()){//取sku的图片
                hasSetSkuPic =true;
                index++;
                Map<String,Object> tmp = pictureMap.next();
                String picUrl = tmp.get("pictureUrl").toString();
                allPictureUrls.add(picUrl);
               if(index == 1){//取第一张为主图
                   map.put("pictureUrl",picUrl);
               }
            }
            if(hasSetSkuPic && allPictureUrls.size()<=1){//sku只设置了一张图片,把产品的其它图片设置到urlList 以免页面只显示一张图片
                allPictureUrls.clear();
                Iterator<Map<String,Object>> productPicMap = (Iterator<Map<String,Object>>) dataSource.getData("select pictureUrl ,mainPic,minorPic from productPicture where productId="+productId);
                while (productPicMap.hasNext()){
                   allPictureUrls.add(productPicMap.next().get("pictureUrl").toString());
                }
                String mainPicUrl = map.get("pictureUrl").toString();
                allPictureUrls.remove(mainPicUrl);
                allPictureUrls.add(0,mainPicUrl);
            }

            if(!hasSetSkuPic){//如果没有设置sku图片，则按product去取 设置了mainPic==1的为主图
                Iterator<Map<String,Object>>  productPicMap = (Iterator<Map<String,Object>>) dataSource.getData("select pictureUrl ,mainPic,minorPic from productPicture where productId="+productId);

                while(productPicMap.hasNext()){//取sku的图片
                    Map<String,Object> tmp = productPicMap.next();
                    String picUrl = tmp.get("pictureUrl").toString();
                    allPictureUrls.add(picUrl);
                    if((Boolean)tmp.get("mainPic")){//主图
                        map.put("pictureUrl",picUrl);
                    }
                }
            }
            map.put("pictureUrlList",allPictureUrls);//所有的图
            list.add(map);
        }

        return list;

    }

    private Map<String,Object> getActivityPrice(long productId,DataSource dataSource) throws Exception {
        Map<String,Object> returnMap = Maps.newHashMap();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql  = " select id, productId, skuDetailsJson, discountType, discount, beginDate, endDate, createDate, updateDate\n" +
                "   from limitedTimeDiscount where productId="+productId+" and now() between beginDate and endDate limit 1";
        Iterator<Map<String,Object>> result = (Iterator<Map<String,Object>>)dataSource.getData(sql);
        if(result.hasNext()){
            Map<String,Object> map = result.next();
       /*     Date createDate = format.parse(map.get("createDate").toString());
            Date updateDate =   format.parse(map.get("updateDate").toString());*/
          //  if(updateDate.after(createDate)){//如果有更新 则取json里的价格作为活动价
                List<Map<String,Object>> values = new ObjectMapper().readValue(map.get("skuDetailsJson").toString(),List.class);
                for (Map<String, Object> value : values) {
                    returnMap.put(value.get("skuId").toString(), BigDecimal.valueOf((Integer)value.get("skuPrice"), currency.getDefaultFractionDigits()));
                }
           /* }else {//计算折扣价
                  if("Money".equalsIgnoreCase(map.get("discountType").toString().trim())){
                      caculateForMoney(skuPrice,(Long)map.get("discount"));
                  }else if("Ratio".equalsIgnoreCase(map.get("discountType").toString().trim())){
                      caculateForRatio(skuPrice, (Long) map.get("discount"));
                  }

            }*/

        }
        return returnMap;
    }

    private String caculateForMoney(long price,long discount){

        if ((price - discount) <= 0) {
            throw new RuntimeException("设置此值后, 价格会出现负数, 表酱紫!");
        }
        price = precision(price - discount);

        return BigDecimal.valueOf(price, currency.getDefaultFractionDigits()).toString();
    }
    private String caculateForRatio(long price,long discount){

        if (discount >= 100) {
            throw new RuntimeException("比例若设置此值, 价格会高于原价, no!");
        }
        if (discount <= 0) {
            throw new RuntimeException("比例若设置此值, 价格会出现负数. 别这样!");
        }
        price = precision(price * discount / 100);

        return BigDecimal.valueOf(price, currency.getDefaultFractionDigits()).toString();
    }

    private static long precision(long price) {
        return price / 100 * 100;
    }

    private Map<String,Object> getProductPVAndProduceArea(long productId,DataSource dataSource){
        Map<String,Object> row = new HashMap<String, Object>();
        //pidvid
        Iterator<Map<String,Object>> result = (Iterator<Map<String,Object>>)dataSource.getData("select json from ProductProperty where  productId=" + productId );
        Set<Long> pidvids = new HashSet<Long>();
        while(result.hasNext()){
            pidvids.addAll(PidVidJsonUtil.restore(result.next().get("json").toString()).getPidvid());
        }
        Map<String,String> propertyMap = buildPropertyMap(productDump);
        Map<String,String> valueMap = buildValueMap(productDump);
        List<String> produceAreas  = getAllowProduceArea(productDump);

        String produceAreaName = null;
        for (Long pidvid : pidvids) {
            PropertyValueUtil.PV pv = PropertyValueUtil.parseLongToPidVid(pidvid);
            String propertyName = propertyMap.get(String.valueOf(pv.pid));
            if(propertyName != null && "品牌所属地".equals(propertyName.trim())){
                String valueName = valueMap.get(String.valueOf(pv.vid));
                if(valueName != null && produceAreas.contains(valueName)) {
                    produceAreaName = valueName;
                    break;
                }
            }
        }
        row.put("produceArea", produceAreaName == null ? "" : produceAreaName);
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

