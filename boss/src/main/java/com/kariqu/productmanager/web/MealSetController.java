package com.kariqu.productmanager.web;

import com.kariqu.common.JsonResult;
import com.kariqu.productcenter.domain.MealItem;
import com.kariqu.productcenter.domain.MealSet;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.MealSetService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.productcenter.service.SkuStorageService;
import com.kariqu.suppliercenter.domain.ProductStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * User: Asion
 * Date: 13-5-4
 * Time: 下午1:38
 */
@Controller
public class MealSetController {


    @Autowired
    private MealSetService mealSetService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private SkuStorageService skuStorageService;


    @RequestMapping(value = "/product/mealSet/list")
    public void mealSetList(HttpServletResponse response) throws IOException {
        List<MealSet> mealSets = mealSetService.queryAllMealSet();
        new JsonResult(true).addData("totalCount", mealSets.size()).addData("result", mealSets).toJson(response);
    }

    @RequestMapping(value = "/product/skuList/{productId}")
    public void skuList(HttpServletResponse response, @PathVariable int productId) throws IOException {
        List<StockKeepingUnit> stockKeepingUnits = skuService.querySKUByProductId(productId);
        List<Long> list = new ArrayList<Long>(stockKeepingUnits.size());
        for (StockKeepingUnit stockKeepingUnit : stockKeepingUnits) {
            list.add(stockKeepingUnit.getId());
        }
        new JsonResult(true).addData("skuList", list).toJson(response);
    }

    @RequestMapping(value = "/product/mealSet/add", method = RequestMethod.POST)
    public void addMealSet(MealSet mealSet, HttpServletResponse response) throws IOException {
        mealSetService.addMealSet(mealSet);
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/product/mealSet/update", method = RequestMethod.POST)
    public void updateMealSet(MealSet mealSet, HttpServletResponse response) throws IOException {
        MealSet mealSetVo = mealSetService.getMealSetById(mealSet.getId());
        mealSetVo.setName(mealSet.getName());
        mealSetVo.setRecommendReason(mealSet.getRecommendReason());
        mealSetService.updateMealSet(mealSetVo);
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/product/mealSet/assign/sku", method = RequestMethod.POST)
    public void addMealSet(long skuId, int mealId, int number, String skuPrice, HttpServletResponse response) throws IOException {

        MealItem dbMealItem = mealSetService.queryMealItemBySkuIdAndMealId(skuId, mealId);
        if (dbMealItem != null) {
            new JsonResult(false, "已经加入过了").toJson(response);
            return;
        }

        ProductStorage concretionStorage = skuStorageService.getConcretionStorage(skuId);


        StockKeepingUnit stockKeepingUnit = skuService.getStockKeepingUnitWithStock(skuId, concretionStorage.getId());


        if (stockKeepingUnit.getSkuState() == StockKeepingUnit.SKUState.REMOVED) {
            new JsonResult(false, "此sku无效").toJson(response);
            return;
        }

        if (number > stockKeepingUnit.getStockQuantity()) {
            new JsonResult(false, "数量大于库存").toJson(response);
            return;
        }

        long mealPrice = Money.YuanToCent(skuPrice);

        if (mealPrice > stockKeepingUnit.getPrice()) {
            new JsonResult(false, "套餐价格不能高于原价").toJson(response);
            return;
        }

        Collection<MealItem> mealItems = mealSetService.queryMealItemListByMealId(mealId);
        if (mealItems.size() > 0) {
            Iterator<MealItem> iterator = mealItems.iterator();
            MealItem next = iterator.next();

            //已加入套餐商品所在的仓库
            int dbStoreId = skuStorageService.getConcretionStorage(next.getSkuId()).getId();

            if (skuStorageService.getConcretionStorage(skuId).getId() != dbStoreId) {
                new JsonResult(false, "套餐商品只能在一个仓库中选择").toJson(response);
                return;
            }

        }

        MealItem mealItem = new MealItem();
        mealItem.setMealSetId(mealId);
        mealItem.setSkuId(skuId);
        mealItem.setNumber(number);
        mealItem.setSkuPrice(mealPrice);
        mealItem.setProductId(stockKeepingUnit.getProductId());
        mealSetService.addMealItem(mealItem);
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/product/mealSet/delete", method = RequestMethod.POST)
    public void deleteMealSet(int[] ids, HttpServletResponse response) throws IOException {
        for (int id : ids) {
            mealSetService.deleteMealSet(id);
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/product/mealItem/delete", method = RequestMethod.POST)
    public void deleteMealItem(int[] ids, HttpServletResponse response) throws IOException {
        for (int id : ids) {
            mealSetService.deleteMealItemById(id);
        }
        new JsonResult(true).toJson(response);
    }

}
