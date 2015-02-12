package com.kariqu.productmanager.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.productcenter.domain.MealItem;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.domain.SkuStorage;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.MealSetService;
import com.kariqu.productcenter.service.ProductActivityService;
import com.kariqu.productmanager.helper.MealItemRecord;
import com.kariqu.productmanager.helper.StockPriceRecord;
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
import java.util.*;

/**
 * 库存和价格一起编辑的控制器
 * 有些商品的库存和价格是由类目属性决定的，有些则不是，SKU用来表示这些信息
 * User: Asion
 * Date: 11-9-19
 * Time: 上午10:57
 */
@Controller
public class SkuController extends SkuGridBase {

    private final Log logger = LogFactory.getLog(SkuController.class);

    @Autowired
    private ProductActivityService productActivityService;

    @Autowired
    private MealSetService mealSetService;


    /**
     * 修改SKU价格和库存
     *
     * @param skuStorage
     * @param price
     * @param response
     * @throws IOException
     */
    @Permission("修改了SKU信息")
    @RequestMapping(value = "/product/sku/update", method = RequestMethod.POST)
    public void updateSkuPrice(SkuStorage skuStorage, @RequestParam("price") String price, @RequestParam("marketPrice") String marketPrice, @RequestParam("barCode") String barCode, @RequestParam("skuCode") String skuCode, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {

            Money skuPrice = new Money(price);
            Money skuMarketPrice = new Money(marketPrice);

            if (skuPrice.getCent() <= 0 || skuMarketPrice.getCent() <= 0) {
                new JsonResult(false, "价格不合法").toJson(response);
                return;
            }

            if (skuPrice.getCent() > skuMarketPrice.getCent()) {
                new JsonResult(false, "易居尚价不能大于市场价").toJson(response);
                return;
            }

            if (skuStorage.getStockQuantity() < 0 || skuStorage.getTradeMaxNumber() < 0) {
                new JsonResult(false, "库存量不合法").toJson(response);
                return;
            }

            if (skuStorage.getStockQuantity() < skuStorage.getTradeMaxNumber()) {
                new JsonResult(false, "购买上限不能大于库存量").toJson(response);
                return;
            }

            StockKeepingUnit stockKeepingUnit = skuService.getStockKeepingUnit(skuStorage.getSkuId());

            if (productActivityService.checkProductIfJoinActivity(stockKeepingUnit.getProductId())) {
                new JsonResult(false, "此商品正在参加活动，不能修改").toJson(response);
                return;
            }

            skuService.editSkuPrice(skuStorage.getSkuId(), skuPrice.getCent(), skuMarketPrice.getCent());
            skuService.editSkuStorage(skuStorage);

            productService.notifyProductUpdate(stockKeepingUnit.getProductId());

            if (!StringUtils.equals(stockKeepingUnit.getBarCode(), barCode)) {
                if(skuService.hadSKUByBarCode(barCode)){
                    new JsonResult(false, "条形码已经存在").toJson(response);
                    return;
                }
            }

            if (!StringUtils.equals(stockKeepingUnit.getBarCode(), barCode) || !StringUtils.equals(stockKeepingUnit.getSkuCode(), skuCode)) {
                skuService.editSkuBarCodeAndSkuCode(skuStorage.getSkuId(), barCode, skuCode);
            }

        } catch (Exception e) {
            logger.error("商品管理的更新sku信息异常：" + e);
            new JsonResult(false, "更新sku信息出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }


    /**
     * 生成Ext前台编辑表格
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/product/sku/stock/price/list/{productId}")
    public void stockPriceEditor(@PathVariable("productId") int productId, HttpServletResponse response) throws IOException {
        List<StockKeepingUnit> stockKeepingUnits = skuService.querySKUByProductId(productId);
        List<StockPriceRecord> stockPriceRecordList = new ArrayList<StockPriceRecord>();
        for (StockKeepingUnit stockKeepingUnit : stockKeepingUnits) {
            StockPriceRecord stockPriceRecord = new StockPriceRecord();
            buildStockPriceRecord(stockKeepingUnit, stockPriceRecord);
            stockPriceRecordList.add(stockPriceRecord);
        }
        new JsonResult(true).addData("totalCount", stockPriceRecordList.size()).addData("result", stockPriceRecordList).toJson(response);
    }


    //有库存的sku列表
    @RequestMapping(value = "/product/skuWithStock/list/{productId}")
    public void fetchSkuWithStockList(@PathVariable("productId") int productId, HttpServletResponse response) throws IOException {
        try {
            List<StockKeepingUnit> stockKeepingUnits = skuService.queryProductSkuWithStock(productId);
            List<StockPriceRecord> stockPriceRecordList = new ArrayList<StockPriceRecord>();
            for (StockKeepingUnit stockKeepingUnit : stockKeepingUnits) {
                StockPriceRecord stockPriceRecord = new StockPriceRecord();
                buildStockPriceRecord(stockKeepingUnit, stockPriceRecord);
                stockPriceRecordList.add(stockPriceRecord);
            }
            new JsonResult(true).addData("totalCount", stockPriceRecordList.size()).addData("result", stockPriceRecordList).toJson(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("没有sku")) {
                logger.warn(e.getMessage());
                new JsonResult(false, "没有sku").toJson(response);
            } else {
                logger.error("取有库存的sku列表出错", e);
                new JsonResult(false, "程序出错").toJson(response);
            }
        }
    }

    @RequestMapping(value = "/product/mealSet/sku/list/{mealSetId}")
    public void stockPriceEditorForMealSet(@PathVariable("mealSetId") int mealSetId, HttpServletResponse response) throws IOException {
        Collection<MealItem> mealItems = mealSetService.queryMealItemListByMealId(mealSetId);
        List<StockKeepingUnit> stockKeepingUnitList = new ArrayList<StockKeepingUnit>(mealItems.size());

        Map<Long, MealItem> map = new HashMap<Long, MealItem>();

        for (MealItem mealItem : mealItems) {
            StockKeepingUnit sku = skuService.getStockKeepingUnit(mealItem.getSkuId());
            if (sku == null || sku.getSkuState() == StockKeepingUnit.SKUState.REMOVED) {
                logger.warn("显示套餐详情时, sku[" + mealItem.getSkuId() + "]不存在或已失效了, 在套餐[" + mealSetId + "]中忽略!");
                continue;
            }
            stockKeepingUnitList.add(skuService.getStockKeepingUnit(mealItem.getSkuId()));
            map.put(mealItem.getSkuId(), mealItem);
        }

        List<MealItemRecord> mealItemRecords = new ArrayList<MealItemRecord>();
        for (StockKeepingUnit stockKeepingUnit : stockKeepingUnitList) {
            MealItemRecord mealItemRecord = new MealItemRecord();
            buildStockPriceRecord(stockKeepingUnit, mealItemRecord);
            MealItem mealItem = map.get(stockKeepingUnit.getId());
            mealItemRecord.setId(mealItem.getId());
            mealItemRecord.setMealNumber(mealItem.getNumber());
            mealItemRecord.setMealPrice(Money.getMoneyString(mealItem.getSkuPrice()));
            mealItemRecords.add(mealItemRecord);
        }

        new JsonResult(true).addData("totalCount", mealItemRecords.size()).addData("result", mealItemRecords).toJson(response);
    }


    /**
     * 修改sku状态
     *
     * @param ids
     * @param skuState
     * @param response
     * @throws IOException
     */
    @Permission("修改了SKU状态")
    @RequestMapping(value = "/product/sku/updateState", method = RequestMethod.POST)
    public void updateSkuState(int[] ids, @RequestParam("skuState") StockKeepingUnit.SKUState skuState, HttpServletResponse response) throws IOException {
        try {
            skuService.updateSkuState(ids, skuState);
        } catch (Exception e) {
            logger.error("商品管理的更新sku状态异常：" + e);
            new JsonResult(false, e.getMessage()).toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }
}
