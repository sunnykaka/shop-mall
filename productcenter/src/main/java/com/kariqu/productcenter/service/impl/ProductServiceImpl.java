package com.kariqu.productcenter.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.kariqu.categorycenter.domain.model.PropertyType;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.repository.HtmlRepository;
import com.kariqu.productcenter.repository.ProductPictureRepository;
import com.kariqu.productcenter.repository.ProductPropertyRepository;
import com.kariqu.productcenter.repository.ProductRepository;
import com.kariqu.productcenter.service.ProductPictureResolver;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.productcenter.service.SkuStorageService;
import com.kariqu.suppliercenter.domain.Brand;
import com.kariqu.suppliercenter.repository.BrandRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: Asion
 * Date: 11-7-13
 * Time: 下午4:36
 */

@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = Logger.getLogger(ProductServiceImpl.class);

    @Autowired
    protected URLBrokerFactory urlBrokerFactory;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductPictureRepository productPictureRepository;

    @Autowired
    private HtmlRepository htmlRepository;

    @Autowired
    private ProductPropertyRepository productPropertyRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private SkuService skuService;

    @Autowired
    private SkuStorageService skuStorageService;

    /**
     * 获取商品基本信息
     *
     * @param productId
     * @return
     */
    public Map<String, Object> getProductMap(int productId) {
        Map<String, Object> map = new HashMap<String, Object>();
        Product product = getProductById(productId);
        if (null == product) return null;

        map.put("url", urlBrokerFactory.getUrl("ProductDetail").addQueryData("productId", product.getId()).toString());
        Brand brand = brandRepository.queryBrandById(product.getBrandId());
        map.put("brandName", brand == null ? "" : brand.getName());
        map.put("product", product.getName());
        map.put("picture", getMainPicture(productId));
        map.put("price", getProductPrice(productId));
        return map;
    }

    /**
     * 获取商品基本信息,包含商品信息
     *
     * @param productId
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getProductMapWithproductInfo(int productId, int skuId) {
        Map<String, Object> map = new HashMap<String, Object>();
        Product product = getProductById(productId);
        if (null == product) {
            return null;
        }
        map.put("url", urlBrokerFactory.getUrl("ProductDetail").addQueryData("productId", product.getId()).toString());
        map.put("product", product.getName());

        List<ProductPicture> skuPics = getProductPictureBySkuId(skuId);
        if (skuPics != null && skuPics.size() > 0) {
            map.put("picture", ProductPictureResolver.getMinSizeImgUrl(skuPics.get(0).getPictureUrl()));
        } else {
            map.put("picture", getMainPicture(productId));
        }

        map.put("productInfo", product);
        return map;
    }

    /**
     * 取列表主图
     */
    private String getMainPicture(int productId) {
        PictureDesc pictureDesc = getPictureDesc(productId);
        if ("not found".equals(pictureDesc.getMainPicture().getName())) {
            return pictureDesc.getMainPicture().getPictureUrl();
        }

        return ProductPictureResolver.getMinSizeImgUrl(pictureDesc.getMainPicture().getPictureUrl());
    }

    @Override
    @Transactional
    public void makeProductOnline(int productId) throws Exception {
        List<Html> htmls = htmlRepository.queryByProductId(productId);
        if (htmls.size() == 0) {
            throw new Exception("没有商品描述，不能发布");
        }
        List<ProductPicture> productPictures = productPictureRepository.queryProductPicturesByProductId(productId);
        if (productPictures.size() == 0) {
            throw new Exception("没有商品图片,不能发布");
        }

        boolean hasMainPicture = false;
        for (ProductPicture productPicture : productPictures) {
            if (productPicture.isMainPic()) {
                hasMainPicture = true;
            }
        }
        if (!hasMainPicture) {
            throw new Exception("没有商品主图不能发布");
        }

        List<StockKeepingUnit> stockKeepingUnits = skuService.queryProductSkuWithStock(productId);

        boolean hasValidSku = false;

        for (StockKeepingUnit stockKeepingUnit : stockKeepingUnits) {
            if (stockKeepingUnit.getSkuState() == StockKeepingUnit.SKUState.NORMAL) {
                hasValidSku = true;
                SkuStorage skuStorage = skuStorageService.getSkuStorage(stockKeepingUnit.getId());
                if (skuStorage == null) {
                    throw new Exception("SKU没有指定库位");
                }
                if (skuStorage.getStockQuantity() == 0) {
                    throw new Exception("有库存为0的SKU，不能发布");
                }
            }
        }

        if (!hasValidSku) {
            throw new Exception("商品没有有效的sku，不能发布");
        }
        productRepository.changeProductOnlineStatus(productId, true);
    }

    @Override
    public String getMainProductPicture(int productId) {
        ProductPicture productPicture = productPictureRepository.getMainProductPictureByProductId(productId);
        return productPicture != null ? productPicture.getPictureUrl() : "";
    }

    @Override
    public String getMinorProductPicture(int productId) {
        ProductPicture productPicture = productPictureRepository.getMinorProductPictureByProductId(productId);
        return productPicture != null ? productPicture.getPictureUrl() : "";
    }

    @Override
    public void deleteProductPictureById(int id) {
        productPictureRepository.deleteProductPictureById(id);
    }

    @Override
    public void makeProductOffLine(int productId) {
        productRepository.changeProductOnlineStatus(productId, false);

    }

    @Override
    public void notifyProductUpdate(int productId) {
        productRepository.updateProductModifyTime(productId);
    }

    @Override
    public HtmlDesc getHtmlDesc(int productId) {
        HtmlDesc htmlDesc = new HtmlDesc();
        htmlDesc.setHtmlList(htmlRepository.queryByProductId(productId));
        htmlDesc.setProductId(productId);
        return htmlDesc;
    }

    @Override
    public Recommend getRecommend(int productId) {
        Recommend recommend = new Recommend();
        recommend.setProductId(productId);
        recommend.setAccessoryProducts(productRepository.getProductIds(productId, RecommendType.ACCESSORY));
        recommend.setComposeProducts(productRepository.getProductIds(productId, RecommendType.COMPOSE));
        recommend.setCategoryProducts(productRepository.getProductIds(productId, RecommendType.CATEGORY));
        return recommend;
    }


    @Override
    public void deleteProduct(int id) {
        //商品只是逻辑删除，商品的所有信息都还保存在服务器上
        productRepository.deleteProductById(id);
    }

    @Override
    public PictureDesc getPictureDesc(int productId) {
        List<ProductPicture> productPictureList = productPictureRepository.queryProductPicturesByProductId(productId);
        PictureDesc pictureDesc = new PictureDesc();
        pictureDesc.setProductId(productId);
        pictureDesc.setPictures(productPictureList);
        return pictureDesc;
    }

    @Override
    public List<String> getProductPictureUrl(int productId, int limit) {
        return productPictureRepository.queryProductPictureUrlByProductId(productId, limit);
    }

    @Override
    public ProductPicture getMainPictureBySKuId(long skuId, int productId) {
        List<ProductPicture> productPictures = getProductPictureBySkuId(skuId);
        if (productPictures == null || productPictures.size() == 0) {
            PictureDesc pictureDesc = getPictureDesc(productId);
            if (pictureDesc == null) return null;

            return pictureDesc.getMainPicture();
        }

        return productPictures.get(0);
    }

    @Override
    public ProductPicture getProductPictureById(int id) {
        return productPictureRepository.getProductPictureById(id);
    }

    @Override
    @Transactional
    public void setMinorProductPicture(int id) {
        ProductPicture productPicture = productPictureRepository.getProductPictureById(id);
        productRepository.updateProductModifyTime(productPicture.getProductId());

        productPictureRepository.makeProductPictureAsMinor(productPicture);
    }

    @Override
    public List<ProductPicture> getProductPictureBySkuId(long skuId) {
        return productPictureRepository.queryProductPicturesBySkuId(String.valueOf(skuId));
    }

   /* @Override
    public void updatePictureSkuId(int id, long skuId) {
        productPictureRepository.updateProductPictureSkuId(id, skuId);
    }*/

    @Override
    @Transactional
    public void addProductPictureForSku(int[] ids, long skuId) {
        for(int id : ids){
            ProductPicture productPicture = productPictureRepository.getProductPictureById(id);
            String skuIdStr = "";
            boolean flag = false;
            if(StringUtils.isEmpty(productPicture.getSkuId()) || "0".equals(productPicture.getSkuId())){
                skuIdStr = String.valueOf(skuId);
                flag = true;
            }else if(!productPicture.getSkuId().contains(String.valueOf(skuId))){
                skuIdStr = productPicture.getSkuId() + "," + String.valueOf(skuId);
                flag = true;
            }else{
                flag = false;
            }

            if(flag){
                productPictureRepository.updateProductPictureSkuId(productPicture.getId(),skuIdStr);
            }
        }
    }

    @Override
    @Transactional
    public void removeProductPictureForSku(int[] ids, long skuId) {

        for(int id : ids){
            ProductPicture productPicture = productPictureRepository.getProductPictureById(id);
            if(productPicture.getSkuId().contains(String.valueOf(skuId))){
                Splitter ruleSplitter = Splitter.on(',').omitEmptyStrings().trimResults();
                List<String> skuIdList = Lists.newArrayList(ruleSplitter.splitToList(productPicture.getSkuId()));
                if(skuIdList.contains(String.valueOf(skuId))){
                    skuIdList.remove(String.valueOf(skuId));
                }
                productPictureRepository.updateProductPictureSkuId(productPicture.getId(),skuIdList.toString().replace("[","").replace("]",""));
            }
        }
    }

    @Override
    public void updatePictureNumber(int id, int num) {
        productPictureRepository.updateProductPictureNum(id, num);
    }

    @Override
    public boolean existProduct(int categoryId) {
        return productRepository.existProduct(categoryId);
    }

    @Override
    @Transactional
    public int createProduct(Product product) {
        productRepository.createProduct(product);
        return product.getId();
    }

    @Override
    public void createRecommendProduct(int productId, int recommendProductId, RecommendType recommendType) {
        productRepository.createRecommendProduct(productId, recommendProductId, recommendType);
    }

    @Override
    @Transactional
    public void deleteRecommendProduct(int productId, int recommendProductId, RecommendType recommendType) {
        productRepository.deleteRecommendProduct(productId, recommendProductId, recommendType);
    }


    @Override
    public boolean existMainPicture(int productId) {
        return productPictureRepository.getMainProductPictureByProductId(productId) != null;
    }

    @Override
    @Transactional
    public void createProductPicture(ProductPicture productPicture) {
        productPictureRepository.createProductPicture(productPicture);
        if (productPicture.isMainPic()) {
            productRepository.updateProductModifyTime(productPicture.getProductId());
        }
    }

    @Override
    public Product getSimpleProductById(int id) {
        return productRepository.getProductById(id);
    }

    @Override
    public Product getProductById(int id) {
        Product product = getSimpleProductById(id);
        if (product != null) {
            injectSKU(product);
        }
        return product;
    }

    @Override
    public Product getProductByCode(String productCode) {
        if (StringUtils.isBlank(productCode)) return null;

        List<Product> products = productRepository.getProductByCode(productCode);
        if (products == null || products.size() == 0) return null;
        if (products.size() > 1) LOGGER.warn("商品编码(" + productCode + ")设置在了多个商品上!");

        Product product = products.get(0);
        injectSKU(product);
        return product;
    }


    @Override
    public void updateProduct(Product product) {
        productRepository.updateProduct(product);
    }


    @Override
    public List<Product> queryProductsByCategoryId(int categoryId) {
        List<Product> productList = productRepository.queryProductsByCategoryId(categoryId);
        for (Product product : productList) {
            injectSKU(product);
        }
        return productList;
    }

    @Override
    public Page<Product> queryProductsByOnlineStatus(boolean online, Page<Product> page) {
        Page<Product> productList = productRepository.queryProductsByOnlineStatus(online, page);
        for (Product product : productList.getResult()) {
            injectSKU(product);
        }
        return productList;
    }

    @Override
    public Page<Product> queryProductsByCategoryIdByPage(int categoryId, int pageNo, int limit) {
        Page<Product> productPage = productRepository.queryProductsByCategoryIdByPage(categoryId, pageNo, limit);
        for (Product product : productPage.getResult()) {
            injectSKU(product);
        }
        return productPage;
    }

    @Override
    public Page<Product> queryProductsByCustomerIdByPage(int customerId, int pageNo, int limit) {
        Page<Product> productPage = productRepository.queryProductsByCustomerIdByPage(customerId, pageNo, limit);
        for (Product product : productPage.getResult()) {
            injectSKU(product);
        }
        return productPage;
    }

    @Override
    public Page<Product> queryProductsByBrandIdByPage(int brandId, int pageNo, int limit) {
        Page<Product> productPage = productRepository.queryProductsByBrandIdByPage(brandId, pageNo, limit);
        for (Product product : productPage.getResult()) {
            injectSKU(product);
        }
        return productPage;
    }

    @Override
    public Page<Product> queryProductsByStoreIdByPage(int storeId, int pageNo, int limit) {
        Page<Product> productPage = productRepository.queryProductsByStoreIdByPage(storeId, pageNo, limit);
        for (Product product : productPage.getResult()) {
            injectSKU(product);
        }
        return productPage;
    }

    @Override
    public Page<Product> queryAllProductsByPage(int pageNo, int limit) {
        Page<Product> productPage = productRepository.queryAllProductsByPage(pageNo, limit);
        for (Product product : productPage.getResult()) {
            injectSKU(product);
        }
        return productPage;
    }


    @Override
    public List<Product> queryProductByFuzzy(String search) {
        return productRepository.queryProductByFuzzy(search);
    }

    /**
     * 注入SKU列表
     */
    private void injectSKU(Product product) {
        product.setStockKeepingUnits(skuService.querySKUByProductId(product.getId()));
    }

    @Override
    public void createProductProperty(ProductProperty productProperty) {
        productPropertyRepository.createProductProperty(productProperty);
    }

    @Override
    public void updateProductPropertyBrandJsonByPropertyType(String json, int productId, PropertyType propertyType) {
        productPropertyRepository.updateProductPropertyBrandJsonByPropertyType(json, productId, propertyType);
    }

    @Override
    public ProductProperty getProductPropertyByPropertyType(int productId, PropertyType propertyType) {
        return productPropertyRepository.queryProductPropertyByPropertyType(productId, propertyType);
    }

    @Override
    public void deleteProductProperty(int productId) {
        productPropertyRepository.deleteProductPropertyByProductId(productId);
    }

    @Override
    public void deleteProductPropertyByPropertyType(int productId, PropertyType propertyType) {
        productPropertyRepository.deleteProductPropertyByPropertyType(productId, propertyType);
    }

    @Override
    public void createHtml(Html html) {
        htmlRepository.createHtml(html);
    }

    @Override
    public void updateHtml(Html html) {
        htmlRepository.updateHtml(html);
    }

    @Override
    public void deleteHtmlDesc(int productId, String[] names) {
        for (String name : names) {
            htmlRepository.deleteHtmlByProductIdAndName(productId, name);
        }
    }

    public void deleteRecommendBy(int byProductId) {
        productRepository.deleteRecommendByProductId(byProductId);
    }

    @Override
    @Transactional
    public void setMainProductPicture(int id) {

        ProductPicture productPicture = productPictureRepository.getProductPictureById(id);
        productRepository.updateProductModifyTime(productPicture.getProductId());

        productPictureRepository.makeProductPictureAsMain(productPicture);
    }



    @Override
    public Page<Product> queryProductByFuzzyPage(String search, int pageNo, int limit) {
        return productRepository.queryProductByFuzzyPage(search, pageNo, limit);
    }

    @Override
    public boolean existProductCode(String productCode) {
        int amount = productRepository.queryProductAmountByProductCode(productCode);
        return amount > 0;
    }

    @Override
    public List<Integer> queryDeletedProductFromGivingTime(Date date) {
        return productRepository.queryDeletedProductFromGivingTime(date);
    }


    @Override
    public void updateProductStrategy(int productId, StoreStrategy storeStrategy) {
        productRepository.updateProductStrategy(productId, storeStrategy);
    }


    /**
     * 取商品最低价格
     */
    @Override
    public String getProductPrice(int productId) {
        StockKeepingUnit minPriceSku = fetchMinPriceSku(productId);
        if (minPriceSku != null) return Money.getMoneyString(minPriceSku.getPrice());

        return "价格未定";
    }

    //查找指定商品的价格最小的sku, 如果没有找到返回null
    private StockKeepingUnit fetchMinPriceSku(int productId) {
        StockKeepingUnit minPriceSku = null;
        List<StockKeepingUnit> skuList = skuService.querySKUByProductId(productId);
        if (null != skuList && skuList.size() > 0) {
            long price = Long.MAX_VALUE;
            for (StockKeepingUnit sku : skuList) {
                // sku 有效、价格大于 0， 并且是最小的价格
                if (sku.getSkuState() == StockKeepingUnit.SKUState.NORMAL
                        && sku.getPrice() > 0 && sku.getPrice() < price) {
                    price = sku.getPrice();
                    minPriceSku = sku;
                }
            }
        }
        return minPriceSku;
    }

    /**
     * 商品是否有库存
     */
    @Override
    public boolean getProductHasStock(int productId) {
        return skuService.getSumStockQuantityByProductId(productId) > 0;
    }


    @Override
    @Transactional
    public void createProductCollect(ProductCollect productCollect) {
        productRepository.createProductCollect(productCollect);
    }

    @Override
    public boolean hadProductCollect(ProductCollect productCollect) {
        return productRepository.hadProductCollect(productCollect);
    }

    @Override
    public Page<ProductCollect> queryProductCollectByPage(int pageNo, int pageSize, int userId) {
        return productRepository.queryProductCollectByPage(pageNo, pageSize, userId);
    }

    @Override
    @Transactional
    public void deleteProductCollectById(int id, int userId) {
        productRepository.deleteProductCollectById(id, userId);
    }

    @Override
    @Transactional
    public void deleteProductCollectByIds(int[] ids, int userId) {
        productRepository.deleteProductCollectByIds(ids, userId);
    }

    @Override
    @Transactional
    public void cancelProductCollectByIds(int[] ids, int userId) {
        productRepository.cancelProductCollectByIds(ids, userId);
    }

    @Override
    @Transactional
    public void cancelProductCollect(int id, int userId) {
        productRepository.cancelProductCollect(id, userId);
    }

    @Override
    public Integer queryProductCollectNum(int userId) {
        return productRepository.queryProductCollectNum(userId);
    }

    @Override
    public int deleteBrandNameForAllProduct() {
        List<Brand> brandList = brandRepository.queryAllBrand();
        List<Product> productList = productRepository.queryAllProducts();
        int count = 0;
        for (Product product : productList) {
            String productName = product.getName();
            if (StringUtils.isBlank(productName)) continue;

            for (Brand brand : brandList) {
                String brandName = brand.getName();
                if (StringUtils.isBlank(brandName)) continue;

                if (productName.contains(brandName)) {
                    int start = productName.indexOf(brandName) + brandName.length();
                    String changeName = productName.substring(start, productName.length());
                    try {
                        count += productRepository.updateProductName(product.getId(), changeName);
                    } catch (Exception e) {
                        LOGGER.error(String.format("将商品(%s)名(%s)改成(%s)时发生错误: %s",
                                product.getId(), productName, changeName, e.getMessage()));
                    }
                    // 替换后跳出内层循环, 提高效率
                    break;
                }
            }
        }
        return count;
    }
}
