package com.kariqu.designcenter.client.domain.open.api;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.kariqu.categorycenter.client.container.CategoryContainer;
import com.kariqu.categorycenter.client.domain.PropertyValueStatsInfo;
import com.kariqu.categorycenter.client.service.CategoryPropertyQueryService;
import com.kariqu.categorycenter.domain.model.Property;
import com.kariqu.categorycenter.domain.model.PropertyValueDetail;
import com.kariqu.categorycenter.domain.model.Value;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryPropertyValue;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.service.NavigateCategoryPropertyService;
import com.kariqu.categorycenter.domain.service.NavigateCategoryService;
import com.kariqu.categorycenter.domain.util.PropertyValueUtil;
import com.kariqu.designcenter.domain.open.api.CategoryService;
import com.kariqu.designcenter.domain.open.api.ProductService;
import com.kariqu.designcenter.domain.open.module.BasicProduct;
import com.kariqu.designcenter.domain.open.module.BrandData;
import com.kariqu.designcenter.domain.open.module.CategoryChannelData;
import com.kariqu.designcenter.domain.open.module.CategorySearchData;
import com.kariqu.suppliercenter.domain.Brand;
import com.kariqu.suppliercenter.service.SupplierService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author Tiger
 * @version 1.0.0
 * @since 2011-5-6 下午09:32:07
 */
public class CategoryServiceImpl implements CategoryService {

    private static final Log logger = LogFactory.getLog(CategoryServiceImpl.class);

    @Autowired
    private ProductService openProductService;

    @Autowired
    private CategoryContainer categoryContainer;

    @Autowired
    private SupplierService supplierService;

    @Override
    public List<NavigateCategory> categoryNameTree(int naCategoryId) {
        List<NavigateCategory> categoryTree = new LinkedList<NavigateCategory>();
        NavigateCategoryService serviceClient = categoryContainer.getNavigateCategoryService();
        categoryName(serviceClient, naCategoryId, categoryTree);
        Collections.reverse(categoryTree);
        return categoryTree;
    }

    private void categoryName(NavigateCategoryService serviceClient, int naCategoryId, List<NavigateCategory> categoryTree) {
        NavigateCategory navigateCategory = serviceClient.getNavigateCategory(naCategoryId);
        if (navigateCategory == null) return;

        categoryTree.add(navigateCategory);
        NavigateCategory parentNavCategory = serviceClient.getNavigateCategory(navigateCategory.getParent().getId());
        // 父类目是一级类目则直接返回, 只从二级类目开始算起
        if (parentNavCategory == null || parentNavCategory.getParent().getId() == -1) return;

        categoryName(serviceClient, navigateCategory.getParent().getId(), categoryTree);
    }

    @Override
    public List<NavigateCategory> buildCategory(final int naCategoryId, boolean showAllThird, boolean categorySort) {
        NavigateCategoryService serviceClient = categoryContainer.getNavigateCategoryService();
        // 得到当前类目的根类目(一级类目)
        int subCategoryId = getSubCategoryId(serviceClient, naCategoryId);
        if (subCategoryId == -1) return Collections.EMPTY_LIST;

        // 所有的二级类目
        List<NavigateCategory> childCategory = serviceClient.querySubCategories(subCategoryId);
        int index = 0;
        for (int i = 0; i < childCategory.size(); i++) {
            NavigateCategory navigateCategory = childCategory.get(i);
            if (navigateCategory.getId() == naCategoryId) navigateCategory.setOpen(true);

            // 将二级类目放到集合的最前面去(当前类目是二级类目)
            if (index == 0 && i != 0 && navigateCategory.getId() == naCategoryId) {
                index = i;
            }
            // 显示三级类目
            if (showAllThird || navigateCategory.getId() == naCategoryId) {
                List<NavigateCategory> children = serviceClient.querySubCategories(navigateCategory.getId());
                navigateCategory.setChildren(children);

                // 从当前所有的三级类目中寻找, 若找到了则说明当前类目可能需要往前提
                boolean present = Iterables.tryFind(children, new Predicate<NavigateCategory>() {
                    @Override
                    public boolean apply(NavigateCategory input) {
                        return input.getId() == naCategoryId;
                    }
                }).isPresent();
                if (present) navigateCategory.setOpen(true);
                // 将二级类目放到集合的最前面去(当前类目是三级类目)
                if (index == 0 && i != 0 && present) {
                    index = i;
                }
            }
        }
        // 将当前类目放到第一个位置
        if (index > 0 && categorySort) {
            childCategory.add(0, childCategory.remove(index));
        }
        return childCategory;
    }

    private int getSubCategoryId(NavigateCategoryService serviceClient, int naCategoryId) {
        NavigateCategory navigateCategory = serviceClient.getNavigateCategory(naCategoryId);
        if (navigateCategory == null) return -1;
        if (navigateCategory.getParent().getId() == -1) return navigateCategory.getId();

        return getSubCategoryId(serviceClient, navigateCategory.getParent().getId());
    }

    @Override
    public BasicProduct getCategoryMainRecommendProduct(int naCategoryId) {
        NavigateCategory navigateCategory = queryNavigateCategoryById(naCategoryId);
        if (navigateCategory != null) {
            try {
                int mainProduct = navigateCategory.settingsObject().getMainProduct();
                if (mainProduct != 0) {
                    return openProductService.buildBasicProduct(mainProduct);
                }
            } catch (Exception e) {
                logger.error("类目设置的Json有问题:" + navigateCategory.getSettings(), e);
            }
        }
        return null;
    }

    @Override
    public NavigateCategory getParentCategory(int naCategoryId) {
        NavigateCategory navigateCategory = queryNavigateCategoryById(naCategoryId);
        if (navigateCategory == null || navigateCategory.getParent().getId() == -1) {
            if (logger.isErrorEnabled())
                logger.error("无此前台类目或者当前类目是二级类目:" + naCategoryId);
            return null;
        }
        return queryNavigateCategoryById(navigateCategory.getParent().getId());
    }

    @Override
    public CategoryChannelData buildCategoryChannelData(int naCategoryId) {
        CategoryChannelData data = new CategoryChannelData();
        data.setRootCategoryId(naCategoryId);
        NavigateCategoryService serviceClient = categoryContainer.getNavigateCategoryService();
        NavigateCategory navigateCategory = serviceClient.getNavigateCategory(naCategoryId);
        if (navigateCategory != null) {
            data.setRootCategoryName(navigateCategory.getName());
            try {
                data.setPictureUrl(navigateCategory.settingsObject().getMainImg());
                data.setLinkUrl(navigateCategory.settingsObject().getMainImgLink());
            } catch (Exception e) {
                logger.error("类目设置的Json有问题:" + navigateCategory.getSettings(), e);
            }
            data.setChild(serviceClient.querySubCategories(naCategoryId));
        }
        return data;
    }

    @Override
    public List<BrandData> getBrandUrlByChannel(int channelId) {
        CategoryPropertyQueryService categoryPropertyQueryService = categoryContainer.getCategoryPropertyQueryService();
        List<BrandData> list = new ArrayList<BrandData>();
        // 查询指定频道下的所有品牌
        List<PropertyValueStatsInfo> pvsList = categoryPropertyQueryService.queryValueByNavCategoryIdAndPropertyName(channelId, "品牌");
        for (PropertyValueStatsInfo pi : pvsList) {
            long pidVid = pi.getPidvid();
            PropertyValueUtil.PV pv = PropertyValueUtil.parseLongToPidVid(pidVid);
            Value value = categoryPropertyQueryService.getValueById(pv.vid);
            Brand brand = queryBrandById(pv.vid);
            if (value != null && brand != null) {
                BrandData bd = new BrandData();
                bd.setBrandId(brand.getId());
                bd.setBrandName(value.getValueName());
                bd.setImageUrl(brand.getPicture());
                bd.setPidVid(pidVid);

                list.add(bd);
            } else {
                if (logger.isErrorEnabled())
                    logger.error("值或品牌(" + pv.vid + ")为空.");
            }
        }
        return list;
    }

    @Override
    public Brand queryBrandById(int valueId) {
        return supplierService.queryBrandById(valueId);
    }

    @Override
    public String getCategoryPropertyDetail(int propertyId, int valueId) {
        CategoryPropertyQueryService categoryPropertyQueryService = categoryContainer.getCategoryPropertyQueryService();
        PropertyValueDetail detail = categoryPropertyQueryService.getCategoryPropertyValueDetail(propertyId, valueId);
        if (detail != null) {
            return detail.getPictureUrl();
        } else {
            logger.error("没有设置属性值描述,属性是" + propertyId + "属性值是" + valueId);
            return "";
        }
    }

    @Override
    public boolean isColorProperty(int propertyId) {
        CategoryPropertyQueryService categoryPropertyQueryService = categoryContainer.getCategoryPropertyQueryService();
        Property property = categoryPropertyQueryService.getPropertyByName("颜色");
        return property.getId() == propertyId;
    }

    @Override
    public CategorySearchData getNavigateCategorySearchableInfo(String navCategoryId) {
        CategorySearchData categorySearchData = new CategorySearchData();
        if (StringUtils.isBlank(navCategoryId)) return categorySearchData;

        CategoryPropertyQueryService categoryPropertyQueryService = categoryContainer.getCategoryPropertyQueryService();
        NavigateCategoryPropertyService navigateCategoryPropertyService = categoryContainer.getNavigateCategoryPropertyService();
        List<NavCategoryProperty> navCategoryProperties = new LinkedList<NavCategoryProperty>();
        for (String navId : navCategoryId.split(",")) {
            int nav = NumberUtils.toInt(navId);
            if (nav > 0)
                navCategoryProperties.addAll(navigateCategoryPropertyService.querySearchableNavCategoryProperty(nav));
        }
        for (NavCategoryProperty navCategoryProperty : navCategoryProperties) {
            List<NavCategoryPropertyValue> navCategoryPropertyValues = navigateCategoryPropertyService.queryNavCategoryPropertyValues(navCategoryProperty.getNavCategoryId(),
                    navCategoryProperty.getPropertyId());
            List<CategorySearchData.SearchValue> searchValueList = new ArrayList<CategorySearchData.SearchValue>(navCategoryPropertyValues.size());
            for (NavCategoryPropertyValue navCategoryPropertyValue : navCategoryPropertyValues) {
                searchValueList.add(new CategorySearchData.SearchValue(categoryPropertyQueryService.getValueById(navCategoryPropertyValue.getValueId()).getValueName(),
                        PropertyValueUtil.mergePidVidToLong(navCategoryProperty.getPropertyId(), navCategoryPropertyValue.getValueId())));
            }
            categorySearchData.putCategorySearchInfo(categoryPropertyQueryService.getPropertyById(navCategoryProperty.getPropertyId()), searchValueList);
        }
        return categorySearchData;
    }

    @Override
    public NavigateCategory queryNavigateCategoryById(int navCategoryId) {
        NavigateCategoryService navigateCategoryService = CategoryContainer.getNavigateCategoryService();
        return navigateCategoryService.getNavigateCategory(navCategoryId);
    }

    @Override
    public NavigateCategory queryNavigateCategoryById(String navCategoryId) {
        return queryNavigateCategoryById(NumberUtils.toInt(navCategoryId));
    }

}
