package com.kariqu.productcenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.SpacePicture;
import com.kariqu.productcenter.domain.SpaceProperty;

import java.util.List;

/**
 * User: ennoch
 * Date: 12-7-12
 * Time: 下午4:21
 */
public interface SpacePictureService {

     void  createPicture(SpacePicture spacePicture);

     Page<SpacePicture> queryAllPictureOfSpace(int spaceId ,Page page);

     List<SpaceProperty> queryAllSpace();

     void createSpaceProperty(SpaceProperty spaceProperty);

     void updateSpaceProperty(SpaceProperty spaceProperty);

     SpaceProperty querySpaceByName(String spaceName);

     void deleteSpace(String spaceName);

     List<SpaceProperty> queryOtherSpace(String spaceName);

     void deleteSpacePicture(int spaceId);

    SpacePicture querySpacePictureById(int id);

    void deletePicture(int pictureId);

    int queryCountPictureOfSpace(int spaceId);

    int queryDefaultSpacePropertyId();

}
