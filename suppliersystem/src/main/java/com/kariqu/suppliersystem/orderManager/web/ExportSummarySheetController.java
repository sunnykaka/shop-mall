package com.kariqu.suppliersystem.orderManager.web;

import com.kariqu.categorycenter.domain.model.Property;
import com.kariqu.categorycenter.domain.model.Value;
import com.kariqu.categorycenter.domain.service.CategoryPropertyService;
import com.kariqu.common.DateUtils;
import com.kariqu.suppliersystem.orderManager.vo.ProductSku;
import com.kariqu.productcenter.domain.Product;
import com.kariqu.productcenter.domain.SkuProperty;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.suppliersystem.orderManager.vo.SummaryConfig;
import com.kariqu.tradecenter.domain.OrderItem;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 订单列表里面的导出汇总单
 *
 * @author:Wendy
 * @since:1.0.0 Date: 12-11-20
 * Time: 上午11:12
 */
@Controller
@RequestMapping("/orders/summary_report")
public class ExportSummarySheetController extends BaseController {

    private final Log logger = LogFactory.getLog(ExportSummarySheetController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryPropertyService categoryPropertyService;

    @Autowired
    private SkuService skuService;


    /**
     * 导出订单列表汇总单
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/excel")
    public void exportOrderExcel(long[] orderIds, Model model, HttpServletResponse response, HttpServletRequest request) throws IOException {
        WritableWorkbook writableWorkbook = null;
        try {
            response.setContentType("application/msexcel");
            /*使用jxl做表格*/
            String workSheetName = SummaryConfig.SUMMARY_FILE_NAME +  "-" + DateUtils.getCurrentDateStr(DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);
            workSheetName = new String(workSheetName.getBytes("UTF-8"), "ISO-8859-1");
            response.setHeader("Content-disposition", "attachment; filename=" + workSheetName + ".xls");
            writableWorkbook = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet sheet = writableWorkbook.createSheet(SummaryConfig.SUMMARY_FILE_NAME, 0);
            //设置每行能自动内容填充
            sheet.setRowView(0, 1000, false);
            //初始化多少列 从0开始
            sheet.mergeCells(0, 0, 3, 0);
            WritableFont wf_merge = new WritableFont(WritableFont.ARIAL, 24, WritableFont.BOLD, false, jxl.format.UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
            WritableCellFormat wff_merge = new WritableCellFormat(wf_merge);
            wff_merge.setVerticalAlignment(VerticalAlignment.CENTRE);
            wff_merge.setAlignment(Alignment.CENTRE);
            sheet.addCell(new Label(0, 0, "订单汇总单", wff_merge));
            WritableFont wfont = new WritableFont(WritableFont.ARIAL, 15,
                    WritableFont.BOLD, false,
                    jxl.format.UnderlineStyle.NO_UNDERLINE,
                    jxl.format.Colour.BLACK);

            WritableCellFormat titleFormat = new WritableCellFormat(wfont);
            titleFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
            titleFormat.setAlignment(Alignment.CENTRE);
            titleFormat.setWrap(true);

            for (int i = 0; i < SummaryConfig.S_TITLE.length; i++) {
                Label excelTitle = new Label(i, 1, SummaryConfig.S_TITLE[i], titleFormat);
                sheet.addCell(excelTitle);
            }
            //设置列的宽度
            sheet.setColumnView(0, 30);
            sheet.setColumnView(1, 50);
            sheet.setColumnView(2, 22);
            sheet.setColumnView(3, 13);

            WritableFont content = new WritableFont(WritableFont.ARIAL, 8, WritableFont.NO_BOLD, false);
            WritableCellFormat forContent = new WritableCellFormat(content);
            forContent.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            forContent.setVerticalAlignment(VerticalAlignment.CENTRE);
            forContent.setAlignment(Alignment.CENTRE);
            forContent.setWrap(true);
            List<ProductSku> skuInfoList = accessSkuInfo(orderIds);

            for (int i = 0; i < skuInfoList.size(); i++) {
                ProductSku productSku = skuInfoList.get(i);
                Label itemNo = new Label(0, i + 2, productSku.getItemNo(), forContent);
                Label productName = new Label(1, i + 2, productSku.getProductName(), forContent);
                Label productAtt = new Label(2, i + 2, productSku.getAttribute(), forContent);
                Label shipmentNum = new Label(3, i + 2, String.valueOf(productSku.getShipmentNum()), forContent);

                sheet.addCell(productName);
                sheet.addCell(itemNo);
                sheet.addCell(shipmentNum);
                sheet.addCell(productAtt);
            }

        } catch (Exception e) {
            logger.error("导出订单失败",e);
        } finally {
            writableWorkbook.write();
            try {
                writableWorkbook.close();
            } catch (WriteException e) {
                logger.error("导出订单失败" + e.getMessage());
            }
        }
    }


    /**
     * 获取订单里面的相同sku信息
     *
     * @param orderIds
     * @return
     */
    private List<ProductSku> accessSkuInfo(long[] orderIds) {
        List<ProductSku> productSkuList = new ArrayList<ProductSku>();
        for (long orderId : orderIds) {
            List<OrderItem> orderItems = tradeCenterSupplierClient.queryOrderItemWithoutBackingNumberByOrderId(orderId);
            for (OrderItem orderItem : orderItems) {
                StockKeepingUnit stockKeepingUnit = skuService.getStockKeepingUnit(orderItem.getSkuId());
                if (orderItem.getShipmentNum() > 0 && stockKeepingUnit != null) {
                    Product product = productService.getProductById(stockKeepingUnit.getProductId());

                    ProductSku productSku = new ProductSku();
                    productSku.setItemNo(stockKeepingUnit.getSkuCode());
                    String att = "";
                    for (SkuProperty skuProperty : stockKeepingUnit.getSkuProperties()) {
                        Property property = categoryPropertyService.getPropertyById(skuProperty.getPropertyId());
                        Value value = categoryPropertyService.getValueById(skuProperty.getValueId());
                        att += property.getName() + ":" + value.getValueName() + ";";
                    }
                    if (!att.equals("")) {
                        att = att.substring(0, att.length() - 1);
                    }
                    if (productSkuList.size() > 0) {
                        boolean has = false;
                        //判断sku是在集合中存在，存在改变购买数量，移除集合前面的那个值 重新add
                        for (int j = 0; j < productSkuList.size(); j++) {
                            if (productSkuList.get(j).getSkuId() == orderItem.getSkuId()) {
                                ProductSku newProductSku = new ProductSku();
                                has = true;
                                newProductSku.setSkuId(productSkuList.get(j).getSkuId());
                                newProductSku.setProductName(productSkuList.get(j).getProductName());
                                newProductSku.setShipmentNum(productSkuList.get(j).getShipmentNum() + orderItem.getShipmentNum());
                                newProductSku.setItemNo(productSkuList.get(j).getItemNo());
                                newProductSku.setAttribute(productSkuList.get(j).getAttribute());
                                productSkuList.remove(j);
                                productSkuList.add(newProductSku);
                            }
                        }
                        if (!has) {
                            productSku.setSkuId(stockKeepingUnit.getId());
                            productSku.setProductName(product.getName());
                            productSku.setShipmentNum(orderItem.getShipmentNum());
                            productSku.setItemNo(product.getProductCode());
                            productSku.setAttribute(att);
                            productSkuList.add(productSku);
                        }
                    } else {
                        productSku.setSkuId(stockKeepingUnit.getId());
                        productSku.setProductName(product.getName());
                        productSku.setShipmentNum(orderItem.getShipmentNum());
                        productSku.setItemNo(product.getProductCode());
                        productSku.setAttribute(att);
                        productSkuList.add(productSku);
                    }
                }

            }
        }
        return productSkuList;
    }

}
