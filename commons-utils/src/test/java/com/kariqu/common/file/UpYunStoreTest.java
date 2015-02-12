package com.kariqu.common.file;

import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * User: Asion
 * Date: 12-7-3
 * Time: 下午3:15
 */
public class UpYunStoreTest {
    @Test
    public void testUpYunPicture() throws FileNotFoundException {
       /* FileStore upyun = new UpYunStore();
        FileStore.StoreResult store = upyun.store("test.jpg", new FileInputStream("H:/testPhoto/test1.jpg"), FileType.PICTURE);
        assertEquals(true, store.isSuccess());
        System.out.println(store.getDataEntry("FilePath"));*/
    }

    @Test
    public void testUpyunFile() throws FileNotFoundException {
        /*FileStore upyun = new UpYunStore();
        FileStore.StoreResult store = upyun.store("test.txt", new FileInputStream("H:/testPhoto/test1.txt"), FileType.TEXT);
        assertEquals(true, store.isSuccess());
        System.out.println(store.getDataEntry("FilePath"));*/
    }

    @Test
    public void testDelete() {
       /* FileStore upyun = new UpYunStore();
        FileStore.StoreResult storeResult = upyun.deleteFile("test.jpg", FileType.PICTURE);
        assertEquals(true, storeResult.isSuccess());*/
    }
}
