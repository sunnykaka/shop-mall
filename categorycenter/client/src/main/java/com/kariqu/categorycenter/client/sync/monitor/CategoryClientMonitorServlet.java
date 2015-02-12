package com.kariqu.categorycenter.client.sync.monitor;

import com.kariqu.categorycenter.client.container.CategoryContainer;
import com.kariqu.categorycenter.client.sync.SyncMediator;
import com.kariqu.categorycenter.client.sync.SyncRequest;
import com.kariqu.categorycenter.client.sync.SyncResult;
import com.kariqu.categorycenter.client.sync.SyncType;
import com.kariqu.categorycenter.domain.model.*;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.service.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * 对类目数据进行同步的Servlet
 * 客户端调用类目数据是通过Category-Client jar包，这个jar维护了一个内存数据库，定期从
 * 类目服务器上拉数据
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-7-13 下午4:58
 */
public class CategoryClientMonitorServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String syncType = req.getParameter("type");
        final SyncMediator syncMediator = CategoryContainer.getSyncMediator();
        if (SyncType.all.equals(syncType)) {
            SyncRequest syncRequest = new SyncRequest(SyncType.all);
            final SyncResult syncResult = syncMediator.sync(syncRequest);
            if (syncResult.isSuccess()) {
                printSyncResult(resp);
            }
            return;
        }

        if (SyncType.inc.equals(syncType)) {
            SyncRequest syncRequest = new SyncRequest(SyncType.inc);
            final SyncResult syncResult = syncMediator.sync(syncRequest);
            if (syncResult.isSuccess()) {
                printIncSyncResult(syncResult, resp);
                printSyncResult(resp);
            }
        }
    }

    private void printIncSyncResult(SyncResult syncResult, HttpServletResponse resp) throws IOException {
        ProductCategoryService productCategoryServiceClient = CategoryContainer.getProductCategoryService();
        CategoryPropertyService categoryPropertyServiceClient = CategoryContainer.getCategoryPropertyService();
        NavigateCategoryService navigateCategoryServiceClient = CategoryContainer.getNavigateCategoryService();

        PrintWriter out = resp.getWriter();
        resp.setContentType("text/html");

        StringBuilder stringBuilder = new StringBuilder("<b>新增的后台类目</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>ID</td><td>类目名称</td><td>类目描述</td></tr>");
        for (ProductCategory productCategory : syncResult.getAddedProductCategory()) {
            stringBuilder.append("<tr><td>").append(productCategory.getId()).append("</td><td>").append(productCategory.getName())
                    .append("</td><td>").append(productCategory.getDescription()).append("</td><tr>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>更新的后台类目</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>ID</td><td>类目名称</td><td>类目描述</td></tr>");
        for (ProductCategory productCategory : syncResult.getUpdatedProductCategory()) {
            stringBuilder.append("<tr><td>").append(productCategory.getId()).append("</td><td>").append(productCategory.getName())
                    .append("</td><td>").append(productCategory.getDescription()).append("</td><tr>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>删除的后台类目</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>ID</td><td>类目名称</td><td>类目描述</td></tr>");
        for (ProductCategory productCategory : syncResult.getDeletedProductCategory()) {
            stringBuilder.append("<tr><td>").append(productCategory.getId()).append("</td><td>")
                    .append(productCategory.getName()).append("</td><td>").append(productCategory.getDescription()).append("</td><tr>");
        }
        stringBuilder.append("</table>");


        stringBuilder.append("<b>新增的前台类目</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>ID</td><td>类目名</td><td>类目描述</td></tr>");
        for (NavigateCategory navCategory : syncResult.getAddedNavigateCategory()) {
            stringBuilder.append("<tr><td>").append(navCategory.getId()).append("</td><td>").append(navCategory.getName())
                    .append("</td><td>").append(navCategory.getDescription()).append("</td></tr>");
        }
        stringBuilder.append("</table>");
        stringBuilder.append("<b>更新的前台类目</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>ID</td><td>类目名</td><td>类目描述</td></tr>");
        for (NavigateCategory navCategory : syncResult.getUpdatedNavigateCategory()) {
            stringBuilder.append("<tr><td>").append(navCategory.getId()).append("</td><td>")
                    .append(navCategory.getName()).append("</td><td>").append(navCategory.getDescription()).append("</td></tr>");
        }
        stringBuilder.append("</table>");


        stringBuilder.append("<b>删除的前台类目</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>ID</td><td>类目名</td><td>类目描述</td></tr>");
        for (NavigateCategory navCategory : syncResult.getDeletedNavigateCategory()) {
            stringBuilder.append("<tr><td>").append(navCategory.getId()).append("</td><td>")
                    .append(navCategory.getName()).append("</td><td>").append(navCategory.getDescription()).append("</td></tr>");
        }
        stringBuilder.append("</table>");


        stringBuilder.append("<b>新增的属性</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>ID</td><td>name</td></tr>");
        for (Property property : syncResult.getAddedProperty()) {
            stringBuilder.append("<tr><td>").append(property.getId()).append("</td><td>").append(property.getName()).append("</td></tr>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>更新的属性</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>ID</td><td>name</td></tr>");
        for (Property property : syncResult.getUpdatedProperty()) {
            stringBuilder.append("<tr><td>").append(property.getId()).append("</td><td>").append(property.getName()).append("</td></tr>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>删除的属性</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>ID</td><td>name</td></tr>");
        for (Property property : syncResult.getDeletedProperty()) {
            stringBuilder.append("<tr><td>").append(property.getId()).append("</td><td>").append(property.getName()).append("</td></tr>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>新增的属性值</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>ID</td><td>name</td></tr>");
        for (Value value : syncResult.getAddedValue()) {
            stringBuilder.append("<tr><td>").append(value.getId()).append("</td><td>").append(value.getValueName()).append("</td></tr>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>更新的属性值</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>ID</td><td>name</td></tr>");
        for (Value value : syncResult.getUpdatedValue()) {
            stringBuilder.append("<tr><td>").append(value.getId()).append("</td><td>").append(value.getValueName()).append("</td></tr>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>删除的属性值</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>ID</td><td>name</td></tr>");
        for (Value value : syncResult.getDeletedValue()) {
            stringBuilder.append("<tr><td>").append(value.getId()).append("</td><td>").append(value.getValueName()).append("</td></tr>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>新增的属性值细节</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>属性名</td><td>属性值</td><td>图片地址</td></tr>");
        for (PropertyValueDetail detail : syncResult.getAddedPropertyValueDetail()) {
            stringBuilder.append("<tr>");
            Property property = categoryPropertyServiceClient.getPropertyById(detail.getPropertyId());
            if (property != null)
                stringBuilder.append("<tr><td>").append(property.getName()).append( "</td>");

            Value value = categoryPropertyServiceClient.getValueById(detail.getValueId());
            if (value != null)
                stringBuilder.append("<td>").append(value.getValueName()).append("</td>").append("<td>").append(detail.getPictureUrl()).append("</td>");
            stringBuilder.append("</tr>");
        }
        stringBuilder.append("</table>");


        stringBuilder.append("<b>更新的属性值细节</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>属性名</td><td>属性值</td><td>图片地址</td></tr>");
        for (PropertyValueDetail detail : syncResult.getUpdatedPropertyValueDetail()) {
            stringBuilder.append("<tr><td>").append(categoryPropertyServiceClient.getPropertyById(detail.getPropertyId()).getName()).append( "</td><td>")
                    .append(categoryPropertyServiceClient.getValueById(detail.getValueId()).getValueName()).append("</td><td>").append(detail.getPictureUrl()).append("</td></tr>");
        }
        stringBuilder.append("</table>");


        stringBuilder.append("<b>删除的属性值细节</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>属性名</td><td>属性值</td><td>图片地址</td></tr>");
        for (PropertyValueDetail detail : syncResult.getDeletedPropertyValueDetail()) {
            stringBuilder.append("<tr><td>").append(categoryPropertyServiceClient.getPropertyById(detail.getPropertyId()).getName()).append("</td><td>")
                    .append(categoryPropertyServiceClient.getValueById(detail.getValueId()).getValueName()).append("</td><td>").append(detail.getPictureUrl()).append("</td></tr>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>新增的类目属性</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>类目</td><td>属性名</td><td>属性类型</td><td>是否多值</td></tr>");
        for (CategoryProperty categoryProperty : syncResult.getAddedCategoryProperty()) {
            stringBuilder.append("<tr><td>").append(productCategoryServiceClient.getProductCategoryById(categoryProperty.getCategoryId()).getName()).append("</td>");
            stringBuilder.append("<td>").append(categoryPropertyServiceClient.getPropertyById(categoryProperty.getPropertyId()).getName()).append("</td>");
            stringBuilder.append("<td>").append(categoryProperty.getPropertyType()).append("</td>");
            stringBuilder.append("<td>").append(categoryProperty.isMultiValue()).append("</td></tr>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>更新的类目属性</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>类目</td><td>属性名</td><td>属性类型</td><td>是否多值</td></tr>");
        for (CategoryProperty categoryProperty : syncResult.getUpdatedCategoryProperty()) {
            stringBuilder.append("<tr><td>").append(productCategoryServiceClient.getProductCategoryById(categoryProperty.getCategoryId()).getName()).append("</td>");
            stringBuilder.append("<td>").append(categoryPropertyServiceClient.getPropertyById(categoryProperty.getPropertyId()).getName()).append("</td>");
            stringBuilder.append("<td>").append(categoryProperty.getPropertyType()).append("</td>");
            stringBuilder.append("<td>").append(categoryProperty.isMultiValue()).append("</td></tr>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>删除的类目属性</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>类目</td><td>属性名</td><td>属性类型</td><td>是否多值</td></tr>");
        for (CategoryProperty categoryProperty : syncResult.getDeletedCategoryProperty()) {
            stringBuilder.append("<tr><td>").append(productCategoryServiceClient.getProductCategoryById(categoryProperty.getCategoryId()).getName()).append("</td>");
            stringBuilder.append("<td>").append(categoryPropertyServiceClient.getPropertyById(categoryProperty.getPropertyId()).getName()).append("</td>");
            stringBuilder.append("<td>").append(categoryProperty.getPropertyType()).append("</td>");
            stringBuilder.append("<td>").append(categoryProperty.isMultiValue()).append("</td></tr>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>新增的类目属性值</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>类目</td><td>属性名</td><td>属性值</td></tr>");
        for (CategoryPropertyValue categoryPropertyValue : syncResult.getAddedCategoryPropertyValue()) {
            stringBuilder.append("<tr><td>").append(productCategoryServiceClient.getProductCategoryById(categoryPropertyValue.getCategoryId()).getName()).append("</td>");
            stringBuilder.append("<td>").append(categoryPropertyServiceClient.getPropertyById(categoryPropertyValue.getPropertyId()).getName()).append("</td>");
            stringBuilder.append("<td>").append(categoryPropertyServiceClient.getValueById(categoryPropertyValue.getValueId()).getValueName()).append("</td>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>更新的类目属性值</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>类目</td><td>属性名</td><td>属性值</td></tr>");
        for (CategoryPropertyValue categoryPropertyValue : syncResult.getUpdatedCategoryPropertyValue()) {
            stringBuilder.append("<tr><td>").append(productCategoryServiceClient.getProductCategoryById(categoryPropertyValue.getCategoryId()).getName()).append("</td>");
            stringBuilder.append("<td>").append(categoryPropertyServiceClient.getPropertyById(categoryPropertyValue.getPropertyId()).getName()).append("</td>");
            stringBuilder.append("<td>").append(categoryPropertyServiceClient.getValueById(categoryPropertyValue.getValueId()).getValueName()).append("</td>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>删除的类目属性值</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>类目</td><td>属性名</td><td>属性值</td></tr>");
        for (CategoryPropertyValue categoryPropertyValue : syncResult.getDeletedCategoryPropertyValue()) {
            stringBuilder.append("<tr><td>").append(productCategoryServiceClient.getProductCategoryById(categoryPropertyValue.getCategoryId()).getName()).append("</td>");
            stringBuilder.append("<td>").append(categoryPropertyServiceClient.getPropertyById(categoryPropertyValue.getPropertyId()).getName()).append("</td>");
            stringBuilder.append("<td>").append(categoryPropertyServiceClient.getValueById(categoryPropertyValue.getValueId()).getValueName()).append("</td>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>新增的前台类目属性</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>类目</td><td>属性</td></tr>");
        for (NavCategoryProperty navCategoryProperty : syncResult.getAddedNavCategoryProperty()) {
            stringBuilder.append("<tr><td>").append(navigateCategoryServiceClient.getNavigateCategory(navCategoryProperty.getNavCategoryId()).getName()).append("</td><td>").append(categoryPropertyServiceClient.getPropertyById(navCategoryProperty.getPropertyId()).getName()).append("</td></tr>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>更新的前台类目属性</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>类目</td><td>属性</td></tr>");
        for (NavCategoryProperty navCategoryProperty : syncResult.getUpdatedNavCategoryProperty()) {
            stringBuilder.append("<tr><td>").append(navigateCategoryServiceClient.getNavigateCategory(navCategoryProperty.getNavCategoryId()).getName()).append("</td><td>").append(categoryPropertyServiceClient.getPropertyById(navCategoryProperty.getPropertyId()).getName()).append("</td></tr>");
        }
        stringBuilder.append("</table>");

        stringBuilder.append("<b>删除的前台类目属性</b><br/>");
        stringBuilder.append("<table border=\"1\"><tr><td>类目</td><td>属性</td></tr>");
        for (NavCategoryProperty navCategoryProperty : syncResult.getDeletedNavCategoryProperty()) {
            stringBuilder.append("<tr><td>").append(navigateCategoryServiceClient.getNavigateCategory(navCategoryProperty.getNavCategoryId()).getName()).append("</td><td>").append(categoryPropertyServiceClient.getPropertyById(navCategoryProperty.getPropertyId()).getName()).append("</td></tr>");
        }
        stringBuilder.append("</table>");


        out.print(stringBuilder.toString());

    }

    private void printSyncResult(HttpServletResponse resp) throws IOException {
        ProductCategoryService productCategoryServiceClient = CategoryContainer.getProductCategoryService();
        CategoryPropertyService categoryPropertyServiceClient = CategoryContainer.getCategoryPropertyService();
        NavigateCategoryService navigateCategoryServiceClient = CategoryContainer.getNavigateCategoryService();
        CategoryAssociationService categoryAssociationService = CategoryContainer.getCategoryAssociationService();
        NavigateCategoryPropertyService navigateCategoryPropertyServiceClient = CategoryContainer.getNavigateCategoryPropertyService();

        List<ProductCategory> productCategories = productCategoryServiceClient.queryAllProductCategories();
        List<CategoryProperty> categoryProperties = categoryPropertyServiceClient.queryAllCategoryProperties();
        List<CategoryPropertyValue> categoryPropertyValues = categoryPropertyServiceClient.queryAllCategoryPropertyValues();

        PrintWriter out = resp.getWriter();

        StringBuilder categoryTable = new StringBuilder("<h2>后台类目</h2><hr>");
        categoryTable.append("<b>类目</b><br/>");
        categoryTable.append("<table border=\"1\"><tr><td>ID</td><td>类目名称</td><td>类目描述</td></tr>");
        for (ProductCategory productCategory : productCategories) {
            categoryTable.append("<tr><td>").append(productCategory.getId()).append("</td><td>").append(productCategory.getName()).append("</td><td>").append(productCategory.getDescription()).append("</td><tr>");
        }
        categoryTable.append("</table>");

        categoryTable.append("<b>类目属性</b><br/>");
        categoryTable.append("<table border=\"1\"><tr><td>类目</td><td>属性名</td><td>属性类型</td><td>是否多值</td></tr>");
        for (CategoryProperty categoryProperty : categoryProperties) {
            categoryTable.append("<tr><td>").append(productCategoryServiceClient.getProductCategoryById(categoryProperty.getCategoryId()).getName()).append("</td>");
            categoryTable.append("<td>").append(categoryPropertyServiceClient.getPropertyById(categoryProperty.getPropertyId()).getName()).append("</td>");
            categoryTable.append("<td>").append(categoryProperty.getPropertyType()).append("</td>");
            categoryTable.append("<td>").append(categoryProperty.isMultiValue()).append("</td></tr>");
        }
        categoryTable.append("</table>");

        categoryTable.append("<b>类目属性值</b><br/>");
        categoryTable.append("<table border=\"1\"><tr><td>类目</td><td>属性名</td><td>属性值</td></tr>");
        for (CategoryPropertyValue categoryPropertyValue : categoryPropertyValues) {
            ProductCategory productCategory = productCategoryServiceClient.getProductCategoryById(categoryPropertyValue.getCategoryId());
            if (productCategory != null)
                categoryTable.append("<tr><td>").append(productCategory.getName()).append("</td>");

            Property property = categoryPropertyServiceClient.getPropertyById(categoryPropertyValue.getPropertyId());
            if (property != null)
                categoryTable.append("<td>").append(property.getName()).append("</td>");

            Value value = categoryPropertyServiceClient.getValueById(categoryPropertyValue.getValueId());
            if (value != null)
                categoryTable.append("<td>").append(value.getValueName()).append("</td>");
        }
        categoryTable.append("</table>");


        StringBuilder navCategoryTable = new StringBuilder("<h2>前台类目</h2><hr>");
        navCategoryTable.append("<b>类目</b><br/>");
        navCategoryTable.append("<table border=\"1\"><tr><td>ID</td><td>类目名</td><td>类目描述</td></tr>");
        List<NavigateCategory> navigateCategories = navigateCategoryServiceClient.queryAllNavCategories();
        for (NavigateCategory navCategory : navigateCategories) {
            navCategoryTable.append("<tr><td>").append(navCategory.getId()).append("</td><td>").append(navCategory.getName()).append("</td><td>").append(navCategory.getDescription()).append("</td></tr>");
        }
        navCategoryTable.append("</table>");

        navCategoryTable.append("<b>类目属性</b><br/>");
        navCategoryTable.append("<table border=\"1\"><tr><td>类目</td><td>属性</td></tr>");
        List<NavCategoryProperty> navCategoryProperties = navigateCategoryPropertyServiceClient.queryAllNavCategoryProperties();
        for (NavCategoryProperty navCategoryProperty : navCategoryProperties) {
            navCategoryTable.append("<tr><td>").append(navigateCategoryServiceClient.getNavigateCategory(navCategoryProperty.getNavCategoryId()).getName()).append("</td><td>").append(categoryPropertyServiceClient.getPropertyById(navCategoryProperty.getPropertyId()).getName()).append("</td></tr>");
        }
        navCategoryTable.append("</table>");

        StringBuilder propertyTable = new StringBuilder("<h2>属性库</h2><hr><table border=\"1\"><tr><td>ID</td><td>name</td></tr>");
        List<Property> properties = categoryPropertyServiceClient.queryAllProperties();
        for (Property property : properties) {
            propertyTable.append("<tr><td>").append(property.getId()).append("</td><td>").append(property.getName()).append("</td></tr>");
        }
        propertyTable.append("</table>");

        StringBuilder valueTable = new StringBuilder("<h2>属性值库</h2><hr><table border=\"1\"><tr><td>ID</td><td>name</td></tr>");
        List<Value> values = categoryPropertyServiceClient.queryAllValues();
        for (Value value : values) {
            valueTable.append("<tr><td>").append(value.getId()).append("</td><td>").append(value.getValueName()).append("</td></tr>");
        }
        valueTable.append("</table>");

        StringBuilder valueDetailTable = new StringBuilder("<h2>属性值细节</h2><hr><table border=\"1\"><tr><td>属性名</td><td>属性值</td><td>图片地址</td></tr>");
        List<PropertyValueDetail> propertyValueDetails = categoryPropertyServiceClient.queryAllCategoryPropertyValueDetails();
        for (PropertyValueDetail propertyValueDetail : propertyValueDetails) {
            valueDetailTable.append("<tr><td>").append(categoryPropertyServiceClient.getPropertyById(propertyValueDetail.getPropertyId()).getName()).append("</td>").append("<td>").append(categoryPropertyServiceClient.getValueById(propertyValueDetail.getValueId()).getValueName()).append("</td>").append("<td>").append(propertyValueDetail.getPictureUrl()).append("</td></tr>");
        }
        valueDetailTable.append("</table>");


        StringBuilder associationTable = new StringBuilder("<h2>前后关联</h2><hr><table border=\"1\"><tr><td>前台</td><td>后台</td></tr>");
        Map<Integer, List<Integer>> association = categoryAssociationService.queryAllAssociation();
        for (Integer navId : association.keySet()) {
            NavigateCategory navigateCategory = navigateCategoryServiceClient.getNavigateCategory(navId);
            List<Integer> cids = association.get(navId);
            for (Integer cid : cids) {
                ProductCategory productCategory = productCategoryServiceClient.getProductCategoryById(cid);
                associationTable.append("<tr><td>").append(navigateCategory.getName()).append("</td><td>").append(productCategory.getName()).append("</td></tr>");
            }
        }
        associationTable.append("</table>");

        resp.setContentType("text/html");
        out.print(categoryTable.toString());
        out.print(navCategoryTable.toString());
        out.print(propertyTable.toString());
        out.print(valueTable.toString());
        out.print(valueDetailTable.toString());
        out.print(associationTable.toString());
    }
}
