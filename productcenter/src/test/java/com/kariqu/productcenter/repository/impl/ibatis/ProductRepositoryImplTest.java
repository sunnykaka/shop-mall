package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.domain.RecommendType;
import com.kariqu.productcenter.domain.StoreStrategy;
import com.kariqu.productcenter.repository.ProductRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * User: Asion
 * Date: 11-6-27
 * Time: 上午10:55
 */
@SpringApplicationContext({"classpath:productCenter.xml"})
public class ProductRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("productRepository")
    private ProductRepository productRepository;

    @Test
    public void testProductRepository() {
        Product product = new Product();
        product.setName("中兴V960 限时下单立减200！1599成交！再送超值赠品！限时下单立");
        product.setDescription("ddddddddddddddd");
        product.setCategoryId(1);
        product.setBrandId(1);
        product.setCustomerId(1);
        product.setStoreStrategy(StoreStrategy.NormalStrategy);
        product.setUpdateTime(new Date());
        productRepository.createProduct(product);


        assertEquals("中兴V960 限时下单立减200！1599成交！再送超值赠品！限时下单立", productRepository.getProductById(product.getId()).getName());
        assertEquals("ddddddddddddddd", productRepository.getProductById(product.getId()).getDescription());
        assertEquals(1, productRepository.queryAllProducts().size());
        assertEquals(1, productRepository.queryProductsByCategoryId(1).size());
        assertEquals(0,productRepository.queryProductsByOnlineStatus(true,new Page<Product>()).getResult().size());


        product.setCategoryId(3);
        product.setName("中兴V960 限时下单立减200！1599成交！再送超值赠品！限时下单立s");
        product.setDescription("udpatedddddddddddddddd");
        productRepository.updateProduct(product);
        assertEquals("中兴V960 限时下单立减200！1599成交！再送超值赠品！限时下单立s", productRepository.getProductById(product.getId()).getName());
        assertEquals("udpatedddddddddddddddd", productRepository.getProductById(product.getId()).getDescription());
        assertEquals(true, productRepository.existProduct(3));

        assertEquals(1, productRepository.queryProductByFuzzy("960").size());
        assertEquals(1, productRepository.queryProductByFuzzy("成交").size());
        assertEquals(1, productRepository.queryProductByFuzzy("中兴").size());


        assertFalse(productRepository.getProductById(product.getId()).isOnline());
        productRepository.changeProductOnlineStatus(product.getId(), true);
        assertTrue(productRepository.getProductById(product.getId()).isOnline());
        assertEquals(1,productRepository.queryProductsByOnlineStatus(true,new Page<Product>()).getResult().size());

        assertNotNull(productRepository.getProductById(product.getId()).getOnlineTime());
        assertNotNull(productRepository.getProductById(product.getId()).getOnLineTimeLong());
        assertNull(productRepository.getProductById(product.getId()).getOfflineTime());
        productRepository.changeProductOnlineStatus(product.getId(), false);
        assertFalse(productRepository.getProductById(product.getId()).isOnline());
        assertNotNull(productRepository.getProductById(product.getId()).getOfflineTime());


        productRepository.deleteProductById(product.getId());
        assertEquals(0, productRepository.queryAllProducts().size());
        assertEquals(false, productRepository.existProduct(3));

        productRepository.createRecommendProduct(1, 2, RecommendType.ACCESSORY);
        productRepository.createRecommendProduct(1, 4, RecommendType.ACCESSORY);
        productRepository.createRecommendProduct(1, 3, RecommendType.COMPOSE);
        productRepository.createRecommendProduct(1, 5, RecommendType.COMPOSE);
        productRepository.createRecommendProduct(1, 6, RecommendType.CATEGORY);
        productRepository.createRecommendProduct(1, 7, RecommendType.CATEGORY);
        assertEquals(2, productRepository.getProductIds(1, RecommendType.ACCESSORY).size());
        assertEquals(2, productRepository.getProductIds(1, RecommendType.COMPOSE).size());
        assertEquals(2, productRepository.getProductIds(1, RecommendType.CATEGORY).size());

        productRepository.deleteRecommendProduct(1, 2, RecommendType.ACCESSORY);
        assertEquals(1, productRepository.getProductIds(1, RecommendType.ACCESSORY).size());

        productRepository.deleteRecommendProduct(1, 5, RecommendType.COMPOSE);
        assertEquals(1, productRepository.getProductIds(1, RecommendType.COMPOSE).size());

    }

    @Test
    public void testLogicDeleteProduct() {
        Product product = new Product();
        product.setName("中兴V960 限时下单立减200！1599成交！再送超值赠品！限时下单立");
        product.setDescription("ddddddddddddddd");
        product.setCategoryId(1);
        product.setBrandId(1);
        product.setCustomerId(1);
        product.setStoreStrategy(StoreStrategy.NormalStrategy);
        product.setUpdateTime(new Date());
        productRepository.createProduct(product);
        productRepository.deleteProductById(product.getId());

        List<Integer> productIds = productRepository.queryDeletedProductFromGivingTime(new Date(product.getUpdateTime().getTime() - 10000 * 60));
        assertTrue(productIds.contains(product.getId()));
        productRepository.deleteProductById(product.getId());
    }

    @Test
    public void testProductStrategy() {
        Product product = new Product();
        product.setName("WMF福腾宝28cm中华炒锅");
        product.setDescription("ddddddddddddddd");
        product.setCategoryId(1);
        product.setStoreStrategy(StoreStrategy.NormalStrategy);
        product.setBrandId(1);
        product.setCustomerId(1);
        product.setUpdateTime(new Date());
        product.setStoreStrategy(StoreStrategy.NormalStrategy);

        productRepository.createProduct(product);

        productRepository.updateProductStrategy(product.getId(), StoreStrategy.PayStrategy);
    }

}
