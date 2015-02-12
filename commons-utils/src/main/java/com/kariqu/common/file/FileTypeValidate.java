package com.kariqu.common.file;

import java.io.*;
import java.util.HashMap;

/**
 * 文件类型判断
 * User: Alec
 * Date: 13-3-13
 * Time: 下午2:04
 */
public class FileTypeValidate {
    //缓存文件头信息-文件头信息
    public static final HashMap<String, String> fileTypes = new HashMap<String, String>();
    public static final HashMap<String, String> imageTypes = new HashMap<String, String>();

    static {
        // images
        imageTypes.put("FFD8FF", "jpg");
        imageTypes.put("89504E47", "png");
        imageTypes.put("47494638", "gif");
        imageTypes.put("49492A00", "tif");
        imageTypes.put("424D", "bmp");
        //
        fileTypes.put("FFD8FFE0", "jpg");
        fileTypes.put("89504E47", "png");
        fileTypes.put("47494638", "gif");
        fileTypes.put("49492A00", "tif");
        fileTypes.put("424D", "bmp");
        fileTypes.put("41433130", "dwg"); // CAD
        fileTypes.put("38425053", "psd");
        fileTypes.put("7B5C727466", "rtf"); // 日记本
        fileTypes.put("00000000", "txt");
        fileTypes.put("3C3F786D6C", "xml");
        fileTypes.put("68746D6C3E", "html");
        fileTypes.put("44656C69766572792D646174653A", "eml"); // 邮件
        fileTypes.put("D0CF11E0", "doc");
        fileTypes.put("5374616E64617264204A", "mdb");
        fileTypes.put("252150532D41646F6265", "ps");
        fileTypes.put("255044462D312E", "pdf");
        fileTypes.put("504B0304", "docx");
        fileTypes.put("52617221", "rar");
        fileTypes.put("57415645", "wav");
        fileTypes.put("41564920", "avi");
        fileTypes.put("2E524D46", "rm");
        fileTypes.put("000001BA", "mpg");
        fileTypes.put("000001B3", "mpg");
        fileTypes.put("6D6F6F76", "mov");
        fileTypes.put("3026B2758E66CF11", "asf");
        fileTypes.put("4D546864", "mid");
        fileTypes.put("1F8B08", "gz");
    }

    /**
     * 根据文件头获取类型
     *
     * @param filePath
     * @return
     */
    public static String getFileType(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return fileTypes.get(getFileHeader(filePath));
        }
        return null;
    }

    public static String getFileType(InputStream inputStream) throws IOException {
        if (inputStream.available() < 1) {
            return null;
        }
        return fileTypes.get(getFileHeader(inputStream));
    }

    /**
     * 获取文件头
     *
     * @param filePath
     * @return
     */
    public static String getFileHeader(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return getFileHeader(inputStream);

    }


    public static String getFileHeader(InputStream inputStream) {
        String header = null;
        try {
            byte[] b = new byte[4];
            inputStream.read(b, 0, b.length);
            header = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return header;
    }

    /**
     * 　　* 将要读取文件头信息的文件的byte数组转换成string类型表示
     * 　　*
     * 　　* @param src
     * 　　*            要读取文件头信息的文件的byte数组
     * 　　* @return 文件头信息
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // 以十六进制(基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    public static boolean isImage(String filePath) {
        String fileHeader = getFileHeader(filePath);
        for (String img : imageTypes.keySet()) {
            if (fileHeader.startsWith(img)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isImage(InputStream inputStream) {
        String fileHeader = getFileHeader(inputStream);
        for (String img : imageTypes.keySet()) {
            if (fileHeader.startsWith(img)) {
               return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {

        System.out.println(getFileHeader("d:/temp/2.jpg").startsWith("FFD8FF"));
        System.out.println(isImage("d:/temp/2.jpg"));

        /*  System.out.println(isImage("d:/temp/1.png"));
        System.out.println(getFileHeader("d:/temp/1.png"));*/
    }

}
