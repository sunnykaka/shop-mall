package com.kariqu.common.file;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * 本地文件系统存储
 * User: Asion
 * Date: 12-4-15
 * Time: 下午4:23
 */
public class LocalFileSystemFileStore implements FileStore {

    protected final Log logger = LogFactory.getLog(UpYunStore.class);

    private String rootDir;


    @Override
    public StoreResult store(String fileName, byte[] data, FileType fileType) {
        FileWriter fileWriter = null;
        FileOutputStream fileOutputStream = null;
        try {
            if (fileType == FileType.TEXT) {
                fileWriter = new FileWriter(rootDir + fileName);
                fileWriter.write(new String(data, "utf8"));
                fileWriter.flush();
            } else {
                fileOutputStream = new FileOutputStream(rootDir + fileName);
                fileOutputStream.write(data);
                fileOutputStream.flush();
            }
        } catch (IOException e) {
            logger.error(e);
            return new StoreResult(false, e.getMessage());
        } finally {
            IOUtils.closeQuietly(fileWriter);
            IOUtils.closeQuietly(fileOutputStream);
        }
        return new StoreResult(true);
    }

    @Override
    public StoreResult store(String fileName, InputStream inputStream, FileType fileType) {
        try {
            return store(fileName, IOUtils.toByteArray(inputStream), fileType);
        } catch (IOException e) {
            logger.error("上传失败", e);
            return new StoreResult(false, e.getMessage());
        }
    }

    @Override
    public StoreResult store(String fileName, String content) {
        try {
            return store(fileName, content.getBytes("utf8"), FileType.TEXT);
        } catch (IOException e) {
            logger.error("上传失败", e);
            return new StoreResult(false, e.getMessage());
        }
    }

    @Override
    public StoreResult deleteFile(String fileName, FileType fileType) {
        File file = new File(rootDir + fileName);
        file.delete();
        return new StoreResult(true);
    }

    public String getRootDir() {
        if (!rootDir.endsWith("/")) {
            rootDir = rootDir + "/";
        }
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }
}
