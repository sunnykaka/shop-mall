package com.kariqu.searchengine.service;

import com.kariqu.categorycenter.client.container.CategoryContainer;
import com.kariqu.categorycenter.client.domain.PropertyValueStatsInfo;
import com.kariqu.categorycenter.client.service.CategoryPropertyQueryService;
import com.kariqu.categorycenter.domain.model.ProductCategory;
import com.kariqu.categorycenter.domain.model.Property;
import com.kariqu.categorycenter.domain.model.Value;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.service.CategoryAssociationService;
import com.kariqu.categorycenter.domain.service.NavigateCategoryService;
import com.kariqu.categorycenter.domain.service.ProductCategoryService;
import com.kariqu.categorycenter.domain.util.PropertyValueUtil;
import com.kariqu.searchengine.domain.CountStatsNode;
import com.kariqu.searchengine.domain.SearchStatsInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 分析搜索结果
 * 如果只有一套类目：
 * 商品按照类目统计，如果搜到了商品在同一个类目下，则显示属性筛选器，这种情况有两种：
 * 1，通过关键字搜索，确实在同一个类目下
 * 2，通过类目Id和关键字搜索，肯定是在同一个类目下
 * 这个时候的统计结果类似：笔记本（20）品牌：三星（10）...颜色：绿色（1），颜色：红色（2）
 * 如果有两套类目：前台和后台
 * 由于前台类目可以说随意聚合后台的父亲和儿子，所以要判断是否在同一个类目下比较麻烦，这个时候就取属性统计的前几个
 * 但是如果是通过前台类目搜索的时候，就可以将前台类目所关联的属性和值进行筛选
 * User: Asion
 * Date: 11-8-8
 * Time: 下午4:45
 */

public class SearchResultParserService {

    @Autowired
    private CategoryContainer categoryContainer;

    /**
     * 统计属性和属性值
     *
     * @param categoryCount     按后台叶子类目统计
     * @param pidvidStatsResult 按类目属性统计
     * @return
     */
    public SearchStatsInfo statsPv(int categoryCount, List<CountStatsNode<Long>> pidvidStatsResult,
                                   int propertyMaxNumber, int valueMaxNumber, List<String> propertyArrayName) {
        SearchStatsInfo result = new SearchStatsInfo();

        // 如果返回的类目叶子统计大于 1, 说明在不同的类目下搜到了商品, 在搜索结果页就没有显示筛选器的必要.
        // 注意这里是通过后台类目来判断的, 正在上线之后应该是通过前台类目, 但是前台类目没有索引, 所以这是个问题.
        result.setSingleCategory(categoryCount < 2);

        // 分析返回的pidvid，即一串long值和各自的数量，long值的前32位是pid,后32位是vid，这个完全是按照搜索统计出的属性
        // 如果是通过前台类目筛选的时候，通过类目的属性来筛选可能更有意义
        CategoryPropertyQueryService categoryPropertyQueryService = categoryContainer.getCategoryPropertyQueryService();
        for (CountStatsNode<Long> longCountStatsNode : pidvidStatsResult) {
            long pv = longCountStatsNode.getType();
            PropertyValueUtil.PV pvResult = PropertyValueUtil.parseLongToPidVid(pv);
            Property property = categoryPropertyQueryService.getPropertyById(pvResult.pid);
            Value value = categoryPropertyQueryService.getValueById(pvResult.vid);
            result.putPropertyValueStaticInfo(property, new PropertyValueStatsInfo(value.getValueName(), longCountStatsNode.getCount(), pv));
        }
        return result.selectPropertyAndValue(propertyMaxNumber, valueMaxNumber, propertyArrayName);
    }

    /**
     * 统计后台类目树上的数量，原理是加载出整个树，然后从根开始，判断自身和其后代是否有这个商品的类目ID，有则自己加上数量，然后递归下去
     *
     * @param leafStatResult
     * @return
     */
    public List<ProductCategory> statsProductCategoryTree(List<CountStatsNode<Integer>> leafStatResult) {
        ProductCategoryService productCategoryService = categoryContainer.getProductCategoryService();
        List<ProductCategory> productCategories = productCategoryService.loadCategoryTree();
        for (CountStatsNode<Integer> node : leafStatResult) {
            computeProductCategoryStatsCount(productCategories, node);
        }
        return productCategories;
    }

    /**
     * 统计前台类目上的数量
     * 因为前台可以随意聚合后台的节点，不是严格的层级节点，所以统计出来的数量可能没有意义，但是如果运营按照父亲聚合父亲，叶子聚合叶子的方法进行层级聚合，则统计出来的数量也是有意义的
     * 统计原理是，先找出聚合这个类目的前台类目，然后递归的叠加数量
     *
     * @param leafStatsResult
     * @return
     */
    public List<NavigateCategory> statsNavigateCategoryTree(List<CountStatsNode<Integer>> leafStatsResult) {
        NavigateCategoryService navigateCategoryService = categoryContainer.getNavigateCategoryService();
        CategoryAssociationService categoryAssociationService = categoryContainer.getCategoryAssociationService();
        List<NavigateCategory> navigateCategories = navigateCategoryService.loadNavCategoryTree();
        for (CountStatsNode<Integer> node : leafStatsResult) {
            List<Integer> navIds = categoryAssociationService.queryAssociationByCategoryId(node.getType());//关联的前台叶子类目
            for (Integer navId : navIds) {
                CountStatsNode<Integer> newNode = new CountStatsNode<Integer>(navId, node.getCount());
                computeNavigateCategoryStatsCount(navigateCategories, newNode);
            }
        }
        return navigateCategories;
    }

    /**
     * 计算后台类目树上的数量
     *
     * @param children
     * @param node
     */
    private void computeProductCategoryStatsCount(List<ProductCategory> children, CountStatsNode<Integer> node) {
        if (null == children || children.isEmpty()) {
            return;
        }
        for (ProductCategory child : children) {
            //如果后代有这个类目则增加统计数量
            if (child.containsDescendant(node.getType()) || child.getId() == node.getType()) {
                child.increment(node.getCount());
                computeProductCategoryStatsCount(child.getChildren(), node);
            }
        }
    }

    /**
     * 计算前台类目树上的数量
     *
     * @param children
     * @param node
     */
    private void computeNavigateCategoryStatsCount(List<NavigateCategory> children, CountStatsNode<Integer> node) {
        if (null == children || children.isEmpty()) {
            return;
        }
        for (NavigateCategory child : children) {
            //如果后代有这个类目则增加统计数量
            if (child.containsDescendant(node.getType()) || child.getId() == node.getType()) {
                child.increment(node.getCount());
                computeNavigateCategoryStatsCount(child.getChildren(), node);
            }
        }
    }


}
