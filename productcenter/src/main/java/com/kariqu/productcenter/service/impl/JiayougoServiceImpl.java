package com.kariqu.productcenter.service.impl;

import com.kariqu.categorycenter.domain.model.ProductCategory;
import com.kariqu.categorycenter.domain.model.Property;
import com.kariqu.categorycenter.domain.model.PropertyType;
import com.kariqu.categorycenter.domain.model.Value;
import com.kariqu.categorycenter.domain.service.CategoryPropertyService;
import com.kariqu.categorycenter.domain.service.ProductCategoryService;
import com.kariqu.categorycenter.domain.util.PidVid;
import com.kariqu.categorycenter.domain.util.PidVidJsonUtil;
import com.kariqu.categorycenter.domain.util.PropertyValueUtil;
import com.kariqu.common.http.Request;
import com.kariqu.common.http.Response;
import com.kariqu.common.http.Verb;
import com.kariqu.common.json.JsonUtil;
import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.repository.ProductOfPlatformRepository;
import com.kariqu.productcenter.service.JiayougoService;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.productcenter.service.SkuStorageService;
import com.kariqu.suppliercenter.domain.Brand;
import com.kariqu.suppliercenter.domain.Supplier;
import com.kariqu.suppliercenter.service.SupplierService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * User: Alec
 * Date: 13-10-15
 * Time: 下午5:30
 */
public class JiayougoServiceImpl implements JiayougoService {

    private Log logger = LogFactory.getLog(JiayougoServiceImpl.class);
    private static final String AddProductToJYGUrl = "http://m.jiayougo.com/api/product_add";
    private static final String NotFilled = "未填";

    private boolean online;
    private String accountId;
    private String password;

    @Autowired
    private ProductOfPlatformRepository productOfPlatformRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryPropertyService categoryPropertyService;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private SkuStorageService skuStorageService;

    @Override
    public boolean addProductToJYG(int productId) throws Exception {
        Product product = checkJYG(productId);

        Map<String, Map<String, String>> pushProMap = new LinkedHashMap<String, Map<String, String>>();
        Map<String, String> splitSkuMap = new LinkedHashMap<String, String>();
        String key, value;
        StringBuilder sbd = new StringBuilder();

        List<StockKeepingUnit> skuList = skuService.querySKUByProductId(productId);
        for (StockKeepingUnit sku : skuList) {
            if (sku.getSkuState() != StockKeepingUnit.SKUState.NORMAL) continue;

            sbd.delete(0, sbd.length());
            // sku 描述及库存 >> M:白色:20;
            sbd.append(getSkuDesc(sku.getSkuPropertiesInDb())).append(getSkuQuantity(sku.getId())).append(";");

            key = sku.getPrice() + "" + sku.getMarketPrice();
            value = splitSkuMap.get(key);
            if (StringUtils.isNotBlank(value)) {
                Map<String, String> pushPro = pushProMap.get(key);
                // 同样价格的把 sku 相关信息拼接起来 >> M:白色:20;L:黑色:30;
                pushPro.put("spec", sbd.append(pushPro.get("spec")).toString());
            } else {
                Map<String, String> pushPro = new LinkedHashMap<String, String>();
                // 价格规则(成本价 及 家有购物的销售价). 是否需要重构?
                pushPro.put("supplier_price", Money.getMoneyString(sku.getMarketPrice()));
                pushPro.put("price", Money.getMoneyString(sku.getMarketPrice() + 600));

                // sku说明与库存
                pushPro.put("spec", sbd.toString());

                pushProMap.put(key, pushPro);
            }

            splitSkuMap.put(key, sbd.toString());
        }
        pushAndWrite(product, productId, pushProMap);

        return true;
    }

    private void pushAndWrite(Product product, int productId, Map<String, Map<String, String>> pushProMap) throws Exception {
        String title = product.getName();
        String maker_location = getBrandBirthland(productId);
        String category = getAllCategoryName(product.getCategoryId());
        String maker = getSupplierName(product.getCustomerId());
        String brand_name = getBrandName(product.getBrandId());
        String desc = getProductDesc(productId);
        PictureDesc pictureDesc = getPictureDesc(productId);
        String image = getMailPictureUrl(pictureDesc);
        String product_images = getPictures(pictureDesc);

        for (Map<String, String> map : pushProMap.values()) {
            // 商品名称
            map.put("title", title);
            // 原产地 >> 对应品牌产地
            map.put("maker_location", maker_location);
            // 类目名
            map.put("category", category);
            // 供应商
            map.put("maker", maker);
            // 品牌名
            map.put("brand_name", brand_name);
            // 进口还是国产
            map.put("maker_country", "国产");
            // 商品说明
            map.put("desc", desc);
            // 图片
            map.put("image", image);
            // 图片列表
            map.put("product_images", product_images);
            // 商品外部编码 >> 商品Id
            map.put("outer_id", String.valueOf(productId));

            // 向家有购物发送请求, 若成功则写入日志
            if (submit(map)) {
                loggingProductToDb(productId, map);
            }
        }
    }

    /** 检查是否已加入家有购 */
    private Product checkJYG(int productId) throws Exception {
        ProductOfPlatform productOfPlatform = productOfPlatformRepository.queryProductOfPlatform(productId, ProductOfPlatform.Platform.JiaYouGou);
        if (null != productOfPlatform) {
            throw new Exception("该商品已经加入" + ProductOfPlatform.Platform.JiaYouGou.toDesc());
        }

        Product product = productService.getProductById(productId);
        if (product == null || !product.isOnline()) {
            throw new Exception("该商品不存在或已下架");
        }
        return product;
    }

    private void loggingProductToDb(int productId, Map<String, String> productMap) {
        try {
            ProductOfPlatform product = new ProductOfPlatform();
            product.setProductId(productId);
            product.setPlatform(ProductOfPlatform.Platform.JiaYouGou);
            product.setProductOfJson(JsonUtil.objectToJson(productMap));
            productOfPlatformRepository.addProductToPlatform(product);
        } catch (Exception e) {
            logger.error("数据库添加记录加入" + ProductOfPlatform.Platform.JiaYouGou.toDesc() + "平台商品(" + productId + ")出错:" + productMap.toString(), e);
        }
    }

    /**
     * 获取品牌所属地
     *
     * @param productId
     * @return
     */
    private String getBrandBirthland(int productId) {
        ProductProperty keyProperty = productService.getProductPropertyByPropertyType(productId, PropertyType.KEY_PROPERTY);
        if (keyProperty == null) {
            return NotFilled;
        }

        PidVid pidvid = PidVidJsonUtil.restore(keyProperty.getJson());

        int pidOfBrandBirthland = getPIdOfBrandBirthland();

        if (pidOfBrandBirthland == 0) {
            return NotFilled;
        }

        long pidVid = pidvid.getSinglePidVidMap().get(pidOfBrandBirthland);

        int vid = PropertyValueUtil.parseLongToPidVid(pidVid).vid;

        return getBrandBirthlandValue(vid);

    }

    /**
     * 获取品牌所属地的pid
     *
     * @return
     */
    private int getPIdOfBrandBirthland() {
        Property property = categoryPropertyService.getPropertyByName("品牌所属地");
        return null == property ? 0 : property.getId();
    }

    /**
     * 获取品牌所属地
     *
     * @return
     */
    private String getBrandBirthlandValue(int vid) {
        Value value = categoryPropertyService.getValueById(vid);
        return null == value ? NotFilled : value.getValueName();
    }

    /**
     * 品牌名
     *
     * @param brandId
     * @return
     */
    private String getBrandName(int brandId) {
        Brand brand = supplierService.queryBrandById(brandId);
        return brand != null ? brand.getName() : NotFilled;
    }

    /**
     * 商品图片详情
     *
     * @param productId
     * @return
     */
    private PictureDesc getPictureDesc(int productId) {
        return productService.getPictureDesc(productId);
    }

    /**
     * 商品主图
     *
     * @param pictureDesc
     * @return
     */
    private String getMailPictureUrl(PictureDesc pictureDesc) {
        return null == pictureDesc ? PictureDesc.defaultPictureUrl : pictureDesc.getMainPicture().getPictureUrl();
    }

    /**
     * 商品图片列表
     *
     * @param pictureDesc
     * @return
     */
    private String getPictures(PictureDesc pictureDesc) {
        if (null == pictureDesc) {
            return PictureDesc.defaultPictureUrl;
        }
        StringBuilder str = new StringBuilder();
        List<ProductPicture> pictures = pictureDesc.getPictures();
        for (ProductPicture picture : pictures) {
            if (null != picture && StringUtils.isNotEmpty(picture.getPictureUrl())) {
                str.append(picture.getPictureUrl()).append(";");
            }
        }
        return str.toString();
    }

    /**
     * 所有前台类目名
     *
     * @param categoryId
     * @return
     */
    private String getAllCategoryName(int categoryId) {
        StringBuilder str = new StringBuilder();
        ProductCategory productCategory = productCategoryService.getProductCategoryById(categoryId);
        if (null == productCategory) {
            return "";
        }
        List<ProductCategory> categories = productCategoryService.getParentCategories(categoryId, true);
        for (ProductCategory category : categories) {
            str.append(category.getName()).append(";");
        }
        str.append(productCategory.getName()).append(";");
        return str.toString();
    }

    /**
     * 供应商名
     *
     * @param customerId
     * @return
     */
    private String getSupplierName(int customerId) {
        Supplier supplier = supplierService.queryCustomerById(customerId);
        return supplier != null ? supplier.getName() : NotFilled;
    }

    /**
     * 获取商品详情
     *
     * @param productId
     * @return
     */
    private String getProductDesc(int productId) {
        HtmlDesc htmlDesc = productService.getHtmlDesc(productId);
        if (null == htmlDesc) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        for (Html html : htmlDesc.getHtmlList()) {
            if (null != html && StringUtils.isNotEmpty(html.getName()) && StringUtils.isNotEmpty(html.getContent())) {
                str.append("<h1>" + html.getName() + "</h1>").append(html.getContent()).append("<br/>");
            }
        }
        return str.toString();
    }

    /**
     * M:白色:
     *
     * @param skuPropertiesInDb
     * @return
     */
    private String getSkuDesc(String skuPropertiesInDb) {
        StringBuilder skuPropertyToString = new StringBuilder();
        if (StringUtils.isNotEmpty(skuPropertiesInDb)) {
            String[] split = skuPropertiesInDb.split(",");
            if (split.length > 0) {
                for (String pv : split) {
                    PropertyValueUtil.PV pvResult = PropertyValueUtil.parseLongToPidVid(Long.valueOf(pv));
                    Value value = categoryPropertyService.getValueById(pvResult.vid);
                    skuPropertyToString.append(value.getValueName()).append(":");
                }
            }
        }
        return skuPropertyToString.toString();
    }

    /**
     * 获取sku库存数量
     *
     * @param skuId
     * @return
     */
    private int getSkuQuantity(long skuId) {
        SkuStorage skuStorage = skuStorageService.getSkuStorage(skuId);
        if (null == skuStorage) {
            return 0;
        }
        return skuStorage.getStockQuantity();
    }


    /**
     * 提交添加的商品
     *
     * @param product
     * @return
     */
    private boolean submit(Map<String, String> product) throws Exception {
        if (product == null) return false;

        if (online) {
            Request request = new Request(Verb.POST, AddProductToJYGUrl + "/" + accountId + "/" + password);
            addQuerystringParameterByMap(request, product);

            if (logger.isWarnEnabled())
                logger.warn("向" + ProductOfPlatform.Platform.JiaYouGou.toDesc() + "发送请求: " + request);
            Response response = request.send();
            String body = response.getBody();
            if (logger.isWarnEnabled())
                logger.warn(ProductOfPlatform.Platform.JiaYouGou.toDesc() + "返回: " + body);
            checkJYGReturnValue(body);
            return true;
        }
        if (logger.isWarnEnabled())
            logger.warn("非正式环境, 不发送真实请求");

        return false;
    }

    /**
     * 分析家有购返回结果
     *
     * @param returnValue
     * @throws Exception
     */
    private void checkJYGReturnValue(String returnValue) throws Exception {
        if (StringUtils.isEmpty(returnValue)) {
            return;
        }

        Map<String, Object> result = JsonUtil.json2Object(returnValue.replaceAll("'", "\""), Map.class);

        if (!Boolean.parseBoolean(result.get("success").toString())) {
            throw new Exception(result.get("message").toString());
        }
    }

    /**
     * 支持添加map
     *
     * @param request
     * @param params
     */
    private void addQuerystringParameterByMap(Request request, Map<String, String> params) {
        if (params != null) {
            for (String key : params.keySet()) {
                request.addBodyParameter(key, params.get(key));
            }
        }
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

}
