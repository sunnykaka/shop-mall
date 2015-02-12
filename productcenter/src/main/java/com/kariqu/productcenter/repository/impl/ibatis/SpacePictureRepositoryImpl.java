package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.SpacePicture;
import com.kariqu.productcenter.domain.SpaceProperty;
import com.kariqu.productcenter.repository.SpacePictureRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 空间图片仓库
 * User: ennoch
 * Date: 12-7-10
 * Time: 上午10:57
 */
public class SpacePictureRepositoryImpl extends SqlMapClientDaoSupport implements SpacePictureRepository {
    @Override
    public void createSpacePicture(SpacePicture spacePicture) {
        getSqlMapClientTemplate().insert("createSpacePicture",spacePicture);
    }

    @Override
    public SpacePicture getSpacePictureById(int id) {
        return  (SpacePicture)getSqlMapClientTemplate().queryForObject("querySpacePictureById",id);
    }

    @Override
    public void updateSpacePicture(SpacePicture spacePicture) {
         getSqlMapClientTemplate().update("updateSpacePicture",spacePicture);
    }

    @Override
    public void deleteSpacePictureById(int id) {
        getSqlMapClientTemplate().delete("deletePictureSpace",id);
    }

    @Override
    public List<SpacePicture> queryAllSpacePictures() {
        return getSqlMapClientTemplate().queryForList("queryAll");
    }

    @Override
    public List <SpaceProperty> queryAllSpaces() {
        return (List)getSqlMapClientTemplate().queryForList("AllSpace");
    }

    @Override
    public Page<SpacePicture> queryAllPictureOfSpace(int spaceId ,Page page) {
        Map map = new HashMap();
        map.put("spaceId", spaceId);
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());
        List<SpacePicture> list = getSqlMapClientTemplate().queryForList("QueryAllPictureOfSpace", map);
        page.setResult(list);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("QueryAllPictureOfSpaceCount", map));
        return page;
    }

    public  SpaceProperty querySpaceByName(String spaceName){
        List <SpaceProperty> spaceProperties =  getSqlMapClientTemplate().queryForList("querySpacePropertyByName",spaceName);
        if(null !=spaceProperties && spaceProperties.size()>0){
            return spaceProperties.get(0);
        }
        return null;
    }

    public void createSpaceProperty(SpaceProperty spaceProperty){
        getSqlMapClientTemplate().insert("createSpaceProperty", spaceProperty);
    }

    @Override
    public void updateSpaceProperty(SpaceProperty spaceProperty) {
        getSqlMapClientTemplate().update("updateSpace",spaceProperty);
    }

    public void deleteSpaceBySpaceName(String spaceName){
        getSqlMapClientTemplate().delete("deleteSpaceByName",spaceName);
    }
    public List<SpaceProperty> queryOtherSpace(String spaceName){
        return getSqlMapClientTemplate().queryForList("queryOtherSpace",spaceName);
    }

    public  void deleteSpacePictureBySpaceId(int spaceId){
        getSqlMapClientTemplate().delete("deleteAllSpacePicture",spaceId);
    }

    public int queryCountPictureOfSpace(int SpaceId){
        return (Integer)getSqlMapClientTemplate().queryForObject("queryCountPictureOfSpace",SpaceId);
    }

    @Override
    public int queryDefaultSpacePropertyId() {
        return (Integer)getSqlMapClientTemplate().queryForObject("queryDefaultSpacePropertyId");
    }
}
