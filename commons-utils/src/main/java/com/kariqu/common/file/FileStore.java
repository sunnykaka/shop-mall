package com.kariqu.common.file;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Asion
 * Date: 12-4-15
 * Time: 下午4:17
 */
public interface FileStore {

    /**
     * 传入字节数组进行存储
     *
     * @param fileName
     * @param data
     * @return
     */
    StoreResult store(String fileName, byte[] data, FileType fileType);

    /**
     * 传入字节流进行存储
     *
     * @param fileName
     * @param inputStream
     * @return
     */
    StoreResult store(String fileName, InputStream inputStream, FileType fileType);

    /**
     * 文本文件存储
     *
     * @param fileName
     * @param content
     * @return
     */
    StoreResult store(String fileName, String content);


    StoreResult deleteFile(String fileName, FileType fileType);


    public static class StoreResult {

        /**
         * 成功标志
         */
        private boolean success;

        /**
         * 消息，可以用来保存失败理由等
         */
        private String msg;

        /**
         * 数据
         */
        private Map<String, String> data = new HashMap<String, String>();

        public StoreResult(boolean success) {
            this.success = success;
        }

        public StoreResult(boolean success, String msg) {
            this.success = success;
            this.msg = msg;
        }

        public StoreResult addDataEntry(String name, String value) {
            data.put(name, value);
            return this;
        }

        public String getDataEntry(String name) {
            return data.get(name);
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }


}
