package com.kariqu.tradesystem.helper;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExportTradeExcel {

    // (2 << 15) - 1
    //private static final int EXCEL_TOTAL = 65535;

    /**
     * 需要保证 标题列 与 属性列 相对应, 否则会出现数据不对称
     *
     * @param dataMap       需要显示的数据集合
     * @param headerList    标题列(名字, 性格)
     * @param fieldNameList 属性列, 与标题列对应(name, sex)
     */
    public static void exportExcel(Map<String, List> dataMap, List<String[]> headerList, List<String[]> fieldNameList, OutputStream out) {
        if (dataMap == null)
            dataMap = new LinkedHashMap<String, List>();

        // 声明一个工作薄(Office 2003)
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 头样式
        HSSFCellStyle headStyle = createHeadStyle(workbook);
        // 内容样式
        HSSFCellStyle contentStyle = createContentStyle(workbook);

        try {
            // sheet
            HSSFSheet sheet;
            // 行
            HSSFRow row;
            // 列
            HSSFCell cell;
            // 行索引,     列索引
            int rowIndex, cellIndex;
            // sheet 数据
            List sheetList;

            int countSheet = 0;
            for (Map.Entry<String, List> entry : dataMap.entrySet()) {
                sheet = workbook.createSheet(entry.getKey());

                rowIndex = 0;
                cellIndex = 0;
                row = sheet.createRow(rowIndex);
                for (String header : headerList.get(countSheet)) {
                    cell = row.createCell(cellIndex);
                    cell.setCellStyle(headStyle);
                    cell.setCellValue(new HSSFRichTextString(header));

                    cellIndex++;
                }
                // 冻结标题栏
                sheet.createFreezePane(0, 1, 0, 1);

                // 当前 sheet 的数据
                sheetList = entry.getValue();
                // 遍历集合数据，产生数据行
                for (Object obj : sheetList) {
                    rowIndex++;
                    // 行数据
                    row = sheet.createRow(rowIndex);

                    if (obj == null) continue;

                    cellIndex = 0;
                    int countFirst = NumberUtils.toInt(getValue("countFirst", obj));
                    int countSecond = NumberUtils.toInt(getValue("countSecond", obj));
                    for (String method : fieldNameList.get(countSheet)) {
                        String[] methodName = method.split("\\|");
                        // 列数据
                        cell = row.createCell(cellIndex);
                        String value = getValue(methodName[0], obj);
                        // 如果此列的数据为空, 则与上面几行(主要在于订单有几个订单项)的当前列合并单元格, 合并后会默认使用左上单元格的数据
                        if (StringUtils.isBlank(value)) {
                            // 参数: 起始行号, 结束行号, 起始列号, 结束列号
                            if ("F".equalsIgnoreCase(methodName[1]) && countFirst > 1) {
                                sheet.addMergedRegion(new CellRangeAddress(rowIndex + 1 - countFirst, rowIndex, cellIndex, cellIndex));
                            } else if ("S".equalsIgnoreCase(methodName[1]) && countSecond > 1) {
                                sheet.addMergedRegion(new CellRangeAddress(rowIndex + 1 - countSecond, rowIndex, cellIndex, cellIndex));
                            }
                        }
                        cell.setCellStyle(contentStyle);
                        cell.setCellValue(new HSSFRichTextString(value));
                        cellIndex++;
                    }
                }
                countSheet++;
            }

            // 写文件
            workbook.write(out);
        } catch (IOException e) {
            throw new RuntimeException("向客户端生成文件时异常: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("生成文件时异常: " + e.getMessage());
        } finally {
            try {
                if (out != null)
                    out.flush();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * 生成头样式
     *
     * @param workbook
     * @return
     */
    private static HSSFCellStyle createHeadStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        // 背景色
        style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        // 边框
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 居中
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        // 字体大小
        font.setFontHeightInPoints((short) 12);
        // 粗体
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);

        return style;
    }

    /**
     * 生成内容样式
     *
     * @param workbook
     * @return
     */
    private static HSSFCellStyle createContentStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        // 水平居中
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 垂直居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return style;
    }

    /**
     * 反射属性获取对应的 String 值
     *
     * @param fieldName 属性名
     * @param obj       要反射的对象
     * @return
     */
    private static String getValue(String fieldName, Object obj) {
        String backValue = "";
        String method = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            Object object = obj.getClass().getMethod(method, new Class[]{}).invoke(obj, new Object[]{});

            if (object != null) {
                backValue = object.toString();
            }
        } catch (SecurityException e) {
            throw new RuntimeException("不能访问 " + method + " 方法");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("没有此方法 " + method);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("参数不正确");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("访问不正确");
        } catch (InvocationTargetException e) {
            throw new RuntimeException("调用时异常: " + e.getMessage());
        }
        return backValue;
    }

}
