package com.kariqu.categorycenter.client.sync;

import com.kariqu.categorycenter.domain.model.*;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryPropertyValue;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;

import java.util.List;

/**
 * 同步结果对象
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-7-13 上午11:43
 */
public class SyncResult {

    private List<ProductCategory> addedProductCategory;

    private List<CategoryProperty> addedCategoryProperty;

    private List<CategoryPropertyValue> addedCategoryPropertyValue;

    private List<PropertyValueDetail> addedPropertyValueDetail;

    private List<NavigateCategory> addedNavigateCategory;

    private List<NavCategoryProperty> addedNavCategoryProperty;

    private List<NavCategoryPropertyValue> addedNavCategoryPropertyValue;

    private List<Property> addedProperty;

    private List<Value> addedValue;

    private List<ProductCategory> updatedProductCategory;

    private List<CategoryProperty> updatedCategoryProperty;

    private List<CategoryPropertyValue> updatedCategoryPropertyValue;

    private List<PropertyValueDetail> updatedPropertyValueDetail;

    private List<NavigateCategory> updatedNavigateCategory;

    private List<NavCategoryProperty> updatedNavCategoryProperty;

    private List<NavCategoryPropertyValue> updatedNavCategoryPropertyValue;

    private List<Property> updatedProperty;

    private List<Value> updatedValue;

    private List<ProductCategory> deletedProductCategory;

    private List<CategoryProperty> deletedCategoryProperty;

    private List<CategoryPropertyValue> deletedCategoryPropertyValue;

    private List<PropertyValueDetail> deletedPropertyValueDetail;

    private List<NavigateCategory> deletedNavigateCategory;

    private List<NavCategoryProperty> deletedNavCategoryProperty;

    private List<NavCategoryPropertyValue> deletedNavCategoryPropertyValue;

    private List<Property> deletedProperty;

    private List<Value> deletedValue;

    private boolean isSuccess = true;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<ProductCategory> getAddedProductCategory() {
        return addedProductCategory;
    }

    public void setAddedProductCategory(List<ProductCategory> addedProductCategory) {
        this.addedProductCategory = addedProductCategory;
    }

    public List<CategoryProperty> getAddedCategoryProperty() {
        return addedCategoryProperty;
    }

    public void setAddedCategoryProperty(List<CategoryProperty> addedCategoryProperty) {
        this.addedCategoryProperty = addedCategoryProperty;
    }

    public List<CategoryPropertyValue> getAddedCategoryPropertyValue() {
        return addedCategoryPropertyValue;
    }

    public void setAddedCategoryPropertyValue(List<CategoryPropertyValue> addedCategoryPropertyValue) {
        this.addedCategoryPropertyValue = addedCategoryPropertyValue;
    }

    public List<NavigateCategory> getAddedNavigateCategory() {
        return addedNavigateCategory;
    }

    public void setAddedNavigateCategory(List<NavigateCategory> addedNavigateCategory) {
        this.addedNavigateCategory = addedNavigateCategory;
    }

    public List<NavCategoryProperty> getAddedNavCategoryProperty() {
        return addedNavCategoryProperty;
    }

    public void setAddedNavCategoryProperty(List<NavCategoryProperty> addedNavCategoryProperty) {
        this.addedNavCategoryProperty = addedNavCategoryProperty;
    }

    public List<NavCategoryPropertyValue> getAddedNavCategoryPropertyValue() {
        return addedNavCategoryPropertyValue;
    }

    public void setAddedNavCategoryPropertyValue(List<NavCategoryPropertyValue> addedNavCategoryPropertyValue) {
        this.addedNavCategoryPropertyValue = addedNavCategoryPropertyValue;
    }

    public List<Property> getAddedProperty() {
        return addedProperty;
    }

    public void setAddedProperty(List<Property> addedProperty) {
        this.addedProperty = addedProperty;
    }

    public List<Value> getAddedValue() {
        return addedValue;
    }

    public void setAddedValue(List<Value> addedValue) {
        this.addedValue = addedValue;
    }

    public List<ProductCategory> getUpdatedProductCategory() {
        return updatedProductCategory;
    }

    public void setUpdatedProductCategory(List<ProductCategory> updatedProductCategory) {
        this.updatedProductCategory = updatedProductCategory;
    }

    public List<CategoryProperty> getUpdatedCategoryProperty() {
        return updatedCategoryProperty;
    }

    public void setUpdatedCategoryProperty(List<CategoryProperty> updatedCategoryProperty) {
        this.updatedCategoryProperty = updatedCategoryProperty;
    }

    public List<CategoryPropertyValue> getUpdatedCategoryPropertyValue() {
        return updatedCategoryPropertyValue;
    }

    public void setUpdatedCategoryPropertyValue(List<CategoryPropertyValue> updatedCategoryPropertyValue) {
        this.updatedCategoryPropertyValue = updatedCategoryPropertyValue;
    }

    public List<NavigateCategory> getUpdatedNavigateCategory() {
        return updatedNavigateCategory;
    }

    public void setUpdatedNavigateCategory(List<NavigateCategory> updatedNavigateCategory) {
        this.updatedNavigateCategory = updatedNavigateCategory;
    }

    public List<NavCategoryProperty> getUpdatedNavCategoryProperty() {
        return updatedNavCategoryProperty;
    }

    public void setUpdatedNavCategoryProperty(List<NavCategoryProperty> updatedNavCategoryProperty) {
        this.updatedNavCategoryProperty = updatedNavCategoryProperty;
    }

    public List<NavCategoryPropertyValue> getUpdatedNavCategoryPropertyValue() {
        return updatedNavCategoryPropertyValue;
    }

    public void setUpdatedNavCategoryPropertyValue(List<NavCategoryPropertyValue> updatedNavCategoryPropertyValue) {
        this.updatedNavCategoryPropertyValue = updatedNavCategoryPropertyValue;
    }

    public List<Property> getUpdatedProperty() {
        return updatedProperty;
    }

    public void setUpdatedProperty(List<Property> updatedProperty) {
        this.updatedProperty = updatedProperty;
    }

    public List<Value> getUpdatedValue() {
        return updatedValue;
    }

    public void setUpdatedValue(List<Value> updatedValue) {
        this.updatedValue = updatedValue;
    }

    public List<ProductCategory> getDeletedProductCategory() {
        return deletedProductCategory;
    }

    public void setDeletedProductCategory(List<ProductCategory> deletedProductCategory) {
        this.deletedProductCategory = deletedProductCategory;
    }

    public List<CategoryProperty> getDeletedCategoryProperty() {
        return deletedCategoryProperty;
    }

    public void setDeletedCategoryProperty(List<CategoryProperty> deletedCategoryProperty) {
        this.deletedCategoryProperty = deletedCategoryProperty;
    }

    public List<CategoryPropertyValue> getDeletedCategoryPropertyValue() {
        return deletedCategoryPropertyValue;
    }

    public void setDeletedCategoryPropertyValue(List<CategoryPropertyValue> deletedCategoryPropertyValue) {
        this.deletedCategoryPropertyValue = deletedCategoryPropertyValue;
    }

    public List<NavigateCategory> getDeletedNavigateCategory() {
        return deletedNavigateCategory;
    }

    public void setDeletedNavigateCategory(List<NavigateCategory> deletedNavigateCategory) {
        this.deletedNavigateCategory = deletedNavigateCategory;
    }

    public List<NavCategoryProperty> getDeletedNavCategoryProperty() {
        return deletedNavCategoryProperty;
    }

    public void setDeletedNavCategoryProperty(List<NavCategoryProperty> deletedNavCategoryProperty) {
        this.deletedNavCategoryProperty = deletedNavCategoryProperty;
    }

    public List<NavCategoryPropertyValue> getDeletedNavCategoryPropertyValue() {
        return deletedNavCategoryPropertyValue;
    }

    public void setDeletedNavCategoryPropertyValue(List<NavCategoryPropertyValue> deletedNavCategoryPropertyValue) {
        this.deletedNavCategoryPropertyValue = deletedNavCategoryPropertyValue;
    }

    public List<Property> getDeletedProperty() {
        return deletedProperty;
    }

    public void setDeletedProperty(List<Property> deletedProperty) {
        this.deletedProperty = deletedProperty;
    }

    public List<Value> getDeletedValue() {
        return deletedValue;
    }

    public void setDeletedValue(List<Value> deletedValue) {
        this.deletedValue = deletedValue;
    }


    public List<PropertyValueDetail> getAddedPropertyValueDetail() {
        return addedPropertyValueDetail;
    }

    public void setAddedPropertyValueDetail(List<PropertyValueDetail> addedPropertyValueDetail) {
        this.addedPropertyValueDetail = addedPropertyValueDetail;
    }

    public List<PropertyValueDetail> getUpdatedPropertyValueDetail() {
        return updatedPropertyValueDetail;
    }

    public void setUpdatedPropertyValueDetail(List<PropertyValueDetail> updatedPropertyValueDetail) {
        this.updatedPropertyValueDetail = updatedPropertyValueDetail;
    }

    public List<PropertyValueDetail> getDeletedPropertyValueDetail() {
        return deletedPropertyValueDetail;
    }

    public void setDeletedPropertyValueDetail(List<PropertyValueDetail> deletedPropertyValueDetail) {
        this.deletedPropertyValueDetail = deletedPropertyValueDetail;
    }
}
