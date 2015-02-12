package com.kariqu.productcenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.SpacePicture;
import com.kariqu.productcenter.domain.SpaceProperty;

import java.util.List;

/**
 * User: ennoch
 * Date: 12-7-10
 * Time: 上午10:55
 */
public interface SpacePictureRepository{

    Page<SpacePicture> queryAllPictureOfSpace(int spaceId,Page page);

    List <SpaceProperty> queryAllSpaces();

    void createSpaceProperty(SpaceProperty spaceProperty);

    void updateSpaceProperty(SpaceProperty spaceProperty);

    SpaceProperty querySpaceByName(String spaceName);

    void deleteSpaceBySpaceName(String spaceName);

    List<SpaceProperty> queryOtherSpace(String spaceName);

    void deleteSpacePictureBySpaceId(int spaceId);

    int queryCountPictureOfSpace(int SpaceId);

    int queryDefaultSpacePropertyId();

    void createSpacePicture(SpacePicture spacePicture);

    SpacePicture getSpacePictureById(int id);

    void updateSpacePicture(SpacePicture spacePicture);

    void deleteSpacePictureById(int id);

    List<SpacePicture> queryAllSpacePictures();
}
