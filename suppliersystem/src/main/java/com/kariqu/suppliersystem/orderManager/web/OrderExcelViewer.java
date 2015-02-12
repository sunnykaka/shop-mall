package com.kariqu.suppliersystem.orderManager.web;

import com.kariqu.common.DateUtils;
import com.kariqu.suppliersystem.orderManager.vo.PlatformOrder;
import com.kariqu.suppliersystem.orderManager.vo.PlatformOrderItem;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * User: amos.zhou
 * Date: 13-11-15
 * Time: 下午2:38
 */

public class OrderExcelViewer extends AbstractExcelView{
    @Override
    protected void buildExcelDocument(Map<String, Object> stringObjectMap, HSSFWorkbook hssfWorkbook, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        List<PlatformOrderItem> platformOrderItems = (List<PlatformOrderItem>)stringObjectMap.get("platformOrderItems");
        Map<Long, PlatformOrder> platformOrderMap = (Map<Long, PlatformOrder>)stringObjectMap.get("platformOrderMap");

        HSSFSheet sheet = hssfWorkbook.createSheet("订单汇总");
        sheet.setDefaultColumnWidth(30);
        CellStyle style = hssfWorkbook.createCellStyle();
        Font font = hssfWorkbook.createFont();
        font.setFontName("Arial");
        style.setFillForegroundColor(HSSFColor.BLUE.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.WHITE.index);
        style.setFont(font);

        // create header row
        HSSFRow header = sheet.createRow(0);

        header.createCell(0).setCellValue("订单编号");
        header.getCell(0).setCellStyle(style);

//        header.createCell(1).setCellValue("是否开发票");
//        header.getCell(1).setCellStyle(style);


        header.createCell(1).setCellValue("产品名称");
        header.getCell(1).setCellStyle(style);

        header.createCell(2).setCellValue("产品编号");
        header.getCell(2).setCellStyle(style);

        header.createCell(3).setCellValue("条形码");
        header.getCell(3).setCellStyle(style);

        header.createCell(4).setCellValue("产品价格");
        header.getCell(4).setCellStyle(style);

        header.createCell(5).setCellValue("sku价格");
        header.getCell(5).setCellStyle(style);

        header.createCell(6).setCellValue("购买数量");
        header.getCell(6).setCellStyle(style);

        header.createCell(7).setCellValue("发货数量");
        header.getCell(7).setCellStyle(style);


        header.createCell(8).setCellValue("物流编号");
        header.getCell(8).setCellStyle(style);

        header.createCell(9).setCellValue("收货人");
        header.getCell(9).setCellStyle(style);

        header.createCell(10).setCellValue("买家姓名");
        header.getCell(10).setCellStyle(style);

        header.createCell(11).setCellValue("订单状态");
        header.getCell(11).setCellStyle(style);

        header.createCell(12).setCellValue("配送方式");
        header.getCell(12).setCellStyle(style);

        header.createCell(13).setCellValue("仓库");
        header.getCell(13).setCellStyle(style);

        header.createCell(14).setCellValue("下单时间");
        header.getCell(14).setCellStyle(style);

        header.createCell(15).setCellValue("支付时间");
        header.getCell(15).setCellStyle(style);

        header.createCell(16).setCellValue("发货时间");
        header.getCell(16).setCellStyle(style);

        header.createCell(17).setCellValue("买家留言");
        header.getCell(17).setCellStyle(style);

        header.createCell(18).setCellValue("卖家留言");
        header.getCell(18).setCellStyle(style);

        int rowCount = 1;

        for (PlatformOrderItem orderItem : platformOrderItems) {
            HSSFRow aRow = sheet.createRow(rowCount++);
            PlatformOrder order = platformOrderMap.get(orderItem.getOrderId());
            aRow.createCell(0).setCellValue(order.getOrderNo());
//            aRow.createCell(1).setCellValue(order.isInvoice()? "是":"否");

            aRow.createCell(1).setCellValue(orderItem.getProductName());
            aRow.createCell(2).setCellValue(orderItem.getItemNo());
            aRow.createCell(3).setCellValue(orderItem.getBarCode());
            aRow.createCell(4).setCellValue(orderItem.getUnitPrice());
            aRow.createCell(5).setCellValue(orderItem.getSkuPrice());
            aRow.createCell(6).setCellValue(orderItem.getNumber());
            aRow.createCell(7).setCellValue(orderItem.getShipmentNum());

            aRow.createCell(8).setCellValue(order.getWaybillNumber());
            aRow.createCell(9).setCellValue(order.getName());
            aRow.createCell(10).setCellValue(order.getUserName());
            aRow.createCell(11).setCellValue(order.getOrderState());
            aRow.createCell(12).setCellValue(order.getDeliveryType());
            aRow.createCell(13).setCellValue(order.getStorageName());
            aRow.createCell(14).setCellValue(order.getCreateTime());
            aRow.createCell(15).setCellValue(order.getPayTime());
            aRow.createCell(16).setCellValue(order.getSendTime());
            aRow.createCell(17).setCellValue(order.getUserRemark());
            aRow.createCell(18).setCellValue(order.getCustomerServiceRemark());
        }

//        String fileName = "订单汇总-" + DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DateFormatType.SIMPLE_DATE_FORMAT_STR);
//        httpServletResponse.setHeader("Content-disposition", "attachment;filename=" + fileName);
    }
}
