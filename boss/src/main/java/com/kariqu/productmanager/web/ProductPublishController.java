package com.kariqu.productmanager.web;

import com.kariqu.categorycenter.domain.model.CategoryProperty;
import com.kariqu.categorycenter.domain.model.Property;
import com.kariqu.categorycenter.domain.model.PropertyType;
import com.kariqu.categorycenter.domain.service.CategoryPropertyService;
import com.kariqu.categorycenter.domain.util.PidVid;
import com.kariqu.categorycenter.domain.util.PidVidJsonUtil;
import com.kariqu.categorycenter.domain.util.PropertyValueUtil;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.domain.ProductProperty;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.domain.StoreStrategy;
import com.kariqu.productcenter.service.ProductActivityService;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.productmanager.helper.Descartes;
import com.kariqu.productmanager.helper.ProductPidVid;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 商品发布控制器
 * 商品必须发布到某个类目下，类目上保存了商品的所有属性和属性值，这些属性和属性值是在类目系统中由运营维护的
 * 商品只需要选择属性和值即可。
 * 类目上有的属性都必须选择一个值，值分为多值和单值两种，单值是一个下拉框，多值是复选框，数据库中保存的属性和属性值都一个唯一的ID
 * 所有商品共享这些ID，一个商品发布之后就会获得一组属性和属性值ID，他们表示商品在类目上选择的值，格式如下：
 * [pid:vid,pid:vid,pid:vid......]
 * 为了存储方便和搜索引擎建立索引，pid:vid被合并成一个long型数字，前32为表示pid,后32为表示vid
 * <p/>
 * 由于商品的属性分为关键属性和销售属性，每个商品有串long值，一串表示销售属性和值，另个表示关键属性和值。
 * 销售属性和关键属性都有可能有多值和单值，但是销售属性的多值还和库存和价格挂钩。
 * <p/>
 * 比如一件衣服，不同颜色和不同尺码的价格和库存都不同，所以我们把这些决定价格和库存的多值单独抽取出来放入sku表，他们会用笛卡尔组合来对应自己的库存和价格
 * 如果一个商品没有多值的销售属性，则商品就可以直接设置库存和价格，那么在sku表里，笛卡尔组合那个列就是空值
 * <p/>
 * 关键属性的单值目前会显示在商品的参数列表，多值没管，销售属性的多值被SKU利用，单值没管
 * User: Asion
 * Date: 11-7-13
 * Time: 下午2:03
 */

@Controller
public class ProductPublishController {

    private final Log logger = LogFactory.getLog(ProductPublishController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryPropertyService categoryPropertyService;

    @Autowired
    private SkuService skuService;


    @Autowired
    private ProductActivityService productActivityService;


    /**
     * 发布商品
     *
     * @param product
     * @param response
     * @param request
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/product/new", method = RequestMethod.POST)
    @Permission("发布商品")
    public void createProduct(Product product, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            boolean existProductCode = productService.existProductCode(product.getProductCode());
            if (existProductCode) {
                new JsonResult(false, "商品编号重复").toJson(response);
                return;
            }
            ProductBrand productBrand = parseBrandId(request);
            product.setBrandId(productBrand.brandVid);
            productService.createProduct(product);
            ProductPidVid productPidVid = parsePidVid(productBrand, request, categoryPropertyService.queryCategoryPropertyByCategoryId(product.getCategoryId()));
            this.createProperty(product.getId(), productPidVid);
            this.createSkuByDescartes(productPidVid, product);
        } catch (Exception e) {
            logger.error("商品管理的发布商品异常：" + e);
            new JsonResult(false, "发布商品出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }


    /**
     * 更新商品的品牌和商家信息
     * //TODO 如果修改了商家 sku的库位 还没有修改
     * @param productId
     * @param customerId
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/update/customer/brand", method = RequestMethod.POST)
    public void updateCustomerBrand(@RequestParam("productId") Integer productId, @RequestParam("customerId") int customerId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Product product = productService.getProductById(productId);
            product.setCustomerId(customerId);
            ProductBrand productBrand = parseBrandId(request);

            //更新基本信息
            product.setBrandId(productBrand.brandVid);
            productService.updateProduct(product);

            //更新商品关键属性中品牌的json串
            updateProductPropertyBrandJson(productBrand, productId);

            productService.notifyProductUpdate(productId);
        } catch (Exception e) {
            logger.error("更新商品品牌商家异常：" + e);
            new JsonResult(false, "更新商品品牌商家异常").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 修改商品关键属性json串中的品牌
     * 修改关键属性不影响价格 只替换原始的即可
     * <p/>
     *
     * @param productId
     */
    private void updateProductPropertyBrandJson(ProductBrand productBrand, int productId) {
        //获取销售属性
        ProductProperty property = productService.getProductPropertyByPropertyType(productId, PropertyType.KEY_PROPERTY);
        if (null == property || StringUtils.isEmpty(property.getJson())) {
            logger.warn("商品(" + productId + ")没有关键属性");
            return;
        }

        String keyPropertyOfJson = property.getJson();

        int pid = productBrand.brandPid;

        long new_pv = PropertyValueUtil.mergePidVidToLong(pid, productBrand.brandVid);

        long old_pv = getPidAndVidForBrandByKeyPropertyOfJson(keyPropertyOfJson, pid);

        //替换原始的品牌pidvid
        keyPropertyOfJson = keyPropertyOfJson.replaceAll("\\b" + old_pv + "\\b", String.valueOf(new_pv));

        productService.updateProductPropertyBrandJsonByPropertyType(keyPropertyOfJson, productId, PropertyType.KEY_PROPERTY);
    }

    /**
     * 获取原始品牌pid vid，品牌是单值属性可以直接取
     * ps:由于以前没有修改商品属性json串 造成商品基本信息中的品牌Id(vid) 与json中记录的不一致
     * 所以替换的时候要重新解析json获取品牌的id,不能根据商品基本信息中的品牌id来计算pid vid
     *
     * @param keyPropertyOfJson
     * @param pid
     * @return
     */
    private long getPidAndVidForBrandByKeyPropertyOfJson(String keyPropertyOfJson, int pid) {
        PidVid pidvid = PidVidJsonUtil.restore(keyPropertyOfJson);
        return pidvid.getSinglePidVidMap().get(pid);
    }

    /**
     * 修改已发布商品的库存策略
     *
     * @param id
     * @param storeStrategy
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/product/updateStrategy", method = RequestMethod.POST)
    @Permission("修改库存策略")
    public void updateProductStrategy(int id, StoreStrategy storeStrategy, HttpServletResponse response) throws IOException {
        try {
            productService.updateProductStrategy(id, storeStrategy);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("修改编号为" + id + "的商品库存策略错误：" + e);
            new JsonResult(false, "修改库存策略失败").toJson(response);
        }
    }


    /**
     * 读取商品的库存策略
     *
     * @param id
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "product/strategy/{id}", method = RequestMethod.POST)
    public void getProductStrategy(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
        Product product = productService.getProductById(id);
        new JsonResult(true).addData("id", product.getId()).addData("storeStrategy", product.getStoreStrategy()).toJson(response);
    }

    /**
     * 分析出商品的品牌这个属性的值ID
     * 品牌在类目中是一个属性，这里和类目中心的ID保持一致
     *
     * @param request
     * @return
     */
    private ProductBrand parseBrandId(HttpServletRequest request) {
        Property brandProperty = getBrandProperty();
        String brandId = request.getParameter("" + brandProperty.getId());
        ProductBrand productBrand = new ProductBrand();
        productBrand.brandPid = brandProperty.getId();
        productBrand.brandVid = Integer.valueOf(brandId);
        return productBrand;
    }

    private Property getBrandProperty() {
        Property property = categoryPropertyService.getPropertyByName("品牌");
        return property;
    }


    /**
     * 修改了商品类目，重新计算SKU
     *
     * @param product
     * @param response
     * @param request
     * @throws IOException
     */
    @RequestMapping(value = "/product/update/category", method = RequestMethod.POST)
    @Permission("修改商品类目")
    public void updateProductCategory(Product product, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            if (productActivityService.checkProductIfJoinActivity(product.getId())) {
                new JsonResult(false, "此商品正在参加活动，不能修改").toJson(response);
                return;
            }
            Product currentProduct = productService.getProductById(product.getId());

            if (currentProduct.isOnline()) {
                new JsonResult(false, "请先下架再修改").toJson(response);
                return;
            }

            ProductBrand productBrand = new ProductBrand();
            productBrand.brandPid = getBrandProperty().getId();
            productBrand.brandVid = currentProduct.getBrandId();
            int newCategoryId = product.getCategoryId();
            ProductPidVid productPidVid = parsePidVid(productBrand, request, categoryPropertyService.queryCategoryPropertyByCategoryId(newCategoryId));
            createProperty(product.getId(), productPidVid); //重新创建属性
            createSkuByDescartes(productPidVid, product); //重新创建SKU
            currentProduct.setCategoryId(newCategoryId);
            productService.updateProduct(currentProduct);
        } catch (Exception e) {
            logger.error("商品管理的修改商品属性值或者类目值异常：" + e);
            new JsonResult(false, "修改商品的属性值或者类目值出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }


    /**
     * 修改商品的关键属性值
     *
     * @param product
     * @param response
     * @param request
     */
    @RequestMapping(value = "/product/update/key/property", method = RequestMethod.POST)
    @Permission("修改商品关键属性")
    public void updateProductKeyProperty(Product product, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            Product currentProduct = productService.getProductById(product.getId());
            ProductBrand productBrand = new ProductBrand();
            productBrand.brandPid = getBrandProperty().getId();
            productBrand.brandVid = currentProduct.getBrandId();
            ProductPidVid productPidVid = parsePidVid(productBrand, request, categoryPropertyService.getCategoryProperties(product.getCategoryId(), PropertyType.KEY_PROPERTY));
            createKeyProperty(product.getId(), productPidVid.getKeyProperty());//重新创建属性
            productService.notifyProductUpdate(product.getId());
        } catch (Exception e) {
            logger.error("商品管理的修改商品属性值或者类目值异常：" + e);
            new JsonResult(false, "修改商品的属性值或者类目值出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 修改商品的销售属性，SKU列表可能被重新计算
     *
     * @param product
     * @param response
     * @param request
     * @throws IOException
     */
    @RequestMapping(value = "/product/update/sell/property", method = RequestMethod.POST)
    @Permission("修改商品销售属性")
    public void updateProductSellProperty(Product product, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            if (productActivityService.checkProductIfJoinActivity(product.getId())) {
                new JsonResult(false, "此商品正在参加活动，不能修改").toJson(response);
                return;
            }
            Product currentProduct = productService.getProductById(product.getId());

            if (currentProduct.isOnline()) {
                new JsonResult(false, "请先下架再修改").toJson(response);
                return;
            }

            ProductBrand productBrand = new ProductBrand();
            productBrand.brandPid = getBrandProperty().getId();
            productBrand.brandVid = currentProduct.getBrandId();
            ProductPidVid productPidVid = parsePidVid(productBrand, request, categoryPropertyService.getCategoryProperties(product.getCategoryId(), PropertyType.SELL_PROPERTY));
            createSellProperty(product.getId(), productPidVid.getSellProperty());//重新创建属性，这时不影响库存和价格
            PidVid sell = productPidVid.getSellProperty();
            analysisNewDescartes(sell, product);
            productService.notifyProductUpdate(product.getId());
        } catch (Exception e) {
            logger.error("商品管理的修改商品属性值或者类目值异常：" + e);
            new JsonResult(false, "修改商品的属性值或者类目值出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 当修改的销售属性为多值时
     * 分析多值更改，先解析出请求中的所有多值计算出新的笛卡尔组合(newStockKeepingUnits)
     * 把 newStockKeepingUnits 遍历出来，把sku的一串long作为key，sku本身做为value值 组成一个SKU_MAP
     * 从数据库中读取原来商品的sku列表，遍历它拿到组合的pidvid字符串
     * 然后用这个pidvid做为 新SKU_MAP的key来取value
     * 如果取到的值不为空，那么说明新的sku与数据库中老的sku一样,那么新的newStockKeepingUnits就把当前的sku就清除掉
     * 如果取到的值为空，那么说明数据库中老的sku不在newStockKeepingUnits中,那么就记录这些没用的sku_id（组成一个数组）
     * 通过以上我们得到了过滤后的newStockKeepingUnits和数据库中需要删除的sku_id数组，
     * 接下来
     * 用sku_id数组删除数据库中无用的sku
     * 遍历新的newStockKeepingUnits得到sku把这些新的sku加入数据库
     * <p/>
     * 当修改的销售属性为单值，但是数据库中销售属性为多值时
     * 直接清空原来的sku，重新创建新的sku
     *
     * @param sell
     * @param product
     */
    private void analysisNewDescartes(PidVid sell, Product product) {
        //销售属性必须有多值才处理
        if (!sell.getMultiPidVidMap().isEmpty()) {
            List<StockKeepingUnit> newStockKeepingUnits = parseSkuList(sell, product);
            Map<String, StockKeepingUnit> newStockKeepingUnitsMap = new HashMap<String, StockKeepingUnit>();
            for (StockKeepingUnit stockKeepingUnit : newStockKeepingUnits) {
                //用SKU的一串long作为key,这串long就是每个笛卡尔集合中的一个
                newStockKeepingUnitsMap.put(stockKeepingUnit.getSkuPropertiesInDb(), stockKeepingUnit);
            }
            //把数据库中的取出来
            List<StockKeepingUnit> currentSKUList = skuService.querySKUByProductId(product.getId());

            String skuIdList = "";
            for (StockKeepingUnit oldStockKeepingUnit : currentSKUList) {
                StockKeepingUnit stockKeepingUnitInDb = newStockKeepingUnitsMap.get(oldStockKeepingUnit.getSkuPropertiesInDb());
                if (stockKeepingUnitInDb != null) { //判断新的SKU是否包含数据库中的SKU，如果包含就把newStockKeepingUnits中的清除
                    newStockKeepingUnits.remove(stockKeepingUnitInDb);
                } else {
                    //拼接没有的SKU id 要删除
                    skuIdList += oldStockKeepingUnit.getId() + ",";
                }
            }
            //清除不包含的SKU
            skuService.deleteSkuByIdList(skuIdList);
            logger.warn("因为商品属性的修改删除了部分SKU:" + skuIdList);
            //加入新的SKU
            for (StockKeepingUnit newStockKeepingUnit : newStockKeepingUnits) {
                skuService.createStockKeepingUnit(newStockKeepingUnit);
            }

        } else if (skuService.hasMultiSku(product.getId())) {
            //当修改的销售属性为单值，但是数据库中销售属性为多值时，则清空原来的所有sku，重新创建
            skuService.deleteSKUByProductId(product.getId());
            StockKeepingUnit stockKeepingUnit = new StockKeepingUnit();
            stockKeepingUnit.setProductId(product.getId());
            skuService.createStockKeepingUnit(stockKeepingUnit);
        }
    }

    private List<StockKeepingUnit> parseSkuList(PidVid sell, Product product) {
        Map<Integer, List<Long>> multiPidVidMap = sell.getMultiPidVidMap();
        Descartes descartes = new Descartes();
        for (List<Long> pidvids : multiPidVidMap.values()) {
            descartes.compute(pidvids);//计算笛卡尔积
        }
        List<List<Long>> result = descartes.getResult();
        //新的sku列表
        List<StockKeepingUnit> newStockKeepingUnits = new LinkedList<StockKeepingUnit>();
        for (List<Long> pidvidList : result) {
            StockKeepingUnit stockKeepingUnit = new StockKeepingUnit();
            stockKeepingUnit.setProductId(product.getId());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pidvidList.size(); i++) {
                Long pidvid = pidvidList.get(i);
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(pidvid);
            }
            stockKeepingUnit.setSkuPropertiesInDb(sb.toString());
            newStockKeepingUnits.add(stockKeepingUnit);
        }
        return newStockKeepingUnits;
    }


    /**
     * 创建商品的关键属性，销售属性
     * 销售属性中的多值会和价格和库存关联
     * 创建过程之前先清除数据库中的属性
     *
     * @param productId
     * @param productPidVid
     */
    private void createProperty(int productId, ProductPidVid productPidVid) {
        PidVid key = productPidVid.getKeyProperty();
        createKeyProperty(productId, key);
        PidVid sell = productPidVid.getSellProperty();
        createSellProperty(productId, sell);
    }

    private void createKeyProperty(int productId, PidVid key) {
        if (!key.checkEmpty()) {//有关键属性
            productService.deleteProductPropertyByPropertyType(productId, PropertyType.KEY_PROPERTY);
            ProductProperty keyProperty = new ProductProperty();
            keyProperty.setProductId(productId);
            keyProperty.setJson(PidVidJsonUtil.toJson(key));
            keyProperty.setPropertyType(PropertyType.KEY_PROPERTY);
            productService.createProductProperty(keyProperty);
        }
    }


    private void createSellProperty(int productId, PidVid sell) {
        if (!sell.checkEmpty()) { //有销售属性
            productService.deleteProductPropertyByPropertyType(productId, PropertyType.SELL_PROPERTY);
            ProductProperty sellProperty = new ProductProperty();
            sellProperty.setProductId(productId);
            sellProperty.setJson(PidVidJsonUtil.toJson(sell));
            sellProperty.setPropertyType(PropertyType.SELL_PROPERTY);
            productService.createProductProperty(sellProperty);
        }
    }


    /**
     * 创建SKU用多值pid的笛卡尔积
     * 如果销售属性为空或者是单值，则不用笛卡尔积，sku属性列为空
     * 创建过程中先会删除一次
     *
     * @param productPidVid
     * @param product
     */
    private void createSkuByDescartes(ProductPidVid productPidVid, Product product) {
        skuService.deleteSKUByProductId(product.getId());
        //销售属性为空，或者没有多值，直接创建SKU，这时属性为空
        if (productPidVid.isSellEmpty() || productPidVid.getSellProperty().getMultiPidVidMap().isEmpty()) {
            StockKeepingUnit stockKeepingUnit = new StockKeepingUnit();
            stockKeepingUnit.setProductId(product.getId());
            skuService.createStockKeepingUnit(stockKeepingUnit);
        } else {
            List<StockKeepingUnit> stockKeepingUnits = parseSkuList(productPidVid.getSellProperty(), product);
            for (StockKeepingUnit stockKeepingUnit : stockKeepingUnits) {
                skuService.createStockKeepingUnit(stockKeepingUnit);
            }
        }
    }

    /**
     * 从请求中解析出pidvid对象
     * 商品的参数是可能存在顺序的，顺序设置在类目上，所以在获取属性列表的时候统一顺序
     * 这样商品的参数也会产生顺序，保证界面的一致性
     *
     * @param request
     * @param categoryProperties
     */
    private ProductPidVid parsePidVid(ProductBrand productBrand, HttpServletRequest request, List<CategoryProperty> categoryProperties) {
        PidVid key = new PidVid();
        PidVid sell = new PidVid();
        Property property = getBrandProperty();
        for (CategoryProperty categoryProperty : categoryProperties) {
            int pid = categoryProperty.getPropertyId();//遍历这个类目下的所有类目属性，通过属性ID去request中得到值ID
            if (pid != property.getId()) { //品牌属性不需要处理，因为品牌这个属性单独处理了
                if (categoryProperty.isMultiValue()) {
                    String[] values = request.getParameterValues("" + pid);
                    for (String value : values) {
                        if (categoryProperty.getPropertyType() == PropertyType.KEY_PROPERTY) {
                            key.add(pid, Integer.parseInt(value), true);
                        }
                        if (categoryProperty.getPropertyType() == PropertyType.SELL_PROPERTY) {
                            sell.add(pid, Integer.parseInt(value), true);
                        }
                    }
                } else {
                    String vid = request.getParameter("" + pid);
                    if (categoryProperty.getPropertyType() == PropertyType.KEY_PROPERTY) {
                        key.add(pid, Integer.parseInt(vid), false);
                    }
                    if (categoryProperty.getPropertyType() == PropertyType.SELL_PROPERTY) {
                        sell.add(pid, Integer.parseInt(vid), false);
                    }
                }
            }
        }
        key.add(productBrand.brandPid, productBrand.brandVid, false);
        return new ProductPidVid(sell, key);
    }

    /**
     * 根据id修改商品是否上下架
     *
     * @param id
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/product/updateOnline")
    public void updateOnlineById(int id, boolean online, HttpServletResponse response) throws IOException {
        try {
            if (online) {
                productService.makeProductOnline(id);
            } else {
                productService.makeProductOffLine(id);
            }
        } catch (Exception e) {
            logger.error("商品管理的修改商品上架状态异常：" + e);
            new JsonResult(false, e.getMessage()).toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }


    private class ProductBrand {

        //商品品牌属性的pid
        int brandPid;

        //商品是什么品牌这个值的vid
        int brandVid;
    }


}
