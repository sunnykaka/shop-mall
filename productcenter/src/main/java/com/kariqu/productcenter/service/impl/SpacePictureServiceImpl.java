package com.kariqu.productcenter.service.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.SpacePicture;
import com.kariqu.productcenter.domain.SpaceProperty;
import com.kariqu.productcenter.repository.SpacePictureRepository;
import com.kariqu.productcenter.service.SpacePictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: ennoch
 * Date: 12-7-12
 * Time: 下午4:23
 */
@Transactional
public class SpacePictureServiceImpl implements SpacePictureService {

    @Autowired
    private SpacePictureRepository spacePictureRepository;

    @Override
    public void createPicture(SpacePicture spacePicture) {
        spacePictureRepository.createSpacePicture(spacePicture);
    }

    @Override
    public Page <SpacePicture> queryAllPictureOfSpace(int spaceId , Page page){
        return  spacePictureRepository.queryAllPictureOfSpace(spaceId,page);
    }

    @Override
    public List<SpaceProperty> queryAllSpace(){
        return  spacePictureRepository.queryAllSpaces();

    }

    public void createSpaceProperty(SpaceProperty spaceProperty){
        spacePictureRepository.createSpaceProperty(spaceProperty);
    }

    @Override
    public void updateSpaceProperty(SpaceProperty spaceProperty) {
        spacePictureRepository.updateSpaceProperty(spaceProperty);
    }

    public void setSpacePictureRepository(SpacePictureRepository spacePictureRepository) {
        this.spacePictureRepository = spacePictureRepository;
    }
    public SpaceProperty querySpaceByName(String spaceName){
        return spacePictureRepository.querySpaceByName(spaceName);
    }
    public void deleteSpace(String spaceName){
        spacePictureRepository.deleteSpaceBySpaceName(spaceName);
    }
     public List<SpaceProperty>  queryOtherSpace(String spaceName){
        return spacePictureRepository.queryOtherSpace(spaceName);
    }

    public void deleteSpacePicture(int spaceId){
        spacePictureRepository.deleteSpacePictureBySpaceId(spaceId);
    }

    public SpacePicture querySpacePictureById(int id){
       return  spacePictureRepository.getSpacePictureById(id);
    }

    public void deletePicture(int pictureId){
        spacePictureRepository.deleteSpacePictureById(pictureId);
    }

    public int queryCountPictureOfSpace(int spaceId){
        return spacePictureRepository.queryCountPictureOfSpace(spaceId);
    }

    @Override
    public int queryDefaultSpacePropertyId() {
        return spacePictureRepository.queryDefaultSpacePropertyId();
    }
}
