package com.kariqu.common;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import com.google.common.primitives.Ints;
import com.kariqu.usercenter.domain.UserGrade;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: Maven
 * Date: 14-7-14
 * Time: 下午5:05
 */
public class ExcelSheet {

    private String[] header = null;// 表头信息
    private Table<Integer, String, String> table;

    private Sheet st = null;
    private int firstRowNum;// 第一行号
    private int lastRowNum;// 最后行号

    /**
     * 由一个HSSFSheet构建一个Sheet对象
     *
     * @param st
     */
    public ExcelSheet(Sheet st) {
        this.st = st;
        this.firstRowNum = st.getFirstRowNum();
        this.lastRowNum = st.getLastRowNum();
    }

    /**
     * 返回表的第一个非空行行号
     *
     * @return 第一个非空行的行号
     */
    public int getFirstRowNum() {
        return this.firstRowNum;
    }

    /**
     * 返回表的最后一个非空行行号
     *
     * @return 最后一个非空行行号
     */
    public int getLastRowNum() {
        return this.lastRowNum;
    }

    /**
     * 当前打开的表名字
     *
     * @return 表名字
     */
    public String getSheetName() {
        return this.st.getSheetName();
    }

    /**
     * 获取表数据行数
     *
     * @return 行数
     * @see org.apache.poi.hssf.usermodel.HSSFSheet
     */
    public int getRowSize() {
        return this.st.getLastRowNum() - this.st.getFirstRowNum();
    }

    /**
     * 获取表数据列数
     *
     * @return 列数
     */
    public int getColumnSize() {
        Row row = this.st.getRow(this.getFirstRowNum());
        return row.getLastCellNum() - row.getFirstCellNum();
    }

    /**
     * 返回当前表的列名数组
     *
     * @return 列名数组
     */
    public String[] getColumnNames() {
        if (null == header) {
            ArrayList<String> h = getRowAt(this.firstRowNum);
            String[] head = new String[h.size()];
            int i = 0;
            for (String o : h) {
                head[i++] = String.valueOf(o);
            }
            this.header = head;
        }

        return header;
    }

    /**
     * 获取指定行的数据
     *
     * @param index 指定行号
     * @return 指定行数据
     */
    public ArrayList<String> getRowAt(int index) {
        Row row = this.st.getRow(index);
        if (null == row) {
            return null;
        }
        ArrayList<String> cells = new ArrayList<String>();
        int i = row.getFirstCellNum();

        while (i < this.getColumnSize()) {
            Cell cell = row.getCell(i++);
            cells.add(this.getCellDate(cell));
        }
        return cells;
    }

    /**
     * 读取cell单元格的数据
     *
     * @param cell
     * @return
     */
    public String getCellDate(Cell cell) {
        Object val;
        DecimalFormat df = new DecimalFormat("#");

        if (cell == null) {
            val = "";
        } else {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    val = cell.getBooleanCellValue();
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    try {
                        val = String.valueOf(cell.getNumericCellValue());
                    } catch (IllegalStateException e) {
                        try {
                            val = String.valueOf(cell.getRichStringCellValue());
                        } catch (IllegalStateException e1) {
                            val = cell.getCellFormula();
                        }
                    }
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        val = DateUtil.getJavaDate(cell.getNumericCellValue());
                        val = DateUtils.formatDate((Date) val, DateUtils.DateFormatType.DATE_FORMAT_STR);

                    } else {
                        //强制转换科学计数法
                        val = df.format(cell.getNumericCellValue());
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    val = cell.getStringCellValue();
                    break;
                default:
                    val = cell.getRichStringCellValue();
            }
        }
        return val.toString();
    }

    /**
     * 返回表的Table视图
     *
     * @return 表的table视图
     */
    public Table<Integer, String, String> getAsTable() {
        if (null == table || table.size() == 0) {
            table = HashBasedTable.create();
            int columnNum = this.getColumnNames().length;
            for (int i = this.getFirstRowNum() + 1; i <= this.getRowSize(); i++) {
                Row row = this.st.getRow(i);
                if (null == row) {
                    continue;
                }

                for (int columnIndex = row.getFirstCellNum(); columnIndex < columnNum; columnIndex++) {
                    Cell cell = row.getCell(columnIndex);
                    table.put(i, header[columnIndex], this.getCellDate(cell));
                }
            }
        }

        return table;
    }
}
