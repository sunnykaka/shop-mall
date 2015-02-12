package com.kariqu.designcenter.client.domain.open.api;

import com.kariqu.categorycenter.client.container.CategoryContainer;
import com.kariqu.categorycenter.client.service.CategoryPropertyQueryService;
import com.kariqu.categorycenter.domain.model.*;
import com.kariqu.categorycenter.domain.util.PidVid;
import com.kariqu.categorycenter.domain.util.PidVidJsonUtil;
import com.kariqu.categorycenter.domain.util.PropertyValueUtil;
import com.kariqu.designcenter.domain.open.api.ProductService;
import com.kariqu.designcenter.domain.open.module.BasicProduct;
import com.kariqu.designcenter.domain.open.module.Product;
import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.service.AttentionInfoService;
import com.kariqu.productcenter.service.ProductActivityService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.suppliercenter.domain.Brand;
import com.kariqu.suppliercenter.service.SupplierService;
import com.kariqu.tradecenter.service.IntegralService;
import com.kariqu.tradecenter.service.ValuationService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author Tiger
 * @author Asion
 * @version 1.0.0
 * @since 2011-5-6 下午09:33:15
 */
public class ProductServiceImpl implements ProductService {

    private final Log logger = LogFactory.getLog(ProductServiceImpl.class);

    @Autowired
    private com.kariqu.productcenter.service.ProductService productService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private AttentionInfoService attentionInfoService;

    @Autowired
    private CategoryContainer categoryContainer;

    @Autowired
    private ProductActivityService productActivityService;

    @Autowired
    private ValuationService valuationService;

    @Autowired
    private IntegralService integralService;

    @Override
    public List<Map<String, Object>> getProductMap(String productIds) {
        List<Map<String, Object>> productList = new ArrayList<Map<String, Object>>();
        if (StringUtils.isBlank(productIds)) return productList;

        for (String productId : productIds.split(",")) {
            int pid = NumberUtils.toInt(productId.trim());
            if (pid > 0)
                productList.add(productService.getProductMap(pid));
        }
        return productList;
    }

    @Override
    public List<BasicProduct> queryProductRecommend(int productId, String recommendType) {
        Recommend recommend = productService.getRecommend(productId);
        List<Integer> idList = recommend.getProductList(RecommendType.valueOf(recommendType));
        List<BasicProduct> list = new LinkedList<BasicProduct>();
        for (Integer id : idList) {
            list.add(buildBasicProduct(id));
        }
        return list;
    }

    @Override
    public Product buildOpenProduct(int productId) {
        com.kariqu.productcenter.domain.Product product = productService.getProductById(productId);
        if (product != null) {
            return buildOpenProduct(product);
        }
        return null;
    }

    @Override
    public Product buildOpenProduct(com.kariqu.productcenter.domain.Product product) {
        PictureDesc pictureDesc = productService.getPictureDesc(product.getId());
        HtmlDesc htmlDesc = productService.getHtmlDesc(product.getId());

        Product openProduct = new Product(product, pictureDesc, htmlDesc);

        // 品牌
        Brand brand = supplierService.queryBrandById(product.getBrandId());
        openProduct.setBrandName(brand.getName());

        fillBasic(openProduct);

        List<Product.PropertyValuePair> pairList = new LinkedList<Product.PropertyValuePair>();
        pairList.add(new Product.PropertyValuePair("所属商家", supplierService.queryCustomerById(product.getCustomerId()).getName()));
        pairList.add(new Product.PropertyValuePair("所属品牌", brand.getName()));
        pairList.add(new Product.PropertyValuePair("商品货号", product.getProductCode()));
        pairList.add(new Product.PropertyValuePair("商品名称", product.getName()));

        pairList.addAll(queryKeyPropertyPair(product.getId()));
        openProduct.setKeyProperty(pairList);

        setAttentionInfo(openProduct);

        //如果有多个SKU，则分析这些SKU
        if (!openProduct.isSingleSku()) {
            //将多值的用于筛选SKU的Map填充,使用LinkedHashMap保证顺序
            Map<Property, List<Product.SKuValue>> skuPickMap = new LinkedHashMap<Property, List<Product.SKuValue>>();

            for (StockKeepingUnit stockKeepingUnit : openProduct.getStockKeepingUnitList()) {
                openProduct.putSku(buildSKU(stockKeepingUnit));
                parseProductPV(skuPickMap, stockKeepingUnit, product);
            }
            correctPVSort(skuPickMap);
            openProduct.setSkuPickMap(skuPickMap);
        }
        return openProduct;
    }


    /**
     * 纠正属性值在详情页的顺序
     * <p/>
     * 针对数据库遗留数据，如果新发的商品不会出现数序不一致
     *
     * @param skuPickMap
     */
    private void correctPVSort(Map<Property, List<Product.SKuValue>> skuPickMap) {
        for (List<Product.SKuValue> sKuValues : skuPickMap.values()) {
            Collections.sort(sKuValues, new Comparator<Product.SKuValue>() {
                @Override
                public int compare(Product.SKuValue o1, Product.SKuValue o2) {
                    return o1.getPriority() - o2.getPriority();
                }
            });
        }
    }

    /**
     * 检查最大购买数量和库存数量是否合法
     *
     * @param stockKeepingUnitList
     */
    private void checkTradeMaxNumberAndStockQuantity(List<StockKeepingUnit> stockKeepingUnitList) {
        for (StockKeepingUnit stockKeepingUnit : stockKeepingUnitList) {
            int tradeMaxNumber = stockKeepingUnit.getTradeMaxNumber();
            int stockQuantity = stockKeepingUnit.getStockQuantity();
            if (tradeMaxNumber > stockQuantity) {
                stockKeepingUnit.setTradeMaxNumber(stockQuantity);
                logger.warn("数据异常：单个sku：" + stockKeepingUnit.getId() + " 最大购买数量("
                        + tradeMaxNumber + ")大于库存(" + stockQuantity + ")");
            }
        }
    }

    /**
     * 设置商品的保养和使用信息
     *
     * @param product
     */
    private void setAttentionInfo(Product product) {
        List<AttentionInfo> maintenanceInfo = attentionInfoService.queryAllMaintenanceByProductId(product.getId());
        for (AttentionInfo attentionInfo : maintenanceInfo) {
            product.addMaintainInfo(attentionInfo.getInfo());
        }
        List<AttentionInfo> attentionInfo = attentionInfoService.queryAllUseByProductId(product.getId());
        for (AttentionInfo info : attentionInfo) {
            product.addUseInfo(info.getInfo());
        }
    }


    /**
     * 分析商品的关键属性对
     * //TODO 现在不确定到底销售属性一定是多值，关键属性一定是单值，所以目前只是把关键属性的单值返回
     *
     * @param productId
     * @return
     */
    public List<Product.PropertyValuePair> queryKeyPropertyPair(int productId) {
        return queryKeyPropertyPair(productId, -1);
    }

    /**
     * 重载的方法，支持参数是字符串的商品ID
     *
     * @param productId
     * @param max
     * @return
     */
    public List<Product.PropertyValuePair> queryKeyPropertyPair(String productId, int max) {
        return queryKeyPropertyPair(Integer.parseInt(productId), max);
    }

    @Override
    public List<Product.PropertyValuePair> queryKeyPropertyPair(int productId, int max) {
        CategoryPropertyQueryService categoryPropertyQueryService = categoryContainer.getCategoryPropertyQueryService();
        ProductProperty keyProperty = productService.getProductPropertyByPropertyType(productId, PropertyType.KEY_PROPERTY);
        List<Product.PropertyValuePair> pairList = new LinkedList<Product.PropertyValuePair>();
        //商品有可能没有关键属性
        if (keyProperty != null) {
            PidVid pidvid = PidVidJsonUtil.restore(keyProperty.getJson());
            //单值
            singlePid(categoryPropertyQueryService, pairList, pidvid);
            //多值
            multiPid(categoryPropertyQueryService, pairList, pidvid);
        }
        return subPairList(pairList, max);
    }

    //截取比较结果长度
    private List<Product.PropertyValuePair> subPairList(List<Product.PropertyValuePair> pairList, int max) {
        if (pairList.size() <= max || max < 0) {
            return pairList;
        }
        List<Product.PropertyValuePair> newPairList = new ArrayList<Product.PropertyValuePair>(max);
        for (int i = 0; i < max; i++) {
            newPairList.add(pairList.get(i));
        }
        pairList = null;
        return newPairList;
    }

    //单值
    private void singlePid(CategoryPropertyQueryService categoryPropertyQueryService, List<Product.PropertyValuePair> pairList, PidVid pidvid) {
        int brandPid = categoryPropertyQueryService.getPropertyByName("品牌").getId();
        Map<Integer, Long> singlePidVidMap = pidvid.getSinglePidVidMap();
        Collection<Long> values = singlePidVidMap.values();
        for (Long pv : values) {
            //通过比较来限制数量
            PropertyValueUtil.PV pvResult = PropertyValueUtil.parseLongToPidVid(pv);
            //品牌这个属性在商品上有，所以这里过滤掉
            if (brandPid != pvResult.pid) {
                //从类目客户端子容器中获取类目服务
                Property property = categoryPropertyQueryService.getPropertyById(pvResult.pid);
                Value value = categoryPropertyQueryService.getValueById(pvResult.vid);
                if (property != null && value != null) {
                    pairList.add(new Product.PropertyValuePair(property.getName(), value.getValueName()));
                }
            }
        }
    }


    //多值
    private void multiPid(CategoryPropertyQueryService categoryPropertyQueryService, List<Product.PropertyValuePair> pairList, PidVid pidvid) {
        Map<Integer, List<Long>> multiPidVidMap = pidvid.getMultiPidVidMap();
        Collection<List<Long>> m_values = multiPidVidMap.values();
        for (List<Long> mpv : m_values) {
            String propertyName = null;
            StringBuilder values = new StringBuilder();
            int mark = 0;
            for (Long pv : mpv) {
                PropertyValueUtil.PV pvResult = PropertyValueUtil.parseLongToPidVid(pv);
                Property property = categoryPropertyQueryService.getPropertyById(pvResult.pid);
                Value value = categoryPropertyQueryService.getValueById(pvResult.vid);
                if (property != null && value != null) {
                    if (StringUtils.isEmpty(propertyName)) {
                        propertyName = property.getName();
                    }
                    if (mark > 0) {
                        values.append(", ");
                    }
                    values.append(value.getValueName());
                    mark++;
                }
            }
            pairList.add(new Product.PropertyValuePair(propertyName, values.toString()));
        }
    }

    /**
     * 分析商品的所有SKU属性，分析出每个属性和属性的值列表
     *
     * @param skuPickMap
     * @param stockKeepingUnit
     */
    private void parseProductPV(Map<Property, List<Product.SKuValue>> skuPickMap, StockKeepingUnit
            stockKeepingUnit, com.kariqu.productcenter.domain.Product product) {
        CategoryPropertyQueryService categoryPropertyQueryService = categoryContainer.getCategoryPropertyQueryService();
        List<SkuProperty> skuProperties = stockKeepingUnit.getSkuProperties();
        for (SkuProperty skuProperty : skuProperties) {
            int propertyId = skuProperty.getPropertyId();
            int valueId = skuProperty.getValueId();
            Property property = categoryPropertyQueryService.getPropertyById(propertyId);
            Value value = categoryPropertyQueryService.getValueById(valueId);
            if (property == null || value == null) {
                if (logger.isWarnEnabled())
                    logger.warn("属性(" + propertyId + ")或值(" + valueId + ")为空, 请检查是否有向 hsql 推送");

                continue;
            }

            Product.SKuValue sKuValue = new Product.SKuValue();
            sKuValue.setId(value.getId());
            sKuValue.setValueName(value.getValueName());

            //查询出这个商品发布时的相应类目属性值，可能已被删除
            CategoryPropertyValue categoryPropertyValue = categoryPropertyQueryService.queryCategoryPropertyValueByCategoryIdAndPropertyIdAndValueId(product.getCategoryId(), property.getId(), value.getId());
            if (categoryPropertyValue != null) {
                sKuValue.setPriority(categoryPropertyValue.getPriority());
            }

            //查出属性值的描述
            PropertyValueDetail categoryPropertyValueDetail = categoryPropertyQueryService.getCategoryPropertyValueDetail(property.getId(), value.getId());
            if (categoryPropertyValueDetail != null) {
                sKuValue.setImgUrl(categoryPropertyValueDetail.getPictureUrl());
                sKuValue.setDescription(categoryPropertyValueDetail.getDescription());
            }

            List<Product.SKuValue> valueList = skuPickMap.get(property);
            if (valueList == null) {
                valueList = new LinkedList<Product.SKuValue>();
                valueList.add(sKuValue);
                skuPickMap.put(property, valueList);
            } else {
                if (!valueList.contains(sKuValue)) {
                    valueList.add(sKuValue);
                }
            }
        }
    }


    private Product.SKU buildSKU(StockKeepingUnit stockKeepingUnit) {
        Product.SKU sku = new Product.SKU();
        SkuPriceDetail skuMarketingPrice = productActivityService.getSkuMarketingPrice(stockKeepingUnit);
        sku.setId(skuMarketingPrice.getSkuId());

        sku.setMoney(skuMarketingPrice.getOriginalPrice());
        // 积分(100 块钱换一积分, 抵扣 1 块钱)
        sku.setSkuIntegral(integralService.getTradeCurrency(Money.YuanToCent(skuMarketingPrice.getSellPrice())));
        // sku 描述
        sku.setSkuDesc(skuService.getSkuPropertyValueToStr(stockKeepingUnit));

        // sku 图片
        List<ProductPicture> productPictures = productService.getProductPictureBySkuId(stockKeepingUnit.getId());
        List<String> imageList = new ArrayList<String>();
        for (ProductPicture picture : productPictures) {
            imageList.add(picture.getPictureUrl());
        }
        sku.setSkuImageList(imageList);

        sku.setMarketing(skuMarketingPrice.isActivity());
        sku.setActivityDateStr(skuMarketingPrice.getActivityDateStr());
        sku.setMarketingPrice(skuMarketingPrice.getActivityPrice());
        sku.setActivityType(skuMarketingPrice.getActivityType());
        sku.setLimit(stockKeepingUnit.getTradeMaxNumber());
        sku.setStock(stockKeepingUnit.getStockQuantity());
        sku.setMarketPrice(Money.getMoneyString(stockKeepingUnit.getMarketPrice()));
        String skuPropertiesInDb = stockKeepingUnit.getSkuPropertiesInDb();
        if (skuPropertiesInDb != null) {
            //将pidvid按从小到大排序
            String[] pidvid = skuPropertiesInDb.split(",");
            long[] pidvidLong = new long[pidvid.length];
            for (int i = 0; i < pidvid.length; i++) {
                pidvidLong[i] = Long.parseLong(pidvid[i]);
            }
            Arrays.sort(pidvidLong);
            sku.setPvList(pidvidLong);
        }
        return sku;
    }


    /**
     * 构建基本商品对象，用于在频道页等页面简单现实商品的时候
     */
    public BasicProduct buildBasicProduct(int productId) {
        com.kariqu.productcenter.domain.Product product = productService.getProductById(productId);
        if (product != null) {
            BasicProduct basicProduct = new BasicProduct(product, productService.getMainProductPicture(product.getId()));
            fillBasic(basicProduct);
            return basicProduct;
        } else {
            return null;
        }
    }


    private void fillBasic(BasicProduct basicProduct) {
        List<StockKeepingUnit> stockKeepingUnits = skuService.queryValidSkuForSell(basicProduct.getId());

        checkTradeMaxNumberAndStockQuantity(stockKeepingUnits);

        basicProduct.setStockKeepingUnitList(stockKeepingUnits);
        basicProduct.setValuation(valuationService.queryValuationCountByProductId(basicProduct.getId()));

        BasicProduct.SKU defaultSkuObject = buildSKU(getMinPriceSku(stockKeepingUnits));

        basicProduct.setDefaultSkuObject(defaultSkuObject);
    }


    //过滤出价格最低的sku
    private StockKeepingUnit getMinPriceSku(List<StockKeepingUnit> list) {
        StockKeepingUnit find = null;
        for (StockKeepingUnit stockKeepingUnit : list) {
            if (find == null) {
                find = stockKeepingUnit;
            } else if (find.getPrice() > stockKeepingUnit.getPrice()) {
                find = stockKeepingUnit;
            }
        }
        return find;
    }
}
