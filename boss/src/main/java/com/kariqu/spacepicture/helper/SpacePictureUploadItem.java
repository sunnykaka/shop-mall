package com.kariqu.spacepicture.helper;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created with IntelliJ IDEA.
 * User: ennoch
 * Date: 12-7-13
 * Time: 下午1:22
 * To change this template use File | Settings | File Templates.
 */
public class SpacePictureUploadItem {

    private int spaceId;

    private MultipartFile uploadFile;


    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }

    public MultipartFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(MultipartFile uploadFile) {
        this.uploadFile = uploadFile;
    }
}
