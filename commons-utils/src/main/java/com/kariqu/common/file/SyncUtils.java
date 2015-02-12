package com.kariqu.common.file;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 同步工具类
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-7-13 下午2:59
 */
public class SyncUtils {

    private static final Log logger = LogFactory.getLog(SyncUtils.class);

    /**
     * timestamp format pattern
     */
    public static String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 记录同步时间
     *
     * @param syncTimeStamp sync timestamp
     * @param fileName      文件名
     * @throws java.io.IOException io exception
     */
    public static void recordSyncTimeStamp(Date syncTimeStamp, String fileName) {
        FileOutputStream fos = null;
        try {
            String userHome = System.getProperty("user.home");
            File tmsRecordFile = new File(new File(userHome), fileName);
            fos = new FileOutputStream(tmsRecordFile);
            IOUtils.write(DateFormatUtils.format(syncTimeStamp, TIMESTAMP_PATTERN), fos);
        } catch (Exception e) {
            logger.error("写入同步数据文件出错", e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    /**
     * 记录同步时间
     *
     * @param fileName 文件名
     * @return 同步时间
     * @throws java.io.IOException io exception
     */
    public static Date getSyncTimeStamp(String fileName) {
        DateFormat dateFormat = new SimpleDateFormat(SyncUtils.TIMESTAMP_PATTERN);
        String userHome = System.getProperty("user.home");
        FileInputStream input = null;
        File tmsRecordFile = new File(new File(userHome), fileName);
        Date date = null;
        if (tmsRecordFile.exists()) {
            try {
                input = new FileInputStream(tmsRecordFile);
                String dateString = IOUtils.toString(input);
                date = dateFormat.parse(dateString);
            } catch (ParseException e) {
                logger.error("解析日期出现错误", e);
            } catch (IOException e) {
                logger.error("读取同步时间出错", e);
            } finally {
                IOUtils.closeQuietly(input);
            }
        }
        return date;
    }
}
