package com.kariqu.categorycenter.domain.service.impl;

import com.kariqu.categorycenter.domain.model.ProductCategory;
import com.kariqu.categorycenter.domain.repository.CategoryPropertyRepository;
import com.kariqu.categorycenter.domain.repository.CategoryPropertyValueRepository;
import com.kariqu.categorycenter.domain.repository.NavigateCategoryRepository;
import com.kariqu.categorycenter.domain.repository.ProductCategoryRepository;
import com.kariqu.categorycenter.domain.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Asion
 * Date: 11-6-26
 * Time: 下午1:36
 */
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private NavigateCategoryRepository navigateCategoryRepository;

    @Autowired
    private CategoryPropertyRepository categoryPropertyRepository;

    @Autowired
    private CategoryPropertyValueRepository categoryPropertyValueRepository;

    @Override
    public List<ProductCategory> querySubCategories(int parentId) {
        return productCategoryRepository.querySubCategoryTree(parentId);
    }

    @Override
    public List<ProductCategory> loadCategoryTree() {
        return loadCategoryTreeById(-1);
    }

    @Override
    public List<ProductCategory> loadCategoryTreeById(int categoryId) {
        List<ProductCategory> productCategories = this.querySubCategories(categoryId);
        init(productCategories, new ProductCategory());
        return productCategories;
    }

    @Override
    public List<ProductCategory> getParentCategories(int categoryId, boolean includedAll) {
        List<ProductCategory> productCategories = recursiveCategoryTree(categoryId, includedAll);
        Collections.reverse(productCategories);
        return productCategories;
    }

    @Override
    public ProductCategory queryTopCategoryById(int categoryId) {
        ProductCategory category = this.getProductCategoryById(categoryId);
        if (null != category && category.getParent().getId() != -1) {
            return queryTopCategoryById(category.getParent().getId());
        }
        return category;
    }

    private List<ProductCategory> recursiveCategoryTree(int categoryId, boolean includedAll) {
        List<ProductCategory> productCategories = new ArrayList<ProductCategory>();
        ProductCategory productCategory = getProductCategoryById(categoryId);
        if (productCategory.getParent().getId() == -1) {
            return Collections.<ProductCategory>emptyList();
        } else if (!includedAll) {
            productCategories.add(getProductCategoryById(productCategory.getParent().getId()));
            return productCategories;
        } else {
            ProductCategory parent = getProductCategoryById(productCategory.getParent().getId());
            productCategories.add(parent);
            productCategories.addAll(recursiveCategoryTree(parent.getId(), true));
            return productCategories;
        }
    }

    private void init(List<ProductCategory> children, ProductCategory parent) {
        if (null == children || children.isEmpty()) {
            return;
        }
        parent.setChildren(children);
        for (ProductCategory child : children) {
            List<ProductCategory> subCategoryies = this.querySubCategories(child.getId());
            init(subCategoryies, child);
        }
    }

    @Override
    @Transactional
    public int createProductCategory(ProductCategory productCategory) {
        productCategoryRepository.create(productCategory);
        return productCategory.getId();
    }


    @Override
    public ProductCategory getProductCategoryById(int id) {
        return productCategoryRepository.getProductCategoryById(id);
    }

    @Override
    @Transactional
    public void updateProductCategory(ProductCategory productCategory) {
        productCategoryRepository.updateProductCategory(productCategory);
    }

    @Override
    @Transactional
    public void deleteProductCategory(int id) {
        productCategoryRepository.deleteProductCategoryById(id);
        categoryPropertyRepository.deleteCategoryPropertyByCategoryId(id);
        categoryPropertyValueRepository.deleteCategoryPropertyValueByCategoryId(id);
        navigateCategoryRepository.deleteAllCategoryAssociation(id);
    }

    @Override
    @Transactional
    public void deleteAllProductCategory() {
        productCategoryRepository.deleteAllProductCategory();
    }

    @Override
    public List<ProductCategory> queryAllProductCategories() {
        return productCategoryRepository.queryAllProductCategories();
    }

    @Override
    public int queryProductCategoryByNameAndParentId(String name, int parentId) {
        return productCategoryRepository.queryProductCategoryByNameAndParentId(name, parentId);
    }
}
