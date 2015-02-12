package com.kariqu.common.file;

import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * User: Asion
 * Date: 12-4-15
 * Time: 下午7:59
 */
public class LocalFileSystemFileStoreTest {

    @Test
    public void storeText() {
       /* LocalFileSystemFileStore localFileSystemFileStore = new LocalFileSystemFileStore();
        localFileSystemFileStore.setRootDir("H:/");
        assertEquals(true, localFileSystemFileStore.store("test.txt", "hello text").isSuccess());*/
    }

    @Test
    public void storeFile() throws FileNotFoundException {
       /* LocalFileSystemFileStore localFileSystemFileStore = new LocalFileSystemFileStore();
        localFileSystemFileStore.setRootDir("H:/");
        localFileSystemFileStore.store("test.pdf", new FileInputStream("H:/MasteringSpringMVC3.pdf"), FileType.BINARY);*/
    }

    @Test
    public void storePicture() throws FileNotFoundException {
       /* LocalFileSystemFileStore localFileSystemFileStore = new LocalFileSystemFileStore();
        localFileSystemFileStore.setRootDir("H:/");
        localFileSystemFileStore.store("test.jpg", new FileInputStream("H:/spring1.jpg"), FileType.PICTURE);*/
    }

}
